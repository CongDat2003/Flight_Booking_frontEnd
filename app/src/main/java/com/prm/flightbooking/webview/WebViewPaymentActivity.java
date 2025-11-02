package com.prm.flightbooking.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prm.flightbooking.R;

public class WebViewPaymentActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "extra_url";
    public static final String RETURN_SCHEME = "flightbooking";
    private static final String VNPAY_RETURN_PATH = "/api/payment/vnpay-return";

    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        webView = findViewById(R.id.payment_webview);
        progressBar = findViewById(R.id.payment_progress);

        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url == null) {
            finish();
            return;
        }

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
                    } else {
                        android.util.Log.w("WebViewPayment", "VNPay return URL but no response code - might be loading");
                    }
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
    
    private void parsePaymentResult(Uri uri) {
        try {
            android.util.Log.d("WebViewPayment", "=== parsePaymentResult ===");
            android.util.Log.d("WebViewPayment", "Full URI: " + uri.toString());
            
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
            
            Intent resultIntent = new Intent();
            resultIntent.putExtra("status", status);
            resultIntent.putExtra("message", message);
            resultIntent.putExtra("transactionId", transactionId);
            resultIntent.putExtra("responseCode", responseCode);
            if (amount != null) {
                resultIntent.putExtra("amount", amount);
            }
            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            android.util.Log.e("WebViewPayment", "Error parsing payment result", e);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("status", "failed");
            resultIntent.putExtra("message", "Lỗi xử lý kết quả thanh toán: " + e.getMessage());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}



