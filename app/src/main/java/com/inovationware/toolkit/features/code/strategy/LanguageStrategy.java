package com.inovationware.toolkit.features.code.strategy;

import com.inovationware.toolkit.features.code.domain.Language;
public interface LanguageStrategy {
    String[] getLanguages();

    String to(String s, Language src, Language dest);

    String to(String s, Language dest);

    Language detectLanguage(String s);

}
