package com.example.discovermada.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.discovermada.R;
import com.example.discovermada.utils.PreferenceUtils;
import com.example.discovermada.utils.Utils;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String user_session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_session = PreferenceUtils.getSessionToken(requireContext());
        onListenClickRestorPreference();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (user_session != null) {
            PreferenceUtils.applyAppLanguage(requireContext(), user_session);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("notifications_enabled")) {
            boolean notificationsEnabled = sharedPreferences.getBoolean(key, true);
            PreferenceUtils.setNotificationsEnabled(requireContext(), notificationsEnabled , user_session);
            PreferenceUtils.setAppDisplayPreferences(requireContext() , user_session);
        } else if (key.equals("selected_language")) {
            String selectedLanguage = sharedPreferences.getString(key, "fr");
            PreferenceUtils.setSelectedLanguage(requireContext(), selectedLanguage , user_session);
            PreferenceUtils.applyAppLanguage(requireContext(), user_session);
            PreferenceUtils.setAppDisplayPreferences(requireContext() , user_session);
        } else if (key.equals("dark_mode_enabled")) {
            boolean darkModeEnabled = sharedPreferences.getBoolean(key, false);
            PreferenceUtils.setDarkModeEnabled(requireContext(), darkModeEnabled , user_session);
            PreferenceUtils.applyAppTheme(requireContext() , user_session);
            PreferenceUtils.setAppDisplayPreferences(requireContext() , user_session);
        }
    }

    private void onListenClickRestorPreference(){
        Preference restoreDefaultPreference = findPreference("restore_default_preference");
        restoreDefaultPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                 Utils.showRestoreDefaultConfirmationDialog(requireContext());
                return true;
            }
        });
    }
}