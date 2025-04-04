package com.inovationware.toolkit.memo.model;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.external.Json;
import com.inovationware.toolkit.global.library.utility.Code;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import retrofit2.Response;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class Memo {
    @JsonProperty
    private String noteId;
    @JsonProperty
    private String noteTitle;
    @JsonProperty
    private String postnote;
    @JsonProperty
    private String sender;
    @JsonProperty
    private String target;
    @JsonProperty
    private String noteDate;
    @JsonProperty
    private String noteTime;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

    public static Memo create(String text, Context context, SharedPreferencesManager store) {
        Date date = new Date();
        return new Memo(
                createNoteId(),
                getTitleFromRawText(text, store, context),
                getNoteFromRawText(text),
                store.getSender(context),
                store.getID(context),
                dateFormat.format(date),
                timeFormat.format(date)
        );
    }

    public static Memo create(String title, String note, Context context, SharedPreferencesManager store) {
        Date date = new Date();
        return new Memo(
                createNoteId(),
                title,
                note,
                store.getSender(context),
                store.getID(context),
                dateFormat.format(date),
                timeFormat.format(date)
        );
    }

    public static List<Memo> listing(Response<String> response) throws IOException {
        List<Memo> memos = new ArrayList<>();

        if (response.body() == null) return memos;
        if (response.body().isEmpty()) return memos;

        memos = Json.asList(response.body(), new TypeReference<List<Memo>>() {
        });
        //Todo Comparator of int, using RecordSerial
        //Collections.sort(memos, this);
        return memos;
    }

    public static String createNoteId() {
        return Code.newGUID(Code.IDPattern.Short);
    }

    public static String getNoteFromRawText(String rawText) {
        String[] lines = rawText.split("\n");
        if (lines.length == 1) return rawText;
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < lines.length; i++) {
            result.append(lines[i]).append("\n");
        }
        return result.toString();
    }

    public static String getTitleFromRawText(String rawText, SharedPreferencesManager store, Context context) {
        String[] lines = rawText.split("\n");
        return lines.length > 1 ? lines[0] : store.getDefaultNoteTitle(context);
    }

    // Comparator to compare by noteDate and then by noteTime in reverse order
    public static class MemoComparator implements Comparator<Memo> {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy");
        private static final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        @Override
        public int compare(Memo m1, Memo m2) {
            try {
                // Compare by noteDate in reverse order
                Date date1 = dateFormat.parse(m1.getNoteDate());
                Date date2 = dateFormat.parse(m2.getNoteDate());
                int dateComparison = date2.compareTo(date1); // Reverse order

                if (dateComparison != 0) {
                    return dateComparison; // Return if dates are different
                }

                // If dates are the same, compare by noteTime in reverse order
                Date time1 = timeFormat.parse(m1.getNoteTime());
                Date time2 = timeFormat.parse(m2.getNoteTime());
                return time2.compareTo(time1); // Reverse order
            } catch (ParseException e) {
                throw new RuntimeException("Date parsing error", e);
            }
        }
    }

    /*private static class MemoComparator implements Comparator<Memo>{

        @SneakyThrows
        @Override
        public int compare(Memo o1, Memo o2) {
            String lowerDateString = o1.noteDate;
            String lowerTimeString = o1.noteTime;

            Date lowerDate = dateFormat.parse(lowerDateString);
            Date lowerTime = timeFormat.parse(lowerTimeString);

            Calendar lowerCalendar = Calendar.getInstance();
            lowerCalendar.setTime(lowerDate);
            lowerCalendar.set(Calendar.HOUR_OF_DAY, lowerTime.getHours());
            lowerCalendar.set(Calendar.MINUTE, lowerTime.getMinutes());
            lowerCalendar.set(Calendar.AM_PM, lowerTime.getHours() >= 12 ? Calendar.PM : Calendar.AM);

            Date lower = lowerCalendar.getTime();

            String upperDateString = o2.noteDate;
            String upperTimeString = o2.noteTime;

            Date upperDate = dateFormat.parse(upperDateString);
            Date upperTime = timeFormat.parse(upperTimeString);

            Calendar upperCalendar = Calendar.getInstance();
            upperCalendar.setTime(upperDate);
            upperCalendar.set(Calendar.HOUR_OF_DAY, upperTime.getHours());
            upperCalendar.set(Calendar.MINUTE, upperTime.getMinutes());
            upperCalendar.set(Calendar.AM_PM, upperTime.getHours() >= 12 ? Calendar.PM : Calendar.AM);

            Date upper = upperCalendar.getTime();

            return upper.after(lower) ? -1 : upper.equals(lower) ? 0 : 1;
        }
    }*/
}


