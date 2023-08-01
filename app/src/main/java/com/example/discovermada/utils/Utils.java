package com.example.discovermada.utils;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.discovermada.R;

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

    public static List<ImageView> getAllImageViews(Context context,int numberOfImageViews) {
        List<ImageView> imageViews = new ArrayList<>();

        for (int i = 0; i < numberOfImageViews; i++) {
            // Créez une nouvelle instance d'ImageView
            ImageView imageView = new ImageView(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                imageView.setId(View.generateViewId());
            }

            imageViews.add(imageView);
        }

        return imageViews;
    }

    public static void initImagesSpot(View view,List<ImageView> imageViews){
        imageViews.add((ImageView) view.findViewById(R.id.spotimage1));
        imageViews.add((ImageView) view.findViewById(R.id.spotimage2));
        imageViews.add((ImageView) view.findViewById(R.id.spotimage3));
    }
}
