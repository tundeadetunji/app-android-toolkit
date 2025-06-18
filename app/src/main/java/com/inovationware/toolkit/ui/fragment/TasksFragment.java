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
import com.inovationware.toolkit.databinding.FragmentLocalTaskBinding;
import com.inovationware.toolkit.databinding.FragmentTasksBinding;
import com.inovationware.toolkit.ui.adapter.ViewPagerAdapter;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupViewPager(this);

        return view;
    }


    private void setupViewPager(Fragment fragment){
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragment);
        TabLayout tabLayout = binding.tasksTabsTabLayout;
        ViewPager2 viewPager = binding.tasksTabsViewPager;

        adapter.addFragment(new LocalTaskFragment(), "Local" );
        adapter.addFragment(new PreviousTasksFragment(), "Previous" );
        adapter.addFragment(new RemoteTasksFragment(), "Remote");
        adapter.addFragment(new NewTaskFragment(), "New");
        adapter.addFragment(new SchedulerFragment(), "Schedule");

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