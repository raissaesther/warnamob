package com.warnabroda.mobile.android.service.model;

import java.util.Date;

/**
 * Created by ademarizu on 16/03/15.
 */
public class Warning {

    private String contact;
    private int id_contact_type;
    private long id_message;
    private String lang_key;
    private Date created_date;

    private String ip;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getId_contact_type() {
        return id_contact_type;
    }

    public void setId_contact_type(int id_contact_type) {
        this.id_contact_type = id_contact_type;
    }

    public long getId_message() {
        return id_message;
    }

    public void setId_message(long id_message) {
        this.id_message = id_message;
    }

    public String getLang_key() {
        return lang_key;
    }

    public void setLang_key(String lang_key) {
        this.lang_key = lang_key;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
