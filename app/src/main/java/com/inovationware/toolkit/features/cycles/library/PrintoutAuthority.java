package com.inovationware.toolkit.features.cycles.library;

import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_CREATE;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.common.utility.Support.determineMeta;
import static com.inovationware.toolkit.common.utility.Support.determineTarget;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.textfield.TextInputEditText;
import com.inovationware.toolkit.features.cycles.model.DayToken;
import com.inovationware.toolkit.features.cycles.model.Entity;
import com.inovationware.toolkit.features.cycles.service.CyclesFacade;
import com.inovationware.toolkit.features.cycles.service.DailyCycleService;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.features.datatransfer.service.rest.RestDataTransferService;
import com.inovationware.toolkit.features.datatransfer.strategy.rest.RestDataTransferStrategy;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.utility.CheckboxTextViewDialog;
import com.inovationware.toolkit.common.utility.EncryptionManager;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.Code;
import com.inovationware.toolkit.common.utility.DeviceClient;
import com.inovationware.toolkit.common.utility.StorageClient;
import com.inovationware.toolkit.common.utility.Support;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.features.memo.service.impl.KeepIntentService;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

public class PrintoutAuthority {
    CheckBox personal;
    CheckBox health;
    CheckBox soul;
    CheckBox business;
    CheckBox daily;
    CheckBox periods;
    CheckBox copy;
    CheckBox send;
    CheckBox share;
    CheckBox save;
    CheckBox create;
    CheckBox memo;
    TextInputEditText filename;
    private Factory factory;


    private CheckboxTextViewDialog dialog;
    private final String title = "Choose Output";
    private final String message = "Please note that you can't send and create file at same time.";
    private final String positiveButtonText = "Now";
    private Context context;

