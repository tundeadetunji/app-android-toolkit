package com.inovationware.toolkit.features.code.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public final class VocabularyUnit {
    @Getter
    private String keyword;
    @Getter
    private String description;
    @Getter
    private String inYoruba;
    @Getter
    private String inBulgarian;

    private VocabularyUnit(String keyword, String description, String inYoruba, String inBulgarian) {
        this.keyword = keyword;
        this.description = description;
        this.inYoruba = inYoruba;
        this.inBulgarian = inBulgarian;
    }

    public static VocabularyUnit create(String keyword, String description, String inYoruba, String inBulgarian) {
        return new VocabularyUnit(keyword, description, "Ní Yorùbá:  " + inYoruba, "На български:  " + inBulgarian);
    }

    public static List<VocabularyUnit> listing(List<String> english, List<String> yoruba, List<String> bulgarian, List<String> description) {
        List<VocabularyUnit> result = new ArrayList<>();
        for (int i = 0; i < english.size(); i++) {
            result.add(create(english.get(i), description.get(i), yoruba.get(i), bulgarian.get(i)));
        }
        return result;
    }
}
