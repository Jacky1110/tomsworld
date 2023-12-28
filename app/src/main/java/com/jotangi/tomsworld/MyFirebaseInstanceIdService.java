package com.jotangi.tomsworld;

import android.content.Context;
import android.util.Log;

import com.jotangi.tomsworld.common.ComKeywd;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by carolyn on 2018/4/9.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        saveFile(this,recent_token);
    }

    public void saveFile(Context context, String token){
        try{
            FileOutputStream fos = context.openFileOutput(ComKeywd.KEY_FIREBASE_TOKEN_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(token);
            out.close();
            fos.close();
        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
