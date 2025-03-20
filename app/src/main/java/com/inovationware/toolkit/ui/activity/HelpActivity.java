package com.inovationware.toolkit.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.inovationware.toolkit.databinding.ActivityHelpBinding;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;

public class HelpActivity extends AppCompatActivity {
    private ActivityHelpBinding binding;
    private Context context;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setReferences();
        setConfigurations();
        setUi();

    }

    private void setConfigurations() {
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void setReferences() {
        context = HelpActivity.this;
        webView = binding.webView;
    }

    private void setUi() {
        webView.loadUrl(
                SharedPreferencesManager.getInstance().getBaseUrl(context) + "/extra/help"
        );
    }

}