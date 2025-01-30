package com.inovationware.toolkit.global.repository;

import android.content.Context;

import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.library.app.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;

import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Repo {
    private static Repo instance;

    public static Repo getInstance() {
        if (instance == null) instance = new Repo();
        return instance;
    }

    private Repo() {

    }
    public final Retrofit create(Context context, SharedPreferencesManager store){
        return new retrofit2.Retrofit.Builder()
                .baseUrl(Strings.BASE_URL(context, store))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(Retrofit.class);
    }

}
