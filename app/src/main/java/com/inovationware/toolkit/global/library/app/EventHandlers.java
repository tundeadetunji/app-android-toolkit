package com.inovationware.toolkit.global.library.app;

import android.content.Context;
import android.content.Intent;

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




}
