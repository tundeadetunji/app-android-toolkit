package com.inovationware.toolkit.global.library.app;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.ui.activity.LoginActivity;

import lombok.Getter;

public class SignInManager {
    private GoogleSignInClient mGoogleSignInClient;

    private static SignInManager instance;
    public static SignInManager getInstance(){
        if(instance == null) instance = new SignInManager();
        return  instance;
    }

    private SignInManager(){

    }

    /*@Getter
    private GoogleSignInAccount principal;*/

    public Principal getSignedInUser(Context context) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        return acct != null ? Principal.create(acct.getDisplayName()) : null;
    }
    public boolean isLoggedIn(Context context) {
        return getSignedInUser(context) != null;
    }

    public boolean thereIsPrincipal(Context context){
        return GoogleSignIn.getLastSignedInAccount(context) != null;
    }

    public void beginLoginProcess(Context context, String value) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(DomainObjects.DTO_CLASS_STRING, value);
        startActivity(context, intent, null);
    }

    public static class Principal {

        @Getter
        private String name;

        private Principal(String name){
            this.name = name;
        }

        public static Principal create(String name){
            return new Principal(name);
        }
    }
}
