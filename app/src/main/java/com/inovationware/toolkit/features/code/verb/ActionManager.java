package com.inovationware.toolkit.features.code.verb;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActionManager {

    private static ActionManager instance;

    public static ActionManager getInstance(){
        if(instance == null) instance = new ActionManager();
        return instance;
    }

    public enum Action {
        I_WANT_SOMETHING,
        I_WANT_SOMEONE_TO_HAVE_SOMETHING,
        I_WANT_SOMEONE_TO_DO_SOMETHING,
        I_DID_SOMETHING,
        SOMEONE_DID_SOMETHING,
        COMPLIMENT,
        CONFIRM,
        GREET
    }

    public String[] getActions(){
        List<String> temp = new ArrayList<>();
        for(Action action : Action.values()){
            temp.add(action.name());
        }
        return temp.toArray(new String[Action.values().length]);
    }


}
