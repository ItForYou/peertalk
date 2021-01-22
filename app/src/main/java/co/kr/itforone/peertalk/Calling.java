package co.kr.itforone.peertalk;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Calling extends Service {
    private WindowManager.LayoutParams mParams;  //layout params 객체. 뷰의 위치 및 크기
    private WindowManager mWindowManager;
    private WindowManager windowManager;
    protected View rootView;
    String number;
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("service_test","service1_onbind");
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("service_test","service1_ondestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String temp = "";
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(new PhoneStateListener(){

            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                String phoneNumber_extra = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //Log.d("service_test", phoneNumber_extra);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);

        if(intent !=null) {
             temp = intent.getStringExtra("number");
        }
        if(temp!=null && !temp.isEmpty()) {
            Log.d("service_test", temp);
        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
