package com.inovationware.toolkit.global.library.utility;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.inovationware.toolkit.R;

public class Ui {
    private static Ui instance;
    private Ui(){}
    public static Ui getInstance(){
        if(instance == null) instance = new Ui();
        return instance;
    }

    public void bindProperty(Context context, AutoCompleteTextView control, String[] list, int layout, String initialText){
        control.setText(initialText);
        com.inovationware.generalmodule.Ui.bindProperty(context, control, list == null ? new String[0] : list, layout);
    }

    public void bindProperty(Context context, AutoCompleteTextView dropdown, String[] list) {
        dropdown.setAdapter(new ArrayAdapter<String>(context, R.layout.default_drop_down, list == null ? new String[0] : list));
    }

    public void bindProperty(Context context, AutoCompleteTextView dropdown, Integer[] list) {
        dropdown.setAdapter(new ArrayAdapter<Integer>(context, R.layout.default_drop_down, list == null ? new Integer[0] : list));
    }

    public void bindProperty(Context context, AutoCompleteTextView dropdown, String[] list, String initialText) {
        dropdown.setAdapter(new ArrayAdapter<String>(context, R.layout.default_drop_down, list == null ? new String[0] : list));
        dropdown.setText(initialText);
    }

    /*public boolean isEmpty(TextView textView){
        return com.inovationware.generalmodule.General.isEmpty(textView);
    }*/

    public boolean isEmpty(AutoCompleteTextView textView){
        return textView.getText().toString().length() < 1 ? true : false;
    }

    /*public String content(TextView textView){
        return textView.getText().toString();
    }
    */

    public String content(AutoCompleteTextView textView){
        return textView.getText().toString();
    }

}
