package com.example.android.nfchandson;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    
    
    TextView nfcActionView;
    TextView nfcIdView;
    TextView nfcTagView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        nfcActionView = (TextView)findViewById(R.id.nfc_action_view);
        nfcIdView = (TextView)findViewById(R.id.nfc_id_textview);
        nfcTagView = (TextView)findViewById(R.id.nfc_tag_view);
        
        //resolveIntent(getIntent());
    }
    
    public void setDefaultValue(String action){
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all MIME based dispatches
        //IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter ndef = new IntentFilter(action);
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };
    }
    
    public void resolveIntent(Intent intent){

        String action = intent.getAction();
        if (isNFCAction(action)) {
            nfcActionView.setText(action);
            
            byte[] rowId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String text = bytesToText(rowId);
            nfcIdView.setText(text);
            
            Tag tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String techStr = "";
            for (String tech : tag.getTechList()) {
                techStr = techStr + tech + "\n";
            }
            if (techStr.equals("")) {
                techStr = "no techList.";
            }
            nfcTagView.setText(techStr);

        }
    }
    
    private boolean isNFCAction(String action){
        if (action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)
                || action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)
                || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED) ) {
            return true;
        }
        return false;
    }
    
    private String bytesToText(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02X", b);
            buffer.append(hex).append(" ");
        }
        String text = buffer.toString().trim();
        return text;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        //if (mAdapter != null) {
            setDefaultValue(getIntent().getAction());
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
        //}
    }
    
    @Override
    public void onPause() {
        super.onPause();
        //if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
        if ( this.isFinishing() && mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
}