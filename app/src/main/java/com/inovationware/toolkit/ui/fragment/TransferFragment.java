package com.inovationware.toolkit.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.databinding.FragmentTransferBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendNoteRequest;
import com.inovationware.toolkit.datatransfer.dto.response.ResponseEntity;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.InputDialog;
import com.inovationware.toolkit.global.library.app.retrofit.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.app.retrofit.Repo;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.memo.service.MemoService;
import com.inovationware.toolkit.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.ui.support.TransferAuthority;
import com.inovationware.toolkit.ui.memento.UiMemento;

import java.io.IOException;
import java.security.GeneralSecurityException;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inovationware.generalmodule.Device.clipboardGetText;
import static com.inovationware.generalmodule.Device.clipboardSetText;
import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.EMPTY_STRING;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_CREATE;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_EMPHASIZE;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_INFORM;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_UPDATE;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_WRITE_NOTE;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_PERIODIC_UPDATES_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_READ_OUT_LOUD_ON_RECEIVE_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_SEARCH_ON_RECEIVE_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_SEND_TO_CLIPBOARD_ON_RECEIVE_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SendFrom;
import static com.inovationware.toolkit.global.domain.Strings.TEXT;
import static com.inovationware.toolkit.global.domain.Strings.cachedMemos;
import static com.inovationware.toolkit.global.library.utility.Code.content;
import static com.inovationware.toolkit.global.library.utility.Code.dtoDropDownAdapter;
import static com.inovationware.toolkit.global.library.utility.Code.isNothing;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;
import static com.inovationware.toolkit.global.library.utility.Support.responseStringIsValid;

