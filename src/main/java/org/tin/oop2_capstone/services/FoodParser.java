package org.tin.oop2_capstone.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

public class FoodParser {

    public static Food parseFood(String searchJson) {

        JsonObject obj = JsonParser.parseString(searchJson).getAsJsonObject();
        JsonArray foods = obj.getAsJsonArray("foods");

        if (foods == null || foods.isEmpty()) {
            return null;
        }

        // Junk filters
        String[] junkKeywords = {
                "powder",
                "dehydrated",
                "dried",
                "canned",
                "pickled",
                "preserved",
                "concentrate",
                "frozen",
                "imitation",
                "rose-apple",
                "juice",
                "sauce",
                "pastry",
                "strudel",
                "dessert",
                "cured",
                "corned",
                "sweet",
                "candied",
                "syrup"
        };

        // Select best result
        JsonObject bestFood = null;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < foods.size(); i++) {

            JsonObject candidate =
                    foods.get(i).getAsJsonObject();

            if (!candidate.has("description")) {
                continue;
            }

            String candidateName =
                    candidate.get("description")
                            .getAsString()
                            .toLowerCase();

            // Skip junk
            boolean isJunk = false;

            for (String keyword : junkKeywords) {

                if (candidateName.contains(keyword)) {

                    isJunk = true;

                    System.out.println(
                            "Skipping junk result: "
                                    + candidateName
                    );

                    break;
                }
            }

            if (isJunk) {
                continue;
            }

            // ─────────────────────────────────────────
            // Scoring
            // ─────────────────────────────────────────
            int score = 0;

            if (candidateName.contains("raw")) {
                score += 10;
            }

            if (candidateName.contains("cooked")) {
                score -= 3;
            }

            if (candidateName.split(" ").length <= 4) {
                score += 5;
            }

            if (candidateName.contains(",")) {
                score -= 2;
            }

            if (candidate.has("dataType")) {

                String dataType =
                        candidate.get("dataType")
                                .getAsString();

                if (dataType.equalsIgnoreCase("Foundation")) {
                    score += 15;
                }

                if (dataType.equalsIgnoreCase("SR Legacy")) {
                    score += 10;
                }
            }

            if (score > bestScore) {
                bestScore = score;
                bestFood = candidate;
            }
        }

        // Fallback
        if (bestFood == null) {

            bestFood =
                    foods.get(0).getAsJsonObject();

            System.out.println(
                    "Warning: all results filtered."
            );
        }

        // ─────────────────────────────────────────────────────────────
        // Food name
        // ─────────────────────────────────────────────────────────────
        String name =
                bestFood.get("description")
                        .getAsString();

        // ─────────────────────────────────────────────────────────────
        // Fetch FULL detail record
        // ─────────────────────────────────────────────────────────────
        JsonObject detail = null;

        if (bestFood.has("fdcId")) {

            int fdcId =
                    bestFood.get("fdcId").getAsInt();

            String detailJson =
                    FoodAPI.getFoodDetail(fdcId);

            if (detailJson != null) {

                detail =
                        JsonParser.parseString(detailJson)
                                .getAsJsonObject();
            }
        }

        // ─────────────────────────────────────────────────────────────
        // Nutrient values
        // ─────────────────────────────────────────────────────────────
        double calories = 0;
        double protein = 0;
        double fat = 0;
        double carbs = 0;
        double cholesterol = 0;
        double sodium = 0;
        double sugar = 0;
        double fiber = 0;

        JsonArray nutrients = null;

        // Prefer detail endpoint nutrients
        if (detail != null
                && detail.has("foodNutrients")) {

            nutrients =
                    detail.getAsJsonArray("foodNutrients");
        }

        // Fallback
        else if (bestFood.has("foodNutrients")) {

            nutrients =
                    bestFood.getAsJsonArray("foodNutrients");
        }

        // Parse nutrients

