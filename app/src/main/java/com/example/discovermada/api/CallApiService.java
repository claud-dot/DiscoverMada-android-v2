package com.example.discovermada.api;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface CallApiService<T> {
     void handle(Call<ResponseBody> responseBody, ApiResponseCallback callback);
}
