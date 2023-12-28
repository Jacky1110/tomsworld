package com.jotangi.tomsworld;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.webkit.GeolocationPermissions;

import com.jotangi.tomsworld.common.ComKeywd;
import com.jotangi.tomsworld.util.webInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.app.Activity.RESULT_OK;

public class WebInterfaceFragment extends Fragment {
    private LocalBroadcastManager broadcaster;
    public static final String INFO_RESET_MENU = "info_reset_menu";
    WebView wvPage;
    private SharedPreferences storeInfo;//load  information
    //===========
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    //===========
    public WebInterfaceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        storeInfo = getActivity().getSharedPreferences(ComKeywd.SI_Key,0);
        View v = inflater.inflate(R.layout.fragment_web_interface, container, false);
        wvPage = (WebView)v.findViewById(R.id.wvWebInterface);
        //============for facebook logon 允許第三方SDK存取cookes===================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(wvPage, true);
        }
        //===============================
        wvPage.addJavascriptInterface(new WebInterfaceFragment.AndroidJsInterface(),"hamels");

        LoadFragmentContent();
        wvPage.getSettings().setDomStorageEnabled(true);
        //

        wvPage.setWebViewClient(new WebInterfaceFragment.MyWebViewClient());

        wvPage.getSettings().setJavaScriptEnabled(true);

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (mNetworkInfo != null && mNetworkInfo.isAvailable()){
            //wvPage.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            wvPage.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            //wvPage.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }else{
            wvPage.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        //===========網頁下載檔案=============

        wvPage.setDownloadListener(new DownloadListener(){
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Log.e("HEHE","onDownloadStart被呼叫：下載連結：" + url);
                new Thread(new DownLoadThread(url,contentDisposition,mimetype)).start();
                Toast.makeText(getActivity(),"下載完畢",Toast.LENGTH_SHORT).show();

            }
        });

        //========================
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        wvPage.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle("提示訊息");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }
            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

        });
        //=====================
        return v;
    }


    public void LoadFragmentContent(){
        String url = (String)getArguments().get("webURL");
        //允許定位
        if (Build.VERSION.SDK_INT >= 23 && url.indexOf("consumer.html")>0) {
            int checkPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getActivity(),"沒有權限，請手動開啟應用程式定位權限",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }
        //允許下載
        /*
        if (Build.VERSION.SDK_INT >= 23 && url.indexOf("member.html")>0) {
            int checkDownloadPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkDownloadPermission != PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getActivity(),"沒有權限，請手動開啟應用程式儲存權限",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

        }
        */
        wvPage.loadUrl(url);
    }

    public class DownLoadThread implements Runnable {
        private String dlUrl;
        private String contentDisposition;
        private String mimetype;
        public DownLoadThread(String dlUrl,String contentDisposition,String mimetype) {
            this.dlUrl = dlUrl;
            this.contentDisposition = contentDisposition;
            this.mimetype = mimetype;
        }

        @Override
        public void run() {
            Log.e("HEHE", "開始下載~~~~~");
            InputStream in = null;
            FileOutputStream fout = null;
            File sdFile = null;
            try {
                URL httpUrl = new URL(dlUrl);
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                in = conn.getInputStream();
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String fileName = URLUtil.guessFileName(dlUrl,contentDisposition,mimetype);
                sdFile = new File(path, fileName);
                fout = new FileOutputStream(sdFile);

                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    fout.write(buffer, 0, len);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fout != null) {
                    try {
                        fout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.e("HEHE", "下載完畢~~~~");
        }
    }

    public void onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            String currentPage="";
            String goBackPage="";
            int goBackIndex=1;
            WebBackForwardList mWebBackForwardList = wvPage.copyBackForwardList();
            if (mWebBackForwardList.getCurrentIndex() > 0){
                currentPage = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getUrl();
                goBackPage = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
            }

            if(currentPage=="" || currentPage.indexOf("index.html")>0) {
                new AlertDialog.Builder(getActivity())
                        //.setTitle("確認視窗")
                        .setMessage("確定要結束應用程式嗎?")
                        //.setIcon(R.drawable.ic_launcher)
                        .setPositiveButton("確定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        ((MainActivity)getActivity()).finish();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                    }
                                }).show();
            }else if(currentPage.indexOf("login.html")>0){
                //導回首頁
                wvPage.loadUrl(ComKeywd.WEB_URL + "index.html");
            }else{
                if(wvPage.canGoBack()){
                    //wvPage.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                    wvPage.goBack(); //goBack()表示返回WebView的上一页面
                }else{
                    ((MainActivity)getActivity()).createHomeFragment();
                }

            }
            ((MainActivity)getActivity()).resetShopCartNum();
        }

    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    public class AndroidJsInterface {
        @JavascriptInterface
        public void jsCall_getMemberInfo() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("acct", storeInfo.getString(ComKeywd.SI_Key_UserAccount, ""));//加密的內容
                jsonObject.put("name", storeInfo.getString(ComKeywd.SI_Key_MemberName, ""));
                jsonObject.put("memberType", storeInfo.getString(ComKeywd.SI_Key_MemberType, ""));
                //jsonObject.put("logon_type", storeInfo.getString(ComKeywd.SI_Key_LOGON_TYPE, ""));
                jsonObject.put("app_type", "tomsWord");
                webInterface.callJS(wvPage,"appCall_getMemberInfo('" + jsonObject + "')");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @JavascriptInterface
        public void jsCall_setMemberInfo(String arge){

            try {
                JSONObject jsonObject = new JSONObject(arge);
                String strMethod=jsonObject.getString("method");
                JSONObject json = (JSONObject) jsonObject.get("params");
                storeInfo.edit()
                        .putString(ComKeywd.SI_Key_LOGIN_DATA, json.toString())
                        .putString(ComKeywd.SI_Key_MemberName,json.getString("member_name"))
                        .putString(ComKeywd.SI_Key_UserAccount,json.getString("member_no"))
                        .putString(ComKeywd.SI_Key_MemberType,json.getString("member_type"))
                        //.putString(ComKeywd.SI_Key_MemberShipCardNo,json.getString("membershipcardno"))
                        .apply();


                if(json.getString("member_type").equals(("S"))){
                    Intent intent = new Intent();
                    intent.setClass(getActivity()  , Qrcode.class);
                    intent.putExtra("member_no",storeInfo.getString(ComKeywd.SI_Key_UserAccount, ""));
                    startActivity(intent);
                }else{
                    ((MainActivity)getActivity()).setMenuLogonStatus();
                    ((MainActivity)getActivity()).resetShopCartNum();
                    resetLeftMenu();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void jsCall_QRcode(){
            Intent intent = new Intent();
            intent.setClass(getActivity()  , Qrcode.class);
            intent.putExtra("member_no",storeInfo.getString(ComKeywd.SI_Key_UserAccount, ""));
            startActivity(intent);

        }

        @JavascriptInterface
        public String jsCall_getMemberData(){
            return storeInfo.getString(ComKeywd.SI_Key_LOGIN_DATA,"");
        }

        @JavascriptInterface
        public void jsCall_modifyMemberName(String sMemberName){
            storeInfo.edit()
                    .putString(ComKeywd.SI_Key_MemberName,sMemberName)
                    .apply();

            ((MainActivity)getActivity()).setMenuLogonStatus();

        }

        @JavascriptInterface
        public void jsCall_getFirebaseToken(){
                try
                {
                    FileInputStream fileIn = getActivity().openFileInput(ComKeywd.KEY_FIREBASE_TOKEN_FILENAME);
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    String strFirebaseToken = (String)in.readObject();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("firebaseToken", strFirebaseToken);
                        jsonObject.put("machineType", "Android");
                        webInterface.callJS(wvPage,"appCall_getFirebaseToken('" + jsonObject + "')");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e) {
                    // TODO: handle exception
                }
        }

        @JavascriptInterface
        public void jsCall_setShopCartNum(){
            ((MainActivity)getActivity()).resetShopCartNum();
        }

        @JavascriptInterface
        public void jsCall_setMessageReadNum(){
            ((MainActivity)getActivity()).resetMessageReadNum();
        }

        @JavascriptInterface
        public void jsCall_doLogout(){
            Log.d("789789789","Logout");
            storeInfo.edit()
                    .putString(ComKeywd.SI_Key_MemberName,"")
                    .putString(ComKeywd.SI_Key_UserAccount,"")
                    .putString(ComKeywd.SI_Key_MemberType,"")
                    .putString(ComKeywd.SI_Key_LOGIN_DATA, "")
                    .putString(ComKeywd.SI_Key_LOGOUT_FROM, "WEB")
                    .apply();

            try {
                String callbackMSG="";
                Message m =((MainActivity)getActivity()).sendMsgHandler.obtainMessage();
                m.what = 4;
                m.obj = callbackMSG;
                ((MainActivity)getActivity()).sendMsgHandler.sendMessage(m);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
            public void jsCall_goArApp(){
            ((MainActivity)getActivity()).goARAPP();
        }

        @JavascriptInterface
        public void jsCall_setTitle(String title){
//            ((MainActivity)getActivity()).setBarTitle(title);
        }
    }


    private void resetLeftMenu(){
        broadcaster = LocalBroadcastManager.getInstance(((MainActivity)getActivity()));
        Intent intent = new Intent(INFO_RESET_MENU);
        broadcaster.sendBroadcast(intent);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String[] urlArray = url.split(ComKeywd.WEB_URL);
            if(urlArray.length >= 2){
                //判斷url是qr_code 調整亮度
                if (urlArray[1].equals("qr_code.html")){
                    ((MainActivity)getActivity()).setLight("0");
                }
                else{
                    ((MainActivity)getActivity()).setLight("1");
                }
            }

            String Title = view.getTitle();
            if(urlArray.length >= 2){
                //判斷url是Title後 修改TopBarTitle
                if(Title.equals("")){
                    ((MainActivity)getActivity()).setBarTitle("湯姆熊歡樂世界");
                }
                else{
                    ((MainActivity)getActivity()).setBarTitle(view.getTitle());
                }
            }
            else{
                ((MainActivity)getActivity()).setBarTitle("湯姆熊歡樂世界");
            }
            super.onPageFinished(view, url);
        }

        //        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            if (error.getPrimaryError() == SslError.SSL_DATE_INVALID // 日期不正確
//                    || error.getPrimaryError() == SslError.SSL_EXPIRED // 日期不正確
//                    || error.getPrimaryError() == SslError.SSL_INVALID // webview BUG
//                    || error.getPrimaryError() == SslError.SSL_UNTRUSTED) { // 根證書丟失
//                if (chkMySSLCNCert(error.getCertificate())) {
//                    handler.proceed(); // 如果證書一致，忽略錯誤
//                }else{
//                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
//                    b.setTitle("警告");
//                    b.setMessage("SSL憑證錯誤，需要更新APP或是WiFi不安全");
//                    b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ((MainActivity)getActivity()).finish();
//                        }
//                    });
//                    b.setCancelable(false);
//                    b.create().show();
//                }
//            }
//        }
//        webView.setWebViewClient(new WebViewClient(){
//            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                try{
//                    if(url.startsWith("baidumap://"))
//                    { Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); startActivity(intent);
//                    return true;
//                    }
//                }catch (Exception e)
//                { return false; }
//                webView.loadUrl(url);
//                return true; } });
        @Override public boolean shouldOverrideUrlLoading(WebView webView, String url){
            try{
                if(url.startsWith("https://maps.app.goo.gl/") || url.startsWith("https://www.google.com/")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                else if(url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity( intent );
                }
            }catch (Exception e){
                return false;
            }
//            webView.loadUrl(url);
            return true;
        }
    }
//    private boolean chkMySSLCNCert(SslCertificate cert) {
//        byte[] MySSLCNSHA256 = { -15, 63, 83, 25, -126, 113, -20, -22, 21, -90, -128,
//                50, -127, 3, -98, -27, -72, 42, 51, 41, -118, -126, 52, -87, 32, -82, -110,
//                88, 5, -44, 46, 83 }; //證書指紋
//        Bundle bundle = SslCertificate.saveState(cert);
//        byte[] bytes = bundle.getByteArray("x509-certificate");
//        if (bytes != null) {
//            try {
//                CertificateFactory cf = CertificateFactory.getInstance("X.509");
//                Certificate ca = cf.generateCertificate(new ByteArrayInputStream(bytes));
//                MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
//                byte[] key = sha256.digest(((X509Certificate) ca).getEncoded());
//                return Arrays.equals(key, MySSLCNSHA256);
//            } catch (Exception Ex) {}
//        }
//        return false;
//    }
}
