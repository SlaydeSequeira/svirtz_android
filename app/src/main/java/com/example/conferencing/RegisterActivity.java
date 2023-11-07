package com.example.conferencing;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RegisterActivity extends Activity {
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        registerButton = findViewById(R.id.button);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace with your API endpoint
                String apiUrl = "http://192.168.1.5:3000/api/signup";

                String u = username.getText().toString();
                String p = password.getText().toString();

                if (!TextUtils.isEmpty(u) && !TextUtils.isEmpty(p)) {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("username", u);
                        json.put("password", p);
                        String jsonData = json.toString();

                        new RegisterTask().execute(apiUrl, jsonData);
                    } catch (JSONException e) {
                        // Handle JSON exception
                    }
                } else {
                    // Handle empty username or password
                }
            }
        });
    }

        private class RegisterTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String apiUrl = params[0];
                String jsonData = params[1];
                String result = null;

                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    OutputStream os = connection.getOutputStream();
                    os.write(jsonData.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Scanner scanner = new Scanner(connection.getInputStream());
                        scanner.useDelimiter("\\A");
                        if (scanner.hasNext()) {
                            result = scanner.next();
                        }
                    } else {
                        result = "HTTP Error: " + responseCode;
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        String jsonString = result.trim(); // Trim the result to remove potential white spaces

                        if (jsonString.contains("userId")) {
                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(i);
                            Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + jsonString, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }