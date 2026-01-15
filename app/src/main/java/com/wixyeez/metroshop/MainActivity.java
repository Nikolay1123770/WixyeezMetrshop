package com.wixyeez.metroshop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class MainActivity extends Activity {

    private static final String TAG = "WixyeezShop";
    private static final String API_URL = "https://wixyeez-api.onrender.com/api/get_products.php";
    
    private LinearLayout productsContainer;
    private FrameLayout cartBtn;
    private TextView cartBadge;
    private TextView searchBar;
    
    private TextView catAll, catServices, catSets, catSupport;
    private LinearLayout navHome, navSearch, navFavorites, navProfile;
    
    private List<Product> allProducts = new ArrayList<>();
    private String currentCategory = "all";
    
    public static List<Product> cart = new ArrayList<>();
    public static List<Product> favorites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupCategories();
        setupNavigation();
        
        trustAllCertificates();
        loadProductsFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            Log.e(TAG, "SSL Error: " + e.getMessage());
        }
    }

    private void initViews() {
        productsContainer = findViewById(R.id.productsContainer);
        cartBtn = findViewById(R.id.cartBtn);
        cartBadge = findViewById(R.id.cartBadge);
        searchBar = findViewById(R.id.searchBar);
        
        catAll = findViewById(R.id.catAll);
        catServices = findViewById(R.id.catServices);
        catSets = findViewById(R.id.catSets);
        catSupport = findViewById(R.id.catSupport);
        
        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navFavorites = findViewById(R.id.navFavorites);
        navProfile = findViewById(R.id.navProfile);

        cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        searchBar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
    }

    private void loadProductsFromServer() {
        showLoading();
        new LoadProductsTask().execute(API_URL);
    }

    private void showLoading() {
        productsContainer.removeAllViews();
        TextView loadingText = new TextView(this);
        loadingText.setText("⏳ Загрузка товаров...");
        loadingText.setTextColor(0xFFB3B3CC);
        loadingText.setTextSize(16);
        loadingText.setGravity(android.view.Gravity.CENTER);
        loadingText.setPadding(0, 100, 0, 0);
        productsContainer.addView(loadingText);
    }

    private class LoadProductsTask extends AsyncTask<String, Void, String> {
        
        private String errorMessage = "";
        
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection conn = null;
            try {
                Log.d(TAG, "Connecting to: " + urls[0]);
                
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", "WixyeezApp/1.0");
                
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();
                    
                    String result = response.toString();
                    Log.d(TAG, "Response: " + result);
                    return result;
                } else {
                    errorMessage = "HTTP Error: " + responseCode;
                    Log.e(TAG, errorMessage);
                    return null;
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e(TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                parseProducts(result);
            } else {
                showError(errorMessage);
            }
        }
    }

    private void parseProducts(String json) {
        try {
            Log.d(TAG, "Parsing JSON: " + json);
            
            JSONObject response = new JSONObject(json);
            
            if (response.has("success") && response.getBoolean("success")) {
                JSONArray productsArray = response.getJSONArray("products");
                allProducts.clear();
                
                Log.d(TAG, "Products count: " + productsArray.length());
                
                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject p = productsArray.getJSONObject(i);
                    
                    int id = p.optInt("id", 0);
                    String name = p.optString("name", "Без названия");
                    String desc = p.optString("description", "");
                    String category = p.optString("category", "Другое");
                    
                    double price = 0;
                    try {
                        price = Double.parseDouble(p.optString("price", "0"));
                    } catch (Exception e) {
                        price = p.optDouble("price", 0);
                    }
                    
                    double oldPrice = price;
                    try {
                        oldPrice = Double.parseDouble(p.optString("old_price", String.valueOf(price)));
                    } catch (Exception e) {
                        oldPrice = p.optDouble("old_price", price);
                    }
                    
                    int discount = p.optInt("discount", 0);
                    float rating = (float) p.optDouble("rating", 4.5);
                    int reviews = p.optInt("reviews", 0);
                    String emoji = p.optString("emoji", "🔥");
                    
                    Product product = new Product(id, name, desc, category, price, oldPrice, discount, rating, reviews, emoji);
                    allProducts.add(product);
                    
                    Log.d(TAG, "Added product: " + name);
                }
                
                runOnUiThread(() -> {
                    displayProducts(allProducts);
                    Toast.makeText(MainActivity.this, "✅ Загружено: " + allProducts.size(), Toast.LENGTH_SHORT).show();
                });
                
            } else {
                String error = response.optString("error", "Unknown error");
                showError(error);
            }
        } catch (Exception e) {
            Log.e(TAG, "Parse error: " + e.getMessage());
            e.printStackTrace();
            showError("Ошибка обработки данных: " + e.getMessage());
        }
    }

    private void showError(final String message) {
        runOnUiThread(() -> {
            productsContainer.removeAllViews();
            
            LinearLayout errorLayout = new LinearLayout(MainActivity.this);
            errorLayout.setOrientation(LinearLayout.VERTICAL);
            errorLayout.setGravity(android.view.Gravity.CENTER);
            errorLayout.setPadding(50, 100, 50, 100);
            
            TextView errorIcon = new TextView(MainActivity.this);
            errorIcon.setText("😕");
            errorIcon.setTextSize(50);
            errorIcon.setGravity(android.view.Gravity.CENTER);
            errorLayout.addView(errorIcon);
            
            TextView errorText = new TextView(MainActivity.this);
            errorText.setText("Не удалось загрузить товары");
            errorText.setTextColor(0xFFFFFFFF);
            errorText.setTextSize(16);
            errorText.setGravity(android.view.Gravity.CENTER);
            errorLayout.addView(errorText);
            
            TextView errorDetail = new TextView(MainActivity.this);
            errorDetail.setText(message != null ? message : "Проверьте интернет");
            errorDetail.setTextColor(0xFF666680);
            errorDetail.setTextSize(12);
            errorDetail.setGravity(android.view.Gravity.CENTER);
            errorDetail.setPadding(0, 10, 0, 0);
            errorLayout.addView(errorDetail);
            
            TextView retryBtn = new TextView(MainActivity.this);
            retryBtn.setText("🔄 Повторить");
            retryBtn.setTextColor(0xFF6C63FF);
            retryBtn.setTextSize(16);
            retryBtn.setGravity(android.view.Gravity.CENTER);
            retryBtn.setPadding(0, 40, 0, 0);
            retryBtn.setOnClickListener(v -> loadProductsFromServer());
            errorLayout.addView(retryBtn);
            
            productsContainer.addView(errorLayout);
            
            Toast.makeText(MainActivity.this, "❌ " + (message != null ? message : "Ошибка"), Toast.LENGTH_LONG).show();
        });
    }

    private void displayProducts(List<Product> products) {
        productsContainer.removeAllViews();
        
        if (products.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("📦 Нет товаров");
            emptyText.setTextColor(0xFFB3B3CC);
            emptyText.setTextSize(16);
            emptyText.setGravity(android.view.Gravity.CENTER);
            emptyText.setPadding(0, 100, 0, 0);
            productsContainer.addView(emptyText);
            return;
        }
        
        LinearLayout row = null;
        
        for (int i = 0; i < products.size(); i++) {
            if (i % 2 == 0) {
                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
                productsContainer.addView(row);
            }
            
            View card = createProductCard(products.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            card.setLayoutParams(params);
            row.addView(card);
            
            animateCard(card, i);
        }
        
        if (products.size() % 2 != 0 && row != null) {
            View empty = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            empty.setLayoutParams(params);
            row.addView(empty);
        }
    }

    private View createProductCard(final Product product) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_product, null);
        
        TextView nameText = card.findViewById(R.id.nameText);
        TextView descText = card.findViewById(R.id.descText);
        TextView priceText = card.findViewById(R.id.priceText);
        TextView oldPriceText = card.findViewById(R.id.oldPriceText);
        TextView discountText = card.findViewById(R.id.discountText);
        TextView categoryText = card.findViewById(R.id.categoryText);
        TextView ratingText = card.findViewById(R.id.ratingText);
        TextView reviewsText = card.findViewById(R.id.reviewsText);
        final TextView favBtn = card.findViewById(R.id.favBtn);
        FrameLayout addBtn = card.findViewById(R.id.addBtn);

        nameText.setText(product.name);
        descText.setText(product.desc);
        priceText.setText(String.format("%.0f ₽", product.price));
        
        if (product.oldPrice > product.price) {
            oldPriceText.setText(String.format("%.0f ₽", product.oldPrice));
            oldPriceText.setPaintFlags(oldPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            oldPriceText.setVisibility(View.VISIBLE);
        } else {
            oldPriceText.setVisibility(View.GONE);
        }
        
        if (product.discount > 0) {
            discountText.setText("-" + product.discount + "%");
            discountText.setVisibility(View.VISIBLE);
        } else {
            discountText.setVisibility(View.GONE);
        }
        
        categoryText.setText(product.emoji + " " + product.category.toUpperCase());
        ratingText.setText(String.format("%.1f", product.rating));
        reviewsText.setText("(" + product.reviews + ")");
        
        product.isFav = isInFavorites(product);
        favBtn.setText(product.isFav ? "❤️" : "🤍");

        card.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "📦 " + product.name, Toast.LENGTH_SHORT).show();
        });

        addBtn.setOnClickListener(v -> {
            animateBtn(v);
            addToCart(product);
        });

        favBtn.setOnClickListener(v -> {
            animateBtn(v);
            toggleFavorite(product);
            favBtn.setText(product.isFav ? "❤️" : "🤍");
        });

        return card;
    }

    private void setupCategories() {
        View.OnClickListener categoryClick = v -> {
            resetCategories();
            v.setBackgroundResource(R.drawable.btn_gradient);
            ((TextView) v).setTextColor(0xFFFFFFFF);
            
            if (v == catAll) {
                currentCategory = "all";
                displayProducts(allProducts);
            } else if (v == catServices) {
                filterByCategory("Услуги");
            } else if (v == catSets) {
                filterByCategory("Сеты");
            } else if (v == catSupport) {
                filterByCategory("Сопровождение");
            }
        };
        
        catAll.setOnClickListener(categoryClick);
        catServices.setOnClickListener(categoryClick);
        catSets.setOnClickListener(categoryClick);
        catSupport.setOnClickListener(categoryClick);
    }

    private void resetCategories() {
        catAll.setBackgroundResource(R.drawable.chip_bg);
        catAll.setTextColor(0xFFB3B3CC);
        catServices.setBackgroundResource(R.drawable.chip_bg);
        catServices.setTextColor(0xFFB3B3CC);
        catSets.setBackgroundResource(R.drawable.chip_bg);
        catSets.setTextColor(0xFFB3B3CC);
        catSupport.setBackgroundResource(R.drawable.chip_bg);
        catSupport.setTextColor(0xFFB3B3CC);
    }

    private void filterByCategory(String category) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.category.equalsIgnoreCase(category)) {
                filtered.add(p);
            }
        }
        displayProducts(filtered);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> loadProductsFromServer());
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        navFavorites.setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void addToCart(Product product) {
        for (Product p : cart) {
            if (p.id == product.id) {
                Toast.makeText(this, "Уже в корзине!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        cart.add(product);
        updateCartBadge();
        Toast.makeText(this, "✅ Добавлено в корзину!", Toast.LENGTH_SHORT).show();
    }

    private void updateCartBadge() {
        if (cart.size() > 0) {
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(String.valueOf(cart.size()));
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }

    private boolean isInFavorites(Product product) {
        for (Product p : favorites) {
            if (p.id == product.id) return true;
        }
        return false;
    }

    private void toggleFavorite(Product product) {
        if (product.isFav) {
            for (int i = 0; i < favorites.size(); i++) {
                if (favorites.get(i).id == product.id) {
                    favorites.remove(i);
                    break;
                }
            }
            product.isFav = false;
            Toast.makeText(this, "💔 Удалено", Toast.LENGTH_SHORT).show();
        } else {
            favorites.add(product);
            product.isFav = true;
            Toast.makeText(this, "❤️ Добавлено", Toast.LENGTH_SHORT).show();
        }
    }

    private void animateCard(View v, int pos) {
        ScaleAnimation anim = new ScaleAnimation(0.8f, 1f, 0.8f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(200);
        anim.setStartOffset(pos * 50L);
        v.startAnimation(anim);
    }

    private void animateBtn(View v) {
        ScaleAnimation anim = new ScaleAnimation(1f, 0.8f, 1f, 0.8f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(100);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        v.startAnimation(anim);
    }

    public static class Product {
        public int id;
        public String name, desc, category, emoji;
        public double price, oldPrice;
        public int discount, reviews;
        public float rating;
        public boolean isFav;

        public Product(int id, String name, String desc, String category, 
                double price, double oldPrice, int discount, float rating, int reviews, String emoji) {
            this.id = id;
            this.name = name;
            this.desc = desc;
            this.category = category;
            this.price = price;
            this.oldPrice = oldPrice;
            this.discount = discount;
            this.rating = rating;
            this.reviews = reviews;
            this.emoji = emoji;
            this.isFav = false;
        }
    }
}