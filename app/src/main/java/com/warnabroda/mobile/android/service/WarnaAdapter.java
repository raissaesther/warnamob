package com.warnabroda.mobile.android.service;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.warnabroda.mobile.android.service.model.Warna;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by ademarizu on 27/03/15.
 */
public class WarnaAdapter extends RealmBaseAdapter<Warna> implements ListAdapter {

    public WarnaAdapter(Context context, RealmResults<Warna> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
