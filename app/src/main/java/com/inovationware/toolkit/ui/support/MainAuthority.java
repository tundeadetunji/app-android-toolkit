package com.inovationware.toolkit.ui.support;

import static com.inovationware.generalmodule.Device.clipboardGetText;
import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.common.utility.Support.determineMeta;
import static com.inovationware.toolkit.common.utility.Support.determineTarget;
import static com.inovationware.toolkit.common.utility.Support.initialParamsAreSet;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.utility.EncryptionManager;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.SignInManager;
import com.inovationware.toolkit.ui.activity.SettingsActivity;

import java.util.List;

import lombok.Getter;

public class MainAuthority {
    private static MainAuthority instance;

    private final List<ApplicationToLoad> applicationsToStart = List.of(
            ApplicationToLoad.create("com.WhatsApp", "WhatsApp"),
            ApplicationToLoad.create("com.Slack", "Slack"),
            ApplicationToLoad.create("org.telegram.messenger", "Telegram")
    );

    public static MainAuthority getInstance() {
        if (instance == null) instance = new MainAuthority();
        return instance;
    }

    private MainAuthority() {
    }

    public void onFinishedLoading(Context context){
        startSystemAlertWindowPermission(context);
        startApplications(context);
    }

    private void startApplications(Context context){
        //Todo Fix use web url instead, so it automatically redirects to app
        /*for (ApplicationToLoad app : applicationsToStart){
            AppLauncher.launchAppWithNotification(context, app.getPackageName(), app.getAppName());
        }*/
    }

    public void sendToPCFromClipboard(Context context, Factory factory, SharedPreferencesManager store, GroupManager machines, EncryptionManager security) {
        //canSend
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        if (initialParamsAreSet(context, store, machines) && clipboard.hasText())
            doSend(context, factory, store, machines, security, POST_PURPOSE_REGULAR, determineMeta(context, store));
    }

    public void openSettingsActivity(Context context, SignInManager signInManager) {
        if (signInManager.getSignedInUser(context) != null)
            context.startActivity(new Intent(context, SettingsActivity.class));
        else
            signInManager.beginLoginProcess(context, SettingsActivity.class.getSimpleName());
    }

    void doSend(Context context, Factory factory, SharedPreferencesManager store, GroupManager machines, EncryptionManager security, String purpose, String meta) {
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
                        purpose,
                        meta,
                        security.encrypt(context, store, clipboardGetText(context)),
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }

    public void startSystemAlertWindowPermission(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    //Log.i(TAG, "[startSystemAlertWindowPermission] requesting system alert window permission.");
                    context.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName())));
                }
            }
        } catch (Exception ignored) {

        }
    }

}

@Getter
class ApplicationToLoad{
    private String packageName;
    private String appName;

    private ApplicationToLoad(String packageName, String appName){
        this.packageName = packageName;
        this.appName = appName;
    }

    public static ApplicationToLoad create(String packageName, String appName){
        return new ApplicationToLoad(packageName, appName);
    }

}