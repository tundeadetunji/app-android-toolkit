package com.inovationware.toolkit.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.inovationware.toolkit.databinding.FragmentMenuBottomSheetBinding;
import com.inovationware.toolkit.common.utility.Ui;

import java.util.ArrayList;
import java.util.List;

public class MenuBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "MenuBottomSheetFragment";
    private Context context;
    private FragmentMenuBottomSheetBinding binding;
    private View view;

    private List<Ui.ButtonObject> buttons = new ArrayList<>();
    public MenuBottomSheetFragment(List<Ui.ButtonObject> buttons){
        this.buttons = buttons;
    }
    public MenuBottomSheetFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setUi();
        return view;
    }

    private void setUi() {
        if (buttons.isEmpty()) return;
        for (Ui.ButtonObject button : buttons){
            binding.rootLinearLayout.addView(button.set());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}