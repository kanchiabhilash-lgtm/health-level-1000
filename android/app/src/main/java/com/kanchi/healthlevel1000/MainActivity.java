package com.kanchi.healthlevel1000;

import android.os.Bundle;
import android.webkit.WebSettings;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bridge != null && bridge.getWebView() != null) {
            WebSettings settings = bridge.getWebView().getSettings();
            String userAgent = settings.getUserAgentString();
            if (userAgent != null) {
                // Remove WebView identification tags so Google OAuth does not block with disallowed_useragent
                userAgent = userAgent.replace("; wv", "").replace("Version/4.0 ", "");
                settings.setUserAgentString(userAgent);
            }
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setSupportMultipleWindows(true);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
        }
    }
}
