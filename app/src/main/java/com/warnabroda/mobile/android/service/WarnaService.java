package com.warnabroda.mobile.android.service;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.warnabroda.mobile.android.service.model.Warna;
import com.warnabroda.mobile.android.service.model.Warning;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.RestAdapter;
import retrofit.client.Response;

public class WarnaService {

    private WarnabrodaService service;
    private Realm realm;
    private Context context;
    private final String TAG = "WarnaService";

    public WarnaService(Context context) {
        this.context = context;
        this.realm = Realm.getInstance(context);
    }

    private WarnabrodaService getService() {
        if (this.service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://warnabroda.com").build();
            this.service = restAdapter.create(WarnabrodaService.class);
        }
        return this.service;
    }

    public List<Warna> listWarna(String lang) {
        RealmResults<Warna> result = this.realm.where(Warna.class).contains("lang_key", lang).findAllSorted("id");
        return result.subList(0, result.size() -1);
    }

    public void sendWarna(final Warning warning) {
        final WarnabrodaService service = this.getService();
        final Context thisContext = this.context;
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Response response = null;
                try {
                    response = service.sendWarning(warning);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return response;
            }
        };
        task.execute();
    }

    public void loadMessages(String file) throws IOException {
        InputStream is = this.context.getAssets().open("Files/"+ file);
        realm.beginTransaction();
        try {
            realm.createAllFromJson(Warna.class, is);
            realm.commitTransaction();
        } catch (IOException e) {
            realm.cancelTransaction();
        }
    }
}