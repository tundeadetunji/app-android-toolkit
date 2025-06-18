package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_CREATE;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_INFORM;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_UPDATE;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.code.domain.Language;
import com.inovationware.toolkit.code.entity.VocabularyUnit;
import com.inovationware.toolkit.code.service.impl.LanguageServiceImpl;
import com.inovationware.toolkit.code.strategy.impl.LanguageStrategyImpl;
import com.inovationware.toolkit.code.verb.Vocabulary;
import com.inovationware.toolkit.converter.domain.DataUnit;
import com.inovationware.toolkit.converter.service.ConverterService;
import com.inovationware.toolkit.converter.service.impl.ConverterServiceImpl;
import com.inovationware.toolkit.databinding.FragmentConverterBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.InputDialog;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.external.GitHubLite;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.StorageClient;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.global.library.utility.Ui;
import com.inovationware.toolkit.memo.model.Memo;
import com.inovationware.toolkit.memo.service.MemoService;
import com.inovationware.toolkit.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.ui.activity.CodeActivity;
import com.inovationware.toolkit.ui.activity.ConverterActivity;
import com.inovationware.toolkit.ui.adapter.VocabularyRecyclerViewAdapter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

public class ConverterFragment extends Fragment {
    private View view;
    private FragmentConverterBinding binding;
    private ConverterService service;
    private DeviceClient device;
    private Ui ui;
    private Factory factory;
    private SharedPreferencesManager store;
    private GroupManager machines;
    private EncryptionManager security;
    private MemoService memoService;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConverterBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        initializeValues();
        setupUi();
        
