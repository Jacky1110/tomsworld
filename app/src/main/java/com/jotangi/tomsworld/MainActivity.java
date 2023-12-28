package com.jotangi.tomsworld;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jotangi.tomsworld.Title.Title;
import com.jotangi.tomsworld.Title.TitleInterface;
import com.jotangi.tomsworld.common.ComKeywd;
import com.jotangi.tomsworld.common.ComMethod;
import com.jotangi.tomsworld.util.SocketMsgSender;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.jotangi.tomsworld.common.ComKeywd.AR_APP_PACKAGE_NAME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //=========
    private View headerView ;
    private TextView txtViewUserAct,txtViewUserName;
    private SharedPreferences storeInfo;//load  information
    public static ProgressDialog myProDialog ;
    private static final String TAG = "MainActivity";
    Fragment WebInterfacefragment = new WebInterfaceFragment();
    FragmentManager fragmentManager = getSupportFragmentManager();
    Title mtitle = new Title();
    //=========
    Badge badgeShopCart;
    Badge badgeConsumer;
    View view_Group;
    private DrawerLayout mDrawerLayout;
    public ExpandableListAdapter mMenuAdapter;
    public ExpandableListView expandableList;
    private String leftMenuGoWebURL;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private BroadcastReceiver receiver;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableList.setIndicatorBounds(expandableList.getRight()- 80, expandableList.getWidth());
        } else {
            expandableList.setIndicatorBoundsRelative(expandableList.getRight()- 80, expandableList.getWidth());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeInfo = this.getSharedPreferences(ComKeywd.SI_Key,0);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) findViewById(R.id.toolbar_title);
        toolbartitle.setText("湯姆熊歡樂世界");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        headerView = navigationView.getHeaderView(0);
        txtViewUserAct = (TextView) headerView.findViewById(R.id.txtUserAcct);
        txtViewUserName = (TextView) headerView.findViewById(R.id.txtUserName);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView,
                                        View view,
                                        int groupPosition,
                                        int childPosition, long id) {

                view.setSelected(true);
                if (view_Group != null) {
                    view_Group.setBackgroundColor(Color.argb(0, 0, 0, 0));
                }
                view_Group = view;
                view_Group.setBackgroundColor(Color.parseColor("#DDDDDD"));
                mDrawerLayout.closeDrawers();
                Log.d("Look","groupPosition:"+groupPosition + "childPosition:"+childPosition);
                showWebFragment(groupPosition, childPosition);
                return false;
            }
        });

        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (view_Group != null) {
                    view_Group.setBackgroundColor(Color.argb(0, 0, 0, 0));
                }
                return false;
            }
        });


        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bundle bundle = new Bundle();
                if ( storeInfo.getString(ComKeywd.SI_Key_MemberType, "") == null ||  storeInfo.getString(ComKeywd.SI_Key_MemberType, "").equals("")) {
                    bundle.putString("webURL", ComKeywd.WEB_URL + "login.html");
                } else {
                    bundle.putString("webURL", ComKeywd.WEB_URL + "maintainMember.html");
                }
                changeWebView(bundle);
                mDrawerLayout.closeDrawers();
                resetBadgeInfo();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        ComMethod.disableBottomNavigationShiftMode(navigation);

        final Bundle bundle = new Bundle();
        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        if (leftMenuGoWebURL == null || leftMenuGoWebURL.equals("")) {
                            switch (item.getItemId()) {
                                case R.id.navigation_home:
                                    bundle.putString("webURL", ComKeywd.WEB_URL + "index.html");
                                    break;
//                                case R.id.navigation_shop:
//                                    bundle.putString("webURL", ComKeywd.WEB_URL + "shop.html");
//                                    break;
                                case R.id.navigation_member:
                                    bundle.putString("webURL", ComKeywd.WEB_URL + "member.html");
                                    break;
//                                case R.id.navigation_car:
//                                    bundle.putString("webURL", ComKeywd.WEB_URL + "shoppingCartStore.html");
//                                    break;
                                case R.id.navigation_store:
                                    bundle.putString("webURL", ComKeywd.WEB_URL + "consumer.html");
                                    break;
                            }
                        } else {
                            bundle.putString("webURL", leftMenuGoWebURL);
                        }
                        changeWebView(bundle);
                        mDrawerLayout.closeDrawers();
                        resetBadgeInfo();
                        return true;
                    }
                });
        InitInfo();
        //====for notification 當此MainActivity是由FirebaseMessageService呼叫帶起時,要直接將畫面轉至訊息中心========
        Intent intent = getIntent();
        String notify_extra = intent.getStringExtra("NOTIFY_EXTRA");
        if(notify_extra!=null && !notify_extra.equals("")){
            bundle.putString("webURL", ComKeywd.WEB_URL + "member.html");
            changeWebView(bundle);
        }
        //============================
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive (Context context, Intent intent){
                try {
                    if (intent.getAction().equals(MyFirebaseMessagingService.INFO_UPDATE_FILTER)) {
//                        resetMessageReadNum();
                    }else if (intent.getAction().equals(WebInterfaceFragment.INFO_RESET_MENU)) {
                        setLoginMenuList();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

    }

    public void setBarTitle(String title){
        TextView toolbartitle = findViewById(R.id.toolbar_title);
//        getSupportActionBar().setTitle(title);
        toolbartitle.setText(title);
//        mtitle.setBarTitle(title);
//        mtitle.titleInterface = setTitleEvent;
    }
    //調整亮度
    public void setLight(String  type){
        switch (type){
            case "0":
                WindowManager.LayoutParams lp = getWindow().getAttributes();

                lp.screenBrightness = 1.0f;

                getWindow().setAttributes(lp);
                break;
            case "1":
                WindowManager.LayoutParams olp = getWindow().getAttributes();

                olp.screenBrightness = -1.0f;

                getWindow().setAttributes(olp);
                break;
        }
    }

    private void changeWebView(Bundle bundle){
        WebInterfacefragment.setArguments(bundle);
        WebInterfaceFragment fragment = (WebInterfaceFragment) fragmentManager.findFragmentByTag(WebInterfacefragment.getTag());
        if (fragment == null) {
            fragmentManager.beginTransaction().replace(
                    R.id.relativeLayout_for_fragment,
                    WebInterfacefragment,
                    "WebInterfacefragment"
            ).commit();
        } else {
            fragment.LoadFragmentContent();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(receiver,
                new IntentFilter(MyFirebaseMessagingService.INFO_UPDATE_FILTER)
        );
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(receiver,
                new IntentFilter(WebInterfaceFragment.INFO_RESET_MENU)
        );
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        resetMessageReadNum();
    }

    public void createHomeFragment(){
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        final Bundle bundle = new Bundle();

        if(storeInfo.getString(ComKeywd.SI_Key_MemberType, "").equals("S")){
            Intent intent = new Intent();
            intent.setClass(this  , Qrcode.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("member_no",storeInfo.getString(ComKeywd.SI_Key_UserAccount, ""));
            startActivity(intent);
            return;
        }else if(storeInfo.getString(ComKeywd.SI_Key_LOGOUT_FROM, "").equals("QR")){
            bundle.putString("webURL",ComKeywd.WEB_URL + "login.html");
            storeInfo.edit()
                    .putString(ComKeywd.SI_Key_LOGOUT_FROM, "WEB")
                    .apply();
        }else{
            bundle.putString("webURL",ComKeywd.WEB_URL + "index.html");
        }
        WebInterfacefragment.setArguments(bundle);
        WebInterfaceFragment fragment = (WebInterfaceFragment)fragmentManager.findFragmentByTag(WebInterfacefragment.getTag());
        if(fragment==null){
            fragmentManager.beginTransaction().replace(
                    R.id.relativeLayout_for_fragment,
                    WebInterfacefragment,
                    "WebInterfacefragment"
            ).commit();
        }else{
            fragment.LoadFragmentContent();
        }
    }

    private void showWebFragment(int headerPosition,int childPosition){
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        final Bundle bundle = new Bundle();
        if(headerPosition==0){
            switch (childPosition){
                case 0: //登出.登入

                    if( storeInfo.getString(ComKeywd.SI_Key_UserAccount, "").equals("") || storeInfo.getString(ComKeywd.SI_Key_UserAccount, "") ==null){
                        leftMenuGoWebURL=ComKeywd.WEB_URL + "login.html";
                        navigation.setSelectedItemId(R.id.navigation_home);
                    }
                    else{
                        leftMenuGoWebURL=ComKeywd.WEB_URL + "logout.html?memberNo="+storeInfo.getString(ComKeywd.SI_Key_UserAccount, "");
                        Logout();
                        navigation.setSelectedItemId(R.id.navigation_home);
                        setLoginMenuList();
                    }


                    break;
                case 1: //結束
                    finish();

                    break;
            }
        }else if(headerPosition==1) {
            switch (childPosition) {
                case 0: //影音專區
                    leftMenuGoWebURL = "http://www.tomsworld.com.tw/video.aspx?mnuid=1116&modid=3";
                    navigation.setSelectedItemId(R.id.navigation_member);
                    break;
                case 1: //客服中心
                    //leftMenuGoWebURL=ComKeywd.WEB_URL + "orderMessage.html?app_tab=Comments";
                    leftMenuGoWebURL="http://www.tomsworld.com.tw/form_fill.aspx?siteid=&ver=&usid=&mnuid=1087&modid=4&mode=";
                    navigation.setSelectedItemId(R.id.navigation_member);
                    break;
            }
        }
        leftMenuGoWebURL="";

    }


    public void setMenuLogonStatus()
    {
        String strMemberName = storeInfo.getString(ComKeywd.SI_Key_MemberName, "");
        String userName = strMemberName + ",您好";
        if (storeInfo.getString(ComKeywd.SI_Key_UserAccount, "") != null && storeInfo.getString(ComKeywd.SI_Key_UserAccount, "") != ""){
            txtViewUserName.setText(userName);
        }
        else{
            txtViewUserName.setText(strMemberName);
        }

        return;
    }

    public void setLoginMenuList() {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding data header
        listDataHeader.add("服務");
        listDataHeader.add("系統");

        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        if(storeInfo.getString(ComKeywd.SI_Key_UserAccount, "")==null || storeInfo.getString(ComKeywd.SI_Key_UserAccount, "").equals("")) {
            heading1.add("登入");
        }else{
            heading1.add("登出");
        }

        heading1.add("結束");

        List<String> heading2 = new ArrayList<String>();
        heading2.add("影音專區");
        heading2.add("客服中心");

        listDataChild.put(listDataHeader.get(0), heading1);// Header, Child data
        listDataChild.put(listDataHeader.get(1), heading2);
        mMenuAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild);
        expandableList.setAdapter(mMenuAdapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        ((WebInterfaceFragment)WebInterfacefragment).onKeyDown(keyCode, event);

        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        if(strUserAccount=="" || strUserAccount ==null) {
//            menu.clear(); // Clear the menu first
//        }
//        /* Add the menu items */
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

//        if(strUserAccount=="" || strUserAccount ==null){
//            return false;
//        }
//        else{
        getMenuInflater().inflate(R.menu.main, menu);
//        }
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.icon_qrcode) {
            final Bundle bundle = new Bundle();
            bundle.putString("webURL", ComKeywd.WEB_URL + "qr_code.html");
            changeWebView(bundle);
            resetBadgeInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void InitInfo(){
        if(storeInfo.getString(ComKeywd.SI_Key_UserAccount, "")==null || storeInfo.getString(ComKeywd.SI_Key_UserAccount, "").equals("")){
            //導入登入及註冊畫面
            txtViewUserAct.setText("");
            txtViewUserName.setText("尚未登入");
        }
        setMenuLogonStatus();
        setLoginMenuList();
        //================
        createHomeFragment();
        resetBadgeInfo();
        //================

    }


    public void Logout(){
        storeInfo.edit()
                .putString(ComKeywd.SI_Key_LOGIN_DATA, "")
                .putString(ComKeywd.SI_Key_MemberName,"尚未登入")
                .putString(ComKeywd.SI_Key_UserAccount,"")
                .putString(ComKeywd.SI_Key_MemberType,"")
                .putString(ComKeywd.SI_Key_MemberShipCardNo,"")
                .putString(ComKeywd.SI_Key_LOGOUT_FROM,"")
                .putBoolean(ComKeywd.SI_Key_LoginEnable, false)
                .apply();
        setMenuLogonStatus();
        setLoginMenuList();
        resetBadgeInfo();
        clearCookies(this);
        Toast.makeText(MainActivity.this, getResources().getText(R.string.logoutOK), Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
                // a callback which is executed when the cookies have been removed
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    //Log.d(TAG, "Cookie removed: " + aBoolean);
                }
            });
            CookieManager.getInstance().flush();
        }else{
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager= CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public void goARAPP(){
        Intent intent = getPackageManager().getLaunchIntentForPackage(AR_APP_PACKAGE_NAME);
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + AR_APP_PACKAGE_NAME));
            startActivity(intent);
        }
    }

    public void resetBadgeInfo(){
        resetShopCartNum();
//        resetMessageReadNum();
    }

    public void resetShopCartNum(){
        if(storeInfo.getString(ComKeywd.SI_Key_UserAccount, "") != "" && ComMethod.checkInternetConnection(this)) {
            new Thread(new Runnable() {
                String requestMSG;
                String callbackMSG;
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        requestMSG=String.format(ComKeywd.TXCmd_GetShopCartNum, storeInfo.getString(ComKeywd.SI_Key_UserAccount, ""));
                        callbackMSG = SocketMsgSender.SendMsg(requestMSG,ComKeywd.KEY_SERVER_DOMAIN,ComKeywd.KEY_SERVER_PORT);
                        Message m =sendMsgHandler.obtainMessage();
                        m.what = 2;
                        m.obj = callbackMSG;
                        sendMsgHandler.sendMessage(m);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Message m =sendMsgHandler.obtainMessage();
                        m.what = 0;
                        m.obj = getResources().getText(R.string.connection)+" " +getResources().getText(R.string.error);
                        sendMsgHandler.sendMessage(m);
                    }

                }
            }).start();

        }else
        {
//            setShopCartBadge(0);
        }
    }

    public void resetMessageReadNum(){
        if(storeInfo.getString(ComKeywd.SI_Key_UserAccount, "") != "" && ComMethod.checkInternetConnection(this)) {
            new Thread(new Runnable() {
                String requestMSG;
                String callbackMSG;
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        requestMSG=String.format(ComKeywd.TXCmd_GetMessageReadNum, storeInfo.getString(ComKeywd.SI_Key_UserAccount, ""), "N");
                        callbackMSG = SocketMsgSender.SendMsg(requestMSG,ComKeywd.KEY_SERVER_DOMAIN,ComKeywd.KEY_SERVER_PORT);
                        Message m =sendMsgHandler.obtainMessage();
                        m.what = 3;
                        m.obj = callbackMSG;
                        sendMsgHandler.sendMessage(m);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Message m =sendMsgHandler.obtainMessage();
                        m.what = 0;
                        m.obj = getResources().getText(R.string.connection)+" " +getResources().getText(R.string.error);
                        sendMsgHandler.sendMessage(m);
                    }

                }
            }).start();

        }else
        {
            setConsumerBadge(0);
        }
    }



