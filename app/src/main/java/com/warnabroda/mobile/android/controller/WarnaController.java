package com.warnabroda.mobile.android.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.warnabroda.mobile.android.R;
import com.warnabroda.mobile.android.service.WarnaSender;
import com.warnabroda.mobile.android.service.WarnaService;
import com.warnabroda.mobile.android.service.model.Warna;
import com.warnabroda.mobile.android.service.model.Warning;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit.client.Response;

/**
 * Created by ademarizu on 21/04/15.
 */
public class WarnaController implements WarnaSender.WarnaSenderListener<Response> {

    private static final String TAG = "WarnaController";

    private String lang = "pt-br";
    private WarnaService service;
    private Context context;
    private WarnaControllerListener listener;

    public WarnaController(Context context) {
        this.context = context;
    }

    public void addListener(WarnaControllerListener listener) {
        this.listener = listener;
    }

    public Locale getLocale() {
        Locale locale = Locale.forLanguageTag("pt");
        String lang = getLang();
        Log.d("LANG ", lang);
        if (lang == "en") {
            locale = Locale.ENGLISH;
        } else if (lang == "es") {
            locale = Locale.forLanguageTag("es");
        }
        return locale;
    }

    public String getLang() {
        SharedPreferences sp = this.context.getSharedPreferences("", Context.MODE_PRIVATE);
        return sp.getString("preferred_lang", "pt-br");
    }

    private WarnaService getService() {
        if (service == null) {
            service = new WarnaService(this.context);
            loadMessages();
        }
        return service;
    }

    public List<Warna> getMessages() {
        return getService().listWarna(getLang());
    }

    public void sendWarna(final Warning warning) {
        WarnaSender sender = new WarnaSender(this.getService());
        sender.addListener(this);
        sender.execute(warning);
    }

    private void loadMessages() {
        try {
            for (String file : this.context.getAssets().list("Files")) {
                getService().loadMessages(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostExecute(Response result) {
        String msg = this.context.getString(R.string.msg_could_not_send);
        if (result != null) {
            msg = result.getReason();
        }
        this.listener.warnaResult(msg);
    }
}
