package com.prm.flightbooking.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String ANDROID_STUDIO_BASE_URL = "http://10.0.2.2:501/api/";
    private static final String BLUESTACKS_BASE_URL = "http://10.0.2.2:501/api/";
    private static final String REAL_IP_BASE_URL = "http://192.168.10.33:501/api/";
    private static final String LOCALHOST_BASE_URL = "http://10.0.2.2:501/api/";

    private static Retrofit retrofitInstance;
    private static Gson gsonInstance;

    public static Retrofit getInstance() {
        if (retrofitInstance == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            String baseUrl = getBaseUrl();

            // Cấu hình OkHttpClient để trust all certificates (chỉ cho development)
            OkHttpClient httpClient = getUnsafeOkHttpClient();
            
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitInstance;
    }

    // Helper method để trust all SSL certificates (chỉ cho development)
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Tạo trust manager để accept tất cả certificates
            final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
            };

            // Tạo SSL context với trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            
            // Tạo SSLSocketFactory với SSL context
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Build OkHttpClient với SSL socket factory
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getBaseUrl() {
        // Detect môi trường chạy
        String emulator = android.os.Build.PRODUCT;
        String manufacturer = android.os.Build.MANUFACTURER;
        
        // Kiểm tra nếu là Android Emulator
        if (emulator.contains("sdk") || emulator.contains("generic")) {
            // Emulator luôn dùng 10.0.2.2 để truy cập localhost máy host
            return ANDROID_STUDIO_BASE_URL;
        }
        
        // Nếu chạy trên Bluestacks
        if (manufacturer.toLowerCase().contains("bluestacks") ||
            emulator.toLowerCase().contains("bluestacks")) {
            return BLUESTACKS_BASE_URL;
        }
        
        // Nếu chạy trên thiết bị thật
        return REAL_IP_BASE_URL;
    }
    
    // Method để force sử dụng localhost (tạm thời)
    public static void forceLocalhost() {
        retrofitInstance = null; // Reset instance
    }

    public static Gson getGson() {
        if (gsonInstance == null) {
            gsonInstance = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();
        }
        return gsonInstance;
    }
}
