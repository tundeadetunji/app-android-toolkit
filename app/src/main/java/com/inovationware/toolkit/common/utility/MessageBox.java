package com.inovationware.toolkit.common.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import lombok.Setter;

public abstract class MessageBox {

    private String title = "";

    public void SetTitle(String title) {
        this.title = title;
    }

    @Setter
    private String message = "Proceed?";
    @Setter
    private String positiveButtonText = "Yes";
    @Setter
    private String negativeButtonText = "No";
    private boolean negativeButton = true;

    public MessageBox(String message, String positiveButtonText, String negativeButtonText) {
        this.message = message;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.negativeButton = true;
    }

    public MessageBox(String message, String positiveButtonText, String negativeButtonText, boolean showNegativeButton) {
        this.message = message;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.negativeButton = showNegativeButton;
    }

    public MessageBox(){

    }

    public abstract void positiveButtonAction();

    public abstract void negativeButtonAction();

    public void show(Context context) {
        buildDialog(context).create().show();
    }

    public void showNegativeButton(boolean value) {
        negativeButton = value;
    }

    private AlertDialog.Builder buildDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                positiveButtonAction();
            }
        });
        if (negativeButton)
            builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    negativeButtonAction();
                }
            });
        return builder;
    }

}
