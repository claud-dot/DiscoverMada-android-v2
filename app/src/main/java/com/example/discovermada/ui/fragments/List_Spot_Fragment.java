package com.example.discovermada.ui.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.discovermada.R;
import com.example.discovermada.api.ApiClient;
import com.example.discovermada.api.ApiResponseCallback;
import com.example.discovermada.api.ApiService;
import com.example.discovermada.api.CallApiServiceImpl;
import com.example.discovermada.api.JsonConverter;
import com.example.discovermada.api.JsonConverterImpl;
import com.example.discovermada.model.TouristSpots;
import com.example.discovermada.ui.MainActivity;
import com.example.discovermada.ui.adapters.ListSpotAdapter;
import com.example.discovermada.utils.FireBaseClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class List_Spot_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseStorage storage;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list__spot_, container, false);
        super.onCreate(savedInstanceState);
        recyclerView = rootView.findViewById(R.id.recycleViewListSpot);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        storage = FirebaseStorage.getInstance();
        getTouristSpots(1);
        return rootView;
    }

    private void getTouristSpots(int page) {
        CallApiServiceImpl<List<TouristSpots>> callApiService = new CallApiServiceImpl<>(new JsonConverterImpl<>(new TypeReference<List<TouristSpots>>() {}));
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.getTouristSpots(page , "fr");
        callApiService.handle(call, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException {
                JsonConverter<List<TouristSpots>> jsonConverter = new JsonConverterImpl<>((new TypeReference<List<TouristSpots>>() {}));
                try {
                    List<TouristSpots> touristSpots = jsonConverter.convert(data.getJSONArray("data"));
                    FireBaseClient baseClient = new FireBaseClient(storage);
                    ListSpotAdapter adapterForList = new ListSpotAdapter(touristSpots , baseClient , new ListSpotAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(TouristSpots spot) {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            if (mainActivity != null) {
                                mainActivity.replaceFragment(Details_Spot_Fragment.newInstance(spot));
                            }
                        }
                    });
                    recyclerView.setAdapter(adapterForList);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.hideBackButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.showBackButton();
    }
}