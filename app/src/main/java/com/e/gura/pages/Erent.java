package com.e.gura.pages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.e.gura.R;

public class Erent extends Fragment {
    private WebView webView;

    public Erent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_erent, container, false);
        webView = view.findViewById(R.id.webView);
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

        webView.loadUrl("https://e-gura.com/e-rent/index.php");
        return view;
    }
}