        return view;    
    }
    private void setupUi() {
        binding.convertUnitButton.setOnClickListener(handleConvertUnitButtonClick);
        binding.shareConversionButton.setOnClickListener(handleShareConversionButtonClick);
        binding.copyConversionButton.setOnClickListener(handleCopyConversionButtonClick);
        binding.sendConversionButton.setOnClickListener(handleSendConversionButton);
        binding.saveConversionButton.setOnClickListener(saveConversionButtonListener);
        binding.createConversionButton.setOnClickListener(createConversionButtonListener);
        binding.updateConversionButton.setOnClickListener(updateConversionButtonListener);
        binding.saveMemoButton.setOnClickListener(saveMemoButtonListener);
        ui.bindProperty(view.getContext(), binding.fromUnitDropDown, DataUnit.toStringArray());
        ui.bindProperty(view.getContext(), binding.toUnitDropDown, DataUnit.toStringArray());
    }

    private void initializeValues() {
        context = view.getContext();
        service = ConverterServiceImpl.getInstance();
        device = DeviceClient.getInstance();
        ui = Ui.getInstance();
        factory = Factory.getInstance();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        security = EncryptionManager.getInstance();
        memoService = KeepIntentService.getInstance(view.getContext(), store, device);
    }

    private void createConversion(){
        if (Support.isEmpty(binding.conversionResultTextView)) return;
        if (!thereIsInternet(view.getContext())) return;
        createInputDialog(
                Code.formatOutput(view.getContext(), binding.conversionResultTextView.getText().toString(), store, device),
                POST_PURPOSE_CREATE
        ).show();

    }

    private final View.OnClickListener createConversionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createConversion();
        }
    };

    private void updateConversion(){
        if (Support.isEmpty(binding.conversionResultTextView)) return;
        if (!thereIsInternet(view.getContext())) return;
        createInputDialog(
                Code.formatOutput(view.getContext(), binding.conversionResultTextView.getText().toString(), store, device),
                POST_PURPOSE_UPDATE
        ).show();
    }

    private final View.OnClickListener updateConversionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateConversion();
        }
    };

    @SneakyThrows
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createMemo(){
        if (Support.isEmpty(binding.conversionResultTextView)) return;
        //if (!thereIsInternet(view.getContext())) return;
        memoService.saveNoteToCloud(Memo.create(createTitle(), Code.formatOutput(view.getContext(), binding.conversionResultTextView.getText().toString(), store, device), view.getContext(), store));

    }
    private final View.OnClickListener saveMemoButtonListener = new View.OnClickListener() {
        @SneakyThrows
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            createMemo();
        }
    };

    private final View.OnClickListener handleInformConversion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Support.isEmpty(binding.conversionResultTextView)) return;
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
                            POST_PURPOSE_INFORM,
                            determineMeta(view.getContext(), store),
                            security.encrypt(view.getContext(),
                                    store,
                                    Code.formatOutput(view.getContext(), binding.conversionResultTextView.getText().toString(), store, device)),
                            DomainObjects.EMPTY_STRING
                    ),
                    DEFAULT_ERROR_MESSAGE_SUFFIX,
                    DEFAULT_FAILURE_MESSAGE_SUFFIX);
        }
    };

    private final View.OnClickListener handleConvertUnitButtonClick = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            if (Support.isEmpty(binding.fromTextField) || Support.isEmpty(binding.fromUnitDropDown) || Support.isEmpty(binding.toUnitDropDown))
                return;

            BigDecimal result = BigDecimal.ZERO;
            try {
                binding.conversionResultTextView.setText(service.convert(
                        Double.parseDouble(binding.fromTextField.getText().toString()),
                        DataUnit.valueOf(binding.toUnitDropDown.getText().toString()),
                        DataUnit.valueOf(binding.fromUnitDropDown.getText().toString())) + " " + DataUnit.toPlural(result, binding.fromUnitDropDown.getText().toString()));
            } catch (UnsupportedOperationException exception) {
                binding.conversionResultTextView.setText(exception.getMessage().replace("[0x29]", "\n"));
            }
        }
    };

    private void shareConversion(){
        if (Support.isEmpty(binding.conversionResultTextView)) return;
        device.shareText(view.getContext(),
                Code.formatOutput(view.getContext(), binding.conversionResultTextView.getText().toString(), store, device));

    }
    private final View.OnClickListener handleShareConversionButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MenuBottomSheetFragment(getButtons()).show(getActivity().getSupportFragmentManager(), MenuBottomSheetFragment.TAG);
        }
    };

    private final View.OnClickListener handleCopyConversionButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Support.isEmpty(binding.conversionResultTextView)) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                device.toClipboard(Code.formatOutput(view.getContext(), binding.conversionResultTextView.getText().toString(), store, device), view.getContext());
            }
            device.tell(DomainObjects.copiedToClipboardMessage(DomainObjects.CONVERSION), view.getContext());
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveConversion(){
        if (Support.isEmpty(binding.conversionResultTextView)) return;

        String filename = createFilename();

        StorageClient.getInstance(binding.getRoot().getContext()).writeText(
                Code.formatOutput(view.getContext(), binding.conversionResultTextView.getText().toString(), store, device),
                filename,
                filename + " created in Internal Storage.",
                DomainObjects.WRITE_FILE_FAILED
        );
    }

    private final View.OnClickListener saveConversionButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            saveConversion();
        }
    };


    private final View.OnClickListener handleSendConversionButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Support.isEmpty(binding.conversionResultTextView)) return;
            if (!thereIsInternet(view.getContext())) return;

            sendConversion(
                    Code.formatOutput(view.getContext(), binding.conversionResultTextView.getText().toString(), store, device),
                    POST_PURPOSE_REGULAR
            );
        }
    };


    private InputDialog createInputDialog(String text, String purpose) {
        return new InputDialog(view.getContext(), "", "Full path of file\non the target device", "e.g.  c:\\users\\file.txt") {
            @Override
            public void positiveButtonAction() {
                if (purpose.equalsIgnoreCase(POST_PURPOSE_CREATE) || purpose.equalsIgnoreCase(POST_PURPOSE_UPDATE)){
                    if(!this.getText().isEmpty()) sendConversion(this.getText() + "\n" + text, purpose);
                }else{
                    sendConversion(text, purpose);
                }
            }

            @Override
            public void negativeButtonAction() {

            }
        };
    }

    private void sendConversion(String text, String purpose) {
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
                        determineMeta(view.getContext(), store),
                        security.encrypt(view.getContext(),
                                store,
                                text),
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createFilename(){
        return createTitle() + ".txt";
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createTitle(){
        return "Conversion " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm"));
    }

    private List<Ui.ButtonObject> getButtons(){
        Ui.ButtonObject.DimensionInfo dimensions = new Ui.ButtonObject.DimensionInfo(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        Ui.ButtonObject.MarginInfo margins = new Ui.ButtonObject.MarginInfo();


        Ui.ButtonObject.ViewInfo createViewing = new Ui.ButtonObject.ViewInfo("create file", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_insert_drive_file_24, 1);
        Ui.ButtonObject createButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createConversion();
            }
        }, margins, dimensions, createViewing);


        Ui.ButtonObject.ViewInfo memoViewing = new Ui.ButtonObject.ViewInfo("create note", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_sticky_note_2_24, 1);
        Ui.ButtonObject memoButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                createMemo();
            }
        }, margins, dimensions, memoViewing);


        Ui.ButtonObject.ViewInfo updateViewing = new Ui.ButtonObject.ViewInfo("update file", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_edit, 1);
        Ui.ButtonObject updateButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateConversion();
            }
        }, margins, dimensions, updateViewing);


        Ui.ButtonObject.ViewInfo shareViewing = new Ui.ButtonObject.ViewInfo("share this", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_share, 1);
        Ui.ButtonObject shareButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareConversion();
            }
        }, margins, dimensions, shareViewing);


        Ui.ButtonObject.ViewInfo saveViewing = new Ui.ButtonObject.ViewInfo("save to device", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_save, 1);
        Ui.ButtonObject saveButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                saveConversion();
            }
        }, margins, dimensions, saveViewing);






        List<Ui.ButtonObject> buttons = new ArrayList<>();
        buttons.add(shareButton);
        buttons.add(createButton);
        buttons.add(updateButton);
        buttons.add(memoButton);
        buttons.add(saveButton);

        return buttons;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}