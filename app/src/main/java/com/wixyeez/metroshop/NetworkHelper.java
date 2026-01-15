package com.wixyeez.metroshop;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetworkHelper {
    
    // Замени на URL твоего сервера
    private static final String API_URL = "https://your-domain.com/api/";
    
    public interface OnProductsLoadedListener {
        void onProductsLoaded(List<MainActivity.Product> products);
        void onError(String error);
    }
    
    public static void loadProducts(final OnProductsLoadedListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    URL url = new URL(API_URL + "get_products.php");
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
                    return null;
                }
            }
            
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject response = new JSONObject(result);
                        if (response.getBoolean("success")) {
                            JSONArray productsArray = response.getJSONArray("products");
                            List<MainActivity.Product> products = new ArrayList<>();
                            
                            for (int i = 0; i < productsArray.length(); i++) {
                                JSONObject p = productsArray.getJSONObject(i);
                                
                                products.add(new MainActivity.Product(
                                    p.getInt("id"),
                                    p.getString("name"),
                                    p.getString("description"),
                                    p.getString("category"),
                                    p.getDouble("price"),
                                    p.getDouble("old_price"),
                                    p.getInt("discount"),
                                    (float) p.getDouble("rating"),
                                    p.getInt("reviews"),
                                    p.getString("emoji")
                                ));
                            }
                            
                            listener.onProductsLoaded(products);
                        }
                    } catch (Exception e) {
                        listener.onError("Ошибка парсинга данных");
                    }
                } else {
                    listener.onError("Ошибка подключения");
                }
            }
        }.execute();
    }
}