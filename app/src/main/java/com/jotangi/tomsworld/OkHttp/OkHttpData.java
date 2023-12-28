package com.jotangi.tomsworld.OkHttp;

import android.util.Log;

import com.jotangi.tomsworld.OkHttp.OkHttpInterface;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpData {

    public OkHttpInterface okHttpInterface;
    private final String url = "https://tomsworldapp.com.tw/api/";
    private Map<String, String> map = new HashMap<>();
    public void checkLogin(String account , String password)
    {
//        Map<String, String> map = new HashMap<>();
        map.put("logon_type", "S");
        map.put("app_type", "tomsworld");
        map.put("member_no", account);
        map.put("password", password);
        String params = new JSONObject(map).toString();
        final String Str = url + "Member/doLogin";
        get(Str , params);
    }

    public void SendPrivateMessage(String sConnectionid)
    {
        map.put("from", "");
        map.put("to", sConnectionid);
        map.put("message", "掃描成功");
        String params = new JSONObject(map).toString();
        final String Str = url + "Signalr/SendPrivateMessage";
        get(Str , params);
    }

    public void writeOffCoupon(String coupon_no , String user_member_no , String member_no)
    {
        map.put("coupon_no", coupon_no);
        map.put("member_no", user_member_no);
        map.put("verification_member_no", member_no);
        String params = new JSONObject(map).toString();
        final String Str = url + "Pay/updateWriteOff";
        get(Str , params);
    }

    private void get(final String str , final String data) {
        final OkHttpClient client = new OkHttpClient();
        final ExecutorService service = Executors.newSingleThreadExecutor();

        service.submit(new Runnable() {
            @Override
            public void run() {
                MediaType MEDIA_TYPE_JSON= MediaType.parse("application/json; charset=utf-8");
                String jsonStr = data;
                RequestBody requestBody=RequestBody.create(MEDIA_TYPE_JSON,jsonStr);
                Request request = new Request.Builder()
                        .url(str)
                        .post(requestBody)
                        .build();
                try {
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();
                    okHttpInterface.getObject(resStr ,data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
