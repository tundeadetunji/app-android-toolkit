package com.inovationware.toolkit.global.library.utility;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.inovationware.toolkit.R;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Ui {
    private static Ui instance;

    private Ui() {
    }

    public static Ui getInstance() {
        if (instance == null) instance = new Ui();
        return instance;
    }

    public void bindProperty(Context context, AutoCompleteTextView control, String[] list, int layout, String initialText) {
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

    public boolean isEmpty(AutoCompleteTextView textView) {
        return textView.getText().toString().length() < 1 ? true : false;
    }

    /*public String content(TextView textView){
        return textView.getText().toString();
    }
    */

    public String content(AutoCompleteTextView textView) {
        return textView.getText().toString();
    }

    public void bindItem(LinearLayoutCompat layout, View view) {
        layout.addView(view);
    }

    public void bindItems(LinearLayoutCompat layout, List<View> views) {
        for (View view : views) {
            bindItem(layout, view);
        }
    }

    public static class ButtonObject {
        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class MarginInfo {
            private int left = 8;
            private int top = 8;
            private int right = 8;
            private int bottom = 0;

        }

        /**
         * LinearLayout.LayoutParams for MATCH_PARENT and WRAP_CONTENT, 0 otherwise
         */
        @Getter @Setter @AllArgsConstructor
        public static class DimensionInfo {
            private int width;
            private int height;
        }

        /**
         * for iconGravity: e.g. MaterialButton.ICON_GRAVITY_TEXT_START
         * for iconDrawable: R.drawable.your_icon
         */
        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class ViewInfo {
            private String text;
            int iconGravity;
            int iconDrawable;
            int weight;
        }

        private final MaterialButton button;
        private final View.OnClickListener listener;
        private final MarginInfo margins;
        private final DimensionInfo dimensions;
        private final ViewInfo viewing;
        public ButtonObject(Context context, View.OnClickListener listener, MarginInfo margins, DimensionInfo dimensions, ViewInfo viewing) {
            this.button = new MaterialButton(context);
            this.listener = listener;
            this.margins = margins;
            this.dimensions = dimensions;
            this.viewing = viewing;
        }

        public MaterialButton set() {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimensions.width, dimensions.height);
            params.weight = viewing.weight;
            params.setMargins(margins.left, margins.top, margins.right, margins.bottom);
            button.setLayoutParams(params);
            button.setText(viewing.text);
            button.setIconGravity(viewing.iconGravity);
            button.setIconResource(viewing.iconDrawable);
            button.setOnClickListener(listener);
            return button;
        }
    }

    public void bindButton(LinearLayoutCompat layout, ButtonObject button) {
        layout.addView(button.set());
    }

}
