package com.inovationware.toolkit.ui.activity;

import androidx.core.view.MenuCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.ActivitySettingsBinding;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.common.utility.MessageBox;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.Code;
import com.inovationware.toolkit.ui.contract.BaseActivity;

import static com.inovationware.toolkit.common.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_APPEND_TIMEZONE_WHEN_SENDING;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_APPEND_UUID_WHEN_SENDING;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_PROMPT_TO_SYNC_NOTE;
import static com.inovationware.toolkit.common.utility.Code.content;
import static com.inovationware.toolkit.common.utility.Code.isNothing;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_DISPLAY_ERROR_MESSAGE;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_ID_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_SENDER;
import static com.inovationware.toolkit.common.utility.Support.*;

public class SettingsActivity extends BaseActivity {
    private ActivitySettingsBinding binding;
    private Factory factory;
    private GroupManager machines;
    private SharedPreferencesManager store;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupProperties();
        setupListeners();
        setupUi();
    }

    private void setupProperties(){
        context = binding.getRoot().getContext();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        factory = Factory.getInstance();
    }
    private void setupListeners(){
        binding.vibrateFeedbackOnlyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                store.setHapticFeedbackOnly(context, binding.vibrateFeedbackOnlyCheckBox.isChecked());
            }
        });

        binding.vibrateOnAcknowledgementCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                store.setHapticFeedbackOnAcknowledgement(context, binding.vibrateOnAcknowledgementCheckBox.isChecked());
            }
        });

        binding.baseUrlSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(binding.baseUrlTextView)) return;
                MessageBox template = new MessageBox() {
                    @Override
                    public void positiveButtonAction() {
                        store.setBaseUrl(SettingsActivity.this, binding.baseUrlTextView.getText().toString());
                        announce(SettingsActivity.this, "Hotspot information saved.");
                    }
                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Really set hotspot information?");
                template.setPositiveButtonText(DomainObjects.yes);
                template.setNegativeButtonText(DomainObjects.never_mind);
                template.show(SettingsActivity.this);
            }
        });

        binding.baseUrlClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageBox template = new MessageBox() {
                    @Override
                    public void positiveButtonAction() {
                        store.clearBaseUrl(SettingsActivity.this, binding.baseUrlTextView);
                        announce(SettingsActivity.this, "Hotspot information cleared.");
                    }
                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Really clear hotspot information?");
                template.setPositiveButtonText(DomainObjects.yes);
                template.setNegativeButtonText(DomainObjects.never_mind);
                template.show(SettingsActivity.this);
            }
        });


        binding.saveUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageBox template = new MessageBox() {
                    @Override
                    public void positiveButtonAction() {
                        //assuming username is the one that is supplied by the user during registration
                        if (!Code.isEmail(binding.usernameTextView.getText().toString())){
                            Toast.makeText(SettingsActivity.this, "Please enter the email used to register.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        store.setUsername(SettingsActivity.this, binding.usernameTextView.getText().toString());
                        announce(SettingsActivity.this, "Username has been saved");
                    }

                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Really save this username?");
                template.setPositiveButtonText(DomainObjects.yes);
                template.setNegativeButtonText(DomainObjects.never_mind);
                template.show(SettingsActivity.this);

            }
        });
        binding.resetSenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                store.clearSender(SettingsActivity.this, binding.senderTextView);
            }
        });
        binding.clearIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                store.clearID(SettingsActivity.this, binding.IDTextView);
            }
        });
        binding.saveDefaultDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageBox template = new MessageBox() {
                    @Override
                    public void positiveButtonAction() {
                        store.setTargetMode(SettingsActivity.this, content(binding.targetModeDropDown));
                        announce(SettingsActivity.this, "Sending will be " + content(binding.targetModeDropDown).toLowerCase() + " by default.");
                    }

                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Really set sending " + content(binding.targetModeDropDown).toLowerCase() + " as default?");
                template.setPositiveButtonText(DomainObjects.yes);
                template.setNegativeButtonText(DomainObjects.never_mind);
                template.show(SettingsActivity.this);
            }
        });
        binding.clearMachinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                machines.setDropDown(SettingsActivity.this,binding.machinesDropDown, machines.removeAll(SettingsActivity.this,binding.machinesDropDown));
            }
        });
        binding.saveMachineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageBox template = new MessageBox() {
                    @Override
                    public void positiveButtonAction() {
                        machines.addNew(SettingsActivity.this,content(binding.machinesDropDown));
                        announce(SettingsActivity.this, "Device has been added.");
                    }

                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Really add this device?");
                template.setPositiveButtonText(DomainObjects.yes);
                template.setNegativeButtonText(DomainObjects.never_mind);
                template.show(SettingsActivity.this);

            }
        });
        binding.saveMachineAsDefaultTargetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageBox template = new MessageBox() {
                    @Override
                    public void positiveButtonAction() {
                        machines.addNew(SettingsActivity.this,content(binding.machinesDropDown));
                        machines.setDefaultDevice(SettingsActivity.this,content(binding.machinesDropDown), binding.machinesDropDown);
                        announce(SettingsActivity.this, content(binding.machinesDropDown) + " is now default.");
                    }

                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Really set " + content(binding.machinesDropDown) + " as default?");
                template.setPositiveButtonText(DomainObjects.yes);
                template.setNegativeButtonText(DomainObjects.never_mind);
                template.show(SettingsActivity.this);
            }
        });
        binding.saveSenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageBox template = new MessageBox() {
                    @Override
                    public void positiveButtonAction() {
                        store.setString(SettingsActivity.this, SHARED_PREFERENCES_SENDER, !isNothing(content(binding.senderTextView)) ? content(binding.senderTextView) : Settings.Secure.getString(view.getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                        announce(SettingsActivity.this, DomainObjects.value_saved);
                    }

                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Really set this alias?");
                template.setPositiveButtonText(DomainObjects.yes);
                template.setNegativeButtonText(DomainObjects.never_mind);
                template.show(SettingsActivity.this);
            }
        });
        binding.saveIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageBox template = new MessageBox() {
                    @Override
                    public void positiveButtonAction() {
                        store.setString(SettingsActivity.this, SHARED_PREFERENCES_ID_KEY, binding.IDTextView.getText().toString());
                        announce(SettingsActivity.this, "Id has been changed.");
                    }

                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Really save this Id?");
                template.setPositiveButtonText(DomainObjects.yes);
                template.setNegativeButtonText(DomainObjects.never_mind);
                template.show(SettingsActivity.this);
            }
        });
        binding.displayErrorMessagesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                store.setChecked(SettingsActivity.this, SHARED_PREFERENCES_DISPLAY_ERROR_MESSAGE, binding.displayErrorMessagesCheckBox.isChecked());
            }
        });
        binding.appendUuidCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                store.setChecked(SettingsActivity.this, SHARED_PREFERENCES_APPEND_UUID_WHEN_SENDING, binding.appendUuidCheckbox.isChecked());
            }
        });
        binding.appendTimezoneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                store.setChecked(SettingsActivity.this, SHARED_PREFERENCES_APPEND_TIMEZONE_WHEN_SENDING, binding.appendTimezoneCheckbox.isChecked());
            }
        });
        binding.syncCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                store.setChecked(SettingsActivity.this, SHARED_PREFERENCES_PROMPT_TO_SYNC_NOTE, binding.syncCheckBox.isChecked());
            }
        });

    }

    private void setupUi(){
        binding.baseUrlTextView.setText(store.getBaseUrl(SettingsActivity.this));
        binding.usernameTextView.setText(store.getUsername(SettingsActivity.this));
        store.setDropDown(SettingsActivity.this, binding.targetModeDropDown, store.getTargetModes(), store.getTargetMode(SettingsActivity.this));
        machines.setDropDown(SettingsActivity.this,binding.machinesDropDown, machines.list(SettingsActivity.this), machines.getDefaultDevice(SettingsActivity.this));
        binding.IDTextView.setText(store.getString(SettingsActivity.this, SHARED_PREFERENCES_ID_KEY, EMPTY_STRING));
        binding.senderTextView.setText(store.getSender(SettingsActivity.this));
        binding.displayErrorMessagesCheckBox.setChecked(store.shouldDisplayErrorMessage(SettingsActivity.this));
        binding.appendUuidCheckbox.setChecked(store.shouldAppendUuidToOutput(SettingsActivity.this));
        binding.appendTimezoneCheckbox.setChecked(store.shouldAppendTimezoneToOutput(SettingsActivity.this));
        binding.syncCheckBox.setChecked(store.shouldPromptToSyncNote(SettingsActivity.this));
        binding.vibrateFeedbackOnlyCheckBox.setChecked(store.hapticFeedbackOnly(SettingsActivity.this));
        binding.vibrateOnAcknowledgementCheckBox.setChecked(store.hapticFeedbackOnAcknowledgement(SettingsActivity.this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.moreSettingsMenuItem) {
            openAdvancedSettings(binding.getRoot());
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void openAdvancedSettings(View view){

        //Todo refactor
        if (!factory.user.service.isLoggedIn()){
            if (factory.user.service.userLoggedIn()){
                startActivity(new Intent(view.getContext(), AdvancedSettingsActivity.class));
            }
        }
        startActivity(new Intent(view.getContext(), AdvancedSettingsActivity.class));
    }
}