    private DailyCycleService dailyService;
    private DetailViewSource detailsResource;
    private ProfileViewSource profilesResource;
    private LanguageViewSource languageResource;
    private CyclesFacade yearlyService;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public PrintoutAuthority(Context context, SharedPreferencesManager store, GroupManager machines,  Factory factory, CyclesFacade yearlyService, DailyCycleService dailyService, DetailViewSource detailsResource, ProfileViewSource profilesResource, LanguageViewSource languageResource) {
        this.context = context;

        this.factory = factory;
        this.detailsResource = detailsResource;
        this.profilesResource = profilesResource;
        this.languageResource = languageResource;
        this.yearlyService = yearlyService;
        this.dailyService = dailyService;

        Typeface bold = Typeface.defaultFromStyle(Typeface.BOLD);

        CheckBox personal = new CheckBox(context);
        personal.setId(View.generateViewId());
        personal.setText("Personal");
        this.personal = personal;

        CheckBox health = new CheckBox(context);
        health.setId(View.generateViewId());
        health.setText("Health");
        this.health = health;

        CheckBox soul = new CheckBox(context);
        soul.setText("Soul");
        soul.setId(View.generateViewId());
        this.soul = soul;

        CheckBox business = new CheckBox(context);
        business.setId(View.generateViewId());
        business.setText("Business");
        this.business = business;

        CheckBox daily = new CheckBox(context);
        daily.setId(View.generateViewId());
        daily.setText("Daily");
        this.daily = daily;

        CheckBox periods = new CheckBox(context);
        periods.setId(View.generateViewId());
        periods.setText("Periods");
        this.periods = periods;

        CheckBox copy = new CheckBox(context);
        copy.setId(View.generateViewId());
        copy.setTypeface(bold);
        copy.setText("Copy");
        this.copy = copy;

        CheckBox send = new CheckBox(context);
        send.setId(View.generateViewId());
        send.setTypeface(bold);
        send.setText("Send");
        this.send = send;

        CheckBox share = new CheckBox(context);
        share.setId(View.generateViewId());
        share.setTypeface(bold);
        share.setText("Share");
        this.share = share;

        CheckBox save = new CheckBox(context);
        save.setId(View.generateViewId());
        save.setTypeface(bold);
        save.setText("Save");
        this.save = save;

        CheckBox create = new CheckBox(context);
        create.setId(View.generateViewId());
        create.setTypeface(bold);
        create.setText("Create File");
        this.create = create;

        CheckBox memo = new CheckBox(context);
        memo.setId(View.generateViewId());
        memo.setTypeface(bold);
        memo.setText("Save Memo");
        this.memo = memo;

        List<CheckBox> checkBoxes = new ArrayList<>();
        checkBoxes.add(personal);
        checkBoxes.add(health);
        checkBoxes.add(soul);
        checkBoxes.add(business);
        checkBoxes.add(daily);
        checkBoxes.add(periods);
        checkBoxes.add(copy);
        checkBoxes.add(send);
        checkBoxes.add(share);
        checkBoxes.add(save);
        checkBoxes.add(create);
        checkBoxes.add(memo);

        filename = new TextInputEditText(context);
        filename.setHint("Target, e.g. C:\\File.txt");

        dialog = new CheckboxTextViewDialog(
                context,
                title,
                message,
                positiveButtonText,
                checkBoxes,
                filename
        ) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPositiveButtonClick() {
                printout();
            }
        };
    }

    @SneakyThrows
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void printout() {
        announce("Please stay on this screen while information is transferred...");

        if (!dialog.getCheckboxes().get(copy.getId()).isChecked() &&
                !dialog.getCheckboxes().get(save.getId()).isChecked() &&
                !dialog.getCheckboxes().get(send.getId()).isChecked() &&
                !dialog.getCheckboxes().get(memo.getId()).isChecked() &&
                !dialog.getCheckboxes().get(share.getId()).isChecked() &&
                !dialog.getCheckboxes().get(create.getId()).isChecked()
        ) return;

        String output = Code.formatOutput(context, createOutput(), SharedPreferencesManager.getInstance(), DeviceClient.getInstance());

        if (dialog.getCheckboxes().get(copy.getId()).isChecked()){
            factory.device.toClipboard(output, context);
            announce("Information sent to clipboard...");
        }

        if (dialog.getCheckboxes().get(save.getId()).isChecked()){
            String filename = createFilenameForInternalStorage();
            StorageClient.getInstance(context)
                    .writeText(output, filename, filename + " created in Internal Storage.", "Could not create file in Internal Storage.");
        }

        if (dialog.getCheckboxes().get(send.getId()).isChecked()){
            sendInformation(output, SharedPreferencesManager.getInstance(), GroupManager.getInstance(), POST_PURPOSE_REGULAR);
        }

        if (dialog.getCheckboxes().get(create.getId()).isChecked() && !dialog.getCheckboxes().get(send.getId()).isChecked() && !dialog.getEditText().getText().toString().isEmpty()){
            sendInformation(dialog.getEditText().getText().toString() + DomainObjects.NEW_LINE + output, SharedPreferencesManager.getInstance(), GroupManager.getInstance(), POST_PURPOSE_CREATE);
        }

        if (dialog.getCheckboxes().get(memo.getId()).isChecked()){
            KeepIntentService.getInstance(context, SharedPreferencesManager.getInstance(), factory.device).
                    saveNoteToCloud(Memo.create(output, createTitle(), context,SharedPreferencesManager.getInstance()));
        }

        if (dialog.getCheckboxes().get(share.getId()).isChecked()){
            factory.device.shareText(context, output);
        }

        announce("Done.");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void beginWorkflow() {
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createOutput() {
        StringBuilder builder = new StringBuilder();
        if (dialog.getCheckboxes().get(personal.getId()).isChecked() ||
                dialog.getCheckboxes().get(health.getId()).isChecked() ||
                dialog.getCheckboxes().get(soul.getId()).isChecked() ||
                dialog.getCheckboxes().get(business.getId()).isChecked() ||
                dialog.getCheckboxes().get(daily.getId()).isChecked()
        ) {
            Entity entity = yearlyService.createEntity(profilesResource);

            builder.append(yearlyService.title(detailsResource, languageResource));
            builder.append(DomainObjects.NEW_LINE);
            builder.append(yearlyService.headline(detailsResource));
            builder.append(DomainObjects.NEW_LINE);
            builder.append(DomainObjects.NEW_LINE);

            builder.append(dialog.getCheckboxes().get(soul.getId()).isChecked() ?
                    yearlyService.soul(detailsResource) + DomainObjects.NEW_LINE + DomainObjects.NEW_LINE :
                    DomainObjects.EMPTY_STRING
            );

            builder.append(dialog.getCheckboxes().get(daily.getId()).isChecked() ?
                    createDailyCyclePrintout() + DomainObjects.NEW_LINE + DomainObjects.NEW_LINE : DomainObjects.EMPTY_STRING);

            builder.append(
                    dialog.getCheckboxes().get(periods.getId()).isChecked() ?
                            "Personal Cycle Periods" + DomainObjects.NEW_LINE + yearlyService.dates(entity.getPERIOD_LISTING()) + DomainObjects.NEW_LINE + DomainObjects.NEW_LINE :
                            DomainObjects.EMPTY_STRING
            );

            builder.append(
                    dialog.getCheckboxes().get(personal.getId()).isChecked() ?
                            "Personal Cycle" + DomainObjects.NEW_LINE + entity.getPERSONAL() + DomainObjects.NEW_LINE + DomainObjects.NEW_LINE :
                            DomainObjects.EMPTY_STRING
            );

            builder.append(
                    dialog.getCheckboxes().get(health.getId()).isChecked() ?
                            "Health Cycle" + DomainObjects.NEW_LINE + entity.getHEALTH() + DomainObjects.NEW_LINE + DomainObjects.NEW_LINE :
                            DomainObjects.EMPTY_STRING
            );

            builder.append(
                    dialog.getCheckboxes().get(business.getId()).isChecked() ?
                            "Business Cycle" + DomainObjects.NEW_LINE + entity.getBUSINESS() + DomainObjects.NEW_LINE + DomainObjects.NEW_LINE :
                            DomainObjects.EMPTY_STRING
            );
        }

        return builder.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createDailyCyclePrintout() {
        //todo include default system's timezone
        DayToken token = dailyService.findByPeriod();
        return new StringBuilder()
                .append(token.getHeadline())
                .append(DomainObjects.NEW_LINE)
                .append(dailyService.createTitle())
                .append(DomainObjects.NEW_LINE)
                .append(Support.createTimestamp(Support.FormatForDateAndTime.TIME_DATE))
                .append(DomainObjects.NEW_LINE)
                .append(dailyService.getSystemTimezone())
                .append(token.getDetail())
                .toString();
    }

    private void announce(String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private String createFilenameForInternalStorage(){
        return "Cycles Information.txt";
    }
    private String createTitle(){
        return "Cycles Information";
    }

    private void sendInformation(String output,  SharedPreferencesManager store, GroupManager machines, String purpose){
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
                        purpose,
                        determineMeta(context, SharedPreferencesManager.getInstance()),
                        EncryptionManager.getInstance().encrypt(context, SharedPreferencesManager.getInstance(), output),
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);

    }
}
