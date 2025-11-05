# 📱 PHÂN TÍCH LUỒNG CHÍNH APP MOBILE

## ✅ LUỒNG HOẠT ĐỘNG CHÍNH

### 1. **Luồng Đăng Nhập** ✅
```
MainActivity (Welcome) 
  → Login 
  → Lưu thông tin vào SharedPreferences 
  → MainMenuActivity
```

**Trạng thái:** ✅ HOẠT ĐỘNG TỐT
- Validate input đầy đủ
- Xử lý lỗi đúng cách (401, 400, 500)
- Lưu userId, username, email, fullName vào SharedPreferences
- Kiểm tra userId hợp lệ trước khi lưu
- Redirect đúng đến MainMenuActivity

---

### 2. **Luồng Tìm Kiếm Chuyến Bay** ✅
```
MainMenuActivity 
  → SearchFlightActivity 
  → Load Airports từ API 
  → Validate input 
  → Call Advanced Search API 
  → FlightResultsActivity (với JSON results)
```

**Trạng thái:** ✅ HOẠT ĐỘNG TỐT
- Load danh sách airports từ API
- Validate đầy đủ: from, to, date, time, seatClass
- Xử lý lỗi API đúng cách
- Truyền kết quả qua JSON string

**Điểm cần chú ý:**
- ✅ DatePicker chỉ cho phép chọn ngày từ hôm nay trở đi
- ✅ TimePicker cho phép chọn giờ
- ✅ Adult count có giới hạn 1-9 người
- ✅ Seat class có 3 loại: Economy, Business, First Class

---

### 3. **Luồng Chọn Chuyến Bay** ✅
```
FlightResultsActivity 
  → Hiển thị danh sách flights 
  → User chọn flight 
  → ChooseSeatsActivity (với flightId)
```

**Trạng thái:** ✅ HOẠT ĐỘNG TỐT
- Parse JSON từ SearchFlightActivity
- Hiển thị danh sách flights trong RecyclerView
- Pass flightId đúng đến ChooseSeatsActivity

**Điểm cần chú ý:**
- ✅ FlightAdapter xử lý click event đúng
- ✅ Check login status trước khi cho phép chọn

---

### 4. **Luồng Chọn Ghế & Thông Tin Hành Khách** ⚠️
```
ChooseSeatsActivity 
  → Load SeatMap từ API 
  → User nhập số hành khách 
  → Dynamic form cho từng hành khách 
  → User chọn seat class 
  → Validate thông tin 
  → BookingFormActivity (với flightId, seatClassId, passengerCount, passengerDetails, seatClassPrice)
```

**Trạng thái:** ⚠️ HOẠT ĐỘNG NHƯNG CÓ VẤN ĐỀ

**Vấn đề phát hiện:**
1. ❌ **ChooseSeatsActivity bị comment code cũ** - File có nhiều code bị comment (dòng 1-508)
2. ✅ **Code hiện tại (dòng 845-1247) hoạt động tốt:**
   - Load seat map từ API
   - Dynamic form cho hành khách
   - Validate thông tin hành khách (tên, CMND/CCCD 9-12 số)
   - Tính giá dựa trên seat class và số lượng hành khách
   - Pass đúng dữ liệu đến BookingFormActivity

**Điểm cần cải thiện:**
- ⚠️ Code cũ bị comment nên file khá dài, khó maintain
- ✅ Validate passenger info đầy đủ
- ✅ Tính giá real-time khi thay đổi seat class hoặc số lượng hành khách

---

### 5. **Luồng Đặt Vé** ⚠️
```
BookingFormActivity 
  → Validate dữ liệu từ Intent 
  → Hiển thị booking summary 
  → User chọn payment method 
  → Tạo payment (có vấn đề) 
  → Tạo booking 
  → Navigate đến MainMenuActivity
```

**Trạng thái:** ⚠️ HOẠT ĐỘNG NHƯNG CÓ VẤN ĐỀ

**Vấn đề phát hiện:**

1. ❌ **Payment được tạo trước Booking:**
   ```java
   // Line 239-254: createPaymentWithMethod() được gọi khi user chọn payment method
   // NHƯNG booking chưa được tạo!
   paymentDto.setBookingId(0); // Line 243 - bookingId = 0!
   ```
   - Payment được tạo với `bookingId = 0` (chưa có booking)
   - Điều này không đúng với flow thực tế
   - Nên: Tạo booking trước → Lấy bookingId → Tạo payment

2. ❌ **Mock Payment URL:**
   ```java
   // Line 250: generateMockPaymentUrl() - Dùng mock URL thay vì API thực
   String mockPaymentUrl = generateMockPaymentUrl(paymentMethod);
   ```
   - Không gọi API thực để tạo payment
   - Dùng mock URL cho VNPay, MoMo, ZaloPay

3. ✅ **Booking creation hoạt động tốt:**
   ```java
   // Line 369-393: onBtnBookClick() tạo booking đúng cách
   CreateBookingDto bookingDto = createBookingData();
   Call<BookingResponseDto> call = bookingApi.createBooking(bookingDto);
   ```
   - Validate đầy đủ
   - Xử lý lỗi đúng cách
   - Hiển thị notification khi thành công

**Khuyến nghị:**
- 🔧 Sửa flow: Tạo booking trước → Tạo payment sau
- 🔧 Bỏ mock URL, dùng API thực
- 🔧 Hoặc chuyển payment sang PayActivity sau khi booking thành công

---

