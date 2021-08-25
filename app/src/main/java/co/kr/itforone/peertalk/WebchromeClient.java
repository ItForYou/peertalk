package co.kr.itforone.peertalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

public class WebchromeClient extends WebChromeClient {
    Activity activity;
    MainActivity mainActivity;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
    WebchromeClient(Activity activity, MainActivity mainActivity){

        this.activity = activity;
        this.mainActivity = mainActivity;

    }

    WebchromeClient(){}

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                .create()
                .show();
        return true;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
    {
        new AlertDialog.Builder(view.getContext())
                .setTitle("")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                result.confirm();
                            }
                        })
                .setCancelable(false)
                .create()
                .show();

        return true;
    }


    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

        mainActivity.set_filePathCallbackLollipop(filePathCallback);
        int maxnum=1;
        //갯수 변경이 필요할때 현재 필요없어서 1개로 fix
        if(webView.getUrl().contains("contact.php?"))
            maxnum=1;
        else
            maxnum=10;

        maxnum=1;
        Matisse.from(activity)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(maxnum)
                .imageEngine(new GlideEngine())
                .showPreview(true) // Default is `true`
                .countable(false)
                .forResult(FILECHOOSER_LOLLIPOP_REQ_CODE);

        return true;
    }
}
