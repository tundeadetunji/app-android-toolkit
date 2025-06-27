package com.inovationware.toolkit.common.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceClient {

    private static DeviceClient instance;
    public static DeviceClient getInstance(){
        if(instance == null) instance = new DeviceClient();
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void toClipboard(String s, Context context){
        com.inovationware.generalmodule.Device.clipboardSetText(context, s);
    }

    public void tell(String s, Context context){
        if (s.trim().length() < 1) return;
        new com.inovationware.generalmodule.Feedback(context).toast(s);
    }

    public void shareText(Context context, String textToShare) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public String readText(Context context, Uri fileUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder fileContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            fileContent.append(line).append("\n");
        }
        reader.close();
        return fileContent.toString();
    }

    public String getSystemId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void startAlarm(Context context, int hour, int minute, boolean pm){
        //AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, minute); // Set the minute to 30
        calendar.set(Calendar.HOUR, hour); // Set the hour to 14 (2 PM)
        calendar.set(Calendar.AM_PM, pm ? Calendar.PM : Calendar.AM); // Set AM/PM to PM

        // Check if the set time has already passed for the current day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            // If the set time has passed, add a day to the calendar
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR));
        intent.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE));
        context.startActivity(intent);
    }

    private boolean vlcIsInstalled(PackageManager pm){
        try {
            pm.getPackageInfo("org.videolan.vlc", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // VLC is not installed
            return false;
        }
    }

    public void openAudioFile(Context context, File file, PackageManager packageManager){
        if (vlcIsInstalled(packageManager)){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
            intent.setPackage("org.videolan.vlc");
            context.startActivity(intent);
        }else{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
            context.startActivity(Intent.createChooser(intent, "Choose a media player"));
        }
    }

}
