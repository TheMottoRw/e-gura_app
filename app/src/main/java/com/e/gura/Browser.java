package com.e.gura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class Browser extends AppCompatActivity {
    public WebView webView;
    public ImageView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        //Customer toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("E-Gura");
        setSupportActionBar(toolbar);
        webView = findViewById(R.id.webView);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCacheMaxSize(10 * 1024 * 1024);
        settings.setAppCachePath("");
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // Flash settings
        settings.setPluginState(WebSettings.PluginState.ON);

        // Geo location settings
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath("/data/data/gura");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;//super.shouldOverrideUrlLoading(view, request);
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("url"))
            webView.loadUrl(intent.getStringExtra("url"));
    }
}