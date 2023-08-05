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
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.example.discovermada.R;
import com.example.discovermada.api.ApiClient;
import com.example.discovermada.api.ApiResponseCallback;
import com.example.discovermada.api.ApiService;
import com.example.discovermada.api.CallApiServiceImpl;
import com.example.discovermada.api.JsonConverter;
import com.example.discovermada.api.JsonConverterImpl;
import com.example.discovermada.model.TouristSpots;
import com.example.discovermada.model.UserPreference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class PreferenceUtils {

    public static boolean isNotified(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("notif_preference", true);
    }

    public static String currentLang(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("lang_preference", "fr");
    }

    private static boolean isDarkModeEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("mod_preference", true);
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
        editor.putBoolean("notif_preference", true);
        editor.putBoolean("mod_preference", false);
        editor.putString("lang_preference" , "fr");
        editor.apply();

        updateAppLanguageAndTheme(context);
//        resetAppSetting(context);
    }

    private static void updateAppPreference(JSONObject preference, ApiResponseCallback callback) {
        CallApiServiceImpl<Object> callApiService = new CallApiServiceImpl<>(new JsonConverterImpl<>(new TypeReference<Object>() {}));
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.changeSettingUser(preference, "64ce0d3a9e21252bfa4af2ab");
        callApiService.handle(call, callback);
    }

    private static void resetAppPreference(ApiResponseCallback callback){
        CallApiServiceImpl<Object> callApiService = new CallApiServiceImpl<>(new JsonConverterImpl<>(new TypeReference<Object>() {}));
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.resetSettingUser("64ce0d3a9e21252bfa4af2ab");
        callApiService.handle(call, callback);
    }

    public static void getPreferenceUser(ApiResponseCallback callback){
        CallApiServiceImpl<UserPreference> callApiService = new CallApiServiceImpl<>(new JsonConverterImpl<>(new TypeReference<UserPreference>() {}));
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.resetSettingUser("64ce0d3a9e21252bfa4af2ab");
        callApiService.handle(call, callback);
    }

    public static void setAppPreference(Context context,String key){
        try {
            String preference = null;
            String value = null;
            if(key.contains("mod")){
                preference = Constant.KEY_DARK_MODE_PREFERENCE;
                value = isDarkModeEnabled(context) ? "true" : "false";
            }else if (key.contains("lang")){
                preference = Constant.KEY_LANG_PREFERENCE;
                value = PreferenceUtils.currentLang(context);
            } else if (key.contains("notif")) {
                preference = Constant.KEY_NOFIED_PREFERENCE;
                value = isNotified(context) ? "true" : "false";
            }
            JSONObject jsonPreference =  new JSONObject();
            jsonPreference.put("item" , preference);
            jsonPreference.put("value" , value);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("preference" , jsonPreference);
            updateAppPreference(jsonObject , new ApiResponseCallback() {
                @Override
                public void onSuccess(JSONObject data) throws JSONException, JsonProcessingException {
                    Toast.makeText(context , data.getString("message") , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });
        }catch (Exception e){

        }
    }

    public static  void resetAppSetting(Context context){
        resetAppPreference(new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException, JsonProcessingException {
                Toast.makeText(context , data.getString("message") , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static  void getAppSetting(Context context){
        PreferenceUtils.getPreferenceUser(new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException, JsonProcessingException {
                JsonConverter<UserPreference> jsonConverter = new JsonConverterImpl<>((new TypeReference<UserPreference>() {}));
                try {
                    UserPreference userPreference = jsonConverter.convert(data.getJSONObject("data"));
                    PreferenceUtils.initPreferenceUser(userPreference , context);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    public static void initPreferenceUser(UserPreference userPreference, Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.contains("notif_preference")) {
            editor.putBoolean("notif_preference", userPreference.getNotificationEnabled());
        }

        if (!sharedPreferences.contains("mod_preference")) {
            editor.putBoolean("mod_preference", userPreference.getDarkMode());
        }

        if (!sharedPreferences.contains("lang_preference")) {
            editor.putString("lang_preference", userPreference.getLanguage());
        }
    }

    public static boolean arePreferencesInitialized(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.contains("notif_preference")
                && sharedPreferences.contains("mod_preference")
                && sharedPreferences.contains("lang_preference");
    }
}
