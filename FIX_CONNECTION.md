# Hướng dẫn sửa lỗi kết nối API

## Vấn đề
Lỗi: `failed to connect to /10.33.56.130 (port 501)`

Nguyên nhân: IP `10.33.56.130` không khả dụng trong môi trường hiện tại.

## Giải pháp

### Bước 1: Tìm IP của máy tính đang chạy API server

**Trên Windows:**
```cmd
ipconfig
```
Tìm dòng "IPv4 Address" trong phần adapter đang kết nối mạng (WiFi hoặc Ethernet).
Ví dụ: `192.168.1.100` hoặc `192.168.0.50`

**Trên Mac/Linux:**
```bash
ifconfig
```
Tìm dòng "inet" trong phần en0 (WiFi) hoặc eth0 (Ethernet).

### Bước 2: Cập nhật IP trong code

Mở file: `app/src/main/java/com/prm/flightbooking/api/RetrofitClient.java`

Tìm dòng 24:
```java
private static final String REAL_IP_BASE_URL = "http://192.168.1.100:501/api/"; // Thay đổi IP này
```

Thay `192.168.1.100` bằng IP bạn vừa tìm được ở Bước 1.

### Bước 3: Đảm bảo API server đang chạy

1. Mở terminal trong thư mục API:
   ```cmd
   cd API/FlightBooking
   ```

2. Chạy API server:
   ```cmd
   dotnet run
   ```

3. Kiểm tra API đang chạy:
   - Mở browser: `http://localhost:501/swagger`
   - Hoặc: `http://[IP_CỦA_BẠN]:501/swagger`

### Bước 4: Đảm bảo thiết bị và máy tính cùng mạng

- Thiết bị Android và máy tính phải kết nối cùng mạng WiFi/LAN
- Tắt firewall tạm thời nếu cần (chỉ cho development)
- Hoặc mở port 501 trong Windows Firewall

### Bước 5: Rebuild và chạy lại app

1. Clean và rebuild project trong Android Studio
2. Chạy lại app trên thiết bị

## Giải pháp thay thế

### Nếu dùng Android Emulator:
Không cần thay đổi gì, code đã tự động dùng `10.0.2.2:501` cho emulator.

### Nếu cần test nhanh:
Có thể tạm thời force dùng emulator URL bằng cách sửa hàm `getBaseUrl()`:
```java
private static String getBaseUrl() {
    // Tạm thời force dùng emulator URL
    return ANDROID_STUDIO_BASE_URL;
}
```

## Kiểm tra kết nối

Sau khi cấu hình xong, thử mở URL này trên browser trong thiết bị:
`http://[IP_CỦA_MÁY_TÍNH]:501/api/Bookings`

Nếu thấy JSON response, nghĩa là kết nối đã thành công.













