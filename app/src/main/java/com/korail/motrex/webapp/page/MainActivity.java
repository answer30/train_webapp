package com.korail.motrex.webapp.page;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.korail.motrex.webapp.R;
import com.korail.motrex.webapp.common.WebViewCustom;
import com.korail.motrex.webapp.db.blacklist_DB;
import com.korail.motrex.webapp.dialog.NotiDialog;
import com.korail.motrex.webapp.dialog.OKDialog;
import com.korail.motrex.webapp.listener.IPopUpButtonEventListener;
import com.korail.motrex.webapp.listener.ITouchEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private WebViewCustom mWebView; // 웹뷰 선언
    private WebSettings mWebSettings; //웹뷰세팅

    Context con;

    ProgressBar progressBar;

    String[] black_list;

    RelativeLayout btn_layout;
    TextView time_text;

    Boolean webview_clear = false;

    Locale systemLocale;

    String local_str;

    OKDialog okDialog;

    SwipeRefreshLayout mSwipeRefreshLayout;

    ImageView btn_back, btn_webhome;

    int fail_count = 0;

    long finish_time =  300000;
    long btn_time = 3000;

    Boolean is_pause = false;

    String err_url;

    int zoom = 0;

    ImageButton btn_n, btn_p, btn_web_init;

    TextView message_01, message_02;

    RelativeLayout btn_box, guide;

    NotiDialog notiDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        View decoView = getWindow().getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decoView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);

        con = this;

        SharedPreferences pref = getSharedPreferences("WebApp", Context.MODE_PRIVATE);

        int ver = pref.getInt("blacklist_ver", -1);

        if(ver < 2){

            try {

                String assetTxt = readText("blacklist.txt");

                db_update(assetTxt);

                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("blacklist_ver", 2);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {

            try {
                Context other = createPackageContext("com.korail.motrex.update", 0);

                SharedPreferences pref_  = other.getSharedPreferences("update", Context.MODE_WORLD_READABLE);

                int aa = pref_.getInt("blacklist", -1);

            }catch (Exception e){

            }

        }

        Black_list_init();

        btn_layout = findViewById(R.id.btn_layout);
        time_text = findViewById(R.id.text_time);

        btn_back = findViewById(R.id.btn_back);
        btn_webhome = findViewById(R.id.btn_webhome);

        btn_n = findViewById(R.id.btn_n);
        btn_p = findViewById(R.id.btn_p);
        btn_web_init = findViewById(R.id.btn_nn);

        btn_box = findViewById(R.id.btn_box);

//        guide = findViewById(R.id.guide);

        message_01 = findViewById(R.id.message_01);
        message_02 = findViewById(R.id.message_02);

        message_02.setText(Html.fromHtml(getResources().getString(R.string.gudie_message)));
        message_01.setText(Html.fromHtml(getResources().getString(R.string.gudie_message2)));

        progressBar =  findViewById(R.id.progress);

        mWebView = findViewById(R.id.webView);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

             @Override
             public void onLoadResource(WebView view, String url) {
                 if (url.startsWith("app://")) {
                 }
             }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                try {

                    for(int i = 0 ; i< black_list.length ; i ++){
                        String encodeStr = URLEncoder.encode(black_list[i].trim(), "UTF-8");

                        if(url.contains(encodeStr)){

                            hideSystemUI();

                            InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) imm.hideSoftInputFromWindow(time_text.getWindowToken(), 0);

                            mhide_navi_Handler.sendMessageDelayed(mhide_navi_Handler.obtainMessage(1), 1000);
                            return true;
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if(0==url.indexOf("intent")){
                    Toast.makeText(MainActivity.this, "표시 할수 없는 사이트 입니다." , Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String fallingUrl) {
                if(fail_count < 2) {
                    try {
                        if (view.getUrl().equals(err_url)) {
                            fail_count++;
                        } else {
                            fail_count = 0;
                            err_url = view.getUrl();
                        }
                        view.reload();
                    }catch (Exception e){
                        Log.e(TAG, "onReceivedError: ", e);
                    }
                }
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if(progressBar != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }


        });

        mWebSettings = mWebView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부

        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setSupportZoom(true); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(true); // 화면 확대 축소 허용 여부
        mWebSettings.setDisplayZoomControls(false); // 화면 확대 축소 허용 여부
//        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기


        mWebSettings.setSaveFormData(true);
        mWebSettings.setSavePassword(false);

        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부
        mWebSettings.setUserAgentString("Mozilla/5.0 (Linux; diordnA 7.1.1; suxeN 6 Build/N6F26U; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/70.0.0.0 eliboM Safari/537.36");

        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress );
            }
        });

