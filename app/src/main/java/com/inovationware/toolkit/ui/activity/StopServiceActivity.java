package com.inovationware.toolkit.ui.activity;

import static com.inovationware.toolkit.global.library.utility.Support.getOutOfThere;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.bistable.service.BistableManager;
import com.inovationware.toolkit.databinding.ActivityMainBinding;
import com.inovationware.toolkit.databinding.ActivityStopServiceBinding;
import com.inovationware.toolkit.global.library.app.MessageBox;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.system.service.ToolkitServiceManager;

public class StopServiceActivity extends AppCompatActivity {

    ActivityStopServiceBinding binding;
    private Context context;
    private ToolkitServiceManager services;
    private BistableManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStopServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupReferences();
        setupListeners();
        setupUi();
    }

    private void setupReferences() {
        context = StopServiceActivity.this;
        services = ToolkitServiceManager.getInstance();
        manager = new BistableManager();
    }

    private void setupListeners() {
        binding.stopLocalTaskServiceButton.setOnClickListener(stopLocalTaskServiceButtonHandler);
        binding.stopNetworkServiceButton.setOnClickListener(stopNetworkServiceButtonHandler);
    }

    private void setupUi() {

    }

    //begin listeners
    private final View.OnClickListener stopLocalTaskServiceButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            manager.stop();
            manager.stopForegroundService(context);
            Support.getOutOfThere(context);
        }
    };

    private final View.OnClickListener stopNetworkServiceButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MessageBox() {
                @Override
                public void positiveButtonAction() {
                    services.stopServices(context);
                    getOutOfThere(context);
                }

                @Override
                public void negativeButtonAction() {

                }
            }.show(context);
        }
    };

    //end listeners
}