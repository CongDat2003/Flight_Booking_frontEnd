package com.prm.flightbooking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.prm.flightbooking.R;

public class PaymentResultActivity extends AppCompatActivity {

    public static final String EXTRA_STATUS = "payment_status";
    public static final String EXTRA_MESSAGE = "payment_message";
    public static final String EXTRA_TRANSACTION_ID = "transaction_id";
    public static final String EXTRA_AMOUNT = "payment_amount";
    public static final String EXTRA_BOOKING_ID = "booking_id";

    private ImageView ivStatusIcon;
    private TextView tvStatusTitle, tvStatusMessage, tvTransactionId, tvAmount;
    private Button btnViewTicket, btnBackToHome;
    private LinearLayout layoutDetails;
    private View viewStatusBackground;

    private boolean isSuccess;
    private String message;
    private String transactionId;
    private String amount;
    private int bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        // Get data from intent
        Intent intent = getIntent();
        isSuccess = "success".equalsIgnoreCase(intent.getStringExtra(EXTRA_STATUS));
        message = intent.getStringExtra(EXTRA_MESSAGE);
        transactionId = intent.getStringExtra(EXTRA_TRANSACTION_ID);
        amount = intent.getStringExtra(EXTRA_AMOUNT);
        bookingId = intent.getIntExtra(EXTRA_BOOKING_ID, -1);

        // Initialize views
        initViews();
        
        // Setup UI based on payment result
        setupUI();
        
        // Start entrance animation
        startEntranceAnimation();
    }

    private void initViews() {
        ivStatusIcon = findViewById(R.id.iv_status_icon);
        tvStatusTitle = findViewById(R.id.tv_status_title);
        tvStatusMessage = findViewById(R.id.tv_status_message);
        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvAmount = findViewById(R.id.tv_amount);
        btnViewTicket = findViewById(R.id.btn_view_ticket);
        btnBackToHome = findViewById(R.id.btn_back_to_home);
        layoutDetails = findViewById(R.id.layout_details);
        viewStatusBackground = findViewById(R.id.view_status_background);
    }

    private void setupUI() {
        if (isSuccess) {
            // Success UI
            ivStatusIcon.setImageResource(R.drawable.ic_check_circle);
            ivStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.success_green));
            tvStatusTitle.setText("Thanh toán thành công!");
            tvStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.success_green));
            viewStatusBackground.setBackgroundColor(ContextCompat.getColor(this, R.color.success_light));
            
            btnViewTicket.setVisibility(View.VISIBLE);
            btnViewTicket.setText("Xem vé");
            btnViewTicket.setBackgroundColor(ContextCompat.getColor(this, R.color.success_green));
            
        } else {
            // Failed UI
            ivStatusIcon.setImageResource(R.drawable.ic_error_circle);
            ivStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.error_red));
            tvStatusTitle.setText("Thanh toán thất bại");
            tvStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.error_red));
            viewStatusBackground.setBackgroundColor(ContextCompat.getColor(this, R.color.error_light));
            
            btnViewTicket.setVisibility(View.GONE);
        }

        // Set message
        if (message != null && !message.isEmpty()) {
            tvStatusMessage.setText(message);
        } else {
            tvStatusMessage.setText(isSuccess ? 
                "Vé máy bay của bạn đã được xác nhận thành công." : 
                "Có lỗi xảy ra trong quá trình thanh toán. Vui lòng thử lại.");
        }

        // Set transaction details
        if (transactionId != null && !transactionId.isEmpty()) {
            tvTransactionId.setText("Mã giao dịch: " + transactionId);
            tvTransactionId.setVisibility(View.VISIBLE);
        } else {
            tvTransactionId.setVisibility(View.GONE);
        }

        if (amount != null && !amount.isEmpty()) {
            tvAmount.setText("Số tiền: " + amount + " VND");
            tvAmount.setVisibility(View.VISIBLE);
        } else {
            tvAmount.setVisibility(View.GONE);
        }

        // Setup button actions
        btnViewTicket.setOnClickListener(v -> {
            if (bookingId != -1) {
                Intent intent = new Intent(this, BookingDetailActivity.class);
                intent.putExtra("bookingId", bookingId);
                startActivity(intent);
            }
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void startEntranceAnimation() {
        // Hide views initially
        ivStatusIcon.setAlpha(0f);
        tvStatusTitle.setAlpha(0f);
        tvStatusMessage.setAlpha(0f);
        layoutDetails.setAlpha(0f);
        btnViewTicket.setAlpha(0f);
        btnBackToHome.setAlpha(0f);

        // Animate icon scale and fade in
        ObjectAnimator iconScaleX = ObjectAnimator.ofFloat(ivStatusIcon, "scaleX", 0f, 1f);
        ObjectAnimator iconScaleY = ObjectAnimator.ofFloat(ivStatusIcon, "scaleY", 0f, 1f);
        ObjectAnimator iconAlpha = ObjectAnimator.ofFloat(ivStatusIcon, "alpha", 0f, 1f);
        
        iconScaleX.setDuration(600);
        iconScaleY.setDuration(600);
        iconAlpha.setDuration(600);
        iconScaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        iconScaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        // Animate title fade in
        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(tvStatusTitle, "alpha", 0f, 1f);
        titleAlpha.setDuration(400);
        titleAlpha.setStartDelay(200);

        // Animate message fade in
        ObjectAnimator messageAlpha = ObjectAnimator.ofFloat(tvStatusMessage, "alpha", 0f, 1f);
        messageAlpha.setDuration(400);
        messageAlpha.setStartDelay(400);

        // Animate details fade in
        ObjectAnimator detailsAlpha = ObjectAnimator.ofFloat(layoutDetails, "alpha", 0f, 1f);
        detailsAlpha.setDuration(400);
        detailsAlpha.setStartDelay(600);

        // Animate buttons fade in
        ObjectAnimator buttonAlpha = ObjectAnimator.ofFloat(btnViewTicket, "alpha", 0f, 1f);
        ObjectAnimator backButtonAlpha = ObjectAnimator.ofFloat(btnBackToHome, "alpha", 0f, 1f);
        buttonAlpha.setDuration(400);
        backButtonAlpha.setDuration(400);
        buttonAlpha.setStartDelay(800);
        backButtonAlpha.setStartDelay(800);

        // Start animations
        iconScaleX.start();
        iconScaleY.start();
        iconAlpha.start();
        titleAlpha.start();
        messageAlpha.start();
        detailsAlpha.start();
        buttonAlpha.start();
        backButtonAlpha.start();

        // Add bounce effect to icon
        new Handler().postDelayed(() -> {
            ObjectAnimator bounceX = ObjectAnimator.ofFloat(ivStatusIcon, "scaleX", 1f, 1.1f, 1f);
            ObjectAnimator bounceY = ObjectAnimator.ofFloat(ivStatusIcon, "scaleY", 1f, 1.1f, 1f);
            bounceX.setDuration(200);
            bounceY.setDuration(200);
            bounceX.start();
            bounceY.start();
        }, 600);
    }

    @Override
    public void onBackPressed() {
        // Navigate to home instead of previous activity
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}



