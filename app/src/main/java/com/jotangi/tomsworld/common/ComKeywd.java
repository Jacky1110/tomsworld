package com.jotangi.tomsworld.common;

/**
 * Created by carolyn on 2017/11/14.
 */

public class ComKeywd {
    public static String KEY_NO_INFO = "NO_INFO";
    public static String KEY_INFO_LOST = "INFO_LOST";
    public static String KEY_NONE = "none";
    public final static String KEY_STORE_INFO = "iiFETnet_StoreInfo"; //獲取資料的所有
    public final static String KEY_CT_IP = "IP";//目前(CURRENT)所選擇的IP，以下同理
    public final static String KEY_CT_PORT = "PORT";//
    public final static String KEY_CT_USER_KEY = "USR_KEY";
    public static String KEY;
    public static final String SI_Key_LoginEnable = "LOGIN_ENABLE";
    public static final String SI_Key = "tomsworld";
    public static final String SI_Key_UserAccount = "USR_ACC";
    public static final String SI_Key_UserPassword = "USR_PASS";
    public static final String SI_Key_MemberName = "USER_NAME";
    public static final String SI_Key_MemberType = "MEMBER_TYPE";
    public static final String SI_Key_VerifyCode = "VERIFY_CODE";
    public static final String SI_Key_LOGIN_DATA = "LoginData";
    public static final String SI_Key_LOGOUT_FROM = "LOGOUT_FROM";
    public static final String SI_Key_MemberShipCardNo = "MEMBER_SHIP_CARDNO";

    public static final String WEB_URL = "https://tomsworldapp.com.tw/";//正式
//    public static final String WEB_URL = "https://tomstest.jotangi.net:10443/"; //測試
    public static final String KEY_SERVER_DOMAIN = "tomsworldapp.com.tw/";
    public static final String AR_APP_PACKAGE_NAME = "com.richmobile.yccar";

    public static final int KEY_SERVER_PORT = 8701;
    /**統一傳送命令*/
    public final static String TXCmd_Logon = "LOGON|%s|%s|%s|%s|%s";//Account|Password|LogonType|FirebaseToken|ipAddr
    public final static String TXCmd_GetShopCartNum = "GET_SHOPCART_NUM|%s";//Account
    public final static String TXCmd_GetMessageReadNum = "GET_MESSAGE_READ_NUM|%s|%s";//Account|SysOperatorFlag
    /**存Uersite檔案名稱*/
    public static final String KEY_USERSITE_FILENAME = "iiFETnet";
    public static final String KEY_FIREBASE_TOKEN_FILENAME = "iiFETnet_firebaseToken";
}
