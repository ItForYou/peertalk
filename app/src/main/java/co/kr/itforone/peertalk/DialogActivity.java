package co.kr.itforone.peertalk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;

import co.kr.itforone.peertalk.Util.Dialog_manager;

import static co.kr.itforone.peertalk.MainActivity.flg_dialog_main;

public class DialogActivity  extends Activity {
    String number,type,name;
    TextView tv_number, tv_name, tv_type;
    ImageView bt_close;
    private BroadcastReceiver broadcastReceiver;
    private Dialog_manager dm = Dialog_manager.getInstance();
    private ArrayList<Activity> arrayList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList = dm.getActivityList();
        Log.d("dialog_intent", String.valueOf(arrayList.size()));

        if(arrayList.size()>1){
            dm.finishAllActivity();
            dm.reset();
        }
        dm.addActivity(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent i = getIntent();

        if(i!=null) {

            number = i.getStringExtra("number");
            type = i.getStringExtra("type");
            name = i.getStringExtra("name");

        }

        setContentView(R.layout.dialog_alarm);
        Display display  = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth()*0.8);
        int height = (int) (display.getHeight()*0.2);
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;
        getWindow().getAttributes().type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_type = (TextView) findViewById(R.id.tv_type);

        if(number != null && !number.isEmpty()){
                tv_number.setText(number);
        }

        if(type != null && !type.isEmpty()){
            tv_type.setText(type);
        }

        if(name != null && !name.isEmpty()){
            tv_name.setText(name);
        }

    }

    public void close_dialog(View view) {
            finishAndRemoveTask();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
       // finish();
        if(flg_dialog_main ==1){
            flg_dialog_main =0;
        }
        finishAndRemoveTask();
    }
}
