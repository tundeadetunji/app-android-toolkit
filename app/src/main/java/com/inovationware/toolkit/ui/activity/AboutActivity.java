package com.inovationware.toolkit.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.ActivityAboutBinding;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.ui.contract.BaseActivity;

public class AboutActivity extends BaseActivity {

    TextView captionTextView;
    Button openProfileButton;
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        captionTextView = findViewById(R.id.captionTextView);
        captionTextView.setText(Strings.ABOUT_TEXT);
        /*captionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });*/

        openProfileButton = findViewById(R.id.openProfileButton);
        openProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Strings.ABOUT_URL)));
            }
        });

    }
}