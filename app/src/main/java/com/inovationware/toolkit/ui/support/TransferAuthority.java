package com.inovationware.toolkit.ui.support;

import static com.inovationware.generalmodule.Device.thereIsInternet;
import static com.inovationware.toolkit.common.domain.DomainObjects.AUDIO;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.EXCEL;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.MIME_TYPE_ANY;
import static com.inovationware.toolkit.common.domain.DomainObjects.MIME_TYPE_AUDIO;
import static com.inovationware.toolkit.common.domain.DomainObjects.MIME_TYPE_EXCEL;
import static com.inovationware.toolkit.common.domain.DomainObjects.MIME_TYPE_PDF;
import static com.inovationware.toolkit.common.domain.DomainObjects.MIME_TYPE_PICTURE;
import static com.inovationware.toolkit.common.domain.DomainObjects.MIME_TYPE_POWERPOINT;
import static com.inovationware.toolkit.common.domain.DomainObjects.MIME_TYPE_RAR;
import static com.inovationware.toolkit.common.domain.DomainObjects.MIME_TYPE_VIDEO;
import static com.inovationware.toolkit.common.domain.DomainObjects.MIME_TYPE_WORD;
import static com.inovationware.toolkit.common.domain.DomainObjects.PDF;
import static com.inovationware.toolkit.common.domain.DomainObjects.PICTURE;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_BLOB_AUDIO;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_BLOB_EXCEL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_BLOB_PDF;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_BLOB_PICTURE;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_BLOB_POWERPOINT;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_BLOB_RAR;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_BLOB_VIDEO;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_BLOB_WORD;
import static com.inovationware.toolkit.common.domain.DomainObjects.POWERPOINT;
import static com.inovationware.toolkit.common.domain.DomainObjects.RAR;
import static com.inovationware.toolkit.common.domain.DomainObjects.VIDEO;
import static com.inovationware.toolkit.common.domain.DomainObjects.WORD;
import static com.inovationware.toolkit.common.domain.DomainObjects.ttsServiceProvider;
import static com.inovationware.toolkit.common.utility.Code.content;
import static com.inovationware.toolkit.common.utility.Support.initialParamsAreSet;
import static com.inovationware.toolkit.common.utility.Support.responseStringIsValid;

