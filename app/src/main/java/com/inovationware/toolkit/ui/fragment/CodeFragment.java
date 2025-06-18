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
import com.inovationware.toolkit.databinding.FragmentHomeBinding;
import com.inovationware.toolkit.ui.adapter.ViewPagerAdapter;

public class CodeFragment extends Fragment {
    private FragmentCodeBinding binding;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCodeBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupViewPager(this);
        return view;
    }

    private void setupViewPager(Fragment fragment){
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragment);
        TabLayout tabLayout = binding.codeTabsTabLayout;
        ViewPager2 viewPager = binding.codeTabsViewPager;

        adapter.addFragment(new TranslatorFragment(), "Translate" );
        adapter.addFragment(new ConverterFragment(), "Convert" );
        adapter.addFragment(new GithubFragment(), "Github" );
        adapter.addFragment(new EspFragment(), "Configure");

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