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
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
import com.inovationware.toolkit.databinding.FragmentCodeBinding;
import com.inovationware.toolkit.databinding.FragmentSendBinding;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendNoteRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.features.datatransfer.dto.response.ResponseEntity;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.features.memo.service.MemoService;
import com.inovationware.toolkit.features.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.ui.adapter.ViewPagerAdapter;
import com.inovationware.toolkit.ui.memento.UiMemento;
import com.inovationware.toolkit.ui.support.TransferAuthority;

import java.io.IOException;
import java.security.GeneralSecurityException;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFragment extends Fragment {
    private FragmentSendBinding binding;
    private View view;

    private Factory factory;
    private TransferAuthority authority;
    private GroupManager machines;
    private SharedPreferencesManager store;
    private Context context;
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
        binding = FragmentSendBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupVariables();
        preSetupUi();
        setupUi();
        setupListeners();

        return view;
    }


    private void setupVariables(){
        factory = Factory.getInstance();
        feedback = new Feedback(view.getContext());
        context = view.getContext();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        device = DeviceClient.getInstance();
        dropdownAdapter = dtoDropDownAdapter(view.getContext());
        security = EncryptionManager.getInstance();
        memoService = KeepIntentService.getInstance(view.getContext(), store, device);
        memento = UiMemento.getInstance();
        authority = new TransferAuthority(factory, context, machines, store);
    }

    private void preSetupUi(){
        memento.restoreTo(view.getContext(), store, DomainObjects.TRANSFER_FRAGMENT_MEMENTO_KEY, binding.detailsTextView);
    }

    private void setupUi(){
        binding.sendDropDown.setAdapter(dropdownAdapter);
        store.setDropDown(context, binding.memoTitle, factory.memo.titles.listing(context, store));

    }
    private void setupListeners(){
        binding.noteButton.setOnClickListener(noteButtonHandler);
        binding.updateFileButton.setOnClickListener(updateFileButtonHandler);
        binding.createFileButton.setOnClickListener(createFileButtonHandler);
        binding.emphasizeButton.setOnClickListener(emphasizeButtonHandler);
        binding.informButton.setOnClickListener(informButtonHandler);
        binding.sendButton.setOnClickListener(sendButtonHandler);
    }

    private final View.OnClickListener noteButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!authority.canSend(DomainObjects.SendFrom.TextView)) return;

            if (binding.memoTitle.getText().toString().isEmpty()) {
                binding.memoTitle.setText(store.getString(view.getContext(), DomainObjects.MEMO_TOPIC_KEY));
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
            if (authority.canSend(DomainObjects.SendFrom.TextView)){
                sendText(DomainObjects.SendFrom.TextView, POST_PURPOSE_EMPHASIZE, determineMeta(view.getContext(), store), false);
            }
        }
    };

    private final View.OnClickListener informButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (authority.canSend(DomainObjects.SendFrom.TextView)){
                sendText(DomainObjects.SendFrom.TextView, POST_PURPOSE_INFORM, determineMeta(view.getContext(), store), false);
            }
        }
    };

    private final View.OnClickListener sendButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (content(binding.sendDropDown).equalsIgnoreCase(TEXT) || content(binding.sendDropDown).isEmpty()) {
                if (authority.canSend(DomainObjects.SendFrom.TextView))

                    if (binding.detailsTextView.getText().toString().isEmpty()) {
                        Intent intent = new Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("*/*"); // allow all file types
                        startActivityForResult(intent, PICK_FILE_TO_READ);
                    } else {
                        sendText(DomainObjects.SendFrom.TextView, POST_PURPOSE_REGULAR, determineMeta(view.getContext(), store), false);
                    }

            } else {
                if (initialParamsAreSet(view.getContext(), store, machines)) {
                    Intent intent = new Intent(android.content.Intent.ACTION_OPEN_DOCUMENT).addCategory(android.content.Intent.CATEGORY_OPENABLE).setType(authority.getType(content(binding.sendDropDown)));
                    startActivityForResult(intent, PICK_FILE_TO_SEND);
                }
            }
        }
    };


    void sendText(DomainObjects.SendFrom from, String purpose, String meta, boolean shouldFormatOutput) {
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
                        from == DomainObjects.SendFrom.TextView ?
                                security.encrypt(view.getContext(), store, shouldFormatOutput ? Code.formatOutput(view.getContext(), binding.detailsTextView.getText().toString(), store, device) : binding.detailsTextView.getText().toString()) :
                                security.encrypt(view.getContext(), store, clipboardGetText(context)),
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);

        memento.clear(view.getContext(), store, DomainObjects.TRANSFER_FRAGMENT_MEMENTO_KEY);
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
                        DomainObjects.EMPTY_STRING
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
        memento.clear(view.getContext(), store, DomainObjects.TRANSFER_FRAGMENT_MEMENTO_KEY);
        cachedMemos = null;
        store.setString(view.getContext(), DomainObjects.MEMO_TOPIC_KEY, binding.memoTitle.getText().toString());
        factory.memo.titles.addTitle(context, binding.memoTitle.getText().toString(), store);
        try {
            memoService.saveNoteToCloud(memo);
        }
        catch (Exception ignored){

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FILE_TO_SEND && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) uri = data.getData();

            feedback.toast("Sending...", Toast.LENGTH_LONG);
            authority.sendFile(uri, binding.sendDropDown);
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