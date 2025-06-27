package com.inovationware.toolkit.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import com.inovationware.toolkit.databinding.ActivityHelpBinding;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.ui.contract.BaseActivity;

public class HelpActivity extends BaseActivity {
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