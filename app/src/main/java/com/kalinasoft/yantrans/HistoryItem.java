package com.kalinasoft.yantrans;

import java.io.Serializable;

/**
 * Created by Andrey Kalikin on 17.04.2017.
 * Class for item in history/favorites
 */

class HistoryItem implements Serializable{
    private final String word;
    private final String translation;
    private final String lang_to;
    private final String lang_from;
    private boolean checked = false;

    HistoryItem(String word, String translation, String lang_to, String lang_from) {
        this.word = word;
        this.translation = translation;
        this.lang_to = lang_to;
        this.lang_from = lang_from;
    }

    String getWord() {
        return word;
    }

    String getTranslation() {
        return translation;
    }

    String getLang_to() {
        return lang_to;
    }

    String getLang_from() {
        return lang_from;
    }

    boolean isChecked() {
        return checked;
    }

    void setChecked(boolean checked) {
        this.checked = checked;
    }


}
