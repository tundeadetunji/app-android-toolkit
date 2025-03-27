package com.inovationware.toolkit.ui.activity;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.Strings.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.ActivityGitHubBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.datatransfer.service.DataTransferService;
import com.inovationware.toolkit.datatransfer.service.rest.RestDataTransferService;
import com.inovationware.toolkit.datatransfer.strategy.rest.RestDataTransferStrategy;
import com.inovationware.toolkit.github.domain.app.BooleanValue;
import com.inovationware.toolkit.github.domain.app.GithubOperation;
import com.inovationware.toolkit.github.utility.Resolver;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.external.GitHubLite;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.StorageClient;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.global.library.utility.Ui;
import com.inovationware.toolkit.ui.contract.BaseActivity;

import java.io.IOException;

public class GitHubActivity extends BaseActivity {
    private ActivityGitHubBinding binding;
    private Resolver resolver;
    private SharedPreferencesManager store;
    private DataTransferService service;
    private GroupManager machines;
    private DeviceClient device;
    private StorageClient storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGitHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setStrategy();
        setupUi(Ui.getInstance());
    }

    private void setStrategy() {
        resolver = Resolver.getInstance();
        store = SharedPreferencesManager.getInstance();
        service = RestDataTransferService.getInstance(RestDataTransferStrategy.getInstance());
        machines = GroupManager.getInstance();
        device = DeviceClient.getInstance();
        storage = StorageClient.getInstance(GitHubActivity.this);
    }

    private Context getContext() {
        return GitHubActivity.this;
    }

    private void setupUi(Ui ui) {
        ui.bindProperty(getContext(), binding.githubOperationsDropDown, GithubOperation.listing());
        ui.bindProperty(getContext(), binding.booleanDropDown, BooleanValue.listing());

        binding.presetParamsButton.setOnClickListener(handlePresetParamsButton);
        binding.executeGitHubOperationButton.setOnClickListener(handleExecuteGitHubOperationButton);
        binding.githubRepositoryTextView.setText(store.getGithubDefaultRepository(GitHubActivity.this));
        binding.githubBranchTextView.setText(store.getGithubDefaultBranch(GitHubActivity.this));
        binding.sendGithubResultButton.setOnClickListener(handleSendGithubResultButton);
        binding.shareGithubResultButton.setOnClickListener(handleShareGithubResultButton);
        binding.copyGithubResultButton.setOnClickListener(handleCopyGithubResultButton);
        binding.saveGithubResultButton.setOnClickListener(handleSaveGithubResultButton);
    }

    private void clearFields() {
        binding.param1TextView.setText(Strings.EMPTY_STRING);
        binding.param1TextView.setHint(Strings.EMPTY_STRING);

        binding.param2TextView.setText(Strings.EMPTY_STRING);
        binding.param2TextView.setHint(Strings.EMPTY_STRING);

        binding.param3TextView.setText(Strings.EMPTY_STRING);
        binding.param3TextView.setHint(Strings.EMPTY_STRING);

        binding.booleanDropDown.setText(Strings.EMPTY_STRING);
        binding.booleanDropDown.setHint(Strings.EMPTY_STRING);

        binding.githubResultTextView.setText(Strings.EMPTY_STRING);
        binding.githubResultTextView.setHint(Strings.EMPTY_STRING);
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
            startActivity(new Intent(GitHubActivity.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            if (!thereIsInternet(GitHubActivity.this)) return;

            if (!store.isGithubDetailsSet(GitHubActivity.this)) {
                Toast.makeText(GitHubActivity.this, "GitHub details is required.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.githubRepositoryTextView.getText().toString().isEmpty() || binding.githubBranchTextView.getText().toString().isEmpty()) {
                Toast.makeText(GitHubActivity.this, "Repository and Branch required.", Toast.LENGTH_SHORT).show();
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
            if (!thereIsInternet(GitHubActivity.this)) return;

            if(Support.isEmpty(binding.githubResultTextView)) return;

            service.sendText(
                    GitHubActivity.this,
                    store,
                    machines,
                    SendTextRequest.create(HTTP_TRANSFER_URL(GitHubActivity.this, store),
                            store.getUsername(GitHubActivity.this),
                            store.getID(GitHubActivity.this),
                            Transfer.Intent.writeText,
                            store.getSender(GitHubActivity.this),
                            determineTarget(GitHubActivity.this, store, machines),
                            POST_PURPOSE_REGULAR,
                            determineMeta(GitHubActivity.this, store),
                            EncryptionManager.getInstance().encrypt(GitHubActivity.this, store, binding.githubResultTextView.getText().toString()),
                            Strings.EMPTY_STRING
                    ),
                    DEFAULT_ERROR_MESSAGE_SUFFIX,
                    DEFAULT_FAILURE_MESSAGE_SUFFIX);

        }
    };

    View.OnClickListener handleShareGithubResultButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Support.isEmpty(binding.githubResultTextView)) return;

            device.shareText(
                    GitHubActivity.this,
                    binding.githubResultTextView.getText().toString()
            );
        }
    };

    View.OnClickListener handleCopyGithubResultButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Support.isEmpty(binding.githubResultTextView)) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                device.toClipboard(binding.githubResultTextView.getText().toString(), GitHubActivity.this);
            }
            device.tell(Strings.copiedToClipboardMessage(null), GitHubActivity.this);
        }
    };

    View.OnClickListener handleSaveGithubResultButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //ToDo check if path from binding.param1TextView.getText() is valid file path
            if(Support.isEmpty(binding.githubResultTextView) || Support.isEmpty(binding.param1TextView)) return;

            storage.writeText(
                    binding.githubResultTextView.getText().toString(),
                    binding.param1TextView.getText().toString(),
                    binding.param1TextView.getText().toString() + " created in Internal Storage.",
                    Strings.WRITE_FILE_FAILED
            );
        }
    };

    private void handleRequiredFieldsMissing() {
        Toast.makeText(GitHubActivity.this, Strings.REQUIRED_FIELDS_MISSING_STRING, Toast.LENGTH_SHORT).show();
    }

    private void readReadMe(String repository, TextView outputTextView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                try {
                    result = new GitHubLite.builder(
                            store.getGithubOwner(getContext()),
                            repository,
                            store.getGithubToken(getContext())
                    ).createClient()
                            .readReadMe();

                    String finalResult = result;
                    runOnUiThread(new Runnable() {
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
                            store.getGithubOwner(getContext()),
                            repository,
                            store.getGithubToken(getContext())
                    ).createClient()
                            .readTextFile(path);

                    String finalResult = result;
                    runOnUiThread(new Runnable() {
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
                            store.getGithubOwner(getContext()),
                            repository,
                            store.getGithubToken(getContext())
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
                            store.getGithubOwner(getContext()),
                            defaultRepository,
                            store.getGithubToken(getContext())
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
                            store.getGithubOwner(getContext()),
                            repository,
                            store.getGithubToken(getContext())
                    ).createClient()
                            .setBranch(branch)
                            .createTextFile(path, content, commitMessage);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            device.tell(Strings.SUCCESS_MESSAGE, GitHubActivity.this);
                        }
                    });
                } catch (IOException ignored) {
                }
            }
        }).start();

    }

}