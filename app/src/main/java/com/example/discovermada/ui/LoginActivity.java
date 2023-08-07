package com.example.discovermada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.discovermada.R;

import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.discovermada.model.LoginResponse;
import com.example.discovermada.utils.Constant;
import com.example.discovermada.utils.PreferenceUtils;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG= "LoginActivity";
    private static final String API_URL= Constant.URL;

    private EditText editTextEmailOrUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        editTextEmailOrUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        //souligner texte inscription
        TextView tv = (TextView) findViewById(R.id.textViewInscription);
        SpannableString content = new SpannableString(getString(R.string.signup_link_login));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv.setText(content);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUp();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                String emailOrUsername = editTextEmailOrUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString();

                // Validate input fields (you can add more validation here if needed)
                if (emailOrUsername.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter username/email and password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog();
                new LoginTask(emailOrUsername,password).execute();
            }
        });

    }
    public void navigateToSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in..."); // Set the message
        progressDialog.setCancelable(false); // Prevent user from canceling
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("FROM_ACTIVITY" , "login");
        // If you need to pass any data to the main activity, you can add extras to the intent here.
        startActivity(intent);
        // Finish the login activity if you don't want the user to return to it when pressing the back button.
        finish();
    }

    private class LoginTask extends AsyncTask<Void,Void,String>{
        private String emailOrUsername;
        private String password;
        public LoginTask(String emailOrUsername, String password) {
            this.emailOrUsername=emailOrUsername;
            this.password= password;
        }

        @Override
        protected String doInBackground(Void... voids){
            String api_url=API_URL+"user/signin";

            OkHttpClient http= new OkHttpClient();
            MediaType mediaType= MediaType.parse("application/json");
            String requestBody= "{\"usernameOrEmail\":\""+this.emailOrUsername+ "\",\"password\":\"" + this.password + "\"}";
            RequestBody body =  RequestBody.create(mediaType, requestBody);

            Request request = new Request.Builder()
                    .url(api_url)
                    .addHeader("Content-Type","application/json")
                    .post(body)
                    .build();
            try{
                Response response= http.newCall(request).execute();
                if(response.isSuccessful()){
                    return response.body().string();
                }
                else{
                    Log.e("LoginActivity", "Unsuccessful response: " + response);
                    hideProgressDialog();
                    return null;
                }
            }
            catch (Exception e){
                Log.e("LoginActivity", "Exception during API call: " + e.getMessage());
                hideProgressDialog();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Gson gson = new Gson();
                LoginResponse loginResponse = gson.fromJson(result, LoginResponse.class);

                if (loginResponse.getError() == 0) {
                    showProgressDialog();
                    Toast.makeText(LoginActivity.this, "Login success. ", Toast.LENGTH_SHORT).show();
                    LoginResponse.User user = loginResponse.getResponse();
                    PreferenceUtils.setSessionToken(LoginActivity.this , user.get_id());
                    PreferenceUtils.setFirstLogApp(LoginActivity.this , user.get_id(),true );
                    PreferenceUtils.setUserPreference(LoginActivity.this,user);
                    startMainActivity();
                } else {
                    Log.e(TAG, "Login failed. Error message: " + loginResponse.getErrorMessage());
                    hideProgressDialog();
                    Toast.makeText(LoginActivity.this, "Login failed. " + loginResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "API call failed.");
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
