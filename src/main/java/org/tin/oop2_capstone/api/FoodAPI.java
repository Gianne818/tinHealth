package org.tin.oop2_capstone.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodAPI {
    private static final String FOOD_API_KEY = System.getenv("var1");

    public static String getFoodData(String query) {
        try {
            String urlString = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" + query
                    + "&dataType=Foundation,SR%20Legacy&pageSize=5&api_key=" + FOOD_API_KEY;

            return fetch(urlString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fetch the full detail record for a specific food by its fdcId.
    // This is the only endpoint that includes the foodMeasures array (serving sizes).
    public static String getFoodDetail(int fdcId) {
        try {
            String urlString = "https://api.nal.usda.gov/fdc/v1/food/" + fdcId
                    + "?api_key=" + FOOD_API_KEY;

            return fetch(urlString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String fetch(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }
}