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
import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentHomeBinding;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.retrofit.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.app.SignInManager;
import com.inovationware.toolkit.global.library.app.retrofit.Repo;
import com.inovationware.toolkit.global.repository.ResourcesManager;
import com.inovationware.toolkit.location.service.LocationService;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.location.service.impl.LocationServiceImpl;
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
import static com.inovationware.toolkit.global.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_FAVORITE_URL_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_PINNED_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_READING_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_RUNNING_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_SCRATCH_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_TODO_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.cachedMemos;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;

import java.io.IOException;
import java.util.List;
import java.util.Random;

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

    private Feedback feedback;
    private LocationService service;
    private static final int PERMISSION_LOCATION_REQUEST = 997;

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
        service = LocationServiceImpl.getInstance(context);
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
                        DomainObjects.WELCOME + signInManager.getSignedInUser(view.getContext()).getName() :
                        DomainObjects.WELCOME);

        setInitialText(binding.toolkitInfoTextView);
        setWelcomeText(binding.toolkitInfoTextView);

        //binding.guideImageView.setImageResource(new ResourcesManager().getWelcomeImage());
        binding.guideImageView.setImageResource(new ResourcesManager().getWelcomeImage(store.getTheme(context)));

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
     * calls #updatePeriodically
     */
    private final View.OnLongClickListener guideImageViewLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            service.updateLocationPeriodically();
            Toast.makeText(context, DomainObjects.FRIENDLY_MESSAGES.get(new Random().nextInt(DomainObjects.FRIENDLY_MESSAGES.size() - 0) + 0), Toast.LENGTH_SHORT).show();
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
                DomainObjects.POST_PURPOSE_READ_NOTE
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
            binding.captionTextView.setText(DomainObjects.WELCOME + ", " + acct.getGivenName());
            /*String personName = acct.getDisplayName();
            Uri personPhoto = acct.getPhotoUrl();

            textName.setText(personName);
            //Glide.with(this).load(String.valueOf(personPhoto)).into(imagePicture);
            Glide.with(this).load(String.valueOf(personPhoto)).circleCrop().into(imagePicture);*/
        } else {
            binding.captionTextView.setText(DomainObjects.WELCOME);
            initialInfoTextView.setVisibility(initialParamsAreSet(view.getContext(), store, machines) ? View.INVISIBLE : View.VISIBLE);
        }

    }

    boolean isInternetResource(String url) {
        return url.toLowerCase().startsWith("http");
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