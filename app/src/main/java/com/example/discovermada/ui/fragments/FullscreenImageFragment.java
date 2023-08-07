package com.example.discovermada.ui.fragments;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.discovermada.R;
import com.example.discovermada.utils.FireBaseClient;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class FullscreenImageFragment extends Fragment {

    private FirebaseStorage storage;
    private ImageView imageView;
    private ImageView closeIc;
    private ImageView prevButton;
    private ImageView nextButton;
    private List<String> imageUrls;
    private int currentPosition = 0;

    private ProgressBar progressBar;
    public static FullscreenImageFragment newInstance(ArrayList<String> imageUrls, int position) {
        FullscreenImageFragment fragment = new FullscreenImageFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("img_urls",  imageUrls);
        args.putInt("position" , position);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fullscreen_image, container, false);
        storage = FirebaseStorage.getInstance();

        progressBar = rootView.findViewById(R.id.progress_bar);
        imageView = rootView.findViewById(R.id.fullscreen_image);
        closeIc = rootView.findViewById(R.id.close_button);
        prevButton = rootView.findViewById(R.id.prev_button);
        nextButton = rootView.findViewById(R.id.next_button);

        Bundle args = getArguments();
        if (args != null) {
            imageUrls = args.getStringArrayList("img_urls");
            currentPosition = args.getInt("position", 0);

            if (imageUrls != null && !imageUrls.isEmpty()) {
                loadImage(imageUrls.get(currentPosition));
            }
        }

        closeIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition == 0 || currentPosition ==1){
                    currentPosition = imageUrls.size() - 1;
                }else if (currentPosition > 0) {
                    currentPosition--;
                }else{
                    currentPosition = imageUrls.size() - 1;
                }
                loadImage(imageUrls.get(currentPosition));
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition == 0 || currentPosition == 1){
                    currentPosition = 2;
                }else if (currentPosition < imageUrls.size() - 1) {
                    currentPosition++;
                }else {
                    currentPosition =1;
                }
                loadImage(imageUrls.get(currentPosition));
            }
        });

        return rootView;
    }


    private void loadImage(String imageUrl) {
        progressBar.setVisibility(View.VISIBLE);
        FireBaseClient fireBaseClient = new FireBaseClient(FirebaseStorage.getInstance());

        fireBaseClient.setSingleImageView(imageUrl, imageView , progressBar, new FireBaseClient.ImageLoadListener() {
            @Override
            public void onImageLoaded() {
            }

            @Override
            public void onImageFailure(Exception e) {
                Toast.makeText(requireContext() ,imageUrl+" Loade Failed!" , Toast.LENGTH_SHORT ).show();
            }
        });
    }
}