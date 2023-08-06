package com.example.discovermada.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.discovermada.R;
import com.example.discovermada.utils.PreferenceUtils;
import com.example.discovermada.utils.Utils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceUtils.updateAppLanguageAndTheme(this);
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();

        SharedPreferences preferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isUserLoggedIn", true);
        editor.apply();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        },2000);

    }
}
