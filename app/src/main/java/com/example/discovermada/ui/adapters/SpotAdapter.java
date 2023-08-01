package com.example.discovermada.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discovermada.R;
import com.example.discovermada.model.TouristSpots;
import com.example.discovermada.utils.FireBaseClient;
import com.example.discovermada.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {

    private TouristSpots touristSpots;
    private FireBaseClient fireBaseClient;
    private OnItemClickListener onItemListener;
    public SpotAdapter(TouristSpots spot, FireBaseClient fireBaseClient, OnItemClickListener onItemListener) {
        this.touristSpots = spot;
        this.fireBaseClient = fireBaseClient;
        this.onItemListener = onItemListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public List<ImageView> imageViews = new ArrayList<>();
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViews.add((ImageView) itemView.findViewById(R.id.spotimage1));
            imageViews.add((ImageView) itemView.findViewById(R.id.spotimage2));
            imageViews.add((ImageView) itemView.findViewById(R.id.spotimage3));
            imageViews.add((ImageView) itemView.findViewById(R.id.spotimage4));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_details_spot, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<String> urls = new ArrayList<>(Arrays.asList(touristSpots.getImages()));
        Log.d("IMAGES ===> ", "onBindViewHolder: "+holder.imageViews);
//        urls.remove(0);
        fireBaseClient.setMediaViews(urls, holder.imageViews);
    }

    @Override
    public int getItemCount() {
        return touristSpots != null ? 1 : 0;
    }

    public interface OnItemClickListener {
        void onItemClick(TouristSpots spot);
    }
}

