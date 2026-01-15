package com.wixyeez.metroshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private ProgressBar progressBar;
    private TextView loadingText;
    private View logoFrame;
    private View glowView;
    
    private Handler handler = new Handler();
    private int progress = 0;
    
    private String[] messages = {"Загрузка...", "Подключение...", "Загрузка товаров...", "Почти готово..."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        logoFrame = findViewById(R.id.logoFrame);
        glowView = findViewById(R.id.glowView);

        animateLogo();
        animateGlow();
        startLoading();
    }

    private void animateLogo() {
        RotateAnimation rotate = new RotateAnimation(0, 360,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setFillAfter(true);
        logoFrame.startAnimation(rotate);
    }

    private void animateGlow() {
        AlphaAnimation pulse = new AlphaAnimation(0.2f, 0.6f);
        pulse.setDuration(1500);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        glowView.startAnimation(pulse);
    }

    private void startLoading() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progress <= 100) {
                    progressBar.setProgress(progress);
                    int idx = Math.min(progress / 30, messages.length - 1);
                    loadingText.setText(messages[idx]);
                    progress += 2;
                    handler.postDelayed(this, 40);
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}