package com.prm.flightbooking.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prm.flightbooking.R;

public class PaymentMethodSelectionDialog {
    
    public interface PaymentMethodListener {
        void onPaymentMethodSelected(String paymentMethod);
        void onQRCodePayment();
    }
    
    public static void show(Context context, PaymentMethodListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_payment_method_selection, null);
        
        builder.setView(dialogView);
        builder.setTitle("Chọn phương thức thanh toán");
        
        // Bind views
        View momoOption = dialogView.findViewById(R.id.option_momo);
        View zalopayOption = dialogView.findViewById(R.id.option_zalopay);
        View vnpayOption = dialogView.findViewById(R.id.option_vnpay);
        View qrOption = dialogView.findViewById(R.id.option_qr);
        
        ImageView momoIcon = dialogView.findViewById(R.id.icon_momo);
        ImageView zalopayIcon = dialogView.findViewById(R.id.icon_zalopay);
        ImageView vnpayIcon = dialogView.findViewById(R.id.icon_vnpay);
        ImageView qrIcon = dialogView.findViewById(R.id.icon_qr);
        
        TextView momoText = dialogView.findViewById(R.id.text_momo);
        TextView zalopayText = dialogView.findViewById(R.id.text_zalopay);
        TextView vnpayText = dialogView.findViewById(R.id.text_vnpay);
        TextView qrText = dialogView.findViewById(R.id.text_qr);
        
        // Set icons and text
        momoIcon.setImageResource(R.drawable.ic_momo);
        zalopayIcon.setImageResource(R.drawable.ic_zalopay);
        vnpayIcon.setImageResource(R.drawable.ic_vnpay);
        qrIcon.setImageResource(R.drawable.ic_qr_code);
        
        momoText.setText("MoMo");
        zalopayText.setText("ZaloPay");
        vnpayText.setText("VNPay");
        qrText.setText("QR Code");
        
        AlertDialog dialog = builder.create();
        
        // Set click listeners
        momoOption.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onPaymentMethodSelected("MOMO");
        });
        
        zalopayOption.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onPaymentMethodSelected("ZALOPAY");
        });
        
        vnpayOption.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onPaymentMethodSelected("VNPAY");
        });
        
        qrOption.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onQRCodePayment();
        });
        
        dialog.show();
    }
}


