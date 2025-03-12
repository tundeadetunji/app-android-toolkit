package com.inovationware.toolkit.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentHomeBinding;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.app.SignInManager;
import com.inovationware.toolkit.global.repository.Repo;
import com.inovationware.toolkit.tracking.service.LocationService;
import com.inovationware.toolkit.tracking.service.impl.LocationServiceImpl;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.ui.activity.BoardActivity;
import com.inovationware.toolkit.ui.activity.CodeActivity;
import com.inovationware.toolkit.ui.activity.CyclesActivity;
import com.inovationware.toolkit.ui.activity.CyclesDayViewActivity;
import com.inovationware.toolkit.ui.activity.EspActivity;
import com.inovationware.toolkit.ui.activity.NetTimerActivity;
import com.inovationware.toolkit.ui.activity.ReplyActivity;
import com.inovationware.toolkit.ui.activity.ScheduleActivity;
import com.inovationware.toolkit.ui.activity.SettingsActivity;
import com.inovationware.toolkit.ui.adapter.MemoRecyclerViewAdapter;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.Strings.EMPTY_STRING;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_FAVORITE_URL_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_PINNED_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_READING_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_RUNNING_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_SCRATCH_KEY;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_TODO_KEY;
import static com.inovationware.toolkit.global.domain.Strings.cachedMemos;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private Context context;
    private SharedPreferencesManager store;
    private GroupManager machines;
    private View view;
    private Factory factory;

    private Handler readNotesHandler;

    private FragmentHomeBinding binding;
    private RecyclerView memoRecyclerView;
    private FusedLocationProviderClient client;

    private Feedback feedback;
    private LocationService loc;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 12;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        memoRecyclerView = view.findViewById(R.id.memoRecyclerView);
        setupVariables();
        setupListeners();
        setupUi();

        return view;
    }

    private void setupVariables() {
        context = view.getContext();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        feedback = new Feedback(view.getContext());
        factory = Factory.getInstance();
        readNotesHandler = new Handler();
        loc = LocationServiceImpl.getInstance(view.getContext(), getActivity());
    }

    private void setupListeners() {
        binding.pinnedButton.setOnClickListener(pinnedButton);
        binding.readingLinkButton.setOnClickListener(readingLinkButton);
        binding.todoLinkButton.setOnClickListener(todoLinkButton);
        binding.runningLinkButton.setOnClickListener(runningLinkButton);
        binding.scratchLinkButton.setOnClickListener(scratchLinkButton);
        binding.extraLinkButton.setOnClickListener(extraLinkButton);
        binding.captionTextView.setOnClickListener(captionTextView);
        binding.linksLabel.setOnClickListener(linksLabel);
        binding.toolkitInfoTextView.setOnClickListener(initialInfoTextViewClick);
        binding.PCButton.setOnClickListener(PCButtonClick);
        binding.EspButton.setOnClickListener(EspButtonClick);
        binding.codeButton.setOnClickListener(codeButtonClick);
        binding.tasksButton.setOnClickListener(tasksButtonClick);
        binding.schedulerButton.setOnClickListener(schedulerButtonClick);
        binding.meetingButton.setOnClickListener(meetingButtonClick);
        binding.guideImageView.setOnClickListener(guideImageViewClick);
        binding.guideImageView.setOnLongClickListener(guideImageViewLongClick);
    }

    private void setupUi() {
        SignInManager signInManager = SignInManager.getInstance();
        binding.captionTextView.setText(
                signInManager.thereIsPrincipal(view.getContext()) ?
                        Strings.WELCOME + signInManager.getSignedInUser(view.getContext()).getName() :
                        Strings.WELCOME);

        setInitialText(binding.toolkitInfoTextView);
        setWelcomeText(binding.toolkitInfoTextView);

        setupNotes(view);
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
            SignInManager.getInstance().beginLoginProcess(context, BoardActivity.class.getSimpleName());
        }
    };

    private final View.OnClickListener guideImageViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, CyclesActivity.class));
        }
    };

    /**
        LongClick should gps.startLocationUpdates()
     */
    private final View.OnLongClickListener guideImageViewLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request permissions
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Start location updates
                loc.startLocationUpdates(true);
            }
            //LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            /*if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                return true;
            }

            client.getLastLocation().addOnSuccessListener(
                    getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Toast.makeText(context, String.valueOf(location == null), Toast.LENGTH_SHORT).show();
                        }
                    }
            );*/

            return true;
        }
    };


    private final View.OnClickListener linksLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, ReplyActivity.class));
        }
    };

    private final View.OnClickListener initialInfoTextViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(context, SettingsActivity.class));
        }
    };

    private final View.OnClickListener pinnedButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(view.getContext(), SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(view.getContext(), SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING))) {
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(view.getContext(), SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING))));
                } else {
                    if (view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING)) != null) {
                        startActivity(view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(view.getContext())) {
                    return;
                }
                feedback.toast("resulted in an error, url may not be valid.");
            }

        }
    };

    private final View.OnClickListener readingLinkButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(view.getContext(), SHARED_PREFERENCES_READING_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(view.getContext(), SHARED_PREFERENCES_READING_KEY, EMPTY_STRING))) {
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(view.getContext(), SHARED_PREFERENCES_READING_KEY, EMPTY_STRING))));
                } else {
                    if (view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_READING_KEY, EMPTY_STRING)) != null) {
                        startActivity(view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_READING_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(view.getContext())) {
                    return;
                }
                feedback.toast("resulted in an error, url may not be valid.");
            }
        }
    };

    private final View.OnClickListener todoLinkButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(view.getContext(), SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(view.getContext(), SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING))) {
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(view.getContext(), SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING))));
                } else {
                    if (view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING)) != null) {
                        startActivity(view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(view.getContext())) {
                    return;
                }
                feedback.toast("resulted in an error, url may not be valid.");
            }
        }
    };

    private final View.OnClickListener runningLinkButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(view.getContext(), SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(view.getContext(), SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING))) {
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(view.getContext(), SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING))));
                } else {
                    if (view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING)) != null) {
                        startActivity(view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(view.getContext())) {
                    return;
                }
                feedback.toast("resulted in an error, url may not be valid.");
            }
        }
    };

    private final View.OnClickListener scratchLinkButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(view.getContext(), SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(view.getContext(), SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING))) {
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(view.getContext(), SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING))));
                } else {
                    if (view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING)) != null) {
                        startActivity(view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(view.getContext())) {
                    return;
                }
                feedback.toast("resulted in an error, url may not be valid.");
            }
        }
    };

    private final View.OnClickListener extraLinkButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(view.getContext(), SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(view.getContext(), SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING))) {
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(view.getContext(), SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING))));
                } else {
                    if (view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING)) != null) {
                        startActivity(view.getContext().getPackageManager().getLaunchIntentForPackage(store.getString(view.getContext(), SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(view.getContext())) {
                    return;
                }
                feedback.toast("resulted in an error, url may not be valid.");
            }
        }
    };

    private final View.OnClickListener captionTextView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(view.getContext(), CyclesDayViewActivity.class));
        }
    };

    public void setupNotes(View view) {
        memoRecyclerView.setVisibility(View.GONE);

        if (cachedMemos != null) {
            memoRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            memoRecyclerView.setAdapter(new MemoRecyclerViewAdapter(view.getContext(), cachedMemos, getFragmentManager(), memoRecyclerView, store, machines));
            memoRecyclerView.setVisibility(View.VISIBLE);
            return;
        }

        readNotesHandler.post(new Runnable() {
            @Override
            public void run() {
                readNotes(view.getContext());
            }
        });
    }

    void readNotes(Context context) {
        if (!thereIsInternet(context) || !initialParamsAreSet(context, store, machines)) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.readNote(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(context),
                store.getID(context),
                String.valueOf(Transfer.Intent.readNote),
                Strings.POST_PURPOSE_READ_NOTE
        );

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    List<Memo> memos;
                    try {
                        memos = Memo.listing(response);
                        cachedMemos = memos;
                    } catch (IOException ignored) {
                        return;
                    }

                    if (memos == null) return;
                    if (memos.isEmpty()) return;

                    memoRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    memoRecyclerView.setAdapter(new MemoRecyclerViewAdapter(view.getContext(), memos, getFragmentManager(), memoRecyclerView, store, machines));
                    memoRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try{

        }catch (Exception ignored){
            loc.stopLocationUpdates();
        }
        binding = null;
    }

    void setInitialText(TextView initialInfoTextView) {
        if (initialParamsAreSet(view.getContext(), store, machines)) {
            return;
        }

        initialInfoTextView.setText(
                "Hotspot information must be present.\n" +
                        "\nAnd you need to set the id and username," +
                        "\nand have at least one target device (while" +
                        "\none of them is set as default)." +
                        "\n\nYou also need to set the password and " +
                        "\nsalt for encyption." +
                        "\n\nYou can get relevant info from the PC" +
                        "\nby clicking Info on the tray icon." +
                        "\n\nNote that you have to enter password" +
                        "\nand salt into your target devices manually." +
                        "\n\nTap this message to go to Settings.");

        initialInfoTextView.setVisibility(View.VISIBLE);
    }

    private void setWelcomeText(TextView initialInfoTextView) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(view.getContext());

        if (acct != null) {
            binding.captionTextView.setText(Strings.WELCOME + ", " + acct.getGivenName());
            /*String personName = acct.getDisplayName();
            Uri personPhoto = acct.getPhotoUrl();

            textName.setText(personName);
            //Glide.with(this).load(String.valueOf(personPhoto)).into(imagePicture);
            Glide.with(this).load(String.valueOf(personPhoto)).circleCrop().into(imagePicture);*/
        } else {
            binding.captionTextView.setText(Strings.WELCOME);
            initialInfoTextView.setVisibility(initialParamsAreSet(view.getContext(), store, machines) ? View.INVISIBLE : View.VISIBLE);
        }

    }

    boolean isInternetResource(String url) {
        return url.toLowerCase().startsWith("http");
    }
    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, proceed to get location
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                loc.startLocationUpdates();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(getContext(), "Location permission is required to access GPS.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLocationPermissions();
    }
}