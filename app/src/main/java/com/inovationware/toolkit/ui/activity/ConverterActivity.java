package com.inovationware.toolkit.ui.activity;

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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.core.view.MenuCompat;

import com.google.android.material.button.MaterialButton;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.converter.domain.DataUnit;
import com.inovationware.toolkit.converter.service.ConverterService;
import com.inovationware.toolkit.converter.service.impl.ConverterServiceImpl;
import com.inovationware.toolkit.databinding.ActivityConverterBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.InputDialog;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.StorageClient;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.global.library.utility.Ui;
import com.inovationware.toolkit.memo.model.Memo;
import com.inovationware.toolkit.memo.service.MemoService;
import com.inovationware.toolkit.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.ui.contract.BaseActivity;
import com.inovationware.toolkit.ui.fragment.MenuBottomSheetFragment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

public class ConverterActivity extends BaseActivity {
    private ActivityConverterBinding binding;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConverterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeValues();
        setupUi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.homeHomeMenuItem) {
            startActivity(new Intent(ConverterActivity.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        ui.bindProperty(ConverterActivity.this, binding.fromUnitDropDown, DataUnit.toStringArray());
        ui.bindProperty(ConverterActivity.this, binding.toUnitDropDown, DataUnit.toStringArray());
    }

    private void initializeValues() {
        context = ConverterActivity.this;
        service = ConverterServiceImpl.getInstance();
        device = DeviceClient.getInstance();
        ui = Ui.getInstance();
        factory = Factory.getInstance();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        security = EncryptionManager.getInstance();
        memoService = KeepIntentService.getInstance(ConverterActivity.this, store, device);
    }

    private void createConversion(){
        if (Support.isEmpty(binding.conversionResultTextView)) return;
        if (!thereIsInternet(ConverterActivity.this)) return;
        createInputDialog(
                Code.formatOutput(ConverterActivity.this, binding.conversionResultTextView.getText().toString(), store, device),
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
        if (!thereIsInternet(ConverterActivity.this)) return;
        createInputDialog(
                Code.formatOutput(ConverterActivity.this, binding.conversionResultTextView.getText().toString(), store, device),
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
        //if (!thereIsInternet(ConverterActivity.this)) return;
        memoService.saveNoteToCloud(Memo.create(createTitle(), Code.formatOutput(ConverterActivity.this, binding.conversionResultTextView.getText().toString(), store, device), ConverterActivity.this, store));

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
            if (!thereIsInternet(ConverterActivity.this)) return;

            factory.transfer.service.sendText(
                    ConverterActivity.this,
                    store,
                    machines,
                    SendTextRequest.create(HTTP_TRANSFER_URL(ConverterActivity.this, store),
                            store.getUsername(ConverterActivity.this),
                            store.getID(ConverterActivity.this),
                            Transfer.Intent.writeText,
                            store.getSender(ConverterActivity.this),
                            determineTarget(ConverterActivity.this, store, machines),
                            POST_PURPOSE_INFORM,
                            determineMeta(ConverterActivity.this, store),
                            security.encrypt(ConverterActivity.this,
                                    store,
                                    Code.formatOutput(ConverterActivity.this, binding.conversionResultTextView.getText().toString(), store, device)),
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
        device.shareText(ConverterActivity.this,
                Code.formatOutput(ConverterActivity.this, binding.conversionResultTextView.getText().toString(), store, device));

    }
    private final View.OnClickListener handleShareConversionButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MenuBottomSheetFragment(getButtons()).show(getSupportFragmentManager(), MenuBottomSheetFragment.TAG);
        }
    };

    private final View.OnClickListener handleCopyConversionButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Support.isEmpty(binding.conversionResultTextView)) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                device.toClipboard(Code.formatOutput(ConverterActivity.this, binding.conversionResultTextView.getText().toString(), store, device), ConverterActivity.this);
            }
            device.tell(DomainObjects.copiedToClipboardMessage(DomainObjects.CONVERSION), ConverterActivity.this);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveConversion(){
        if (Support.isEmpty(binding.conversionResultTextView)) return;

        String filename = createFilename();

        StorageClient.getInstance(binding.getRoot().getContext()).writeText(
                Code.formatOutput(ConverterActivity.this, binding.conversionResultTextView.getText().toString(), store, device),
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
            if (!thereIsInternet(ConverterActivity.this)) return;

            sendConversion(
                    Code.formatOutput(ConverterActivity.this, binding.conversionResultTextView.getText().toString(), store, device),
                    POST_PURPOSE_REGULAR
            );
        }
    };


    private InputDialog createInputDialog(String text, String purpose) {
        return new InputDialog(ConverterActivity.this, "", "Full path of file\non the target device", "e.g.  c:\\users\\file.txt") {
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
                ConverterActivity.this,
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(ConverterActivity.this, store),
                        store.getUsername(ConverterActivity.this),
                        store.getID(ConverterActivity.this),
                        Transfer.Intent.writeText,
                        store.getSender(ConverterActivity.this),
                        determineTarget(ConverterActivity.this, store, machines),
                        purpose,
                        determineMeta(ConverterActivity.this, store),
                        security.encrypt(ConverterActivity.this,
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


        Ui.ButtonObject.ViewInfo memoViewing = new Ui.ButtonObject.ViewInfo("create memo", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_sticky_note_2_24, 1);
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


        Ui.ButtonObject.ViewInfo shareViewing = new Ui.ButtonObject.ViewInfo("share", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_share, 1);
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
}