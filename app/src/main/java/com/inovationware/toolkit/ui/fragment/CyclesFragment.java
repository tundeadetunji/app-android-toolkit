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
import com.inovationware.toolkit.databinding.FragmentCodeBinding;
import com.inovationware.toolkit.databinding.FragmentCyclesBinding;
import com.inovationware.toolkit.features.location.service.impl.LocationServiceImpl;
import com.inovationware.toolkit.ui.adapter.ViewPagerAdapter;

public class CyclesFragment extends Fragment {

    private FragmentCyclesBinding binding;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCyclesBinding.inflate(inflater, container, false);
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
        TabLayout tabLayout = binding.cyclesTabsTabLayout;
        ViewPager2 viewPager = binding.cyclesTabsViewPager;

        adapter.addFragment(new DayFragment(), "Day" );
        adapter.addFragment(new FamilyFragment(), "Family");
        adapter.addFragment(new PersonalFragment(), "Personal" );
        adapter.addFragment(new WeekFragment(), "Week");

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