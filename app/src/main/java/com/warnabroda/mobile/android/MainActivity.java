package com.warnabroda.mobile.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.warnabroda.mobile.android.controller.WarnaController;
import com.warnabroda.mobile.android.controller.WarnaControllerListener;
import com.warnabroda.mobile.android.controller.WarnaSpinnerAdapter;
import com.warnabroda.mobile.android.service.WarnaService;
import com.warnabroda.mobile.android.service.model.Warna;
import com.warnabroda.mobile.android.service.model.Warning;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements WarnaControllerListener {

    private static final String TAG = "MAIN";

    public static final int PICK_CONTACT_PHONE  = 1;
    public static final int PICK_CONTACT_EMAIL  = 2;

    public static final int TYPE_EMAIL      = 1;
    public static final int TYPE_SMS        = 2;
    public static final int TYPE_WHATSAPP   = 3;

    private WarnaController controller;
    private int contactType = PICK_CONTACT_PHONE;

    private Configuration config ; // variable declaration in globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = new Configuration(this.getResources().getConfiguration());
        config.locale = getController().getLocale();
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            PlaceholderFragment frag = new PlaceholderFragment();
            getFragmentManager().beginTransaction().add(R.id.container, frag).commit();
        }
    }

    protected WarnaController getController() {
        if (this.controller == null) {
            this.controller = new WarnaController(this);
            this.controller.addListener(this);
        }
        return this.controller;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class clazz = null;
        switch (item.getItemId()) {
            case R.id.action_settings:
                clazz = SettingsActivity.class;
                break;

            case R.id.action_about:
                clazz = AboutActivity.class;
                break;
        };
        startActivity(new Intent(this, clazz));
        return super.onOptionsItemSelected(item);
    }

    public void messageTypeSelected(View view) {
        int id = view.getId();
        EditText et = (EditText) findViewById(R.id.number);
        if (id == R.id.radioEmail) {
            this.contactType = PICK_CONTACT_EMAIL;
            et.setHint("contact@email.com");
        } else if (id == R.id.radioWhatsapp || id == R.id.radioSMS) {
            Log.d(TAG, "Trocar para Número");
            this.contactType = PICK_CONTACT_PHONE;
            et.setHint("+551199999999");
        }
        et.setText("");
    }

    /**
     * Switchs contacts types
     * @param type
     */
    private void switchContactsType(int type) {
        Button contacts = new Button(getApplicationContext());
        contacts.setText(R.string.contacts);
    }

    @Override
    public void warnaResult(String result) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private WarnaController controller;

        public PlaceholderFragment() {}

        public WarnaController getController() {
            MainActivity parent = (MainActivity) getActivity();
            return parent.getController();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Spinner messages = (Spinner) rootView.findViewById(R.id.messages);
            messages.setAdapter(new WarnaSpinnerAdapter(getController().getMessages()));
            return rootView;
        }
    }

    public void loadContats(View view) {
        Uri uri = Phone.CONTENT_URI;
        if (this.contactType == PICK_CONTACT_EMAIL) {
            uri = Email.CONTENT_URI;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(intent, this.contactType);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            String id = contactData.getLastPathSegment();

            switch (reqCode) {
                case (PICK_CONTACT_EMAIL):
                    String email = extract(id, Email.CONTENT_URI, Email.DATA);
                    this.fillNumber(email);
                    break;

                case (PICK_CONTACT_PHONE):
                    String phone = extract(id, Phone.CONTENT_URI, Phone.NORMALIZED_NUMBER);
                    this.fillNumber(phone);
                    break;
            }
        }
    }

    private String extract(String id, Uri contentUri, String data) {
        Cursor cursor = getContentResolver().query(contentUri, null, "_id" + "=?", new String[]{id}, null);
        Boolean valueExists = cursor.moveToFirst();
        int dataIndex = cursor.getColumnIndex(data);
        String dataValue = "";

        while (valueExists) {
            dataValue = cursor.getString(dataIndex);
            dataValue = dataValue.trim();
            valueExists = cursor.moveToNext();
        }
        return dataValue;
    }

    private void fillNumber(String number) {
        EditText textField = (EditText) this.findViewById(R.id.number);
        textField.setText(number);
    }

    public void sendMessage(View view) {
        Warning warning = new Warning();
        warning.setContact(getContact());
        warning.setCreated_date(new Date());
        warning.setId_message(getMessageId());
        warning.setId_contact_type(getMessageType());
        getController().sendWarna(warning);
    }

    private int getMessageType() {
        RadioGroup types = (RadioGroup) findViewById(R.id.type);
        int type = TYPE_WHATSAPP;
        switch (types.getCheckedRadioButtonId()) {
            case R.id.radioEmail:
                type = TYPE_EMAIL;
                break;
            case R.id.radioSMS:
                type = TYPE_SMS;
                break;
            case  R.id.radioWhatsapp:
                type = TYPE_WHATSAPP;
                break;
            default:
                type = TYPE_WHATSAPP;
                break;
        }
        return type;
    }

    private String getContact(){
        return ((EditText)this.findViewById(R.id.number)).getText().toString();
    }

    private long getMessageId() {
        Warna warna = (Warna)((Spinner)this.findViewById(R.id.messages)).getSelectedItem();
        return warna.getId();
    }
}