### 6. **Luồng Thanh Toán (PayActivity)** ✅
```
PayActivity 
  → Load booking detail từ API 
  → Hiển thị thông tin vé 
  → User chọn payment method 
  → Tạo payment từ API 
  → WebViewPaymentActivity (mở VNPay URL) 
  → Xử lý callback 
  → PaymentResultActivity
```

**Trạng thái:** ✅ HOẠT ĐỘNG TỐT

**Điểm tốt:**
- ✅ Load booking detail từ API đúng cách
- ✅ Hiển thị đầy đủ thông tin: flight, passengers, seats, price
- ✅ Tạo payment qua API thực (không dùng mock)
- ✅ Support VNPay với channel picker (VNBANK, INTCARD, VNPAYQR)
- ✅ Xử lý callback từ WebViewPaymentActivity
- ✅ Navigate đến PaymentResultActivity đúng cách

**Điểm cần chú ý:**
- ✅ Payment button visibility được update dựa trên payment status
- ✅ Support QR code payment (VietQR)

---

### 7. **Luồng WebView Payment** ✅
```
WebViewPaymentActivity 
  → Load VNPay URL trong WebView 
  → User thanh toán trên VNPay 
  → VNPay redirect về ReturnUrl 
  → Detect callback URL 
  → Parse payment result 
  → Return result về PayActivity
```

**Trạng thái:** ✅ HOẠT ĐỘNG TỐT

**Điểm tốt:**
- ✅ Detect cả deep link (`flightbooking://`) và HTTP callback
- ✅ Parse VNPay response parameters đúng cách
- ✅ Return status, message, transactionId về PayActivity
- ✅ Logging đầy đủ để debug

**Code đã được fix trước đó:**
- ✅ `shouldOverrideUrlLoading()` - Detect return URL
- ✅ `onPageStarted()` - Backup detection
- ✅ `onPageFinished()` - Final check
- ✅ `parsePaymentResult()` - Parse VNPay parameters

---

## 🔴 VẤN ĐỀ TỔNG HỢP

### 1. **BookingFormActivity - Payment Flow SAI** ❌

**Vấn đề:**
- Payment được tạo trước khi booking được tạo
- Dùng mock URL thay vì API thực
- User có thể thanh toán nhưng booking chưa tồn tại

**Giải pháp:**
```java
// Option 1: Tạo booking trước, payment sau
1. User click "Đặt vé"
2. Tạo booking → Lấy bookingId
3. Navigate đến PayActivity với bookingId
4. PayActivity tạo payment và xử lý thanh toán

// Option 2: Bỏ payment trong BookingFormActivity
1. User click "Đặt vé"
2. Tạo booking → Lấy bookingId
3. Navigate đến PayActivity với bookingId
4. PayActivity hiển thị và cho phép thanh toán
```

**Khuyến nghị:** Dùng Option 2 (giống PayActivity hiện tại)

---

### 2. **ChooseSeatsActivity - Code Comment** ⚠️

**Vấn đề:**
- File có nhiều code cũ bị comment (500+ dòng)
- Khó maintain và debug

**Giải pháp:**
- Xóa code comment không dùng
- Giữ lại code hiện tại (dòng 845-1247)

---

## ✅ ĐIỂM MẠNH

1. ✅ **Error Handling:** Tất cả API calls đều có error handling đầy đủ
2. ✅ **Validation:** Validate input ở mọi màn hình
3. ✅ **Login Check:** Kiểm tra login status ở các màn hình quan trọng
4. ✅ **User Experience:** Toast messages, progress bars, loading states
5. ✅ **Data Flow:** Truyền dữ liệu giữa các Activity đúng cách
6. ✅ **PayActivity:** Flow thanh toán hoàn chỉnh và đúng cách

---

## 🎯 KHUYẾN NGHỊ SỬA CHỮA

### Priority 1: Sửa BookingFormActivity Payment Flow ❌

**File:** `BookingFormActivity.java`

**Thay đổi:**
1. Bỏ `createPaymentWithMethod()` trong BookingFormActivity
2. Bỏ `showPaymentMethodSelection()` trong BookingFormActivity
3. Sau khi tạo booking thành công, navigate đến PayActivity:
   ```java
   private void handleBookingSuccess(BookingResponseDto bookingResponse) {
       int bookingId = bookingResponse.getBookingId();
       
       // Navigate to PayActivity instead of MainMenu
       Intent intent = new Intent(this, PayActivity.class);
       intent.putExtra("bookingId", bookingId);
       startActivity(intent);
       finish();
   }
   ```

### Priority 2: Clean up ChooseSeatsActivity ⚠️

**File:** `ChooseSeatsActivity.java`

**Thay đổi:**
- Xóa tất cả code comment (dòng 1-508)
- Giữ lại code hiện tại (dòng 845-1247)

---

## 📊 TỔNG KẾT

| Luồng | Trạng thái | Vấn đề |
|-------|------------|--------|
| Login | ✅ Tốt | Không có |
| Search Flight | ✅ Tốt | Không có |
| Choose Flight | ✅ Tốt | Không có |
| Choose Seats | ⚠️ OK | Code comment cần cleanup |
| Booking Form | ⚠️ Có vấn đề | Payment flow sai |
| Payment (PayActivity) | ✅ Tốt | Không có |
| WebView Payment | ✅ Tốt | Không có |

**Kết luận:** 
- ✅ **7/7 luồng hoạt động được**
- ⚠️ **2 luồng cần cải thiện** (ChooseSeats cleanup, BookingForm payment)
- ❌ **1 luồng cần sửa gấp** (BookingForm payment flow)

**App có thể chạy được nhưng cần sửa BookingFormActivity để payment flow đúng.**