public class TransferFragment extends Fragment {
    private FragmentTransferBinding binding;
    private Factory factory;
    private TransferAuthority authority;
    private GroupManager machines;
    private SharedPreferencesManager store;
    private View view;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        memento.backupFrom(binding.getRoot().getContext(), SharedPreferencesManager.getInstance(), Strings.TRANSFER_FRAGMENT_MEMENTO_KEY, binding.detailsTextView);
        binding = null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransferBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupVariables();
        preSetupUi();
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
        authority = new TransferAuthority(factory, binding, context, machines, store);
    }

    private void preSetupUi(){
        memento.restoreTo(view.getContext(), store, Strings.TRANSFER_FRAGMENT_MEMENTO_KEY, binding.detailsTextView);
    }

    private void setupUi(){
        binding.receiveDropDown.setAdapter(dropdownAdapter);
        binding.CopyToClipboardOnReceiveCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_SEND_TO_CLIPBOARD_ON_RECEIVE_KEY, false));
        binding.sendDropDown.setAdapter(dropdownAdapter);
        binding.ReadOutLoudOnReceiveCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_READ_OUT_LOUD_ON_RECEIVE_KEY, false));
        binding.SearchOnReceiveCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_SEARCH_ON_RECEIVE_KEY, false));
        binding.UpdatePeriodicallyCheckBox.setChecked(store.getChecked(view.getContext(), SHARED_PREFERENCES_PERIODIC_UPDATES_KEY, false));
    }

    private void postSetup(){
        if (binding.UpdatePeriodicallyCheckBox.isChecked())
            periodicUpdateRoutineHandler.post(periodicalUpdateRoutine);
    }

    private void setupListeners(){
        binding.noteButton.setOnClickListener(noteButtonHandler);
        binding.updateFileButton.setOnClickListener(updateFileButtonHandler);
        binding.createFileButton.setOnClickListener(createFileButtonHandler);
        binding.emphasizeButton.setOnClickListener(emphasizeButtonHandler);
        binding.informButton.setOnClickListener(informButtonHandler);
        binding.receiveButton.setOnClickListener(receiveButtonHandler);
        binding.sendButton.setOnClickListener(sendButtonHandler);
        binding.SearchOnReceiveCheckBox.setOnCheckedChangeListener(searchOnReceiveCheckboxHandler);
        binding.ReadOutLoudOnReceiveCheckBox.setOnCheckedChangeListener(readOutLoudOnReceiveCheckboxHandler);
        binding.UpdatePeriodicallyCheckBox.setOnCheckedChangeListener(updatePeriodicallyCheckboxHandler);
        binding.CopyToClipboardOnReceiveCheckBox.setOnCheckedChangeListener(copyToClipboardOnReceiveCheckboxHandler);
    }

    private final View.OnClickListener noteButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!authority.canSend(SendFrom.TextView)) return;

            if (binding.memoTitle.getText().toString().isEmpty()) {
                binding.memoTitle.setText(store.getString(view.getContext(), Strings.MEMO_TOPIC_KEY));
                binding.detailsTextView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.detailsTextView, InputMethodManager.SHOW_IMPLICIT);

                return;
            }

            try {
                sendNote();
            } catch (GeneralSecurityException | IOException ignored) {
                //throw new RuntimeException(e);
            }
        }
    };

    private final View.OnClickListener updateFileButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isNothing(content(binding.detailsTextView))) return;

            new InputDialog(view.getContext(), "", "Full path of file\non the target device", "e.g.  c:\\users\\file.txt") {
                @Override
                public void positiveButtonAction() {
                    if (!this.getText().isEmpty())
                        sendText(this.getText() + "\n" + binding.detailsTextView.getText().toString(), POST_PURPOSE_UPDATE, EMPTY_STRING, true);
                }

                @Override
                public void negativeButtonAction() {

                }
            }.show();
        }
    };

    private final View.OnClickListener createFileButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isNothing(content(binding.detailsTextView))) return;
            new InputDialog(view.getContext(), "", "Full path of file\non the target device", "e.g.  c:\\users\\file.txt") {
                @Override
                public void positiveButtonAction() {
                    if (!this.getText().isEmpty())
                        sendText(this.getText() + "\n" + binding.detailsTextView.getText().toString(), POST_PURPOSE_CREATE, EMPTY_STRING, true);
                }

                @Override
                public void negativeButtonAction() {

                }
            }.show();
        }
    };

    private final View.OnClickListener emphasizeButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (authority.canSend(SendFrom.TextView)){
                sendText(SendFrom.TextView, POST_PURPOSE_EMPHASIZE, determineMeta(view.getContext(), store), false);
            }
        }
    };

    private final View.OnClickListener informButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (authority.canSend(SendFrom.TextView)){
                sendText(SendFrom.TextView, POST_PURPOSE_INFORM, determineMeta(view.getContext(), store), false);
            }
        }
    };

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

    private final View.OnClickListener sendButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (content(binding.sendDropDown).equalsIgnoreCase(TEXT) || content(binding.sendDropDown).isEmpty()) {
                if (authority.canSend(SendFrom.TextView))

                    if (binding.detailsTextView.getText().toString().isEmpty()) {
                        Intent intent = new Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("*/*"); // allow all file types
                        startActivityForResult(intent, PICK_FILE_TO_READ);
                    } else {
                        sendText(SendFrom.TextView, POST_PURPOSE_REGULAR, determineMeta(view.getContext(), store), false);
                    }

            } else {
                if (initialParamsAreSet(view.getContext(), store, machines)) {
                    Intent intent = new Intent(android.content.Intent.ACTION_OPEN_DOCUMENT).addCategory(android.content.Intent.CATEGORY_OPENABLE).setType(authority.getType(content(binding.sendDropDown)));
                    startActivityForResult(intent, PICK_FILE_TO_SEND);
                }
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

    void sendText(SendFrom from, String purpose, String meta, boolean shouldFormatOutput) {
        if (!thereIsInternet(view.getContext())) return;

        factory.transfer.service.sendText(
                view.getContext(),
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(view.getContext(), store),
                        store.getUsername(view.getContext()),
                        store.getID(view.getContext()),
                        Transfer.Intent.writeText,
                        store.getSender(view.getContext()),
                        determineTarget(view.getContext(), store, machines),
                        purpose,
                        meta,
                        from == SendFrom.TextView ?
                                security.encrypt(view.getContext(), store, shouldFormatOutput ? Code.formatOutput(view.getContext(), binding.detailsTextView.getText().toString(), store, device) : binding.detailsTextView.getText().toString()) :
                                security.encrypt(view.getContext(), store, clipboardGetText(context)),
                        Strings.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);

        memento.clear(view.getContext(), store, Strings.TRANSFER_FRAGMENT_MEMENTO_KEY);
    }
    void sendText(String text, String purpose, String meta, boolean shouldFormatOutput) {
        if (!thereIsInternet(view.getContext())) return;

        factory.transfer.service.sendText(
                view.getContext(),
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(view.getContext(), store),
                        store.getUsername(view.getContext()),
                        store.getID(view.getContext()),
                        Transfer.Intent.writeText,
                        store.getSender(view.getContext()),
                        determineTarget(view.getContext(), store, machines),
                        purpose,
                        meta,
                        security.encrypt(view.getContext(), store, shouldFormatOutput ? Code.formatOutput(view.getContext(), text, store, device) : text),
                        Strings.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }
    void sendNote() throws GeneralSecurityException, IOException {
        if (binding.detailsTextView.getText().toString().isEmpty() || binding.memoTitle.getText().toString().isEmpty()) return;

        Memo memo = Memo.create(binding.memoTitle.getText().toString(), binding.detailsTextView.getText().toString(), view.getContext(), store);
        if (thereIsInternet(view.getContext())){
            factory.transfer.service.sendNote(
                    view.getContext(),
                    store,
                    machines,
                    SendNoteRequest.create(HTTP_TRANSFER_URL(view.getContext(), store),
                            store.getUsername(view.getContext()),
                            store.getID(view.getContext()),
                            Transfer.Intent.writeNote,
                            store.getSender(view.getContext()),
                            determineTarget(view.getContext(), store, machines),
                            POST_PURPOSE_WRITE_NOTE,
                            determineMeta(view.getContext(), store),
                            binding.detailsTextView.getText().toString(),
                            memo.getPostnote(),
                            memo.getNoteId(),
                            memo.getNoteTitle(),
                            memo.getNoteDate(),
                            memo.getNoteTime()),
                    DEFAULT_ERROR_MESSAGE_SUFFIX,
                    DEFAULT_FAILURE_MESSAGE_SUFFIX);
        }
        memento.clear(view.getContext(), store, Strings.TRANSFER_FRAGMENT_MEMENTO_KEY);
        cachedMemos = null;
        store.setString(view.getContext(), Strings.MEMO_TOPIC_KEY, binding.memoTitle.getText().toString());
        try {
            memoService.saveNoteToCloud(memo);
        }
        catch (Exception ignored){

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    void receiveText(boolean regardless) {
        if (!thereIsInternet(view.getContext()) || !initialParamsAreSet(view.getContext(), store, machines))
            return;


        /*factory.transfer.service.readText(view.getContext(),
                ReadTextRequest.create(
                        HTTP_TRANSFER_URL,
                        store.getUsername(view.getContext()),
                        store.getID(view.getContext()),
                        Noun.Intent.readText
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);*/

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.readText(
                HTTP_TRANSFER_URL(view.getContext(), store),
                store.getUsername(view.getContext()),
                store.getID(view.getContext()),
                String.valueOf(Transfer.Intent.readText),
                Strings.EMPTY_STRING
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
        if (requestCode == PICK_FILE_TO_SEND && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) uri = data.getData();

            feedback.toast("Sending...", Toast.LENGTH_LONG);
            authority.sendFile(uri);
        }

        if (requestCode == PICK_FILE_TO_READ && resultCode == Activity.RESULT_OK) {
            try {
                binding.detailsTextView.setText(device.readText(view.getContext(), data.getData()));
            } catch (IOException ignored) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}