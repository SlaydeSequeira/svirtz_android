package com.example.conferencing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.conferencing.adapter.ChatAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends Activity {
    String username, receiver,rid;
    EditText et;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private static final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://192.168.1.5:3000";  // Replace with your server URL
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ImageButton button = findViewById(R.id.btn_send);
        et = findViewById(R.id.edit);
        Intent intent = getIntent();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            uid = intent.getStringExtra("userid");
            receiver = intent.getStringExtra("RECEIVER");
            rid = intent.getStringExtra("RECEIVERId");
            Log.d(TAG, "onCreate: "+rid);
            Toast.makeText(ChatActivity.this, username+uid, Toast.LENGTH_SHORT).show();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChatMessage();
            }
        });
        makeRequest();
    }
    private void sendChatMessage() {
        new MyAsyncTask().execute();
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String msg = et.getText().toString();
                String json = "{\n" +
                        "  \"message\": \"" + msg + "\",\n" +
                        "  \"messageType\": \"text\",\n" +
                        "  \"sender_name\": \"" + username + "\",\n" +
                        "  \"receiver_name\": \"" + receiver + "\"\n" +
                        "}";

                String response = postRequest("/api/save-msg", json);
                Log.d("HTTP Response", response);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            // Handle the response here
        }
    }

    private String postRequest(String endpoint, String json) throws Exception {
        String url = BASE_URL + endpoint;

        RequestBody requestBody = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    private void makeRequest() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.1.5:3000/api/messages/" + uid + "/" + rid;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network errors
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Error: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(responseBody);
                            try {
                                JSONArray jsonArray = new JSONArray(responseBody);
                                // Initialize arrays to store data
                                ArrayList<String> messages = new ArrayList<>();
                                ArrayList<String> senderIds = new ArrayList<>();
                                ArrayList<String> receiverIds = new ArrayList();
                                ArrayList<Integer> left = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String message = jsonObject.getString("message");
                                    String senderId = jsonObject.getString("sender_id");
                                    String receiverId = jsonObject.getString("receiver_id");
                                    // Add data to arrays
                                    Log.d("Array Sizes", "messages: " + message + ", senderIds: " + senderId + ", receiverIds: " + receiverId);
                                    if(rid.equals(receiverId))
                                    {
                                        left.add(0);
                                        Log.d(TAG, "run: "+0);
                                    }
                                    else
                                    {
                                        left.add(1);
                                        Log.d(TAG, "run: "+1);
                                    }
                                    messages.add(message);
                                    senderIds.add(senderId);
                                    receiverIds.add(receiverId);
                                    chatAdapter = new ChatAdapter(ChatActivity.this, messages, left);
                                    recyclerView.setAdapter(chatAdapter);
                                }
                                Log.d("JSON Response", responseBody);
                                // Now, you have messages, senderIds, and receiverIds arrays containing the data.
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // Handle the exception appropriately
                            }

                        }
                    });
                } else {
                    // Handle non-successful responses
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Error: " + response.code() + " " + response.message());
                        }
                    });
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}