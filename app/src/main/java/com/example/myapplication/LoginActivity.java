package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    public static String cookies;//Truy cap cookies o bat ki dau
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    public static String googleId="";
    private WebView webView;
    private LoginActivity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.web_view);
        self = this;

        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUserAgentString(USER_AGENT);
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://video-vds.herokuapp.com/comment/auth/google");
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //True if the host application wants to leave the current WebView and handle the url itself, otherwise return false.
                webView.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                cookies = CookieManager.getInstance().getCookie(url);//Lay cookie
                if (!url.equals("https://video-vds.herokuapp.com/newfeed"))
                    return;

                googleId=readCookie("googleId",cookies);
                Log.d("cookies", cookies);
                Log.d("googleId", googleId);
                startActivity(new Intent(LoginActivity.this, linkToLogin_google.class));
            }
        });
    }

    public String readCookie(String name,String cookie) {
        String nameEQ = name + "=";
        String[] ca = cookie.split(";");
        for(int i=0;i < ca.length;i++) {
            String c = ca[i] ;
            while (c.charAt(0)==' ') c = c.substring(1,c.length());
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length(),c.length());
        }
        return null;
    }
}