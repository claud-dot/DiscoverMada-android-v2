package com.example.discovermada.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.discovermada.R;
import com.example.discovermada.utils.PreferenceUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        PreferenceUtils.updateAppLanguageAndTheme(this);
        setContentView(R.layout.activity_splash);
        if (savedInstanceState == null) {
            controlActivity();
        }
    }

    private void controlActivity() {
        String user_session = PreferenceUtils.getSessionToken(this);
        if (user_session != null) {
            initPreference(user_session);
        }

        Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (user_session != null) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, 2000);
    }

    private void initPreference(String user_session){
        PreferenceUtils.applyAppLanguage(SplashActivity.this , user_session);
        PreferenceUtils.applyAppTheme(SplashActivity.this , user_session);
        PreferenceUtils.setFirstLogApp(SplashActivity.this , user_session,true );
        PreferenceUtils.setAppDisplayPreferences(SplashActivity.this , user_session);
    }
}
