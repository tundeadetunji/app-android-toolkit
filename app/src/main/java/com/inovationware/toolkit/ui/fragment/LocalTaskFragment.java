package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.toolkit.global.domain.DomainObjects.HOURS_CAPITALIZED;
import static com.inovationware.toolkit.global.domain.DomainObjects.MINUTES_CAPITALIZED;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_LOCAL_TASK_REGULAR;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_LOCAL_TASK_REGULAR_INTERVAL_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_LOCAL_TASK_REGULAR_TIME_UNIT_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_LOCAL_TASK_REPEAT_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_LOCAL_TASK_REVERSE;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_LOCAL_TASK_REVERSE_INTERVAL_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_LOCAL_TASK_REVERSE_TIME_UNIT_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.bistableManager;
import static com.inovationware.toolkit.global.domain.DomainObjects.ttsServiceProvider;
import static com.inovationware.toolkit.global.library.utility.Code.configureTimeUnitDropDownAdapter;
import static com.inovationware.toolkit.global.library.utility.Code.isNothing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.databinding.FragmentLocalTaskBinding;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.external.ApkClient;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.bistable.verb.BistableCommand;
import com.inovationware.toolkit.bistable.verb.BistableNotifier;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.system.foreground.LocalTaskService;
import com.inovationware.toolkit.ui.adapter.ViewPagerAdapter;

import java.util.concurrent.TimeUnit;

public class LocalTaskFragment extends Fragment {
    private final int READ_ALOUD_REPEAT_COUNT_MIN = 1;
    private final int READ_ALOUD_REPEAT_COUNT_MAX = 5;
    private final long INTERVAL_MIN = 1;
    private FragmentLocalTaskBinding binding;
    private View view;
    private SharedPreferencesManager store;
    private Context context;

    ArrayAdapter<String> timeUnitDropdownAdapter, installedAppsDropDownAdapter;

    private ApkClient strategy;
    private Feedback feedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocalTaskBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupReferences();
        setupListeners();
        setupUi(this);

