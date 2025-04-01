package com.inovationware.toolkit.code.verb;

import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.domain.DomainObjects;

import java.util.List;

import lombok.Builder;

public class Structure {
    private static Structure instance;
    private Structure(){}
    public static Structure getInstance(){
        if(instance == null) instance = new Structure();
        return instance;
    }
    private static final Factory factory = Factory.getInstance();

    public Tokens from(String action){
        if (action.equalsIgnoreCase(ActionManager.Action.I_WANT_SOMETHING.name())){
            return Tokens.builder()
                    .subject(factory.vocabulary.self.toArray(new String[factory.vocabulary.self.size()]))
                    .linker(factory.vocabulary.linker.toArray(new String[factory.vocabulary.linker.size()]))
                    .object(combineLists(factory.vocabulary.noun, factory.vocabulary.pronoun).toArray(new String[factory.vocabulary.noun.size() + factory.vocabulary.pronoun.size()]))
                    .postfix(factory.vocabulary.postfix.toArray(new String[factory.vocabulary.postfix.size()]))
                    .build();
        }
        else if (action.equalsIgnoreCase(ActionManager.Action.I_WANT_SOMEONE_TO_HAVE_SOMETHING.name())){
            return Tokens.builder()
                    .subject(factory.vocabulary.pronoun.toArray(new String[factory.vocabulary.pronoun.size()]))
                    .linker(factory.vocabulary.linker.toArray(new String[factory.vocabulary.linker.size()]))
                    .object(combineLists(factory.vocabulary.noun, factory.vocabulary.pronoun).toArray(new String[factory.vocabulary.noun.size() + factory.vocabulary.pronoun.size()]))
                    .postfix(factory.vocabulary.postfix.toArray(new String[factory.vocabulary.postfix.size()]))
                    .build();
        }
        else if (action.equalsIgnoreCase(ActionManager.Action.I_WANT_SOMEONE_TO_DO_SOMETHING.name())){
            return Tokens.builder()
                    .subject(factory.vocabulary.pronoun.toArray(new String[factory.vocabulary.pronoun.size()]))
                    .linker(factory.vocabulary.verb.toArray(new String[factory.vocabulary.verb.size()]))
                    .object(new String[]{})
                    .postfix(factory.vocabulary.postfix.toArray(new String[factory.vocabulary.postfix.size()]))
                    .build();
        }
        else if (action.equalsIgnoreCase(ActionManager.Action.I_DID_SOMETHING.name())){
            return Tokens.builder()
                    .subject(factory.vocabulary.self.toArray(new String[factory.vocabulary.self.size()]))
                    .linker(factory.vocabulary.verb.toArray(new String[factory.vocabulary.verb.size()]))
                    .object(combineLists(factory.vocabulary.noun, factory.vocabulary.pronoun).toArray(new String[factory.vocabulary.noun.size() + factory.vocabulary.pronoun.size()]))
                    .postfix(factory.vocabulary.postfix.toArray(new String[factory.vocabulary.postfix.size()]))
                    .build();
        }
        else if (action.equalsIgnoreCase(ActionManager.Action.SOMEONE_DID_SOMETHING.name())){
            return Tokens.builder()
                    .subject(factory.vocabulary.pronoun.toArray(new String[factory.vocabulary.pronoun.size()]))
                    .linker(factory.vocabulary.verb.toArray(new String[factory.vocabulary.verb.size()]))
                    .object(new String[]{})
                    .postfix(factory.vocabulary.postfix.toArray(new String[factory.vocabulary.postfix.size()]))
                    .build();
        }
        else if (action.equalsIgnoreCase(ActionManager.Action.COMPLIMENT.name())){
            return Tokens.builder()
                    .subject(factory.vocabulary.pronoun.toArray(new String[factory.vocabulary.pronoun.size()]))
                    .linker(factory.vocabulary.adjective.toArray(new String[factory.vocabulary.adjective.size()]))
                    .object(new String[]{})
                    .postfix(factory.vocabulary.postfix.toArray(new String[factory.vocabulary.postfix.size()]))
                    .build();
        }
        else if (action.equalsIgnoreCase(ActionManager.Action.CONFIRM.name())){
            return Tokens.builder()
                    .subject(factory.vocabulary.confirmation.toArray(new String[factory.vocabulary.confirmation.size()]))
                    .linker(new String[]{})
                    .object(new String[]{})
                    .postfix(factory.vocabulary.postfix.toArray(new String[factory.vocabulary.postfix.size()]))
                    .build();
        }
        else if (action.equalsIgnoreCase(ActionManager.Action.GREET.name())){
            return Tokens.builder()
                    .subject(factory.vocabulary.greeting.toArray(new String[factory.vocabulary.greeting.size()]))
                    .linker(new String[]{})
                    .object(new String[]{})
                    .postfix(new String[]{})
                    .build();
        }

        throw new RuntimeException(DomainObjects.TODO_ERROR_VALIDATION_MSG);
    }

    private List<String> combineLists(List<String> i, List<String> j){
        i.addAll(j);
        return i;

    }

    @Builder
    public static class Tokens{
        public String[] subject;
        public String[] linker;
        public String[] object;
        public String[] postfix;

    }
}
