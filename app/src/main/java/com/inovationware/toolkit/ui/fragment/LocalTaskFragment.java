package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.toolkit.global.domain.Strings.HOURS_CAPITALIZED;
import static com.inovationware.toolkit.global.domain.Strings.MINUTES_CAPITALIZED;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_LOCAL_TASK_REGULAR;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_LOCAL_TASK_REGULAR_INTERVAL_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_LOCAL_TASK_REGULAR_TIME_UNIT_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_LOCAL_TASK_REPEAT_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_LOCAL_TASK_REVERSE;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_LOCAL_TASK_REVERSE_INTERVAL_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_LOCAL_TASK_REVERSE_TIME_UNIT_KEY;
import static com.inovationware.toolkit.global.domain.Strings.bistable;
import static com.inovationware.toolkit.global.domain.Strings.netTimerMobileServiceIsRunning;
import static com.inovationware.toolkit.global.domain.Strings.ttsServiceProvider;
import static com.inovationware.toolkit.global.library.utility.Code.configureTimeUnitDropDownAdapter;
import static com.inovationware.toolkit.global.library.utility.Code.isNothing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.databinding.FragmentLocalTaskBinding;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.external.ApkClient;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.nettimer.domain.NetTimerBistable;
import com.inovationware.toolkit.nettimer.domain.NetTimerNotifierObject;
import com.inovationware.toolkit.ui.activity.MainActivity;

import java.util.concurrent.TimeUnit;

public class LocalTaskFragment extends Fragment {
    private FragmentLocalTaskBinding binding;
    private View view;
    private SharedPreferencesManager store;

    ArrayAdapter<String> timeUnitDropdownAdapter, installedAppsDropDownAdapter;

    AutoCompleteTextView regularTimeUnitDropDown, reverseTimeUnitDropDown, regularDropDown, reverseDropDown;
    TextView regularIntervalTextView, reverseIntervalTextView;

    Button startButton, stopButton;
    private ApkClient strategy;
    private Feedback feedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocalTaskBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        feedback = new Feedback(view.getContext());
        store = SharedPreferencesManager.getInstance();
        strategy = ApkClient.getInstance();
        timeUnitDropdownAdapter = configureTimeUnitDropDownAdapter(view.getContext());
        installedAppsDropDownAdapter = Code.configureInstalledAppsDropDownAdapter(view.getContext());

        regularDropDown = binding.regularDropDown;

        regularTimeUnitDropDown = binding.regularTimeUnitDropDown;

        reverseDropDown = binding.reverseDropDown;

        reverseTimeUnitDropDown = binding.reverseTimeUnitDropDown;

        regularIntervalTextView = binding.regularIntervalTextView;
        regularIntervalTextView.setText(String.valueOf(5));

        reverseIntervalTextView = binding.reverseIntervalTextView;
        reverseIntervalTextView.setText(String.valueOf(5));

        stopButton = binding.stopButton;
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (netTimerMobileServiceIsRunning) {
                    if (bistable != null) {
                        bistable.cancel();
                        bistable.repeat = false;
                        bistable = null;
                    }
                    netTimerMobileServiceIsRunning = false;
                }
                finishThis();
            }
        });

        startButton = binding.startButton;
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!thereIsMinimumRequiredInfo()) {
                    feedback.toast("Some required fields are missing.");
                    return;
                }
                NetTimerNotifierObject regular = NetTimerNotifierObject.builder()
                        .details(strategy.getUri(binding.regularDropDown.getText().toString()))
                        .interval(toInterval(regularIntervalTextView.getText().toString()))
                        .timeUnit(toTimeUnit(regularTimeUnitDropDown.getText().toString()))
                        .readAloudThisManyTimes(3)
                        .ttsService(ttsServiceProvider.getService())
                        .context(view.getContext())
                        .build();

                NetTimerNotifierObject reverse = reverseIsSet() ?
                        NetTimerNotifierObject.builder()
                        .details(strategy.getUri(binding.reverseDropDown.getText().toString()))
                        .interval(toInterval(reverseIntervalTextView.getText().toString()))
                        .timeUnit(toTimeUnit(reverseTimeUnitDropDown.getText().toString()))
                        .readAloudThisManyTimes(3)
                        .ttsService(ttsServiceProvider.getService())
                        .context(view.getContext())
                        .build() :
                        NetTimerNotifierObject.builder()
                        .details(strategy.getUri(binding.regularDropDown.getText().toString()))
                        .interval(toInterval(regularIntervalTextView.getText().toString()))
                        .timeUnit(toTimeUnit(regularTimeUnitDropDown.getText().toString()))
                        .readAloudThisManyTimes(3)
                        .ttsService(ttsServiceProvider.getService())
                        .context(view.getContext())
                        .build();

                bistable = new NetTimerBistable(regular, reverse, binding.repeatCheckBox.isChecked());
                bistable.start();
                netTimerMobileServiceIsRunning = true;
                saveSet(!reverseIsSet());

                finishThis();
            }
        });

        binding.editRegularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.regularDropDown.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.regularDropDown.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.regularDropDown, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        binding.editReverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.reverseDropDown.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.reverseDropDown.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.reverseDropDown, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        loadLastSet();


        return view;
    }


    private void finishThis() {
        startActivity(new Intent(view.getContext(), MainActivity.class));
    }


    void loadLastSet() {

        binding.regularDropDown.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR));
        binding.regularDropDown.setAdapter(installedAppsDropDownAdapter);

        regularIntervalTextView.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR_INTERVAL_KEY, "5"));
        regularTimeUnitDropDown.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR_TIME_UNIT_KEY, MINUTES_CAPITALIZED));
        regularTimeUnitDropDown.setAdapter(timeUnitDropdownAdapter);

        //binding.reverseDropDown.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE));
        binding.reverseDropDown.setAdapter(installedAppsDropDownAdapter);

        reverseIntervalTextView.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE_INTERVAL_KEY, "5"));
        reverseTimeUnitDropDown.setText(store.getString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE_TIME_UNIT_KEY, MINUTES_CAPITALIZED));
        reverseTimeUnitDropDown.setAdapter(timeUnitDropdownAdapter);

        binding.repeatCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REPEAT_KEY, true));

    }

    void saveSet(boolean regularAndReverseAreSame) {

        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR, binding.regularDropDown.getText().toString());
        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE, binding.reverseDropDown.getText().toString());

        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR_INTERVAL_KEY, regularIntervalTextView.getText().toString());
        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE_INTERVAL_KEY, reverseIntervalTextView.getText().toString());

        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REGULAR_TIME_UNIT_KEY, regularTimeUnitDropDown.getText().toString());
        store.setString(view.getContext(), SHARED_PREFERENCES_LOCAL_TASK_REVERSE_TIME_UNIT_KEY, reverseTimeUnitDropDown.getText().toString());
    }

    private boolean thereIsMinimumRequiredInfo() {
        return regularIsSet() || (regularIsSet() && reverseIsSet());
    }

    private boolean regularIsSet() {
        return !isNothing(binding.regularDropDown.getText().toString()) &&
                !isNothing(regularIntervalTextView.getText().toString()) &&
                !isNothing(regularTimeUnitDropDown.getText().toString());
    }

    private boolean reverseIsSet() {
        return !binding.reverseDropDown.getText().toString().isEmpty();
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


    private final int READ_ALOUD_REPEAT_COUNT_MIN = 1;
    private final int READ_ALOUD_REPEAT_COUNT_MAX = 5;

    private final long INTERVAL_MIN = 1;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}