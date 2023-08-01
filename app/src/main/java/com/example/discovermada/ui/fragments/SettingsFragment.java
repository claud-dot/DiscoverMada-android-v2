package com.example.discovermada.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.discovermada.R;
import com.example.discovermada.utils.PreferenceUtils;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SwitchPreference darkModePreference;
    private SwitchPreference notifPreference;
    private ListPreference languagePreference;
    private boolean currentDarkMode;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        initPreference();
    }

    private void initPreference(){
        darkModePreference = findPreference("mod_preference");
        languagePreference = findPreference("lang_preference");
        notifPreference = findPreference("notif_preference");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentDarkMode = sharedPreferences.getBoolean("mod_preference", false);

        onListenClickRestorPreference();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "mod_preference");
        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "lang_preference");
        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "notif_preference");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("mod_preference")) {
            boolean darkModeEnabled = sharedPreferences.getBoolean("mod_preference", false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("selected_language", darkModeEnabled);
            editor.apply();
            if (darkModeEnabled != currentDarkMode) {
                currentDarkMode = darkModeEnabled;
                PreferenceUtils.applyAppTheme(requireContext());
                getActivity().recreate();
            }
        } else if (key.equals("lang_preference")) {
            String selectedLanguage = sharedPreferences.getString("lang_preference", "fr");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selected_language", selectedLanguage);
            editor.apply();
            PreferenceUtils.updateAppLanguage(requireContext());
        } else if (key.equals("notif_preference")) {
            boolean notificationsEnabled = sharedPreferences.getBoolean("notif_preference", false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("selected_language", notificationsEnabled);
            editor.apply();
        }
    }

    private void onListenClickRestorPreference(){
        Preference restoreDefaultPreference = findPreference("restore_default_preference");
        restoreDefaultPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                 String title = requireContext().getString(R.string.restor_title_show);
                 String message = requireContext().getString(R.string.restor_message_show);
                 String accept = requireContext().getString(R.string.restor_accept_show);
                 String refuse = requireContext().getString(R.string.restor_refuse_show);
                 PreferenceUtils.showRestoreDefaultConfirmationDialog(requireContext(),title , message , accept , refuse);
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}