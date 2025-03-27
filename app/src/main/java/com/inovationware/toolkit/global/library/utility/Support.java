package com.inovationware.toolkit.global.library.utility;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.Strings.EMPTY_STRING;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.ui.activity.MainActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
public class Support {
    public static boolean canTransferData(Context context, SharedPreferencesManager store, GroupManager machines){
        return thereIsInternet(context) && initialParamsAreSet(context, store, machines);
    }

    public static boolean initialParamsAreSet(Context context, SharedPreferencesManager store, GroupManager machines) {
        return !store.getString(context, Strings.SHARED_PREFERENCES_ID_KEY, EMPTY_STRING).isEmpty() &&
                !machines.getDefaultDevice(context).isEmpty() &&
                !store.getUsername(context).isEmpty() &&
                !store.getBaseUrl(context).isEmpty() &&
                !store.getEncryptionSalt(context).isEmpty() &&
                !store.getEncryptionPassword(context).isEmpty();
    }

    public static String determineMeta(Context context, SharedPreferencesManager store) {
        return store.getTargetMode(context);
    }

    public static String determineTarget(Context context,SharedPreferencesManager store, GroupManager machines) {
        return store.getTargetMode(context).equalsIgnoreCase(Strings.TARGET_MODE_TO_DEVICE) ? machines.getDefaultDevice(context) : store.getID(context);
    }

    public static boolean responseStringIsValid(String content) {
        String temp = content.strip().trim();
        return !temp.startsWith("<!--404-->") && !temp.startsWith("<!DOCTYPE html>") && !temp.startsWith("<meta") && !temp.contains("<html>") && !temp.contains("<xml>");
    }

    public static void startApp(Context context, String uri) {
        if (context.getPackageManager().getLaunchIntentForPackage(uri) != null) {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(uri));
        }
    }

    public static boolean isWebResource(String resource){
        return resource.trim().toLowerCase().startsWith("http");
    }

    public static boolean isWebResourceOrApp(Context context,String resource){
        return resource.trim().toLowerCase().startsWith("http") ||
                context.getPackageManager().getLaunchIntentForPackage(resource) != null;
    }

    public static boolean isApp(Context context, String resource){
        return context.getPackageManager().getLaunchIntentForPackage(resource) != null;
    }

    public static boolean isEmpty(TextView textView){
        return textView.getText().toString().isEmpty();
    }

    public static void announce(Context context, String s) {
        announce(context, s, Toast.LENGTH_LONG);
    }

    public static void announce(Context context, String s, int length) {
        Toast.makeText(context, s , length).show();
    }

    public static void openUrlInBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
    public static void visit(Context context, String url){
        openUrlInBrowser(context, url);
    }
    public static void visit(Context context, String url, boolean isFromNonActivityClass){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (isFromNonActivityClass) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String createTimestamp(FormatForDateAndTime format){
        return "\n\n\n" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatForDateAndTimeStringMap.get(format)));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String createTimestampForFile(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatForDateAndTimeStringMap.get(FormatForDateAndTime.DATE_TIME_FOR_FILE)));
    }

    public static void getOutOfThere(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public enum FormatForDateAndTime{
        DATE,
        TIME,
        DATE_TIME,
        TIME_DATE,
        DATE_TIME_FOR_FILE
    }

    public static int anyOf(int inclusiveMin, int exclusiveMax){
        // Check if the range is valid
        if (inclusiveMin >= exclusiveMax) {
            throw new IllegalArgumentException("inclusiveMin must be less than exclusiveMax");
        }

        // Create a Random object
        Random random = new Random();

        // Generate a random number within the specified range
        return random.nextInt(exclusiveMax - inclusiveMin) + inclusiveMin;
    }

    private static Map<FormatForDateAndTime, String> formatForDateAndTimeStringMap =
            Map.of(
                    FormatForDateAndTime.DATE, Strings.FORMAT_FOR_DATE,
                    FormatForDateAndTime.TIME, Strings.FORMAT_FOR_TIME,
                    FormatForDateAndTime.DATE_TIME, Strings.FORMAT_FOR_DATE_TIME,
                    FormatForDateAndTime.TIME_DATE, Strings.FORMAT_FOR_TIME_DATE,
                    FormatForDateAndTime.DATE_TIME_FOR_FILE, Strings.FORMAT_FOR_DATE_TIME_FOR_FILE
            );

    public static String createUrl(Context context, Transfer.Intent intent, SharedPreferencesManager store){
        return Strings.BASE_URL(context, store) + "/api/regular.ashx?id=" + store.getID(context) + "&intent=" + intent + "&username=" + store.getUsername(context);
    }
}
