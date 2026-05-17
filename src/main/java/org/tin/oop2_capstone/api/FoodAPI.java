package org.tin.oop2_capstone.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//CHECK
public class FoodAPI {
    private static final String FOOD_API_KEY = "IyFOfsOqFASMkLhvVoLpIntMChfeFviV04ucj5A6";

    public static String getFoodData(String query) {
        try {
            String urlString = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" + query
                    + "&dataType=Foundation,SR%20Legacy,Branded&pageSize=15&api_key=" + FOOD_API_KEY;

            System.out.println("REached here");
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

    private static String fetch(String urlString) throws Exception{
        int maxRetries = 5;
        for(int i = 1; i<=maxRetries; i++){
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // so we dont wait to long to establish connection and wait long for the api to send results
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            // 200 is successfull
            if(responseCode == 200){
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                conn.disconnect();
                return response.toString();
            }
            else {
                conn.disconnect();
                System.out.println("API Error: " + responseCode + ", retries: " + i);
                System.out.println("Retrying");
                Thread.sleep(100);

            }
        }
        return null;
    }
}