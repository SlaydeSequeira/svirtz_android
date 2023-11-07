package com.example.conferencing.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conferencing.R;

import java.util.List;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final int VIEW_TYPE_RIGHT = 0;
    private static final int VIEW_TYPE_LEFT = 1;

    private Context context;
    private List<String> messages;
    private List<Integer> leftPositions;

    public ChatAdapter(Context context, List<String> messages, List<Integer> leftPositions) {
        this.context = context;
        this.messages = messages;
        this.leftPositions = leftPositions;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        String message = messages.get(position);
        holder.setMessage(message);
    }

    @Override
    public int getItemViewType(int position) {
        return leftPositions.get(position) == 0 ? VIEW_TYPE_RIGHT : VIEW_TYPE_LEFT;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }

        public void setMessage(String message) {
            messageText.setText(message);
        }
    }
}
