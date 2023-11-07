package com.example.conferencing.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conferencing.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter2 extends RecyclerView.Adapter<UserAdapter2.UserViewHolder> {
    private String[] userIds;
    private String[] usernames;
    private boolean[] checkedUsers;

    public UserAdapter2(String[] userIds, String[] usernames) {
        this.userIds = userIds;
        this.usernames = usernames;
        this.checkedUsers = new boolean[userIds.length]; // Initialize with false
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item2, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.usernameTextView.setText("Username: " + usernames[position]);

        // Set the checkbox state based on the checkedUsers array
        holder.checkbox.setChecked(checkedUsers[position]);

        // Handle checkbox click events to update the checkedUsers array
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkedUsers[position] = isChecked;
        });
    }

    @Override
    public int getItemCount() {
        return userIds.length;
    }

    // Add a method to get the checked user IDs
    public List<String> getCheckedUserIds() {
        List<String> checkedIds = new ArrayList<>();
        for (int i = 0; i < userIds.length; i++) {
            if (checkedUsers[i]) {
                checkedIds.add(userIds[i]);
            }
        }
        return checkedIds;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTextView;
        TextView usernameTextView;
        CheckBox checkbox;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.userStatusTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}

