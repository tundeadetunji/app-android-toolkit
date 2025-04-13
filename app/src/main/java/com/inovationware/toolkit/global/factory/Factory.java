package com.inovationware.toolkit.global.factory;

import com.inovationware.toolkit.code.verb.Vocabulary;
import com.inovationware.toolkit.global.library.app.EventHandlers;
import com.inovationware.toolkit.global.library.app.FeedbackManager;
import com.inovationware.toolkit.global.library.external.EncoderLite;
import com.inovationware.toolkit.global.library.app.GlideClient;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.auxiliary.LoginManager;
import com.inovationware.toolkit.datatransfer.service.DataTransferService;
import com.inovationware.toolkit.datatransfer.service.rest.RestDataTransferService;
import com.inovationware.toolkit.datatransfer.strategy.rest.RestDataTransferStrategy;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.global.library.utility.StringFunctions;
import com.inovationware.toolkit.global.library.utility.Ui;
import com.inovationware.toolkit.memo.title.TitleService;
import com.inovationware.toolkit.tts.service.TTSService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Factory {
    public TransferFactory transfer = TransferFactory.getInstance();
    public WorksFactory works = WorksFactory.getInstance();
    public FeedbackManagerFactory feedback = FeedbackManagerFactory.getInstance();

    public EncryptionFactory encryption = EncryptionFactory.getInstance();

    public UserLoginFactory user = UserLoginFactory.getInstance();
    public GlideFactory image = GlideFactory.getInstance();
    public StringFunctions stringFunctions = StringFunctions.getInstance();

    public Vocabulary vocabulary = Vocabulary.getInstance();
    public Ui ui = Ui.getInstance();
    public DeviceClient device = DeviceClient.getInstance();
    public EventHandlerFactory events = EventHandlerFactory.getInstance();
    public MemoFactory memo = MemoFactory.getInstance();


    private static Factory instance;

    public static Factory getInstance() {
        if (instance == null) instance = new Factory();
        return instance;
    }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MemoFactory {
        private static MemoFactory instance;
        public static MemoFactory getInstance(){
            if (instance == null) instance = new MemoFactory();
            return instance;
        }

        public final TitleService titles = new TitleService();
    }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EventHandlerFactory {
        private static EventHandlerFactory instance;

        public static EventHandlerFactory getInstance() {
            if (instance == null) instance = new EventHandlerFactory();
            return instance;
        }

        public final EventHandlers handlers = EventHandlers.getInstance();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GlideFactory {
        private static GlideFactory instance;

        public static GlideFactory getInstance() {
            if (instance == null) instance = new GlideFactory();
            return instance;
        }

        public final GlideClient service = GlideClient.getInstance();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EncryptionFactory {
        private static EncryptionFactory instance;

        public static EncryptionFactory getInstance() {
            if (instance == null) instance = new EncryptionFactory();
            return instance;
        }

        public final EncoderLite service = EncoderLite.getInstance();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TransferFactory {
        private static TransferFactory instance;

        public static TransferFactory getInstance() {
            if (instance == null) instance = new TransferFactory();
            return instance;
        }

        public final DataTransferService service = RestDataTransferService.getInstance(RestDataTransferStrategy.getInstance());
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FeedbackManagerFactory {
        private static FeedbackManagerFactory instance;

        public static FeedbackManagerFactory getInstance() {
            if (instance == null) instance = new FeedbackManagerFactory();
            return instance;
        }

        public final FeedbackManager service = new FeedbackManager();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserLoginFactory {
        private static UserLoginFactory instance;

        public static UserLoginFactory getInstance() {
            if (instance == null) instance = new UserLoginFactory();
            return instance;
        }

        public final LoginManager service = LoginManager.getInstance();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WorksFactory {
        private static WorksFactory instance;

        public static WorksFactory getInstance() {
            if (instance == null) instance = new WorksFactory();
            return instance;
        }

        public static final GroupManager groupManager = GroupManager.getInstance();
        public static final SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance();
    }

}

