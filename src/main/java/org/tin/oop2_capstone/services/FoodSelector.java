package org.tin.oop2_capstone.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FoodSelector {

    // -----------------------------
    // Normalize strings (important for determinism)
    // -----------------------------
    private static String normalize(String s) {
        return s.toLowerCase()
                .replaceAll("[^a-z0-9 ]", "")
                .trim();
    }

    // -----------------------------
    // Junk filter
    // -----------------------------
    public static boolean isJunk(String name) {

        name = name.toLowerCase();

        String[] junkKeywords = {
                "powder", "dehydrated", "dried", "canned",
                "pickled", "preserved", "concentrate", "frozen",
                "imitation", "juice", "sauce", "strudel",
                "dessert", "cured", "corned", "candied",
                "syrup", "cracker", "roll"
        };

        for (String keyword : junkKeywords) {
            if (name.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public static int cookingScore(String name, String query) {

        name = name.toLowerCase();
        query = query.toLowerCase();

        int score = 0;

        // exact ingredient boost
        if (name.contains(query)) {
            score += 30;
        }

        // simple food boost
        if (name.split(" ").length <= 4) {
            score += 5;
        }

        // preferred datasets
        if (name.contains("raw")) {
            score += 10;
        }

        return score;
    }

    // -----------------------------
    // MAIN PICKER (deterministic)
    // -----------------------------
    public static JsonObject pickBest(JsonArray foods, String userQuery) {

        if (foods == null || foods.size() == 0) return null;

        String qNorm = normalize(userQuery);

        JsonObject best = null;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < foods.size(); i++) {

            JsonObject candidate = foods.get(i).getAsJsonObject();

            if (!candidate.has("description")) continue;

            String name = candidate.get("description").getAsString();
            String nameNorm = normalize(name);

            // -----------------------------
            // 1. EXACT MATCH (HIGHEST PRIORITY)
            // -----------------------------
            if (nameNorm.equals(qNorm)) {
                return candidate;
            }

            // -----------------------------
            // 2. JUNK FILTER (skip unless needed)
            // -----------------------------
            if (isJunk(name) && !nameNorm.contains(qNorm)) {
                continue;
            }

            // -----------------------------
            // 3. SCORE
            // -----------------------------
            int score = cookingScore(name, userQuery);

            // boost data quality
            if (candidate.has("dataType")) {

                String type = candidate.get("dataType").getAsString();

                if (type.equalsIgnoreCase("Foundation")) score += 15;
                if (type.equalsIgnoreCase("SR Legacy")) score += 10;
                if (type.equalsIgnoreCase("Branded")) score += 5;
            }

            // -----------------------------
            // 4. UPDATE BEST
            // -----------------------------
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        return best;
    }
}