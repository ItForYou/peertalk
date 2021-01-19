package co.kr.itforone.peertalk;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public class Calling extends Service {
    private WindowManager windowManager;
    WindowManager.LayoutParams params;
    protected View rootView;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test_call","service_oncreate1");
     //   Log.d("test_call","service_oncreate");
        /*windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.9); //Display 사이즈의 90%
        int height = (int) (display.getHeight() * 0.3);

        params = new WindowManager.LayoutParams(width,height, WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rootView = layoutInflater.inflate(R.layout.dialog_alarm, null);
*/





    }

    @Override
    public void onDestroy() {
        Log.d("test_call","service_oncreate3");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("test_call","service_oncreate2");
        return super.onUnbind(intent);
    }
}
