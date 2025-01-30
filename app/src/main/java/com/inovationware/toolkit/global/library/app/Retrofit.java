package com.inovationware.toolkit.global.library.app;

import com.inovationware.toolkit.global.domain.Strings;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface Retrofit {
    @GET()
    Call<String> readText(@Url String baseUrl,
                          @Query("username") String username,
                          @Query("id") String id,
                          @Query("intent") String intent,
                          @Query("tag") String tag); //meetingId

    @FormUrlEncoded
    @POST()
    Call<String> readNote(@Url String baseUrl,
                          @Query("username") String username,
                          @Query("id") String id,
                          @Query("intent") String intent,
                          @Field("Purpose") String purpose);

    @FormUrlEncoded
    @POST()
    Call<String> sendText(@Url String baseUrl,
                          @Query("username") String username,
                          @Query("id") String id,
                          @Query("intent") String intent,
                          @Field("Sender") String sender,
                          @Field("Target") String target,
                          @Field("Purpose") String purpose,
                          @Field("Meta") String meta,
                          @Field("Info") String info,
                          @Query("tag") String tag);

    @FormUrlEncoded
    @POST()
    Call<String> sendMeetingContribution(@Url String baseUrl,
                                         @Query("username") String username,
                                         @Query("id") String id,
                                         @Query("intent") String intent,
                                         @Field("Sender") String sender,
                                         @Field("Target") String target,
                                         @Field("Purpose") String purpose,
                                         @Field("Meta") String meta,
                                         @Field("Info") String info,
                                         @Query("tag") String tag,
                                         @Query("meetingId") String meetingId);

    @FormUrlEncoded
    @POST()
    Call<String> sendNote(@Url String baseUrl,
                          @Query("username") String username,
                          @Query("id") String id,
                          @Query("intent") String intent,
                          @Field("Sender") String sender,
                          @Field("Target") String target,
                          @Field("Purpose") String purpose,
                          @Field("Meta") String meta,
                          @Field("Info") String info,
                          @Field("note") String note,
                          @Field("noteId") String noteId,
                          @Field("noteTitle") String noteTitle,
                          @Field("noteDate") String noteDate,
                          @Field("noteTime") String noteTime);

    @FormUrlEncoded
    @POST()
    Call<String> deleteNote(@Url String baseUrl,
                            @Query("username") String username,
                            @Query("id") String id,
                            @Query("intent") String intent,
                            @Field("Sender") String sender,
                            @Field("Target") String target,
                            @Field("Purpose") String purpose,
                            @Field("Meta") String meta,
                            @Field("Info") String info,
                            @Field("noteId") String noteId);

    @Multipart
    @POST()
    Call<String> sendFile(@Url String baseUrl,
                          @Query("username") String username,
                          @Query("id") String id,
                          @Query("intent") String intent,
                          @Part MultipartBody.Part file,
                          @Field(Strings.PostHeaderFilename) String filename,
                          @Field(Strings.PostHeaderTimestamp) String timestamp,
                          @Field("Purpose") String purpose);

    @GET()
    Call<String> readBlob(@Url String baseUrl,
                          @Query("username") String username,
                          @Query("id") String id,
                          @Query("intent") String intent);

}
