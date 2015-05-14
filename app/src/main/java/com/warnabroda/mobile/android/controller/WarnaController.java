package com.warnabroda.mobile.android.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.warnabroda.mobile.android.R;
import com.warnabroda.mobile.android.service.WarnaSender;
import com.warnabroda.mobile.android.service.WarnaService;
import com.warnabroda.mobile.android.service.model.Warna;
import com.warnabroda.mobile.android.service.model.Warning;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    private RequestQueue mRequestQueue;

    public WarnaController(Context context) {
        this.context = context;
        this.mRequestQueue = Volley.newRequestQueue(context); // 'this' is Context
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
//        WarnaSender sender = new WarnaSender(this.getService());
//        sender.addListener(this);
//        sender.execute(warning);

        final String URL = "http://www.warnabroda.com/warnabroda/warnings";
        // Post params to be sent to the server
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("id_message", warning.getId_message());
        params.put("id_contact_type", warning.getId_contact_type());
        params.put("contact", warning.getContact());
        params.put("ip", "1.0.0.1");
        params.put("lang_key", "pt-br");
        final WarnaControllerListener listner = this.listener;

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    listner.warnaResult(response.toString(4));
                    VolleyLog.v("Response:%n %s", response.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("PORRA", error.getMessage());
//                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        Log.d("PORA", req.getBodyContentType());
        mRequestQueue.add(req);
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
