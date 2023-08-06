package com.example.discovermada.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.discovermada.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.discovermada.model.SignupResponse;
import com.example.discovermada.utils.Constant;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final String API_URL = Constant.URL+"user/signup";

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignup = findViewById(R.id.buttonSignup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString();

                // Validate email and username
                if (email.isEmpty() || username.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter email and username.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate username format (no special characters)
                if (!isValidUsername(username)) {
                    Toast.makeText(SignupActivity.this, "Invalid username format.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate email format
                if (!isValidEmail(email)) {
                    Toast.makeText(SignupActivity.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate input fields
                if (password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please enter password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgressDialog();
                new SignupTask().execute(username, email, password);
            }
        });
    }

    private boolean isValidUsername(String username) {
        String usernamePattern = "^[a-zA-Z0-9]*$";
        return username.matches(usernamePattern);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailPattern);
    }
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setMessage("Logging in..."); // Set the message
        progressDialog.setCancelable(false); // Prevent user from canceling
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private class SignupTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            String username = params[0];
            String email = params[1];
            String password = params[2];
            String requestBody = "{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
            RequestBody body = RequestBody.create(mediaType, requestBody);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", "Cookie_2=value")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    Log.e(TAG, "Unsuccessful response: " + response);
                    hideProgressDialog();
                    return null;
                }
            } catch (IOException e) {
                Log.e(TAG, "Exception during API call: " + e.getMessage());
                hideProgressDialog();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                // Process the API response here
                Log.d(TAG, "API Response: " + result);
                Gson gson = new Gson();
                SignupResponse signupResponse = gson.fromJson(result, SignupResponse.class);

                // Check if there was an error
                if (signupResponse.getError() == 0) {
                    // Signup success
                    SignupResponse.User user = signupResponse.getResponse();
                    hideProgressDialog();
                    // Handle the user data, for example, save user details or navigate to the main app screen.
                    Toast.makeText(SignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Signup successful. User ID: " + user.get_id());
                } else {
                    // Signup failed
                    Log.e(TAG, "Signup failed. Error message: " + signupResponse.getErrorMessage());
                    hideProgressDialog();
                    // Show an error message to the user or handle signup failure scenarios.
                    Toast.makeText(SignupActivity.this, "Signup failed. " + signupResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case where the API call was not successful
                Log.e(TAG, "API call failed.");
                hideProgressDialog();
                Toast.makeText(SignupActivity.this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
