package co.kr.itforone.peertalk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

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
        Log.d("service_test","service1_oncreate");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("service_test","service1_ondestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
   /*     String temp = "";
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
        Log.d("service_test","service1_onStartCommand");*/


        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("service_test","service1_onUnbind");
        return super.onUnbind(intent);
    }
}
