package com.wixyeez.metroshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FavoritesActivity extends Activity {

    private LinearLayout favoritesContainer, emptyState;
    private TextView backBtn, browseBtn;
    private LinearLayout navHome, navSearch, navFavorites, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void initViews() {
        favoritesContainer = findViewById(R.id.favoritesContainer);
        emptyState = findViewById(R.id.emptyState);
        backBtn = findViewById(R.id.backBtn);
        browseBtn = findViewById(R.id.browseBtn);
        
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

        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void loadFavorites() {
        if (MainActivity.favorites.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
            displayFavorites();
        }
    }

    private void displayFavorites() {
        // Clear except emptyState
        for (int i = favoritesContainer.getChildCount() - 1; i >= 0; i--) {
            View child = favoritesContainer.getChildAt(i);
            if (child != emptyState) {
                favoritesContainer.removeViewAt(i);
            }
        }

        for (final MainActivity.Product product : MainActivity.favorites) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setBackgroundResource(R.drawable.card_bg);
            item.setPadding(24, 24, 24, 24);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 16;
            item.setLayoutParams(params);

            TextView emoji = new TextView(this);
            emoji.setText(product.emoji);
            emoji.setTextSize(28);
            item.addView(emoji);

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

            TextView price = new TextView(this);
            price.setText(String.format("%.0f ₽", product.price));
            price.setTextColor(0xFF00E676);
            price.setTextSize(14);
            info.addView(price);

            item.addView(info);

            TextView removeBtn = new TextView(this);
            removeBtn.setText("❤️");
            removeBtn.setTextSize(24);
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.favorites.remove(product);
                    product.isFav = false;
                    loadFavorites();
                }
            });
            item.addView(removeBtn);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FavoritesActivity.this, ProductDetailActivity.class);
                    intent.putExtra("product_id", product.id);
                    startActivity(intent);
                }
            });

            favoritesContainer.addView(item, 0);
        }
    }

    private void setupNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                finish();
            }
        });

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FavoritesActivity.this, SearchActivity.class));
                finish();
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FavoritesActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }
}