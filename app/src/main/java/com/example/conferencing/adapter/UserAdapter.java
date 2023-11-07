package com.example.conferencing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conferencing.ChatActivity;
import com.example.conferencing.R;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.conferencing.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final String username;
    private final String uid;
    private Context context; // Add a context field to use in item click handling
    private String[] userIds;
    private String[] usernames;

    public UserAdapter(Context context, String[] userIds, String[] usernames,String username, String uid) {
        this.context = context;
        this.userIds = userIds;
        this.usernames = usernames;
        this.username = username;
        this.uid = uid;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.usernameTextView.setText("Username: " + usernames[position]);

        // Handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // When an item is clicked, start a new activity and pass the userId
                    String clickedUsername = usernames[adapterPosition];
                    String clickedUserId = userIds[adapterPosition];
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("RECEIVER", clickedUsername);
                    intent.putExtra("RECEIVERId", clickedUserId);
                    intent.putExtra("userid", uid);
                    intent.putExtra("username", username);

                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return userIds.length;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTextView;
        TextView usernameTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.userStatusTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
        }
    }
}
