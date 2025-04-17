package com.inovationware.toolkit.ui.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.ActivityReplyBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.datatransfer.service.rest.RestDataTransferService;
import com.inovationware.toolkit.datatransfer.strategy.rest.RestDataTransferStrategy;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.EngagementService;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.InputDialog;
import com.inovationware.toolkit.global.library.app.retrofit.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.app.SignInManager;
import com.inovationware.toolkit.global.library.external.Json;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.StorageClient;
import com.inovationware.toolkit.global.library.app.retrofit.Repo;
import com.inovationware.toolkit.interaction.model.InteractionToken;
import com.inovationware.toolkit.ui.contract.BaseActivity;
import com.inovationware.toolkit.ui.support.EngageAuthority;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inovationware.generalmodule.Device.clipboardSetText;
import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.HIBERNATE;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.LOCK;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_ENGAGE;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_LAST_30;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_PING;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_WHAT_IS_ON;
import static com.inovationware.toolkit.global.library.utility.Code.content;
import static com.inovationware.toolkit.global.library.utility.Code.isNothing;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.global.domain.DomainObjects.TARGET_MODE_TO_DEVICE;
import static com.inovationware.toolkit.global.library.utility.Support.announce;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;

import java.io.IOException;

public class ReplyActivity extends BaseActivity {
    private GroupManager machines;
    private SharedPreferencesManager store;
    private DeviceClient device;
    private StorageClient disk;
    private Feedback feedback;
    private EditText dataTransferSharedTextView;
    private ActivityReplyBinding binding;
    private Factory factory;
    private Handler engagementHandler;

    private EngagementService engagementService;
    private EngageAuthority authority;

