package com.inovationware.toolkit.global.library.auxiliary;

import lombok.Getter;

public class LoginManager {
    private static LoginManager instance;
    private LoginManager(){}

    public static LoginManager getInstance(){
        if(instance == null) instance = new LoginManager();
        return instance;
    }

    @Getter
    private boolean loggedIn = true;

    public boolean userLoggedIn(){
        //after successfully logging in through GoogleSignIn
        //set loggedIn to true;
        throw new RuntimeException("This would be after GoogleSignIn");
    }

}
