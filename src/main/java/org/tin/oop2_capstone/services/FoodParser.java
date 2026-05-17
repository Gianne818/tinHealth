package org.tin.oop2_capstone.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

public class FoodParser {

    public static Food parseFood(String searchJson) {
        if (searchJson == null || searchJson.isEmpty()) {
            System.out.println("Warning: API returned null/empty JSON");
            return null;
        }

        JsonObject obj = JsonParser.parseString(searchJson).getAsJsonObject();
        JsonArray foods = obj.getAsJsonArray("foods");

        if (foods == null || foods.isEmpty()) {
            return null;
        }

        // ✅ NEW: delegate selection to FoodSelector
        JsonObject bestFood = FoodSelector.pickBest(foods, searchJson);

        if (bestFood == null) {
            System.out.println("Warning: no valid food found.");
            return null;
        }

        // ─────────────────────────────────────────
        // Food name
        // ─────────────────────────────────────────
        String name = bestFood.get("description").getAsString();

        // ─────────────────────────────────────────
        // Fetch full detail record
        // ─────────────────────────────────────────
        JsonObject detail = null;

        if (bestFood.has("fdcId")) {

            int fdcId = bestFood.get("fdcId").getAsInt();

            String detailJson = FoodAPI.getFoodDetail(fdcId);

            if (detailJson != null) {
                detail = JsonParser.parseString(detailJson).getAsJsonObject();
            }
        }

        // ─────────────────────────────────────────
        // Nutrients
        // ─────────────────────────────────────────
        double calories = 0;
        double protein = 0;
        double fat = 0;
        double carbs = 0;
        double cholesterol = 0;
        double sodium = 0;
        double sugar = 0;
        double fiber = 0;

        JsonArray nutrients = null;

        if (detail != null && detail.has("foodNutrients")) {
            nutrients = detail.getAsJsonArray("foodNutrients");
        } else if (bestFood.has("foodNutrients")) {
            nutrients = bestFood.getAsJsonArray("foodNutrients");
        }

        if (nutrients != null) {

            for (int i = 0; i < nutrients.size(); i++) {

                JsonObject nutrient = nutrients.get(i).getAsJsonObject();

                String nutrientName = "";
                String nutrientNumber = "";
                String unitName = "";
                double value = 0;

                // FORMAT A
                if (nutrient.has("nutrientName")) {

                    nutrientName = nutrient.get("nutrientName")
                            .getAsString()
                            .toLowerCase();

                    value = nutrient.has("value")
                            ? nutrient.get("value").getAsDouble()
                            : 0;

                    nutrientNumber = nutrient.has("nutrientNumber")
                            ? nutrient.get("nutrientNumber").getAsString()
                            : "";

                    unitName = nutrient.has("unitName")
                            ? nutrient.get("unitName").getAsString()
                            : "";
                }

                // FORMAT B
                else if (nutrient.has("nutrient")) {

                    JsonObject nutrientObj = nutrient.getAsJsonObject("nutrient");

                    nutrientName = nutrientObj.has("name")
                            ? nutrientObj.get("name").getAsString().toLowerCase()
                            : "";

                    nutrientNumber = nutrientObj.has("number")
                            ? nutrientObj.get("number").getAsString()
                            : "";

                    unitName = nutrientObj.has("unitName")
                            ? nutrientObj.get("unitName").getAsString()
                            : "";

                    value = nutrient.has("amount")
                            ? nutrient.get("amount").getAsDouble()
                            : nutrient.has("value")
                              ? nutrient.get("value").getAsDouble()
                              : 0;
                }

                if (nutrientName.isEmpty()) continue;

                // Calories
                if (nutrientNumber.equals("208") || nutrientName.contains("energy")) {
                    if (unitName.equalsIgnoreCase("KCAL") || unitName.isEmpty()) {
                        calories = value;
                    }
                }

                switch (nutrientName) {

                    case "protein":
                        protein = value;
                        break;

                    case "total lipid (fat)":
                    case "fat":
                        fat = value;
                        break;

                    case "carbohydrate, by difference":
                    case "carbohydrate":
                        carbs = value;
                        break;

                    case "cholesterol":
                        cholesterol = value;
                        break;

                    case "sodium, na":
                        sodium = value;
                        break;

                    case "total sugars":
                    case "sugars, total including nlea":
                        sugar = value;
                        break;

                    case "fiber, total dietary":
                        fiber = value;
                        break;
                }
            }
        }

        // ─────────────────────────────────────────
        // Serving size logic (unchanged)
        // ─────────────────────────────────────────
        double targetGramWeight = 100.0;
        String servingLabel = "100g";

        JsonObject targetContainer = (detail != null) ? detail : bestFood;

        if (targetContainer.has("foodPortions")
                && !targetContainer.getAsJsonArray("foodPortions").isEmpty()) {

            JsonArray portions = targetContainer.getAsJsonArray("foodPortions");
            JsonObject preferred = null;
            int bestSeq = Integer.MAX_VALUE;

            for (int i = 0; i < portions.size(); i++) {

                JsonObject p = portions.get(i).getAsJsonObject();

                String modifier = p.has("modifier")
                        ? p.get("modifier").getAsString().toLowerCase()
                        : "";

                int seq = p.has("sequenceNumber")
                        ? p.get("sequenceNumber").getAsInt()
                        : 999;

                if (preferred == null && modifier.contains("medium")) {
                    preferred = p;
                }

                if (preferred == null && seq < bestSeq) {
                    bestSeq = seq;
                    preferred = p;
                }
            }

            if (preferred != null && preferred.has("gramWeight")) {
                targetGramWeight = preferred.get("gramWeight").getAsDouble();

                servingLabel = preferred.has("modifier")
                        ? preferred.get("modifier").getAsString()
                        : targetGramWeight + "g";
            }
        }

        if (targetGramWeight == 100.0 && targetContainer.has("servingSize")) {

            targetGramWeight = targetContainer.get("servingSize").getAsDouble();

            if (targetContainer.has("householdServingFullText")) {
                servingLabel = targetContainer.get("householdServingFullText").getAsString();
            } else {
                String unit = targetContainer.has("servingSizeUnit")
                        ? targetContainer.get("servingSizeUnit").getAsString()
                        : "g";

                servingLabel = targetGramWeight + " " + unit;
            }
        }

        // ─────────────────────────────────────────
        // Scaling
        // ─────────────────────────────────────────
        double scale = targetGramWeight / 100.0;

        calories = round(calories * scale);
        protein = round(protein * scale);
        fat = round(fat * scale);
        carbs = round(carbs * scale);
        cholesterol = round(cholesterol * scale);
        sodium = round(sodium * scale);
        sugar = round(sugar * scale);
        fiber = round(fiber * scale);

        return new Food(
                name,
                new NutritionDetails(
                        calories, protein, fat, carbs,
                        cholesterol, sodium, sugar, fiber
                ),
                false
        );
    }

