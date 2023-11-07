package com.example.conferencing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conferencing.adapter.UserAdapter2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CreateGroupActivity extends AppCompatActivity {
    private final String baseUrl = "http://192.168.1.5:3000/api/";
    String receivedData;

    private RecyclerView recyclerView;
    private UserAdapter2 userAdapter;
    // Create a list to store the checked users
    List<String> checkedUserIds = new ArrayList<>();
    private String groupId; // Store the groupId after group creation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("myStringData")) {
            receivedData = intent.getStringExtra("myStringData");
        }

        OkHttpClient client = new OkHttpClient();
        MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");

        // Find the EditText and Buttons in your layout
        EditText groupNameEditText = findViewById(R.id.groupNameEditText);
        Button createGroupButton = findViewById(R.id.createGroupButton);
        Button addUserButton = findViewById(R.id.addUserButton); // New button for adding users

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameEditText.getText().toString();

                // Create a JSON request body with the group name and user ID
                String json = "{\"groupname\":\"" + groupName + "\",\"userId\":\"" + receivedData + "\"}";
                RequestBody requestBody = RequestBody.create(jsonMediaType, json);

                // Create the POST request
                Request request = new Request.Builder()
                        .url(baseUrl + "create-group")
                        .post(requestBody)
                        .build();

                // Send the request
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle the failure here
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            Log.d(TAG, "onResponse: " + responseBody);

                            // Parse the JSON response to get the groupId
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                groupId = jsonResponse.getString("groupId");

                                // Enable the "Add User" button after group creation
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addUserButton.setEnabled(true);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Handle the unsuccessful response here
                        }
                    }
                });
            }
        });

        // Configure the "Add User" button to add users to the group
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the list of checked user IDs
                List<String> checkedUserIds = userAdapter.getCheckedUserIds();

                // Add the checked users to the group using the stored groupId
                for (String userId : checkedUserIds) {
                    addUserToGroup(groupId, userId);
                }
            }
        });

        // Fetch and display the list of users
        new NetworkRequestTask().execute(baseUrl + "users");
    }

    private void addUserToGroup(String groupId, String userId) {
        OkHttpClient client = new OkHttpClient();
        MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");

        // Create a JSON request body to add the user to the group
        String json = "{\"groupId\":\"" + groupId + "\",\"userId\":\"" + userId + "\"}";
        RequestBody requestBody = RequestBody.create(jsonMediaType, json);

        // Create the POST request to add the user to the group
        Request request = new Request.Builder()
                .url(baseUrl + "add-user-to-group")
                .post(requestBody)
                .build();

        // Send the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the failure here
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "onResponse (Add user to group): " + responseBody);
                    // Handle the successful response for adding the user to the group
                } else {
                    // Handle the unsuccessful response for adding the user to the group
                }
            }
        });
    }

    private class NetworkRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();

            try {
                Response response = client.newCall(request).execute();
                ResponseBody responseBody = response.body();

                if (response.isSuccessful() && responseBody != null) {
                    return responseBody.string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // Parse the JSON response
                    JSONArray jsonArray = new JSONArray(result);

                    // Initialize arrays based on the number of users
                    String[] userIds = new String[jsonArray.length()];
                    String[] usernames = new String[jsonArray.length()];

                    // Extract user information and store it in arrays
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        userIds[i] = user.getString("userId");
                        usernames[i] = user.getString("username");
                    }

                    // Create and set the adapter with the data
                    userAdapter = new UserAdapter2(userIds, usernames);
                    recyclerView.setAdapter(userAdapter);

                    showToast("User Count: " + jsonArray.length());

                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("JSON parsing error");
                }
            } else {
                showToast("Request failed.");
            }
        }

        private void showToast(String message) {
            if (CreateGroupActivity.this != null && !CreateGroupActivity.this.isFinishing()) {
                Toast.makeText(CreateGroupActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
