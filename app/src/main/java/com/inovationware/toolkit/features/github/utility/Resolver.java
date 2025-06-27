package com.inovationware.toolkit.features.github.utility;

import com.inovationware.toolkit.features.github.domain.app.GithubOperation;

import lombok.Getter;

public class Resolver {
    private static Resolver instance;
    public static Resolver getInstance(){
        if(instance == null) instance = new Resolver();
        return instance;
    }

    public Hints resolveHints(GithubOperation operation){
        switch (operation){
            case Create_Text_File:
                return new Hints(Hints.PATH, Hints.CONTENT, Hints.COMMIT_MESSAGE, Hints.LEAVE_BLANK);
            case Create_Gist:
                return new Hints(Hints.PATH, Hints.CONTENT, Hints.DESCRIPTION, Hints.IS_PUBLIC);
            case Read_Text_File:
                return new Hints(Hints.PATH, Hints.LEAVE_BLANK, Hints.LEAVE_BLANK, Hints.LEAVE_BLANK);
            case Create_Repository:
                return new Hints(Hints.REPOSITORY, Hints.DESCRIPTION, Hints.HOME_PAGE, Hints.IS_PUBLIC);
        }
        //case Read_ReadMe:
        return new Hints(Hints.LEAVE_BLANK, Hints.LEAVE_BLANK, Hints.LEAVE_BLANK, Hints.LEAVE_BLANK);
    }

    @Getter
    public static class Hints{
        private static final String REPOSITORY = "Name of repository";
        private static final String HOME_PAGE = "Homepage url";
        private static final String PATH = "Remote file path";
        private static final String CONTENT = "Content of the file";
        private static final String COMMIT_MESSAGE = "Commit message";
        private static final String DESCRIPTION = "Description";
        private static final String IS_PUBLIC = "Is this public?";
        private static final String LEAVE_BLANK = "Leave this blank";


        private String stringParam1Hint;
        private String stringParam2Hint;
        private String stringParam3Hint;
        private String booleanParamHint;

        public Hints(String stringParam1Hint, String stringParam2Hint, String stringParam3Hint, String booleanParamHint){
            this.stringParam1Hint = stringParam1Hint;
            this.stringParam2Hint = stringParam2Hint;
            this.stringParam3Hint = stringParam3Hint;
            this.booleanParamHint = booleanParamHint;
        }
    }
}
