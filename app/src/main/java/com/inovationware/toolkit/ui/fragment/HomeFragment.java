package com.inovationware.toolkit.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.databinding.FragmentHomeBinding;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.app.SignInManager;
import com.inovationware.toolkit.global.repository.ResourcesManager;
import com.inovationware.toolkit.location.service.LocationService;
import com.inovationware.toolkit.location.service.impl.LocationServiceImpl;
import com.inovationware.toolkit.ui.activity.CodeActivity;
import com.inovationware.toolkit.ui.activity.CyclesActivity;
import com.inovationware.toolkit.ui.activity.EspActivity;
import com.inovationware.toolkit.ui.activity.NetTimerActivity;
import com.inovationware.toolkit.ui.activity.ReplyActivity;
import com.inovationware.toolkit.ui.activity.ScheduleActivity;
import com.inovationware.toolkit.ui.adapter.ViewPagerAdapter;

import static com.inovationware.toolkit.global.library.utility.Code.content;

public class HomeFragment extends Fragment {
    private Context context;
    private SharedPreferencesManager store;
    private GroupManager machines;
    private View view;
    private Factory factory;


    private FragmentHomeBinding binding;

    private Feedback feedback;
    private static final int PERMISSION_LOCATION_REQUEST = 997;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupVariables();
        setupListeners();
        setupUi(this);

        return view;
    }


    private void setupVariables() {
        context = view.getContext();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        feedback = new Feedback(view.getContext());
        factory = Factory.getInstance();
    }

    private void setupListeners() {
        binding.PCButton.setOnClickListener(PCButtonClick);
        binding.EspButton.setOnClickListener(EspButtonClick);
        binding.codeButton.setOnClickListener(codeButtonClick);
        binding.tasksButton.setOnClickListener(tasksButtonClick);
        binding.schedulerButton.setOnClickListener(schedulerButtonClick);
        binding.meetingButton.setOnClickListener(meetingButtonClick);
        binding.guideImageView.setOnClickListener(guideImageViewClick);
    }

    private void setupUi(Fragment fragment) {

        //binding.guideImageView.setImageResource(new ResourcesManager().getWelcomeImage());
        binding.guideImageView.setImageResource(new ResourcesManager().getWelcomeImage(store.getTheme(context)));

        //Todo move this to MainActivity
//        WelcomeCaptionService.getInstance(this.getActivity(), view.getContext()).setupCaptions(view.findViewById(R.id.captionTextView), view.findViewById(R.id.toolkitInfoTextView), store, machines)

        setupViewPager(fragment);
    }

    private void setupViewPager(Fragment fragment){
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragment);
        TabLayout tabLayout = binding.homeTabsTabLayout;
        ViewPager2 viewPager = binding.homeTabsViewPager;

        adapter.addFragment(new LinksFragment(), "Pinned" );
        adapter.addFragment(new NotesFragment(), "Notes" );
        adapter.addFragment(new SearchFragment(), "Search");

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(adapter.getPageTitle(position))
        ).attach();
    }

    private final View.OnClickListener PCButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, ReplyActivity.class));
        }
    };

    private final View.OnClickListener EspButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, EspActivity.class));
        }
    };

    private final View.OnClickListener codeButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, CodeActivity.class));
        }
    };

    private final View.OnClickListener tasksButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (SignInManager.getInstance().getSignedInUser(context) != null)
                startActivity(new Intent(context, NetTimerActivity.class));
            else
                SignInManager.getInstance().beginLoginProcess(context, NetTimerActivity.class.getSimpleName());
        }
    };

    private final View.OnClickListener schedulerButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, ScheduleActivity.class));
        }
    };

    private final View.OnClickListener meetingButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    private final View.OnClickListener guideImageViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, CyclesActivity.class));
        }
    };

    /**
     * calls #updatePeriodically
     */
    /*private final View.OnLongClickListener guideImageViewLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            service.stopLocationUpdates();
            Toast.makeText(context, DomainObjects.FRIENDLY_MESSAGES.get(new Random().nextInt(DomainObjects.FRIENDLY_MESSAGES.size() - 0) + 0), Toast.LENGTH_SHORT).show();
            return true;
        }
    };*/


    private final View.OnClickListener linksLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, ReplyActivity.class));
        }
    };




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_REQUEST);

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {requestPermissions(...);}
        } else {
            // Permissions already granted, proceed to get location
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //Toast.makeText(context, "Permission is required for Location to work!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLocationPermissions();
    }
}