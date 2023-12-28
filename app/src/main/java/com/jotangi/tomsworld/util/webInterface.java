package com.jotangi.tomsworld.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * Created by carolyn on 2017/11/28.
 */

public class webInterface {
    public static void callJS(final WebView myWebView, final String jsURL){
        // 必须另开线程进行JS方法调用(否则无法调用)
        myWebView.post(new Runnable() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                final int version = Build.VERSION.SDK_INT;
                // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
                if (version < 18) {
                    myWebView.loadUrl("javascript:" + jsURL);
                } else {
                    myWebView.evaluateJavascript("javascript:" + jsURL, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //此处为 js 返回的结果
                        }
                    });
                }
            }
        });
    }

}
