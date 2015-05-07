package com.warnabroda.mobile.android.service;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.warnabroda.mobile.android.service.model.Warning;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import retrofit.client.Response;

/**
 * Created by ademarizu on 21/04/15.
 */
public class WarnaSender extends AsyncTask<Warning, Void, Response> {

    private static final String TAG = "WarnaSender";
    private WarnaService service;
    private WarnaSenderListener<Response> listener;

    public WarnaSender(WarnaService service) {
        this.service = service;
    }

    public void addListener(WarnaSenderListener listener) {
        this.listener = listener;
    }

    protected Response doInBackground(Warning... warnings) {
        Response response = null;
        try {
            for (Warning warning : warnings) {
                warning.setIp(getIPAddress(true));
                Log.d("SENDER", warning.getIp());
                response = service.sendWarning(warning);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        this.listener.onPostExecute(response);
    }

    public interface WarnaSenderListener<T> {
        public void onPostExecute(T result);
    }

    public String getIPAddress(boolean useIPv4) {
        String ip = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://ip2country.sourceforge.net/ip2c.php?format=JSON");
            // HttpGet httpget = new HttpGet("http://whatismyip.com.au/");
            // HttpGet httpget = new HttpGet("http://www.whatismyip.org/");
            HttpResponse response;

            response = httpclient.execute(httpget);
            //Log.i("externalip",response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            entity.getContentLength();
            String str = EntityUtils.toString(entity);
            JSONObject json_data = new JSONObject(str);
            ip = json_data.getString("ip");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return ip;
    }

}