    public static Food parseFood(String searchJson, String originalQuery) {

        if (searchJson == null || searchJson.isEmpty()) {
            System.out.println("Warning: API returned null/empty JSON");
            return null;
        }

        JsonObject obj = JsonParser.parseString(searchJson).getAsJsonObject();
        JsonArray foods = obj.getAsJsonArray("foods");

        if (foods == null || foods.isEmpty()) {
            return null;
        }

        // FIX: pass actual user query, NOT JSON
        JsonObject bestFood = FoodSelector.pickBest(foods, originalQuery);

        if (bestFood == null) {
            System.out.println("Warning: no valid food found.");
            return null;
        }

        String name = bestFood.get("description").getAsString();

        // -------------------------
        // DETAIL REQUEST
        // -------------------------
        JsonObject detail = null;

        if (bestFood.has("fdcId")) {

            int fdcId = bestFood.get("fdcId").getAsInt();
            String detailJson = FoodAPI.getFoodDetail(fdcId);

            if (detailJson != null && !detailJson.isEmpty()) {
                detail = JsonParser.parseString(detailJson).getAsJsonObject();
            }
        }

        // -------------------------
        // NUTRIENTS
        // -------------------------
        double calories = 0;
        double protein = 0;
        double fat = 0;
        double carbs = 0;
        double cholesterol = 0;
        double sodium = 0;
        double sugar = 0;
        double fiber = 0;

        JsonArray nutrients = null;

        if (detail != null && detail.has("foodNutrients")) {
            nutrients = detail.getAsJsonArray("foodNutrients");
        } else if (bestFood.has("foodNutrients")) {
            nutrients = bestFood.getAsJsonArray("foodNutrients");
        }

        if (nutrients != null) {

            for (int i = 0; i < nutrients.size(); i++) {

                JsonObject nutrient = nutrients.get(i).getAsJsonObject();

                String nutrientName = "";
                String nutrientNumber = "";
                String unitName = "";
                double value = 0;

                if (nutrient.has("nutrientName")) {

                    nutrientName = nutrient.get("nutrientName")
                            .getAsString()
                            .toLowerCase();

                    value = nutrient.has("value")
                            ? nutrient.get("value").getAsDouble()
                            : 0;

                    nutrientNumber = nutrient.has("nutrientNumber")
                            ? nutrient.get("nutrientNumber").getAsString()
                            : "";

                    unitName = nutrient.has("unitName")
                            ? nutrient.get("unitName").getAsString()
                            : "";
                }

                else if (nutrient.has("nutrient")) {

                    JsonObject n = nutrient.getAsJsonObject("nutrient");

                    nutrientName = n.has("name")
                            ? n.get("name").getAsString().toLowerCase()
                            : "";

                    nutrientNumber = n.has("number")
                            ? n.get("number").getAsString()
                            : "";

                    unitName = n.has("unitName")
                            ? n.get("unitName").getAsString()
                            : "";

                    value = nutrient.has("amount")
                            ? nutrient.get("amount").getAsDouble()
                            : nutrient.has("value")
                              ? nutrient.get("value").getAsDouble()
                              : 0;
                }

                if (nutrientName.isEmpty()) continue;

                if (nutrientNumber.equals("208")
                        || nutrientName.contains("energy")) {

                    if (unitName.equalsIgnoreCase("KCAL")
                            || unitName.isEmpty()) {
                        calories = value;
                    }
                }

                switch (nutrientName) {

                    case "protein":
                        protein = value;
                        break;

                    case "total lipid (fat)":
                    case "fat":
                        fat = value;
                        break;

                    case "carbohydrate, by difference":
                    case "carbohydrate":
                        carbs = value;
                        break;

                    case "cholesterol":
                        cholesterol = value;
                        break;

                    case "sodium, na":
                        sodium = value;
                        break;

                    case "total sugars":
                    case "sugars, total including nlea":
                        sugar = value;
                        break;

                    case "fiber, total dietary":
                        fiber = value;
                        break;
                }
            }
        }

        // -------------------------
        // SERVING SIZE
        // -------------------------
        double targetGramWeight = 100.0;
        String servingLabel = "100g";

        JsonObject targetContainer = (detail != null) ? detail : bestFood;

        if (targetContainer.has("foodPortions")
                && !targetContainer.getAsJsonArray("foodPortions").isEmpty()) {

            JsonArray portions = targetContainer.getAsJsonArray("foodPortions");

            JsonObject preferred = null;
            int bestSeq = Integer.MAX_VALUE;

            for (int i = 0; i < portions.size(); i++) {

                JsonObject p = portions.get(i).getAsJsonObject();

                String modifier = p.has("modifier")
                        ? p.get("modifier").getAsString().toLowerCase()
                        : "";

                int seq = p.has("sequenceNumber")
                        ? p.get("sequenceNumber").getAsInt()
                        : 999;

                if (preferred == null && modifier.contains("medium")) {
                    preferred = p;
                }

                if (preferred == null && seq < bestSeq) {
                    bestSeq = seq;
                    preferred = p;
                }
            }

            if (preferred != null && preferred.has("gramWeight")) {

                targetGramWeight = preferred.get("gramWeight").getAsDouble();

                servingLabel = preferred.has("modifier")
                        ? preferred.get("modifier").getAsString()
                        : targetGramWeight + "g";
            }
        }

        if (targetGramWeight == 100.0 && targetContainer.has("servingSize")) {

            targetGramWeight = targetContainer.get("servingSize").getAsDouble();

            servingLabel = targetContainer.has("householdServingFullText")
                    ? targetContainer.get("householdServingFullText").getAsString()
                    : targetGramWeight + "g";
        }

        // -------------------------
        // SCALING
        // -------------------------
        double scale = targetGramWeight / 100.0;

        calories = round(calories * scale);
        protein = round(protein * scale);
        fat = round(fat * scale);
        carbs = round(carbs * scale);
        cholesterol = round(cholesterol * scale);
        sodium = round(sodium * scale);
        sugar = round(sugar * scale);
        fiber = round(fiber * scale);

        return new Food(
                name,
                new NutritionDetails(
                        calories, protein, fat, carbs,
                        cholesterol, sodium, sugar, fiber
                ),
                false
        );
    }


    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}