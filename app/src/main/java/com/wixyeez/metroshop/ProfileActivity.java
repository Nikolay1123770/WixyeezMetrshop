package com.wixyeez.metroshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

    private TextView backBtn, loginBtn;
    private LinearLayout ordersItem, supportItem, settingsItem, aboutItem;
    private LinearLayout navHome, navSearch, navFavorites, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupMenuItems();
        setupNavigation();
    }

    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        loginBtn = findViewById(R.id.loginBtn);
        ordersItem = findViewById(R.id.ordersItem);
        supportItem = findViewById(R.id.supportItem);
        settingsItem = findViewById(R.id.settingsItem);
        aboutItem = findViewById(R.id.aboutItem);
        
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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "🔐 Авторизация будет добавлена", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMenuItems() {
        ordersItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "📦 Мои заказы", Toast.LENGTH_SHORT).show();
            }
        });

        supportItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "💬 Поддержка: @wixyeez_support", Toast.LENGTH_SHORT).show();
            }
        });

        settingsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "⚙️ Настройки", Toast.LENGTH_SHORT).show();
            }
        });

        aboutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "WIXYEEZ METRO SHOP v1.0.0\nPremium Gaming Store", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
                finish();
            }
        });

        navFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, FavoritesActivity.class));
                finish();
            }
        });
    }
}