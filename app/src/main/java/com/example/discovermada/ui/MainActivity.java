package com.example.discovermada.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;


import com.example.discovermada.R;
import com.example.discovermada.ui.fragments.List_Spot_Fragment;
import com.example.discovermada.ui.fragments.Search_Fragment;
import com.example.discovermada.utils.PreferenceUtils;
import com.example.discovermada.utils.Utils;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Search_Fragment searchFragment;

    private NotificationManager manager;
    String user_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.redirectSession(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPreference();
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Utils.initActionBar(this,toolbar);
        replaceFragment(new List_Spot_Fragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceUtils.applyAppLanguage(this , user_session);
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
            Utils.showExitConfirmationDialog(this);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1 ) {
            manager.popBackStack();
        } else {
            Utils.showExitConfirmationDialog(this);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Utils.replaceFragment(fragmentManager, R.id.container, fragment);
    }

    private void initPreference(){
        Intent mIntent = getIntent();
        String previousActivity= mIntent.getStringExtra("FROM_ACTIVITY");
        user_session = PreferenceUtils.getSessionToken(this);
        PreferenceUtils.applyAppLanguage(this , user_session);
        PreferenceUtils.applyAppTheme(this , user_session);
        if (previousActivity!=null && previousActivity.equals("login")){
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            PreferenceUtils.notifWelcome(this,user_session, manager );
            mIntent.putExtra("FROM_ACTIVITY", (String) null);
       }
    }
}
