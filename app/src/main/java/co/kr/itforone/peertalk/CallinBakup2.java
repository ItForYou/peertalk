package co.kr.itforone.peertalk;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class CallinBakup2 extends BroadcastReceiver {

    WindowManager.LayoutParams params;
    private WindowManager windowManager;
    protected View rootView;
    private Context context_public;
    Intent serviceIntent;
    NotificationManager notificationManager ;
    Intent notificationIntent;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationIntent= new Intent(context, MainActivity.class);
        context_public = context;
        String action = intent.getAction();
        Log.d("test_call", action);
        serviceIntent = new Intent(context,DialogActivity.class);
        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if(action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){

            String savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Log.d("test_call", "outcall_"+savedNumber);

//            Intent serviceIntent = new Intent(context,Calling.class);
//            serviceIntent.putExtra("number", savedNumber);
//            context.startService(serviceIntent);

//            Intent sendbroad= new Intent();
//            sendbroad.setAction("Test_alert"); //"aaa"라는 액션 방송을 듣는 모든 리시버 반응해라.
//            context.sendBroadcast(sendbroad);


//            serviceIntent.putExtra("number", savedNumber);
//            serviceIntent.putExtra("type", "발신 중 ...");
//            context_public.startActivity(serviceIntent);

            NotificationSomethings(savedNumber);

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
                                NotificationSomethings(phoneNumber_extra);
                                break;
                            case TelephonyManager.CALL_STATE_IDLE:
                                Toast.makeText(context_public.getApplicationContext(), "현재 " + phoneNumber_extra + " 번호로 통화가 종료되었습니다.", Toast.LENGTH_LONG).show();
                                Log.d("test_call", "disconnect");
                                break;
                            case TelephonyManager.CALL_STATE_OFFHOOK:
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

    public void NotificationSomethings(String Number) {

        notificationIntent.putExtra("notificationId", 1); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(context_public, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context_public, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(context_public.getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle("테스트이름")
                .setContentText(Number)
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴

    }

}