import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.inovationware.toolkit.databinding.FragmentTransferBinding;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendFileRequest;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class TransferAuthority {
    private static final int BUF_SIZE = 4096;
    private final Factory factory;
    private final Context context;
    private final GroupManager machines;
    private final SharedPreferencesManager store;
    public TransferAuthority(Factory factory, Context context, GroupManager machines, SharedPreferencesManager store){
        this.factory = factory;
        this.context = context;
        this.machines = machines;
        this.store = store;
    }

    public boolean canSend(DomainObjects.SendFrom from) {
        if (from == DomainObjects.SendFrom.Clipboard) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
            return initialParamsAreSet(context, store, machines) && clipboard.hasText();
        } else {
            return initialParamsAreSet(context, store, machines); // && !isNothing(content(detailsTextView));
        }
    }
    public static byte[] getContentData(final Context context, final String uri) {
        if (uri == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = null;
        byte[] buf = new byte[BUF_SIZE];
        int len;
        try {
            ContentResolver r = context.getContentResolver();
            in = r.openInputStream(Uri.parse(uri));
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getPurpose(String s) {
        //s = s.trim();
        if (s.trim().equalsIgnoreCase(AUDIO)) {
            return POST_PURPOSE_BLOB_AUDIO;
        } else if (s.trim().equalsIgnoreCase(PDF)) {
            return POST_PURPOSE_BLOB_PDF;
        } else if (s.trim().equalsIgnoreCase(EXCEL)) {
            return POST_PURPOSE_BLOB_EXCEL;
        } else if (s.trim().equalsIgnoreCase(PICTURE)) {
            return POST_PURPOSE_BLOB_PICTURE;
        } else if (s.trim().equalsIgnoreCase(POWERPOINT)) {
            return POST_PURPOSE_BLOB_POWERPOINT;
        } else if (s.trim().equalsIgnoreCase(RAR)) {
            return POST_PURPOSE_BLOB_RAR;
        } else if (s.trim().equalsIgnoreCase(VIDEO)) {
            return POST_PURPOSE_BLOB_VIDEO;
        } else {
            return POST_PURPOSE_BLOB_WORD;
        }
    }
    public String getType(String s) {
        //s = s.trim();
        if (s.trim().equalsIgnoreCase(AUDIO)) {
            return MIME_TYPE_AUDIO;
        } else if (s.trim().equalsIgnoreCase(PDF)) {
            return MIME_TYPE_PDF;
        } else if (s.trim().equalsIgnoreCase(EXCEL)) {
            return MIME_TYPE_EXCEL;
        } else if (s.trim().equalsIgnoreCase(PICTURE)) {
            return MIME_TYPE_PICTURE;
        } else if (s.trim().equalsIgnoreCase(POWERPOINT)) {
            return MIME_TYPE_POWERPOINT;
        } else if (s.trim().equalsIgnoreCase(RAR)) {
            return MIME_TYPE_RAR;
        } else if (s.trim().equalsIgnoreCase(VIDEO)) {
            return MIME_TYPE_VIDEO;
        } else if (s.trim().equalsIgnoreCase(WORD)) {
            return MIME_TYPE_WORD;
        } else {
            return MIME_TYPE_ANY;
        }
    }

    public String getFilename(Uri uri) {
        String path = uri.getPath();
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return path.substring(lastSlashIndex + 1);
        } else {
            return path;
        }
        /*if (s.trim().isEmpty())
            return "no_given_title_but_automatically_added_txt_extension.txt";*/

        //return s.trim().replaceAll(".*/", "");

        /*s = s.trim();
        String result = store.getString(view.getContext(), SHARED_PREFERENCES_ID_KEY, EMPTY_STRING);
        if (s.equalsIgnoreCase(AUDIO)) {
            return result + ".mp3";
        } else if (s.equalsIgnoreCase(PDF)) {
            return result + ".pdf";
        } else if (s.equalsIgnoreCase(EXCEL)) {
            return result + ".xlsx";
        } else if (s.equalsIgnoreCase(PICTURE)) {
            return result + ".jpg";
        } else if (s.equalsIgnoreCase(POWERPOINT)) {
            return result + ".pptx";
        } else if (s.equalsIgnoreCase(RAR)) {
            return result + ".rar";
        } else if (s.equalsIgnoreCase(VIDEO)) {
            return result + ".mp4";
        } else if (s.equalsIgnoreCase(WORD)) {
            return result + ".docx";
        } else {
            return s;
        }*/
    }

    public void sendFile(Uri uri, TextView sendDropDown) {
        if (!thereIsInternet(context) || uri == null) return;

        factory.transfer.service.sendFile(
                context,
                store,
                machines,
                SendFileRequest.create(
                        HTTP_TRANSFER_URL(context, store),
                        store.getUsername(context),
                        store.getID(context),
                        Transfer.Intent.writeFile,
                        MultipartBody.Part.createFormData("file", getFilename(uri), RequestBody.create(MediaType.parse(getType(content(sendDropDown))), getContentData(context, String.valueOf(uri)))),
                        getFilename(uri),
                        getPurpose(content(sendDropDown))
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }

    public String displayInformationAboutSender(String sender) {
        //return sender.toLowerCase().contains(store.getString(view.getContext(), SHARED_PREFERENCES_SENDER).toLowerCase()) ? "previously sent from\nthis device" : "received from\n" + sender;
        return sender.toLowerCase().equalsIgnoreCase(store.getSender(context)) ? "previously sent from\nthis device" : "received from\n" + sender;

    }

    public void PerformReadOutLoud(String content) {
        if (responseStringIsValid(content.replace("\n", "").trim())) {
            try {
                ttsServiceProvider.say(content.replace("\n", "").trim());
            } catch (Exception ignored) {

            }
        }
    }

    public void openInBrowser(String url) {
        String result = url;
        //site:https://stackoverflow.com
        if (url.trim().startsWith("site")) {
            result = url.replace("site:", "");
        }
        //(site:https://stackoverflow.com OR site:https://docs.oracle.com) (working with arrays)
        else if (url.trim().startsWith("(")) {
            result = url.replace("(", "").replace(")", "").replace("site:", "");
        }

        try {
            context.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(result)));
        } catch (Exception ignored) {

        }
    }

    public String createReadFileUrl(String dto){
        return DomainObjects.BASE_URL(context, store) + "/api/regular.ashx?id=" + store.getID(context) + "&intent=" + Transfer.Intent.readFile + "&username=" + store.getUsername(context) + "&tag=" + getPurpose(dto);
    }
    public void openReceivedFile(String dto) {
        if (!thereIsInternet(context) || !initialParamsAreSet(context, store, machines)) return;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(createReadFileUrl(dto))));
    }
    /*public String resetText(String string) {
        String[] tokens = string.split("\n");
        return tokens.length > 1 ? tokens[0] + "\n" : string;
    }*/

}
