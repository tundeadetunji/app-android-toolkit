package com.inovationware.toolkit.ui.activity;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_CREATE;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_REGULAR;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_UPDATE;
import static com.inovationware.toolkit.common.utility.Support.determineMeta;
import static com.inovationware.toolkit.common.utility.Support.determineTarget;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.features.code.domain.Language;
import com.inovationware.toolkit.features.code.entity.VocabularyUnit;
import com.inovationware.toolkit.features.code.service.impl.LanguageServiceImpl;
import com.inovationware.toolkit.features.code.strategy.impl.LanguageStrategyImpl;
import com.inovationware.toolkit.features.code.verb.Vocabulary;
import com.inovationware.toolkit.databinding.ActivityCodeBinding;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.common.utility.EncryptionManager;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.InputDialog;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.GitHubLite;
import com.inovationware.toolkit.common.utility.Code;
import com.inovationware.toolkit.common.utility.DeviceClient;
import com.inovationware.toolkit.common.utility.StorageClient;
import com.inovationware.toolkit.common.utility.Ui;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.features.memo.service.impl.KeepIntentService;
import com.inovationware.toolkit.ui.adapter.VocabularyRecyclerViewAdapter;
import com.inovationware.toolkit.ui.contract.BaseActivity;
import com.inovationware.toolkit.ui.fragment.MenuBottomSheetFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeActivity extends BaseActivity {
    private ActivityCodeBinding binding;
    private View view;
    private Factory factory = Factory.getInstance();
    private LanguageServiceImpl service = LanguageServiceImpl.getInstance(LanguageStrategyImpl.getInstance());
    private SharedPreferencesManager store;
    private DeviceClient device;
    private GroupManager machines;
    private Context context;

    private GitHubLite git;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCodeBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);

        setReferences();
        setupUi(view);
        setListeners();

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

    private void setReferences(){
        context = CodeActivity.this;
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        device = DeviceClient.getInstance();

    }

    private void setupUi(View view) {

        factory.ui.bindProperty(view.getContext(), view.findViewById(R.id.programmingLanguageDropDown), service.getLanguages(), R.layout.default_drop_down, DomainObjects.EMPTY_STRING);

        setupVocabulary();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void shareCode(){
        factory.device.shareText(
                CodeActivity.this,
                getTranslation()
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createFile(){
        createInputDialog(
                EncryptionManager.getInstance().encrypt(CodeActivity.this, store, getTranslation()),
                POST_PURPOSE_CREATE
        ).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateFile(){
        createInputDialog(
                EncryptionManager.getInstance().encrypt(CodeActivity.this, store, getTranslation()),
                POST_PURPOSE_UPDATE
        ).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createMemo(){
        try {
            KeepIntentService.getInstance(CodeActivity.this, store, device).saveNoteToCloud(Memo.create(
                    createLocalFilename(),
                    getTranslation(),
                    CodeActivity.this, store));
        } catch (IOException ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveCode(){
        if (((TextView) binding.convertedCodeTextView).getText().toString().trim().isEmpty())
            return;

        String filename = ((TextView) view.findViewById(R.id.codeTextView)).getText().toString().contains(DomainObjects.BEGIN_HTML_TAG) && ((TextView) view.findViewById(R.id.codeTextView)).getText().toString().contains(DomainObjects.END_HTML_TAG) ?
                DomainObjects.HTML_FILENAME : DomainObjects.JAVA_FILENAME;
        StorageClient.getInstance(view.getContext()).writeText(getTranslation(), filename,
                filename + " created in Internal Storage.", DomainObjects.WRITE_FILE_FAILED);
    }

    private void setListeners(){
        view.findViewById(R.id.saveCodeButton).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                saveCode();
            }
        });

        view.findViewById(R.id.saveMemoButton).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                createMemo();
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
                new MenuBottomSheetFragment(getButtons()).show(getSupportFragmentManager(), MenuBottomSheetFragment.TAG);
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
                createFile();
            }
        });

        binding.updateCodeFileButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                updateFile();
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


    private List<Ui.ButtonObject> getButtons(){
        Ui.ButtonObject.DimensionInfo dimensions = new Ui.ButtonObject.DimensionInfo(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        Ui.ButtonObject.MarginInfo margins = new Ui.ButtonObject.MarginInfo();


        Ui.ButtonObject.ViewInfo shareViewing = new Ui.ButtonObject.ViewInfo("share this", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_share, 1);
        Ui.ButtonObject shareButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                shareCode();
            }
        }, margins, dimensions, shareViewing);



        Ui.ButtonObject.ViewInfo createViewing = new Ui.ButtonObject.ViewInfo("create file", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_insert_drive_file_24, 1);
        Ui.ButtonObject createButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                createFile();
            }
        }, margins, dimensions, createViewing);



        Ui.ButtonObject.ViewInfo updateViewing = new Ui.ButtonObject.ViewInfo("update file", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_edit, 1);
        Ui.ButtonObject updateButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                updateFile();
            }
        }, margins, dimensions, updateViewing);



        Ui.ButtonObject.ViewInfo memoViewing = new Ui.ButtonObject.ViewInfo("create note", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.baseline_sticky_note_2_24, 1);
        Ui.ButtonObject memoButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                createMemo();
            }
        }, margins, dimensions, memoViewing);



        Ui.ButtonObject.ViewInfo saveViewing = new Ui.ButtonObject.ViewInfo("save to device", MaterialButton.ICON_GRAVITY_TEXT_START, R.drawable.ic_save, 1);
        Ui.ButtonObject saveButton = new Ui.ButtonObject(context, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                saveCode();
            }
        }, margins, dimensions, saveViewing);





        List<Ui.ButtonObject> buttons = new ArrayList<>();
        buttons.add(shareButton);
        buttons.add(createButton);
        buttons.add(updateButton);
        buttons.add(memoButton);
        buttons.add(saveButton);

        return buttons;

    }

}