        return view;
    }

    //begin setup
    private void setupReferences() {
        context = view.getContext();
        feedback = new Feedback(view.getContext());
        store = SharedPreferencesManager.getInstance();
        strategy = ApkClient.getInstance();
        timeUnitDropdownAdapter = configureTimeUnitDropDownAdapter(view.getContext());
        installedAppsDropDownAdapter = Code.configureInstalledAppsDropDownAdapter(view.getContext());
    }
    private void setupListeners(){
        binding.startButton.setOnClickListener(startButtonHandler);
        binding.stopButton.setOnClickListener(stopButtonHandler);
        binding.regularAppButton.setOnClickListener(regularAppButtonHandler);
        binding.reverseAppButton.setOnClickListener(reverseAppButtonHandler);
    }

    private void setupUi(Fragment fragment) {
        binding.regularIntervalTextView.setText(String.valueOf(5));
        binding.reverseIntervalTextView.setText(String.valueOf(5));
        loadLastSet();
        setButtons(bistableManager.isNetTimerMobileServiceIsRunning());

    }

    //end setup

    //begin listeners
    private final View.OnClickListener startButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!thereIsMinimumRequiredInfo()) {
                feedback.toast("Some required fields are missing.");
                return;
            }

            bistableManager.stop();

            BistableNotifier regular = BistableNotifier.builder()
                    .details(binding.regularTextView.getText().toString())
                    .interval(toInterval(binding.regularIntervalTextView.getText().toString()))
                    .timeUnit(toTimeUnit(binding.regularTimeUnitDropDown.getText().toString()))
                    .readAloudThisManyTimes(3)
                    .ttsService(ttsServiceProvider.getService())
                    .context(view.getContext())
                    .build();

            BistableNotifier reverse = reverseIsSet() ?
                    BistableNotifier.builder()
                            .details(binding.reverseTextView.getText().toString())
                            .interval(toInterval(binding.reverseIntervalTextView.getText().toString()))
                            .timeUnit(toTimeUnit(binding.reverseTimeUnitDropDown.getText().toString()))
                            .readAloudThisManyTimes(3)
                            .ttsService(ttsServiceProvider.getService())
                            .context(view.getContext())
                            .build() :
                    BistableNotifier.builder()
                            .details(binding.regularTextView.getText().toString())
                            .interval(toInterval(binding.regularIntervalTextView.getText().toString()))
                            .timeUnit(toTimeUnit(binding.regularTimeUnitDropDown.getText().toString()))
                            .readAloudThisManyTimes(3)
                            .ttsService(ttsServiceProvider.getService())
                            .context(view.getContext())
                            .build();


            bistableManager.setBistable(new BistableCommand(regular, reverse, binding.repeatCheckBox.isChecked()));
            //bistable = new BistableCommand(regular, reverse, binding.repeatCheckBox.isChecked());
            saveSet(!reverseIsSet());
            startForegroundService();
            // bistable.start();
            // netTimerMobileServiceIsRunning = true;
            // finishThis();
        }
    };

    private void startForegroundService(){
        ContextCompat.startForegroundService(context, new Intent(context, LocalTaskService.class));
        bistableManager.setNetTimerMobileServiceIsRunning(true);
        setButtons(true);
        finishThis(true);
    }

    private final View.OnClickListener stopButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bistableManager.stop();
            bistableManager.stopForegroundService(context);
            setButtons(false);
            finishThis(false);
        }
    };

    private final View.OnClickListener regularAppButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //binding.regularTextView.setText(binding.regularAppDropDown.getText().toString());
            binding.regularTextView.setText(strategy.getUri(binding.regularAppDropDown.getText().toString()));
            /*binding.regularAppDropDown.setInputType(InputType.TYPE_CLASS_TEXT);
            binding.regularAppDropDown.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.regularAppDropDown, InputMethodManager.SHOW_IMPLICIT);*/
        }
    };

    private final View.OnClickListener reverseAppButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //binding.reverseTextView.setText(binding.reverseAppDropDown.getText().toString());
            binding.reverseTextView.setText(strategy.getUri(binding.reverseAppDropDown.getText().toString()));

            /*binding.reverseAppDropDown.setInputType(InputType.TYPE_CLASS_TEXT);
            binding.reverseAppDropDown.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.reverseAppDropDown, InputMethodManager.SHOW_IMPLICIT);*/
        }
    };

    //end listeners

    //begin support
    private void finishThis(boolean isStarting) {
        Support.getOutOfThere(view.getContext());
    }

    private void loadLastSet() {

        binding.regularTextView.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR));

        binding.regularAppDropDown.setAdapter(installedAppsDropDownAdapter);

        binding.regularIntervalTextView.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR_INTERVAL_KEY, "5"));
        binding.regularTimeUnitDropDown.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR_TIME_UNIT_KEY, MINUTES_CAPITALIZED));
        binding.regularTimeUnitDropDown.setAdapter(timeUnitDropdownAdapter);

        //binding.reverseDropDown.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE));
        binding.reverseAppDropDown.setAdapter(installedAppsDropDownAdapter);

        binding.reverseIntervalTextView.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE_INTERVAL_KEY, "5"));
        binding.reverseTimeUnitDropDown.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE_TIME_UNIT_KEY, MINUTES_CAPITALIZED));
        binding.reverseTimeUnitDropDown.setAdapter(timeUnitDropdownAdapter);

        binding.repeatCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REPEAT_KEY, true));

    }

    void saveSet(boolean regularAndReverseAreSame) {

        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR, binding.regularTextView.getText().toString());
        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE, binding.regularAppDropDown.getText().toString());

        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR_INTERVAL_KEY, binding.regularIntervalTextView.getText().toString());
        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE_INTERVAL_KEY, binding.reverseIntervalTextView.getText().toString());

        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR_TIME_UNIT_KEY, binding.regularTimeUnitDropDown.getText().toString());
        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE_TIME_UNIT_KEY, binding.reverseTimeUnitDropDown.getText().toString());
    }

    private boolean thereIsMinimumRequiredInfo() {
        return regularIsSet() || (regularIsSet() && reverseIsSet());
    }

    private boolean regularIsSet() {
        return !isNothing(binding.regularTextView.getText().toString()) &&
                !isNothing(binding.regularIntervalTextView.getText().toString()) &&
                !isNothing(binding.regularTimeUnitDropDown.getText().toString());
        /*return !isNothing(binding.regularAppDropDown.getText().toString()) &&
                !isNothing(binding.regularIntervalTextView.getText().toString()) &&
                !isNothing(binding.regularTimeUnitDropDown.getText().toString());*/
    }

    private boolean reverseIsSet() {
        return !binding.reverseTextView.getText().toString().isEmpty();
    }

    long toInterval(String value) {
        return Long.parseLong(String.valueOf(Integer.parseInt(value) < INTERVAL_MIN ? INTERVAL_MIN : Integer.parseInt(value)));
    }

    int toRepeatCount(String value) {
        return Integer.parseInt(String.valueOf((Integer.parseInt(value) < READ_ALOUD_REPEAT_COUNT_MIN ? READ_ALOUD_REPEAT_COUNT_MIN : Integer.parseInt(value) > READ_ALOUD_REPEAT_COUNT_MAX ? READ_ALOUD_REPEAT_COUNT_MAX : Integer.parseInt(value))));
    }

    TimeUnit toTimeUnit(String value) {
        return value.equalsIgnoreCase(MINUTES_CAPITALIZED) ? TimeUnit.MINUTES : value.equalsIgnoreCase(HOURS_CAPITALIZED) ? TimeUnit.HOURS : TimeUnit.SECONDS;
    }

    private void setButtons(boolean isStartingOrStarted){
        binding.regularAppButton.setEnabled(!isStartingOrStarted);
        binding.reverseAppButton.setEnabled(!isStartingOrStarted);
        binding.startButton.setEnabled(!isStartingOrStarted);
        binding.stopButton.setEnabled(isStartingOrStarted);
    }

    //end support

    //begin overrides
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //end overrides

}