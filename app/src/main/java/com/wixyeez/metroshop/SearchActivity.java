package com.wixyeez.metroshop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {

    private EditText searchInput;
    private LinearLayout resultsContainer;
    private TextView backBtn, recentTitle;
    private LinearLayout navHome, navSearch, navFavorites, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupSearch();
        setupNavigation();
        showRecentSearches();
    }

    private void initViews() {
        searchInput = findViewById(R.id.searchInput);
        resultsContainer = findViewById(R.id.resultsContainer);
        backBtn = findViewById(R.id.backBtn);
        recentTitle = findViewById(R.id.recentTitle);
        
        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navFavorites = findViewById(R.id.navFavorites);
        navProfile = findViewById(R.id.navProfile);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    recentTitle.setText("Результаты поиска");
                    searchProducts(s.toString());
                } else {
                    recentTitle.setText("Недавние поиски");
                    showRecentSearches();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showRecentSearches() {
        resultsContainer.removeAllViews();
        
        String[] recent = {"Буст CS2", "Сопровождение", "GTA 5"};
        
        for (final String search : recent) {
            TextView item = new TextView(this);
            item.setText("🕐 " + search);
            item.setTextColor(0xFFB3B3CC);
            item.setTextSize(14);
            item.setPadding(0, 20, 0, 20);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchInput.setText(search);
                }
            });
            resultsContainer.addView(item);
        }
    }

    private void searchProducts(String query) {
        resultsContainer.removeAllViews();
        
        List<MainActivity.Product> allProducts = getProducts();
        List<MainActivity.Product> results = new ArrayList<>();
        
        for (MainActivity.Product p : allProducts) {
            if (p.name.toLowerCase().contains(query.toLowerCase()) ||
                p.desc.toLowerCase().contains(query.toLowerCase()) ||
                p.category.toLowerCase().contains(query.toLowerCase())) {
                results.add(p);
            }
        }
        
        if (results.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("😕 Ничего не найдено");
            empty.setTextColor(0xFFB3B3CC);
            empty.setTextSize(16);
            empty.setPadding(0, 50, 0, 0);
            resultsContainer.addView(empty);
        } else {
            for (MainActivity.Product p : results) {
                View card = createSearchResult(p);
                resultsContainer.addView(card);
            }
        }
    }

    private View createSearchResult(final MainActivity.Product product) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setBackgroundResource(R.drawable.card_bg);
        item.setPadding(24, 24, 24, 24);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 16;
        item.setLayoutParams(params);

        // Emoji
        TextView emoji = new TextView(this);
        emoji.setText(product.emoji);
        emoji.setTextSize(28);
        item.addView(emoji);

        // Info
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        infoParams.leftMargin = 24;
        info.setLayoutParams(infoParams);

        TextView name = new TextView(this);
        name.setText(product.name);
        name.setTextColor(0xFFFFFFFF);
        name.setTextSize(14);
        info.addView(name);

        TextView cat = new TextView(this);
        cat.setText(product.category);
        cat.setTextColor(0xFF6C63FF);
        cat.setTextSize(12);
        info.addView(cat);

        item.addView(info);

        // Price
        TextView price = new TextView(this);
        price.setText(String.format("%.0f ₽", product.price));
        price.setTextColor(0xFF00E676);
        price.setTextSize(16);
        item.addView(price);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.id);
                startActivity(intent);
            }
        });

        return item;
    }

    private List<MainActivity.Product> getProducts() {
        List<MainActivity.Product> products = new ArrayList<>();
        products.add(new MainActivity.Product(1, "Буст CS2 Ранга", "До любого ранга", "Услуги", 1990, 2500, 20, 4.9f, 312, "🛡️"));
        products.add(new MainActivity.Product(2, "Прокачка Valorant", "От Iron до Radiant", "Услуги", 2990, 3500, 15, 4.8f, 189, "🛡️"));
        products.add(new MainActivity.Product(3, "Калибровка Dota 2", "10 игр калибровки", "Услуги", 1490, 1990, 25, 4.7f, 256, "🛡️"));
        products.add(new MainActivity.Product(4, "Coaching Apex", "3 часа с Pro", "Услуги", 3500, 4500, 22, 5.0f, 56, "🛡️"));
        products.add(new MainActivity.Product(5, "GTA 5 Mega Pack", "500M$, Все машины", "Сеты", 2990, 3990, 25, 4.6f, 167, "📦"));
        products.add(new MainActivity.Product(6, "Fortnite Pack", "50 скинов", "Сеты", 1990, 2490, 20, 4.8f, 234, "📦"));
        products.add(new MainActivity.Product(7, "Minecraft Pack", "Все моды", "Сеты", 990, 1500, 34, 4.9f, 445, "📦"));
        products.add(new MainActivity.Product(8, "Roblox Set", "10000 Robux", "Сеты", 2490, 2990, 17, 4.7f, 312, "📦"));
        products.add(new MainActivity.Product(9, "Сопровождение CS2", "24/7 на месяц", "Сопровождение", 4990, 6000, 17, 5.0f, 89, "🎯"));
        products.add(new MainActivity.Product(10, "Сопровождение Valorant", "2 недели", "Сопровождение", 3990, 5000, 20, 4.9f, 67, "🎯"));
        products.add(new MainActivity.Product(11, "VIP Сопровождение", "Все игры", "Сопровождение", 9990, 12000, 17, 5.0f, 34, "🎯"));
        products.add(new MainActivity.Product(12, "Турнирная подготовка", "С командой", "Сопровождение", 7990, 9990, 20, 4.8f, 45, "🎯"));
        return products;
    }

    private void setupNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                finish();
            }
        });

        navFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, FavoritesActivity.class));
                finish();
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }
}