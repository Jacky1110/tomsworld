package com.jotangi.tomsworld.util;
import android.util.Base64;

/**
 * Created by carolyn on 2017/11/9.
 */

public class strBase64 {
    /*encode&decode base64*/
    public static String base64StrEn(String authBase64){
        authBase64 =new String(Base64.encodeToString(authBase64.getBytes(),Base64.DEFAULT));

        return authBase64;
    }
    public static String base64StrDe(String authBase64){
        authBase64 =new String(Base64.decode(authBase64,Base64.DEFAULT));
        return authBase64;
    }
}
