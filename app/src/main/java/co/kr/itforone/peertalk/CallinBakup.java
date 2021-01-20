package co.kr.itforone.peertalk;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class CallinBakup extends BroadcastReceiver {
    WindowManager.LayoutParams params;
    private WindowManager windowManager;
    protected View rootView;
    private Context context_public;
    Intent serviceIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        context_public = context;
        String action = intent.getAction();
        Log.d("test_call", action);
        serviceIntent = new Intent(context,DialogActivity.class);
        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if(action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){

            String savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Log.d("test_call", "outcall_"+savedNumber);

        /*    Intent serviceIntent = new Intent(context,Calling.class);
            serviceIntent.putExtra("number", savedNumber);
            context.startService(serviceIntent);*/


            serviceIntent.putExtra("number", savedNumber);
            serviceIntent.putExtra("type", "발신 중 ...");
            context_public.startActivity(serviceIntent);

        }


        else {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber_extra = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (phoneNumber_extra != null && !phoneNumber_extra.isEmpty()) {
                telephonyManager.listen(new PhoneStateListener() {
                    @Override
                    public void onCallStateChanged(int state, String phoneNumber) {
                        super.onCallStateChanged(state, phoneNumber);
                        Log.d("test_call_number", String.valueOf(state));
                        switch (state) {

                            case TelephonyManager.CALL_STATE_RINGING:
                                Toast.makeText(context_public.getApplicationContext(), "현재 " + phoneNumber_extra + " 번호로 통화가 오는중입니다.", Toast.LENGTH_LONG).show();
                                Log.d("test_call", "ringing");
                                serviceIntent.putExtra("number", phoneNumber_extra);
                                serviceIntent.putExtra("type", "수신 중 ...");
                                context_public.startActivity(serviceIntent);
                                Log.d("test_call", "ringing2");
                                break;
                            case TelephonyManager.CALL_STATE_IDLE:
                                Toast.makeText(context_public.getApplicationContext(), "현재 " + phoneNumber_extra + " 번호로 통화가 종료되었습니다.", Toast.LENGTH_LONG).show();
                                Log.d("test_call", "disconnect");
                                break;
                            case TelephonyManager.CALL_STATE_OFFHOOK:
                                serviceIntent.putExtra("number", phoneNumber_extra);
                                serviceIntent.putExtra("type", "수신 중 ...");
                                context_public.startActivity(serviceIntent);
                                Log.d("test_call", "connect");
                                break;
                        }
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE);
            }
            else{
                Log.d("test_call", "number_null!!");
            }
        }
    }
}

