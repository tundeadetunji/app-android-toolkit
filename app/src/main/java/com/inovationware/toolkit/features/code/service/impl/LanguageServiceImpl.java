package com.inovationware.toolkit.features.code.service.impl;

import com.inovationware.toolkit.features.code.domain.Language;
import com.inovationware.toolkit.features.code.service.LanguageService;
import com.inovationware.toolkit.features.code.strategy.LanguageStrategy;

public class LanguageServiceImpl implements LanguageService {
    private LanguageStrategy strategy;

    private static LanguageServiceImpl instance;
    public static LanguageServiceImpl getInstance(LanguageStrategy strategy){
        if(instance == null) instance = new LanguageServiceImpl(strategy);
        return instance;
    }
    private LanguageServiceImpl(LanguageStrategy strategy){
        this.strategy = strategy;
    }

    @Override
    public String[] getLanguages() {
        return strategy.getLanguages();
    }

    @Override
    public String to(String s, Language src, Language dest) {
        return strategy.to(s, src, dest);
    }

    @Override
    public String to(String s, Language dest) {
        return strategy.to(s, dest);
    }

    @Override
    public Language detectLanguage(String s) {
        return strategy.detectLanguage(s);
    }
}
