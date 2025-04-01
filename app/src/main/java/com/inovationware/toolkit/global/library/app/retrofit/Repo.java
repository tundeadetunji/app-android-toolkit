package com.inovationware.toolkit.global.library.app.retrofit;

import android.content.Context;

import com.inovationware.toolkit.global.domain.DomainObjects;
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
                .baseUrl(DomainObjects.BASE_URL(context, store))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(Retrofit.class);
    }

}
