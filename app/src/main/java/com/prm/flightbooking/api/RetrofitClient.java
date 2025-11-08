package com.prm.flightbooking.api;

import android.util.Log;

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
    private static final String TAG = "RetrofitClient";
    private static final String ANDROID_STUDIO_BASE_URL = "http://10.0.2.2:501/api/";
    private static final String BLUESTACKS_BASE_URL = "http://10.0.2.2:501/api/";
    
    // ============================================
    // QUAN TRỌNG: CẬP NHẬT IP NÀY!
    // ============================================
    // Để tìm IP của máy tính:
    // 1. Mở Command Prompt (cmd)
    // 2. Chạy lệnh: ipconfig
    // 3. Tìm "IPv4 Address" trong phần adapter đang dùng (WiFi hoặc Ethernet)
    // 4. Thay IP bên dưới bằng IP bạn vừa tìm được
    // Ví dụ: nếu IP là 192.168.1.100, thay thành: "http://192.168.1.100:501/api/"
    // ============================================
    private static final String REAL_IP_BASE_URL = "http://192.168.10.9:501/api/";
    private static final String LOCALHOST_BASE_URL = "http://10.0.2.2:501/api/";
    
    // Danh sách các IP dự phòng để thử (theo thứ tự ưu tiên)
    // Có thể thêm nhiều IP vào đây để tự động thử
    private static final String[] FALLBACK_IPS = {
        "http://192.168.10.9:501/api/",   // IP chính
        "http://192.168.10.50:501/api/",  // IP dự phòng 1
        "http://192.168.10.100:501/api/", // IP dự phòng 2
        "http://10.0.2.2:501/api/",       // Emulator fallback
    };

    private static Retrofit retrofitInstance;
    private static Gson gsonInstance;

    public static Retrofit getInstance() {
        if (retrofitInstance == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            String baseUrl = getBaseUrl();
            Log.d(TAG, "Initializing Retrofit with base URL: " + baseUrl);

            // Cấu hình OkHttpClient để trust all certificates (chỉ cho development)
            OkHttpClient httpClient = getUnsafeOkHttpClient();
            
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            
            Log.d(TAG, "Retrofit instance created successfully");
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
        String emulator = android.os.Build.PRODUCT != null ? android.os.Build.PRODUCT.toLowerCase() : "";
        String manufacturer = android.os.Build.MANUFACTURER != null ? android.os.Build.MANUFACTURER.toLowerCase() : "";
        String model = android.os.Build.MODEL != null ? android.os.Build.MODEL.toLowerCase() : "";
        String brand = android.os.Build.BRAND != null ? android.os.Build.BRAND.toLowerCase() : "";
        String device = android.os.Build.DEVICE != null ? android.os.Build.DEVICE.toLowerCase() : "";
        String hardware = android.os.Build.HARDWARE != null ? android.os.Build.HARDWARE.toLowerCase() : "";
        
        // Log device info for debugging
        Log.d(TAG, "Device Detection - PRODUCT: " + android.os.Build.PRODUCT + 
                   ", MANUFACTURER: " + android.os.Build.MANUFACTURER + 
                   ", MODEL: " + android.os.Build.MODEL + 
                   ", BRAND: " + android.os.Build.BRAND + 
                   ", DEVICE: " + android.os.Build.DEVICE + 
                   ", HARDWARE: " + android.os.Build.HARDWARE);
        
        // Kiểm tra nếu là Android Emulator (Android Studio AVD)
        if (emulator.contains("sdk") || emulator.contains("generic") || 
            emulator.contains("emulator") || device.contains("generic") ||
            hardware.contains("goldfish") || hardware.contains("ranchu")) {
            // Emulator luôn dùng 10.0.2.2 để truy cập localhost máy host
            Log.d(TAG, "Detected Android Studio Emulator, using: " + ANDROID_STUDIO_BASE_URL);
            return ANDROID_STUDIO_BASE_URL;
        }
        
        // Nếu chạy trên Bluestacks (kiểm tra nhiều cách)
        if (manufacturer.contains("bluestacks") || emulator.contains("bluestacks") ||
            model.contains("bluestacks") || brand.contains("bluestacks") ||
            device.contains("bluestacks") || hardware.contains("bluestacks")) {
            Log.d(TAG, "Detected BlueStacks, using: " + BLUESTACKS_BASE_URL);
            return BLUESTACKS_BASE_URL;
        }
        
        // Kiểm tra các emulator khác (Nox, LDPlayer, etc.)
        if (manufacturer.contains("nox") || manufacturer.contains("ldplayer") ||
            manufacturer.contains("mumu") || manufacturer.contains("memu") ||
            model.contains("nox") || model.contains("ldplayer")) {
            Log.d(TAG, "Detected other emulator, using: " + BLUESTACKS_BASE_URL);
            return BLUESTACKS_BASE_URL; // Cũng dùng 10.0.2.2 cho các emulator khác
        }
        
        // Nếu chạy trên thiết bị thật, dùng IP cấu hình
        // Lưu ý: Cần đảm bảo API server đang chạy và thiết bị ở cùng mạng LAN
        // Nếu gặp lỗi kết nối, hãy kiểm tra:
        // 1. API server có đang chạy không? (http://localhost:501/swagger)
        // 2. IP trong REAL_IP_BASE_URL có đúng không? (chạy ipconfig trên Windows)
        // 3. Thiết bị và máy tính có cùng mạng WiFi/LAN không?
        // 4. Firewall có chặn cổng 501 không?
        Log.d(TAG, "Detected real device, using: " + REAL_IP_BASE_URL);
        return REAL_IP_BASE_URL;
    }
    
    /**
     * Get base URL without /api/ suffix (for image loading, etc.)
     * @return Base URL like "http://10.0.2.2:501" or "http://192.168.10.9:501"
     */
    public static String getBaseUrlWithoutApi() {
        String baseUrl = getBaseUrl();
        // Remove /api/ suffix if present
        if (baseUrl.endsWith("/api/")) {
            return baseUrl.substring(0, baseUrl.length() - 5);
        }
        if (baseUrl.endsWith("/api")) {
            return baseUrl.substring(0, baseUrl.length() - 4);
        }
        return baseUrl;
    }
    
    /**
     * Thử kết nối với các IP dự phòng nếu IP chính thất bại
     * Có thể gọi method này khi gặp lỗi kết nối
     */
    public static void tryFallbackUrls() {
        retrofitInstance = null; // Reset để thử lại
        // Logic thử các IP dự phòng có thể được implement ở đây
    }
    
    /**
     * Method để set base URL tùy chỉnh (hữu ích khi cần thay đổi IP động)
     * @param baseUrl URL đầy đủ, ví dụ: "http://192.168.1.100:501/api/"
     */
    public static void setBaseUrl(String baseUrl) {
        retrofitInstance = null; // Reset instance để tạo lại với URL mới
        // Lưu ý: Cần implement SharedPreferences để lưu URL tùy chỉnh nếu muốn lưu vĩnh viễn
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
