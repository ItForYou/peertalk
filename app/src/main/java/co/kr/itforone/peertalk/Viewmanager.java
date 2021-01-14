package co.kr.itforone.peertalk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Viewmanager extends WebViewClient {

    Activity viewActivity;
    Viewmanager(Activity viewActivity) {
        this.viewActivity = viewActivity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        super.onPageFinished(view, url);
    }
}
