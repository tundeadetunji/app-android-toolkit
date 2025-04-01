package com.inovationware.toolkit.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.databinding.FragmentLinkBinding;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.library.app.retrofit.Retrofit;
import com.inovationware.toolkit.global.library.app.retrofit.Repo;
import com.inovationware.toolkit.ui.adapter.LinkRecyclerViewAdapter;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.apps;
import static com.inovationware.toolkit.global.library.utility.Code.stringToList;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;
import static com.inovationware.toolkit.global.library.utility.Support.responseStringIsValid;

public class LinkFragment extends Fragment {
    private SharedPreferencesManager store;
    private View view;
    private FragmentLinkBinding binding;
    private Handler getAppsListRoutineHandler;
    private Feedback feedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLinkBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupAccess();
        setupReferences();
        setupUi();

        return view;
    }

    private void setupAccess(){
        feedback = new Feedback(view.getContext());
        store = SharedPreferencesManager.getInstance();
    }

    private void setupReferences(){
        getAppsListRoutineHandler = new Handler();
    }

    private void setupUi(){
        setupProgressBar();

        if (!initialParamsAreSet(view.getContext(), store, GroupManager.getInstance())) return;

        if (apps == null) {
            if (store.getString(view.getContext(), SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY, EMPTY_STRING).trim().isEmpty() && thereIsInternet(view.getContext())) {
                showProgressBar();
                hideRecyclerView();
                getAppsListRoutineHandler.post(getAppsListRoutine);
            }
        } else {
            apps = stringToList(store.getString(view.getContext(), SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY, EMPTY_STRING).trim());
            hideProgressBar();
            setupRecyclerView();
            showRecyclerView();
        }
    }

    Runnable getAppsListRoutine = new Runnable() {
        @Override
        public void run() {

            Retrofit retrofitImpl = Repo.getInstance().create(view.getContext(), store);
            Call<String> navigate = retrofitImpl.readText(
                    HTTP_TRANSFER_URL(view.getContext(), store),
                    store.getUsername(view.getContext()),
                    store.getID(view.getContext()),
                    String.valueOf(Transfer.Intent.readApps),
                    DomainObjects.EMPTY_STRING);
            navigate.enqueue(new Callback<String>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        if (response.body() == null) return;

                        if (response.body().trim().isEmpty()) {
                            feedback.toast("There aren't any applications at the moment, you can try again after a while.", Toast.LENGTH_LONG);
                            return;
                        }
                        if (responseStringIsValid(response.body())) {
                            store.setString(view.getContext(), SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY, response.body().replace(store.getSender(view.getContext()), "").trim());
                            try {
                                apps = stringToList(response.body());
                                hideProgressBar();
                                setupRecyclerView();
                                showRecyclerView();
                            } catch (Exception ignored) {
                                if (!store.shouldDisplayErrorMessage(view.getContext())) {
                                    return;
                                }
                                feedback.toast("The information received appear to be unusable, you can try again after a while.", Toast.LENGTH_LONG);
                            }
                        } else {
                            feedback.toast("The information received appear to be unusable, you can try again after a while.", Toast.LENGTH_LONG);
                        }
                    } else {
                        if (store.shouldDisplayErrorMessage(view.getContext())) {
                            feedback.toast("Couldn't retrieve list of applications, will try again.", Toast.LENGTH_LONG);
                        }
                        getAppsListRoutineHandler.postDelayed(getAppsListRoutine, 10000);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (!store.shouldDisplayErrorMessage(view.getContext())) {
                        return;
                    }
                    feedback.toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
                }
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView(){
        binding.linkRecyclerView.setAdapter(new LinkRecyclerViewAdapter(view.getContext(), apps));
        binding.linkRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

    }

    private void showRecyclerView(){
        binding.linkRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideRecyclerView(){
        binding.linkRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void setupProgressBar(){
        binding.remoteLinkProgressBar.setIndeterminate(true);
    }

    private void showProgressBar(){
        binding.remoteLinkProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        binding.remoteLinkProgressBar.setVisibility(View.INVISIBLE);
    }

}