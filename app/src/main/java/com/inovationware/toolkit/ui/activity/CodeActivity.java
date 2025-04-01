package com.inovationware.toolkit.ui.activity;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_CREATE;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.global.domain.DomainObjects.POST_PURPOSE_UPDATE;
import static com.inovationware.toolkit.global.library.utility.Support.determineMeta;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.code.domain.Language;
import com.inovationware.toolkit.code.entity.VocabularyUnit;
import com.inovationware.toolkit.code.service.impl.LanguageServiceImpl;
import com.inovationware.toolkit.code.strategy.impl.LanguageStrategyImpl;
import com.inovationware.toolkit.code.verb.Vocabulary;
import com.inovationware.toolkit.databinding.ActivityCodeBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.InputDialog;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.external.GitHubLite;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.StorageClient;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.ui.adapter.VocabularyRecyclerViewAdapter;
import com.inovationware.toolkit.ui.contract.BaseActivity;

import java.io.IOException;

public class CodeActivity extends BaseActivity {
    private ActivityCodeBinding binding;
    private View view;
    private Factory factory = Factory.getInstance();
    private LanguageServiceImpl service = LanguageServiceImpl.getInstance(LanguageStrategyImpl.getInstance());
    private SharedPreferencesManager store;
    private DeviceClient device;
    private GroupManager machines;

    private GitHubLite git;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCodeBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);

        setupUi(view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.code_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.converterMenuItem) {
            startActivity(new Intent(CodeActivity.this, ConverterActivity.class));
            return true;
        } else if (item.getItemId() == R.id.githubMenuItem) {
            startActivity(new Intent(CodeActivity.this, GitHubActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void setupUi(View view) {
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        device = DeviceClient.getInstance();

        factory.ui.bindProperty(view.getContext(), view.findViewById(R.id.programmingLanguageDropDown), service.getLanguages(), R.layout.default_drop_down, DomainObjects.EMPTY_STRING);

        setupVocabulary();


        view.findViewById(R.id.saveCodeButton).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (((TextView) binding.convertedCodeTextView).getText().toString().trim().isEmpty())
                    return;

                String filename = ((TextView) view.findViewById(R.id.codeTextView)).getText().toString().contains(DomainObjects.BEGIN_HTML_TAG) && ((TextView) view.findViewById(R.id.codeTextView)).getText().toString().contains(DomainObjects.END_HTML_TAG) ?
                        DomainObjects.HTML_FILENAME : DomainObjects.JAVA_FILENAME;
                StorageClient.getInstance(view.getContext()).writeText(getTranslation(), filename,
                        filename + " created in Internal Storage.", DomainObjects.WRITE_FILE_FAILED);
            }
        });

        view.findViewById(R.id.saveMemoButton).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                try {
                    KeepIntentService.getInstance(CodeActivity.this, store, device).saveNoteToCloud(Memo.create(
                            createLocalFilename(),
                            getTranslation(),
                            CodeActivity.this, store));
                } catch (IOException ignored) {
                }

            }
        });

        view.findViewById(R.id.convertCodeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((TextView) view.findViewById(R.id.convertedCodeTextView)).setText(
                            service.to(
                                    ((TextView) view.findViewById(R.id.codeTextView)).getText().toString(),
                                    service.detectLanguage(((TextView) view.findViewById(R.id.codeTextView)).getText().toString()),
                                    Language.valueOf(((TextView) view.findViewById(R.id.programmingLanguageDropDown)).getText().toString())));

                } catch (Exception ignored) {
                }
            }
        });

        binding.shareCodeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                factory.device.shareText(
                        CodeActivity.this,
                        getTranslation()
                );

            }
        });

        binding.copyCodeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    factory.device.toClipboard(getTranslation(), CodeActivity.this);
                    factory.device.tell(DomainObjects.copiedToClipboardMessage(DomainObjects.CODE), CodeActivity.this);
                }
            }
        });

        binding.sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (!thereIsInternet(CodeActivity.this)) return;
                sendCode(
                        EncryptionManager.getInstance().encrypt(CodeActivity.this, store, getTranslation()),
                        POST_PURPOSE_REGULAR
                );
            }
        });

        binding.createCodeFileButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                createInputDialog(
                        EncryptionManager.getInstance().encrypt(CodeActivity.this, store, getTranslation()),
                        POST_PURPOSE_CREATE
                ).show();
            }
        });

        binding.updateCodeFileButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                createInputDialog(
                        EncryptionManager.getInstance().encrypt(CodeActivity.this, store, getTranslation()),
                        POST_PURPOSE_UPDATE
                ).show();
            }
        });

    }

    private void setupVocabulary() {
        Vocabulary vocabulary = Vocabulary.getInstance();
        binding.vocabularyRecyclerView.setVisibility(View.GONE);
        binding.vocabularyRecyclerView.setLayoutManager(new LinearLayoutManager(CodeActivity.this));
        binding.vocabularyRecyclerView.setAdapter(new VocabularyRecyclerViewAdapter(CodeActivity.this, VocabularyUnit.listing(
                vocabulary.CodeInEnglish, vocabulary.CodeInYoruba, vocabulary.CodeInBulgarian, vocabulary.CodeDescription
        )));
        binding.vocabularyRecyclerView.setVisibility(View.VISIBLE);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getTranslation() {
        if (((TextView) binding.convertedCodeTextView).getText().toString().trim().isEmpty() || ((TextView) view.findViewById(R.id.programmingLanguageDropDown)).getText().toString().isEmpty())
            return "";


        Language dest = Language.from(((TextView) view.findViewById(R.id.programmingLanguageDropDown)).getText().toString());
        return Code.formatOutput(CodeActivity.this,
                service.to(
                        ((TextView) view.findViewById(R.id.codeTextView)).getText().toString(),
                        service.detectLanguage(((TextView) view.findViewById(R.id.codeTextView)).getText().toString()),
                        dest),
                store,
                device,
                dest
        );
    }


    private InputDialog createInputDialog(String text, String purpose) {
        return new InputDialog(CodeActivity.this, "", "Full path of file\non the target device", "e.g.  c:\\users\\file.txt") {
            @Override
            public void positiveButtonAction() {
                if (purpose.equalsIgnoreCase(POST_PURPOSE_CREATE) || purpose.equalsIgnoreCase(POST_PURPOSE_UPDATE)) {
                    if (!this.getText().isEmpty()) sendCode(this.getText() + "\n" + text, purpose);
                } else {
                    sendCode(text, purpose);
                }
            }

            @Override
            public void negativeButtonAction() {

            }
        };
    }

    private void sendCode(String text, String purpose) {
        factory.transfer.service.sendText(
                CodeActivity.this,
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(CodeActivity.this, store),
                        store.getUsername(CodeActivity.this),
                        store.getID(CodeActivity.this),
                        Transfer.Intent.writeText,
                        store.getSender(CodeActivity.this),
                        determineTarget(CodeActivity.this, store, machines),
                        purpose,
                        determineMeta(CodeActivity.this, store),
                        text,
                        DomainObjects.EMPTY_STRING),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }

    private String createLocalFilename() {
        return "Code Output.txt";
    }
}