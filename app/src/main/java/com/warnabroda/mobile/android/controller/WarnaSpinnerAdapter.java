package com.warnabroda.mobile.android.controller;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.warnabroda.mobile.android.service.WarnaService;
import com.warnabroda.mobile.android.service.model.Warna;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ademarizu on 13/03/15.
 */
public class WarnaSpinnerAdapter extends BaseAdapter {

    private WarnaService service;
    private String lang = "pt-br";
    private List<Warna> warnas = new ArrayList<Warna>();

    public WarnaSpinnerAdapter(WarnaService service) {
        this.service = service;
    }

    private void updateWarnas() {
        this.warnas = this.service.listWarna(this.lang);
    }

    public void updateLanguage(String lang) {
        this.lang = lang;
        this.updateWarnas();
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return warnas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.warnas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.warnas.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Warna warna = (Warna) getItem(position);
        TextView view = new TextView(parent.getContext());
        view.setText(warna.getName());
        return view;
    }
}


