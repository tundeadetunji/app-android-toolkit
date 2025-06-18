package com.inovationware.toolkit.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentLinkBinding;
import com.inovationware.toolkit.databinding.FragmentRemoteTasksBinding;


public class RemoteTasksFragment extends Fragment {
    private View view;
    private FragmentRemoteTasksBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRemoteTasksBinding.inflate(inflater, container, false);
        view = binding.getRoot();


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}