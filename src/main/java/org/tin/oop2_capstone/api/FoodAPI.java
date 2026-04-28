package org.tin.oop2_capstone.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodAPI {
    private static final String FOOD_API_KEY = "IyFOfsOqFASMkLhvVoLpIntMChfeFviV04ucj5A6";

    public static String getFoodData(String query) {
        try {
            String urlString = "https://api.nal.usda.gov/fdc/v1/foods/search?query="
                    + query + "&api_key=" + FOOD_API_KEY;

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

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}