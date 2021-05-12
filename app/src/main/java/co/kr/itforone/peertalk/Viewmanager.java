package co.kr.itforone.peertalk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Viewmanager extends WebViewClient {

    Activity viewActivity;
    MainActivity mainActivity;
    Viewmanager(Activity viewActivity) {
        this.viewActivity = viewActivity;
    }
    Viewmanager(Activity viewActivity, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.viewActivity = viewActivity;
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(mainActivity!=null && mainActivity.called_state==1){
                mainActivity.movefromcalled=1;
            }

            if(mainActivity!=null && mainActivity.choosehp_state==1){
                mainActivity.movefromchoosehp=1;
            }

            view.loadUrl(url);

        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        super.onPageFinished(view, url);
    }
}
