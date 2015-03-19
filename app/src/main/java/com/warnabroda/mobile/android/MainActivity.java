package com.warnabroda.mobile.android;

import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.warnabroda.mobile.android.controller.WarnaSpinnerAdapter;
import com.warnabroda.mobile.android.service.model.Warna;


public class MainActivity extends Activity {

    public static final int PICK_CONTAC = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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
        System.out.println("Escolhido" + view);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Spinner messages = (Spinner) rootView.findViewById(R.id.messages);
            messages.setAdapter(new WarnaSpinnerAdapter());
            return rootView;
        }
    }

    public void loadContacts(View view) {
        Log.d("Abrindo contatos", "Abrindo contatos");
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTAC);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTAC) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    String id = contactData.getLastPathSegment();

                    this.extractEmail(id);

//                    Cursor c = getContentResolver().query(Data.CONTENT_URI,
//                            new String[] {Data._ID, Phone.NORMALIZED_NUMBER, Phone.TYPE, Phone.LABEL},
//                            Data.RAW_CONTACT_ID + "=?" + " AND "
//                                    + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'",
//                            new String[] {String.valueOf(id)}, null);
//
//                    if (c.moveToFirst()) {
//                        String phone = c.getString(c.getColumnIndex(Phone.NORMALIZED_NUMBER));
//                        String phoneLabel = c.getString(c.getColumnIndex(Phone.LABEL));
//                        String phoneType = c.getString(c.getColumnIndex(Phone.TYPE));
//                        Log.d("Phone: " + phoneLabel, phoneType + " - " + phone);
////                        this.fillNumber(name);
//                    }
                }
                break;
        }
    }

    private String extractEmail(String id) {
        // query for everything email
        String[] projection = {Data._ID, Phone.NORMALIZED_NUMBER, Phone.NUMBER, Phone.TYPE, Phone.LABEL};
        Cursor cursor = getContentResolver().query(
                Phone.CONTENT_URI, projection,
                Phone.CONTACT_ID + "=?" + " AND " + Phone.HAS_PHONE_NUMBER,
                new String[]{id}, null);

        cursor.moveToFirst();
        Log.d("DATA:", cursor.getString(cursor.getColumnIndex(Phone.LABEL)));
        Log.d("DATA:", cursor.getString(cursor.getColumnIndex(Phone.TYPE)));
        Log.d("DATA:", cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
        Log.d("DATA:", cursor.getString(cursor.getColumnIndex(Phone.NORMALIZED_NUMBER)));
//        String columns[] = cursor.getColumnNames();
//        for (String column : columns) {
//
//            int index = cursor.getColumnIndex(column);
//            Log.v("DEBUG", "Column: " + column + " == ["
//                    + cursor.getString(index) + "]");
//        }
        return null;
    }

    private void fillNumber(String number) {
        EditText textField = (EditText) this.findViewById(R.id.number);
        textField.setText(number);
    }
}
