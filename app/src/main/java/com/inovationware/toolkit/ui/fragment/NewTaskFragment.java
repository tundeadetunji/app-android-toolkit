package com.inovationware.toolkit.ui.fragment;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;
import static com.inovationware.toolkit.global.library.utility.Support.initialParamsAreSet;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.FragmentNewTaskBinding;
import com.inovationware.toolkit.databinding.FragmentRemoteTasksBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.nettimer.model.NetTimerTaskDtoFromNetwork;
import com.inovationware.toolkit.nettimer.model.NetTimerToDo;

import java.io.IOException;

import lombok.SneakyThrows;

public class NewTaskFragment extends Fragment {

    private View view;
    private FragmentNewTaskBinding binding;

    private Factory factory;
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewTaskBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        setupAccess();
        setupUi();
        setupListeners();

        return view;
    }
    private void setupAccess(){
        context = view.getContext();
        factory = Factory.getInstance();
    }

    private void setupListeners(){
        binding.ntoSendButton.setOnClickListener(sendButtonOnClickListener);
        binding.ntoRequirementTextView.addTextChangedListener(requirementsTextWatcher);
    }
    private void setupUi(){
        factory.ui.bindProperty(context, binding.ntoToDoDropDown, NetTimerToDo.listing());
        binding.ntoAfterTextField.setText("1");
    }

    private final View.OnClickListener sendButtonOnClickListener = new View.OnClickListener() {
        @SneakyThrows
        @Override
        public void onClick(View v) {
            if (binding.ntoToDoDropDown.getText().toString().isEmpty()) return;
            if (binding.ntoRequirementTextView.getText().toString().isEmpty()) return;
            if (binding.ntoDisplayTextField.getText().toString().isEmpty()) return;
            if (!thereIsInternet(view.getContext())) return;

            binding.ntoSendButton.setEnabled(false);

            sendTaskToBase(NetTimerTaskDtoFromNetwork.create(
                    NetTimerToDo.fromLabel(binding.ntoToDoDropDown.getText().toString()),
                    binding.ntoRequirementTextView.getText().toString(),
                    Integer.parseInt(binding.ntoAfterTextField.getText().toString()),
                    binding.ntoDisplayTextField.getText().toString(),
                    binding.ntoRemindCheckBox.isChecked(),
                    binding.ntoRepeatCheckBox.isChecked()
            ),
                    SharedPreferencesManager.getInstance(),
                    GroupManager.getInstance());
            binding.ntoSendButton.setEnabled(true);
        }
    };
    private final TextWatcher requirementsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (binding.ntoRequirementTextView.getText().toString().isEmpty()) return;
            if (binding.ntoToDoDropDown.getText().toString().isEmpty()) return;
            if (!binding.ntoToDoDropDown.getText().toString().equalsIgnoreCase(NetTimerToDo.READ_OUT_LOUD.getLabel())) return;
            if(!Code.isPhraseOrSentence(binding.ntoRequirementTextView.getText().toString())) return;

            try {
                binding.ntoDisplayTextField.setText(
                        Code.TransformText(
                                "You Will Be " + Code.toContinuousTense(Code.firstWord(binding.ntoRequirementTextView.getText().toString()), Code.otherWords(binding.ntoRequirementTextView.getText().toString()))
                        )
                );
            }catch (Exception ignored){

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void sendTaskToBase(NetTimerTaskDtoFromNetwork dto, SharedPreferencesManager store, GroupManager machines) throws IOException {
        if (!initialParamsAreSet(context, store, machines)) return;

        String info = NetTimerTaskDtoFromNetwork.toJson(dto);

        Factory.getInstance().transfer.service.sendText(
                context,
                store,
                machines,
                SendTextRequest.create(
                        HTTP_TRANSFER_URL(view.getContext(), store),
                        store.getUsername(context),
                        store.getID(context),
                        Transfer.Intent.writeText,
                        store.getSender(context),
                        determineTarget(view.getContext(), store, machines),
                        DomainObjects.POST_PURPOSE_NEW_NET_TIMER_TASK,
                        DomainObjects.EMPTY_STRING,
                        info,
                        DomainObjects.EMPTY_STRING),
                DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX,
                DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX
        );

        clearUi();
    }

    private void clearUi(){
        binding.ntoRequirementTextView.setText(DomainObjects.EMPTY_STRING);
        binding.ntoDisplayTextField.setText(DomainObjects.EMPTY_STRING);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}