package com.inovationware.toolkit.ui.activity;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_INFORM;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_REGULAR;
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
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.core.view.MenuCompat;

import com.google.android.material.button.MaterialButton;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.cycles.library.CalendarLite;
import com.inovationware.toolkit.cycles.model.DayToken;
import com.inovationware.toolkit.cycles.model.domain.DayPeriod;
import com.inovationware.toolkit.cycles.service.DailyCycleService;
import com.inovationware.toolkit.databinding.ActivityCyclesByDayBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.datatransfer.service.rest.RestDataTransferService;
import com.inovationware.toolkit.datatransfer.strategy.rest.RestDataTransferStrategy;
import com.inovationware.toolkit.global.domain.Section;
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
import com.inovationware.toolkit.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.scheduler.model.Schedule;
import com.inovationware.toolkit.scheduler.service.impl.GCalendarIntentService;
import com.inovationware.toolkit.scheduler.strategy.impl.GCalendarIntentStrategy;
import com.inovationware.toolkit.ui.contract.BaseActivity;
import com.inovationware.toolkit.ui.fragment.MenuBottomSheetFragment;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CyclesByDayActivity extends BaseActivity {
    private ActivityCyclesByDayBinding binding;
    private Ui ui;
    private Context context;
    private Factory factory;
    private GroupManager machines;
    private SharedPreferencesManager store;
    private DailyCycleService service;
    private EncryptionManager security;
    private DeviceClient device;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCyclesByDayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeVariables();
        setupUi();
    }

    private void initializeVariables() {
        context = CyclesByDayActivity.this;
        ui = Ui.getInstance();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        factory = Factory.getInstance();
        service = DailyCycleService.getInstance();
        security = EncryptionManager.getInstance();
        device = DeviceClient.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void setupUi() {
        ui.bindProperty(context, binding.section, Section.listing());
        ui.bindProperty(context, binding.day, CalendarLite.getInstance().WEEK_DAY_LISTING);

        binding.preview.setFocusable(false);
        binding.preview.setClickable(false);

        binding.loadDayOfWeek.setOnClickListener(loadDayOfWeek);
        binding.load.setOnClickListener(load);
        binding.setTimer.setOnClickListener(setTimer);
        binding.visit.setOnClickListener(visit);
        binding.share.setOnClickListener(shareButtonListener);
        binding.copy.setOnClickListener(copy);
        binding.send.setOnClickListener(send);
        binding.memo.setOnClickListener(sendMemoButtonListener);
        binding.scheduleButton.setOnClickListener(scheduleButtonListener);
        binding.inform.setOnClickListener(informButtonListener);
        binding.save.setOnClickListener(saveButtonListener);
    }

    private final View.OnClickListener loadDayOfWeek = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (binding.day.getText().toString().isEmpty()) return;
            ui.bindProperty(context, binding.match, DayPeriod.listingSortedByDay(DayOfWeek.valueOf(binding.day.getText().toString())));
        }
    };

    private final View.OnClickListener load = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!binding.match.getText().toString().isEmpty()) {
                clearSection();
                clearDetail();
                clearHeadline();
                DayToken token = service.findByPeriod(DayPeriod.valueOf(binding.match.getText().toString()));
                ui.bindProperty(context, binding.times, token.getPeriodTimes().toArray(new String[0]), "");
                binding.headline.setText(token.getHeadline());
            }
        }
    };

    private final View.OnClickListener setTimer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (binding.match.getText().toString().isEmpty()) return;

            LocalTime when = service.getBeginningOfPeriod(DayPeriod.valueOf(binding.match.getText().toString()));
            DeviceClient.getInstance().startAlarm(context, when.getHour(), when.getMinute(), when.getHour() >= 12 ? true : false);
        }
    };

    private final View.OnClickListener visit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (binding.headline.getText().toString().isEmpty()) return;

            try {
                String stripped = binding.headline.getText().toString().replace(DayPeriod.HEADLINE_SUFFIX, "").replace(DayPeriod.HEADLINE_PREFIX, "");

                DayPeriod period = DayPeriod.valueOf(stripped);
                switch (Section.valueOf(binding.section.getText().toString())) {
                    case Summary:
                        binding.preview.setText(service.findByPeriod(period).getSummary());
                        break;
                    default:
                        binding.preview.setText(service.findByPeriod(period).getDetail());
                        break;
                }
            } catch (Exception ignored) {

            }
        }
    };


    private void share(){
        if (binding.preview.getText().toString().isEmpty()) return;
        factory.device.shareText(context, createSummary());
    }

    private final View.OnClickListener shareButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MenuBottomSheetFragment(getButtons()).show(getSupportFragmentManager(), MenuBottomSheetFragment.TAG);
        }
    };

    private final View.OnClickListener copy = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (binding.preview.getText().toString().isEmpty()) return;
            factory.device.toClipboard(createSummary(), context);
            factory.device.tell(DomainObjects.copiedToClipboardMessage("Information"), context);
        }
    };

    private final View.OnClickListener send = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (binding.headline.getText().toString().isEmpty() || !thereIsInternet(binding.getRoot().getContext()))
                return;

            RestDataTransferService.getInstance(RestDataTransferStrategy.getInstance()).sendText(
                    context,
                    store,
                    machines,
                    SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                            SharedPreferencesManager.getInstance().getUsername(context),
                            SharedPreferencesManager.getInstance().getID(context),
                            Transfer.Intent.writeText,
                            SharedPreferencesManager.getInstance().getSender(context),
                            determineTarget(context, SharedPreferencesManager.getInstance(), GroupManager.getInstance()),
                            POST_PURPOSE_REGULAR,
                            determineMeta(context, SharedPreferencesManager.getInstance()),
                            EncryptionManager.getInstance().encrypt(context, SharedPreferencesManager.getInstance(), createPrintout()),
                            DomainObjects.EMPTY_STRING
                    ),
                    DEFAULT_ERROR_MESSAGE_SUFFIX,
                    DEFAULT_FAILURE_MESSAGE_SUFFIX);


        }
    };

    private void createMemo(){
        if (binding.headline.getText().toString().isEmpty()) return;
        try {
            KeepIntentService.getInstance(context, store, device).saveNoteToCloud(Memo.create(
                    service.createTitle(),
                    Code.formatOutput(context, createPrintout(), store, device),
                    context, store));
        } catch (IOException ignored) {
        }
    }
    private final View.OnClickListener sendMemoButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createMemo();
        }
    };

    private void scheduleCycles(){
        if (binding.headline.getText().toString().isEmpty()) return;

        InputDialog dialog = new InputDialog(context, "Title...", "What's the title of this schedule?", "") {
            @Override
            public void positiveButtonAction() throws IOException {
                if (this.getText().isEmpty()) return;

                LocalTime startOfCurrentPeriod = service.getBeginningOfPeriod(service.getCurrentPeriod());

                GCalendarIntentService.getInstance(GCalendarIntentStrategy.getInstance(binding.getRoot().getContext()))
                        .createSchedule(
                                Schedule.create(
                                        this.getText().toString(),
                                        "Daily Cycle Event",
                                        LocalDate.now().getYear(),
                                        LocalDate.now().getMonth().ordinal(),
                                        LocalDate.now().getDayOfMonth(),
                                        Schedule.Recurrence.WEEKLY,
                                        startOfCurrentPeriod.getHour(),
                                        startOfCurrentPeriod.getMinute()
                                )
                        );
            }

            @Override
            public void negativeButtonAction() {

            }
        };
        dialog.show();
    }

    private final View.OnClickListener scheduleButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            scheduleCycles();
        }
    };

    private void informCycles(){
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
    private final View.OnClickListener informButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            informCycles();
        }
    };

    private void saveCycles(){
        if (binding.headline.getText().toString().isEmpty()) return;
        String filename = binding.headline.getText().toString() + " " + Support.createTimestamp(Support.FormatForDateAndTime.TIME).replace(":", "-") + ".txt";
        StorageClient.getInstance(context).writeText(createPrintout(), filename, filename + " created in Internal Storage", "Could not create file.");
    }
    private final View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveCycles();
        }
    };


    private void clearHeadline() {
        binding.headline.setText("");
    }

    private void clearDetail() {
        binding.preview.setText("");
    }

    private void clearSection() {
        ui.bindProperty(context, binding.section, Section.listing());
    }

    private String createSummary() {
        return binding.headline.getText().toString() + "\n" + binding.preview.getText().toString();
    }

    private String createPrintout() {
        return new StringBuilder()
                .append(binding.headline.getText().toString())
                .append(DomainObjects.NEW_LINE)
                .append(service.createTitle())
                .append(Support.createTimestamp(Support.FormatForDateAndTime.TIME_DATE))
                .append(service.findByPeriod(DayPeriod.valueOf(binding.headline.getText().toString().replace(DayPeriod.HEADLINE_SUFFIX, "").replace(DayPeriod.HEADLINE_PREFIX, ""))).getDetail())
                .toString();
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
            startActivity(new Intent(context, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Ui.ButtonObject> getButtons(){
        Ui.ButtonObject.DimensionInfo dimensions = new Ui.ButtonObject.DimensionInfo(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        Ui.ButtonObject.MarginInfo margins = new Ui.ButtonObject.MarginInfo();


        Ui.ButtonObject.ViewInfo shareViewing = new Ui.ButtonObject.ViewInfo("share this", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_share, 1);
        Ui.ButtonObject shareButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        }, margins, dimensions, shareViewing);


        Ui.ButtonObject.ViewInfo memoViewing = new Ui.ButtonObject.ViewInfo("create note", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_sticky_note_2_24, 1);
        Ui.ButtonObject memoButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMemo();
            }
        }, margins, dimensions, memoViewing);


        Ui.ButtonObject.ViewInfo scheduleViewing = new Ui.ButtonObject.ViewInfo("create calendar schedule", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_event_repeat_24, 1);
        Ui.ButtonObject scheduleButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleCycles();
            }
        }, margins, dimensions, scheduleViewing);


        Ui.ButtonObject.ViewInfo informViewing = new Ui.ButtonObject.ViewInfo("inform home", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_inform, 1);
        Ui.ButtonObject informButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informCycles();
            }
        }, margins, dimensions, informViewing);


        Ui.ButtonObject.ViewInfo saveViewing = new Ui.ButtonObject.ViewInfo("save to device", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_save, 1);
        Ui.ButtonObject saveButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCycles();
            }
        }, margins, dimensions, saveViewing);






        List<Ui.ButtonObject> buttons = new ArrayList<>();
        buttons.add(shareButton);
        buttons.add(memoButton);
        buttons.add(scheduleButton);
        buttons.add(informButton);
        buttons.add(saveButton);


        return buttons;
    }


}