package com.inovationware.toolkit.code.service;

import com.inovationware.toolkit.code.domain.Language;

public interface LanguageService{
    String[] getLanguages();

    String to(String s, Language src, Language dest);

    String to(String s, Language dest);

    Language detectLanguage(String s);

    //Noun.ProgrammingLanguage detectProgrammingLanguage(String s);

}
