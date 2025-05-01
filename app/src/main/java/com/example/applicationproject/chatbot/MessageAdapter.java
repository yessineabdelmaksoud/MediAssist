package com.example.applicationproject.chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter() {
        messageList = new ArrayList<>();
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    public void clear() {
        messageList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getType() == Message.TYPE_USER) {
            holder.userMessageLayout.setVisibility(View.VISIBLE);
            holder.botMessageLayout.setVisibility(View.GONE);
            holder.userMessageText.setText(message.getContent());
        } else {
            holder.userMessageLayout.setVisibility(View.GONE);
            holder.botMessageLayout.setVisibility(View.VISIBLE);
            holder.botMessageText.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout userMessageLayout, botMessageLayout;
        TextView userMessageText, botMessageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageLayout = itemView.findViewById(R.id.userMessageLayout);
            botMessageLayout = itemView.findViewById(R.id.botMessageLayout);
            userMessageText = itemView.findViewById(R.id.userMessageText);
            botMessageText = itemView.findViewById(R.id.botMessageText);
        }
    }
}