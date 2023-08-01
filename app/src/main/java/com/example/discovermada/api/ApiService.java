package com.example.discovermada.api;

import com.example.discovermada.model.TouristSpots;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("touristSpots/{page}/{lang}")
    Call<ResponseBody> getTouristSpots(@Path("page") int page , @Path("lang") String lang);

    @GET("touristSpots/spot/{idSpot}/{lang}")
    Call<ResponseBody> getDetailsSpot(@Path("idSpot") String idSpot , @Path("lang") String lang);

    @POST("touristSpots/search/{lang}")
    Call<ResponseBody> getSpotSearch(@Body JSONObject object, @Path("lang") String lang);
}
