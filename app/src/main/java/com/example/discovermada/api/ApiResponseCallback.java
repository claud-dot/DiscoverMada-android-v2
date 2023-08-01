package com.example.discovermada.api;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

public interface ApiResponseCallback {
    void onSuccess(JSONObject data) throws JSONException, JsonProcessingException;
    void onFailure(Throwable t);
}
