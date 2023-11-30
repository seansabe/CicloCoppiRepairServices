package com.example.repairservicesapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.repairservicesapp.R;
import com.example.repairservicesapp.app.AppManager;
import com.example.repairservicesapp.model.ChatMessage;
import com.example.repairservicesapp.util.DateFormatter;

import java.util.ArrayList;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<ChatMessage> chatMessages;
    public ChatRecyclerAdapter(ArrayList<ChatMessage> chatMessages, Context context) {
        this.context = context;
        this.chatMessages = chatMessages;
    }
    @NonNull
    @Override
    public ChatRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.chat_message_recycler_row, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerAdapter.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (chatMessage.getSenderId().equals(AppManager.instance.user.getUserId())) {
            holder.layoutLeft.setVisibility(View.GONE);
            holder.layoutRight.setVisibility(View.VISIBLE);
            holder.textRight.setText(chatMessage.getMessage());
            holder.timeRight.setText(DateFormatter.INSTANCE.fromTimeStampToHour(chatMessage.getTimestamp()));
        } else {
            holder.layoutLeft.setVisibility(View.VISIBLE);
            holder.layoutRight.setVisibility(View.GONE);
            holder.textLeft.setText(chatMessage.getMessage());
            holder.timeLeft.setText(DateFormatter.INSTANCE.fromTimeStampToHour(chatMessage.getTimestamp()));
        }
    }

    public void listen() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutLeft, layoutRight;
        TextView textLeft, textRight, timeLeft, timeRight;
        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            layoutLeft = itemView.findViewById(R.id.layoutLeft);
            layoutRight = itemView.findViewById(R.id.layoutRight);
            textLeft = itemView.findViewById(R.id.txtMessageLeft);
            textRight = itemView.findViewById(R.id.txtMessageRight);
            timeLeft = itemView.findViewById(R.id.txtTimestampLeft);
            timeRight = itemView.findViewById(R.id.txtTimestampRight);
        }
    }
}
