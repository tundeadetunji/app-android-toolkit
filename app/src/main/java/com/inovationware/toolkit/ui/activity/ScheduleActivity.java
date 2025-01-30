package com.inovationware.toolkit.ui.activity;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.inovationware.toolkit.databinding.ActivityScheduleBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Ui;
import com.inovationware.toolkit.schedule.model.Schedule;
import com.inovationware.toolkit.schedule.model.ScheduleViewSource;
import com.inovationware.toolkit.schedule.utility.ScheduleUtility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.SneakyThrows;

public class ScheduleActivity extends AppCompatActivity implements ScheduleViewSource {

    private ActivityScheduleBinding binding;
    private Set<String> programs;
    private Ui ui;
    private ScheduleUtility support;
    private Factory factory;
    private GroupManager machines;
    private SharedPreferencesManager store;
    private EncryptionManager security;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeVariables();
        setupUi();
    }

    private void initializeVariables(){
        ui = Ui.getInstance();
        programs = new HashSet<>();
        support = ScheduleUtility.getInstance();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        security = EncryptionManager.getInstance();
        factory = Factory.getInstance();
    }

    private void setupUi() {
        ui.bindProperty(ScheduleActivity.this, binding.appDropDown, programs.toArray(new String[0]));
        ui.bindProperty(ScheduleActivity.this, binding.hourDropDown, support.hours());
        ui.bindProperty(ScheduleActivity.this, binding.minuteDropDown, support.minutes());
        ui.bindProperty(ScheduleActivity.this, binding.meridianDropDown, support.meridian());

        binding.timeoutCheckBox.setChecked(true);

        binding.startScheduleButton.setOnClickListener(startScheduleButtonClick);
        binding.addAppButton.setOnClickListener(addAppButtonClick);
    }

    private final View.OnClickListener startScheduleButtonClick = new View.OnClickListener() {
        @SneakyThrows
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (programs.isEmpty() && binding.announceTextView.getText().toString().isEmpty()) return;

            if (binding.hourDropDown.getText().toString().isEmpty()) return;

            if (binding.minuteDropDown.getText().toString().isEmpty()) return;

            if (binding.meridianDropDown.getText().toString().isEmpty()) return;

            if (!thereIsInternet(ScheduleActivity.this)) return;

            factory.transfer.service.sendText(
                    ScheduleActivity.this,
                    store,
                    machines,
                    SendTextRequest.create(HTTP_TRANSFER_URL(ScheduleActivity.this, store),
                            store.getUsername(ScheduleActivity.this),
                            store.getID(ScheduleActivity.this),
                            Transfer.Intent.writeText,
                            store.getSender(ScheduleActivity.this),
                            determineTarget(ScheduleActivity.this, store, machines),
                            Strings.POST_PURPOSE_CREATE_SCHEDULE,
                            determineMeta(ScheduleActivity.this, store),
                            security.encrypt(ScheduleActivity.this, store, Schedule.to(Schedule.create(ScheduleActivity.this))),
                            Strings.EMPTY_STRING
                    ),
                    DEFAULT_ERROR_MESSAGE_SUFFIX,
                    DEFAULT_FAILURE_MESSAGE_SUFFIX);

        }
    };

    private final View.OnClickListener addAppButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!binding.appDropDown.getText().toString().isEmpty()){
                programs.add(binding.appDropDown.getText().toString());
                ui.bindProperty(ScheduleActivity.this, binding.appDropDown, programs.toArray(new String[0]));
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<String> getApps() {
        List<String> result = new ArrayList<>();
        programs.forEach(app -> result.add(String.valueOf(app)));
        return result;
    }

    @Override
    public TextView getAnnounceTextView() {
        return binding.announceTextView;
    }

    @Override
    public TextView getHourDropDown() {
        return binding.hourDropDown;
    }

    @Override
    public TextView getMinuteDropDown() {
        return binding.minuteDropDown;
    }

    @Override
    public TextView getMeridianDropDown() {
        return binding.meridianDropDown;
    }

    @Override
    public CheckBox getMonCheckbox() {
        return binding.monCheckBox;
    }

    @Override
    public CheckBox getTueCheckbox() {
        return binding.tueCheckBox;
    }

    @Override
    public CheckBox getWedCheckbox() {
        return binding.wedCheckBox;
    }

    @Override
    public CheckBox getThuCheckbox() {
        return binding.thuCheckBox;
    }

    @Override
    public CheckBox getFriCheckbox() {
        return binding.friCheckBox;
    }

    @Override
    public CheckBox getSatCheckbox() {
        return binding.satCheckBox;
    }

    @Override
    public CheckBox getSunCheckbox() {
        return binding.sunCheckBox;
    }

    @Override
    public CheckBox getTimeoutCheckbox() {
        return binding.timeoutCheckBox;
    }

    @Override
    public TextView getTimeoutTextView() {
        return binding.timeoutValue;
    }
}