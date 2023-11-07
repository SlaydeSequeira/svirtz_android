package com.example.conferencing;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conferencing.adapter.UserAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HomeFragment extends Fragment {
    String BASE_URL = "http://192.168.1.5:3000/api/users";

    // Arrays to store user information
    String[] userIds;
    String[] usernames;

    String username,uid;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args1 = getArguments();

        if (args1 != null && args1.containsKey("username")) {
            username = args1.getString("username");
            uid = args1.getString("myStringData");
            Toast.makeText(getActivity(),username,Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreateView: "+username);
        }

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new NetworkRequestTask().execute(BASE_URL);
        return view;
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
                    userIds = new String[jsonArray.length()];
                    usernames = new String[jsonArray.length()];

                    // Extract user information and store it in arrays
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        userIds[i] = user.getString("userId");
                        usernames[i] = user.getString("username");
                    }

                    // Create and set the adapter with the data
                    userAdapter = new UserAdapter(getActivity(),userIds,usernames,username,uid);
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
            if (getActivity() != null && isAdded() && !getActivity().isFinishing()) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
