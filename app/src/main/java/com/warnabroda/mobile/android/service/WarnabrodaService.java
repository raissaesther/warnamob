package com.warnabroda.mobile.android.service;

import com.warnabroda.mobile.android.service.model.Warna;
import com.warnabroda.mobile.android.service.model.Warning;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by ademarizu on 16/03/15.
 */
public interface WarnabrodaService {

    @POST("/warnabroda/warnings")
    public void sendWarning(Warning warning);

    @GET("/warnabroda/messages/{language}")
    public List<Warna> getWarnas(String language);
}
