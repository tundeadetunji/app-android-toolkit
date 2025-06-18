package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.toolkit.global.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_FAVORITE_URL_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_PINNED_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_READING_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_RUNNING_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_SCRATCH_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_TODO_KEY;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentHomeBinding;
import com.inovationware.toolkit.databinding.FragmentLinksBinding;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.services.LinksService;
import com.inovationware.toolkit.global.library.services.WelcomeCaptionService;

public class LinksFragment extends Fragment {

    private View view;
    private FragmentLinksBinding binding;
    private Context context;
    private SharedPreferencesManager store;
    private GroupManager machines;
    private Feedback feedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLinksBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupAccess();
        setupListeners();
        setupUi();

        return view;

    }

    private void setupAccess() {
        context = view.getContext();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        feedback = new Feedback(view.getContext());
    }

    private void setupListeners() {

        LinksService.getInstance(getActivity(), context, store).setupListeners(
                binding.pinnedButton,
                binding.readingLinkButton,
                binding.todoLinkButton,
                binding.runningLinkButton,
                binding.scratchLinkButton,
                binding.extraLinkButton
        );
    }

    private void setupUi(){
        //WelcomeCaptionService.getInstance(getActivity(), context).setupCaptions(view.findViewById(R.id.captionTextView), view.findViewById(R.id.toolkitInfoTextView), store, machines);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}