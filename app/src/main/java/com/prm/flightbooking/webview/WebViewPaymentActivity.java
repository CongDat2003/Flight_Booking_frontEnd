package com.prm.flightbooking.webview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.prm.flightbooking.R;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.PaymentApiEndpoint;
import com.prm.flightbooking.dto.payment.CreatePaymentDto;
import com.prm.flightbooking.dto.payment.PaymentResponseDto;
import com.prm.flightbooking.dto.payment.UpdatePaymentDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewPaymentActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "extra_url";
    public static final String RETURN_SCHEME = "flightbooking";
    private static final String VNPAY_RETURN_PATH = "/api/payment/vnpay-return";

    private WebView webView;
    private ProgressBar progressBar;
    private int bookingId = -1; // Store bookingId from Intent
    private boolean isPaymentProcessed = false; // Flag để tránh xử lý trùng
    private PaymentApiEndpoint paymentApi; // API endpoint

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        webView = findViewById(R.id.payment_webview);
        progressBar = findViewById(R.id.payment_progress);

        // Initialize payment API
        paymentApi = ApiServiceProvider.getPaymentApi();

        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url == null) {
            finish();
            return;
        }
        
        // Get bookingId if passed (from BookingFormActivity)
        bookingId = getIntent().getIntExtra("bookingId", -1);

        WebSettings settings = webView.getSettings();
        // Enable JavaScript - bắt buộc cho VNPay
        settings.setJavaScriptEnabled(true);
        // Enable DOM Storage - cần cho VNPay
        settings.setDomStorageEnabled(true);
        // Enable database storage
        settings.setDatabaseEnabled(true);
        // Enable cache
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // Note: setAppCacheEnabled() was deprecated and removed in newer Android versions
        // WebView will use default caching behavior
        // Load with overview
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        // Enable mixed content (HTTP trong HTTPS page)
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        // Set user agent để VNPay nhận diện đúng
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent + " FlightBookingApp");
        // Enable zoom
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        // Enable file access
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        // Add JavaScript Interface để giao tiếp với JavaScript
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String urlString = uri.toString();
                
                android.util.Log.d("WebViewPayment", "=== shouldOverrideUrlLoading ===");
                android.util.Log.d("WebViewPayment", "URL: " + urlString);
                android.util.Log.d("WebViewPayment", "Scheme: " + uri.getScheme());
                android.util.Log.d("WebViewPayment", "Host: " + uri.getHost());
                android.util.Log.d("WebViewPayment", "Path: " + uri.getPath());
                
                // Handle deep link scheme (nếu có)
                if (RETURN_SCHEME.equalsIgnoreCase(uri.getScheme())) {
                    android.util.Log.d("WebViewPayment", "Detected deep link return");
                    parsePaymentResult(uri);
                    return true;
                }
                
                // Handle VNPay HTTP(S) ReturnUrl (backend endpoint)
                // VNPay sẽ redirect về URL này sau khi thanh toán
                if (("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
                    String path = uri.getPath();
                    String host = uri.getHost();
                    
                    // Kiểm tra nếu là backend callback URL
                    if (path != null && (path.contains("/vnpay-return") || path.contains("/payment/vnpay-return"))) {
                        android.util.Log.d("WebViewPayment", "Detected VNPay return URL - Parsing result");
                        // Vẫn load URL để backend xử lý, sau đó parse query params
                        // Không return true ngay, để WebView load URL và xử lý trong onPageFinished
                    }
                    
                    // Kiểm tra nếu là VNPay sandbox domain
                    if (host != null && (host.contains("vnpayment.vn") || host.contains("sandbox.vnpayment.vn"))) {
                        android.util.Log.d("WebViewPayment", "VNPay domain - allowing load");
                        return false; // Cho phép WebView load VNPay pages
                    }
                }
                
                // Cho phép WebView load URL bình thường
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                android.util.Log.d("WebViewPayment", "Page started: " + url);
                progressBar.setVisibility(View.VISIBLE);
                
                // Kiểm tra nếu URL chứa callback từ VNPay
                Uri uri = Uri.parse(url);
                if (uri.getPath() != null && (uri.getPath().contains("/vnpay-return") || uri.getPath().contains("/payment/vnpay-return"))) {
                    android.util.Log.d("WebViewPayment", "Detected VNPay callback in onPageStarted");
                    parsePaymentResult(uri);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                android.util.Log.d("WebViewPayment", "=== onPageFinished ===");
                android.util.Log.d("WebViewPayment", "URL: " + url);
                progressBar.setVisibility(View.GONE);
                
                // Kiểm tra lại khi page finished (một số trường hợp VNPay redirect sau khi page load)
                Uri uri = Uri.parse(url);
                String path = uri.getPath();
                String host = uri.getHost();
                
                android.util.Log.d("WebViewPayment", "Host: " + host);
                android.util.Log.d("WebViewPayment", "Path: " + path);
                android.util.Log.d("WebViewPayment", "Query: " + uri.getQuery());
                
                // Kiểm tra nếu là backend callback URL với query parameters
                if (path != null && (path.contains("/vnpay-return") || path.contains("/payment/vnpay-return"))) {
                    android.util.Log.d("WebViewPayment", "Detected VNPay callback in onPageFinished - Parsing result");
                    
                    // Kiểm tra có query parameters không (cần có vnp_ResponseCode)
                    String responseCode = uri.getQueryParameter("vnp_ResponseCode");
                    if (responseCode != null) {
                        android.util.Log.d("WebViewPayment", "Found vnp_ResponseCode: " + responseCode);
                        parsePaymentResult(uri);
                        return;
                    } else {
                        android.util.Log.w("WebViewPayment", "VNPay return URL but no response code - might be loading");
                    }
                }
                
                // Detect VNPay QR Code page và inject button
                if (host != null && (host.contains("vnpayment.vn") || host.contains("sandbox.vnpayment.vn"))) {
                    // Inject JavaScript để detect QR code và thêm button xác nhận
                    injectQRCodeConfirmButton(view);
                    
                    // Inject lại sau khi DOM có thể thay đổi (AJAX, dynamic content)
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            injectQRCodeConfirmButton(view);
                        }
                    }, 5000);
                }
            }
            
            @Override
            public void onReceivedError(WebView view, android.webkit.WebResourceRequest request, android.webkit.WebResourceError error) {
                android.util.Log.e("WebViewPayment", "WebView error: " + error.getDescription());
                super.onReceivedError(view, request, error);
            }
        });

        webView.loadUrl(url);
    }
    
    // JavaScript Interface để giao tiếp với JavaScript
    public class JavaScriptInterface {
        private WebViewPaymentActivity activity;
        
        public JavaScriptInterface(WebViewPaymentActivity activity) {
            this.activity = activity;
        }
        
        @JavascriptInterface
        public void confirmQRCodePayment() {
            // Gọi method trong Activity để xác nhận thanh toán
            activity.confirmVNPayQRCodePayment();
        }
    }
    
    private void parsePaymentResult(Uri uri) {
        // Kiểm tra nếu đã xử lý rồi thì không xử lý lại
        if (isPaymentProcessed) {
            android.util.Log.d("WebViewPayment", "Payment already processed, skipping parsePaymentResult");
            return;
        }
        try {
            android.util.Log.d("WebViewPayment", "=== parsePaymentResult ===");
            android.util.Log.d("WebViewPayment", "Full URI: " + uri.toString());
            
            // Get bookingId from Intent if not already stored
            if (bookingId == -1) {
                bookingId = getIntent().getIntExtra("bookingId", -1);
            }
            
            // Lấy tất cả query parameters từ VNPay
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");
            String transactionId = uri.getQueryParameter("vnp_TxnRef");
            String amount = uri.getQueryParameter("vnp_Amount");
            String orderInfo = uri.getQueryParameter("vnp_OrderInfo");
            String responseMessage = uri.getQueryParameter("vnp_ResponseMessage");
            String secureHash = uri.getQueryParameter("vnp_SecureHash");
            
            android.util.Log.d("WebViewPayment", "vnp_ResponseCode: " + responseCode);
            android.util.Log.d("WebViewPayment", "vnp_TxnRef: " + transactionId);
            android.util.Log.d("WebViewPayment", "vnp_Amount: " + amount);
            android.util.Log.d("WebViewPayment", "vnp_OrderInfo: " + orderInfo);
            android.util.Log.d("WebViewPayment", "vnp_ResponseMessage: " + responseMessage);
            android.util.Log.d("WebViewPayment", "vnp_SecureHash: " + secureHash);
            
            String status = "failed";
            String message = "Thanh toán thất bại";
            
            if ("00".equals(responseCode)) {
                status = "success";
                message = "Thanh toán thành công";
                android.util.Log.d("WebViewPayment", "Payment SUCCESS");
            } else {
                // Parse message từ VNPay
                if (responseMessage != null && !responseMessage.isEmpty()) {
                    message = responseMessage;
                } else {
                    // Map response code to message
                    switch (responseCode) {
                        case "07":
                            message = "Giao dịch bị nghi ngờ";
                            break;
                        case "09":
                            message = "Thẻ/Tài khoản chưa đăng ký dịch vụ";
                            break;
                        case "10":
                            message = "Xác thực thông tin không đúng";
                            break;
                        case "11":
                            message = "Đã hết hạn chờ thanh toán";
                            break;
                        case "12":
                            message = "Thẻ/Tài khoản bị khóa";
                            break;
                        case "13":
                            message = "Nhập sai mật khẩu xác thực (OTP)";
                            break;
                        case "24":
                            message = "Khách hàng hủy giao dịch";
                            break;
                        case "51":
                            message = "Tài khoản không đủ số dư";
                            break;
                        case "65":
                            message = "Vượt quá hạn mức giao dịch";
                            break;
                        case "75":
                            message = "Ngân hàng thanh toán đang bảo trì";
                            break;
                        default:
                            message = "Thanh toán thất bại (Mã lỗi: " + responseCode + ")";
                            break;
                    }
                }
                android.util.Log.d("WebViewPayment", "Payment FAILED: " + message);
            }
            
            // Đánh dấu đã xử lý
            isPaymentProcessed = true;
            
            Intent resultIntent = new Intent();
            resultIntent.putExtra("status", status);
            resultIntent.putExtra("message", message);
            resultIntent.putExtra("transactionId", transactionId);
            resultIntent.putExtra("responseCode", responseCode);
            if (amount != null) {
                resultIntent.putExtra("amount", amount);
            }
            // Pass bookingId back to caller (BookingFormActivity or PayActivity)
            if (bookingId != -1) {
                resultIntent.putExtra("bookingId", bookingId);
            }
            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            android.util.Log.e("WebViewPayment", "Error parsing payment result", e);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("status", "failed");
            resultIntent.putExtra("message", "Lỗi xử lý kết quả thanh toán: " + e.getMessage());
            // Pass bookingId back even on error
            if (bookingId != -1) {
                resultIntent.putExtra("bookingId", bookingId);
            }
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
    
    // Inject JavaScript để detect QR code và thêm button xác nhận
    private void injectQRCodeConfirmButton(WebView view) {
        String jsCode = 
            "(function() {" +
            "  // Kiểm tra xem đã có button chưa" +
            "  var existingBtn = document.getElementById('vnpay-qr-confirm-btn');" +
            "  if (existingBtn) {" +
            "    return;" +
            "  }" +
            "  " +
            "  // Detect QR code bằng nhiều cách" +
            "  var qrImages = document.querySelectorAll('img[src*=\"qr\"], img[src*=\"QR\"], img[alt*=\"QR\"], img[alt*=\"qr\"], img[src*=\"VNPAYQR\"], img[src*=\"vnpayqr\"]');" +
            "  var qrDivs = document.querySelectorAll('[class*=\"qr\"], [id*=\"qr\"], [class*=\"QR\"], [id*=\"QR\"], [class*=\"VNPAYQR\"], [id*=\"VNPAYQR\"]');" +
            "  var qrCanvas = document.querySelectorAll('canvas');" +
            "  " +
            "  // Detect qua text content" +
            "  var bodyText = document.body.innerText || document.body.textContent || '';" +
            "  var hasQRText = bodyText.includes('QR') || bodyText.includes('Mã QR') || " +
            "                  bodyText.includes('Quét mã') || bodyText.includes('Scan to Pay') || " +
            "                  bodyText.includes('Thông tin đơn hàng');" +
            "  " +
            "  var hasQRCode = qrImages.length > 0 || qrDivs.length > 0 || qrCanvas.length > 0 || hasQRText;" +
            "  " +
            "  // Luôn inject nếu đang ở trang VNPay" +
            "  var isVNPayDomain = window.location.hostname.includes('vnpayment.vn') || " +
            "                     window.location.hostname.includes('sandbox.vnpayment.vn');" +
            "  " +
            "  if (hasQRCode || isVNPayDomain) {" +
            "    // Tìm nút 'Hủy thanh toán' hoặc 'Cancel'" +
            "    var cancelBtn = null;" +
            "    var allButtons = document.querySelectorAll('button, a[role=\"button\"], [class*=\"btn\"], [class*=\"button\"]');" +
            "    " +
            "    for (var i = 0; i < allButtons.length; i++) {" +
            "      var btn = allButtons[i];" +
            "      var btnText = btn.innerText || btn.textContent || btn.getAttribute('aria-label') || '';" +
            "      if (btnText.includes('Hủy') || btnText.includes('Cancel') || btnText.includes('hủy thanh toán') || btnText.includes('Hủy thanh toán')) {" +
            "        cancelBtn = btn;" +
            "        break;" +
            "      }" +
            "    }" +
            "    " +
            "    // Tạo button xác nhận" +
            "    var confirmBtn = document.createElement('button');" +
            "    confirmBtn.id = 'vnpay-qr-confirm-btn';" +
            "    confirmBtn.innerHTML = '✓ XÁC NHẬN THANH TOÁN';" +
            "    " +
            "    // Thêm vào body trước để có thể tính toán position" +
            "    document.body.appendChild(confirmBtn);" +
            "    " +
            "    // Đặt style và vị trí" +
            "    setTimeout(function() {" +
            "      if (cancelBtn) {" +
            "        var cancelRect = cancelBtn.getBoundingClientRect();" +
            "        var cancelBottom = cancelRect.bottom;" +
            "        var cancelTop = cancelRect.top;" +
            "        var cancelLeft = cancelRect.left;" +
            "        var cancelWidth = cancelRect.width;" +
            "        var cancelHeight = cancelRect.height;" +
            "        var viewportHeight = window.innerHeight;" +
            "        " +
            "        // Tính toán bottom position (cùng bottom với nút Hủy)" +
            "        var bottomPos = viewportHeight - cancelBottom;" +
            "        " +
            "        // Đặt button xác nhận bên trái nút Hủy, cùng bottom và height" +
            "        var confirmLeft = Math.max(20, cancelLeft - 220);" +
            "        " +
            "        confirmBtn.style.cssText = " +
            "          'position: fixed;' +" +
            "          'bottom: ' + bottomPos + 'px;' +" +
            "          'left: ' + confirmLeft + 'px;' +" +
            "          'height: ' + cancelHeight + 'px;' +" +
            "          'line-height: ' + cancelHeight + 'px;' +" +
            "          'background: linear-gradient(135deg, #D81B60 0%, #C2185B 100%);' +" +
            "          'color: white;' +" +
            "          'border: none;' +" +
            "          'padding: 0 24px;' +" +
            "          'border-radius: 6px;' +" +
            "          'font-size: 14px;' +" +
            "          'font-weight: bold;' +" +
            "          'box-shadow: 0 2px 8px rgba(216, 27, 96, 0.3);' +" +
            "          'z-index: 99999;' +" +
            "          'cursor: pointer;' +" +
            "          'font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Arial, sans-serif;' +" +
            "          'min-width: 180px;' +" +
            "          'white-space: nowrap;' +" +
            "          'display: flex;' +" +
            "          'align-items: center;' +" +
            "          'justify-content: center;' +" +
            "          'visibility: visible;';" +
            "      } else {" +
            "        // Fallback: đặt ở giữa, bottom 20px" +
            "        confirmBtn.style.cssText = " +
            "          'position: fixed;' +" +
            "          'bottom: 20px;' +" +
            "          'left: 50%;' +" +
            "          'transform: translateX(-50%);' +" +
            "          'background: linear-gradient(135deg, #D81B60 0%, #C2185B 100%);' +" +
            "          'color: white;' +" +
            "          'border: none;' +" +
            "          'padding: 16px 32px;' +" +
            "          'border-radius: 25px;' +" +
            "          'font-size: 16px;' +" +
            "          'font-weight: bold;' +" +
            "          'box-shadow: 0 4px 12px rgba(216, 27, 96, 0.4);' +" +
            "          'z-index: 99999;' +" +
            "          'cursor: pointer;' +" +
            "          'font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Arial, sans-serif;' +" +
            "          'min-width: 200px;' +" +
            "          'white-space: nowrap;' +" +
            "          'display: block;' +" +
            "          'visibility: visible;';" +
            "      }" +
            "    }, 100);" +
            "    " +
            "    // Cập nhật lại vị trí sau khi page scroll hoặc resize" +
            "    var updatePosition = function() {" +
            "      if (cancelBtn && confirmBtn) {" +
            "        var cancelRect = cancelBtn.getBoundingClientRect();" +
            "        var bottomPos = window.innerHeight - cancelRect.bottom;" +
            "        var confirmLeft = Math.max(20, cancelRect.left - 220);" +
            "        confirmBtn.style.bottom = bottomPos + 'px';" +
            "        confirmBtn.style.left = confirmLeft + 'px';" +
            "        confirmBtn.style.height = cancelRect.height + 'px';" +
            "        confirmBtn.style.lineHeight = cancelRect.height + 'px';" +
            "      }" +
            "    };" +
            "    " +
            "    window.addEventListener('resize', updatePosition);" +
            "    window.addEventListener('scroll', updatePosition);" +
            "    " +
            "    confirmBtn.onclick = function(e) {" +
            "      e.preventDefault();" +
            "      e.stopPropagation();" +
            "      e.stopImmediatePropagation();" +
            "      if (typeof Android !== 'undefined' && Android.confirmQRCodePayment) {" +
            "        Android.confirmQRCodePayment();" +
            "      }" +
            "      return false;" +
            "    };" +
            "    confirmBtn.addEventListener('click', confirmBtn.onclick, true);" +
            "  }" +
            "})();";
        
        // Inject nhiều lần để đảm bảo page đã load xong
        // Lần 1: Sau 1 giây
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.evaluateJavascript(jsCode, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        android.util.Log.d("WebViewPayment", "JavaScript injected (1s), result: " + value);
                    }
                });
            }
        }, 1000);
        
        // Lần 2: Sau 2 giây (đảm bảo page render xong)
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.evaluateJavascript(jsCode, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        android.util.Log.d("WebViewPayment", "JavaScript injected (2s), result: " + value);
                    }
                });
            }
        }, 2000);
        
        // Lần 3: Sau 3 giây (trường hợp page load chậm)
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.evaluateJavascript(jsCode, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        android.util.Log.d("WebViewPayment", "JavaScript injected (3s), result: " + value);
                    }
                });
            }
        }, 3000);
    }
    
    // Method để xác nhận thanh toán QR Code (được gọi từ JavaScript)
    public void confirmVNPayQRCodePayment() {
        // Chạy trên UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Kiểm tra nếu đã xử lý rồi
                if (isPaymentProcessed) {
                    Toast.makeText(WebViewPaymentActivity.this, 
                        "Thanh toán đã được xử lý", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Show confirmation dialog
                new AlertDialog.Builder(WebViewPaymentActivity.this)
                    .setTitle("Xác nhận thanh toán")
                    .setMessage("Bạn đã thanh toán thành công bằng QR Code?\n\nNhấn 'Xác nhận' để hoàn tất giao dịch.")
                    .setPositiveButton("Xác nhận", (dialog, which) -> {
                        // Đánh dấu đã xử lý
                        isPaymentProcessed = true;
                        // Gọi API để update payment status
                        updatePaymentStatusToSuccess();
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .show();
            }
        });
    }
    
    // Update payment status thành SUCCESS
    private void updatePaymentStatusToSuccess() {
        // Lấy transactionId từ URL hiện tại hoặc từ Intent
        String transactionId = getTransactionIdFromUrl();
        
        if (transactionId == null || transactionId.isEmpty()) {
            // Nếu không có transactionId, dùng bookingId để tìm payment
            findPaymentByBookingId();
            return;
        }
        
        // Get payment status để có paymentId
        android.util.Log.d("WebViewPayment", "Getting payment status for transaction: " + transactionId);
        
        Call<PaymentResponseDto> statusCall = paymentApi.getPaymentStatus(transactionId);
        
        statusCall.enqueue(new Callback<PaymentResponseDto>() {
            @Override
            public void onResponse(Call<PaymentResponseDto> call, Response<PaymentResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int paymentId = response.body().getPaymentId();
                    updatePaymentStatus(paymentId, "SUCCESS", transactionId);
                } else {
                    findPaymentByBookingId();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponseDto> call, Throwable t) {
                android.util.Log.e("WebViewPayment", "Failed to get payment status", t);
                findPaymentByBookingId();
            }
        });
    }
    
    // Get transactionId từ URL hiện tại
    private String getTransactionIdFromUrl() {
        String url = webView.getUrl();
        if (url != null) {
            Uri uri = Uri.parse(url);
            // VNPay URL có thể chứa vnp_TxnRef trong query
            String txnRef = uri.getQueryParameter("vnp_TxnRef");
            if (txnRef != null && !txnRef.isEmpty()) {
                return txnRef;
            }
        }
        return null;
    }
    
    // Tìm payment bằng bookingId (fallback)
    private void findPaymentByBookingId() {
        if (bookingId == -1) {
            android.util.Log.e("WebViewPayment", "No bookingId available");
            showErrorAndFinish("Không thể xác định giao dịch thanh toán");
            return;
        }
        
        // Tạo payment mới với status SUCCESS
        createPaymentAndUpdateStatus();
    }
    
    // Tạo payment và update status
    private void createPaymentAndUpdateStatus() {
        CreatePaymentDto paymentDto = new CreatePaymentDto();
        paymentDto.setBookingId(bookingId);
        paymentDto.setPaymentMethod("VNPAY");
        paymentDto.setReturnUrl("flightbooking://payment/return");
        paymentDto.setCancelUrl("flightbooking://payment/cancel");
        
        Call<PaymentResponseDto> call = paymentApi.createPayment(paymentDto);
        
        call.enqueue(new Callback<PaymentResponseDto>() {
            @Override
            public void onResponse(Call<PaymentResponseDto> call, Response<PaymentResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int paymentId = response.body().getPaymentId();
                    String transactionId = response.body().getTransactionId();
                    updatePaymentStatus(paymentId, "SUCCESS", transactionId);
                } else {
                    showErrorAndFinish("Không thể tạo giao dịch thanh toán");
                }
            }

            @Override
            public void onFailure(Call<PaymentResponseDto> call, Throwable t) {
                android.util.Log.e("WebViewPayment", "Failed to create payment", t);
                showErrorAndFinish("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // Update payment status
    private void updatePaymentStatus(int paymentId, String status, String transactionId) {
        UpdatePaymentDto updateDto = new UpdatePaymentDto();
        updateDto.setStatus(status);
        updateDto.setNotes("Thanh toán VNPay QR Code - Đã xác nhận");
        
        Call<PaymentResponseDto> call = paymentApi.updatePayment(paymentId, updateDto);
        
        call.enqueue(new Callback<PaymentResponseDto>() {
            @Override
            public void onResponse(Call<PaymentResponseDto> call, Response<PaymentResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Gửi notification và navigate
                    sendPaymentSuccessNotification(transactionId);
                    navigateToPaymentResult("success", "Thanh toán VNPay QR Code thành công", transactionId);
                } else {
                    showErrorAndFinish("Không thể cập nhật trạng thái thanh toán");
                }
            }

            @Override
            public void onFailure(Call<PaymentResponseDto> call, Throwable t) {
                android.util.Log.e("WebViewPayment", "Failed to update payment status", t);
                showErrorAndFinish("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // Gửi notification thanh toán thành công
    private void sendPaymentSuccessNotification(String transactionId) {
        String channelId = "PaymentChannelId";
        String channelName = "Thông báo thanh toán";

        NotificationManager notificationManager = 
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, com.prm.flightbooking.PaymentResultActivity.class);
        intent.putExtra(com.prm.flightbooking.PaymentResultActivity.EXTRA_STATUS, "success");
        intent.putExtra(com.prm.flightbooking.PaymentResultActivity.EXTRA_BOOKING_ID, bookingId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, bookingId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Thanh toán thành công")
                .setContentText("Giao dịch " + transactionId + " đã được xác nhận")
                .setSmallIcon(R.drawable.ic_notifications)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Giao dịch " + transactionId + 
                            " đã được xác nhận thành công. Vé máy bay của bạn đã sẵn sàng."))
                .build();

        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
    
    // Navigate to PaymentResultActivity
    private void navigateToPaymentResult(String status, String message, String transactionId) {
        Intent resultIntent = new Intent(this, com.prm.flightbooking.PaymentResultActivity.class);
        resultIntent.putExtra(com.prm.flightbooking.PaymentResultActivity.EXTRA_STATUS, status);
        resultIntent.putExtra(com.prm.flightbooking.PaymentResultActivity.EXTRA_MESSAGE, message);
        resultIntent.putExtra(com.prm.flightbooking.PaymentResultActivity.EXTRA_BOOKING_ID, bookingId);
        resultIntent.putExtra("transactionId", transactionId);
        
        setResult(RESULT_OK, resultIntent);
        startActivity(resultIntent);
        finish();
    }
    
    // Show error và finish
    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}



