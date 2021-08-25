package co.kr.itforone.peertalk;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.util.List;

import co.kr.itforone.peertalk.Util.Dialog_manager;
import co.kr.itforone.peertalk.retrofit.RetrofitAPI;
import co.kr.itforone.peertalk.retrofit.RetrofitHelper;
import co.kr.itforone.peertalk.retrofit.responseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void shownoti(String name, String numbers, String type){

        RemoteViews remoteViews = new RemoteViews(context_public.getPackageName(), R.layout.dialog_alarm);
        remoteViews.setTextViewText(R.id.tv_type, type);
        remoteViews.setTextViewText(R.id.tv_name,  name);
        remoteViews.setTextViewText(R.id.tv_number,  numbers);
        String channelId = "peertalk";
        Intent intent = new Intent(context_public.getApplicationContext(),Calling.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setClass(this, MainActivity.class);

        PendingIntent fullscreen = PendingIntent.getActivity(context_public.getApplicationContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context_public.getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                        .setContentTitle(type)
                        .setContentText(numbers)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(remoteViews)
                      //  .setContent(remoteViews)
                       // .setCustomBigContentView(remoteViews)
                        .setFullScreenIntent(fullscreen,true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);


        NotificationManager notificationManager =
                (NotificationManager) context_public.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "peertalk",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

        }

        notificationManager.notify(3001 /* ID of notification */, notificationBuilder.build());

    }

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

                                    SharedPreferences pref = context.getSharedPreferences("logininfo", context.MODE_PRIVATE);
                                    String mb_id = pref.getString("id", "");


                                    if(mb_id!=null && !mb_id.isEmpty()) {

                                        RetrofitAPI networkService = RetrofitHelper.getRetrofit().create(RetrofitAPI.class);
                                        Call<responseModel> call = networkService.getList(mb_id, phoneNumber_extra);
                                        call.enqueue(new Callback<responseModel>() {
                                            @Override
                                            public void onResponse(Call<responseModel> call, Response<responseModel> response) {
                                                if(response.isSuccessful()){

                                                    responseModel responsemodel = response.body();
                                                    Log.d("test_call", responsemodel.getWr_subject());
                                                    if(responsemodel.getWr_subject()!=null && !responsemodel.getWr_subject().equals("test_subject")) {

                                                        shownoti(responsemodel.getWr_subject(), responsemodel.getWr_content(),"수신중...");

/*
                                                        serviceIntent.putExtra("name", responsemodel.getWr_subject());
                                                        serviceIntent.putExtra("number", phoneNumber_extra);
                                                        serviceIntent.putExtra("type", "수신 중 ...");
                                                        context_public.startActivity(serviceIntent);
*/
                                                    }

                                                }
                                                else{
                                                    Log.d("result_call_fail",String.valueOf(response.isSuccessful()));
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<responseModel> call, Throwable t) {
                                                Log.d("result_call_fail",t.toString());
                                            }
                                        });
                                    }
                                }

                                break;
                            case TelephonyManager.CALL_STATE_IDLE:

                                //Toast.makeText(context_public.getApplicationContext(), "현재 " + phoneNumber_extra + " 번호로 통화가 종료되었습니다.", Toast.LENGTH_LONG).show();
                                Log.d("test_call", "disconnect");
                                break;
                            case TelephonyManager.CALL_STATE_OFFHOOK:
                                if(incomming_flg==0)
                                {
//  주소록 동기화
                                    SharedPreferences pref = context.getSharedPreferences("logininfo", context.MODE_PRIVATE);
                                    String mb_id = pref.getString("id", "");


                                    if(mb_id!=null && !mb_id.isEmpty()) {

                                        RetrofitAPI networkService = RetrofitHelper.getRetrofit().create(RetrofitAPI.class);
                                        Call<responseModel> call = networkService.getList(mb_id, phoneNumber_extra);
                                        call.enqueue(new Callback<responseModel>() {
                                            @Override
                                            public void onResponse(Call<responseModel> call, Response<responseModel> response) {
                                                if(response.isSuccessful()){

                                                    responseModel responsemodel = response.body();

                                                    if(responsemodel.getWr_subject()!=null && !responsemodel.getWr_subject().equals("test_subject")) {

                                                       // shownoti(responsemodel.getWr_subject(),phoneNumber_extra,"발신중...");

                                                        serviceIntent.putExtra("name", responsemodel.getWr_subject());
                                                        serviceIntent.putExtra("number", phoneNumber_extra);
                                                        serviceIntent.putExtra("type", "발신 중 ...");
                                                        context_public.startActivity(serviceIntent);
                                                    }
                                                }
                                                else{
                                                    Log.d("result_call_fail",String.valueOf(response.isSuccessful()));
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<responseModel> call, Throwable t) {
                                                Log.d("result_call_fail",t.toString());
                                            }
                                        });


                                    }
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

