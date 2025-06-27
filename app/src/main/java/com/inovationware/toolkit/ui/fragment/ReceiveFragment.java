package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.clipboardGetText;
import static com.inovationware.generalmodule.Device.clipboardSetText;
import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_CREATE;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_EMPHASIZE;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_INFORM;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_UPDATE;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_WRITE_NOTE;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_PERIODIC_UPDATES_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_READ_OUT_LOUD_ON_RECEIVE_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_SEARCH_ON_RECEIVE_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_SEND_TO_CLIPBOARD_ON_RECEIVE_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.TEXT;
import static com.inovationware.toolkit.common.domain.DomainObjects.cachedMemos;
import static com.inovationware.toolkit.common.utility.Code.content;
import static com.inovationware.toolkit.common.utility.Code.dtoDropDownAdapter;
import static com.inovationware.toolkit.common.utility.Code.isNothing;
import static com.inovationware.toolkit.common.utility.Support.determineMeta;
import static com.inovationware.toolkit.common.utility.Support.determineTarget;
import static com.inovationware.toolkit.common.utility.Support.initialParamsAreSet;
import static com.inovationware.toolkit.common.utility.Support.responseStringIsValid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.common.infrastructure.retrofit.Repo;
import com.inovationware.toolkit.common.infrastructure.retrofit.Retrofit;
import com.inovationware.toolkit.common.utility.Code;
import com.inovationware.toolkit.common.utility.DeviceClient;
import com.inovationware.toolkit.common.utility.EncryptionManager;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.InputDialog;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.databinding.FragmentReceiveBinding;
import com.inovationware.toolkit.databinding.FragmentSendBinding;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendNoteRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.features.datatransfer.dto.response.ResponseEntity;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.features.memo.service.MemoService;
import com.inovationware.toolkit.features.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.ui.memento.UiMemento;
import com.inovationware.toolkit.ui.support.TransferAuthority;

