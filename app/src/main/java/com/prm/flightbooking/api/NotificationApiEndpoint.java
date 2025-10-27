package com.prm.flightbooking.api;


import com.prm.flightbooking.dto.notify.NotificationDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationApiEndpoint {
    @GET("Notification/user/{userId}")
    Call<List<NotificationDto>> getUserNotifications(
            @Path("userId") int userId,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @POST("Notification/{notificationId}/read")
    Call<Map<String, String>> markAsRead(
            @Path("notificationId") int notificationId,
            @Query("userId") int userId
    );

    @GET("Notification/user/{userId}/unread-count")
    Call<Integer> getUnreadCount(@Path("userId") int userId);
}
