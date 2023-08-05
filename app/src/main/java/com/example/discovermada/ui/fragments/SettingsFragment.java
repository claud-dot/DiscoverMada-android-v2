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
import com.example.discovermada.utils.Utils;

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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentDarkMode = sharedPreferences.getBoolean("mod_preference", false);

        onListenClickRestorPreference();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "mod_preference");
        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "lang_preference");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("mod_preference") || key.equals("lang_preference")) {
            PreferenceUtils.updateAppLanguageAndTheme(requireContext());
        } else if (key.equals("notif_preference")) {
            PreferenceUtils.updateNotifPreference(sharedPreferences , requireContext());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}