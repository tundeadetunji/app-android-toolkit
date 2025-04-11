package com.inovationware.toolkit.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.inovationware.toolkit.databinding.FragmentMenuBottomSheetBinding;
import com.inovationware.toolkit.global.library.app.SignInManager;
import com.inovationware.toolkit.ui.activity.BoardActivity;
import com.inovationware.toolkit.ui.activity.CodeActivity;
import com.inovationware.toolkit.ui.activity.EspActivity;
import com.inovationware.toolkit.ui.activity.NetTimerActivity;
import com.inovationware.toolkit.ui.activity.ReplyActivity;
import com.inovationware.toolkit.ui.activity.ScheduleActivity;

public class MenuBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "MenuBottomSheetFragment";
    private Context context;
    private FragmentMenuBottomSheetBinding binding;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupListeners();
        return view;
        //return inflater.inflate(R.layout.fragment_menu_bottom_sheet, container, false);
    }

    private void setupListeners() {
        binding.toPcButton.setOnClickListener(handleToPcButton);
        binding.toEspHomeButton.setOnClickListener(handleToEspHomeButton);
        binding.toCodeButton.setOnClickListener(handleToCodeButton);
        binding.toNetTimerTasksButton.setOnClickListener(handleToNetTimerTasksButton);
        binding.toScheduleButton.setOnClickListener(handleToSchedulerButton);
        binding.toBoardButton.setOnClickListener(handleToBoardButton);
    }

    private final View.OnClickListener handleToPcButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, ReplyActivity.class));
        }
    };

    private final View.OnClickListener handleToEspHomeButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, EspActivity.class));
        }
    };

    private final View.OnClickListener handleToCodeButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, CodeActivity.class));
        }
    };

    private final View.OnClickListener handleToNetTimerTasksButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (SignInManager.getInstance().getSignedInUser(context) != null)
                startActivity(new Intent(context, NetTimerActivity.class));
            else
                SignInManager.getInstance().beginLoginProcess(context, NetTimerActivity.class.getSimpleName());
        }
    };

    private final View.OnClickListener handleToSchedulerButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, ScheduleActivity.class));        }
    };

    private final View.OnClickListener handleToBoardButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SignInManager.getInstance().beginLoginProcess(context, BoardActivity.class.getSimpleName());        }
    };

@Override
public void onDestroyView() {
    super.onDestroyView();
    binding = null;
}

}