//        mWebView.loadUrl("about : blank");
        mWebView.loadUrl("https://www.daum.net");

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if(!isOpen){
                    hideSystemUI();
                }
            }
        });

        mWebSettings.setUseWideViewPort(true);

        systemLocale = getApplicationContext().getResources().getConfiguration().locale;

        local_str = systemLocale.getLanguage();

        mtime_Handler.sendMessage( mtime_Handler.obtainMessage(0));

        mpopup_Handler.sendMessageDelayed(mpopup_Handler.obtainMessage(0), finish_time);

        findViewById(R.id.corverView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(btn_layout.getVisibility() != View.VISIBLE) {
                        mpopup_Handler.removeMessages(0);
                        mpopup_Handler.sendMessageDelayed(mpopup_Handler.obtainMessage(0), finish_time);
                    }
                }
                return false;
            }
        });

        mWebView.setTouchListener(new ITouchEventListener() {
            @Override
            public void TouchEvent(int id) {

                if(id == MotionEvent.ACTION_DOWN){
                    if(btn_layout.getVisibility() != View.VISIBLE) {
                        mbutton_Handler.removeMessages(0);
                        update_button();
                    }
                }else if(id == MotionEvent.ACTION_UP){
                    mbutton_Handler.sendMessageDelayed(mbutton_Handler.obtainMessage(0), btn_time);
                }
            }
        });




        okDialog =  new OKDialog(con);

        okDialog.setButtonListener(new IPopUpButtonEventListener() {
            @Override
            public void buttonEvent(int id) {

                switch (id){
                    case -1 :{
                        mpopup_Handler.removeMessages(0);
                        app_finish();
                    }
                        break;
                    case  1 :{
                        mpopup_Handler.removeMessages(0);
                        mpopup_Handler.sendMessageDelayed(mpopup_Handler.obtainMessage(0), finish_time);
                    }
                        break;

                }
            }
        });


        if("ko".equals(local_str)){
            findViewById(R.id.btn_05).setVisibility(View.GONE);
        }else{
            findViewById(R.id.btn_05).setVisibility(View.GONE);
        }

        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(false);

        webviewClear();

        notiDialog = new NotiDialog(con);
        notiDialog.setCanceledOnTouchOutside(true);


