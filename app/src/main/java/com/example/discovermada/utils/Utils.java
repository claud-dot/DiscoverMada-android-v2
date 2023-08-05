package com.example.discovermada.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.discovermada.R;
import com.example.discovermada.ui.MainActivity;
import com.example.discovermada.ui.fragments.List_Spot_Fragment;
import com.example.discovermada.ui.fragments.Search_Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public String loadHtmlFromAssets(String filename) {
        try {
            InputStream inputStream = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void replaceFragment(FragmentManager fragmentManager, int containerId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public static void initImagesSpot(View view,List<ImageView> imageViews){
        imageViews.add((ImageView) view.findViewById(R.id.spotimage1));
        imageViews.add((ImageView) view.findViewById(R.id.spotimage2));
        imageViews.add((ImageView) view.findViewById(R.id.spotimage3));
        imageViews.add((ImageView) view.findViewById(R.id.spotimage4));
    }

    public static void showRestoreDefaultConfirmationDialog(Context context ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.restor_title_show));
        builder.setMessage(context.getString(R.string.restor_message_show));
        builder.setPositiveButton(context.getString(R.string.restor_accept_show), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceUtils.restoreDefaultPreferences(context);
            }
        });
        builder.setNegativeButton(context.getString(R.string.restor_refuse_show), null);
        builder.show();
    }

    public static void showExitConfirmationDialog(final  Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.exit_app_message));
        builder.setPositiveButton(activity.getString(R.string.restor_accept_show), (dialog, which) -> {
           activity.moveTaskToBack(true);
           activity.finish();
        });
        builder.setNegativeButton(activity.getString(R.string.restor_refuse_show), (dialog, which) -> {
        });
        builder.show();
    }

    public static  void initActionBar(MainActivity activity , Toolbar toolbar){
        ActionBar actionBar = activity.getSupportActionBar();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.app_name);

            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.replaceFragment(new List_Spot_Fragment());
                }
            });
        }
    }
}
