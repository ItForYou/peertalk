package co.kr.itforone.peertalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.JavascriptInterface;

public class WebviewJavainterface {
    MainActivity mainActivity;
    public WebviewJavainterface(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @JavascriptInterface
    public void openserch() {

        mainActivity.choosehp();

    }

    @JavascriptInterface
    public void openlist() {

        mainActivity.calledlist();

    }

    @JavascriptInterface
    public void call_number(String number) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+number));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainActivity.startActivity(i);

    }



    @JavascriptInterface
    public void setLogininfo(String id,String password) {
        // Toast.makeText(mainActivity.getApplicationContext(),"setlogin",Toast.LENGTH_LONG).show();
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("pwd",password);
        editor.commit();
    }


}
