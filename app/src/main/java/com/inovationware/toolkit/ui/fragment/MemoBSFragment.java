package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.clipboardSetText;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_APP;
import static com.inovationware.toolkit.common.utility.Support.determineMeta;
import static com.inovationware.toolkit.common.utility.Support.determineTarget;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.databinding.FragmentMemoBSBinding;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.infrastructure.retrofit.Retrofit;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.DeviceClient;
import com.inovationware.toolkit.common.infrastructure.retrofit.Repo;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.ui.adapter.MemoRecyclerViewAdapter;

import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemoBSFragment extends BottomSheetDialogFragment {
    public static final String TAG = "MemoBSFragment";
    private FragmentMemoBSBinding binding;

    @Setter
    private Memo memo;

    @Setter
    private DeviceClient device;

    @Setter
    private SharedPreferencesManager store;

    @Setter
    private GroupManager machines;

    @Setter
    MemoRecyclerViewAdapter memoRecyclerViewAdapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_memo_b_s, container, false);

        binding = FragmentMemoBSBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupUi();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setupUi() {
        binding.shareMemoButton.setOnClickListener(handleShareButton);
        binding.shareMemoCaption.setOnClickListener(handleShareButton);
        binding.deleteMemoButton.setOnClickListener(handleDeleteButton);
        binding.deleteMemoCaption.setOnClickListener(handleDeleteButton);
        binding.copyMemoButton.setOnClickListener(handleCopy);
        binding.copyMemoCaption.setOnClickListener(handleCopy);

        if (memo != null) {
            binding.detailCaption.setText(
                    "Note in " + memo.getNoteTitle() + "\ncreated on " + memo.getNoteDate() +
                            "\nat " + memo.getNoteTime().toLowerCase() // + "\nfrom " + memo.getTimezoneInfo()
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    View.OnClickListener handleShareButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (memo == null || device == null) return;

            String message = memo.getPostnote() +
                    "\n\nFrom " + memo.getSender() +
                    "\n\nTo " + memo.getTarget() +
                    "\n\nId: " + memo.getNoteId() +
                    "\n\nTitle: " + memo.getNoteTitle() +
                    "\n\nCreated " + memo.getNoteDate() + ", " + memo.getNoteTime();

            device.shareText(view.getContext(), message);

        }
    };

    View.OnClickListener handleDeleteButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Retrofit retrofitImpl = Repo.getInstance().create(view.getContext(), store);

            Call<String> navigate = retrofitImpl.deleteNote(
                    HTTP_TRANSFER_URL(view.getContext(), store),
                    store.getUsername(view.getContext()),
                    store.getID(view.getContext()),
                    String.valueOf(Transfer.Intent.deleteNote),
                    store.getSender(view.getContext()),
                    determineTarget(view.getContext(), store, machines),
                    POST_PURPOSE_APP,
                    determineMeta(view.getContext(), store),
                    DomainObjects.EMPTY_STRING,
                    memo.getNoteId());
            navigate.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        resetRecyclerView();
                        new Feedback(view.getContext()).toast(response.body());
                        //dismiss
                        dismiss();
                    } else {
                        if (!store.shouldDisplayErrorMessage(view.getContext())) {
                            return;
                        }
                        new Feedback(view.getContext()).toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (!store.shouldDisplayErrorMessage(view.getContext())) {
                        return;
                    }
                    new Feedback(view.getContext()).toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
                }
            });
        }
    };

    View.OnClickListener handleCopy = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                clipboardSetText(view.getContext(), memo.getPostnote());
                dismiss();
            }
        }
    };

    private void resetRecyclerView() {
        memoRecyclerViewAdapter.memos.remove(memo);
        memoRecyclerViewAdapter.recyclerView.setVisibility(View.INVISIBLE);
        memoRecyclerViewAdapter.recyclerView.setLayoutManager(new LinearLayoutManager(memoRecyclerViewAdapter.context));
        memoRecyclerViewAdapter.recyclerView.setAdapter(memoRecyclerViewAdapter);
        memoRecyclerViewAdapter.recyclerView.setVisibility(View.VISIBLE);
    }
}