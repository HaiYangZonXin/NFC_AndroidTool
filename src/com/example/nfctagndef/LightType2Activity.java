package com.example.nfctagndef;

import java.io.IOException;
import java.nio.charset.Charset;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord; 
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcB;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class LightType2Activity extends Activity implements CreateNdefMessageCallback {

	TextView textView;
	String ReadFormIntent;
	NfcAdapter mNfcAdapter;
	String[][] techList;
	IntentFilter[] intentFilters;
	PendingIntent pendingIntent;
	/*******************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		System.out.print("\nonCreat\n");
		setContentView(R.layout.lighttype2activity);
		textView= (TextView)this.findViewById(R.id.textView2);
		Intent intent = getIntent();
		Bundle bundle =intent.getExtras();
		ReadFormIntent = bundle.getString("Mode");
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
//        mNfcAdapter.setNdefPushMessageCallback(this, this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        //声明一个intent过滤器
        IntentFilter intentNfcFFilters = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try{
        	intentNfcFFilters.addDataType("*/*");
        }
        catch(MalformedMimeTypeException e) {
        	throw new RuntimeException("fail", e);
        }
         intentFilters = new IntentFilter[]{intentNfcFFilters};
         techList = new String[][]{
        		new String[]{NfcB.class.getName()},
        		new String[]{Ndef.class.getName()},
        		new String[]{IsoDep.class.getName()}	
        };
        
	}
/*******************************************************************************/

	public NdefMessage createNdefMessage(NfcEvent event) {

		System.out.print("\ncreateNdefMessage\n");
		String text = (ReadFormIntent);
		NdefMessage msg = new NdefMessage
				(
						
						new NdefRecord[] 
								{
								    createMimeRecord("text/plain",text.getBytes())
								}
				);
		return msg;

	}
	/*********************************************************************************************/
	public NdefRecord createMimeRecord(String mimeType,byte[] payload){
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,mimeBytes,new byte[0],payload);
		return mimeRecord;
	}
	
	/********************************************************************************************/
    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }
    /******************************************************************************************/
	 @Override
	    public void onResume() {
	        super.onResume();
	        System.out.print("\nonResume\n");
	        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters,techList);
	        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
	            processIntent(getIntent());
	        }
	    }
	 /*******************************************************************************/
	    @Override
	    public void onNewIntent(Intent intent) {
	        // onResume gets called after this to handle the intent
	        setIntent(intent);
	    }
 /*******************************************************************************/
	    void processIntent(Intent intent) {
	    	
	    	System.out.print("processIntent");
	        
	    	Tag myTag=getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    	Ndef ndef =Ndef.get(myTag);
	    	try{
	    		ndef.connect();
	    		NdefRecord ndefReCord= createMimeRecord("text/plain",ReadFormIntent.getBytes());
	    		NdefRecord[] records ={ndefReCord};
	    		NdefMessage ndefMessage=new NdefMessage(records);
//	    		NdefMessage ndefMessage=createNdefMessage();
	    		ndef.writeNdefMessage(ndefMessage);
	    		Toast.makeText(this, "流水灯设置成功", Toast.LENGTH_LONG).show();
		    	finish();
	    	}catch(IOException e1){
	    		e1.printStackTrace();
	    	} 
	        catch (FormatException e) {
				e.printStackTrace();
			}
	    	finish();
	    }
}
