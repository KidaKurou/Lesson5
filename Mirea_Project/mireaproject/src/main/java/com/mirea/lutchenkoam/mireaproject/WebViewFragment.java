package com.mirea.lutchenkoam.mireaproject;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewFragment extends Fragment {
    private WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_web_view, container, false);

        webView = root.findViewById(R.id.web_view);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setForceDark(WebSettings.FORCE_DARK_AUTO);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.example.com");

        return root;
    }

    @Override
    public void onDestroy() {
        // Очищаем WebView при уничтожении фрагмента
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}