    private SignInManager user;
    private Context context;
    private DownloadManager downloadManager;
    private long downloadManagerReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeVariables();
        setListeners();
        setupUi();
        otherToDos();

    }

    private void initializeVariables() {
        context = ReplyActivity.this;
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        feedback = new Feedback(getApplicationContext());
        factory = Factory.getInstance();
        engagementService = EngagementService.getInstance();
        engagementHandler = new Handler();
        user = SignInManager.getInstance();
        device = DeviceClient.getInstance();
        disk = StorageClient.getInstance(context);
        authority = EngageAuthority.getInstance(context);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        //dataTransferSharedTextView = findViewById(R.id.DataTransferSharedTextView);

    }

    private void otherToDos() {
        //handle incoming intent
        if (android.content.Intent.ACTION_SEND.equals(getIntent().getAction()) && getIntent().getType() != null) {
            if ("text/plain".equals(getIntent().getType())) {
                shareText(getIntent());
            }
        }

        hideEngageControl();

    }

    private void setListeners() {
        binding.copySharedTextButton.setOnClickListener(handleCopySharedTextButton);
        binding.DataTransferSendButton.setOnClickListener(handleDataTransferSendButton);
        binding.powerButton.setOnClickListener(handlePowerButton);
        binding.pcButton.setOnClickListener(handlePcButton);
        binding.engageButton.setOnClickListener(handleEngageButton);
        binding.interactNowButton.setOnClickListener(handleInteractNowButton);
        binding.resumeButton.setOnClickListener(resumeButtonListener);
    }

    private void setupUi() {
        store.setDropDown(ReplyActivity.this, binding.powerDropDown, engagementService.listing(EngagementService.EngagementSection.Power).toArray(new String[0]), HIBERNATE);
        machines.setDropDown(ReplyActivity.this, binding.powerMachineDropDown, machines.list(context, false), machines.getDefaultDevice(context));

        store.setDropDown(ReplyActivity.this, binding.pcDropDown, engagementService.listing(EngagementService.EngagementSection.Pc).toArray(new String[0]), LOCK);
        machines.setDropDown(ReplyActivity.this, binding.pcMachineDropDown, machines.list(context, false), machines.getDefaultDevice(context));

        store.setDropDown(ReplyActivity.this, binding.engageOperationDropDown, engagementService.listing(EngagementService.EngagementSection.Engaging).toArray(new String[0]));
        machines.setDropDown(ReplyActivity.this, binding.engageMachineDropDown, machines.list(context, true), machines.getDefaultDevice(context));

        machines.setDropDown(ReplyActivity.this, binding.interactMachineDropDown, machines.list(context, false), machines.getDefaultDevice(context));
        store.setDropDown(ReplyActivity.this, binding.interactOpDropDown, InteractionToken.opListing());

        machines.setDropDown(ReplyActivity.this, binding.resumeMachineDropDown, machines.list(context, false), machines.getDefaultDevice(context));
        setResumeDropDown();
    }

    private void setResumeDropDown() {
        binding.resumeDropDown.setEnabled(false);

        if (!thereIsInternet(context) || !initialParamsAreSet(context, store, machines))
            return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.readText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(context),
                store.getID(context),
                String.valueOf(Transfer.Intent.readResumeWorkAppsListing),
                DomainObjects.EMPTY_STRING
        );
        navigate.enqueue(new Callback<String>() {
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                    if (response.body() == null) return;

                    if (DomainObjects.resumeAppsListing == null) {
                        DomainObjects.resumeAppsListing = response.body().toString().split("\n");
                    }

                    store.setDropDown(context, binding.resumeDropDown, DomainObjects.resumeAppsListing);
                    binding.resumeDropDown.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)) {
                    new Feedback(context).toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
                }
            }
        });

    }

    private final View.OnClickListener resumeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            factory.events.handlers.resumeWorkButton(context, factory, store, machines, binding.resumeDropDown.getText().toString().trim(), binding.resumeMachineDropDown.getText().toString());
        }
    };

    private final View.OnClickListener handleInteractNowButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (binding.interactOpDropDown.getText().toString().isEmpty()) return;

            if (!initialParamsAreSet(ReplyActivity.this, store, machines) || !thereIsInternet(ReplyActivity.this))
                return;

            if (binding.interactOpDropDown.getText().toString().equalsIgnoreCase(InteractionToken.PcOp.MoveApp.name()) ||
                    binding.interactOpDropDown.getText().toString().equalsIgnoreCase(InteractionToken.PcOp.ExitApp.name())) {
                InputDialog dialog = getTargetFilename();
                dialog.show();
            } else {
                InteractionToken token = InteractionToken.create(
                        InteractionToken.PcOp.valueOf(binding.interactOpDropDown.getText().toString()),
                        binding.xDropDown.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.xDropDown.getText().toString()),
                        binding.yDropDown.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.yDropDown.getText().toString()),
                        ""
                );

                try {
                    sendInteraction(Json.from(token));
                } catch (IOException ignored) {
                }
            }
        }
    };

    private final View.OnClickListener handlePowerButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Todo Also see where login is necessary in other parts of the app before doing anything
            if (!thereIsInternet(ReplyActivity.this)) return;

            if (!user.isLoggedIn(ReplyActivity.this)) {
                SignInManager.getInstance().beginLoginProcess(ReplyActivity.this, ReplyActivity.class.getSimpleName());
                return;
            }

            if (canSend()) {
                binding.powerButton.setEnabled(false);
                requestEngage(binding.powerDropDown.getText().toString(), content(binding.powerMachineDropDown));
                binding.powerButton.setEnabled(true);
            }
        }
    };

    private final View.OnClickListener handlePcButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Todo Also see where login is necessary in other parts of the app before doing anything
            if (!thereIsInternet(ReplyActivity.this)) return;

            if (!user.isLoggedIn(ReplyActivity.this)) {
                SignInManager.getInstance().beginLoginProcess(ReplyActivity.this, ReplyActivity.class.getSimpleName());
                return;
            }

            if (canSend()) {
                binding.pcButton.setEnabled(false);
                requestEngage(binding.pcDropDown.getText().toString(), content(binding.pcMachineDropDown));
                binding.pcButton.setEnabled(true);
            }
        }
    };

    private final View.OnClickListener handleEngageButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Todo Also see where login is necessary in other parts of the app before doing anything
            if (!thereIsInternet(ReplyActivity.this)) return;

            if (!user.isLoggedIn(ReplyActivity.this)) {
                SignInManager.getInstance().beginLoginProcess(ReplyActivity.this, ReplyActivity.class.getSimpleName());
                return;
            }

            /*Last_30Ex
            if (canSend() && binding.engageOperationDropDown.getText().toString().equalsIgnoreCase(EngagementService.Engagement.Last_30.name().replace("_", " "))) {
                factory.image.service.loadPlaceholder(context, binding.engageImageView);
                binding.engageButton.setEnabled(false);
                requestLast30(binding.engageOperationDropDown.getText().toString(), content(binding.engageMachineDropDown));
                //Todo when checking ifThereIsInternet, also always check if canSend()
                return;
            }*/

           /* Last_30
            if (canSend() && EngagementService.Engagement.fromCanonicalString(binding.engageOperationDropDown.getText().toString()) == EngagementService.Engagement.Last_30) {
                binding.engageButton.setEnabled(false);
                factory.image.service.loadPlaceholder(ReplyActivity.this, binding.engageImageView);
                requestLast30(binding.engageOperationDropDown.getText().toString(), content(binding.engageMachineDropDown));
                //engagementHandler.post(readLast30);
                return;
            }*/

            /*Who_Was
            if (canSend() && binding.engageOperationDropDown.getText().toString().equalsIgnoreCase(EngagementService.Engagement.Who_Was.name().replace("_", " "))) {
                hideEngageControl();
                binding.engageButton.setEnabled(false);
                //ToDo
                //getTimestamp();
                factory.image.service.loadGifImage(ReplyActivity.this, binding.engageImageView, R.drawable.placeholder);
                factory.image.service.loadStaticImage(ReplyActivity.this, binding.engageImageView, authority.getEngagementUrl(), R.mipmap.ic_launcher, R.drawable.baseline_question_mark_24, R.drawable.baseline_question_mark_24);
                binding.engageButton.setEnabled(true);
                showEngageControl();
                return;
            }*/

            /*if (canSend() && binding.engageOperationDropDown.getText().toString().equalsIgnoreCase(EngagementService.Engagement.Ping.name().replace("_", " "))) {
                hideEngageControl();
                requestPing(binding.engageOperationDropDown.getText().toString(), content(binding.engageMachineDropDown));
                binding.engageButton.setEnabled(false);
                factory.image.service.loadPlaceholder(ReplyActivity.this, binding.engageImageView);
                showEngageControl();
                engagementHandler.postDelayed(readPingHandler, 20000);
                return;
            }*/

            //What_Is_On
            if (canSend() && EngagementService.Engagement.fromCanonicalString(binding.engageOperationDropDown.getText().toString()) == EngagementService.Engagement.What_Is_On) {
                hideEngageControl();
                requestWhatIsOn(binding.engageOperationDropDown.getText().toString(), content(binding.engageMachineDropDown));
                binding.engageButton.setEnabled(false);
                factory.image.service.loadPlaceholder(ReplyActivity.this, binding.engageImageView);
                showEngageControl();
                engagementHandler.postDelayed(readWhatIsOnRunnable, 30000);
                return;
            }

            /*if (canSend() && EngagementService.Engagement.fromCanonicalString(binding.engageOperationDropDown.getText().toString()) == EngagementService.Engagement.Dim_Screen){
                doSendForDataTransfer(POST_PURPOSE_DIM_SCREEN, determineMeta(context, store));
                return;
            }*/

            //Every other
            if (canSend()) {
                hideEngageControl();
                requestEngage(binding.engageOperationDropDown.getText().toString(), content(binding.engageMachineDropDown));
                binding.engageButton.setEnabled(false);
                factory.image.service.loadGifImage(ReplyActivity.this, binding.engageImageView, R.drawable.placeholder);
                showEngageControl();
                if (engagementService.from(EngagementService.Engagement.fromCanonicalString(binding.engageOperationDropDown.getText().toString())) == EngagementService.EngagementResponseType.isRequest) {
                    //ToDo incorporate others in EngagementResponseType
                    //getTimestamp();
                    engagementHandler.postDelayed(readWhoIsRunnable, 30000);
                    return;
                } else if (engagementService.from(EngagementService.Engagement.fromCanonicalString(binding.engageOperationDropDown.getText().toString())) == EngagementService.EngagementResponseType.isPing) {
                    requestPing(DomainObjects.IGNORE, determineTarget(context, store, machines));
                    return;
                }
                hideEngageControl();
                binding.engageButton.setEnabled(true);

            }
        }
    };

    private final Runnable readPingRunnable = new Runnable() {
        @Override
        public void run() {

            Retrofit retrofitImpl = Repo.getInstance().create(context, store);

            Call<String> navigate = retrofitImpl.readText(
                    authority.readPingUrl(),
                    store.getUsername(context),
                    store.getID(context),
                    String.valueOf(Transfer.Intent.readPing),
                    DomainObjects.EMPTY_STRING
            );
            navigate.enqueue(new Callback<String>() {
                @SneakyThrows
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    hideEngageControl();
                    binding.engageButton.setEnabled(true);

                    if (response.isSuccessful()) {

                        binding.engageButton.setEnabled(true);

                        if (response.body() == null) return;
                        //Todo check if this is working
                        factory.feedback.service.giveFeedback(context, store, response.body(), true, Toast.LENGTH_LONG);
                    } else {
                        if (SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)) {
                            new Feedback(context).toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)) {
                        new Feedback(context).toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
                    }
                }
            });

        }
    };

    private final View.OnClickListener handleCopySharedTextButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isNothing(dataTransferSharedTextView.getText().toString()))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    clipboardSetText(getApplicationContext(), dataTransferSharedTextView.getText().toString());
                }

        }
    };

    private final View.OnClickListener handleDataTransferSendButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (initialParamsAreSet(ReplyActivity.this, store, machines) && !isNothing(content(dataTransferSharedTextView)))
                doSendForDataTransfer(POST_PURPOSE_REGULAR, determineMeta(ReplyActivity.this, store));
        }
    };

    private void hideEngageControl() {
        binding.engageImageView.setVisibility(View.INVISIBLE);
    }

    private void showEngageControl() {
        binding.engageImageView.setVisibility(View.VISIBLE);
    }

    void shareText(Intent intent) {
        dataTransferSharedTextView.setText(intent.getStringExtra(android.content.Intent.EXTRA_TEXT));
    }

    boolean canSend() {
        return initialParamsAreSet(ReplyActivity.this, store, machines);
    }

    void requestWhatIsOn(String info, String target) {
        if (!thereIsInternet(getApplicationContext())) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.sendText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(ReplyActivity.this),
                store.getID(ReplyActivity.this),
                String.valueOf(Transfer.Intent.writeText),
                store.getSender(ReplyActivity.this),
                target,
                POST_PURPOSE_WHAT_IS_ON,
                TARGET_MODE_TO_DEVICE,
                info,
                DomainObjects.EMPTY_STRING);

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (!response.body().isEmpty())
                            new Feedback(ReplyActivity.this).toast(response.body());
                } else {
                    if (!store.shouldDisplayErrorMessage(ReplyActivity.this)) {
                        return;
                    }
                    feedback.toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (!store.shouldDisplayErrorMessage(ReplyActivity.this)) {
                    return;
                }
                feedback.toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
            }
        });
    }

    void requestPing(String info, String target) {
        if (!thereIsInternet(getApplicationContext())) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.sendText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(ReplyActivity.this),
                store.getID(ReplyActivity.this),
                String.valueOf(Transfer.Intent.writeText),
                store.getSender(ReplyActivity.this),
                target,
                POST_PURPOSE_PING,
                TARGET_MODE_TO_DEVICE,
                info,
                DomainObjects.EMPTY_STRING);

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) return;

                    engagementHandler.postDelayed(readPingRunnable, 30000);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (!store.shouldDisplayErrorMessage(ReplyActivity.this)) {
                    return;
                }
                feedback.toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
            }
        });
    }

    void requestEngage(String engagementOperation, String target) {
        if (!thereIsInternet(getApplicationContext())) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.sendText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(ReplyActivity.this),
                store.getID(ReplyActivity.this),
                String.valueOf(Transfer.Intent.writeText),
                store.getSender(ReplyActivity.this),
                target,
                POST_PURPOSE_ENGAGE,
                engagementService.from(EngagementService.Engagement.fromCanonicalString(engagementOperation)).toString(), //TARGET_MODE_TO_DEVICE,
                engagementOperation,
                DomainObjects.EMPTY_STRING);

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (!response.body().isEmpty())
                            new Feedback(ReplyActivity.this).toast(response.body());
                } else {
                    if (!store.shouldDisplayErrorMessage(ReplyActivity.this)) {
                        return;
                    }
                    feedback.toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (!store.shouldDisplayErrorMessage(ReplyActivity.this)) {
                    return;
                }
                feedback.toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
            }
        });
    }

    void requestLast30(String info, String target) {
        if (!thereIsInternet(getApplicationContext())) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.sendText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(ReplyActivity.this),
                store.getID(ReplyActivity.this),
                String.valueOf(Transfer.Intent.writeText),
                store.getSender(ReplyActivity.this),
                target,
                POST_PURPOSE_LAST_30,
                TARGET_MODE_TO_DEVICE,
                info,
                DomainObjects.EMPTY_STRING);

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    engagementHandler.postDelayed(readLast30, 45000);
                }
                if (response.body() != null)
                    if (!response.body().isEmpty()) {
                        new Feedback(ReplyActivity.this).toast(response.body());
                    } else {
                        if (!store.shouldDisplayErrorMessage(ReplyActivity.this)) {
                            return;
                        }
                        feedback.toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                    }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (!store.shouldDisplayErrorMessage(ReplyActivity.this)) {
                    return;
                }
                feedback.toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
            }
        });
    }

    void doSendForDataTransfer(String purpose, String meta) {
        if (!thereIsInternet(getApplicationContext())) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.sendText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(ReplyActivity.this),
                store.getID(ReplyActivity.this),
                String.valueOf(Transfer.Intent.writeText),
                store.getSender(ReplyActivity.this),
                determineTarget(ReplyActivity.this, store, machines),
                purpose,
                meta,
                content(dataTransferSharedTextView),
                DomainObjects.EMPTY_STRING);

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (!response.body().isEmpty())
                            new Feedback(ReplyActivity.this).toast(response.body());
                    finish();
                } else {
                    if (!store.shouldDisplayErrorMessage(ReplyActivity.this)) {
                        return;
                    }
                    feedback.toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (!store.shouldDisplayErrorMessage(ReplyActivity.this)) {
                    return;
                }
                feedback.toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
            }
        });
    }

    private final Runnable readWhatIsOnRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            factory.image.service.loadStaticImage(ReplyActivity.this, binding.engageImageView, authority.getWhatIsOnUrl(), R.mipmap.ic_launcher, R.drawable.baseline_question_mark_24, R.drawable.baseline_question_mark_24);
            binding.engageButton.setEnabled(true);
        }
    };

    private final Runnable readWhoIsRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            factory.image.service.loadStaticImage(ReplyActivity.this, binding.engageImageView, authority.getEngagementUrl(), R.mipmap.ic_launcher, R.drawable.baseline_question_mark_24, R.drawable.baseline_question_mark_24);
            binding.engageButton.setEnabled(true);
        }
    };

    private final Runnable readLast30 = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            Uri uri = Uri.parse(authority.getLast30Url());
            DownloadManager.Request request = new DownloadManager.Request(uri);

            // Set the destination directory and filename
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, authority.createFilenameForDownloadedAudio());

            // Set the title and description for the download notification
            request.setTitle("Downloading file...");
            request.setDescription("Downloading file from server...");

            // Set the visibility of the download notification
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

            // Enqueue the download request
            downloadManagerReference = downloadManager.enqueue(request);
            engagementHandler.postDelayed(receiveLast30, 15000);
        }
    };

    Runnable receiveLast30 = new Runnable() {
        @Override
        public void run() {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadManagerReference);
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadManagerReference);

                    // Create an intent to view the file
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, downloadManager.getMimeTypeForDownloadedFile(downloadManagerReference));

                    binding.engageButton.setEnabled(true);
                    binding.engageImageView.setVisibility(View.INVISIBLE);

                    // Start the activity
                    startActivity(intent);
                } else {
                    engagementHandler.postDelayed(this, 5000);
                }
            }
            cursor.close();
        }
    };


    private String getLast30Url() {
        return DomainObjects.BASE_URL(context, store) + "/api/regular.ashx?id=" + store.getID(ReplyActivity.this) + "&intent=" + Transfer.Intent.readLast30 + "&username=" + store.getUsername(ReplyActivity.this);
    }

    private final InputDialog getTargetFilename() {
        return new InputDialog(ReplyActivity.this, "", "Full path or filename on\nthe target device", "e.g.  file.txt") {
            @Override
            public void positiveButtonAction() throws IOException {
                InteractionToken token = InteractionToken.create(
                        InteractionToken.PcOp.valueOf(binding.interactOpDropDown.getText().toString()),
                        binding.xDropDown.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.xDropDown.getText().toString()),
                        binding.yDropDown.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.yDropDown.getText().toString()),
                        this.getText()
                );

                sendInteraction(Json.from(token));
            }

            @Override
            public void negativeButtonAction() {

            }
        };

    }

    private void sendInteraction(String text) {

        RestDataTransferService.getInstance(RestDataTransferStrategy.getInstance()).sendText(
                ReplyActivity.this,
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                        SharedPreferencesManager.getInstance().getUsername(ReplyActivity.this),
                        SharedPreferencesManager.getInstance().getID(ReplyActivity.this),
                        Transfer.Intent.writeText,
                        SharedPreferencesManager.getInstance().getSender(ReplyActivity.this),
                        determineTarget(ReplyActivity.this, SharedPreferencesManager.getInstance(), GroupManager.getInstance()),
                        DomainObjects.POST_PURPOSE_INTERACTION,
                        determineMeta(ReplyActivity.this, SharedPreferencesManager.getInstance()),
                        EncryptionManager.getInstance().encrypt(ReplyActivity.this, SharedPreferencesManager.getInstance(), text),
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX
        );
    }

}