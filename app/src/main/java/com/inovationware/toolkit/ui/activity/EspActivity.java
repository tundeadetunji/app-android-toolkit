package com.inovationware.toolkit.ui.activity;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_CREATE;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_UPDATE;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.inovationware.toolkit.databinding.ActivityEspBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.esp.model.Configuration;
import com.inovationware.toolkit.esp.model.domain.BasePlatform;
import com.inovationware.toolkit.esp.model.domain.Board;
import com.inovationware.toolkit.esp.model.domain.Control;
import com.inovationware.toolkit.esp.model.domain.LogLevel;
import com.inovationware.toolkit.esp.model.domain.Mode;
import com.inovationware.toolkit.esp.model.domain.SensorPlatform;
import com.inovationware.toolkit.esp.model.domain.TimingValue;
import com.inovationware.toolkit.esp.model.domain.Unit;
import com.inovationware.toolkit.esp.model.value.Api;
import com.inovationware.toolkit.esp.model.value.Component;
import com.inovationware.toolkit.esp.model.value.Filter;
import com.inovationware.toolkit.esp.model.value.Logger;
import com.inovationware.toolkit.esp.model.value.Sensor;
import com.inovationware.toolkit.esp.model.value.Wifi;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.InputDialog;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.StorageClient;
import com.inovationware.toolkit.global.library.utility.Ui;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.ui.contract.BaseActivity;

import java.io.IOException;
import java.util.List;

public class EspActivity extends BaseActivity {
    private ActivityEspBinding binding;
    private Ui ui;
    private Context context;
    private Factory factory;
    private GroupManager machines;
    private SharedPreferencesManager store;
    private EncryptionManager security;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEspBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeVariables();
        setupUi();
    }

    private void initializeVariables() {
        context = EspActivity.this;
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

        binding.saveConfig.setOnClickListener(saveConfigHandler);
        binding.shareConfig.setOnClickListener(shareConfigHandler);
        binding.createConfigFile.setOnClickListener(createConfigFileHandler);
        binding.updateConfigFile.setOnClickListener(updateConfigFileHandler);
        binding.copyConfig.setOnClickListener(copyConfigHandler);
        binding.sendConfig.setOnClickListener(sendConfigHandler);
        binding.saveMemo.setOnClickListener(saveMemo);
    }

    private final View.OnClickListener saveMemo = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            if (!thereIsInput()) return;
            try {
                KeepIntentService.getInstance(context, store, factory.device).saveNoteToCloud(Memo.create(
                        createTitle(),
                        Code.formatOutput(context, createConfiguration().toString(), store, factory.device),
                        context, store));
            } catch (IOException ignored) {
            }

        }
    };

    private final View.OnClickListener saveConfigHandler = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            if (!thereIsInput()) return;

            String filename = binding.baseName.getText().toString() + ".yaml";

            StorageClient.getInstance(context).writeText(createConfiguration().toString(), filename,
                    filename + " created in Internal Storage.", Strings.WRITE_FILE_FAILED);

        }
    };

    private final View.OnClickListener shareConfigHandler = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            if (!thereIsInput()) return;

            factory.device.shareText(context, createConfiguration().toString());
        }
    };

    private final View.OnClickListener createConfigFileHandler = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            if (!thereIsInput()) return;
            if (!thereIsInternet(context)) return;

            inputDialog(security.encrypt(context, store, createConfiguration().toString()), POST_PURPOSE_CREATE).show();
        }
    };

    private final View.OnClickListener updateConfigFileHandler = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            if (!thereIsInput()) return;
            if (!thereIsInternet(context)) return;

            inputDialog(security.encrypt(context, store, createConfiguration().toString()), POST_PURPOSE_UPDATE).show();
        }
    };

    private final View.OnClickListener copyConfigHandler = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            if (!thereIsInput()) return;

            factory.device.toClipboard(createConfiguration().toString(), context);
            factory.device.tell(Strings.copiedToClipboardMessage("Configuration"), context);

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
                        Strings.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);

    }

    private String createTitle(){
        return binding.baseName.getText().toString();
    }

}