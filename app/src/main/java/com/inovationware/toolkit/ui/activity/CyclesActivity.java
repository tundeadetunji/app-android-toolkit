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
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.view.MenuCompat;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.code.domain.Language;
import com.inovationware.toolkit.code.service.LanguageService;
import com.inovationware.toolkit.code.service.impl.LanguageServiceImpl;
import com.inovationware.toolkit.code.strategy.impl.LanguageStrategyImpl;
import com.inovationware.toolkit.cycles.library.DetailViewSource;
import com.inovationware.toolkit.cycles.library.LanguageViewSource;
import com.inovationware.toolkit.cycles.library.PrintoutAuthority;
import com.inovationware.toolkit.cycles.library.ProfileViewSource;
import com.inovationware.toolkit.cycles.model.Entity;
import com.inovationware.toolkit.cycles.model.domain.Cycle;
import com.inovationware.toolkit.cycles.service.CyclesFacade;
import com.inovationware.toolkit.cycles.service.DailyCycleService;
import com.inovationware.toolkit.databinding.ActivityCyclesBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.datatransfer.service.rest.RestDataTransferService;
import com.inovationware.toolkit.datatransfer.strategy.rest.RestDataTransferStrategy;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.InputDialog;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.cycles.library.CalendarLite;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.StorageClient;
import com.inovationware.toolkit.global.library.utility.Ui;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.profile.model.Profile;
import com.inovationware.toolkit.profile.strategy.ProfileManager;
import com.inovationware.toolkit.scheduler.model.Schedule;
import com.inovationware.toolkit.scheduler.service.impl.GCalendarIntentService;
import com.inovationware.toolkit.scheduler.strategy.impl.GCalendarIntentStrategy;
import com.inovationware.toolkit.ui.contract.BaseActivity;

import java.io.IOException;
import java.time.LocalDate;

public class CyclesActivity extends BaseActivity implements DetailViewSource, ProfileViewSource, LanguageViewSource {

    private ActivityCyclesBinding binding;
    private Ui ui;
    private CyclesFacade facade;
    private CalendarLite calendarLite;
    private LanguageService languageServiceLite;
    private ProfileManager profiler;
    private SharedPreferencesManager store;
    private DeviceClient device;
    private Context context;
    private Factory factory;
    private GroupManager machines;
    private EncryptionManager security;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCyclesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ui = Ui.getInstance();
        facade = CyclesFacade.getInstance();
        calendarLite = CalendarLite.getInstance();
        languageServiceLite = LanguageServiceImpl.getInstance(LanguageStrategyImpl.getInstance());
        store = SharedPreferencesManager.getInstance();
        device = DeviceClient.getInstance();
        profiler = ProfileManager.getInstance(store, CyclesActivity.this);
        context = CyclesActivity.this;
        factory = Factory.getInstance();
        machines = GroupManager.getInstance();
        security = EncryptionManager.getInstance();

        setupUi();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupUi() {

        ui.bindProperty(CyclesActivity.this, binding.profileDropDown, profiler.getNameListing());
        ui.bindProperty(CyclesActivity.this, binding.dayDropDown, calendarLite.DAY_LISTING);
        ui.bindProperty(CyclesActivity.this, binding.monthDropDown, calendarLite.MONTH_LISTING);
        ui.bindProperty(CyclesActivity.this, binding.section, Cycle.listing());

        binding.computeCyclesButton.setOnClickListener(handleComputeCycles);
        binding.createCyclesButton.setOnClickListener(handleCreateCyclesFile);
        binding.shareCyclesResultButton.setOnClickListener(handleShareCycles);
        binding.sendCyclesResultButton.setOnClickListener(handleSendCycles);
        binding.copyCyclesResultButton.setOnClickListener(handleCopyCycles);
        binding.memoCyclesResultButton.setOnClickListener(handleCreateMemo);
        binding.scheduleCyclesResultButton.setOnClickListener(handleScheduleCycles);
        binding.convertCyclesButton.setOnClickListener(handleConvertCycleToLanguage);
        binding.addProfileButton.setOnClickListener(handleAddProfile);
        binding.selectProfileButton.setOnClickListener(handleSelectProfile);
        binding.updateCyclesButton.setOnClickListener(handleUpdateCyclesFile);
        binding.inform.setOnClickListener(inform);
        binding.clear.setOnClickListener(clear);

        ui.bindProperty(CyclesActivity.this, binding.cyclesLanguageDropDown, languageServiceLite.getLanguages(), R.layout.default_drop_down, DomainObjects.EMPTY_STRING);

        binding.visit.setOnClickListener(visit);
        binding.save.setOnClickListener(save);
    }

