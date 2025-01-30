package com.inovationware.toolkit.tts.service;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTSService {
    private static TTSService instance;
    private final Context context;
    private TextToSpeech service;

    private TTSService(Context context){
        this.context = context;
        service = new TextToSpeech(this.context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result =service.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //can't do it
                    }
                }
            }
        });
    }

    public static TTSService getInstance(Context context){
        if (instance == null) {
            instance = new TTSService(context);
        }
        return instance;
    }

    public TextToSpeech getService(){
        return service;
    }

    public void shutdown(){
        if (service != null) {
            service.stop();
            service.shutdown();
        }
    }

    public void say(String text){
        service.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null);
    }

}
