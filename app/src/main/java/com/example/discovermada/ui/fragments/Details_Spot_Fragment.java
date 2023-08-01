package com.example.discovermada.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.discovermada.R;
import com.example.discovermada.api.ApiClient;
import com.example.discovermada.api.ApiResponseCallback;
import com.example.discovermada.api.ApiService;
import com.example.discovermada.api.CallApiServiceImpl;
import com.example.discovermada.api.JsonConverter;
import com.example.discovermada.api.JsonConverterImpl;
import com.example.discovermada.model.TouristSpots;
import com.example.discovermada.utils.FireBaseClient;
import com.example.discovermada.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Details_Spot_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Details_Spot_Fragment extends Fragment {
    private FirebaseStorage storage;
    private WebView webView;
    private ImageView spotImage;
    private List<ImageView> listSpotImg = new ArrayList<>();

    public static Details_Spot_Fragment newInstance(TouristSpots touristSpot) {
        Details_Spot_Fragment fragment = new Details_Spot_Fragment();
        Bundle args = new Bundle();
        args.putString("spot_id",  touristSpot.get_id());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String idSpot = getArguments().getString("spot_id");
        View rootView = inflater.inflate(R.layout.fragment_details__spot_, container, false);
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        webView = (WebView)rootView.findViewById(R.id.htmlContent);
        spotImage = (ImageView)rootView.findViewById(R.id.spot);

        Toolbar toolbar =  getActivity().findViewById(R.id.toolBar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listSpotImg.add(spotImage);
        Utils.initImagesSpot(rootView , listSpotImg);
        getDetailsSpot(idSpot);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("BACK ", "onOptionsItemSelected: *");
        if (id == android.R.id.home) {
            // Effectuer l'action de retour souhaitée ici
            // Par exemple, retourner à la Fragment List_Spot
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   public void getDetailsSpot(String idSpot){
        CallApiServiceImpl<List<TouristSpots>> callApiService = new CallApiServiceImpl<>(new JsonConverterImpl<>(new TypeReference<List<TouristSpots>>() {}));
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.getDetailsSpot(idSpot , "fr");
        callApiService.handle(call, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException {
                JsonConverter<TouristSpots> jsonConverter = new JsonConverterImpl<>((new TypeReference<TouristSpots>() {}));
                try {
                    TouristSpots spot = jsonConverter.convert(data.getJSONObject("data"));
                    FireBaseClient fireBaseClient = new FireBaseClient(storage);
                    generateView(spot , fireBaseClient);
                }catch (JsonProcessingException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void generateView(TouristSpots spots , FireBaseClient fireBaseClient){
        fireBaseClient.setMediaViews(Arrays.asList(spots.getImages()) , listSpotImg);
        Utils utils = new Utils(requireContext());
        String templateHtml = utils.loadHtmlFromAssets("template.html");
        String fullHtmlContent = templateHtml.replace("<div id=\"pageContent\"></div>", spots.getHtmlContent());
        fullHtmlContent = fullHtmlContent.replace("ID_VIDEO" , spots.getVideos()[0]);
        webView.loadDataWithBaseURL(null,fullHtmlContent, "text/html", "UTF-8", null);
        WebSettings webSettings =  webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

    }
}