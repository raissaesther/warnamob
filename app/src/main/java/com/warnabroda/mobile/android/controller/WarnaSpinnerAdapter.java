package com.warnabroda.mobile.android.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.warnabroda.mobile.android.R;
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
    private List<Warna> warnas;

    public WarnaSpinnerAdapter(WarnaService service) {
        this.service = service;
    }

    private List<Warna> getWarnas() {
        if (warnas == null) {
            updateWarnas();
        }
        return this.warnas;
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
        return getWarnas().size();
    }

    @Override
    public Object getItem(int position) {
        return getWarnas().get(position);
    }

    @Override
    public long getItemId(int position) {
        return getWarnas().get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Warna warna = (Warna) getItem(position);
//        TextView view = new TextView(parent.getContext());
//        view.setText(warna.getName());

        LayoutInflater mInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;
        TextView text;

        if (convertView == null) {
            view = mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        } else {
            view = convertView;
        }

        text = (TextView) view;

        text.setText(warna.getName());

        return view;
    }
}


