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
        View zalopayOption = dialogView.findViewById(R.id.option_zalopay);
        View vnpayOption = dialogView.findViewById(R.id.option_vnpay);
        View momoOption = dialogView.findViewById(R.id.option_momo);
        View qrOption = dialogView.findViewById(R.id.option_qr);
        
        ImageView zalopayIcon = dialogView.findViewById(R.id.icon_zalopay);
        ImageView vnpayIcon = dialogView.findViewById(R.id.icon_vnpay);
        ImageView momoIcon = dialogView.findViewById(R.id.icon_momo);
        ImageView qrIcon = dialogView.findViewById(R.id.icon_qr);
        
        TextView zalopayText = dialogView.findViewById(R.id.text_zalopay);
        TextView vnpayText = dialogView.findViewById(R.id.text_vnpay);
        TextView momoText = dialogView.findViewById(R.id.text_momo);
        TextView qrText = dialogView.findViewById(R.id.text_qr);
        
        // Set icons and text
        if (zalopayIcon != null) zalopayIcon.setImageResource(R.drawable.ic_zalopay);
        if (vnpayIcon != null) vnpayIcon.setImageResource(R.drawable.ic_vnpay_simple);
        if (momoIcon != null) momoIcon.setImageResource(R.drawable.ic_momo);
        if (qrIcon != null) qrIcon.setImageResource(R.drawable.ic_qr_code);
        
        if (zalopayText != null) zalopayText.setText("ZaloPay");
        if (vnpayText != null) vnpayText.setText("VNPay");
        if (momoText != null) momoText.setText("MoMo");
        if (qrText != null) qrText.setText("QR Code");
        
        AlertDialog dialog = builder.create();
        
        // Set click listeners
        // ZaloPay và MoMo đang bảo trì
        if (zalopayOption != null) {
            zalopayOption.setOnClickListener(v -> {
                Toast.makeText(context, "ZaloPay đang được bảo trì. Vui lòng chọn phương thức thanh toán khác.", Toast.LENGTH_LONG).show();
                // Không dismiss dialog để user có thể chọn phương thức khác
            });
        }
        
        if (momoOption != null) {
            momoOption.setOnClickListener(v -> {
                Toast.makeText(context, "MoMo đang được bảo trì. Vui lòng chọn phương thức thanh toán khác.", Toast.LENGTH_LONG).show();
                // Không dismiss dialog để user có thể chọn phương thức khác
            });
        }
        
        // VNPay hoạt động bình thường
        if (vnpayOption != null) {
            vnpayOption.setOnClickListener(v -> {
                dialog.dismiss();
                listener.onPaymentMethodSelected("VNPAY");
            });
        }
        
        // QR Code
        if (qrOption != null) {
            qrOption.setOnClickListener(v -> {
                dialog.dismiss();
                listener.onQRCodePayment();
            });
        }
        
        dialog.show();
    }
}


