package com.inovationware.toolkit.ui.activity;

import static com.inovationware.toolkit.global.domain.DomainObjects.bistableManager;
import static com.inovationware.toolkit.global.library.utility.Support.getOutOfThere;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.inovationware.toolkit.bistable.service.BistableManager;
import com.inovationware.toolkit.databinding.ActivityStopServiceBinding;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.library.app.MessageBox;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.system.service.ToolkitServiceManager;
import com.inovationware.toolkit.ui.contract.BaseActivity;

public class StopServiceActivity extends BaseActivity {

    ActivityStopServiceBinding binding;
    private Context context;
    private ToolkitServiceManager services;
    private final String YES = DomainObjects.exactly;
    private final String NO = DomainObjects.no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStopServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupReferences();
        setupListeners();
        setupUi();
    }

    //begin setups
    private void setupReferences() {
        context = StopServiceActivity.this;
        services = ToolkitServiceManager.getInstance();
    }

    private void setupListeners() {
        binding.stopLocalTaskServiceButton.setOnClickListener(stopLocalTaskServiceButtonHandler);
        binding.stopNetworkServiceButton.setOnClickListener(stopNetworkServiceButtonHandler);
        binding.stopAllServicesButton.setOnClickListener(stopAllServicesButtonHandler);
    }

    private void setupUi() {

    }

    //end setups

    //begin listeners
    private final View.OnClickListener stopAllServicesButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MessageBox("This stops receiving requests and cancels any scheduled reminder.", YES, NO) {
                @Override
                public void positiveButtonAction() {
                    stopLocalTaskService();
                    stopNetworkService();
                    getOutOfThere(context);
                }

                @Override
                public void negativeButtonAction() {

                }
            }.show(context);
        }
    };

    private final View.OnClickListener stopLocalTaskServiceButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!bistableManager.isNetTimerMobileServiceIsRunning()) return;
            new MessageBox("Stop reminding me about that?", YES, NO) {
                @Override
                public void positiveButtonAction() {
                    stopLocalTaskService();
                    Support.getOutOfThere(context);
                }

                @Override
                public void negativeButtonAction() {

                }
            }.show(context);
        }
    };

    private final View.OnClickListener stopNetworkServiceButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MessageBox("Stop receiving requests?", YES, NO) {
                @Override
                public void positiveButtonAction() {
                    stopNetworkService();
                    getOutOfThere(context);
                }

                @Override
                public void negativeButtonAction() {

                }
            }.show(context);
        }
    };

    //end listeners

    //begin support
    private void stopNetworkService(){
        services.stopServices(context);
    }

    private void stopLocalTaskService(){
        bistableManager.stop();
        bistableManager.stopForegroundService(context);
    }

    //end support
}