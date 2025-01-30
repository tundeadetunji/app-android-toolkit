package com.inovationware.toolkit.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.cycles.model.Entity;
import com.inovationware.toolkit.cycles.model.domain.Period;
import com.inovationware.toolkit.cycles.service.CyclesFacade;
import com.inovationware.toolkit.databinding.ActivityProfilerBinding;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Ui;
import com.inovationware.toolkit.profile.model.Profile;
import com.inovationware.toolkit.profile.strategy.ProfileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfilerActivity extends AppCompatActivity {

    private ActivityProfilerBinding binding;
    private ProfileManager profiler;
    private Ui ui;
    private CyclesFacade facade;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferencesManager store = SharedPreferencesManager.getInstance();
        profiler = ProfileManager.getInstance(store, ProfilerActivity.this);
        ui = Ui.getInstance();
        facade = CyclesFacade.getInstance();

        setupUi();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void setupUi() {
        ui.bindProperty(ProfilerActivity.this, binding.namesDropDown, profiler.getNameListing());

        binding.computeProfilesButton.setOnClickListener(handleComputeProfiles);
        binding.computeFirstProfileButton.setOnClickListener(handleComputeFirstProfile);
        binding.computeSecondProfileButton.setOnClickListener(handleComputeSecondProfile);
        binding.computeThirdProfileButton.setOnClickListener(handleComputeThirdProfile);
        binding.computeFourthProfileButton.setOnClickListener(handleComputeFourthProfile);
        binding.computeFifthProfileButton.setOnClickListener(handleComputeFifthProfile);
        binding.computeSixthProfileButton.setOnClickListener(handleComputeSixthProfile);
        binding.computeSeventhProfileButton.setOnClickListener(handleComputeSeventhProfile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.homeHomeMenuItem) {
            startActivity(new Intent(ProfilerActivity.this, MainActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private final View.OnClickListener handleComputeFirstProfile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.firstDropDown.getText().toString().isEmpty()) return;

            Profile profile = profiler.getProfile(binding.firstDropDown.getText().toString());
            computeDetails(facade.createEntity(profile.getMonth(), profile.getDay(), profile.getName()));
        }
    };

    private final View.OnClickListener handleComputeSecondProfile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.secondDropDown.getText().toString().isEmpty()) return;

            Profile profile = profiler.getProfile(binding.secondDropDown.getText().toString());
            computeDetails(facade.createEntity(profile.getMonth(), profile.getDay(), profile.getName()));
        }
    };
    private final View.OnClickListener handleComputeThirdProfile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.thirdDropDown.getText().toString().isEmpty()) return;

            Profile profile = profiler.getProfile(binding.thirdDropDown.getText().toString());
            computeDetails(facade.createEntity(profile.getMonth(), profile.getDay(), profile.getName()));

        }
    };
    private final View.OnClickListener handleComputeFourthProfile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.fourthDropDown.getText().toString().isEmpty()) return;

            Profile profile = profiler.getProfile(binding.fourthDropDown.getText().toString());
            computeDetails(facade.createEntity(profile.getMonth(), profile.getDay(), profile.getName()));
        }
    };
    private final View.OnClickListener handleComputeFifthProfile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.fifthDropDown.getText().toString().isEmpty()) return;

            Profile profile = profiler.getProfile(binding.fifthDropDown.getText().toString());
            computeDetails(facade.createEntity(profile.getMonth(), profile.getDay(), profile.getName()));
        }
    };
    private final View.OnClickListener handleComputeSixthProfile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.sixthDropDown.getText().toString().isEmpty()) return;

            Profile profile = profiler.getProfile(binding.sixthDropDown.getText().toString());
            computeDetails(facade.createEntity(profile.getMonth(), profile.getDay(), profile.getName()));
        }
    };
    private final View.OnClickListener handleComputeSeventhProfile = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.seventhDropDown.getText().toString().isEmpty()) return;

            Profile profile = profiler.getProfile(binding.seventhDropDown.getText().toString());
            computeDetails(facade.createEntity(profile.getMonth(), profile.getDay(), profile.getName()));
        }
    };

    private final View.OnClickListener handleComputeProfiles = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            List<Entity> entities = new ArrayList<>();
            for (Map.Entry<String, Profile> entry : profiler.getProfiles().entrySet()) {
                entities.add(new Entity(entry.getValue().getDay(), entry.getValue().getMonth(), entry.getValue().getName()));
            }

            ui.bindProperty(
                    binding.getRoot().getContext(),
                    binding.firstDropDown,
                    entities.stream()
                            .filter(entity -> entity.getPERIOD().equals(Period.First))
                            .map(Entity::getNAME).toArray(String[]::new)
            );

            ui.bindProperty(
                    binding.getRoot().getContext(),
                    binding.secondDropDown,
                    entities.stream()
                            .filter(entity -> entity.getPERIOD().equals(Period.Second))
                            .map(Entity::getNAME).toArray(String[]::new)
            );

            ui.bindProperty(
                    binding.getRoot().getContext(),
                    binding.thirdDropDown,
                    entities.stream()
                            .filter(entity -> entity.getPERIOD().equals(Period.Third))
                            .map(Entity::getNAME).toArray(String[]::new)
            );

            ui.bindProperty(
                    binding.getRoot().getContext(),
                    binding.fourthDropDown,
                    entities.stream()
                            .filter(entity -> entity.getPERIOD().equals(Period.Fourth))
                            .map(Entity::getNAME).toArray(String[]::new)
            );

            ui.bindProperty(
                    binding.getRoot().getContext(),
                    binding.fifthDropDown,
                    entities.stream()
                            .filter(entity -> entity.getPERIOD().equals(Period.Fifth))
                            .map(Entity::getNAME).toArray(String[]::new)
            );

            ui.bindProperty(
                    binding.getRoot().getContext(),
                    binding.sixthDropDown,
                    entities.stream()
                            .filter(entity -> entity.getPERIOD().equals(Period.Sixth))
                            .map(Entity::getNAME).toArray(String[]::new)
            );

            ui.bindProperty(
                    binding.getRoot().getContext(),
                    binding.seventhDropDown,
                    entities.stream()
                            .filter(entity -> entity.getPERIOD().equals(Period.Seventh))
                            .map(Entity::getNAME).toArray(String[]::new)
            );
        }
    };

    private void computeDetails(Entity entity){
        binding.soulsTextView.setText(entity.getSOUL());
        binding.personalsTextView.setText(entity.getPERSONAL());
        binding.businessesTextView.setText(entity.getBUSINESS());
        binding.healthsTextView.setText(entity.getHEALTH());

    }

}