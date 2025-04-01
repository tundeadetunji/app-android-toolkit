package com.inovationware.toolkit.code.strategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.inovationware.toolkit.code.domain.Language;
import com.inovationware.toolkit.code.strategy.LanguageStrategy;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.domain.DomainObjects;

public class LanguageStrategyImpl implements LanguageStrategy {
    private final Factory factory = Factory.getInstance();
    private static LanguageStrategyImpl instance;

    private LanguageStrategyImpl() {
        initialize();
    }

    public static LanguageStrategyImpl getInstance() {
        if (instance == null) instance = new LanguageStrategyImpl();
        return instance;
    }

    @Override
    public String[] getLanguages() {
        List<String> result = new ArrayList<>();
        for (Language language : Language.values()) {
            result.add(language.name());
        }
        return result.toArray(new String[Language.values().length]);
    }

    @Override
    public String to(String s, Language src, Language dest) {
        String[] lines = s.split("\n");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            result.append(parseLine(lines[i], src, dest));
        }
        return result.toString();
    }

    @Override
    public String to(String s, Language dest) {
        return to(s, detectLanguage(s), dest);
    }

    @Override
    public Language detectLanguage(String s) {
        for (Map.Entry<Language, List<String>> entry : LanguageToWords.entrySet()) {
            if (entry.getValue().contains(factory.stringFunctions.firstWord(s))) {
                return entry.getKey();
            }
        }

        throw new NoSuchElementException(DomainObjects.TODO_NO_SUCH_ELEMENT_EXCEPTION);
    }

    private String parseLine(String s, Language src, Language dest) {
        String[] tokens = s.split(" ");
        StringBuilder string = new StringBuilder();
        for (String current : tokens) {
            String firstLetter = current.trim().substring(0, 1);
            if (braces.contains(firstLetter)) {
                String currentTrimmed = current.trim().substring(1);
                string.append(firstLetter).append(convert(currentTrimmed.trim(), src, dest)).append(" ");
            } else {
                string.append(convert(current.trim(), src, dest)).append(" ");
            }
        }
        return string.toString();
    }

    private String convert(String s, Language src, Language dest) {
        if (src == dest) return s;

        //English-Yoruba
        if (src == Language.English && dest == Language.Yoruba) {
            return EnglishYoruba.containsKey(s) ? EnglishYoruba.get(s) : s;
        }

        //English-Bulgarian
        /*else if (src == Language.English && dest == Language.Bulgarian) {
            return EnglishBulgarian.containsKey(s) ? EnglishBulgarian.get(s) : s;
        }*/


        //Yoruba-English
        else if (src == Language.Yoruba && dest == Language.English) {
            return YorubaEnglish.containsKey(s) ? YorubaEnglish.get(s) : s;
        }

        //Yoruba-Bulgarian
        /*else if (src == Language.Yoruba && dest == Language.Bulgarian) {
            return YorubaBulgarian.containsKey(s) ? YorubaBulgarian.get(s) : s;
        }*/

        //Bulgarian-English
        /*else if (src == Language.Bulgarian && dest == Language.English) {
            return BulgarianEnglish.containsKey(s) ? BulgarianEnglish.get(s) : s;
        }*/

        //Bulgarian-Yoruba
        /*else if (src == Language.Bulgarian && dest == Language.Yoruba) {
            return BulgarianYoruba.containsKey(s) ? BulgarianYoruba.get(s) : s;
        }*/


        throw new RuntimeException("ToDo - give explanation");

    }


    private void initialize() {
        for (int i = 0; i < factory.vocabulary.English.size(); i++) {
            EnglishYoruba.put(factory.vocabulary.English.get(i), factory.vocabulary.Yoruba.get(i));
            EnglishBulgarian.put(factory.vocabulary.English.get(i), factory.vocabulary.Bulgarian.get(i));

            YorubaEnglish.put(factory.vocabulary.Yoruba.get(i), factory.vocabulary.English.get(i));
            YorubaBulgarian.put(factory.vocabulary.Yoruba.get(i), factory.vocabulary.Bulgarian.get(i));

            BulgarianEnglish.put(factory.vocabulary.Bulgarian.get(i), factory.vocabulary.English.get(i));
            BulgarianYoruba.put(factory.vocabulary.Bulgarian.get(i), factory.vocabulary.Yoruba.get(i));

        }
    }


    private Map<String, String> EnglishYoruba = new HashMap<>();
    private Map<String, String> EnglishBulgarian = new HashMap<>();

    private Map<String, String> YorubaEnglish = new HashMap<>();
    private Map<String, String> YorubaBulgarian = new HashMap<>();

    private Map<String, String> BulgarianEnglish = new HashMap<>();
    private Map<String, String> BulgarianYoruba = new HashMap<>();

    private static List<String> braces = List.of("{", "(", "[", "}", ")", "]");

    private Map<Language, List<String>> LanguageToWords = Map.of(
            Language.English, factory.vocabulary.English,
            Language.Yoruba, factory.vocabulary.Yoruba
            //Language.Bulgarian, factory.vocabulary.Bulgarian
    );
}
