package com.example.discovermada.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;


import com.afollestad.materialdialogs.MaterialDialog;
import com.example.discovermada.R;
import com.example.discovermada.ui.fragments.Details_Spot_Fragment;
import com.example.discovermada.ui.fragments.List_Spot_Fragment;
import com.example.discovermada.ui.fragments.Search_Fragment;
import com.example.discovermada.utils.NotificationUtils;
import com.example.discovermada.utils.PreferenceUtils;
import com.example.discovermada.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Search_Fragment searchFragment;

    private NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Utils.initActionBar(this,toolbar);
        PreferenceUtils.notifWelcome(this, manager);
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
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
                if (currentFragment instanceof Search_Fragment) {
                    searchFragment.searchResultSpot(query);
                } else {
                    searchFragment = Search_Fragment.newInstance(query);
                    replaceFragment(searchFragment);
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
        } else if (id == R.id.item_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }else if(id == R.id.item_exit){
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vous déconnecter")
                    .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);

                            finish();
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmationDialog() {


    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1 ) {
            manager.popBackStack();
        } else {
            Utils.showExitConfirmationDialog(this);
        }
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Utils.replaceFragment(fragmentManager, R.id.container, fragment);
    }



}