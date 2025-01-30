package com.inovationware.toolkit.datatransfer.dto.request;

import com.inovationware.toolkit.global.domain.Transfer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.MultipartBody;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SendFileRequest {

    //    Call<String> sendFile(@Url String baseUrl, @Url String username, @Url String id, @Url String intent, @Part MultipartBody.Part file);
    private String baseUrl;
    private String username;
    private String id;
    private Transfer.Intent intent;
    private MultipartBody.Part file;
    private String filename;
    private String purpose;

    public static SendFileRequest create(
            String baseUrl,
            String username,
            String id,
            Transfer.Intent intent,
            MultipartBody.Part file,
            String filename,
            String purpose){
        return new SendFileRequest(
                baseUrl, username, id, intent, file, filename, purpose);
    }

}
