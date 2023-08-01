package com.example.discovermada.utils;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.discovermada.api.MediaLoadListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class FireBaseClient {
    FirebaseStorage storage;

    public FireBaseClient(FirebaseStorage storage ) {
        this.storage = storage;
    }

    public void setMediaViews(List<String> urls, List<ImageView> views) {
        if (urls.size() != views.size()) {
            throw new IllegalArgumentException("Le nombre d'URLs doit être égal au nombre de vues.");
        }

        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            ImageView view = views.get(i);
            StorageReference storageRef = storage.getReference().child(url);
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageURL = uri.toString();
                    Log.d("FIRE BASE ====>", "onSuccess: " + uri);
                    Picasso.get().load(imageURL).into(view);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("FIRE BASE ====>", "onFailure: " + e.getMessage());
                }
            });
        }
    }
}
