package co.kr.itforone.peertalk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import co.kr.itforone.peertalk.contact_pkg.ListActivity;
import co.kr.itforone.peertalk.contact_pkg.itemModel;
import co.kr.itforone.peertalk.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding  activityMainBinding;
    String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE

    };
    String tv_total ="";
    static final int PERMISSION_REQUEST_CODE = 1;
    static final int RECEIVED_CONTATSLIST = 2;


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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case RECEIVED_CONTATSLIST:
                if(data!=null){
                    int print_size = data.getIntExtra("size",0);
                    Toast.makeText(getApplicationContext(),print_size+"개의 주소록을 가져왔습니다.", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    public void choosehp(View view){

        if (!hasPermissions(PERMISSIONS)){
            Toast.makeText(this, "권한을 승인하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }else{
            Intent i = new Intent(MainActivity.this, ListActivity.class);
            startActivityForResult(i,RECEIVED_CONTATSLIST);
        }

    }
}