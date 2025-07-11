package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_CREATE;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_INFORM;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_UPDATE;
import static com.inovationware.toolkit.common.utility.Support.determineMeta;
import static com.inovationware.toolkit.common.utility.Support.determineTarget;
import static com.inovationware.toolkit.common.utility.Support.initialParamsAreSet;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.features.code.domain.Language;
import com.inovationware.toolkit.features.code.service.LanguageService;
import com.inovationware.toolkit.features.code.service.impl.LanguageServiceImpl;
import com.inovationware.toolkit.features.code.strategy.impl.LanguageStrategyImpl;
import com.inovationware.toolkit.features.cycles.library.CalendarLite;
import com.inovationware.toolkit.features.cycles.library.DetailViewSource;
import com.inovationware.toolkit.features.cycles.library.LanguageViewSource;
import com.inovationware.toolkit.features.cycles.library.PrintoutAuthority;
import com.inovationware.toolkit.features.cycles.library.ProfileViewSource;
import com.inovationware.toolkit.features.cycles.model.Entity;
import com.inovationware.toolkit.features.cycles.model.domain.Cycle;
import com.inovationware.toolkit.features.cycles.service.CyclesFacade;
import com.inovationware.toolkit.features.cycles.service.DailyCycleService;
import com.inovationware.toolkit.databinding.FragmentPersonalBinding;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.features.datatransfer.service.rest.RestDataTransferService;
import com.inovationware.toolkit.features.datatransfer.strategy.rest.RestDataTransferStrategy;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.utility.EncryptionManager;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.InputDialog;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.Code;
import com.inovationware.toolkit.common.utility.DeviceClient;
import com.inovationware.toolkit.common.utility.StorageClient;
import com.inovationware.toolkit.common.utility.Ui;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.features.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.features.profile.model.Profile;
import com.inovationware.toolkit.features.profile.strategy.ProfileManager;
import com.inovationware.toolkit.features.scheduler.model.Schedule;
import com.inovationware.toolkit.features.scheduler.service.impl.GCalendarIntentService;
import com.inovationware.toolkit.features.scheduler.strategy.impl.GCalendarIntentStrategy;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonalFragment extends Fragment implements DetailViewSource, ProfileViewSource, LanguageViewSource {

    private DetailViewSource detailViewSource;
    private ProfileViewSource profileViewSource;
    private LanguageViewSource languageViewSource;
    private View view;
    private FragmentPersonalBinding binding;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPersonalBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        ui = Ui.getInstance();
        facade = CyclesFacade.getInstance();
        calendarLite = CalendarLite.getInstance();
        languageServiceLite = LanguageServiceImpl.getInstance(LanguageStrategyImpl.getInstance());
        store = SharedPreferencesManager.getInstance();
        device = DeviceClient.getInstance();
        profiler = ProfileManager.getInstance(store, view.getContext());
        context = view.getContext();
        factory = Factory.getInstance();
        machines = GroupManager.getInstance();
        security = EncryptionManager.getInstance();

        detailViewSource = this;
        profileViewSource = this;
        languageViewSource = this;

        setupUi();

        return view;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupUi() {

        ui.bindProperty(view.getContext(), binding.profileDropDown, profiler.getNameListing());
        ui.bindProperty(view.getContext(), binding.dayDropDown, calendarLite.DAY_LISTING);
        ui.bindProperty(view.getContext(), binding.monthDropDown, calendarLite.MONTH_LISTING);
        ui.bindProperty(view.getContext(), binding.section, Cycle.listing());

        binding.preview.setFocusable(false);
        binding.preview.setClickable(false);

        binding.computeCyclesButton.setOnClickListener(handleComputeCycles);
        binding.createCyclesButton.setOnClickListener(createCyclesButtonListener);
        binding.shareCyclesResultButton.setOnClickListener(shareCyclesResultButtonListener);
        binding.sendCyclesResultButton.setOnClickListener(handleSendCycles);
        binding.copyCyclesResultButton.setOnClickListener(copyCyclesResultButtonListener);
        binding.memoCyclesResultButton.setOnClickListener(memoCyclesResultButtonListener);
        binding.scheduleCyclesResultButton.setOnClickListener(scheduleCyclesResultButtonListener);
        binding.convertCyclesButton.setOnClickListener(handleConvertCycleToLanguage);
        binding.addProfileButton.setOnClickListener(handleAddProfile);
        binding.selectProfileButton.setOnClickListener(handleSelectProfile);
        binding.updateCyclesButton.setOnClickListener(updateCyclesButtonListener);
        binding.inform.setOnClickListener(informButtonListener);
        binding.clear.setOnClickListener(clear);

        ui.bindProperty(view.getContext(), binding.cyclesLanguageDropDown, languageServiceLite.getLanguages(), R.layout.default_drop_down, DomainObjects.EMPTY_STRING);

        binding.visit.setOnClickListener(visit);
        binding.save.setOnClickListener(save);


    }

    private final View.OnClickListener save = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(profileViewSource))
                return;

            String filename = createTitle() + ".txt";
            StorageClient.getInstance(context).writeText(facade.scroll(detailViewSource, profileViewSource, languageViewSource), filename, filename + " created in Internal Storage", "Could not create file.");
        }
    };


    private void inform() {
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
            new MenuBottomSheetFragment(getButtons()).show(requireActivity().getSupportFragmentManager(), MenuBottomSheetFragment.TAG);
        }
    };

    private final View.OnClickListener visit = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (getHeadlineTextView().getText().toString().isEmpty()) return;

            CyclesFacade.DetailResource tokens = facade.toLanguage(profileViewSource, Language.English);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createCycles() {
        createInputDialog(Code.formatOutput(view.getContext(), facade.scroll(detailViewSource, profileViewSource, languageViewSource), store, device), POST_PURPOSE_CREATE).show();
    }

    private final View.OnClickListener createCyclesButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            createCycles();
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateCycles() {
        createInputDialog(Code.formatOutput(view.getContext(), facade.scroll(detailViewSource, profileViewSource, languageViewSource), store, device), POST_PURPOSE_UPDATE).show();
    }

    private final View.OnClickListener updateCyclesButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            updateCycles();
        }
    };

    private final View.OnClickListener handleComputeCycles = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(profileViewSource)) {
                clearOutput();
                return;
            }

            Entity entity = facade.createEntity(profileViewSource);

            CyclesFacade.DetailResource tokens = facade.toLanguage(profileViewSource, Language.English);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void share() {
        if (!facade.userInputIsValid(profileViewSource)) return;

        DeviceClient.getInstance().shareText(
                view.getContext(),
                Code.formatOutput(view.getContext(), facade.scroll(detailViewSource, profileViewSource, languageViewSource), store, device)
        );
    }

    private final View.OnClickListener shareCyclesResultButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            share();
        }
    };

    private final View.OnClickListener handleSendCycles = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (!facade.userInputIsValid(profileViewSource) || !thereIsInternet(binding.getRoot().getContext()))
                return;

            sendCycles(Code.formatOutput(view.getContext(), facade.scroll(detailViewSource, profileViewSource, languageViewSource), store, device), POST_PURPOSE_REGULAR);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void copyCyclesResult() {
        if (!facade.userInputIsValid(profileViewSource)) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DeviceClient.getInstance().toClipboard(
                    Code.formatOutput(
                            view.getContext(),
                            facade.scroll(detailViewSource, profileViewSource, languageViewSource),
                            store,
                            device
                    ),
                    view.getContext()
            );
        }
        DeviceClient.getInstance().tell("Information copied to clipboard.", view.getContext());
    }

    private final View.OnClickListener copyCyclesResultButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            copyCyclesResult();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createMemo() {
        if (!facade.userInputIsValid(profileViewSource)) return;

        try {
            KeepIntentService.getInstance(binding.getRoot().getContext(), SharedPreferencesManager.getInstance(), device).saveNoteToCloud(Memo.create(
                    createTitle(),
                    Code.formatOutput(view.getContext(), facade.scroll(detailViewSource, profileViewSource, languageViewSource), store, device),
                    binding.getRoot().getContext(), SharedPreferencesManager.getInstance()));
        } catch (IOException ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private final View.OnClickListener memoCyclesResultButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createMemo();
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createSchedule() {
        if (!facade.userInputIsValid(profileViewSource) || !facade.personalCyclesArePresent(detailViewSource) || binding.datesDropDown.getText().toString().isEmpty())
            return;

        CyclesFacade.DateResource token = CyclesFacade.DateResource.create(binding.datesDropDown.getText().toString(), profiler);
        GCalendarIntentService.getInstance(GCalendarIntentStrategy.getInstance(binding.getRoot().getContext()))
                .createSchedule(
                        Schedule.create(
                                facade.createTitleForSchedule(profileViewSource),
                                facade.createDescriptionForSchedule(profileViewSource),
                                LocalDate.now().getYear(),
                                token.getMonth(),
                                token.getDay(),
                                Schedule.Recurrence.YEARLY,
                                0,
                                0
                        )
                );
    }

    private final View.OnClickListener scheduleCyclesResultButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            createSchedule();
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

            CyclesFacade.DetailResource tokens = facade.toLanguage(profileViewSource, Language.valueOf(binding.cyclesLanguageDropDown.getText().toString()));


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
        return new InputDialog(view.getContext(), "", "Full path of file\non the target device", "e.g.  c:\\users\\file.txt") {
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
                view.getContext(),
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                        SharedPreferencesManager.getInstance().getUsername(view.getContext()),
                        SharedPreferencesManager.getInstance().getID(view.getContext()),
                        Transfer.Intent.writeText,
                        SharedPreferencesManager.getInstance().getSender(view.getContext()),
                        determineTarget(view.getContext(), SharedPreferencesManager.getInstance(), GroupManager.getInstance()),
                        purpose,
                        determineMeta(view.getContext(), SharedPreferencesManager.getInstance()),
                        EncryptionManager.getInstance().encrypt(view.getContext(), SharedPreferencesManager.getInstance(), text),
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void printout() {
        if (!facade.userInputIsValid(profileViewSource)) return;
        new PrintoutAuthority(context,
                store,
                machines,
                factory,
                facade,
                DailyCycleService.getInstance(),
                detailViewSource,
                profileViewSource,
                languageViewSource
        ).beginWorkflow();
    }


    private void resetDay() {
        binding.dayDropDown.setText("");
        ui.bindProperty(view.getContext(), binding.dayDropDown, calendarLite.DAY_LISTING);
    }

    private void resetMonth() {
        binding.monthDropDown.setText("");
        ui.bindProperty(view.getContext(), binding.monthDropDown, calendarLite.MONTH_LISTING);
    }

    private void resetPeriods() {
        binding.datesDropDown.setText("");
        ui.bindProperty(view.getContext(), binding.datesDropDown, new String[0]);
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

    private String createTitle() {
        return "Yearly Cycles Printout For " + (binding.profileDropDown.getText().toString().isEmpty() ? "Entity" : binding.profileDropDown.getText().toString());
    }

    private void clearUi() {
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

    private void clearProfile() {
        //ui.bindProperty(view.getContext(), binding.profileDropDown, new String[0], "");
        binding.profileDropDown.setText("");
    }

    private void clearOutput() {
        clearHeadline();
        clearPersonal();
        clearHealth();
        clearBusiness();
        clearSoul();
        resetPeriods();
        clearPreview();
    }

    private List<Ui.ButtonObject> getButtons() {
        Ui.ButtonObject.DimensionInfo dimensions = new Ui.ButtonObject.DimensionInfo(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        Ui.ButtonObject.MarginInfo margins = new Ui.ButtonObject.MarginInfo();


        Ui.ButtonObject.ViewInfo informViewing = new Ui.ButtonObject.ViewInfo("inform home", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_inform, 1);
        Ui.ButtonObject informButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inform();
            }
        }, margins, dimensions, informViewing);


        Ui.ButtonObject.ViewInfo shareViewing = new Ui.ButtonObject.ViewInfo("share this", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_share, 1);
        Ui.ButtonObject shareButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                share();
            }
        }, margins, dimensions, shareViewing);


        Ui.ButtonObject.ViewInfo copyViewing = new Ui.ButtonObject.ViewInfo("copy to clipboard", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_content_copy_24, 1);
        Ui.ButtonObject copyButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                copyCyclesResult();
            }
        }, margins, dimensions, copyViewing);


        Ui.ButtonObject.ViewInfo createViewing = new Ui.ButtonObject.ViewInfo("create file", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_insert_drive_file_24, 1);
        Ui.ButtonObject createButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                createCycles();
            }
        }, margins, dimensions, createViewing);


        Ui.ButtonObject.ViewInfo updateViewing = new Ui.ButtonObject.ViewInfo("update file", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_edit, 1);
        Ui.ButtonObject updateButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                updateCycles();
            }
        }, margins, dimensions, updateViewing);


        Ui.ButtonObject.ViewInfo memoViewing = new Ui.ButtonObject.ViewInfo("create note", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_sticky_note_2_24, 1);
        Ui.ButtonObject memoButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                createMemo();
            }
        }, margins, dimensions, memoViewing);


        Ui.ButtonObject.ViewInfo scheduleViewing = new Ui.ButtonObject.ViewInfo("create schedule", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_event_repeat_24, 1);
        Ui.ButtonObject scheduleButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                createSchedule();
            }
        }, margins, dimensions, scheduleViewing);


        List<Ui.ButtonObject> buttons = new ArrayList<>();
        buttons.add(informButton);
        buttons.add(shareButton);
        buttons.add(copyButton);
        buttons.add(createButton);
        buttons.add(updateButton);
        buttons.add(memoButton);

        return buttons;

    }

}