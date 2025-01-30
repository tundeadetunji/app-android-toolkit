package com.inovationware.toolkit.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.library.app.Retrofit;
import com.inovationware.toolkit.global.repository.Repo;
import com.inovationware.toolkit.ui.adapter.LinkRecyclerViewAdapter;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.EMPTY_STRING;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY;
import static com.inovationware.toolkit.global.domain.Strings.apps;
import static com.inovationware.toolkit.global.library.utility.Code.stringToList;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;
import static com.inovationware.toolkit.global.library.utility.Support.responseStringIsValid;

public class LinkFragment extends Fragment {
    private SharedPreferencesManager store;

    private View view;

    private RecyclerView linkRecyclerView;
    private ProgressBar remoteLinkProgressBar;
    private Handler getAppsListRoutineHandler;

    private Feedback feedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_link, container, false);

        linkRecyclerView = view.findViewById(R.id.linkRecyclerView);
        remoteLinkProgressBar = view.findViewById(R.id.remoteLinkProgressBar);
        remoteLinkProgressBar.setIndeterminate(true);
        feedback = new Feedback(view.getContext());
        store = SharedPreferencesManager.getInstance();

        getAppsListRoutineHandler = new Handler();
        if (!initialParamsAreSet(view.getContext(), store, GroupManager.getInstance())) {
            return view;
        }

        if (apps == null) {
            if (store.getString(view.getContext(), SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY, EMPTY_STRING).trim().isEmpty() && thereIsInternet(view.getContext())) {
                remoteLinkProgressBar.setVisibility(View.VISIBLE);
                linkRecyclerView.setVisibility(View.INVISIBLE);
                getAppsListRoutineHandler.post(getAppsListRoutine);
            }
        } else {
            apps = stringToList(store.getString(view.getContext(), SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY, EMPTY_STRING).trim());
            remoteLinkProgressBar.setVisibility(View.INVISIBLE);
            linkRecyclerView.setAdapter(new LinkRecyclerViewAdapter(view.getContext(), apps));
            linkRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            linkRecyclerView.setVisibility(View.VISIBLE);
        }

        return view;
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
                    Strings.EMPTY_STRING);
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
                                /*if (!responseStringIsValid(response.body())){
                                    return;
                                }*/
                                apps = stringToList(response.body());
                                remoteLinkProgressBar.setVisibility(View.INVISIBLE);
                                linkRecyclerView.setAdapter(new LinkRecyclerViewAdapter(view.getContext(), apps));
                                linkRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                linkRecyclerView.setVisibility(View.VISIBLE);
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

}