package com.warnabroda.mobile.android.controller;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.warnabroda.mobile.android.service.model.Warna;

import io.realm.Realm;

/**
 * Created by ademarizu on 13/03/15.
 */
public class WarnaSpinnerAdapter extends BaseAdapter {

    public WarnaSpinnerAdapter() {}

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setText("Putz o Warna");
        return view;
    }
}


