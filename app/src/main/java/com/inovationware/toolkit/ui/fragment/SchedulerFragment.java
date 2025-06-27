package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.utility.Support.determineMeta;
import static com.inovationware.toolkit.common.utility.Support.determineTarget;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.inovationware.toolkit.databinding.FragmentSchedulerBinding;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.utility.EncryptionManager;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.Ui;
import com.inovationware.toolkit.features.schedule.model.Schedule;
import com.inovationware.toolkit.features.schedule.model.ScheduleViewSource;
import com.inovationware.toolkit.features.schedule.utility.ScheduleUtility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.SneakyThrows;

public class SchedulerFragment extends Fragment implements ScheduleViewSource {
    private View view;
    private FragmentSchedulerBinding binding;
    private Set<String> programs;
    private Ui ui;
    private ScheduleUtility support;
    private Factory factory;
    private GroupManager machines;
    private SharedPreferencesManager store;
    private EncryptionManager security;

    private Context context;

    private ScheduleViewSource fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSchedulerBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        
        context = view.getContext();
        fragment = this;
        initializeVariables();
        setupUi();

        return view;
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
        ui.bindProperty(context, binding.appDropDown, programs.toArray(new String[0]));
        ui.bindProperty(context, binding.hourDropDown, support.hours());
        ui.bindProperty(context, binding.minuteDropDown, support.minutes());
        ui.bindProperty(context, binding.meridianDropDown, support.meridian());

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

            if (!thereIsInternet(context)) return;

            factory.transfer.service.sendText(
                    context,
                    store,
                    machines,
                    SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                            store.getUsername(context),
                            store.getID(context),
                            Transfer.Intent.writeText,
                            store.getSender(context),
                            determineTarget(context, store, machines),
                            DomainObjects.POST_PURPOSE_CREATE_SCHEDULE,
                            determineMeta(context, store),
                            security.encrypt(context, store, Schedule.to(Schedule.create(fragment))),
                            DomainObjects.EMPTY_STRING
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
                ui.bindProperty(context, binding.appDropDown, programs.toArray(new String[0]));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}