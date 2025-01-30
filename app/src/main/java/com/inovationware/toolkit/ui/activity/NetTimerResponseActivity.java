package com.inovationware.toolkit.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.StorageClient;
import com.inovationware.toolkit.global.repository.Repo;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.memo.service.impl.KeepIntentService;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.DETAILS;
import static com.inovationware.toolkit.global.domain.Strings.HEADLINE;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.Strings.NET_TIMER_REPLY_DELIMITER;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_NET_TIMER_EMPHASIZE;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_NET_TIMER_INFORM;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_NET_TIMER_REPLY_APPEND_ORIGINAL_KEY;
import static com.inovationware.toolkit.global.domain.Strings.TARGET_MODE_TO_DEVICE;
import static com.inovationware.toolkit.global.domain.Strings.TARGET_MODE_TO_GROUP;
import static com.inovationware.toolkit.global.domain.Strings.TIME_STRING;
import static com.inovationware.toolkit.global.domain.Strings.ZONE_STRING;
import static com.inovationware.toolkit.global.library.utility.Code.content;
import static com.inovationware.toolkit.global.library.utility.Code.isNothing;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;

public class NetTimerResponseActivity extends AppCompatActivity {
    private GroupManager machines;
    private SharedPreferencesManager store;

    private TextView objectHeadlineTextView, objectDetailsTextView, objectTimeTextView, replyTextView;
    private Button stressReplyButton, informReplyButton, sendReplyButton, shareButton;
    private CheckBox appendOriginalMessageCheckBox;

    private AutoCompleteTextView replyToDeviceDropDown;

    private String headline = "";
    private String details = "";
    private String time_string = "";
    private String zone_string = "";

    private Feedback feedback;

