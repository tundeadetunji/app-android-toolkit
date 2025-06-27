package com.inovationware.toolkit.features.code.domain;

public class Phrase {

    public final String TODO_NO_SUCH_ELEMENT_EXCEPTION = "ToDo: NoSuchElementException";
    public final String EMPTY_STRING = "";
    public final String TODO_ERROR_VALIDATION_MSG = "TODO_ERROR_VALIDATION_MSG";
    public final String SPACE = " ";
    public final String ASTERISK = "*";
    public final String BEGIN_HTML_TAG = "<";
    public final String END_HTML_TAG = ">";
    public final String HTML_FILENAME = "index.html";
    public final String JAVA_FILENAME = "main.java";
    public final String WRITE_FILE_SUCCEEDED = "File has been created.";
    public final String WRITE_FILE_FAILED = "That resulted in an error. Ensure file was created.";


    private static Phrase instance;
    private Phrase(){}
    public static Phrase getInstance(){
        if(instance == null) instance = new Phrase();
        return instance;
    }


    public final String ABOUT_TEXT = "by Tunde Adetunji\nmade in the year 2024";
    public final String ABOUT_URL = "https://www.linkedin.com/in/tundeadetunji";

}