//        mWebSettings.setJavaScriptEnabled(true);
//        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//
//        mWebSettings.setAllowFileAccess(true);
//        mWebSettings.setAllowFileAccessFromFileURLs(true);
//// 다른 도메인의경우에도 허용하는가
//        mWebSettings.setAllowUniversalAccessFromFileURLs(true);



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        is_pause = true;
        mpopup_Handler.removeMessages(0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        is_pause = false;
        mpopup_Handler.removeMessages(0);
        mpopup_Handler.sendMessageDelayed(mpopup_Handler.obtainMessage(0), finish_time);
        hide_button();
    }

    private Handler mtime_Handler = new Handler(){
        public void handleMessage(Message msg) {

            try {
                Date date = new Date(System.currentTimeMillis());
                if("ko".equals(local_str)){
                    String formattedDate = new SimpleDateFormat("MM월 dd일 HH:mm", Locale.KOREA).format(date);
                    time_text.setText(formattedDate);
                }else{
                    String formattedDate = new SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH).format(date);
                    time_text.setText(formattedDate);
                }

            }catch (Exception e){

            }
            mtime_Handler.sendMessageDelayed(mtime_Handler.obtainMessage(0), 500);

        }
    };

    boolean is_onstart = false;


    @Override
    protected void onStart() {
        super.onStart();

        is_onstart = true;
    }

    public void webBack(){

        if(mWebView.canGoBack()){
            mWebView.stopLoading();
            mWebView.goBack();
        }else {
            mWebView.stopLoading();
            if (btn_layout.getVisibility() == View.VISIBLE) {
                app_finish();
            } else {
                webview_clear = true;
                if(is_onstart) {
                    is_onstart = false;
                    webviewClear();
                }
                btn_layout.setVisibility(View.VISIBLE);

//                btn_back.setVisibility(View.INVISIBLE);
//                btn_webhome.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void web_init(){
        if(is_onstart) {
            is_onstart = false;
        }

        btn_layout.setVisibility(View.VISIBLE);

//        btn_back.setVisibility(View.INVISIBLE);
//        btn_webhome.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onBackPressed() {
        webBack();
    }

    public void webviewClear(){

        mWebView.loadUrl("about : blank");
        mWebView.clearHistory();
//        mWebView.clearCache(true);

        webview_clear = true;

        mWebView.clearFormData();

//        this.deleteDatabase("webview.db");
//        this.deleteDatabase("webviewCache.db");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(MainActivity.this);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }else {

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.getInstance().flush();
        }
    }


    public void btnClick(View view) {

        switch (view.getId()){

            case R.id.guide:{
//                guide.setVisibility(View.INVISIBLE);
            }
                break;
            case R.id.test_btn01:{
                mWebView.flingScroll(0, -1500);
            }
                break;

            case R.id.test_btn02:{
                mWebView.flingScroll(0, 1500);

            }
            break;

            case R.id.test_btn03:{
                mWebView.flingScroll(-1500, 0);

            }
            break;

            case R.id.test_btn04:{
                mWebView.flingScroll(1500, 0);

            }
            break;

            case R.id.test_btn05:{

                mWebView.zoomIn();
            }
            break;

            case R.id.test_btn06:{

                mWebView.zoomOut();
            }
            break;

            case R.id.test_btn07:{
                while ( mWebView.canZoomOut()){
                    mWebView.zoomOut();
                }
            }
            break;

            case R.id.btn_nn:{
                int a =0;
                while ( mWebView.canZoomOut() && a < 10){
                    mWebView.zoomOut();
                    a++;
                }
                update_button();
                mbutton_Handler.removeMessages(0);
                mbutton_Handler.sendMessageDelayed(mbutton_Handler.obtainMessage(0), btn_time);
            }
            break;
            case R.id.btn_n:{

                mWebView.zoomOut();

                update_button();

                mbutton_Handler.removeMessages(0);
                mbutton_Handler.sendMessageDelayed(mbutton_Handler.obtainMessage(0), btn_time);

            }
            break;
            case R.id.btn_p:{

                mWebView.zoomIn();

                update_button();

                mbutton_Handler.removeMessages(0);
                mbutton_Handler.sendMessageDelayed(mbutton_Handler.obtainMessage(0), btn_time);

            }
            break;

            case R.id.btn_home:{
                app_finish();
            }
            break;
            case R.id.btn_back:{
                webBack();
            }
            break;
            case R.id.btn_webhome:{
                web_init();
            }
            break;
            case R.id.btn_02:{
                mWebSettings.setUseWideViewPort(true);

                if("ko".equals(local_str)){
                    mWebView.loadUrl("https://www.naver.com/");
                }else{
                    mWebView.loadUrl("https://www.koreatimes.co.kr/");
                }

//                btn_layout.setVisibility(View.INVISIBLE);
                btn_back.setVisibility(View.VISIBLE);
                btn_webhome.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.btn_01:{
                mWebSettings.setUseWideViewPort(true);

                if("ko".equals(local_str)){
                    mWebView.loadUrl("https://www.daum.net");
                }else{
                    mWebView.loadUrl("https://www.msn.com/en-us");
                }

//                btn_layout.setVisibility(View.INVISIBLE);
                btn_back.setVisibility(View.VISIBLE);
                btn_webhome.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.btn_03:{
                mWebSettings.setUseWideViewPort(true);

                if("ko".equals(local_str)){
//                    mWebView.loadUrl("https://www.google.co.kr/webhp?hl=ko");

                    mWebView.loadUrl("https://www.youtube.com/");

                }else{
                    mWebView.loadUrl("https://www.google.com/webhp?hl=en-us");
                }

//                btn_layout.setVisibility(View.INVISIBLE);
                btn_back.setVisibility(View.VISIBLE);
                btn_webhome.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.btn_04:{
                if("ko".equals(local_str)){
                    mWebSettings.setUseWideViewPort(true);
                    mWebView.loadUrl("https://zum.com");
                }else{
                    mWebSettings.setUseWideViewPort(false);
                    mWebView.loadUrl("http://info.korail.com/mbs/english/index.jsp");
                }

//                btn_layout.setVisibility(View.INVISIBLE);
                btn_back.setVisibility(View.VISIBLE);
                btn_webhome.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.btn_05:{
                webview_clear = true;

                if("ko".equals(local_str)){
                    mWebView.loadUrl("http://WWW.KTXmagazine.com");
                }else{
                    mWebView.loadUrl("http://WWW.KTXmagazine.com");
                }

//                btn_layout.setVisibility(View.INVISIBLE);
                btn_back.setVisibility(View.VISIBLE);
                btn_webhome.setVisibility(View.VISIBLE);
            }
            break;
        }

    }

    public void db_update(String black_list_temp){

        blacklist_DB mrequest_DB= new blacklist_DB(con);
        mrequest_DB.getReadableDatabase().delete(blacklist_DB.TABLE_NAME, null, null);

        ContentValues values = new ContentValues();

        values.put(blacklist_DB.LIST_NAME, black_list_temp);
        long table_id = mrequest_DB.getReadableDatabase().insertOrThrow(blacklist_DB.TABLE_NAME, null, values);

        mrequest_DB.close();

    }

    public void Black_list_init(){

        blacklist_DB mrequest_DB= new blacklist_DB(con);

        Cursor c = mrequest_DB.getReadableDatabase().query(blacklist_DB.TABLE_NAME, blacklist_DB.FROM, null, null, null, null, null);

        while (c.moveToNext()) {

            String assetTxt = c.getString(0);

           black_list = assetTxt.split("\\n");

           for(int i = 0; i < black_list.length; i++){
               black_list[i] = black_list[i].trim();
           }

        }

        c.close();
        mrequest_DB.close();
    }

    private String readText(String file) throws IOException {
        InputStream is = getAssets().open(file);

        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        String text = new String(buffer);

        return text;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void setFullscreen(boolean fullscreen) {

        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            attrs.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            attrs.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            attrs.flags |= WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;

        }
        else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        }
        getWindow().setAttributes(attrs);
        hideSystemUI();
    }

    protected void initWebViewSettings(WebView w) {
        w.setScrollbarFadingEnabled(true);
        w.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        w.setMapTrackballToArrowKeys(false); // use trackball directly
        // Enable the built-in zoom
        w.getSettings().setBuiltInZoomControls(true);
        final PackageManager pm = con.getPackageManager();
        boolean supportsMultiTouch =
                pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)
                        || pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);
        w.getSettings().setDisplayZoomControls(!supportsMultiTouch);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

//        w.setLayerType(View.LAYER_TYPE_SOFTWARE, null);


    }

    private Handler mpopup_Handler = new Handler(){
        public void handleMessage(Message msg) {

            if(!is_pause) {
//                    okDialog.show(",", 1);
            }else{
                webviewClear();
                finish();
            }
        }
    };


    private Handler mbutton_Handler = new Handler(){
        public void handleMessage(Message msg) {

            if(!is_pause) {
                hide_button();
            }
        }
    };


    private Handler mhide_navi_Handler = new Handler(){
        public void handleMessage(Message msg) {
            if(!is_pause) {

                hideSystemUI();
                notiDialog.show("" , getResources().getString(R.string.blcaklist_message));
            }
        }
    };

    public void hide_button(){
        btn_box.setVisibility(View.INVISIBLE);
    }

    public void update_button(){

        btn_box.setVisibility(View.VISIBLE);

        if(mWebView.canZoomIn()){
            btn_p.setEnabled(true);
        }else{
            btn_p.setEnabled(false);
        }

        if(mWebView.canZoomOut()){
            btn_n.setEnabled(true);
        }else{
            btn_n.setEnabled(false);
        }
    }

    public void app_finish(){
        finish();
    }


}
