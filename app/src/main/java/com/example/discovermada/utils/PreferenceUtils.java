package com.example.discovermada.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.discovermada.api.ApiClient;
import com.example.discovermada.api.ApiResponseCallback;
import com.example.discovermada.api.ApiService;
import com.example.discovermada.api.CallApiServiceImpl;
import com.example.discovermada.api.JsonConverterImpl;
import com.example.discovermada.model.LoginResponse;
import com.example.discovermada.model.UserPreference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class PreferenceUtils {

    private static  String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static  String PREF_SELECTED_LANGUAGE = "selected_language";
    private static  String PREF_DARK_MODE_ENABLED = "dark_mode_enabled";

    private static String PREF_USER = "user_id";
    private static String PREF_FIRST_LOG = "first_log";

    public static boolean isFirstLogApp(Context context, String id_user){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+id_user, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_FIRST_LOG+"_"+id_user, false);
    }

    public static void setFirstLogApp(Context context, String id_user , boolean enabled){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+id_user, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_FIRST_LOG+"_"+id_user, enabled);
        editor.apply();
    }

    public static boolean areNotificationsEnabled(Context context, String id_user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+id_user, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_NOTIFICATIONS_ENABLED+"_"+id_user, true);
    }

    public static void setNotificationsEnabled(Context context, boolean enabled , String id_user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+id_user, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_NOTIFICATIONS_ENABLED+"_"+id_user, enabled);
        editor.apply();
    }

    public static String getSelectedLanguage(Context context, String id_user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+id_user, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_SELECTED_LANGUAGE+"_"+id_user, "fr");
    }

    public static void setSelectedLanguage(Context context, String language, String id_user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+id_user, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SELECTED_LANGUAGE+"_"+id_user, language);
        editor.apply();
    }

    public static boolean isDarkModeEnabled(Context context, String id_user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+id_user, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_DARK_MODE_ENABLED+"_"+id_user, false);
    }

    public static void setDarkModeEnabled(Context context, boolean enabled, String id_user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+id_user, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_DARK_MODE_ENABLED+"_"+id_user, enabled);
        editor.apply();
    }

    public static void setUserPreference(Context context, LoginResponse.User user){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+user.get_id(), Context.MODE_PRIVATE);
        String preferenceKey = PREF_NOTIFICATIONS_ENABLED+"_"+user.get_id();
        boolean isPreferenceStored = sharedPreferences.contains(preferenceKey);
        if(!isPreferenceStored){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_NOTIFICATIONS_ENABLED+"_"+user.get_id(), true);
            editor.putString(PREF_SELECTED_LANGUAGE+"_"+user.get_id(), "fr");
            editor.putBoolean(PREF_DARK_MODE_ENABLED+"_"+user.get_id(), false);
            editor.apply();
        }else{
            applyAppLanguage(context , user.get_id());
            applyAppTheme(context , user.get_id());
        }
        setAppDisplayPreferences(context , user.get_id());
    }

    public static void setAppDisplayPreferences(Context context, String user_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+user_id, Context.MODE_PRIVATE);

        boolean notificationsEnabled = sharedPreferences.getBoolean(PREF_NOTIFICATIONS_ENABLED+"_"+user_id, true);
        String selectedLanguage = sharedPreferences.getString(PREF_SELECTED_LANGUAGE+"_"+user_id, "fr");
        boolean darkModeEnabled = sharedPreferences.getBoolean(PREF_DARK_MODE_ENABLED+"_"+user_id, false);

        SharedPreferences displayPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = displayPreferences.edit();
        editor.putBoolean("notifications_enabled", notificationsEnabled);
        editor.putString("selected_language", selectedLanguage);
        editor.putBoolean("dark_mode_enabled", darkModeEnabled);
        editor.apply();
    }

    public static void setSettingApp(){

    }

    public static void clearSessionToken(Context context) {
        setSessionToken(context, null);
    }

    public static String getSessionToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("session_token", null);
    }

    public static void setSessionToken(Context context, String sessionToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("session_token", sessionToken);
        editor.apply();
    }

    public static void notifWelcome(Context context , String id_user , NotificationManager manager){
        NotificationUtils utils = new NotificationUtils();
        boolean isFirstLog = isFirstLogApp(context , id_user);

        Log.d("NOTIFICATION ====>", "notifWelcome: "+isFirstLog);
        if (isFirstLog && areNotificationsEnabled(context , id_user)) {
            utils.createNotificationChannel(manager , context);
            utils.showNotification(context);

            setFirstLogApp(context , id_user , false);
        }
    }

    public static void applyAppLanguage(Context context , String user_id) {
        String selectedLanguage =  getSelectedLanguage(context, user_id);

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

    public static void applyAppTheme(Context context , String user_id) {
        boolean isDarkMode = isDarkModeEnabled(context , user_id);
        int mode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);
    }


    public static void restoreDefaultPreferences(Context context) {
        String user_id = getSessionToken(context);
        Log.d("TAG", "restoreDefaultPreferences: "+user_id);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs_"+user_id, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(PREF_NOTIFICATIONS_ENABLED+"_"+user_id, true);
        editor.putString(PREF_SELECTED_LANGUAGE+"_"+user_id, "fr");
        editor.putBoolean(PREF_DARK_MODE_ENABLED+"_"+user_id, false);
        editor.apply();

        applyAppLanguage(context , user_id);
        applyAppTheme(context , user_id);
        setAppDisplayPreferences(context , user_id);
    }

}
