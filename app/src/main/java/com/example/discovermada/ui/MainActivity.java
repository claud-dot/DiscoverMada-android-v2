package com.example.discovermada.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.example.discovermada.R;
import com.example.discovermada.ui.fragments.Details_Spot_Fragment;
import com.example.discovermada.ui.fragments.List_Spot_Fragment;
import com.example.discovermada.ui.fragments.Search_Fragment;
import com.example.discovermada.utils.PreferenceUtils;
import com.example.discovermada.utils.Utils;

import java.util.Locale;


public class MainActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private Search_Fragment searchFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceUtils.applyAppTheme(this);
        setContentView(R.layout.activity_main);
        PreferenceUtils.updateAppLanguage(this);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        replaceFragment(new List_Spot_Fragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchViewItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchFragment == null) {
                    searchFragment = Search_Fragment.newInstance(query);
                    replaceFragment(searchFragment);
                } else {
                    searchFragment.searchResultSpot(query);
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_search) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }else if(id == R.id.item_settings){
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void replaceFragment(Fragment fragment) {
        currentFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Utils.replaceFragment(fragmentManager, R.id.container, fragment);
    }

    public void hideBackButton() {
        ActionBar actionBar = getSupportActionBar();
        Utils.hideBackButton(actionBar);
    }

    public void showBackButton() {
        ActionBar actionBar = getSupportActionBar();
        Utils.showBackButton(actionBar);
    }


}