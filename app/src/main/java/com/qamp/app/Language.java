package com.qamp.app;

public class Language {
    private String language;
    private String languageCode;

    public Language(String language, String languageCode) {
        this.language = language;
        this.languageCode = languageCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}