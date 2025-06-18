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
import com.inovationware.toolkit.databinding.FragmentCodeBinding;
import com.inovationware.toolkit.databinding.FragmentCyclesBinding;
import com.inovationware.toolkit.ui.adapter.ViewPagerAdapter;

public class CyclesFragment extends Fragment {

    private FragmentCyclesBinding binding;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCyclesBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupViewPager(this);
        return view;
    }

    private void setupViewPager(Fragment fragment){
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragment);
        TabLayout tabLayout = binding.cyclesTabsTabLayout;
        ViewPager2 viewPager = binding.cyclesTabsViewPager;

        adapter.addFragment(new PersonalFragment(), "Personal" );
        adapter.addFragment(new DayFragment(), "Day" );
        adapter.addFragment(new WeekFragment(), "Week");
        adapter.addFragment(new FamilyFragment(), "Family");

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