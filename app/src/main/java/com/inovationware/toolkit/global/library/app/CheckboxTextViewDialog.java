package com.inovationware.toolkit.global.library.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

public abstract class CheckboxTextViewDialog {
    private Context context;
    private String title;
    private String dialogMessage;
    @Getter
    private TextInputEditText editText;
    @Getter
    private final Map<Integer, CheckBox> checkboxes = new HashMap<>();
    private final String positiveButtonText;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CheckboxTextViewDialog(Context context, String title, String message, String positiveButtonText, List<CheckBox> checkboxes, TextInputEditText editText) {
        this.context = context;
        this.title = title;
        this.positiveButtonText = positiveButtonText;
        this.dialogMessage = message;
        this.editText = editText;
        checkboxes.forEach(checkbox -> this.checkboxes.put(checkbox.getId(), checkbox));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        GridLayout gridLayout = new GridLayout(context);
        gridLayout.setColumnCount(2);

        checkboxes.keySet().forEach(checkbox -> gridLayout.addView(checkboxes.get(checkbox)));

        layout.addView(gridLayout);

        layout.addView(editText);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(layout);

        builder.setView(scrollView);

        builder.setMessage(dialogMessage);

        builder.setPositiveButton(positiveButtonText, (dialog, which) -> onPositiveButtonClick());

        Dialog dialog = builder.create();
        dialog.show();
    }

    public abstract void onPositiveButtonClick();
}