package com.inovationware.toolkit.memo.title;

import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_MEMO_TITLES_KEY;

import android.content.Context;

import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;

import java.util.Arrays;

public class TitleService {
    public String[] listing(Context context, SharedPreferencesManager store){
        String saved = store.getString(context, SHARED_PREFERENCES_MEMO_TITLES_KEY);
        if (saved.isEmpty()) return new String[0];
        String[] result =  saved.split("\n");
        Arrays.sort(result, String.CASE_INSENSITIVE_ORDER);
        return result;
    }
    public void addTitle(Context context, String title, SharedPreferencesManager store){
        if (title.isEmpty()) return;
        for (String saved : listing(context, store)){
            if (saved.trim().equalsIgnoreCase(title.trim())) return;
        }
        store.setString(context, SHARED_PREFERENCES_MEMO_TITLES_KEY, store.getString(context, SHARED_PREFERENCES_MEMO_TITLES_KEY) + "\n" + title.trim());
    }

    public void clearTitles(Context context, SharedPreferencesManager store){
        store.setString(context, SHARED_PREFERENCES_MEMO_TITLES_KEY,"");
    }
}
