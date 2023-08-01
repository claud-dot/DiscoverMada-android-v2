package com.example.discovermada.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.discovermada.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstTime();
            }
        },2000);

//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    sleep(3000);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }finally {
//                    Intent intent =  new Intent(SplashActivity.this , MainActivity.class);
//                    startActivity(intent);
//                }
//            }
//        };
//        thread.start();
    }

    private void isFirstTime(){
        SharedPreferences preferences = getApplication().getSharedPreferences("onSplash" , Context.MODE_PRIVATE);
        boolean isFirst = preferences.getBoolean("isFirstTime" , true);
        if(isFirst){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime" , false);
            editor.apply();
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }else{
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }
}