        if (nutrients != null) {

            for (int i = 0; i < nutrients.size(); i++) {

                JsonObject nutrient =
                        nutrients.get(i).getAsJsonObject();

                String nutrientName = "";
                String nutrientNumber = "";
                String unitName = "";
                double value = 0;

                // FORMAT A
                // nutrientName + value

                if (nutrient.has("nutrientName")) {

                    nutrientName =
                            nutrient.get("nutrientName")
                                    .getAsString()
                                    .toLowerCase();

                    if (nutrient.has("value")) {

                        value =
                                nutrient.get("value")
                                        .getAsDouble();
                    }

                    if (nutrient.has("nutrientNumber")) {

                        nutrientNumber =
                                nutrient.get("nutrientNumber")
                                        .getAsString();
                    }

                    if (nutrient.has("unitName")) {

                        unitName =
                                nutrient.get("unitName")
                                        .getAsString();
                    }
                }

                // FORMAT B
                // nutrient.name + amount

                else if (nutrient.has("nutrient")) {

                    JsonObject nutrientObj =
                            nutrient.getAsJsonObject("nutrient");

                    if (nutrientObj.has("name")) {

                        nutrientName =
                                nutrientObj.get("name")
                                        .getAsString()
                                        .toLowerCase();
                    }

                    if (nutrientObj.has("number")) {

                        nutrientNumber =
                                nutrientObj.get("number")
                                        .getAsString();
                    }

                    if (nutrientObj.has("unitName")) {

                        unitName =
                                nutrientObj.get("unitName")
                                        .getAsString();
                    }

                    if (nutrient.has("amount")) {

                        value =
                                nutrient.get("amount")
                                        .getAsDouble();
                    }

                    else if (nutrient.has("value")) {

                        value =
                                nutrient.get("value")
                                        .getAsDouble();
                    }
                }

                // Skip invalid nutrients
                if (nutrientName.isEmpty()) {
                    continue;
                }

                // ─────────────────────────────────────────
                // Calories
                // Avoid KJ values
                // ─────────────────────────────────────────
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

        // Serving size detection
        double targetGramWeight = 100.0;
        String servingLabel = "100g";

        if (detail != null) {

            // Foundation / SR Legacy
            if (detail.has("foodPortions")) {

                JsonArray portions =
                        detail.getAsJsonArray("foodPortions");

                JsonObject preferredPortion = null;

                int bestSeq = Integer.MAX_VALUE;

                for (int i = 0; i < portions.size(); i++) {

                    JsonObject p =
                            portions.get(i).getAsJsonObject();

                    String modifier =
                            p.has("modifier")
                                    ? p.get("modifier")
                                      .getAsString()
                                      .toLowerCase()
                                    : "";

                    int seq =
                            p.has("sequenceNumber")
                                    ? p.get("sequenceNumber")
                                      .getAsInt()
                                    : 999;

                    // Prefer medium
                    if (preferredPortion == null
                            && modifier.contains("medium")) {

                        preferredPortion = p;
                    }

                    // Fallback lowest sequence
                    if (preferredPortion == null
                            && seq < bestSeq) {

                        bestSeq = seq;
                        preferredPortion = p;
                    }
                }

                if (preferredPortion != null
                        && preferredPortion.has("gramWeight")) {

                    targetGramWeight =
                            preferredPortion.get("gramWeight")
                                    .getAsDouble();

                    servingLabel =
                            preferredPortion.has("modifier")
                                    ? preferredPortion.get("modifier")
                                      .getAsString()
                                    : targetGramWeight + "g";
                }
            }

            // Branded fallback
            if (targetGramWeight == 100.0
                    && detail.has("servingSize")) {

                targetGramWeight =
                        detail.get("servingSize")
                                .getAsDouble();

                if (detail.has("householdServingFullText")) {

                    servingLabel =
                            detail.get("householdServingFullText")
                                    .getAsString();
                }

                else {

                    String unit =
                            detail.has("servingSizeUnit")
                                    ? detail.get("servingSizeUnit")
                                      .getAsString()
                                    : "g";

                    servingLabel =
                            targetGramWeight + " " + unit;
                }
            }
        }

        // Scale nutrients to serving size
        double scale =
                targetGramWeight / 100.0;

        calories *= scale;
        protein *= scale;
        fat *= scale;
        carbs *= scale;
        cholesterol *= scale;
        sodium *= scale;
        sugar *= scale;
        fiber *= scale;

        // Debug
//        System.out.println(
//                "Serving: "
//                        + servingLabel
//                        + " ("
//                        + targetGramWeight
//                        + "g)"
//        );

        return new Food(name, new NutritionDetails(
                        calories,
                        protein,
                        fat,
                        carbs,
                        cholesterol,
                        sodium,
                        sugar,
                        fiber
                ),
                false
        );
    }
}