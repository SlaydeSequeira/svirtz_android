package com.example.conferencing;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends Activity {
    private Button loginButton;
    private TextView tokenTextView,t2;

    String u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        loginButton = findViewById(R.id.button);
        tokenTextView = findViewById(R.id.text);
        t2 = findViewById(R.id.text2);
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace with your API endpoint
                String apiUrl = "http://192.168.1.5:3000/api/login";
                u = username.getText().toString();
                String p = password.getText().toString();

                if (!TextUtils.isEmpty(u) && !TextUtils.isEmpty(p)) {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("username", u);
                        json.put("password", p);
                        String jsonData = json.toString();

                        new LoginTask().execute(apiUrl, jsonData);
                    } catch (JSONException e) {
                        // Handle JSON exception
                    }
                } else {
                    // Handle empty username or password
                }
            }
        });


    }

    private class LoginTask extends AsyncTask<String, Void, String> {
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

                    // Parse the JSON
                    JsonParser parser = new JsonParser();
                    JsonObject json = parser.parse(jsonString).getAsJsonObject();

                    // Extract the userId and store it in the 'uid' variable
                    String uid = json.getAsJsonObject("userDetails")
                            .getAsJsonObject("user")
                            .get("userId")
                            .getAsString();

                    // Print the 'uid'
                    System.out.println("User ID (uid): " + uid);
                    if (jsonString.contains("\"token\"")) {
                        int tokenStart = jsonString.indexOf("\"token\"") + 9; // 9 is the length of the key "token"
                        int tokenEnd = jsonString.indexOf("\"", tokenStart);

                        if (tokenEnd > tokenStart) {
                            String token = jsonString.substring(tokenStart, tokenEnd);
                            Toast.makeText(MainActivity.this, "Login successful. Token: " + token, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, token);
                            Intent i = new Intent(MainActivity.this, MainActivity3.class);
                            i.putExtra("myStringData", uid);
                            i.putExtra("token",token);
                            i.putExtra("username",u);
                            startActivity(i);
                        } else {
                            Toast.makeText(MainActivity.this, "Token not found in the response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Token not found in the response", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        }

    }
}