    private Factory factory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_timer_response);

        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        feedback = new Feedback(getApplicationContext());
        factory = Factory.getInstance();

        if (getIntent().getExtras() != null) {
            headline = getIntent().getStringExtra(HEADLINE);
            details = getIntent().getStringExtra(DETAILS);
            time_string = getIntent().getStringExtra(TIME_STRING);
            zone_string = getIntent().getStringExtra(ZONE_STRING);
        }

        findViewById(R.id.netTimerSaveMemo).setOnClickListener(saveMemo);
        findViewById(R.id.netTimerSaveLocal).setOnClickListener(saveLocal);

        objectDetailsTextView = findViewById(R.id.objectDetailsTextView);
        objectDetailsTextView.setText(details);

        objectHeadlineTextView = findViewById(R.id.objectHeadlineTextView);
        objectHeadlineTextView.setText(headline);

        objectTimeTextView = findViewById(R.id.objectTimeTextView);
        objectTimeTextView.setText("Sent\n" + time_string + "\n" + zone_string);

        replyTextView = findViewById(R.id.replyTextView);

        replyToDeviceDropDown = findViewById(R.id.replyToDeviceDropDown);
        setupReplyToDevices();

        appendOriginalMessageCheckBox = findViewById(R.id.appendOriginalMessageCheckBox);
        appendOriginalMessageCheckBox.setChecked(store.getChecked(NetTimerResponseActivity.this, SHARED_PREFERENCES_NET_TIMER_REPLY_APPEND_ORIGINAL_KEY, false));
        appendOriginalMessageCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                store.setChecked(NetTimerResponseActivity.this, SHARED_PREFERENCES_NET_TIMER_REPLY_APPEND_ORIGINAL_KEY, appendOriginalMessageCheckBox.isChecked());
            }
        });

        stressReplyButton = findViewById(R.id.stressReplyButton);
        stressReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNothing(content(replyToDeviceDropDown))) {
                    Toast.makeText(NetTimerResponseActivity.this, "Select a target...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (canSend())
                    doSend(POST_PURPOSE_NET_TIMER_EMPHASIZE, constructMessage(appendOriginalMessageCheckBox.isChecked()), content(replyToDeviceDropDown));
            }
        });

        informReplyButton = findViewById(R.id.informReplyButton);
        informReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNothing(content(replyToDeviceDropDown))) {
                    Toast.makeText(NetTimerResponseActivity.this, "Select a target...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (canSend())
                    doSend(POST_PURPOSE_NET_TIMER_INFORM, constructMessage(appendOriginalMessageCheckBox.isChecked()), content(replyToDeviceDropDown));
            }
        });

        sendReplyButton = findViewById(R.id.sendReplyButton);
        sendReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNothing(content(replyToDeviceDropDown))) {
                    Toast.makeText(NetTimerResponseActivity.this, "Select a target...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (canSend())
                    doSend(POST_PURPOSE_REGULAR, constructMessage(appendOriginalMessageCheckBox.isChecked()), content(replyToDeviceDropDown));
            }
        });


        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factory.device.shareText(NetTimerResponseActivity.this, constructMessage(appendOriginalMessageCheckBox.isChecked()));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_back_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.backHomeBackMenuItem) {
            startActivity(new Intent(NetTimerResponseActivity.this, NetTimerActivity.class));
            return true;
        } else if (item.getItemId() == R.id.homeHomeBackMenuItem) {
            startActivity(new Intent(NetTimerResponseActivity.this, MainActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    void setupReplyToDevices() {
        String[] machinesList = Arrays.copyOf(machines.list(NetTimerResponseActivity.this), machines.list(NetTimerResponseActivity.this).length + 1);
        machinesList[machinesList.length - 1] = store.getID(NetTimerResponseActivity.this);

        replyToDeviceDropDown.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.default_drop_down, machinesList));

    }

    boolean canSend() {
        return initialParamsAreSet(NetTimerResponseActivity.this, store, machines) && !isNothing(content(replyToDeviceDropDown));
    }

    void doSend(String purpose, String info, String target) {
        if (!thereIsInternet(getApplicationContext())) return;

        Retrofit retrofitImpl = Repo.getInstance().create(NetTimerResponseActivity.this, store);

        Call<String> navigate = retrofitImpl.sendText(
                HTTP_TRANSFER_URL(NetTimerResponseActivity.this, store),
                store.getUsername(NetTimerResponseActivity.this),
                store.getID(NetTimerResponseActivity.this),
                String.valueOf(Transfer.Intent.writeText),
                store.getSender(NetTimerResponseActivity.this),
                target,
                purpose,
                determineTargetMode(target),
                info,
                Strings.EMPTY_STRING);

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (!response.body().isEmpty())
                            new Feedback(NetTimerResponseActivity.this).toast(response.body());
                } else {
                    if (!store.shouldDisplayErrorMessage(NetTimerResponseActivity.this)) {
                        return;
                    }
                    feedback.toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (!store.shouldDisplayErrorMessage(NetTimerResponseActivity.this)) {
                    return;
                }
                feedback.toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
            }
        });
    }

    String determineTargetMode(String target) {
        return target.equalsIgnoreCase(store.getID(NetTimerResponseActivity.this)) ? TARGET_MODE_TO_GROUP : TARGET_MODE_TO_DEVICE;
    }

    String constructMessage(boolean appendOriginal) {
        return new StringBuilder(content(replyTextView))
                .append(appendOriginal ? constructOriginalMessage() : "")
                .toString();
    }

    String constructOriginalMessage() {
        return "\n\n\n" + NET_TIMER_REPLY_DELIMITER + ":\n" + headline + "\n" + details + "\nSent " + time_string + "\nSender Time Zone: " + zone_string;
    }

    private final View.OnClickListener saveMemo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                KeepIntentService.getInstance(NetTimerResponseActivity.this, store, DeviceClient.getInstance()).saveNoteToCloud(Memo.create(
                        ((TextView) findViewById(R.id.objectHeadlineTextView)).getText().toString(),
                        constructMessage(appendOriginalMessageCheckBox.isChecked()),
                        NetTimerResponseActivity.this, store));
            } catch (IOException ignored) {
            }

        }
    };

    private final View.OnClickListener saveLocal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isNothing(content(replyToDeviceDropDown))) {
                Toast.makeText(NetTimerResponseActivity.this, "Select a target...", Toast.LENGTH_SHORT).show();
                return;
            }
            //Todo make filename more descriptive by adding time/date of original message
            String filename = ((TextView) findViewById(R.id.objectHeadlineTextView)).getText().toString() + ".txt";
            StorageClient.getInstance(NetTimerResponseActivity.this).writeText(constructMessage(appendOriginalMessageCheckBox.isChecked()), filename,
                    filename + " created in Internal Storage.", Strings.WRITE_FILE_FAILED);
        }
    };




}