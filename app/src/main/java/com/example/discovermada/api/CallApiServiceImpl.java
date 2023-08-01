package com.example.discovermada.api;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallApiServiceImpl<T> implements CallApiService<T>{
    private final JsonConverter<T> jsonConverter;
//    private T data;

    public CallApiServiceImpl(JsonConverter<T> jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void handle(Call<ResponseBody> responseBody, ApiResponseCallback callback) {
        responseBody.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("test ==== ", jsonObject.toString());
//                        T data = jsonConverter.convert(jsonObject);
                        callback.onSuccess(jsonObject);
                    } catch (JSONException | IOException e) {
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}
