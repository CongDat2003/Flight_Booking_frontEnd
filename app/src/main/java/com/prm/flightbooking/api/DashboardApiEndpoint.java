package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.dashboard.DashboardStatsDto;
import com.prm.flightbooking.dto.dashboard.PopularRouteDto;
import com.prm.flightbooking.dto.dashboard.RevenueByMonthDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DashboardApiEndpoint {
    @GET("admin/Dashboard/stats")
    Call<DashboardStatsDto> getDashboardStats();

    @GET("admin/Dashboard/revenue/{year}")
    Call<List<RevenueByMonthDto>> getRevenueReport(@Path("year") int year);

    @GET("admin/Dashboard/revenue/{startYear}/{endYear}")
    Call<List<RevenueByMonthDto>> getRevenueReportRange(@Path("startYear") int startYear, @Path("endYear") int endYear);

    @GET("admin/Dashboard/revenue")
    Call<List<RevenueByMonthDto>> getRevenueReportQuery(@Query("year") Integer year, @Query("startYear") Integer startYear, @Query("endYear") Integer endYear);

    @GET("admin/Dashboard/popular-routes")
    Call<List<PopularRouteDto>> getPopularRoutes(@Query("topCount") int topCount);
}
