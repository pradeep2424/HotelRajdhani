package com.miracle.rajdhani.broadcastReceiver;//package com.miracle.dronam.broadcastReceiver;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import com.google.android.gms.auth.api.phone.SmsRetriever;
//import com.google.android.gms.common.api.CommonStatusCodes;
//import com.google.android.gms.common.api.Status;
//import com.miracle.dronam.listeners.OTPListener;
//
//public class MySMSBroadcastReceiver extends BroadcastReceiver {
//    private static final String TAG = "SmsBroadcastReceiver";
//    OTPListener otpListener = null;
//
//    public void setOnOtpListeners(OTPListener otpListener) {
//        this.otpListener = otpListener;
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
//            Bundle extras = intent.getExtras();
//            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
//
//            switch(status.getStatusCode()) {
//                case CommonStatusCodes.SUCCESS:
//                    // Get SMS message contents
//                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
//                    // Extract one-time code from the message and complete verification
//                    // by sending the code back to your server.
//                    Log.d(TAG, "onReceive: failure "+message);
//                    if (otpListener != null) {
//                        String otp = message.replace("<#> Your otp code is : ", "");
//                        otpListener.onOtpReceived(otp);
//                    }
//                    break;
//                case CommonStatusCodes.TIMEOUT:
//                    // Waiting for SMS timed out (5 minutes)
//                    // Handle the error ...
//                    if (otpListener != null) {
//                        otpListener.onOtpTimeout();
//                    }
//                    break;
//            }
//        }
//    }
//}
