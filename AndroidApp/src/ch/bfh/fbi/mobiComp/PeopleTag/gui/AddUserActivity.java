package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.nio.charset.Charset;

public class AddUserActivity extends Activity {

    private static final String TAG = "AddUserActivity";
    NfcAdapter mNfcAdapter;

    PendingIntent mNfcPendingIntent;
    IntentFilter[] mNdefExchangeFilters;

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adduser);

        final Button button = (Button) findViewById(R.id.buttonPair);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pairUser(v);
            }
        });


        final TextView textViewSubTitle = (TextView) findViewById(R.id.textViewSubTitle);
        textViewSubTitle.setText("Your ID: " + ((PeopleTagApplication)getApplication()).getUserID());

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // NFC is not enabled
            final TextView textViewNFC = (TextView) findViewById(R.id.textViewNFC);
            textViewNFC.setVisibility(View.INVISIBLE);

            final ProgressBar progressBarNFC = (ProgressBar) findViewById(R.id.progressBarNFC);
            progressBarNFC.setVisibility(View.INVISIBLE);

            final TextView textViewNFCStatus = (TextView) findViewById(R.id.textViewNFCStatus);
            textViewNFCStatus.setText("This phone is not NFC enabled.");

        } else {

            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                NdefMessage[] messages = getNdefMessages(getIntent());
                byte[] payload = messages[0].getRecords()[0].getPayload();
                Toast.makeText(this, "received via nfc onCreate: " + new String(payload), Toast.LENGTH_LONG).show();
                setIntent(new Intent()); // Consume this intent.
                finish();
            }

            // Create an NDEF message a URL
            // NdefMessage message = NdefRecord.createUri("http://www.android.com")
            mNfcAdapter.setNdefPushMessage(getAsNdef("id:"+((PeopleTagApplication)getApplication()).getUserID()), this);

            final TextView textViewNFCStatus = (TextView) findViewById(R.id.textViewNFCStatus);
            textViewNFCStatus.setText("NFC is activated, transmitting...");

            /*  Nachfolgender Code muss nur aktiviert werden, falls man ausnahmslos alle NFC Tags in der App erhalten will
            // Handle all of our received NFC intents in this activity.
            mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            // Intent filters for exchanging over p2p.
            IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            //IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            try {
                ndefDetected.addDataType("application/peopletag"); //"application/" + this.getPackageName()); // "text/plain"); //
            } catch (MalformedMimeTypeException e) { }

            mNdefExchangeFilters = new IntentFilter[] { ndefDetected };
*/

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sticky notes received from Android
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            Toast.makeText(this, "received via nfc onResume: " + new String(payload), Toast.LENGTH_LONG).show();
            setIntent(new Intent()); // Consume this intent.
            finish();
        }

        //mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mNfcAdapter.disableForegroundDispatch(this);
    }

    private NdefMessage getAsNdef(String text) {
        if(text == null) {
            return null;
        }
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, ("application/peopletag").getBytes(Charset.forName("US-ASCII")), new byte[] {}, text.getBytes()); // "text/plain".getBytes()
        return new NdefMessage(new NdefRecord[] { textRecord });
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)  || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            //romptForContent(msgs[0]);
            byte[] payload = msgs[0].getRecords()[0].getPayload();
            Toast.makeText(this, "received via nfc intent: " + new String(payload), Toast.LENGTH_LONG).show();
        }
    }

    public void pairUser(View view) {

        final EditText editText = (EditText) this.findViewById(R.id.editTextFriendName);

        if (editText.getText() == null || editText.getText().length() < 1) {
            Toast.makeText(this, getString(R.string.ui_setup_noname),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
    }
}
