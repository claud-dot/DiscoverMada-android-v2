package com.example.discovermada.api;

import android.util.Log;

import com.example.discovermada.model.TouristSpots;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class JsonConverterImpl<T> implements JsonConverter<T>{

    private final TypeReference<T> typeReference;

    public JsonConverterImpl(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
    }

    @Override
    public T convert(Object jsonObject) throws JsonProcessingException {
        T response = null;
        if (jsonObject instanceof JSONObject || jsonObject instanceof JSONArray) {
            ObjectMapper objectMapper = new ObjectMapper();
            response = objectMapper.readValue(jsonObject.toString(),  this.typeReference);
        }
        return response;
    }
}
