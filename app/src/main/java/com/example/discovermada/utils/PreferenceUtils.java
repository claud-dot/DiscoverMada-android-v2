package com.example.discovermada.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.example.discovermada.R;

import java.util.Locale;

public class PreferenceUtils {

    public static boolean isNotified(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("notif_preference", true);
    }

    public static String currentLang(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("lang_preference", "fr");
    }

    public static void notifWelcome(Context context , NotificationManager manager){
        NotificationUtils utils = new NotificationUtils();
        SharedPreferences preferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean isUserLoggedIn = preferences.getBoolean("isUserLoggedIn", false);

        if (isUserLoggedIn && isNotified(context)) {
            utils.createNotificationChannel(manager , context);
            utils.showNotification(context);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isUserLoggedIn", false);
            editor.apply();
        }
    }

    public static void updateAppLanguageAndTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedLanguage = sharedPreferences.getString("lang_preference", "fr");
        boolean darkModeEnabled = sharedPreferences.getBoolean("mod_preference", false);

        if (!getCurrentLanguage(context).equals(selectedLanguage)) {
            Locale newLocale = new Locale(selectedLanguage);
            Locale.setDefault(newLocale);
            Configuration configuration = context.getResources().getConfiguration();
            configuration.locale = newLocale;
            context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).recreate();
            } else if (context instanceof Activity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ((Activity) context).recreate();
                }
            }
        }
        applyAppTheme(context, darkModeEnabled);
    }

    public static void  updateNotifPreference(SharedPreferences sharedPreferences , Context context){
        boolean notificationsEnabled = sharedPreferences.getBoolean("notif_preference", true);

        SharedPreferences userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putBoolean("areNotificationsEnabled", notificationsEnabled);
        editor.apply();
    }

    private static String getCurrentLanguage(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    public static void applyAppTheme(Context context , boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    public static void restoreDefaultPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d("RESTOR +++", "restoreDefaultPreferences: ");
        editor.putBoolean("notif_preference", true);
        editor.putBoolean("mod_preference", false);
        editor.putString("lang_preference" , "fr");
        editor.apply();
        updateAppLanguageAndTheme(context);
    }

}
