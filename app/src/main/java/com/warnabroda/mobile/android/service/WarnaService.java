package com.warnabroda.mobile.android.service;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.warnabroda.mobile.android.service.model.Warna;
import com.warnabroda.mobile.android.service.model.Warning;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.RestAdapter;
import retrofit.client.Response;

public class WarnaService {

    private WarnabrodaService service;
    private Realm realm;

    public WarnaService(Context context) {
        this.realm = Realm.getInstance(context);
    }

    private WarnabrodaService getService() {
        if (this.service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://warnabroda.com").build();
            this.service = restAdapter.create(WarnabrodaService.class);
        }
        return this.service;
    }

    public List<Warna> listWarna() {
        RealmResults<Warna> result = this.realm.allObjects(Warna.class);
        return result.subList(0, result.size() -1);
    }

    public void sendWarna(final Warning warning) {
        final WarnabrodaService service = this.getService();
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Response response = service.sendWarning(warning);
                Log.d("OLHA A RESPOSTA", response.toString());
                return response;
            }
        };
        task.execute();
    }
}