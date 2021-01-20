package co.kr.itforone.peertalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.w3c.dom.Text;

public class DialogActivity  extends Activity {
    String number,type;
    TextView tv_number, tv_name, tv_type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("service_call","DIALOGON");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent i = getIntent();
        if(i!=null) {
            number = i.getStringExtra("number");
            type = i.getStringExtra("type");
        }

        Log.d("service_call_type",type);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //finish();
        finishAndRemoveTask();
    }
}
