package com.inovationware.toolkit.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentSearchBinding;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.SiteManager;

import static com.inovationware.generalmodule.Device.clipboardSetText;
import static com.inovationware.toolkit.common.utility.Code.clean;
import static com.inovationware.toolkit.common.utility.Code.content;
import static com.inovationware.toolkit.common.utility.Code.isNothing;

public class SearchFragment extends Fragment {
    private GroupManager machines;
    private SharedPreferencesManager store;
    private SiteManager sites;
    private View view;

    Button searchButton, copyUrlButton;
    AutoCompleteTextView siteDropDown;
    TextView searchTermTextBox;

    Feedback feedback;

    private FragmentSearchBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        sites = SiteManager.getInstance(view.getContext());
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        feedback = new Feedback(view.getContext());

        searchTermTextBox = view.findViewById(R.id.searchTermTextBox);
        searchTermTextBox.setText(sites.getLastSearchTerm(view.getContext()));

        copyUrlButton = view.findViewById(R.id.copyUrlButton);
        copyUrlButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (!isNothing(content(searchTermTextBox)) && !isNothing(content(siteDropDown)))
                    clipboardSetText(view.getContext(), createUrl(content(siteDropDown), content(searchTermTextBox)));
            }
        });

        siteDropDown = view.findViewById(R.id.siteDropDown);
        siteDropDown.setText(sites.getLastVisitedSite(view.getContext()));
        sites.setDropDown(view.getContext(), siteDropDown, sites.getSites(view.getContext()));

        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNothing(content(siteDropDown)))
                    return;
                if (!sites.exists(view.getContext(), clean(content(siteDropDown)))) {
                    sites.setDropDown(view.getContext(), siteDropDown, sites.addSite(view.getContext(), clean(content(siteDropDown))));
                }
                sites.setLastVisitedSite(view.getContext(), content(siteDropDown));
                sites.setLastSearchTerm(view.getContext(), content(searchTermTextBox));
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(createUrl(content(siteDropDown), content(searchTermTextBox)))));
            }
        });

        binding.editSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.siteDropDown.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.siteDropDown.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.siteDropDown, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    String createUrl(String site, String term) {
        return clean(site) + " " + clean(term);
    }

}