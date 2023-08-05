package com.example.discovermada.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.discovermada.R;
import com.example.discovermada.api.ApiClient;
import com.example.discovermada.api.ApiResponseCallback;
import com.example.discovermada.api.ApiService;
import com.example.discovermada.api.CallApiServiceImpl;
import com.example.discovermada.api.JsonConverter;
import com.example.discovermada.api.JsonConverterImpl;
import com.example.discovermada.model.TouristSpots;
import com.example.discovermada.ui.MainActivity;
import com.example.discovermada.utils.FireBaseClient;
import com.example.discovermada.utils.PreferenceUtils;
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
    private TouristSpots spotDetails;
    private ProgressBar progressBar;
    private TextView photosTxt;

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
        progressBar = rootView.findViewById(R.id.progressBar);
        photosTxt = rootView.findViewById(R.id.txt_photo);

        storage = FirebaseStorage.getInstance();
        webView = (WebView)rootView.findViewById(R.id.htmlContent);
        spotImage = (ImageView)rootView.findViewById(R.id.spot);

        Toolbar toolbar =  getActivity().findViewById(R.id.toolBar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listSpotImg.add(spotImage);
        Utils.initImagesSpot(rootView , listSpotImg);

        progressBar.setVisibility(View.VISIBLE);
        if(spotDetails==null){
            getDetailsSpot(idSpot);
        }else {
            generateView(spotDetails, new FireBaseClient(storage));
        }
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("BACK ", "onOptionsItemSelected: *");
        if (id == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   public void getDetailsSpot(String idSpot){
        CallApiServiceImpl<List<TouristSpots>> callApiService = new CallApiServiceImpl<>(new JsonConverterImpl<>(new TypeReference<List<TouristSpots>>() {}));
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.getDetailsSpot(idSpot , PreferenceUtils.currentLang(requireContext()));
        callApiService.handle(call, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException {
                JsonConverter<TouristSpots> jsonConverter = new JsonConverterImpl<>((new TypeReference<TouristSpots>() {}));
                try {
                    spotDetails = jsonConverter.convert(data.getJSONObject("data"));
                    FireBaseClient fireBaseClient = new FireBaseClient(storage);
                    generateView(spotDetails , fireBaseClient);
                }catch (JsonProcessingException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                progressBar.setVisibility(View.GONE);
                awaitTextVewShow();
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void generateView(TouristSpots spots , FireBaseClient fireBaseClient){
        List<String> urls = new ArrayList<>(Arrays.asList(spots.getImages()));
        urls.add(1,spots.getImages()[0]);
        fireBaseClient.setMediaViews( urls, listSpotImg);
        Utils utils = new Utils(requireContext());

        int backgroundColor = ContextCompat.getColor(requireContext(), R.color.all_background_color);
        int textColor = ContextCompat.getColor(requireContext(), R.color.text_color_primary);
        String templateHtml = utils.loadHtmlFromAssets("template.html");
        String fullHtmlContent = templateHtml.replace("<div id=\"pageContent\"></div>", spots.getHtmlContent());

        fullHtmlContent = fullHtmlContent.replace("BACKGROUND_COLOR", String.format("#%06X", (0xFFFFFF & backgroundColor)));
        fullHtmlContent = fullHtmlContent.replace("TEXT_COLOR", String.format("#%06X", (0xFFFFFF & textColor)));
        fullHtmlContent = fullHtmlContent.replace("ID_VIDEO" , spots.getVideos()[0]);
        webView.loadDataWithBaseURL(null,fullHtmlContent, "text/html", "UTF-8", null);
        WebSettings webSettings =  webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        blank_LikWebView(webView);
        progressBar.setVisibility(View.GONE);
    }

    private void blank_LikWebView(WebView webView){
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
        });
    }

    private void awaitTextVewShow(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                photosTxt.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }


}