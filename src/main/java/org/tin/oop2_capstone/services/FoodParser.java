package org.tin.oop2_capstone.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.api.APIResponse;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FoodParser {

    private static class FoodCandidate {
        JsonObject json;
        int score;
        FoodCandidate(JsonObject json, int score) {
            this.json = json;
            this.score = score;
        }
    }

    public static Food parseFood(String searchJson) {
        return parseFood(searchJson, "");
    }

    public static Food parseFood(String searchJson, String originalQuery) {
        List<Food> foods = processFoodSearch(searchJson, originalQuery, 1);
        return foods.isEmpty() ? null : foods.get(0);
    }

    public static List<Food> parseFoods(String searchJson) {
        return parseFoods(searchJson, "");
    }

    public static List<Food> parseFoods(String searchJson, String originalQuery) {
        return processFoodSearch(searchJson, originalQuery, 5);
    }

    private static List<Food> processFoodSearch(String searchJson, String originalQuery, int maxResults) {
        List<Food> parsedFoods = new ArrayList<>();
        if (searchJson == null || searchJson.isEmpty()) return parsedFoods;

        JsonObject obj = JsonParser.parseString(searchJson).getAsJsonObject();
        JsonArray foods = obj.getAsJsonArray("foods");
        if (foods == null || foods.isEmpty()) return parsedFoods;

        List<FoodCandidate> candidates = new ArrayList<>();

        for (int i = 0; i < foods.size(); i++) {
            JsonObject candidate = foods.get(i).getAsJsonObject();
            if (!candidate.has("description")) continue;

            String name = candidate.get("description").getAsString();

            if (FoodSelector.isJunk(name) && !name.toLowerCase().contains(originalQuery.toLowerCase())) continue;

            int score = FoodSelector.cookingScore(name, originalQuery);

            if (candidate.has("dataType")) {
                String type = candidate.get("dataType").getAsString();
                if (type.equalsIgnoreCase("Foundation")) score += 15;
                if (type.equalsIgnoreCase("SR Legacy"))  score += 10;
                if (type.equalsIgnoreCase("Branded"))    score += 3;

                // Penalize branded unless query words strongly match the food name
                if (type.equalsIgnoreCase("Branded")) {
                    String nameLower = name.toLowerCase();
                    String queryLower = originalQuery.toLowerCase();
                    String[] words = queryLower.split("\\s+");
                    long matchCount = 0, meaningfulCount = 0;
                    for (String word : words) {
                        if (word.length() > 2) {
                            meaningfulCount++;
                            if (nameLower.contains(word)) matchCount++;
                        }
                    }
                    boolean hasMatch = meaningfulCount > 0 && matchCount == meaningfulCount;
                    if (!hasMatch) score -= 20;
                }
            }

            candidates.add(new FoodCandidate(candidate, score));
        }

        if (candidates.isEmpty() && foods.size() > 0) {
            candidates.add(new FoodCandidate(foods.get(0).getAsJsonObject(), 0));
        }

        candidates.sort((a, b) -> Integer.compare(b.score, a.score));

        List<FoodCandidate> deduped = new ArrayList<>();
        java.util.Set<String> seenNames = new java.util.HashSet<>();
        for (FoodCandidate c : candidates) {
            String n = c.json.get("description").getAsString().toLowerCase();
            if (seenNames.add(n)) deduped.add(c);
        }

        int limit = Math.min(maxResults, deduped.size());
        ExecutorService exec = Executors.newFixedThreadPool(limit);
        List<Future<Food>> futures = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            JsonObject json = deduped.get(i).json;
            futures.add(exec.submit(() -> buildFood(json)));
        }

        for (Future<Food> f : futures) {
            try {
                Food food = f.get(10, TimeUnit.SECONDS);
                if (food != null) parsedFoods.add(food);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        exec.shutdown();

        return parsedFoods;
    }

    private static Food buildFood(JsonObject bestFood) {
        String name = bestFood.get("description").getAsString();
        JsonObject detail = null;

        if (bestFood.has("fdcId")) {
            int fdcId = bestFood.get("fdcId").getAsInt();
            APIResponse detailJson = FoodAPI.getFoodDetail(fdcId);
            if (detailJson.getJson() != null && !detailJson.getJson().isEmpty()) {
                detail = JsonParser.parseString(detailJson.getJson()).getAsJsonObject();
            }
        }

        double calories = 0, protein = 0, fat = 0, carbs = 0;
        double cholesterol = 0, sodium = 0, sugar = 0, fiber = 0;

        JsonArray nutrients = null;
        if (detail != null && detail.has("foodNutrients")) {
            nutrients = detail.getAsJsonArray("foodNutrients");
        } else if (bestFood.has("foodNutrients")) {
            nutrients = bestFood.getAsJsonArray("foodNutrients");
        }

        if (nutrients != null) {
            for (int i = 0; i < nutrients.size(); i++) {
                JsonObject nutrient = nutrients.get(i).getAsJsonObject();
                String nutrientName = "", nutrientNumber = "", unitName = "";
                double value = 0;

                if (nutrient.has("nutrientName")) {
                    nutrientName   = nutrient.get("nutrientName").getAsString().toLowerCase();
                    value          = nutrient.has("value") ? nutrient.get("value").getAsDouble() : 0;
                    nutrientNumber = nutrient.has("nutrientNumber") ? nutrient.get("nutrientNumber").getAsString() : "";
                    unitName       = nutrient.has("unitName") ? nutrient.get("unitName").getAsString() : "";
                } else if (nutrient.has("nutrient")) {
                    JsonObject n   = nutrient.getAsJsonObject("nutrient");
                    nutrientName   = n.has("name") ? n.get("name").getAsString().toLowerCase() : "";
                    nutrientNumber = n.has("number") ? n.get("number").getAsString() : "";
                    unitName       = n.has("unitName") ? n.get("unitName").getAsString() : "";
                    value          = nutrient.has("amount") ? nutrient.get("amount").getAsDouble()
                            : nutrient.has("value")  ? nutrient.get("value").getAsDouble() : 0;
                }

                if (nutrientName.isEmpty()) continue;

                if (nutrientNumber.equals("208") || nutrientName.contains("energy")) {
                    if (unitName.equalsIgnoreCase("KCAL") || unitName.isEmpty()) calories = value;
                }

                switch (nutrientName) {
                    case "protein":                              protein     = value; break;
                    case "total lipid (fat)": case "fat":        fat         = value; break;
                    case "carbohydrate, by difference":
                    case "carbohydrate":                         carbs       = value; break;
                    case "cholesterol":                          cholesterol = value; break;
                    case "sodium, na":                           sodium      = value; break;
                    case "total sugars":
                    case "sugars, total including nlea":         sugar       = value; break;
                    case "fiber, total dietary":                 fiber       = value; break;
                }
            }
        }

        double targetGramWeight = 100.0;
        JsonObject targetContainer = detail != null ? detail : bestFood;

        if (targetContainer.has("foodPortions") && !targetContainer.getAsJsonArray("foodPortions").isEmpty()) {
            JsonArray portions = targetContainer.getAsJsonArray("foodPortions");
            JsonObject preferred = null;
            int bestSeq = Integer.MAX_VALUE;

            for (int i = 0; i < portions.size(); i++) {
                JsonObject p = portions.get(i).getAsJsonObject();
                String modifier = p.has("modifier") ? p.get("modifier").getAsString().toLowerCase() : "";
                int seq = p.has("sequenceNumber") ? p.get("sequenceNumber").getAsInt() : 999;

                if (preferred == null && modifier.contains("medium")) preferred = p;
                if (preferred == null && seq < bestSeq) { bestSeq = seq; preferred = p; }
            }

            if (preferred != null && preferred.has("gramWeight")) {
                targetGramWeight = preferred.get("gramWeight").getAsDouble();
            }
        }

        if (targetGramWeight == 100.0 && targetContainer.has("servingSize")) {
            targetGramWeight = targetContainer.get("servingSize").getAsDouble();
        }

        double scale = targetGramWeight / 100.0;
        return new Food(name, new NutritionDetails(
                round(calories * scale), round(protein * scale), round(fat * scale),
                round(carbs * scale), round(cholesterol * scale), round(sodium * scale),
                round(sugar * scale), round(fiber * scale)
        ), false);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}