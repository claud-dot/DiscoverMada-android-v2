package com.example.discovermada.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.example.discovermada.utils.PreferenceUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class Search_Fragment extends Fragment {
    private FirebaseStorage storage;
    private RecyclerView recyclerView;

    public static Search_Fragment newInstance(String querySearch) {
        Search_Fragment fragment = new Search_Fragment();
        Bundle args = new Bundle();
        args.putString("search_query",  querySearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PreferenceUtils.applyAppTheme(requireContext());
        View rootView = inflater.inflate(R.layout.fragment_search_, container, false);
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        recyclerView = rootView.findViewById(R.id.recycleViewResultSpot);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String keyword = getArguments().getString("search_query");
        Log.d("KEYWORD ", "onViewCreated: " + keyword);
        searchResultSpot(keyword);
    }

    public void searchResultSpot(String query){
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("searchKeyword", query);
            CallApiServiceImpl<List<TouristSpots>> callApiService = new CallApiServiceImpl<>(new JsonConverterImpl<>(new TypeReference<List<TouristSpots>>() {}));
            ApiService apiService = ApiClient.getApiService();
            Call<ResponseBody> call = apiService.getSpotSearch(requestBody , PreferenceUtils.currentLang(requireContext()));
            callApiService.handle(call, new ApiResponseCallback() {
                @Override
                public void onSuccess(JSONObject data) throws JSONException, JsonProcessingException {
                    JsonConverter<List<TouristSpots>> jsonConverter = new JsonConverterImpl<>((new TypeReference<List<TouristSpots>>() {}));
                    List<TouristSpots> touristSpots = jsonConverter.convert(data.getJSONArray("data"));
                    Log.d("Search result ===< ", "onSuccess: "+data.getJSONArray("data"));
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
                }
                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}