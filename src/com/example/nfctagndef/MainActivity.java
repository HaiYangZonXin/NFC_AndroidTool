package com.example.nfctagndef;

import java.io.UnsupportedEncodingException;
import com.example.nfctagndef.R;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
import android.view.Menu;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcB;
import com.example.nfctagndef.LightType1Activity;

public class MainActivity extends Activity implements OnClickListener {

private Button /*initButton,*/lightType1Button,lightType2Button,
               saveOnTagButton;
private TextView textViewRxDat;
private EditText editTextTxDat/*,editTextTxAddr*/;
//private RatingBar ratingBarVol;



/* NFC value */
private NfcAdapter mAdapter;
private String[][] techList;
private IntentFilter[] intentFilters;
private PendingIntent pendingIntent;
private String txData;
private Tag myTag;

/* normal value */
public byte txDatToNFC;
public int txAddrToNFC;
public int Vol;
String readResult;
public NdefMessage[] msgs;
public String messageDisTextView="\n";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*button*/
//		initButton = (Button)this.findViewById(R.id.buttonInitTag);
//		initButton.setOnClickListener(this);
		lightType1Button=(Button)this.findViewById(R.id.buttonLightType1);
		lightType1Button.setOnClickListener(this);
		lightType2Button=(Button)this.findViewById(R.id.buttonLightType2);
		lightType2Button.setOnClickListener(this);
		saveOnTagButton=(Button)this.findViewById(R.id.buttonSavOnTag);
		saveOnTagButton.setOnClickListener(this);
		/*TextView*/
		textViewRxDat=(TextView)this.findViewById(R.id.textViewRxDat);
		/*EditText*/
		editTextTxDat=(EditText)this.findViewById(R.id.editTextTxDat);
//		editTextTxAddr=(EditText)this.findViewById(R.id.editTextTxAddr);
//		/* RatingBar */
//		ratingBarVol=(RatingBar)this.findViewById(R.id.ratingBarVol);
//		ratingBarVol.setStepSize((float)1.0);
//		ratingBarVol.setRating((float)3.0);
//		ratingBarVol.setIsIndicator(true);
		
		/************************************NFC ********************************/
	    mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        //创建一个 PendingIntent 对象, 这样Android系统就能在一个tag被检测到时定位到这个对象
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
    /****************************onCreateOptionsMenu****************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/*******************************onPause**************************************/
    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }
    /******************************onResume*************************************/
	@Override
    protected void  onResume() {
        super.onResume();
        //使用前台发布系统

        mAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters,techList);
        if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction()) ){
        	processIntent(getIntent());
        }
    }
    /****************************onNewIntent************************************/
    
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}
	/******************************processIntent******************************/
    private void processIntent(Intent intent){

    	myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	//String[] tagType = myTag.getTechList();
        Parcelable[] rawMsg = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(rawMsg!=null){
             msgs = new NdefMessage[rawMsg.length];
        	for(int i=0;i < rawMsg.length;i++)
        	msgs[i]=(NdefMessage)rawMsg[i];
        }
        try {
			txData=new String(msgs[0].getRecords()[0].getPayload(),"GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        textViewRxDat.setText("\n"+txData+"\n");
        System.out.print("processIntent__try——OUT\n");	
    }
  
	/******************************onClick************************************/
		@Override
		public void onClick(View v){
			// TODO Auto-generated catch block
			switch (v.getId()){
//			case R.id.buttonInitTag : 
//				messageDisTextView="\nInitButton has press!\n";
//				textViewRxDat.setText(messageDisTextView);
//				break;
			case R.id.buttonLightType1:
				{
					Intent intentType1 =new Intent(MainActivity.this, LightType1Activity.class);
					Bundle bundleType1 = new Bundle();
					bundleType1.putString("Mode", "Mode1");
					intentType1.putExtras(bundleType1);
				    MainActivity.this.startActivity(intentType1);
				}
				break;
			case R.id.buttonLightType2:
			{
				Intent intentType2 =new Intent(MainActivity.this, LightType2Activity.class);
				Bundle bundleType2 = new Bundle();
				bundleType2.putString("Mode", "Mode2");
				intentType2.putExtras(bundleType2);
			    MainActivity.this.startActivity(intentType2);
			}
				break;
			case R.id.buttonSavOnTag:
				/*************************/
				buttonSaveOnTag();
	            break;
			default:
			    textViewRxDat.setText(readResult+"__是收到的数据\n");
				break;
			}
		}
		/****************************buttonSaveOnTag******************************/
		/*写标签信息按键的函数*/
		public void buttonSaveOnTag ()
		{
			String txBuff = editTextTxDat.getText().toString();
			messageDisTextView="数据输入框输入的是 "+txBuff+"\n";
	        try{
//			    int buffForTxInt = Integer.parseInt(txBuff);
			    if (txBuff.length()>=256)
		        {
					messageDisTextView=messageDisTextView+"输入值大于1Byte\n";
					textViewRxDat.setText(messageDisTextView);
		        }
		        else
		        {
					/*************************/
					Intent intentSaveOnTag =new Intent(MainActivity.this, SaveOnTagActivity.class);
					Bundle bundleSaveOnTag = new Bundle();
					bundleSaveOnTag.putString("TxDat", txBuff);
					intentSaveOnTag.putExtras(bundleSaveOnTag);
				    MainActivity.this.startActivity(intentSaveOnTag);
		        }
	        }catch( Exception e )
	        {
	        	System.out.print("没输入任何数字\n");
	        	 messageDisTextView="\n";
	        }
	       
		}
		
}
