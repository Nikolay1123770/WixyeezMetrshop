package com.wixyeez.metroshop;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ProductDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Simple detail layout for now
        int productId = getIntent().getIntExtra("product_id", 0);
        
        Toast.makeText(this, "Товар #" + productId, Toast.LENGTH_SHORT).show();
        finish(); // For now just go back
    }
}