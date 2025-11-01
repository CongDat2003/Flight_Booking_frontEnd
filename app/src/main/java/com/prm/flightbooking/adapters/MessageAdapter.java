package com.prm.flightbooking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.flightbooking.R;
import com.prm.flightbooking.dto.chat.MessageDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<MessageDto> messages;
    private SimpleDateFormat timeFormat;

    public MessageAdapter() {
        this.messages = new ArrayList<>();
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public void updateData(List<MessageDto> newMessages) {
        this.messages = newMessages != null ? newMessages : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMessage(MessageDto message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageDto message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutUserMessage, layoutAdminMessage;
        private TextView tvUserContent, tvUserTime;
        private TextView tvAdminSender, tvAdminContent, tvAdminTime, tvAutoReplyBadge;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutUserMessage = itemView.findViewById(R.id.layout_user_message);
            layoutAdminMessage = itemView.findViewById(R.id.layout_admin_message);
            tvUserContent = itemView.findViewById(R.id.tv_user_content);
            tvUserTime = itemView.findViewById(R.id.tv_user_time);
            tvAdminSender = itemView.findViewById(R.id.tv_admin_sender);
            tvAdminContent = itemView.findViewById(R.id.tv_admin_content);
            tvAdminTime = itemView.findViewById(R.id.tv_admin_time);
            tvAutoReplyBadge = itemView.findViewById(R.id.tv_auto_reply_badge);
        }

        public void bind(MessageDto message) {
            if (message.isFromUser()) {
                // Hiển thị tin nhắn của user (bên phải)
                layoutUserMessage.setVisibility(View.VISIBLE);
                layoutAdminMessage.setVisibility(View.GONE);
                tvUserContent.setText(message.getContent());
                if (message.getCreatedAt() != null) {
                    tvUserTime.setText(timeFormat.format(message.getCreatedAt()));
                }
            } else {
                // Hiển thị tin nhắn từ admin/system (bên trái)
                layoutUserMessage.setVisibility(View.GONE);
                layoutAdminMessage.setVisibility(View.VISIBLE);
                
                if (message.isFromAdmin()) {
                    tvAdminSender.setText("Admin");
                    tvAdminSender.setVisibility(View.VISIBLE);
                } else {
                    tvAdminSender.setText("Hệ thống");
                    tvAdminSender.setVisibility(View.VISIBLE);
                }
                
                tvAdminContent.setText(message.getContent());
                if (message.getCreatedAt() != null) {
                    tvAdminTime.setText(timeFormat.format(message.getCreatedAt()));
                }
                
                // Hiển thị badge auto-reply nếu là tin nhắn tự động
                if (message.isAutoReply()) {
                    tvAutoReplyBadge.setVisibility(View.VISIBLE);
                } else {
                    tvAutoReplyBadge.setVisibility(View.GONE);
                }
            }
        }
    }
}







