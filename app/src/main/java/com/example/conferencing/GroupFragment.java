package com.example.conferencing;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

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

public class GroupFragment extends Fragment {
    String receivedData,username;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
   String uid;
    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        Bundle args = getArguments();
        if (args != null && args.containsKey("dataKey")) {
            receivedData = args.getString("dataKey");
            username = args.getString("username");
            uid = args.getString("uid");
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        String apiUrl = "http://192.168.1.5:3000/api/groups/" + receivedData;
        new HttpTask().execute(apiUrl);

        return view;
    }

    private class HttpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        return responseBody.string();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            try {
                // Assuming jsonResponse is a JSONArray
                JSONArray jsonArray = new JSONArray(jsonResponse);
                int numberOfGroups = jsonArray.length();
                String[] groupIds = new String[numberOfGroups];
                String[] groupNames = new String[numberOfGroups];

                for (int i = 0; i < numberOfGroups; i++) {
                    JSONObject groupInfo = jsonArray.getJSONObject(i);
                    groupIds[i] = groupInfo.optString("groupId");
                    groupNames[i] = groupInfo.optString("groupname");
                }

                // Create and set the adapter with the data
                userAdapter = new UserAdapter(getActivity(), groupIds, groupNames,username,uid);
                recyclerView.setAdapter(userAdapter);

                // Display the total number of groups
                String numberOfGroupsMessage = "Number of groups: " + numberOfGroups;
                Toast.makeText(getActivity(), numberOfGroupsMessage, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
            }
        }
    }
}
