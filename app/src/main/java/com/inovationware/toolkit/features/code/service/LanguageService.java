package com.inovationware.toolkit.features.code.service;

import com.inovationware.toolkit.features.code.domain.Language;

public interface LanguageService{
    String[] getLanguages();

    String to(String s, Language src, Language dest);

    String to(String s, Language dest);

    Language detectLanguage(String s);

    //Noun.ProgrammingLanguage detectProgrammingLanguage(String s);

}
