package co.kr.itforone.peertalk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhihu.matisse.Matisse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.kr.itforone.peertalk.Util.Dialog_manager;
import co.kr.itforone.peertalk.contact_pkg.itemModel;
import co.kr.itforone.peertalk.databinding.ActivityMainBinding;
import co.kr.itforone.peertalk.retrofit.CalllogAPI;
import co.kr.itforone.peertalk.retrofit.ContactAPI;
import co.kr.itforone.peertalk.retrofit.RetrofitAPI;
import co.kr.itforone.peertalk.retrofit.calllogModel;
import co.kr.itforone.peertalk.retrofit.chkTotalAPI;
import co.kr.itforone.peertalk.retrofit.chkTotalModel;
import co.kr.itforone.peertalk.retrofit.contactModel;
import co.kr.itforone.peertalk.retrofit.responseModel;
import co.kr.itforone.peertalk.retrofit.RetrofitHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding  activityMainBinding;
    String[] PERMISSIONS = {

            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.MANAGE_OWN_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,


            //Manifest.permission.SEND_SMS

    };

    String tv_total ="";

    static final int PERMISSION_REQUEST_CODE = 1;
    static final int RECEIVED_CONTATSLIST = 2;
    static final int REQ_CODE_OVERLAY_PERMISSION = 3;
    private static int chkeck_permission= 0, flg_ringing=0, flg_offhook=0;
    public  static int flg_dialog_main =0;
    WebSettings settings;
    private long backPrssedTime = 0;
    public static BroadcastReceiver receiver;
    public int stateprog = View.GONE;
    private Dialog_manager dm = Dialog_manager.getInstance();

    //파일첨부(프로필사진)
    ValueCallback<Uri[]> filePathCallbackLollipop;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;

    int called_state=0, choosehp_state=0, movefromcalled=0, movefromchoosehp=0;
    String user_id, user_pwd;



    private boolean hasPermissions(String[] permissions){
        // 퍼미션 확인해
        int result = -1;
        for (int i = 0; i < permissions.length; i++) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]);
            if(result!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        Log.d("per_result",String.valueOf(result));
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else {
            return false;
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions(PERMISSIONS)){
                 //   ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
                }else{

                }
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("stop_flg","true");
        if(chkeck_permission==1){
          //  finishAndRemoveTask();
        }
    }

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        if(value!=null)
        Log.d("bindingadapter",value.toString());
        //view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("stop_flg","false");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setDatamain(this);
        activityMainBinding.setSale(false);

        //shownoti("test","tset2","test3");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                chkeck_permission=1;
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
            }
        }

        if(chkeck_permission==1){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }

        //Log.d("service_call","DIALOGON");

        IntentFilter filter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String phoneNumber_extra = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                String action = intent.getAction();
                Log.d("Main_action",action);

                PhoneStateListener phoneStateListener = new PhoneStateListener(){

                    @Override
                    public void onCallStateChanged(int state, String phoneNumber) {
                        super.onCallStateChanged(state, phoneNumber);

                        if(phoneNumber_extra!=null && !phoneNumber_extra.isEmpty()) {
                            Log.d("test_call_number_main", phoneNumber_extra);
                        }

                        if(phoneNumber_extra!=null && !phoneNumber_extra.isEmpty()) {
                            switch (state) {

                                case TelephonyManager.CALL_STATE_RINGING:
                                    Log.d("chk_init", "CALL_STATE_RINGING");
                                    flg_ringing=1;
                                    //Toast.makeText(context_public.getApplicationContext(), "현재 " + phoneNumber_extra + " 번호로 통화가 오는중입니다.", Toast.LENGTH_LONG).show();
                                    if(phoneNumber_extra!=null && !phoneNumber_extra.isEmpty() && flg_dialog_main==0) {


                                        Log.d("test_call", "ringing_main");
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                SharedPreferences pref = context.getSharedPreferences("logininfo", context.MODE_PRIVATE);
                                                String mb_id = pref.getString("id", "");


                                                if(mb_id!=null && !mb_id.isEmpty()) {

                                                    RetrofitAPI networkService = RetrofitHelper.getRetrofit().create(RetrofitAPI.class);
                                                    Call<responseModel> call = networkService.getList(mb_id, phoneNumber_extra);
                                                    call.enqueue(new Callback<responseModel>() {
                                                        @Override
                                                        public void onResponse(Call<responseModel> call, retrofit2.Response<responseModel> response) {
                                                            if(response.isSuccessful()){

                                                                responseModel responsemodel = response.body();

                                                                if(responsemodel.getWr_subject()!=null && !responsemodel.getWr_subject().equals("test_subject")) {

                                                                 /*   Intent dialog_intent = new Intent(MainActivity.this, DialogActivity.class);
                                                                    dialog_intent.putExtra("number", phoneNumber_extra);
                                                                    dialog_intent.putExtra("type", "수신 중 ...");
                                                                    dialog_intent.putExtra("name", responsemodel.getWr_subject());
                                                                    dialog_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(dialog_intent);*/
                                                                    String channelId = "peertalk";
                                                                    shownoti(responsemodel.getWr_subject(),responsemodel.getWr_content(),"수신중 ...");

                                                                    Log.d("result_call_fail_",String.valueOf(response.isSuccessful()));

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
                                        }, 500); //딜레이 타임 조절
                                    }
                                    break;
                                case TelephonyManager.CALL_STATE_IDLE:
                                    if(flg_offhook!=1 && flg_ringing==1) {
                                       // SmsManager sms = SmsManager.getDefault();
                                       // sms.sendTextMessage(phoneNumber_extra, null, phoneNumber_extra+"번호로 부재중통화일 시 보내고 싶은 메세지 입니다.", null, null);
                                    }
                                    else {
                                       // SmsManager sms = SmsManager.getDefault();
                                       //  sms.sendTextMessage(phoneNumber_extra, null, phoneNumber_extra+"번호로 통화종료시 보내고 싶은 메세지 입니다.", null, null);
                                    }
                                    flg_offhook=0;
                                    flg_ringing=0;
                                    //Toast.makeText(context_public.getApplicationContext(), "현재 " + phoneNumber_extra + " 번호로 통화가 종료되었습니다.", Toast.LENGTH_LONG).show();
                                    Log.d("test_call", "disconnect");
                                    Log.d("chk_init", "CALL_STATE_IDLE");

                                    break;
                                case TelephonyManager.CALL_STATE_OFFHOOK:
                                    flg_offhook=1;
//                                serviceIntent.putExtra("number", phoneNumber_extra);
//                                serviceIntent.putExtra("type", "수신 중 ...");
//                                context_public.startActivity(serviceIntent);
                                    Log.d("test_call", "connect");
                                    Log.d("chk_init", "CALL_STATE_OFFHOOK");
                                    break;
                            }
                        }
                    }
                };

                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);

            }
        };
        this.registerReceiver(receiver, filter);

      //  Intent serviceintent = new Intent( this, Calling.class );
     //  startService( serviceintent );



        settings = activityMainBinding.mwebview.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setTextZoom(100);

        activityMainBinding.mwebview.addJavascriptInterface(new WebviewJavainterface(this),"Android");
        activityMainBinding.mwebview.setWebViewClient(new Viewmanager(this,this));
        activityMainBinding.mwebview.setWebChromeClient(new WebchromeClient(this, this));

        SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
        user_id = pref.getString("id", "");
        user_pwd = pref.getString("pwd", "");


        if(!user_id.isEmpty() && !user_pwd.isEmpty()){
            activityMainBinding.mwebview.loadUrl(getString(R.string.loginchk) + "mb_id=" + user_id + "&mb_password=" + user_pwd);
        }
        else{
            activityMainBinding.mwebview.loadUrl(getString(R.string.login));
        }

        /*Intent i = new Intent(MainActivity.this, DialogActivity.class);
        startActivity(i);
        */

    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPrssedTime;

        if(!activityMainBinding.mwebview.canGoBack() || activityMainBinding.mwebview.getUrl().contains("all_contact") ||
                activityMainBinding.mwebview.getUrl().contains("login.php")
        ) {
            if (0 <= intervalTime && 2000 >= intervalTime) {
                finishAndRemoveTask();
            } else {
                backPrssedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            activityMainBinding.mwebview.goBack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case RECEIVED_CONTATSLIST:
                if(data!=null){
                /*    int print_size = data.getIntExtra("size",0);
                    String names = data.getStringExtra("names");
                    String numbers = data.getStringExtra("numbers");

                    SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
                    String id = pref.getString("id", "");
                    Log.d("mb_id", id);
                    Log.d("activityresult", names);
                    Log.d("activityresult", numbers);
                    com.android.volley.Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                activityMainBinding.mwebview.reload();
                            } catch (Exception e) {
                                Log.d("volley_result", e.toString());
                                e.printStackTrace();
                            }
                        }
                    };
                    ReqeustInsert requestOfferwall = new ReqeustInsert(names,numbers, id, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(requestOfferwall);
*/
                  /*  Map map=new HashMap();
                    map.put("names",names);
                    map.put("numbers",numbers);

                    RetrofitAPI networkService = RetrofitHelper.getApiService();
                    Call<responseModel> call = networkService.getList(map);

                    call.enqueue(new Callback<responseModel>() {
                        @Override
                        public void onResponse(Call<responseModel> call, Response<responseModel> response) {
                            if(response.isSuccessful()){
                                //Log.d("result_call_init",String.valueOf(response.isSuccessful()));
                                responseModel tmpItModels = response.body();
                                Log.d("result_call_suc",tmpItModels.getResult());
                            }
                            else{
                                Log.d("result_call_fail",String.valueOf(response.isSuccessful()));
                            }
                        }

                        @Override
                        public void onFailure(Call<responseModel> call, Throwable t) {
                            Log.d("result_call_fail",t.toString());
                        }
                    });*/
                }
                break;
            case REQ_CODE_OVERLAY_PERMISSION:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        chkeck_permission=1;
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
                    }
                }

                break;

            case FILECHOOSER_LOLLIPOP_REQ_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    if (resultCode == RESULT_OK && activityMainBinding.mwebview.getUrl().contains("contact.php?")){
                        if (data != null) {
                            //String dataString = data.getDataString();
                            //  ClipData clipData = data.getClipData();
                            // Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                            List<Uri> matisse = Matisse.obtainResult(data);
                            //   Log.d("mselected_stcrop",  matisse.get(0).toString());
                            CropImage.activity(matisse.get(0))
                                    .setAspectRatio(1,1)//가로 세로 1:1로 자르기 기능 * 1:1 4:3 16:9로 정해져 있어요
                                    .setCropShape(CropImageView.CropShape.OVAL)
                                    .start(this);

//                        if (clipData != null) {
//                            result = new Uri[clipData.getItemCount()];
//                            for (int i = 0; i < clipData.getItemCount(); i++) {
//                                ClipData.Item item = clipData.getItemAt(i);
//                                result[i] = item.getUri();
//                            }
//                        }
//                        else {
//                            result = ChromeManager.FileChooserParams.parseResult(resultCode, data);
//                            //result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
//                        }
                        } else {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } else if (resultCode == RESULT_OK && !activityMainBinding.mwebview.getUrl().contains("contact.php?")){

                       /* if (data != null){

                            List<Uri> matisse = Matisse.obtainResult(data);
                            Uri[] result = new Uri[matisse.size()];
                            for (int i = 0; i < matisse.size(); i++) {

                                //  Log.d("mselected2",  matisse.get(i).toString());
                                result[i] = matisse.get(i);

                            }

                            filePathCallbackLollipop.onReceiveValue(result);
                        } else {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }*/
                    } else {
                        try {
                            if (filePathCallbackLollipop != null) {

                                filePathCallbackLollipop.onReceiveValue(null);
                                filePathCallbackLollipop = null;

                            }
                        } catch (Exception e) {

                        }
                    }
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(result!=null) {
                    Uri resultUri = result.getUri();
                    Uri[] arr_Uri = new Uri[1];
                    arr_Uri[0] = resultUri;
                    filePathCallbackLollipop.onReceiveValue(arr_Uri);
                    filePathCallbackLollipop = null;
                }
                else {
                    try {
                        if (filePathCallbackLollipop != null) {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }

    }

    public void set_filePathCallbackLollipop(ValueCallback<Uri[]> filePathCallbackLollipop){
        this.filePathCallbackLollipop = filePathCallbackLollipop;
    }

    public void shownoti(String name, String numbers, String type){

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.dialog_alarm);
        remoteViews.setTextViewText(R.id.tv_type, type);
        remoteViews.setTextViewText(R.id.tv_name,  name);
        remoteViews.setTextViewText(R.id.tv_number,  numbers);
        String channelId = "peertalk";
        Intent intent = new Intent(getApplicationContext(),Calling.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setClass(this, MainActivity.class);

        PendingIntent fullscreen = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                        .setContentTitle(type)
                        .setContentText(numbers)
                        //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(remoteViews)
                        .setContent(remoteViews)
                        .setCustomBigContentView(remoteViews)
                        .setFullScreenIntent(fullscreen,true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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

    public void choosehp(){

        if(choosehp_state==1){
            activityMainBinding.mwebview.post(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.mwebview.loadUrl("javascript:swal('로딩 중 입니다.')");
                }
            });
           return;
        }
        movefromchoosehp=0;
        choosehp_state=1;
        int result = -1;
        activityMainBinding.setSale(true);
     //   activityMainBinding.progcircle.setVisibility(View.VISIBLE);
        result = ContextCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS[0]);
        if(result!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "권한을 승인하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }else{

            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                    ContactsContract.CommonDataKinds.Phone._ID,
            };

            String [] selectionArgs = null;
            String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" COLLATE LOCALIZED ASC";
            ArrayList<itemModel> list = new ArrayList<itemModel>();
            ArrayList<String> list_names = new ArrayList<String>();
            ArrayList<String> list_numbers = new ArrayList<String>();

            String before_name="", before_number="";
            Cursor cursor = getApplicationContext().getContentResolver().query(uri,projection,null,selectionArgs,sortOrder);

            if(cursor.moveToFirst()){
                do {
                    String name, number, struri;
                    number = cursor.getString(0);
                    name = cursor.getString(1);
                    struri = cursor.getString(2);

                    if(number!=null && !number.isEmpty())
                        number = number.replace("-","");

                    if(before_number.equals(number) && before_name.equals(name)){
                        continue;
                    }

                    before_number = number;
                    before_name = name;

                    list_names.add(name);
                    list_numbers.add(number);
                /*tv_total+= cursor.getString(0);
                tv_total+= "\n";
                tv_total+= cursor.getString(1);
                tv_total+= "\n";
                tv_total+= cursor.getString(2);
                tv_total+= "\n";*/
               //     Log.d("cursor_1",number);
               //     Log.d("cursor_2",name);

                    // Log.d("cursor_3",struri);

                }while (cursor.moveToNext());

                String temp_names = TextUtils.join("|", list_names);
                String temp_numbers = TextUtils.join("|", list_numbers);

                SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
                String id = pref.getString("id", "");

               // Log.d("mb_id", id);
               // Log.d("activityresult", temp_names);
               // Log.d("activityresult", temp_numbers);

                ContactAPI networkService = RetrofitHelper.getRetrofit().create(ContactAPI.class);
                Call<contactModel> call = networkService.getList(temp_names, temp_numbers,id);
                call.enqueue(new Callback<contactModel>() {

                    @Override
                    public void onResponse(Call<contactModel> call, retrofit2.Response<contactModel> response) {


                        activityMainBinding.setSale(false);
                        choosehp_state=0;
                        if(movefromchoosehp==0) {
                            activityMainBinding.mwebview.post(new Runnable() {
                                @Override
                                public void run() {
                                    activityMainBinding.mwebview.loadUrl(getString(R.string.personal));
                                }
                            });
                        }
                        Log.d("contact_cus_RE","ok");
//                        if(!activityMainBinding.mwebview.getUrl().contains(getString(R.string.personal))) {
//                            Log.d("contact_cus_RE","ok");
//
//                        }
                    }

                    @Override
                    public void onFailure(Call<contactModel> call, Throwable t) {
                        choosehp_state=0;
                        Log.d("contact_cus_RE",t.toString());
                        activityMainBinding.setSale(false);

                    }
                });

               /* com.android.volley.Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                 //           activityMainBinding.progcircle.setVisibility(View.GONE);
                            activityMainBinding.setSale(false);
                            activityMainBinding.mwebview.loadUrl(getString(R.string.personal));

                        } catch (Exception e) {
                            Log.d("volley_result", e.toString());
                            e.printStackTrace();
                        }
                    }
                };
                ReqeustInsert requestOfferwall = new ReqeustInsert(temp_names,temp_numbers, id, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(requestOfferwall);*/

            }
            else if(cursor.getCount()<=0) {
                choosehp_state=0;
                activityMainBinding.setSale(false);
                activityMainBinding.mwebview.post(new Runnable() {
                    @Override
                    public void run() {
                        activityMainBinding.mwebview.loadUrl(getString(R.string.personal));
                    }
                });
            }
            else{
                choosehp_state=0;
                Toast.makeText(this, "fail"+cursor.getCount(), Toast.LENGTH_SHORT).show();
                activityMainBinding.setSale(false);
            }
        }
        }

    public void calledlist() {

        if(called_state==1){
            activityMainBinding.mwebview.post(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.mwebview.loadUrl("javascript:swal('로딩 중 입니다.')");
                }
            });
            return;
        }
        movefromcalled=0;
        called_state=1;
        int result = -1;
        activityMainBinding.setSale(true);
        //   activityMainBinding.progcircle.setVisibility(View.VISIBLE);
        result = ContextCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS[0]);
        if(result!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "권한을 승인하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }else{

            Uri uri = CallLog.Calls.CONTENT_URI;
            String[] projection = new String[]{
                    CallLog.Calls.TYPE,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION
            };

            Cursor cursor = getApplicationContext().getContentResolver().query(uri,null,null,null,null);

            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            //1: 수신 2: 발신 3:부재중, 5: 거절 , 6: 차단
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("logininfo", getApplicationContext().MODE_PRIVATE);
            String mb_id = pref.getString("id", "");

            ArrayList<String> arr_number = new ArrayList<String>();
            ArrayList<String> arr_date = new ArrayList<String>();
            ArrayList<String> arr_duration = new ArrayList<String>();
            ArrayList<String> arr_name = new ArrayList<String>();
            ArrayList<String> arr_type = new ArrayList<String>();
            int total_cusor =0;

            if(cursor.moveToFirst()) {
                do {

                    total_cusor++;
                    String c_numer = cursor.getString(number);
                    String c_type = cursor.getString(type);

                    String c_date = cursor.getString(date);
                    Date strdate = new Date(Long.valueOf(c_date));
                    SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    c_date = formatdate.format(strdate);

                    String c_duration = cursor.getString(duration);
                    String c_name = cursor.getString(name);

                    if (c_name != null ) {

                        arr_name.add(c_name);
                        Log.d("calllog_cus", c_name);

                    }
                    else {
                        arr_name.add("");
                    }
                    if (c_numer != null) {

                        arr_number.add(c_numer);
                        Log.d("calllog_cus", c_numer);

                    }
                    else {
                        arr_number.add("");
                    }
                    if (c_date != null) {

                        arr_date.add(c_date);
                        Log.d("calllog_cus", c_date);

                    }
                    else {
                        arr_date.add("");
                    }
                    if (c_duration != null) {

                        arr_duration.add(c_duration);
                        Log.d("calllog_cus", c_duration);

                    }
                    else {
                        arr_duration.add("");
                    }
                    if (c_type != null) {

                        arr_type.add(c_type);
                        Log.d("calllog_cus", c_type);
                    }
                    else {
                        arr_type.add("");
                    }



                } while (cursor.moveToNext());

                if(mb_id!=null && !mb_id.isEmpty()) {

                    String c_numer_a = TextUtils.join("|", arr_number);
                    String c_type_a = TextUtils.join("|", arr_type);
                    String c_name_a = TextUtils.join("|", arr_name);
                    String c_date_a = TextUtils.join("|", arr_date);
                    String c_duration_a = TextUtils.join("|", arr_duration);

                    Log.d("arr_log"+total_cusor,arr_number.toString());
                    Log.d("calllog_cus_RE","start");

                    chkTotalAPI networkTotal = RetrofitHelper.getRetrofit().create(chkTotalAPI.class);
                    Call<chkTotalModel> callTotal = networkTotal.getList(mb_id, c_numer_a,c_name_a);

                    callTotal.enqueue(new Callback<chkTotalModel>(){

                        @Override
                        public void onResponse(Call<chkTotalModel> callTotal, Response<chkTotalModel> response) {

                            Log.d("calllog_cus_RE","ok");
                            chkTotalModel chktotalmodel = response.body();
                          //  ArrayList<String> names = response.body().names;

                            Log.d("calllog_cus_RE",String.valueOf(chktotalmodel.getFlg()));
                            Log.d("calllog_cus_RE",chktotalmodel.getNames().toString());
                            String filter_names =  TextUtils.join("|", chktotalmodel.names);
                            CalllogAPI networkService = RetrofitHelper.getRetrofit().create(CalllogAPI.class);
                            //Log.d("calllog_cus_RE",filter_names);
                            Call<calllogModel> call = networkService.getList(mb_id, c_numer_a,c_type_a,filter_names,c_date_a,c_duration_a);

                            call.enqueue(new Callback<calllogModel>(){

                                @Override
                                public void onResponse(Call<calllogModel> call, retrofit2.Response<calllogModel> response) {
                                    called_state=0;
                                    Log.d("calllog_cus_RE","ok");
                                    activityMainBinding.setSale(false);
                                    if(movefromcalled==0) {
                                        activityMainBinding.mwebview.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                activityMainBinding.mwebview.loadUrl(getString(R.string.contact_list));
                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onFailure(Call<calllogModel> call, Throwable t) {
                                    called_state=0;
                                    Log.d("calllog_cus_RE",call.toString());
                                    Log.d("calllog_cus_RE2",t.toString());
                                    activityMainBinding.setSale(false);

                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<chkTotalModel> call, Throwable t) {
                            Log.d("calllog_cus_RE","fail");
                            Log.d("calllog_cus_RE",call.toString());
                            Log.d("calllog_cus_RE",t.toString());
                            called_state=0;
                            activityMainBinding.setSale(false);

                        }
                    });

                }
            }
        }
    }

}