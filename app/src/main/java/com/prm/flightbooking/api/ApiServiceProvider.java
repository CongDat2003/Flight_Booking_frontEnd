package com.prm.flightbooking.api;

import retrofit2.Retrofit;

public class ApiServiceProvider {
    private static Retrofit retrofit = RetrofitClient.getInstance();

    public static AdvancedSearchApiEndpoint getAdvancedSearchApi() {
        return retrofit.create(AdvancedSearchApiEndpoint.class);
    }

    public static AircraftTypeApiEndpoint getAircraftTypeApi() {
        return retrofit.create(AircraftTypeApiEndpoint.class);
    }

    public static AirlineApiEndpoint getAirlineApi() {
        return retrofit.create(AirlineApiEndpoint.class);
    }

    public static AirportApiEndpoint getAirportApi() {
        return retrofit.create(AirportApiEndpoint.class);
    }

    public static AuthApiEndpoint getAuthApi() {
        return retrofit.create(AuthApiEndpoint.class);
    }

    public static BookingApiEndpoint getBookingApi() {
        return retrofit.create(BookingApiEndpoint.class);
    }

    public static DashboardApiEndpoint getDashboardApi() {
        return retrofit.create(DashboardApiEndpoint.class);
    }

    public static FlightApiEndpoint getFlightApi() {
        return retrofit.create(FlightApiEndpoint.class);
    }

    public static UserApiEndpoint getUserApi() {
        return retrofit.create(UserApiEndpoint.class);
    }

    public static NotificationApiEndpoint getNotificationApi() {
        return retrofit.create(NotificationApiEndpoint.class);
    }

    public static PaymentApiEndpoint getPaymentApi() {
        return retrofit.create(PaymentApiEndpoint.class);
    }
}
