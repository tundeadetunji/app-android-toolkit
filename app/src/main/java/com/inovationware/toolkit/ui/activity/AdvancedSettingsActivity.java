package com.inovationware.toolkit.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.MenuCompat;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.ActivityAdvancedSettingsBinding;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.library.app.MessageBox;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.retrofit.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.app.SiteManager;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.global.library.app.retrofit.Repo;
import com.inovationware.toolkit.system.service.ToolkitServiceManager;
import com.inovationware.toolkit.ui.contract.BaseActivity;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.cachedMemos;
import static com.inovationware.toolkit.global.library.utility.Code.content;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_NET_TIMER_UPDATE_TOKEN;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_FAVORITE_URL_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_PINNED_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_READING_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_RUNNING_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_SCRATCH_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_TODO_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.TARGET_MODE_TO_DEVICE;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.getOutOfThere;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;

public class AdvancedSettingsActivity extends BaseActivity {
    private ActivityAdvancedSettingsBinding binding;
    private SharedPreferencesManager store;
    private GroupManager machines;
    private SiteManager sites;
    private Factory factory;
    private ToolkitServiceManager services;
    private Context context;

    private Feedback feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdvancedSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupVariables();
        setupUi();
        setupListeners();
        postSetup();
    }


    private void setupVariables(){
        context = AdvancedSettingsActivity.this;
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        sites = SiteManager.getInstance(getApplicationContext());
        feedback = new Feedback(getApplicationContext());
        factory = Factory.getInstance();
        services = ToolkitServiceManager.getInstance();
    }

    private void setupUi(){
        binding.pinnedTextView.setText(store.getString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING));
        binding.readingTextView.setText(store.getString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_READING_KEY, EMPTY_STRING));
        binding.runningTextView.setText(store.getString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING));
        binding.todoTextView.setText(store.getString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING));
        binding.scratchTextView.setText(store.getString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING));
        binding.favoriteUrlTextView.setText(store.getString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING));
    }

    private void setupListeners(){
        binding.generatePasswordButton.setOnClickListener(generatePasswordButtonHandler);
        binding.generateSaltButton.setOnClickListener(generateSaltButtonHandler);
        binding.resetEncryptionDetailsButton.setOnClickListener(resetEncryptionDetailsButtonHandler);
        binding.saveGithubDetailsButton.setOnClickListener(saveGithubDetailsButtonHandler);
        binding.resetGithubDetailsButton.setOnClickListener(resetGithubDetailsButtonHandler);
        binding.saveEncryptionDetailsButton.setOnClickListener(saveEncryptionDetailsButtonHandler);
        binding.resetSearchSitesButton.setOnClickListener(resetSearchSitesButtonHandler);
        binding.resetNotesButton.setOnClickListener(resetNotesButtonHandler);
        binding.saveLinksButton.setOnClickListener(saveLinksButtonHandler);
        binding.startNetworkServiceButton.setOnClickListener(startNetworkServiceButtonHandler);
        binding.resetNotesTitlesButton.setOnClickListener(resetNotesTitlesButtonListener);

        /*resetLocalTasksKeysButton = findViewById(R.id.resetLocalTasksKeysButton);
        resetLocalTasksKeysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogTemplate template = new DialogTemplate() {
                    @Override
                    public void positiveButtonAction() {
                        store.resetLocalTaskKeys(AdvancedSettingsActivity.this);
                        Support.announce(AdvancedSettingsActivity.this, "Keys have been reset.");
                    }

                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Really reset keys?");
                template.setPositiveButtonText(Phrase.sure);
                template.setNegativeButtonText(Phrase.never_mind);
                template.show(AdvancedSettingsActivity.this);
            }
        });*/

        /*resetTokenButton = findViewById(R.id.resetTokenButton);
        binding.resetTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogTemplate template = new DialogTemplate() {
                    @Override
                    public void positiveButtonAction() {
                        String token = FirebaseInstanceId.getInstance().getToken();

                        ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(view.getContext().CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Text", token);
                        clipboard.setPrimaryClip(clip);

                        //send it to desktop here
                        if (initialParamsAreSet(AdvancedSettingsActivity.this, store, machines))
                            doSendForUpdateNetTimerToken(token);

                        new Feedback(view.getContext()).toast("Token sent to PC and also copied to clipboard.", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void negativeButtonAction() {
                    }
                };
                template.setMessage("Send new token?");
                template.setPositiveButtonText(Phrase.sure);
                template.setNegativeButtonText(Phrase.never_mind);
                template.show(AdvancedSettingsActivity.this);
            }
        });*/

    }
    private void postSetup(){
        setEncryptionDetails();
        setGithubDetails();
    }

    private final View.OnClickListener resetNotesTitlesButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MessageBox template = new MessageBox() {
                @Override
                public void positiveButtonAction() {
                    factory.memo.titles.clearTitles(context, store);
                }

                @Override
                public void negativeButtonAction() {
                }
            };
            template.setMessage("Really clear notes' titles?");
            template.setPositiveButtonText(DomainObjects.exactly);
            template.setNegativeButtonText(DomainObjects.no);
            template.show(AdvancedSettingsActivity.this);

        }
    };
    private final View.OnClickListener startNetworkServiceButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            services.networkServiceShouldRun = true;
            services.startServices(getApplicationContext());
            Toast.makeText(AdvancedSettingsActivity.this, "Auto-handling requests from base is resumed.", Toast.LENGTH_SHORT).show();
            getOutOfThere(AdvancedSettingsActivity.this);
        }
    };

    private final View.OnClickListener saveLinksButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MessageBox template = new MessageBox() {
                @Override
                public void positiveButtonAction() {
                    store.setString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_READING_KEY, content(binding.readingTextView));
                    store.setString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_RUNNING_KEY, content(binding.runningTextView));
                    store.setString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_TODO_KEY, content(binding.todoTextView));
                    store.setString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_SCRATCH_KEY, content(binding.scratchTextView));
                    store.setString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_FAVORITE_URL_KEY, content(binding.favoriteUrlTextView));
                    store.setString(AdvancedSettingsActivity.this, SHARED_PREFERENCES_PINNED_KEY, content(binding.pinnedTextView));
                    Support.announce(AdvancedSettingsActivity.this, "Links have been reset.");
                }

                @Override
                public void negativeButtonAction() {
                }
            };
            template.setMessage("Really reset links?");
            template.setPositiveButtonText(DomainObjects.sure);
            template.setNegativeButtonText(DomainObjects.never_mind);
            template.show(AdvancedSettingsActivity.this);
        }
    };

    private final View.OnClickListener generatePasswordButtonHandler = new View.OnClickListener() {
        @SneakyThrows
        @Override
        public void onClick(View v) {
            binding.passwordTextView.setText(factory.encryption.service.toUtf8String(factory.encryption.service.generateKey()));
        }
    };

    private final View.OnClickListener generateSaltButtonHandler = new View.OnClickListener() {
        @SneakyThrows
        @Override
        public void onClick(View v) {
            binding.saltTextView.setText(factory.encryption.service.toUtf8String(factory.encryption.service.generateKey()));
        }
    };

    private final View.OnClickListener resetEncryptionDetailsButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setEncryptionDetails();
        }
    };

    private final View.OnClickListener saveGithubDetailsButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Support.isEmpty(binding.githubOwnerTextView) || Support.isEmpty(binding.githubTokenTextView))
                return;
            MessageBox template = new MessageBox() {
                @Override
                public void positiveButtonAction() {
                    store.setGithubOwner(AdvancedSettingsActivity.this, content(binding.githubOwnerTextView));
                    store.setGithubToken(AdvancedSettingsActivity.this, content(binding.githubTokenTextView));
                    store.setGithubDefaultRepository(AdvancedSettingsActivity.this, content(binding.githubDefaultRepositoryTextView));
                    store.setGithubDefaultBranch(AdvancedSettingsActivity.this, content(binding.githubDefaultBranchTextView));

                    Support.announce(AdvancedSettingsActivity.this, "GitHub details set.");
                }

                @Override
                public void negativeButtonAction() {
                }
            };
            template.setMessage("Really save details?");
            template.setPositiveButtonText(DomainObjects.yes);
            template.setNegativeButtonText(DomainObjects.never_mind);
            template.show(AdvancedSettingsActivity.this);
        }
    };

    private final View.OnClickListener resetGithubDetailsButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setGithubDetails();
        }
    };

    private final View.OnClickListener saveEncryptionDetailsButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Support.isEmpty(binding.saltTextView)) return;
            MessageBox template = new MessageBox() {
                @Override
                public void positiveButtonAction() {
                    store.setEncryptionSalt(AdvancedSettingsActivity.this, content(binding.saltTextView));
                    store.setEncryptionPassword(AdvancedSettingsActivity.this, content(binding.passwordTextView));
                    Support.announce(AdvancedSettingsActivity.this, "Salt and password set.");
                }

                @Override
                public void negativeButtonAction() {
                }
            };
            template.setMessage("Really save salt and password?\n\nThese values must match that of your target devices!");
            template.setPositiveButtonText(DomainObjects.yes);
            template.setNegativeButtonText(DomainObjects.never_mind);
            template.show(AdvancedSettingsActivity.this);
        }
    };

    private final View.OnClickListener resetSearchSitesButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MessageBox template = new MessageBox() {
                @Override
                public void positiveButtonAction() {
                    sites.clearSites(AdvancedSettingsActivity.this);
                    sites.clearLastVisitedSite(AdvancedSettingsActivity.this);
                    sites.clearLastSearchTerm(AdvancedSettingsActivity.this);
                    Support.announce(AdvancedSettingsActivity.this, "Sites have been reset.");
                }

                @Override
                public void negativeButtonAction() {
                }
            };
            template.setMessage("Really reset sites?");
            template.setPositiveButtonText(DomainObjects.sure);
            template.setNegativeButtonText(DomainObjects.never_mind);
            template.show(AdvancedSettingsActivity.this);
        }
    };

    private final View.OnClickListener resetNotesButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MessageBox template = new MessageBox() {
                @Override
                public void positiveButtonAction() {
                    requestClearNotes(AdvancedSettingsActivity.this);
                }

                @Override
                public void negativeButtonAction() {
                }
            };
            template.setMessage("Really clear notes?");
            template.setPositiveButtonText(DomainObjects.exactly);
            template.setNegativeButtonText(DomainObjects.no);
            template.show(AdvancedSettingsActivity.this);
        }
    };
    
    void requestClearNotes(Context context) {
        if (!thereIsInternet(context) || !initialParamsAreSet(context, store, machines)) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.sendText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(context),
                store.getID(context),
                String.valueOf(Transfer.Intent.clearNote),
                store.getSender(context),
                machines.getDefaultDevice(context),
                DomainObjects.POST_PURPOSE_LOGGER,
                determineMeta(context, store),
                EMPTY_STRING,
                DomainObjects.EMPTY_STRING
        );

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    cachedMemos = null;
                    Support.announce(AdvancedSettingsActivity.this, "Notes have been cleared.");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
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
            startActivity(new Intent(AdvancedSettingsActivity.this, MainActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setEncryptionDetails() {

        binding.saltTextView.setText(store.getEncryptionSalt(AdvancedSettingsActivity.this));
        binding.passwordTextView.setText(store.getEncryptionPassword(AdvancedSettingsActivity.this));
    }

    private void setGithubDetails() {

        binding.githubOwnerTextView.setText(store.getGithubOwner(AdvancedSettingsActivity.this));
        binding.githubTokenTextView.setText(store.getGithubToken(AdvancedSettingsActivity.this));
        binding.githubDefaultRepositoryTextView.setText(store.getGithubDefaultRepository(AdvancedSettingsActivity.this));
        binding.githubDefaultBranchTextView.setText(store.getGithubDefaultBranch(AdvancedSettingsActivity.this));

    }

    //won't inform user that it was successful (i.e. don't display response from server, typically Go, Going, Gone!)
    void doSendForUpdateNetTimerToken(String info) {
        if (!thereIsInternet(getApplicationContext())) return;

        Retrofit retrofitImpl = Repo.getInstance().create(AdvancedSettingsActivity.this, store);

        Call<String> navigate = retrofitImpl.sendText(
                HTTP_TRANSFER_URL(AdvancedSettingsActivity.this, store),
                store.getUsername(AdvancedSettingsActivity.this),
                store.getID(AdvancedSettingsActivity.this),
                String.valueOf(Transfer.Intent.writeText),
                store.getSender(AdvancedSettingsActivity.this),
                machines.getDefaultDevice(AdvancedSettingsActivity.this),
                POST_PURPOSE_NET_TIMER_UPDATE_TOKEN,
                TARGET_MODE_TO_DEVICE,
                info,
                DomainObjects.EMPTY_STRING);

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    /*if (response.body() != null)
                        if (!response.body().isEmpty())
                            new Feedback(context).toast(response.body());*/
                } else {
                    if (!store.shouldDisplayErrorMessage(AdvancedSettingsActivity.this)) {
                        return;
                    }
                    feedback.toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (!store.shouldDisplayErrorMessage(AdvancedSettingsActivity.this)) {
                    return;
                }
                feedback.toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
            }
        });
    }
}

