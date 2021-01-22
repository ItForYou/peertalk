package co.kr.itforone.peertalk;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.List;

import co.kr.itforone.peertalk.Util.Dialog_manager;
import co.kr.itforone.peertalk.databinding.ActivityMainBinding;
import co.kr.itforone.peertalk.retrofit.RetrofitAPI;
import co.kr.itforone.peertalk.retrofit.RetrofitHelper;
import co.kr.itforone.peertalk.retrofit.responseModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class Callin extends BroadcastReceiver {
    WindowManager.LayoutParams params;
    private WindowManager windowManager;
    protected View rootView;
    private Context context_public;
    Intent serviceIntent;
    private  int incomming_flg = 0;
    private Dialog_manager am = Dialog_manager.getInstance();
    private int running_flg = 0;
    private RetrofitAPI retrofitAPI;
    @Override
    public void onReceive(Context context, Intent intent) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> proceses = am.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo process : proceses){

            if(process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){

                if(process.processName.equals(context.getPackageName())){
                    running_flg=1;
                }
                else{
                    running_flg=0;
                }
            }

        }

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
            incomming_flg=0;

        }

        else {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber_extra = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);


            PhoneStateListener phoneStateListener = new PhoneStateListener(){

                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    super.onCallStateChanged(state, phoneNumber);
                    Log.d("test_call_number", String.valueOf(state));
                    if(phoneNumber_extra!=null && !phoneNumber_extra.isEmpty()) {
                        switch (state) {

                            case TelephonyManager.CALL_STATE_RINGING:
                                //Toast.makeText(context_public.getApplicationContext(), "현재 " + phoneNumber_extra + " 번호로 통화가 오는중입니다.", Toast.LENGTH_LONG).show();
                                Log.d("test_call", "ringing");
                                incomming_flg=1;
                                if(running_flg==0) {
                                    serviceIntent.putExtra("number", phoneNumber_extra);
                                    serviceIntent.putExtra("type", "수신 중 ...");
                                    context_public.startActivity(serviceIntent);
                                }

                                break;
                            case TelephonyManager.CALL_STATE_IDLE:

                                //Toast.makeText(context_public.getApplicationContext(), "현재 " + phoneNumber_extra + " 번호로 통화가 종료되었습니다.", Toast.LENGTH_LONG).show();
                                Log.d("test_call", "disconnect");
                                break;
                            case TelephonyManager.CALL_STATE_OFFHOOK:
                                if(incomming_flg==0)
                                {
                                    String savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                                    serviceIntent.putExtra("number", phoneNumber_extra);
                                    serviceIntent.putExtra("type", "발신 중 ...");
                                    context_public.startActivity(serviceIntent);
                                }

//                                serviceIntent.putExtra("number", phoneNumber_extra);
//                                serviceIntent.putExtra("type", "수신 중 ...");
//                                context_public.startActivity(serviceIntent);
                                Log.d("test_call", "connect");
                                break;

                        }
                    }
                }
            };

            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);



        }
    }
}

