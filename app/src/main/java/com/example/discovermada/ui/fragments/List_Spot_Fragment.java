package com.example.discovermada.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.discovermada.R;
import com.example.discovermada.api.ApiClient;
import com.example.discovermada.api.ApiResponseCallback;
import com.example.discovermada.api.ApiService;
import com.example.discovermada.api.CallApiServiceImpl;
import com.example.discovermada.api.JsonConverter;
import com.example.discovermada.api.JsonConverterImpl;
import com.example.discovermada.model.TouristSpots;
import com.example.discovermada.model.UserPreference;
import com.example.discovermada.ui.MainActivity;
import com.example.discovermada.ui.adapters.ListSpotAdapter;
import com.example.discovermada.utils.FireBaseClient;
import com.example.discovermada.utils.PreferenceUtils;
import com.example.discovermada.utils.Utils;
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
    private List<TouristSpots> touristSpotsList;
    ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list__spot_, container, false);
        super.onCreate(savedInstanceState);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        recyclerView = rootView.findViewById(R.id.recycleViewListSpot);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        storage = FirebaseStorage.getInstance();
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (touristSpotsList == null) {
            getTouristSpotsAndDisplay(1);
        } else {
            displayTouristSpots();
        }
        return rootView;
    }

    private void getTouristSpotsAndDisplay(int page) {
        getTouristSpots(page, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException {
                JsonConverter<List<TouristSpots>> jsonConverter = new JsonConverterImpl<>((new TypeReference<List<TouristSpots>>() {}));
                try {
                    touristSpotsList = jsonConverter.convert(data.getJSONArray("data"));
                    displayTouristSpots();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getTouristSpots(int page, ApiResponseCallback callback) {
        CallApiServiceImpl<List<TouristSpots>> callApiService = new CallApiServiceImpl<>(new JsonConverterImpl<>(new TypeReference<List<TouristSpots>>() {}));
        ApiService apiService = ApiClient.getApiService();
        String id_user = PreferenceUtils.getSessionToken(requireContext());
        Call<ResponseBody> call = apiService.getTouristSpots(page, PreferenceUtils.getSelectedLanguage(requireContext() , id_user));
        callApiService.handle(call, callback);
    }

    private void displayTouristSpots() {
        FireBaseClient baseClient = new FireBaseClient(storage);
        ListSpotAdapter adapterForList = new ListSpotAdapter(touristSpotsList, baseClient, new ListSpotAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TouristSpots spot) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.replaceFragment(Details_Spot_Fragment.newInstance(spot));
                }
            }
        });
        recyclerView.setAdapter(adapterForList);
        progressBar.setVisibility(View.GONE);
    }

}