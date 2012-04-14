package com.example.android.nfchandson;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

public class MainActivity extends Activity {
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
        
        resolveIntent(getIntent());
    }
    
    public void resolveIntent(Intent intent){
        String action = intent.getAction();
        if (isNFCAction(action)) {
            nfcActionView.setText(action);
            
            byte[] rowId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String text = bytesToText(rowId);
            nfcIdView.setText(text);
            nfcIdView.setText(text);
            
            Tag tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String techStr = "";
            for (int a=0; a<techList.length; a++) {
                techStr = techList[a] + "\n";
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
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
}