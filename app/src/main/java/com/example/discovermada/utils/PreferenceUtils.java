package com.example.discovermada.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.example.discovermada.R;

import java.util.Locale;

public class PreferenceUtils {

    public static boolean isDarkMode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("mod_preference", false);
    }

    public static String currentLang(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("lang_preference", "fr");
    }

    public static boolean isNotified(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("notif_preference", false);
    }

    public static void updateAppLanguage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedLanguage = sharedPreferences.getString("lang_preference", "fr");

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
    }

    private static String getCurrentLanguage(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    public static void applyAppTheme(Context context) {
        boolean darkModeEnabled = isDarkMode(context);
        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void showRestoreDefaultConfirmationDialog(Context context , String title , String message , String acceptTitle , String cancelTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(acceptTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restoreDefaultPreferences(context);
            }
        });
        builder.setNegativeButton(cancelTitle, null);
        builder.show();
    }

    private static void restoreDefaultPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d("RESTOR +++", "restoreDefaultPreferences: ");
        editor.putBoolean("notif_preference", true);
        editor.putBoolean("mod_preference", false);
        editor.putString("lang_preference" , "fr");
        editor.apply();
        updateAppLanguage(context);
        applyAppTheme(context);
    }

}
