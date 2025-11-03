package com.prm.flightbooking.dto.chat;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ChatConversationDto {
    @SerializedName("messages")
    private List<MessageDto> messages;

    @SerializedName("unreadCount")
    private int unreadCount;

    @SerializedName("lastMessageTime")
    private Date lastMessageTime;

    public ChatConversationDto() {
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}





































