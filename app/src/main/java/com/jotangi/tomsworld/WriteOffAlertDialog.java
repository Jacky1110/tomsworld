package com.jotangi.tomsworld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jotangi.tomsworld.OkHttp.OkHttpData;


public class WriteOffAlertDialog {
    private Dialog mDialog;
    OkHttpData okHttpData = new OkHttpData();

    public void checkCouponData(Context mContext , String coupon_no , String coupon_name , View.OnClickListener click){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.writeoffalertdialog, null);
        TextView txt_coupon_no = v.findViewById(R.id.txt_coupon_no);
        TextView txt_coupon_name = v.findViewById(R.id.txt_coupon_name);
        Button btn_go = v.findViewById(R.id.btn_go);
        Button btn_back = v.findViewById(R.id.btn_back);
        txt_coupon_name.setText(coupon_name);
        txt_coupon_no.setText(coupon_no);
        mDialog = builder.create();
        mDialog.show();
        mDialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        btn_back.setOnClickListener(click);
        btn_go.setOnClickListener(click);
    }
    public void showWriteOffMessage(Context mContext , String  rtnSuccess , String rtnMessage ,String sConnectionid){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.writeoffmessage, null);
        TextView txt_message = v.findViewById(R.id.txt_message);
        ImageView imv_success = v.findViewById(R.id.imv_success);
        if(rtnSuccess == "true"){
            okHttpData.SendPrivateMessage(sConnectionid);
            imv_success.setImageResource(R.drawable.tick);
        }
        else
        {
            imv_success.setImageResource(R.drawable.close);
        }

        Button btn_ok = v.findViewById(R.id.btn_ok);
        txt_message.setText(rtnMessage);
        mDialog = builder.create();
        mDialog.show();
        mDialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dismiss();
            }
        });
    }
    public void Dismiss(){
        mDialog.dismiss();
    }
}
