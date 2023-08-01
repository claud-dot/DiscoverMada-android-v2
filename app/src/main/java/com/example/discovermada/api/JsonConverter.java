package com.example.discovermada.api;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONArray;
import org.json.JSONObject;

public interface JsonConverter<T> {
    T convert(Object jsonObject) throws JsonProcessingException;
}
