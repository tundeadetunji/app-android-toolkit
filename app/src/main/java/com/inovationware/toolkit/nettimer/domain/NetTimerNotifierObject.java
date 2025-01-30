package com.inovationware.toolkit.nettimer.domain;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;

import java.util.concurrent.TimeUnit;

public class NetTimerNotifierObject implements Runnable {
    private Context context;
    private String details;
    private long interval;
    private TextToSpeech ttsService;
    private NetTimerBistable bistable;
    private TimeUnit timeUnit;
    private int readAloudThisManyTimes;
    private boolean isRegular;

    private int t = 0;
    private final String prefix = "It's time to ";

    @Override
    public void run() {
        if(!isWebResourceOrApp(context, details)){
            for (int i = 0; i < getReadAloudThisManyTimes(); i++){
                ttsService.speak(prefix + details, TextToSpeech.QUEUE_ADD, null);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            startResource(context, details);
        }


        if (isRegular) {
            bistable.cancelRegular();
            bistable.startReverse();
        } else {
            bistable.cancelReverse();
            if (bistable.repeat){
                bistable.startRegular();
            }
        }
    }

    public static NetTimerNotifierObject.NetNotifierObjectBuilder builder(){
        return new NetTimerNotifierObject.NetNotifierObjectBuilder();
    }

    public void setRegular(boolean regular) {
        this.isRegular = regular;
    }

    public long getInterval(){
        return this.interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getReadAloudThisManyTimes() {
        return readAloudThisManyTimes;
    }

    public void setBistable(NetTimerBistable bistable){
        this.bistable = bistable;
    }

    public static class NetNotifierObjectBuilder{
        private Context context;
        private String details;
        private long interval;
        private TimeUnit timeUnit;
        private TextToSpeech ttsService;
        private int readAloudThisManyTimes;

        public NetTimerNotifierObject.NetNotifierObjectBuilder details(String details){
            this.details = details;
            return this;
        }

        public NetTimerNotifierObject.NetNotifierObjectBuilder context(Context context){
            this.context = context;
            return this;
        }

        public NetTimerNotifierObject.NetNotifierObjectBuilder interval(long interval){
            this.interval = interval;
            return this;
        }

        public NetTimerNotifierObject.NetNotifierObjectBuilder ttsService(TextToSpeech ttsService){
            this.ttsService = ttsService;
            return this;
        }

        public NetTimerNotifierObject.NetNotifierObjectBuilder timeUnit(TimeUnit timeUnit){
            this.timeUnit = timeUnit;
            return this;
        }

        public NetTimerNotifierObject.NetNotifierObjectBuilder readAloudThisManyTimes(int readAloudThisManyTimes){
            this.readAloudThisManyTimes = readAloudThisManyTimes;
            return this;
        }

        public NetTimerNotifierObject build(){
            NetTimerNotifierObject object = new NetTimerNotifierObject();
            object.details = this.details.strip().trim();
            object.interval = this.interval;
            object.timeUnit = this.timeUnit;
            object.ttsService = this.ttsService;
            object.readAloudThisManyTimes = this.readAloudThisManyTimes;
            object.context = this.context;
            return object;
        }
    }

    public static void startResource(Context context, String uri) {
        if (context.getPackageManager().getLaunchIntentForPackage(uri) != null) {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(uri));
        }
        else{
            try{
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            }
            catch (Exception ignored){
            }
        }
    }

    public static boolean isWebResourceOrApp(Context context, String resource){
        return resource.trim().toLowerCase().startsWith("http") ||
                context.getPackageManager().getLaunchIntentForPackage(resource) != null;
    }

}
