package com.example.discovermada.network;

import android.util.Log;

import com.example.discovermada.api.ApiClient;
import com.example.discovermada.api.ApiService;
import com.example.discovermada.model.TouristSpots;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TouristSpotsManager {

    public interface OnTouristSpotsListener {
        void onSuccess(Object touristSpots);
        void onFailure(Throwable t);
    }

    public static void getTouristSpots(int page, OnTouristSpotsListener listener) {
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.getTouristSpots(page , "fr");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    String responseData = null;
                    try {
                        responseData = response.body().string();
                        JSONObject touristSpots = new JSONObject(responseData);
                        List<Object> objects= new ArrayList<>();
                        JSONArray data =  touristSpots.getJSONArray("data");


                        ObjectMapper objectMapper = new ObjectMapper();
                        List<TouristSpots> touristSpots1 = objectMapper.readValue(data.toString(), new TypeReference<List<TouristSpots>>() {});
                        listener.onSuccess(objects);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    listener.onFailure(new Exception("Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }
}