//    private void setShopCartBadge(int num){
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        BottomNavigationItemView cart = navigation.findViewById(R.id.navigation_car);
//        if(badgeShopCart==null){
//            badgeShopCart = new QBadgeView(this).bindTarget(cart).setBadgeNumber(num);
//        }else{
//            badgeShopCart.bindTarget(cart).setBadgeNumber(num);
//        }
//    }


    private void setConsumerBadge(int num){
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationItemView member = navigation.findViewById(R.id.navigation_member);
        if(badgeConsumer==null){
            badgeConsumer = new QBadgeView(this).bindTarget(member).setBadgeNumber(num);
        }else{
            badgeConsumer.bindTarget(member).setBadgeNumber(num);
        }
        //通知圓點
        if(num>0){
            ShortcutBadger.applyCount(MainActivity.this, num);
        }else{
            ShortcutBadger.removeCount(MainActivity.this);
        }
    }

    public String GetIpAddr() {
        String ipString = "";
        try{
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                ipString = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF)
                        + "." + ((ipAddress >> 16) & 0xFF) + "."
                        + ((ipAddress >> 24) & 0xFF);
            }else{
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface
                            .getNetworkInterfaces(); en.hasMoreElements();) {
                        NetworkInterface intf = en.nextElement();


                        for (Enumeration<InetAddress> enumIpAddr = intf
                                .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()
                                    && !(inetAddress instanceof Inet6Address)) {
                                ipString = inetAddress.getHostAddress().toString();
                            }
                        }
                    }
                } catch (SocketException ex) {
                    Log.e("TAG", "getIpAddr()" + ex.toString());
                }

            }
        }catch (Exception e){
            Log.e("TAG", "getIpAddr()" + e.toString());
        }
        return ipString;
    }

    @SuppressLint("HandlerLeak")
    public Handler sendMsgHandler = new Handler() {

        public void handleMessage(Message msg) {
            String callBackResult= (String)msg.obj;
            Log.d(TAG, callBackResult);
            Log.d(TAG,""+msg.what);
            switch (msg.what) {
                case 0:
                    if (MainActivity.myProDialog != null && MainActivity.myProDialog.isShowing()) {
                        MainActivity.myProDialog.dismiss();
                    }
                    break;
                case 1: //logon

                    break;
                case 2:
                    String[] shopCartSplit = callBackResult.split("/");
                    int cartNum = 0;
                    if(shopCartSplit[0].equals("OK"))
                    {
                        cartNum=Integer.parseInt(shopCartSplit[1]);
                    }
//                    setShopCartBadge(cartNum);
                    break;
                case 3:
//                    resetMessageReadNum();
                    String[] consumerSplit = callBackResult.split("/");
                    int messageNum = 0;
                    if(consumerSplit[0].equals("OK"))
                    {
                        messageNum=Integer.parseInt(consumerSplit[1]);
                    }
                    setConsumerBadge(messageNum);
                    break;
                case 4:
                    //由WebInterfaceFragment呼叫
                    Logout();
                    break;
            }
        }
    };






/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                Logout();
                break;
            case 2:

                break;
        }
    }

    private void Intent_Logout(){
        Intent logout = new Intent();
        logout.setClass(MainActivity.this, MainActivity.class);
        startActivityForResult(logout,1);
    }
*/

//    public void clear(String sharedName) {
//        Log.d("777777777","clear");
//        Context context = MainActivity.this;
//        SharedPreferences preferences = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear();
//        editor.commit();
//    }
    TitleInterface setTitleEvent = new TitleInterface() {
        @Override
        public void getString(String title) {
            getSupportActionBar().setTitle(title);

        }
    };
}