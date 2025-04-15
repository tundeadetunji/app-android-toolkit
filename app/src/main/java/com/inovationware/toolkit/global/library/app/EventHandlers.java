package com.inovationware.toolkit.global.library.app;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_RESUME_WORK;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;

import android.content.Context;
import android.content.Intent;

import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.ui.activity.BoardActivity;
import com.inovationware.toolkit.ui.activity.CodeActivity;
import com.inovationware.toolkit.ui.activity.CyclesActivity;
import com.inovationware.toolkit.ui.activity.EspActivity;
import com.inovationware.toolkit.ui.activity.NetTimerActivity;
import com.inovationware.toolkit.ui.activity.ReplyActivity;
import com.inovationware.toolkit.ui.activity.ScheduleActivity;

public class EventHandlers {
    private static EventHandlers instance;
    public static EventHandlers getInstance() {
        if (instance == null) instance = new EventHandlers();
        return instance;
    }
    private EventHandlers() {
    }

    public void pcButton(Context context){
        context.startActivity(new Intent(context, ReplyActivity.class));
    }
    public void espButton(Context context){
        context.startActivity(new Intent(context, EspActivity.class));
    }
    public void codeButton(Context context){
        context.startActivity(new Intent(context, CodeActivity.class));
    }
    public void netTimerTasksButton(Context context){
        if (SignInManager.getInstance().getSignedInUser(context) != null)
            context.startActivity(new Intent(context, NetTimerActivity.class));
        else
            SignInManager.getInstance().beginLoginProcess(context, NetTimerActivity.class.getSimpleName());
    }
    public void schedulerButton(Context context){
        context.startActivity(new Intent(context, ScheduleActivity.class));
    }
    public void cyclesButton(Context context){
        context.startActivity(new Intent(context, CyclesActivity.class));
    }
    public void meetingButton(Context context){
        /*if (SignInManager.getInstance().getSignedInUser(context) != null)
            context.startActivity(new Intent(context, NetTimerActivity.class));
        else
            SignInManager.getInstance().beginLoginProcess(context, BoardActivity.class.getSimpleName());*/
    }

    public void resumeWorkButton(Context context, Factory factory, SharedPreferencesManager store, GroupManager machines, String app){

        if (!thereIsInternet(context) || !initialParamsAreSet(context, store, machines))
            return;

        String file = app == null ? "" : app;
        if (app.isEmpty()) file = "";


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
                        POST_PURPOSE_RESUME_WORK,
                        DomainObjects.EMPTY_STRING,
                        file,
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);

    }


}
