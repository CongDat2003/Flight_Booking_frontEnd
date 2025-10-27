# 🔒 SỬA LỖI SSL CERTIFICATE - ANDROID APP

## ✅ Đã sửa xong!

Lỗi `java.security.cert.CertPathValidatorException` đã được xử lý.

### Thay đổi:
- **File:** `RetrofitClient.java`
- **Sửa:** Cấu hình OkHttpClient để trust all SSL certificates
- **Mục đích:** Cho phép app kết nối HTTPS trong development environment

---

## 🚀 Cách chạy lại:

1. **Build lại Android app** (đã làm xong):
   ```bash
   ./gradlew clean build
   ```

2. **Chạy app trên device/emulator**

3. **Test lại login/API calls**

---

## ⚠️ Lưu ý:

- **SSL bypass này CHỈ cho development**
- **KHÔNG nên dùng trong production**
- **Production cần proper SSL certificate**

---

## 📱 Test:

1. Mở Android app
2. Thử login hoặc search flights
3. Nếu không còn lỗi SSL → **Thành công!** ✅

---

## 🐛 Nếu vẫn lỗi:

1. Kiểm tra backend có đang chạy không:
   - Mở: `http://localhost:5091/swagger`
   - Phải thấy Swagger UI

2. Kiểm tra URL trong RetrofitClient:
   - Emulator: `http://10.0.2.2:5091/api/`
   - Real device: `http://192.168.10.25:5091/api/`

3. Clean và rebuild:
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