import java.io.IOException;
import java.security.GeneralSecurityException;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiveFragment extends Fragment {

    private FragmentReceiveBinding binding;
    private View view;
    private Factory factory;
    private TransferAuthority authority;
    private GroupManager machines;
    private SharedPreferencesManager store;
    private Context context;
    private Handler periodicUpdateRoutineHandler;
    private EncryptionManager security;
    private ArrayAdapter<String> dropdownAdapter;
    private String lastResponse;
    private Feedback feedback;
    private static final int PICK_FILE_TO_SEND = 999;
    private static final int PICK_FILE_TO_READ = 998;
    private MemoService memoService;
    private DeviceClient device;
    private UiMemento memento;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReceiveBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupVariables();
        setupUi();
        setupListeners();
        postSetup();

        return view;

    }


    private void setupVariables(){
        factory = Factory.getInstance();
        feedback = new Feedback(view.getContext());
        context = view.getContext();
        periodicUpdateRoutineHandler = new Handler();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        device = DeviceClient.getInstance();
        dropdownAdapter = dtoDropDownAdapter(view.getContext());
        security = EncryptionManager.getInstance();
        memoService = KeepIntentService.getInstance(view.getContext(), store, device);
        memento = UiMemento.getInstance();
        authority = new TransferAuthority(factory,  context, machines, store);
    }

    private void setupUi(){
        binding.receiveDropDown.setAdapter(dropdownAdapter);
        binding.CopyToClipboardOnReceiveCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_SEND_TO_CLIPBOARD_ON_RECEIVE_KEY, false));
        binding.ReadOutLoudOnReceiveCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_READ_OUT_LOUD_ON_RECEIVE_KEY, false));
        binding.SearchOnReceiveCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_SEARCH_ON_RECEIVE_KEY, false));
        binding.UpdatePeriodicallyCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_PERIODIC_UPDATES_KEY, false));

    }

    private void postSetup(){
        if (binding.UpdatePeriodicallyCheckBox.isChecked())
            periodicUpdateRoutineHandler.post(periodicalUpdateRoutine);
    }

    private void setupListeners(){
        binding.receiveButton.setOnClickListener(receiveButtonHandler);
        binding.SearchOnReceiveCheckBox.setOnCheckedChangeListener(searchOnReceiveCheckboxHandler);
        binding.ReadOutLoudOnReceiveCheckBox.setOnCheckedChangeListener(readOutLoudOnReceiveCheckboxHandler);
        binding.UpdatePeriodicallyCheckBox.setOnCheckedChangeListener(updatePeriodicallyCheckboxHandler);
        binding.CopyToClipboardOnReceiveCheckBox.setOnCheckedChangeListener(copyToClipboardOnReceiveCheckboxHandler);
    }

    private final View.OnClickListener receiveButtonHandler = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            if (content(binding.receiveDropDown).equalsIgnoreCase(TEXT) || content(binding.receiveDropDown).isEmpty()) {
                receiveText(true);
            } else {
                authority.openReceivedFile(content(binding.receiveDropDown));
            }
        }
    };

    private final CompoundButton.OnCheckedChangeListener searchOnReceiveCheckboxHandler = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            store.setChecked(view.getContext(), SHARED_PREFERENCES_SEARCH_ON_RECEIVE_KEY, binding.SearchOnReceiveCheckBox.isChecked());
        }
    };

    private final CompoundButton.OnCheckedChangeListener readOutLoudOnReceiveCheckboxHandler = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            store.setChecked(view.getContext(), SHARED_PREFERENCES_READ_OUT_LOUD_ON_RECEIVE_KEY, binding.ReadOutLoudOnReceiveCheckBox.isChecked());
        }
    };

    private final CompoundButton.OnCheckedChangeListener updatePeriodicallyCheckboxHandler = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            store.setChecked(view.getContext(), SHARED_PREFERENCES_PERIODIC_UPDATES_KEY, binding.UpdatePeriodicallyCheckBox.isChecked());
            if (binding.UpdatePeriodicallyCheckBox.isChecked()) {
                periodicUpdateRoutineHandler.postDelayed(periodicalUpdateRoutine, 5000);
            }
        }
    };

    private final CompoundButton.OnCheckedChangeListener copyToClipboardOnReceiveCheckboxHandler = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            store.setChecked(view.getContext(), SHARED_PREFERENCES_SEND_TO_CLIPBOARD_ON_RECEIVE_KEY, binding.CopyToClipboardOnReceiveCheckBox.isChecked());
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    void receiveText(boolean regardless) {
        if (!thereIsInternet(view.getContext()) || !initialParamsAreSet(view.getContext(), store, machines))
            return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.readText(
                HTTP_TRANSFER_URL(view.getContext(), store),
                store.getUsername(view.getContext()),
                store.getID(view.getContext()),
                String.valueOf(Transfer.Intent.readText),
                DomainObjects.EMPTY_STRING
        );
        navigate.enqueue(new Callback<String>() {
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                    if (response.body() == null) return;
                    //utility.handleReceivedText(response.body());
                    if (content(binding.UpdatePeriodicallyCheckBox) && response.body().equalsIgnoreCase(lastResponse) & !regardless) {
                        return;
                    }

                    lastResponse = response.body();

                    if ((!responseStringIsValid(response.body())) && store.shouldDisplayErrorMessage(view.getContext())) {
                        feedback.toast("that resulted in an error.", Toast.LENGTH_SHORT);
                        return;
                    }

                    if (response.body().trim().length() < 1) return;

                    ResponseEntity.Envelope received = ResponseEntity.from(response.body());

                    if (received.getInfo() == null) return;

                    if (received.getInfo().trim().isEmpty()) return;

                    String decrypted = security.decrypt(view.getContext(), store, received.getInfo());

                    binding.LastReceivedTextView.setText(authority.displayInformationAboutSender(received.getSender()));
                    binding.detailsTextView.setText(decrypted);

                    if (binding.CopyToClipboardOnReceiveCheckBox.isChecked())
                        clipboardSetText(view.getContext(), decrypted);

                    if (binding.SearchOnReceiveCheckBox.isChecked())
                        authority.openInBrowser(decrypted);

                    if (binding.ReadOutLoudOnReceiveCheckBox.isChecked())
                        authority.PerformReadOutLoud(decrypted);

                    if (binding.UpdatePeriodicallyCheckBox.isChecked())
                        try {
                            periodicUpdateRoutineHandler.post(periodicalUpdateRoutine);
                        } catch (Exception ignored) {
                        }

                } else {
                    if (SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)) {
                        new Feedback(context).toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)) {
                    new Feedback(context).toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
                }
            }
        });
    }
    Runnable periodicalUpdateRoutine = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            receiveText(false);
            if (store.getChecked(view.getContext(), SHARED_PREFERENCES_PERIODIC_UPDATES_KEY))
                periodicUpdateRoutineHandler.postDelayed(this, 5000);
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FILE_TO_READ && resultCode == Activity.RESULT_OK) {
            try {
                binding.detailsTextView.setText(device.readText(view.getContext(), data.getData()));
            } catch (IOException ignored) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        memento.backupFrom(binding.getRoot().getContext(), SharedPreferencesManager.getInstance(), DomainObjects.TRANSFER_FRAGMENT_MEMENTO_KEY, binding.detailsTextView);
        binding = null;
    }
}