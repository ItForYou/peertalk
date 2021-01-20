package co.kr.itforone.peertalk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.kr.itforone.peertalk.contact_pkg.ContactListAdapter;
import co.kr.itforone.peertalk.contact_pkg.ListActivity;
import co.kr.itforone.peertalk.contact_pkg.itemModel;
import co.kr.itforone.peertalk.databinding.ActivityMainBinding;
import co.kr.itforone.peertalk.retrofit.RetrofitAPI;
import co.kr.itforone.peertalk.retrofit.responseModel;
import co.kr.itforone.peertalk.retrofit.RetrofitHelper;
import co.kr.itforone.peertalk.volley.ReqeustInsert;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding  activityMainBinding;
    String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.SYSTEM_ALERT_WINDOW,

    };
    String tv_total ="";
    static final int PERMISSION_REQUEST_CODE = 1;
    static final int RECEIVED_CONTATSLIST = 2;
    WebSettings settings;
    private long backPrssedTime = 0;

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

                }else{

                }
                return;
            }
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        settings = activityMainBinding.mwebview.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setTextZoom(100);

        activityMainBinding.mwebview.addJavascriptInterface(new WebviewJavainterface(this),"Android");
        activityMainBinding.mwebview.setWebViewClient(new Viewmanager(this));
        activityMainBinding.mwebview.setWebChromeClient(new WebchromeClient(this, this));

        activityMainBinding.mwebview.loadUrl(getString(R.string.login));

        /*Intent i = new Intent(MainActivity.this, DialogActivity.class);
        startActivity(i);

*/


    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPrssedTime;

        if(!activityMainBinding.mwebview.canGoBack() || activityMainBinding.mwebview.getUrl().contains("all_contact") ) {
            if (0 <= intervalTime && 2000 >= intervalTime) {
                finish();
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
        }

    }

    public void choosehp(){

        if (!hasPermissions(PERMISSIONS)){
            Toast.makeText(this, "권한을 승인하지 않았습니다.", Toast.LENGTH_SHORT).show();
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
                    Log.d("cursor_1",number);
                    Log.d("cursor_2",name);

                    // Log.d("cursor_3",struri);

                }while (cursor.moveToNext());

                String temp_names = TextUtils.join("|", list_names);
                String temp_numbers = TextUtils.join("|", list_numbers);

                SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
                String id = pref.getString("id", "");
                Log.d("mb_id", id);
                Log.d("activityresult", temp_names);
                Log.d("activityresult", temp_numbers);
                com.android.volley.Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            activityMainBinding.mwebview.loadUrl(getString(R.string.personal));
                        } catch (Exception e) {
                            Log.d("volley_result", e.toString());
                            e.printStackTrace();
                        }
                    }
                };
                ReqeustInsert requestOfferwall = new ReqeustInsert(temp_names,temp_numbers, id, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(requestOfferwall);

            }
            else{
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            }
        }
        }

}