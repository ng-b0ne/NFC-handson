package com.example.android.nfchandson;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;
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

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mFilters = new IntentFilter[] { tag, };
        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };
        
        //インテントを受け取ったら
        if (getIntent().getAction() != null) {
            resolveIntent(getIntent());
        }
    }
    
    public void resolveIntent(Intent intent){
        Log.v("TEST", "resolveIntent");

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
        Log.v("TEST", "onResume");
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Log.v("TEST", "onPause");
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        Log.v("TEST", "onNewIntent");
        setIntent(intent);
        resolveIntent(intent);
    }
}