    private final View.OnClickListener save = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(CyclesActivity.this))
                return;

            String filename = createTitle() + ".txt";
            StorageClient.getInstance(context).writeText(facade.scroll(CyclesActivity.this, CyclesActivity.this, CyclesActivity.this), filename, filename + " created in Internal Storage", "Could not create file.");
        }
    };

    private final View.OnClickListener inform = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (binding.preview.getText().toString().isEmpty()) return;

            if (!initialParamsAreSet(context, store, machines)) return;

            if (!thereIsInternet(context)) return;

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
                            POST_PURPOSE_INFORM,
                            determineMeta(context, store),
                            security.encrypt(context, store, binding.preview.getText().toString()),
                            DomainObjects.EMPTY_STRING
                    ),
                    DEFAULT_ERROR_MESSAGE_SUFFIX,
                    DEFAULT_FAILURE_MESSAGE_SUFFIX);

        }
    };

    private final View.OnClickListener visit = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (getHeadlineTextView().getText().toString().isEmpty()) return;

            CyclesFacade.DetailResource tokens = facade.toLanguage(CyclesActivity.this, Language.English);
            switch (Cycle.valueOf(binding.section.getText().toString())) {
                case Personal:
                    binding.preview.setText(tokens.getPersonal());
                    break;
                case Health:
                    binding.preview.setText(tokens.getHealth());
                    break;
                case Business:
                    binding.preview.setText(tokens.getBusiness());
                    break;
                default:
                    binding.preview.setText(tokens.getSoul());
                    break;
            }


        }
    };

    private final View.OnClickListener clear = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearUi();
        }
    };

    private final View.OnClickListener handleCreateCyclesFile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            createInputDialog(Code.formatOutput(CyclesActivity.this, facade.scroll(CyclesActivity.this, CyclesActivity.this, CyclesActivity.this), store, device), POST_PURPOSE_CREATE).show();
        }
    };

    private final View.OnClickListener handleUpdateCyclesFile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            createInputDialog(Code.formatOutput(CyclesActivity.this, facade.scroll(CyclesActivity.this, CyclesActivity.this, CyclesActivity.this), store, device), POST_PURPOSE_UPDATE).show();
        }
    };

    private final View.OnClickListener handleComputeCycles = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(CyclesActivity.this)) {
                clearOutput();
                return;
            }

            Entity entity = facade.createEntity(CyclesActivity.this);

            CyclesFacade.DetailResource tokens = facade.toLanguage(CyclesActivity.this, Language.English);
            getHeadlineTextView().setText(tokens.getHeadline());
            getSoulTextView().setText(tokens.getSoul());
            getPersonalTextView().setText(tokens.getPersonal());
            getBusinessTextView().setText(tokens.getBusiness());
            getHealthTextView().setText(tokens.getHealth());

            ui.bindProperty(
                    binding.getRoot().getContext(),
                    binding.datesDropDown,
                    entity.getPERIOD_LISTING().toArray(new String[0])
            );
        }
    };

    private final View.OnClickListener handleShareCycles = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(CyclesActivity.this)) return;

            DeviceClient.getInstance().shareText(
                    CyclesActivity.this,
                    Code.formatOutput(CyclesActivity.this, facade.scroll(CyclesActivity.this, CyclesActivity.this, CyclesActivity.this), store, device)
            );
        }
    };

    private final View.OnClickListener handleSendCycles = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(CyclesActivity.this) || !thereIsInternet(binding.getRoot().getContext()))
                return;

            sendCycles(Code.formatOutput(CyclesActivity.this, facade.scroll(CyclesActivity.this, CyclesActivity.this, CyclesActivity.this), store, device), POST_PURPOSE_REGULAR);
        }
    };

    private final View.OnClickListener handleCopyCycles = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(CyclesActivity.this)) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DeviceClient.getInstance().toClipboard(
                        Code.formatOutput(
                                CyclesActivity.this,
                                facade.scroll(CyclesActivity.this, CyclesActivity.this, CyclesActivity.this),
                                store,
                                device
                        ),
                        CyclesActivity.this
                );
            }
            DeviceClient.getInstance().tell("Information copied to clipboard.", CyclesActivity.this);
        }
    };

    /*private final View.OnClickListener handleSaveCyclesToDisk = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(CyclesActivity.this)) return;

            String filename = facade.createFilename(CyclesActivity.this);

            StorageClient.getInstance(binding.getRoot().getContext()).writeText(
                    Code.formatOutput(CyclesActivity.this, facade.scroll(CyclesActivity.this, CyclesActivity.this, CyclesActivity.this), store, device),
                    filename,
                    filename + " created in Internal Storage.",
                    Strings.WRITE_FILE_FAILED
            );
        }
    };*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    private final View.OnClickListener handleCreateMemo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(CyclesActivity.this)) return;

            try {
                KeepIntentService.getInstance(binding.getRoot().getContext(), SharedPreferencesManager.getInstance(), device).saveNoteToCloud(Memo.create(
                        createTitle(),
                        Code.formatOutput(CyclesActivity.this, facade.scroll(CyclesActivity.this, CyclesActivity.this, CyclesActivity.this), store, device),
                        binding.getRoot().getContext(), SharedPreferencesManager.getInstance()));
            } catch (IOException ignored) {
            }

        }
    };

    private final View.OnClickListener handleScheduleCycles = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(CyclesActivity.this) || !facade.personalCyclesArePresent(CyclesActivity.this) || binding.datesDropDown.getText().toString().isEmpty())
                return;

            CyclesFacade.DateResource token = CyclesFacade.DateResource.create(binding.datesDropDown.getText().toString(), profiler);
            GCalendarIntentService.getInstance(GCalendarIntentStrategy.getInstance(binding.getRoot().getContext()))
                    .createSchedule(
                            Schedule.create(
                                    facade.createTitleForSchedule(CyclesActivity.this),
                                    facade.createDescriptionForSchedule(CyclesActivity.this),
                                    LocalDate.now().getYear(),
                                    token.getMonth(),
                                    token.getDay(),
                                    Schedule.Recurrence.YEARLY,
                                    0,
                                    0
                            )
                    );
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private final View.OnClickListener handleSelectProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (binding.profileDropDown.getText().toString().isEmpty()) return;

            Profile profile = profiler.getProfile(binding.profileDropDown.getText().toString());
            binding.dayDropDown.setText(String.valueOf(profile.getDay()));
            binding.monthDropDown.setText(profiler.monthStringFromInt(profile.getMonth()));
        }
    };

    private final View.OnClickListener handleAddProfile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.profileDropDown.getText().toString().isEmpty()) return;
            if (binding.dayDropDown.getText().toString().isEmpty()) return;
            if (binding.monthDropDown.getText().toString().isEmpty()) return;

            profiler.add(binding.profileDropDown.getText().toString().trim(), Integer.parseInt(binding.dayDropDown.getText().toString()), binding.monthDropDown.getText().toString());
            ui.bindProperty(binding.getRoot().getContext(), binding.profileDropDown, profiler.getNameListing());
        }
    };

    private final View.OnClickListener handleConvertCycleToLanguage = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.cyclesLanguageDropDown.getText().toString().trim().isEmpty()) return;
            if (getHeadlineTextView().getText().toString().trim().isEmpty()) return;

            CyclesFacade.DetailResource tokens = facade.toLanguage(CyclesActivity.this, Language.valueOf(binding.cyclesLanguageDropDown.getText().toString()));


            switch (Cycle.valueOf(binding.section.getText().toString())) {
                case Personal:
                    binding.preview.setText(tokens.getPersonal());
                    break;
                case Health:
                    binding.preview.setText(tokens.getHealth());
                    break;
                case Business:
                    binding.preview.setText(tokens.getBusiness());
                    break;
                default:
                    binding.preview.setText(tokens.getSoul());
                    break;

            }

            getHeadlineTextView().setText(tokens.getHeadline());
            getSoulTextView().setText(tokens.getSoul());
            getPersonalTextView().setText(tokens.getPersonal());
            getBusinessTextView().setText(tokens.getBusiness());
            getHealthTextView().setText(tokens.getHealth());
        }
    };

    private InputDialog createInputDialog(String text, String purpose) {
        return new InputDialog(CyclesActivity.this, "", "Full path of file\non the target device", "e.g.  c:\\users\\file.txt") {
            @Override
            public void positiveButtonAction() {
                if (purpose.equalsIgnoreCase(POST_PURPOSE_CREATE) || purpose.equalsIgnoreCase(POST_PURPOSE_UPDATE)) {
                    if (!this.getText().isEmpty())
                        sendCycles(this.getText() + "\n" + text, purpose);
                } else {
                    sendCycles(text, purpose);
                }
            }

            @Override
            public void negativeButtonAction() {

            }
        };
    }

    private void sendCycles(String text, String purpose) {
        RestDataTransferService.getInstance(RestDataTransferStrategy.getInstance()).sendText(
                CyclesActivity.this,
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                        SharedPreferencesManager.getInstance().getUsername(CyclesActivity.this),
                        SharedPreferencesManager.getInstance().getID(CyclesActivity.this),
                        Transfer.Intent.writeText,
                        SharedPreferencesManager.getInstance().getSender(CyclesActivity.this),
                        determineTarget(CyclesActivity.this, SharedPreferencesManager.getInstance(), GroupManager.getInstance()),
                        purpose,
                        determineMeta(CyclesActivity.this, SharedPreferencesManager.getInstance()),
                        EncryptionManager.getInstance().encrypt(CyclesActivity.this, SharedPreferencesManager.getInstance(), text),
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }

    @Override
    public TextView getHeadlineTextView() {
        return binding.headlineTextView;
    }

    @Override
    public TextView getSoulTextView() {
        return binding.soulTextView;
    }

    @Override
    public TextView getPersonalTextView() {
        return binding.personalTextView;
    }

    @Override
    public TextView getBusinessTextView() {
        return binding.businessTextView;
    }

    @Override
    public TextView getHealthTextView() {
        return binding.healthTextView;
    }

    @Override
    public TextView getDayDropDown() {
        return binding.dayDropDown;
    }

    @Override
    public TextView getMonthDropDown() {
        return binding.monthDropDown;
    }

    @Override
    public TextView getProfileDropDown() {
        return binding.profileDropDown;
    }

    @Override
    public TextView getLanguageDropDown() {
        return binding.cyclesLanguageDropDown;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cycles_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.moreActionsMenuItem) {
            startActivity(new Intent(CyclesActivity.this, ProfilerActivity.class));
            return true;
        } else if (item.getItemId() == R.id.weekView) {
            startActivity(new Intent(CyclesActivity.this, CyclesByDayActivity.class));
            return true;
        } else if (item.getItemId() == R.id.dayView) {
            startActivity(new Intent(CyclesActivity.this, CyclesDayViewActivity.class));
            return true;
        } else if (item.getItemId() == R.id.print) {
            printout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void printout() {
        if (!facade.userInputIsValid(CyclesActivity.this)) return;
        new PrintoutAuthority(context,
                store,
                machines,
                factory,
                facade,
                DailyCycleService.getInstance(),
                CyclesActivity.this,
                CyclesActivity.this,
                CyclesActivity.this
        ).beginWorkflow();
    }


    private void resetDay() {
        binding.dayDropDown.setText("");
        ui.bindProperty(CyclesActivity.this, binding.dayDropDown, calendarLite.DAY_LISTING);
    }

    private void resetMonth() {
        binding.monthDropDown.setText("");
        ui.bindProperty(CyclesActivity.this, binding.monthDropDown, calendarLite.MONTH_LISTING);
    }

    private void resetPeriods() {
        binding.datesDropDown.setText("");
        ui.bindProperty(CyclesActivity.this, binding.datesDropDown, new String[0]);
    }

    private void clearHeadline() {
        binding.headlineTextView.setText("");
    }

    private void clearPersonal() {
        binding.personalTextView.setText("");
    }

    private void clearHealth() {
        binding.healthTextView.setText("");
    }

    private void clearBusiness() {
        binding.businessTextView.setText("");
    }

    private void clearSoul() {
        binding.soulTextView.setText("");
    }

    private void clearPreview() {
        binding.preview.setText("");
    }

    private String createTitle(){
        return "Yearly Cycles Printout For " + (binding.profileDropDown.getText().toString().isEmpty() ? "Entity" : binding.profileDropDown.getText().toString());
    }

    private void clearUi(){
        clearProfile();
        resetDay();
        resetMonth();
        clearHeadline();
        clearPersonal();
        clearHealth();
        clearBusiness();
        clearSoul();
        resetPeriods();
        clearPreview();
    }

    private void clearProfile(){
        //ui.bindProperty(CyclesActivity.this, binding.profileDropDown, new String[0], "");
        binding.profileDropDown.setText("");
    }

    private void clearOutput(){
        clearHeadline();
        clearPersonal();
        clearHealth();
        clearBusiness();
        clearSoul();
        resetPeriods();
        clearPreview();
    }


}