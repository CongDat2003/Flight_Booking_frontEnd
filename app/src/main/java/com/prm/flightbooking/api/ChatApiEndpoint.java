package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.chat.ChatConversationDto;
import com.prm.flightbooking.dto.chat.CreateMessageDto;
import com.prm.flightbooking.dto.chat.MessageDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApiEndpoint {
    @POST("Chat/send")
    Call<MessageDto> sendMessage(@Body CreateMessageDto messageDto);

    @GET("Chat/conversation")
    Call<ChatConversationDto> getConversation(@Query("userId") Integer userId);

    @PUT("Chat/{messageId}/read")
    Call<MessageDto> markAsRead(@Path("messageId") int messageId);

    @GET("Chat/unread")
    Call<List<MessageDto>> getUnreadMessages(@Query("userId") Integer userId);
}



























