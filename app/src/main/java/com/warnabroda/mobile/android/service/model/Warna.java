package com.warnabroda.mobile.android.service.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ademarizu on 11/03/15.
 */
public class Warna extends RealmObject {

    @PrimaryKey
    private long id;
    private String message;
    private String lang;
    private boolean active;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}