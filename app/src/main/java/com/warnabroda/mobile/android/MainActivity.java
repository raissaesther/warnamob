package com.warnabroda.mobile.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetManager;
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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.warnabroda.mobile.android.controller.WarnaSpinnerAdapter;
import com.warnabroda.mobile.android.service.WarnaService;
import com.warnabroda.mobile.android.service.model.Warna;
import com.warnabroda.mobile.android.service.model.Warning;

import java.io.IOException;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity {

    private static final String TAG = "MAIN";

    public static final int PICK_CONTACT_PHONE  = 1;
    public static final int PICK_CONTACT_EMAIL  = 2;

    public static final int TYPE_EMAIL      = 1;
    public static final int TYPE_SMS        = 2;
    public static final int TYPE_WHATSAPP   = 3;


    private WarnaService warnaService = null;

    private WarnaService getWarnaService() {
        if (warnaService == null) {
            warnaService = new WarnaService(this);
        }
        return warnaService;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        loadMessages();
        List<Warna> list = getWarnaService().listWarna("pt-br");
        for (Warna w : list) {
            Log.d(TAG, w.getName());
        }
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            PlaceholderFragment frag = new PlaceholderFragment();
            frag.setWarnaService(getWarnaService());
            getFragmentManager().beginTransaction()
                    .add(R.id.container, frag)
                    .commit();
        }
    }

    private void loadMessages() {
        try {
            for (String file : getAssets().list("Files")) {
                getWarnaService().loadMessages(file);
                Log.d(TAG, "ARQUIVO: " + file );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void messageTypeSelected(View view) {
        int id = view.getId();
        if (id == R.id.radioEmail) {
            Log.d(TAG, "Trocar para Email");
        } else if (id == R.id.radioWhatsapp || id == R.id.radioSMS) {
            Log.d(TAG, "Trocar para Número");
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private WarnaService service;

        public PlaceholderFragment() {}

        public void setWarnaService(WarnaService service ){this.service = service;}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Spinner messages = (Spinner) rootView.findViewById(R.id.messages);
            WarnaSpinnerAdapter adapter = new WarnaSpinnerAdapter(this.service);
            adapter.updateLanguage("pt-br");
            messages.setAdapter(adapter);
            return rootView;
        }
    }

    public void loadContactsPhones(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_PHONE);
    }

    public void loadContactsEmails(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, Email.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_EMAIL);
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
        getWarnaService().sendWarna(warning);
        Toast.makeText(getApplicationContext(), "Enviou", Toast.LENGTH_SHORT).show();
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
