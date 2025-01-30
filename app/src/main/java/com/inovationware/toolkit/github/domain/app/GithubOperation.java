package com.inovationware.toolkit.github.domain.app;

public enum GithubOperation {
    Create_File,
    Create_Gist,
    Create_Repository,
    Create_Text_File,
    Read_File,
    Read_ReadMe,
    Read_Text_File;

    private static final String UNDERSCORE = "_";
    private static final String SPACE = " ";

    public static String to(GithubOperation operation){
        return operation.name().replace(UNDERSCORE, SPACE);
    }

    public static GithubOperation from(String operation){
        return GithubOperation.valueOf(operation.replace(SPACE, UNDERSCORE));
    }

    public static String[] listing(){
        String[] result = new String[GithubOperation.values().length];
        for(int i = 0; i < GithubOperation.values().length; i++){
            result[i] = to(GithubOperation.values()[i]);
        }
        return result;
    }
}
