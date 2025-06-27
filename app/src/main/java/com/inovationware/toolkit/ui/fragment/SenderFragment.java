package com.inovationware.toolkit.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.WelcomeCaptionService;
import com.inovationware.toolkit.databinding.FragmentSendBinding;
import com.inovationware.toolkit.databinding.FragmentSenderBinding;
import com.inovationware.toolkit.features.location.service.impl.LocationServiceImpl;
import com.inovationware.toolkit.ui.adapter.ViewPagerAdapter;

public class SenderFragment extends Fragment {

    private FragmentSenderBinding binding;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSenderBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupUi(this);
        return view;

    }

    private void setupUi(Fragment fragment){
        WelcomeCaptionService.getInstance(requireActivity(), view.getContext(), LocationServiceImpl.getInstance(view.getContext())).setupCaptions(binding.welcomeCard.captionTextView, binding.welcomeCard.toolkitInfoTextView, SharedPreferencesManager.getInstance(), GroupManager.getInstance());
        setupViewPager(this);

    }

    private void setupViewPager(Fragment fragment){
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragment);
        TabLayout tabLayout = binding.sendTabsTabLayout;
        ViewPager2 viewPager = binding.sendTabsViewPager;

        adapter.addFragment(new LinkFragment(), "Link" );
        adapter.addFragment(new SendFragment(), "Send" );
        adapter.addFragment(new ReceiveFragment(), "Receive" );
        adapter.addFragment(new MeetingFragment(), "Meet");

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(adapter.getPageTitle(position))
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}