package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_CREATE;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_UPDATE;
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
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentEspBinding;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.features.esp.model.Configuration;
import com.inovationware.toolkit.features.esp.model.domain.BasePlatform;
import com.inovationware.toolkit.features.esp.model.domain.Board;
import com.inovationware.toolkit.features.esp.model.domain.Control;
import com.inovationware.toolkit.features.esp.model.domain.LogLevel;
import com.inovationware.toolkit.features.esp.model.domain.Mode;
import com.inovationware.toolkit.features.esp.model.domain.SensorPlatform;
import com.inovationware.toolkit.features.esp.model.domain.TimingValue;
import com.inovationware.toolkit.features.esp.model.domain.Unit;
import com.inovationware.toolkit.features.esp.model.value.Api;
import com.inovationware.toolkit.features.esp.model.value.Component;
import com.inovationware.toolkit.features.esp.model.value.Filter;
import com.inovationware.toolkit.features.esp.model.value.Logger;
import com.inovationware.toolkit.features.esp.model.value.Sensor;
import com.inovationware.toolkit.features.esp.model.value.Wifi;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.utility.EncryptionManager;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.InputDialog;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.Code;
import com.inovationware.toolkit.common.utility.StorageClient;
import com.inovationware.toolkit.common.utility.Ui;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.features.memo.service.impl.KeepIntentService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EspFragment extends Fragment {

    private View view;
    private FragmentEspBinding binding;

    private Ui ui;
    private Context context;
    private Factory factory;
    private GroupManager machines;
    private SharedPreferencesManager store;
    private EncryptionManager security;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEspBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        initializeVariables();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setupUi();
        }

        return view;
    }


    private void initializeVariables() {
        context = view.getContext();
        ui = Ui.getInstance();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        security = EncryptionManager.getInstance();
        factory = Factory.getInstance();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupUi() {
        ui.bindProperty(context, binding.basePlatform, BasePlatform.listing());
        ui.bindProperty(context, binding.baseBoard, Board.listing());
        ui.bindProperty(context, binding.sensorPlatform, SensorPlatform.listing());
        ui.bindProperty(context, binding.sensorTempUnit, Unit.listing());
        ui.bindProperty(context, binding.sensorHumidityUnit, Unit.listing());
        ui.bindProperty(context, binding.sensorMode, Mode.listing());
        ui.bindProperty(context, binding.level, LogLevel.listing());
        ui.bindProperty(context, binding.sensorDelayedOff, TimingValue.listing());
        ui.bindProperty(context, binding.sensorDelayedOn, TimingValue.listing());

        binding.shareConfig.setOnClickListener(shareConfigButtonListener);
        binding.copyConfig.setOnClickListener(copyConfigHandler);
        binding.sendConfig.setOnClickListener(sendConfigHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createMemo(){
        if (!thereIsInput()) return;
        try {
            KeepIntentService.getInstance(context, store, factory.device).saveNoteToCloud(Memo.create(
                    createTitle(),
                    Code.formatOutput(context, createConfiguration().toString(), store, factory.device),
                    context, store));
        } catch (IOException ignored) {
        }

    }

    private final View.OnClickListener saveMemoButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            createMemo();
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveConfigToDevice(){
        if (!thereIsInput()) return;

        String filename = binding.baseName.getText().toString() + ".yaml";

        StorageClient.getInstance(context).writeText(createConfiguration().toString(), filename,
                filename + " created in Internal Storage.", DomainObjects.WRITE_FILE_FAILED);

    }
    private final View.OnClickListener saveConfigButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            saveConfigToDevice();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void shareConfig(){
        if (!thereIsInput()) return;

        factory.device.shareText(context, createConfiguration().toString());

    }
    private final View.OnClickListener shareConfigButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            shareConfig();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    private  void createConfigFile(){
        if (!thereIsInput()) return;
        if (!thereIsInternet(context)) return;

        inputDialog(security.encrypt(context, store, createConfiguration().toString()), POST_PURPOSE_CREATE).show();
    }
    private final View.OnClickListener createConfigFileListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            createConfigFile();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateConfigFile(){
        if (!thereIsInput()) return;
        if (!thereIsInternet(context)) return;

        inputDialog(security.encrypt(context, store, createConfiguration().toString()), POST_PURPOSE_UPDATE).show();

    }

    private final View.OnClickListener updateConfigFileButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            updateConfigFile();
        }
    };

    private final View.OnClickListener copyConfigHandler = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            if (!thereIsInput()) return;

            factory.device.toClipboard(createConfiguration().toString(), context);
            factory.device.tell(DomainObjects.copiedToClipboardMessage("Configuration"), context);

        }
    };

    private final View.OnClickListener sendConfigHandler = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            if (!thereIsInput()) return;
            if (!thereIsInternet(context)) return;

            sendConfig(security.encrypt(context, store, createConfiguration().toString()), POST_PURPOSE_REGULAR);
        }
    };

    private boolean thereIsInput() {
        return true;
    }

    private Configuration createConfiguration() {
        Wifi wifi = Wifi.create(binding.wifiSsid.getText().toString(), binding.wifiPassword.getText().toString());
        Api api = Api.create(binding.apiPassword.getText().toString());
        Component temp = Component.create(Control.temperature, binding.sensorTempName.getText().toString(), Unit.valueOf(binding.sensorTempUnit.getText().toString()));
        Component humidity = Component.create(Control.humidity, binding.sensorHumidityName.getText().toString(), Unit.valueOf(binding.sensorHumidityUnit.getText().toString()));

        Sensor sensor = Sensor.create(
                SensorPlatform.valueOf(binding.sensorPlatform.getText().toString()),
                Integer.parseInt(binding.sensorPin.getText().toString()),
                List.of(temp, humidity),
                Mode.valueOf(binding.sensorMode.getText().toString()),
                binding.sensorName.getText().toString(),
                Filter.create(TimingValue.valueOf(binding.sensorDelayedOn.getText().toString()), TimingValue.valueOf(binding.sensorDelayedOff.getText().toString()))
        );

        return Configuration.create(
                binding.baseName.getText().toString(),
                BasePlatform.valueOf(binding.basePlatform.getText().toString()),
                Board.valueOf(binding.baseBoard.getText().toString()),
                wifi,
                api,
                List.of(sensor),
                Logger.create(LogLevel.valueOf(binding.level.getText().toString()))
        );
    }

    private final InputDialog inputDialog(String text, String purpose) {
        return new InputDialog(context, "Esp Config", "Full path of file\non the target device", "e.g.  c:\\users\\file.txt") {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void positiveButtonAction() {
                if (!this.getText().isEmpty()) sendConfig(this.getText() + "\n" + text, purpose);
            }

            @Override
            public void negativeButtonAction() {
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendConfig(String text, String purpose) {
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
                        purpose,
                        determineMeta(context, store),
                        text,
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);

    }

    private String createTitle(){
        return binding.baseName.getText().toString();
    }

    private List<Ui.ButtonObject> getButtons(){
        Ui.ButtonObject.DimensionInfo dimensions = new Ui.ButtonObject.DimensionInfo(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        Ui.ButtonObject.MarginInfo margins = new Ui.ButtonObject.MarginInfo();


        Ui.ButtonObject.ViewInfo shareViewing = new Ui.ButtonObject.ViewInfo("share", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_share, 1);
        Ui.ButtonObject shareButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                shareConfig();
            }
        }, margins, dimensions, shareViewing);


        Ui.ButtonObject.ViewInfo createViewing = new Ui.ButtonObject.ViewInfo("share", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_insert_drive_file_24, 1);
        Ui.ButtonObject createButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                createConfigFile();
            }
        }, margins, dimensions, createViewing);


        Ui.ButtonObject.ViewInfo updateViewing = new Ui.ButtonObject.ViewInfo("update", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_edit, 1);
        Ui.ButtonObject updateButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                updateConfigFile();
            }
        }, margins, dimensions, updateViewing);


        Ui.ButtonObject.ViewInfo memoViewing = new Ui.ButtonObject.ViewInfo("create memo", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_sticky_note_2_24, 1);
        Ui.ButtonObject memoButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                createMemo();
            }
        }, margins, dimensions, memoViewing);


        Ui.ButtonObject.ViewInfo saveViewing = new Ui.ButtonObject.ViewInfo("save to device", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_save, 1);
        Ui.ButtonObject saveButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                saveConfigToDevice();
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