package com.jotangi.tomsworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jotangi.tomsworld.OkHttp.OkHttpData;
import com.jotangi.tomsworld.OkHttp.OkHttpInterface;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.jotangi.tomsworld.common.ComKeywd;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class Qrcode extends AppCompatActivity {
    SharedPreferences storeInfo;
    private DecoratedBarcodeView barcodeView;
    private GifImageView iv_qrcode;
    private BeepManager beepManager;
    private String lastText;
    private String member_no;
    OkHttpData okHttpData = new OkHttpData();
    private WriteOffAlertDialog mDialog =new WriteOffAlertDialog();
    String sConnectionid;
    Boolean VisibilityType = false;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            lastText = result.getText();
            String coupon_name = "";
            String coupon_no = "";
            String user_member_no = "";
            Log.e("CouponMsg",lastText);
            try {
                JSONObject oCouponData = new JSONObject(lastText);
                coupon_name = oCouponData.getString("coupon_name");
                coupon_no = oCouponData.getString("coupon_no");
                user_member_no = oCouponData.getString("member_no");
                sConnectionid = oCouponData.getString("connectionid");
//                byte[] data = Base64.decode(coupon_name, Base64.DEFAULT);
//                String text = new String(data, "UTF-8");
//                URLEncoder.encode("測試","UTF-8");
//                Log.e("CouponName",""+URLEncoder.encode("測試","UTF-8"));
                coupon_name = URLDecoder.decode(coupon_name, "UTF-8");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String finalCoupon_no = coupon_no;
            final String finalUser_member_no = user_member_no;

            if (coupon_name != "" && coupon_no != ""){
                barcodeView.pause();
                mDialog.checkCouponData(Qrcode.this, coupon_no, coupon_name, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.btn_back:
                                mDialog.Dismiss();
                                barcodeView.resume();
                                break;
                            case R.id.btn_go:
                                okHttpData.writeOffCoupon(finalCoupon_no, finalUser_member_no,member_no);
                                okHttpData.okHttpInterface = okHttpEvt;
                                mDialog.Dismiss();
                                Visibility();
                                break;
                        }
                    }
                });
                lastText = "";
            }
            else{
                Toast.makeText(Qrcode.this, "核銷條碼錯誤請從新產生", Toast.LENGTH_SHORT).show();
                lastText = "";
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeInfo = Qrcode.this.getSharedPreferences(ComKeywd.SI_Key,0);
        setContentView(R.layout.activity_qrcode);
        barcodeView = findViewById(R.id.barcode_scanner);
        Button goScan = findViewById(R.id.goScan);
        Button logout = findViewById(R.id.logout);
        Button goExit = findViewById(R.id.goExit);
        iv_qrcode = findViewById(R.id.iv_qrcode);
        Bundle bundle = getIntent().getExtras();
        member_no = bundle.getString("member_no");
        startToScan();
        goScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtScanInfo = findViewById(R.id.txtScanInfo);
                Button goScan = findViewById(R.id.goScan);
                if(VisibilityType){
                    onPause();
                    goScan.setText("掃描");
                    VisibilityType = false;
                    txtScanInfo.setText("");
                }
                else{
                    startToScan();
                }

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeInfo.edit()
                        .putString(ComKeywd.SI_Key_MemberName,"")
                        .putString(ComKeywd.SI_Key_UserAccount,"")
                        .putString(ComKeywd.SI_Key_MemberType,"")
                        .putString(ComKeywd.SI_Key_LOGOUT_FROM,"QR")
                        .putString(ComKeywd.SI_Key_LOGIN_DATA, "")
                        .apply();
                Intent intent = new Intent(Qrcode.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        goExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeInfo.edit()
                        .putString(ComKeywd.SI_Key_LOGOUT_FROM, "WEB")
                        .apply();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                System.exit(0);

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
        Visibility();
        }



    private void startToScan(){
        TextView txtScanInfo = findViewById(R.id.txtScanInfo);
        Button goScan = findViewById(R.id.goScan);
        //掃描
        txtScanInfo.setText("  偵測中");
        goScan.setText("停止掃描");
        barcodeView.resume();
        barcodeView.setVisibility(View.VISIBLE);
        iv_qrcode.setVisibility(View.INVISIBLE);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.setStatusText("");
        barcodeView.decodeContinuous(callback);
        beepManager = new BeepManager(Qrcode.this);
        VisibilityType = true;
    }

    private void Visibility(){
        barcodeView.setVisibility(View.INVISIBLE);
        iv_qrcode.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//按返回鍵，則執行退出確認
            return true;
        }
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
    public void ConfirmExit(){//退出確認
        AlertDialog.Builder ad=new AlertDialog.Builder(Qrcode.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開此程式嗎?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                storeInfo.edit()
                        .putString(ComKeywd.SI_Key_LOGOUT_FROM, "WEB")
                        .apply();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                System.exit(0);
            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //不退出不用執行任何操作
            }
        });
        ad.show();//顯示對話框
    }

    OkHttpInterface okHttpEvt = new OkHttpInterface() {
        @Override

        public void getObject(final String msg, String data) {
            runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        String success = "";
                        String message = "";
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            success = jsonObject.getString("isSuccess");
                            message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mDialog.showWriteOffMessage(Qrcode.this , success , message,sConnectionid);
                    }
                }
            );
        }
    };


}
