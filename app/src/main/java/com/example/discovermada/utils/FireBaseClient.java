package com.example.discovermada.utils;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FireBaseClient {
    FirebaseStorage storage;

    public FireBaseClient(FirebaseStorage storage ) {
        this.storage = storage;
    }

    public void setSingleImageViewWithGlide(String url, ImageView view) {
        StorageReference storageRef = storage.getReference().child(url);

        Glide.with(view.getContext())
                .load(storageRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(view);
    }

    public void setSingleImageView(String url, ImageView view , ProgressBar progressBar, ImageLoadListener imageLoadListener) {
        StorageReference storageRef = storage.getReference().child(url);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Picasso.get().load(imageURL).into(view);
                if (imageLoadListener != null) {
                    imageLoadListener.onImageLoaded();
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FIRE BASE FAIL ====>", "onFailure: " + e.getMessage());
                if (imageLoadListener != null) {
                    imageLoadListener.onImageFailure(e);
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setMediaViews(List<String> urls, List<ImageView> views ) {
        if (urls.size() != views.size()) {
            throw new IllegalArgumentException("Le nombre d'URLs doit être égal au nombre de vues.");
        }

        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            ImageView view = views.get(i);

            setSingleImageView(url, view , null, null);
        }
    }

    public interface ImageLoadListener {
        void onImageLoaded();
        void onImageFailure(Exception e);
    }
}
