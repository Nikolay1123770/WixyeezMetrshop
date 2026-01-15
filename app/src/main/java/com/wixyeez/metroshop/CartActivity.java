package com.wixyeez.metroshop;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CartActivity extends Activity {

    private LinearLayout cartContainer, emptyState;
    private TextView backBtn, clearBtn, totalPrice, checkoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        loadCart();
    }

    private void initViews() {
        cartContainer = findViewById(R.id.cartContainer);
        emptyState = findViewById(R.id.emptyState);
        backBtn = findViewById(R.id.backBtn);
        clearBtn = findViewById(R.id.clearBtn);
        totalPrice = findViewById(R.id.totalPrice);
        checkoutBtn = findViewById(R.id.checkoutBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.cart.clear();
                loadCart();
                Toast.makeText(CartActivity.this, "🗑️ Корзина очищена", Toast.LENGTH_SHORT).show();
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.cart.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Корзина пуста!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "💳 Оформление заказа...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCart() {
        // Clear container
        for (int i = cartContainer.getChildCount() - 1; i >= 0; i--) {
            View child = cartContainer.getChildAt(i);
            if (child.getId() != R.id.emptyState) {
                cartContainer.removeViewAt(i);
            }
        }

        if (MainActivity.cart.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            totalPrice.setText("0 ₽");
        } else {
            emptyState.setVisibility(View.GONE);
            
            double total = 0;
            
            for (int i = 0; i < MainActivity.cart.size(); i++) {
                final MainActivity.Product product = MainActivity.cart.get(i);
                final int index = i;
                
                View item = LayoutInflater.from(this).inflate(R.layout.item_cart, null);
                
                TextView emoji = item.findViewById(R.id.productEmoji);
                TextView name = item.findViewById(R.id.productName);
                TextView price = item.findViewById(R.id.productPrice);
                TextView removeBtn = item.findViewById(R.id.removeBtn);
                
                emoji.setText(product.emoji);
                name.setText(product.name);
                price.setText(String.format("%.0f ₽", product.price));
                
                removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.cart.remove(product);
                        loadCart();
                    }
                });
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                item.setLayoutParams(params);
                
                cartContainer.addView(item, 0);
                
                total += product.price;
            }
            
            totalPrice.setText(String.format("%.0f ₽", total));
        }
    }
}