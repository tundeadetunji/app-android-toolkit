package com.inovationware.toolkit.ui.memento;

import android.content.Context;
import android.widget.TextView;

import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;

public class UiMemento {
    private static UiMemento instance;

    public static UiMemento getInstance() {
        if (instance == null) instance = new UiMemento();
        return instance;
    }

    private UiMemento() {
    }

    public void clear (Context context, SharedPreferencesManager store, String key){
        store.setString(context, key, DomainObjects.EMPTY_STRING);
    }

    public void backup (Context context, SharedPreferencesManager store, String key, String state){
        store.setString(context, key, state);
    }

    public void backupFrom (Context context, SharedPreferencesManager store, String key, TextView textView){
        store.setString(context, key, textView.getText().toString());
    }

    public String restore (Context context, SharedPreferencesManager store, String key){
        return store.getString(context, key);
    }

    public void restoreTo (Context context, SharedPreferencesManager store, String key, TextView textView){
        textView.setText(store.getString(context, key));
    }

}
