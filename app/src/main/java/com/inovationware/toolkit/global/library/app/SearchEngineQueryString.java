package com.inovationware.toolkit.global.library.app;

import com.inovationware.toolkit.global.library.utility.Code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.inovationware.toolkit.global.library.utility.Code.isPhraseOrSentence;

public class SearchEngineQueryString {
    public static class TermWithVariations {
        private String _term;
        private List<String> _variations = new ArrayList<>();
        private TermWithVariations _instance;
        private Code.SearchStringOperator _operator = Code.SearchStringOperator.OR_;

        public TermWithVariations(String _term) {
            this._term = _term;
        }

        public TermWithVariations(String _term, Code.SearchStringOperator _operator) {
            this._term = _term;
            this._operator = _operator;
        }

        public void addVariation(String variation) {
            assert variation != null;
            assert variation.length() > 0;
            if (!_variations.contains(variation)) _variations.add(variation);
        }

        public void removeVariation(String variation){
            assert variation != null;
            assert variation.length() > 0;
            if (_variations.contains(variation)) _variations.remove(variation);
        }

        public List<String> variations(){
            return Collections.unmodifiableList(_variations);
        }

        public void clearVariations(){
            _variations = new ArrayList<>();
        }

        @Override
        public String toString() {
            return new StringBuilder(_term).toString();
        }

        public String name(){
            return new StringBuilder(_term).toString();
        }

        public Code.SearchStringOperator booleanOperatorIs(){
            return _operator;
        }

        public void die(){
            _instance = null;
        }
    }

    public String constructParameterString(List<String> parameters){
        StringBuilder result = new StringBuilder().append(parameters.size() > 1 ? "(" : "");
        for(int i = 0; i < parameters.size(); i++){
            result.append(isPhraseOrSentence(parameters.get(i)) ? "(" + parameters.get(i) + ")" : parameters.get(i))
                    .append(i < parameters.size() ? " OR " : "");
        }
        return parameters.size() > 1 ? result.append(")").toString() : result.toString();
    }

    public String constructParameterString(List<String> parameters, Code.SearchStringOperator boolean_operator){
        StringBuilder result = new StringBuilder().append(parameters.size() > 1 ? "(" : "");
        for(int i = 0; i < parameters.size(); i++){
            result.append(isPhraseOrSentence(parameters.get(i)) ? "(" + parameters.get(i) + ")" : parameters.get(i))
                    .append(i < parameters.size() ? " " + boolean_operator.toString().replace("_","") + " " : "");
        }
        return parameters.size() > 1 ? result.append(")").toString() : result.toString();
    }

    public String constructParameterString(String parameter){
        return "(" + parameter + ")";
    }

    public String constructSiteString(String site){
        return "site:" + site;
    }

    public String constructSiteString(List<String> sites){
        StringBuilder result = new StringBuilder().append(sites.size() > 1 ? "(" : "");
        for(int i = 0; i < sites.size(); i++){
            result.append(constructSiteString(sites.get(i)) + (i < sites.size() ? " OR " : ""));
        }
        return sites.size() > 1 ? result.append(")").toString().trim() : result.toString().trim();
    }

    public String constructQueryStringFromParameters(List<String> sites, List<String> parameters){
        return constructSiteString(sites) + " " + constructParameterString(parameters);
    }

    public String constructQueryStringFromTerms(List<String> sites, List<TermWithVariations> terms){
        StringBuilder parameters_string = new StringBuilder();
        List<String> parameters;
        List<String> variations;
        for(int i = 0; i < terms.size(); i++){
            parameters = new ArrayList<>();
            parameters.add(terms.get(i).name());
            for(String variation : terms.get(i).variations()){
                parameters.add(variation);
            }
            parameters_string.append(constructParameterString(parameters, terms.get(i).booleanOperatorIs())).append(i < terms.size() ? " AND " : "");
        }

        return constructSiteString(sites) + " " + parameters_string.toString();
    }

    public String constructQueryStringFromParameter(String site, String parameter){
        return constructSiteString(site) + " " + constructParameterString(parameter);
    }

}
