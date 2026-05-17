package org.tin.oop2_capstone.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

import java.util.ArrayList;
import java.util.List;

public class FoodParser {

    private static class FoodCandidate {
        JsonObject json;
        int score;

        FoodCandidate(JsonObject json, int score) {
            this.json = json;
            this.score = score;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ORIGINAL METHOD: Retained for backward compatibility
    // Returns ONLY the single highest-scoring Food
    // ─────────────────────────────────────────────────────────────
    public static Food parseFood(String searchJson) {
        List<Food> results = processFoodSearch(searchJson, 1);
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // NEW METHOD: Used for the ComboBox Dropdown
    // Returns the Top 5 highest-scoring Foods
    // ─────────────────────────────────────────────────────────────
    public static List<Food> parseFoods(String searchJson) {
        return processFoodSearch(searchJson, 5);
    }

    // ─────────────────────────────────────────────────────────────
    // INTERNAL LOGIC: Handles the sorting and limits API calls
    // ─────────────────────────────────────────────────────────────
    private static List<Food> processFoodSearch(String searchJson, int maxResults) {
        List<Food> parsedFoods = new ArrayList<>();

        if (searchJson == null || searchJson.isEmpty()) {
            return parsedFoods;
        }

        JsonObject obj = JsonParser.parseString(searchJson).getAsJsonObject();
        JsonArray foods = obj.getAsJsonArray("foods");

        if (foods == null || foods.isEmpty()) {
            return parsedFoods;
        }

        String[] junkKeywords = {
                "powder", "dehydrated", "dried", "canned", "pickled",
                "preserved", "concentrate", "frozen", "imitation", "rose-apple",
                "juice", "sauce", "pastry", "strudel", "dessert",
                "cured", "corned", "sweet", "candied", "syrup"
        };

        List<FoodCandidate> candidates = new ArrayList<>();

        for (int i = 0; i < foods.size(); i++) {
            JsonObject candidate = foods.get(i).getAsJsonObject();
            if (!candidate.has("description")) continue;

            String candidateName = candidate.get("description").getAsString().toLowerCase();

            boolean isJunk = false;
            for (String keyword : junkKeywords) {
                if (candidateName.contains(keyword)) {
                    isJunk = true;
                    break;
                }
            }
            if (isJunk) continue;

            int score = 0;
            if (candidateName.contains("raw")) score += 10;
            if (candidateName.contains("cooked")) score -= 3;
            if (candidateName.split(" ").length <= 4) score += 5;
            if (candidateName.contains(",")) score -= 2;

            if (candidate.has("dataType")) {
                String dataType = candidate.get("dataType").getAsString();
                if (dataType.equalsIgnoreCase("Foundation")) score += 15;
                if (dataType.equalsIgnoreCase("SR Legacy")) score += 10;
            }

            candidates.add(new FoodCandidate(candidate, score));
        }

        // Sort candidates by score descending
        candidates.sort((c1, c2) -> Integer.compare(c2.score, c1.score));

        if (candidates.isEmpty() && foods.size() > 0) {
            candidates.add(new FoodCandidate(foods.get(0).getAsJsonObject(), 0));
        }

        // Only process up to 'maxResults' to save API detail calls
        int limit = Math.min(candidates.size(), maxResults);
        for (int i = 0; i < limit; i++) {
            JsonObject bestFood = candidates.get(i).json;
            Food newFood = buildFoodFromCandidate(bestFood);
            if (newFood != null) {
                parsedFoods.add(newFood);
            }
        }

        return parsedFoods;
    }

    // ─────────────────────────────────────────────────────────────
    // DETAIL PARSER: Extracts nutrients from a single JSON object
    // ─────────────────────────────────────────────────────────────
    private static Food buildFoodFromCandidate(JsonObject bestFood) {
        String name = bestFood.get("description").getAsString();
        JsonObject detail = null;

        if (bestFood.has("fdcId")) {
            int fdcId = bestFood.get("fdcId").getAsInt();
            String detailJson = FoodAPI.getFoodDetail(fdcId);

            if (detailJson != null) {
                detail = JsonParser.parseString(detailJson).getAsJsonObject();
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
                    nutrientName = nutrient.get("nutrientName").getAsString().toLowerCase();
                    if (nutrient.has("value")) value = nutrient.get("value").getAsDouble();
                    if (nutrient.has("nutrientNumber")) nutrientNumber = nutrient.get("nutrientNumber").getAsString();
                    if (nutrient.has("unitName")) unitName = nutrient.get("unitName").getAsString();
                } else if (nutrient.has("nutrient")) {
                    JsonObject nutrientObj = nutrient.getAsJsonObject("nutrient");
                    if (nutrientObj.has("name")) nutrientName = nutrientObj.get("name").getAsString().toLowerCase();
                    if (nutrientObj.has("number")) nutrientNumber = nutrientObj.get("number").getAsString();
                    if (nutrientObj.has("unitName")) unitName = nutrientObj.get("unitName").getAsString();

                    if (nutrient.has("amount")) value = nutrient.get("amount").getAsDouble();
                    else if (nutrient.has("value")) value = nutrient.get("value").getAsDouble();
                }

                if (nutrientName.isEmpty()) continue;

                if (nutrientNumber.equals("208") || nutrientName.contains("energy")) {
                    if (unitName.equalsIgnoreCase("KCAL") || unitName.isEmpty()) {
                        calories = value;
                    }
                }

                switch (nutrientName) {
                    case "protein": protein = value; break;
                    case "total lipid (fat)":
                    case "fat": fat = value; break;
                    case "carbohydrate, by difference":
                    case "carbohydrate": carbs = value; break;
                    case "cholesterol": cholesterol = value; break;
                    case "sodium, na": sodium = value; break;
                    case "total sugars":
                    case "sugars, total including nlea": sugar = value; break;
                    case "fiber, total dietary": fiber = value; break;
                }
            }
        }

        double targetGramWeight = 100.0;

        if (detail != null) {
            if (detail.has("foodPortions")) {
                JsonArray portions = detail.getAsJsonArray("foodPortions");
                JsonObject preferredPortion = null;
                int bestSeq = Integer.MAX_VALUE;

                for (int i = 0; i < portions.size(); i++) {
                    JsonObject p = portions.get(i).getAsJsonObject();
                    String modifier = p.has("modifier") ? p.get("modifier").getAsString().toLowerCase() : "";
                    int seq = p.has("sequenceNumber") ? p.get("sequenceNumber").getAsInt() : 999;

                    if (preferredPortion == null && modifier.contains("medium")) preferredPortion = p;
                    if (preferredPortion == null && seq < bestSeq) {
                        bestSeq = seq;
                        preferredPortion = p;
                    }
                }

                if (preferredPortion != null && preferredPortion.has("gramWeight")) {
                    targetGramWeight = preferredPortion.get("gramWeight").getAsDouble();
                }
            }

            if (targetGramWeight == 100.0 && detail.has("servingSize")) {
                targetGramWeight = detail.get("servingSize").getAsDouble();
            }
        }

        double scale = targetGramWeight / 100.0;
        calories *= scale;
        protein *= scale;
        fat *= scale;
        carbs *= scale;
        cholesterol *= scale;
        sodium *= scale;
        sugar *= scale;
        fiber *= scale;

        return new Food(name, new NutritionDetails(
                calories, protein, fat, carbs,
                cholesterol, sodium, sugar, fiber
        ), false);
    }
}