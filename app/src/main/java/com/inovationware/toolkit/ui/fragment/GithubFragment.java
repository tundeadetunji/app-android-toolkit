package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentGithubBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.datatransfer.service.DataTransferService;
import com.inovationware.toolkit.datatransfer.service.rest.RestDataTransferService;
import com.inovationware.toolkit.datatransfer.strategy.rest.RestDataTransferStrategy;
import com.inovationware.toolkit.github.domain.app.BooleanValue;
import com.inovationware.toolkit.github.domain.app.GithubOperation;
import com.inovationware.toolkit.github.utility.Resolver;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.external.GitHubLite;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.StorageClient;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.global.library.utility.Ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GithubFragment extends Fragment {

    private View view;
    private FragmentGithubBinding binding;
    private Resolver resolver;
    private SharedPreferencesManager store;
    private DataTransferService service;
    private GroupManager machines;
    private DeviceClient device;
    private StorageClient storage;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGithubBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        setReferences();
        setListeners();
        setupUi(Ui.getInstance());
        return view;
    }

    private void setReferences() {
        context = view.getContext();
        resolver = Resolver.getInstance();
        store = SharedPreferencesManager.getInstance();
        service = RestDataTransferService.getInstance(RestDataTransferStrategy.getInstance());
        machines = GroupManager.getInstance();
        device = DeviceClient.getInstance();
        storage = StorageClient.getInstance(context);
    }

    private Context getCurrentContext() {
        return context;
    }

    private void setListeners(){
        binding.presetParamsButton.setOnClickListener(handlePresetParamsButton);
        binding.executeGitHubOperationButton.setOnClickListener(handleExecuteGitHubOperationButton);
        binding.githubRepositoryTextView.setText(store.getGithubDefaultRepository(context));
        binding.githubBranchTextView.setText(store.getGithubDefaultBranch(context));
        binding.sendGithubResultButton.setOnClickListener(handleSendGithubResultButton);
        binding.shareGithubResultButton.setOnClickListener(shareGithubResultButtonListener);
        binding.copyGithubResultButton.setOnClickListener(handleCopyGithubResultButton);
        //binding.saveGithubResultButton.setOnClickListener(handleSaveGithubResultButton);

    }
    private void setupUi(Ui ui) {
        ui.bindProperty(getCurrentContext(), binding.githubOperationsDropDown, GithubOperation.listing());
        ui.bindProperty(getCurrentContext(), binding.booleanDropDown, BooleanValue.listing());

    }

    private void clearFields() {
        binding.param1TextView.setText(DomainObjects.EMPTY_STRING);
        binding.param1TextView.setHint(DomainObjects.EMPTY_STRING);

        binding.param2TextView.setText(DomainObjects.EMPTY_STRING);
        binding.param2TextView.setHint(DomainObjects.EMPTY_STRING);

        binding.param3TextView.setText(DomainObjects.EMPTY_STRING);
        binding.param3TextView.setHint(DomainObjects.EMPTY_STRING);

        binding.booleanDropDown.setText(DomainObjects.EMPTY_STRING);
        binding.booleanDropDown.setHint(DomainObjects.EMPTY_STRING);

        binding.githubResultTextView.setText(DomainObjects.EMPTY_STRING);
        binding.githubResultTextView.setHint(DomainObjects.EMPTY_STRING);
    }


    View.OnClickListener handlePresetParamsButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearFields();
            Resolver.Hints hints = resolver.resolveHints(GithubOperation.from(binding.githubOperationsDropDown.getText().toString()));
            binding.param1TextView.setHint(hints.getStringParam1Hint());
            binding.param2TextView.setHint(hints.getStringParam2Hint());
            binding.param3TextView.setHint(hints.getStringParam3Hint());
            binding.booleanDropDown.setHint(hints.getBooleanParamHint());
        }
    };

    View.OnClickListener handleExecuteGitHubOperationButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!thereIsInternet(context)) return;

            if (!store.isGithubDetailsSet(context)) {
                Toast.makeText(context, "GitHub details is required.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.githubRepositoryTextView.getText().toString().isEmpty() || binding.githubBranchTextView.getText().toString().isEmpty()) {
                Toast.makeText(context, "Repository and Branch required.", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (GithubOperation.from(binding.githubOperationsDropDown.getText().toString())) {
                case Create_Text_File:
                    if (binding.param1TextView.getText().toString().isEmpty()
                            || binding.param2TextView.getText().toString().isEmpty()
                            || binding.param3TextView.getText().toString().isEmpty()) {
                        handleRequiredFieldsMissing();
                        return;
                    }

                    createTextFile(
                            binding.param1TextView.getText().toString(),
                            binding.param2TextView.getText().toString(),
                            binding.param3TextView.getText().toString(),
                            binding.githubRepositoryTextView.getText().toString(),
                            binding.githubBranchTextView.getText().toString()
                    );
                    break;
                case Create_Gist:

                    if (binding.param1TextView.getText().toString().isEmpty()
                            || binding.param2TextView.getText().toString().isEmpty()
                            || binding.param3TextView.getText().toString().isEmpty()
                            || binding.booleanDropDown.getText().toString().isEmpty()) {
                        handleRequiredFieldsMissing();
                        return;
                    }

                    createGist(
                            binding.param1TextView.getText().toString(),
                            binding.param2TextView.getText().toString(),
                            binding.param3TextView.getText().toString(),
                            BooleanValue.to(binding.booleanDropDown.getText().toString()),
                            binding.githubRepositoryTextView.getText().toString()
                    );
                    break;

                case Read_Text_File:
                    if (binding.param1TextView.getText().toString().isEmpty()) {
                        handleRequiredFieldsMissing();
                        return;
                    }

                    readTextFile(
                            binding.param1TextView.getText().toString(),
                            binding.githubRepositoryTextView.getText().toString(),
                            binding.githubResultTextView
                    );
                    break;

                case Read_ReadMe:

                    readReadMe(
                            binding.githubRepositoryTextView.getText().toString(),
                            binding.githubResultTextView
                    );
                    break;

                case Create_Repository:

                    if (binding.param1TextView.getText().toString().isEmpty()
                            || binding.param2TextView.getText().toString().isEmpty()
                            || binding.param3TextView.getText().toString().isEmpty()
                            || binding.booleanDropDown.getText().toString().isEmpty()) {
                        handleRequiredFieldsMissing();
                        return;
                    }

                    createRepository(
                            binding.param1TextView.getText().toString(),
                            binding.param2TextView.getText().toString(),
                            binding.param3TextView.getText().toString(),
                            BooleanValue.to(binding.booleanDropDown.getText().toString()),
                            binding.githubRepositoryTextView.getText().toString()
                    );
                    break;
            }
        }
    };

    View.OnClickListener handleSendGithubResultButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!thereIsInternet(context)) return;

            if(Support.isEmpty(binding.githubResultTextView)) return;

            service.sendText(
                    context,
                    store,
                    machines,
                    SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                            store.getUsername(context),
                            store.getID(context),
                            Transfer.Intent.writeText,
                            store.getSender(context),
                            determineTarget(context, store, machines),
                            POST_PURPOSE_REGULAR,
                            determineMeta(context, store),
                            EncryptionManager.getInstance().encrypt(context, store, binding.githubResultTextView.getText().toString()),
                            DomainObjects.EMPTY_STRING
                    ),
                    DEFAULT_ERROR_MESSAGE_SUFFIX,
                    DEFAULT_FAILURE_MESSAGE_SUFFIX);

        }
    };

    private void shareGithubResult(){
        if(Support.isEmpty(binding.githubResultTextView)) return;

        device.shareText(
                context,
                binding.githubResultTextView.getText().toString()
        );

    }

    View.OnClickListener shareGithubResultButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MenuBottomSheetFragment(getButtons()).show(requireActivity().getSupportFragmentManager(), MenuBottomSheetFragment.TAG);
        }
    };

    View.OnClickListener handleCopyGithubResultButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Support.isEmpty(binding.githubResultTextView)) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                device.toClipboard(binding.githubResultTextView.getText().toString(), context);
            }
            device.tell(DomainObjects.copiedToClipboardMessage(null), context);
        }
    };

    private void saveResult(){
        //ToDo check if path from binding.param1TextView.getText() is valid file path
        if(Support.isEmpty(binding.githubResultTextView) || Support.isEmpty(binding.param1TextView)) return;

        storage.writeText(
                binding.githubResultTextView.getText().toString(),
                binding.param1TextView.getText().toString(),
                binding.param1TextView.getText().toString() + " created in Internal Storage.",
                DomainObjects.WRITE_FILE_FAILED
        );

    }

    View.OnClickListener handleSaveGithubResultButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveResult();
        }
    };

    private void handleRequiredFieldsMissing() {
        Toast.makeText(context, DomainObjects.REQUIRED_FIELDS_MISSING_STRING, Toast.LENGTH_SHORT).show();
    }

    private void readReadMe(String repository, TextView outputTextView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                try {
                    result = new GitHubLite.builder(
                            store.getGithubOwner(getCurrentContext()),
                            repository,
                            store.getGithubToken(getCurrentContext())
                    ).createClient()
                            .readReadMe();

                    String finalResult = result;
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            outputTextView.setText(finalResult);
                        }
                    });
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    private void readTextFile(String path, String repository, TextView outputTextView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                try {
                    result = new GitHubLite.builder(
                            store.getGithubOwner(getCurrentContext()),
                            repository,
                            store.getGithubToken(getCurrentContext())
                    ).createClient()
                            .readTextFile(path);

                    String finalResult = result;
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            outputTextView.setText(finalResult);
                        }
                    });
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    private void createGist(String path, String content, String description, boolean isPublic, String repository) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    new GitHubLite.builder(
                            store.getGithubOwner(getCurrentContext()),
                            repository,
                            store.getGithubToken(getCurrentContext())
                    ).createClient()
                            .createGist(path, content, description, isPublic);
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    private void createRepository(String repository, String description, String homePage, boolean isPublic, String defaultRepository) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    new GitHubLite.builder(
                            store.getGithubOwner(getCurrentContext()),
                            defaultRepository,
                            store.getGithubToken(getCurrentContext())
                    ).createClient()
                            .createRepository(repository, description, homePage, isPublic);
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    private void createTextFile(String path, String content, String commitMessage, String repository, String branch) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    new GitHubLite.builder(
                            store.getGithubOwner(getCurrentContext()),
                            repository,
                            store.getGithubToken(getCurrentContext())
                    ).createClient()
                            .setBranch(branch)
                            .createTextFile(path, content, commitMessage);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            device.tell(DomainObjects.SUCCESS_MESSAGE, context);
                        }
                    });
                } catch (IOException ignored) {
                }
            }
        }).start();

    }

    private List<Ui.ButtonObject> getButtons(){
        Ui.ButtonObject.DimensionInfo dimensions = new Ui.ButtonObject.DimensionInfo(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        Ui.ButtonObject.MarginInfo margins = new Ui.ButtonObject.MarginInfo();


        Ui.ButtonObject.ViewInfo shareViewing = new Ui.ButtonObject.ViewInfo("share this", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_share, 1);
        Ui.ButtonObject shareButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareGithubResult();
            }
        }, margins, dimensions, shareViewing);


        Ui.ButtonObject.ViewInfo saveViewing = new Ui.ButtonObject.ViewInfo("save to device", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_save, 1);
        Ui.ButtonObject saveButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResult();
            }
        }, margins, dimensions, saveViewing);






        List<Ui.ButtonObject> buttons = new ArrayList<>();
        buttons.add(shareButton);
        buttons.add(saveButton);


        return buttons;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}