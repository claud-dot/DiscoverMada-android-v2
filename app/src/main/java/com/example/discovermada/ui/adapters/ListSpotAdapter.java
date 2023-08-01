package com.example.discovermada.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discovermada.R;
import com.example.discovermada.model.TouristSpots;
import com.example.discovermada.utils.FireBaseClient;

import java.util.Arrays;
import java.util.List;

public class ListSpotAdapter extends RecyclerView.Adapter<ListSpotAdapter.ViewHolder> {
    private List<TouristSpots> spotsList; // Liste des objets TouristSpots
    private FireBaseClient fireBaseClient;
    private OnItemClickListener onItemListener;

    public ListSpotAdapter(List<TouristSpots> spotsList , FireBaseClient fireBaseClient , OnItemClickListener listener) {
        this.spotsList = spotsList;
        this.fireBaseClient = fireBaseClient;
        this.onItemListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_tourist_spot, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TouristSpots spot =  spotsList.get(position);
        holder.textViewTitle.setText(spot.getName());
        holder.textViewDescription.setText(spot.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemListener != null) {
                    onItemListener.onItemClick(spot);
                    Toast.makeText(view.getContext(),"click on item: "+spot.getDescription(),Toast.LENGTH_LONG).show();
                }
            }
        });
        fireBaseClient.setMediaViews(Arrays.asList(spot.getImages()[0]), Arrays.asList(holder.imageView));
    }

    @Override
    public int getItemCount() {
        return spotsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(TouristSpots spot);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewTitle;

        public  TextView textViewDescription;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}