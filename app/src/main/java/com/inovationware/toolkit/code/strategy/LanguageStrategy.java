package com.inovationware.toolkit.code.strategy;

import com.inovationware.toolkit.code.domain.Language;
public interface LanguageStrategy {
    String[] getLanguages();

    String to(String s, Language src, Language dest);

    String to(String s, Language dest);

    Language detectLanguage(String s);

}
