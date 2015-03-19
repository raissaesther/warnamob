package com.warnabroda.mobile.android.service;


import android.content.Context;

import com.warnabroda.mobile.android.service.model.Warna;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.RestAdapter;

public class WarnaService {

    private WarnabrodaService service;
    private Realm realm;

    public WarnaService(Context context) {
        this.realm = Realm.getInstance(context);
    }

    private WarnabrodaService getService() {
        if (this.service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("https://warnabroda.com").build();
            this.service = restAdapter.create(WarnabrodaService.class);
        }
        return this.service;
    }

    public List<Warna> listWarna() {
        RealmResults<Warna> result = this.realm.allObjects(Warna.class);
        return result.subList(0, result.size() -1);
    }
}