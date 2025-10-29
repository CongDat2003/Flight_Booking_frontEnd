package com.prm.flightbooking.webview;

import android.annotation.SuppressLint;
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
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (RETURN_SCHEME.equalsIgnoreCase(uri.getScheme())) {
                    // Parse resultCode if any: flightbooking://payment/return?code=00
                    String result = uri.getQueryParameter("vnp_ResponseCode");
                    String status = "failed";
                    if ("00".equals(result)) status = "success";
                    getIntent().putExtra("status", status);
                    setResult(RESULT_OK, getIntent());
                    finish();
                    return true;
                }
                // Handle VNPay HTTP(S) ReturnUrl (backend endpoint) inside WebView
                if (("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                        && uri.getPath() != null && uri.getPath().contains(VNPAY_RETURN_PATH)) {
                    String result = uri.getQueryParameter("vnp_ResponseCode");
                    String status = "failed";
                    if ("00".equals(result)) status = "success";
                    getIntent().putExtra("status", status);
                    setResult(RESULT_OK, getIntent());
                    finish();
                    return true;
                }
                // Handle target=_blank by loading within this WebView
                view.loadUrl(uri.toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.loadUrl(url);
    }
}



