package com.jotangi.tomsworld.common;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;


/**
 * Created by carolyn on 2017/11/9.
 */

public class ComMethod {

    public static boolean checkInternetConnection(Context context){
        ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni=cm.getActiveNetworkInfo();
        if(ni!=null && ni.isConnected()){
            // System.out.println("ni.isConnected() = "+ni.isConnected());
            return ni.isConnected();
        }
        else{
            Toast.makeText(context, "無網路連線，請檢查是否開啟!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    @SuppressLint("RestrictedApi")
    public static void disableBottomNavigationShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt( 0 );
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField( "mShiftingMode" );
            shiftingMode.setAccessible( true );
            shiftingMode.setBoolean(menuView, false );
            shiftingMode.setAccessible( false );
            for ( int i = 0 ; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log. e ( "BNVEffect" , "Unable to get shift mode field" , e);
        } catch (IllegalAccessException e) {
            Log. e ( "BNVEffect" , "Unable to change value of shift mode" , e);
        }
    }



}
