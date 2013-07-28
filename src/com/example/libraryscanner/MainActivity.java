package com.example.libraryscanner;



import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private Tag detectedTag;
	MyWebView mitView;
	private int destination=0;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mitView = new MyWebView(this);
		setContentView(mitView);
		
		mAdapter = NfcAdapter.getDefaultAdapter(this);

		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, };
		
		// Setup a tech list for all NfcV tags
		mTechLists = new String[][] { new String[] { NfcV.class.getName() } };
		
	}
	@Override
	public void onResume() {
	  	Log.i("progress","onResume");

		super.onResume();
		if(destination==1){
			mitView.loader();
			destination=0;
		}
		//mitView.loader();
		
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);

		
	}
		

    public void simulateKey(final String a){
    	
    	
    new Thread(new Runnable() {         
        @Override
        public void run() {
            try {
            Instrumentation inst = new Instrumentation();
            for ( int i = 0; i < a.length(); i++ ) {
            	String b = a.substring(i, i+1);
            	if(b.equals("0")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_0);
            	}
            	else if(b.equals("1")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_1);
            	}
            	else if(b.equals("2")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_2);
            	}
            	else if(b.equals("3")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_3);
            	}
            	else if(b.equals("4")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_4);
            	}
            	else if(b.equals("5")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_5);
            	}
            	else if(b.equals("6")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_6);
            	}
            	else if(b.equals("7")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_7);
            	}
            	else if(b.equals("8")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_8);
            	}
            	else if(b.equals("9")){
            		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_9);
            	}
            	
            }
            
                
             
            
            }
            catch(Exception e){
            }
        }   
    }).start();
    
    }    
    
	@Override
    public void onNewIntent(Intent intent) {
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);

		// Get tag and create NfcVBook object to communicate with the tag.
		detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		NfcReader book = new NfcReader(detectedTag);
		
		// Get basic tag information.
		//byte[] info = book.getInfo();
		SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String substartStr = prefs1.getString("startSub", "2");
		int substart = Integer.parseInt(substartStr);
		String subslutStr = prefs1.getString("slutSub", "14");
		int subslut = Integer.parseInt(subslutStr);
		

		// Set pay-load text.
		//TextView payloadText = (TextView) findViewById(R.id.payloadText);
		String output = book.readBlocksAsString((byte)0, (byte)12);
		Log.i("NFC", "outputraw = " + output);
		
		String outputCutted = output.substring(substart, subslut);
		Log.i("NFC", "outputCutted = " + outputCutted);
		simulateKey(outputCutted);
		//payloadText.setText(output.substring(0, 14));
		//String substr=mysourcestring.subString(startIndex,endIndex);
		
		// Update tag count
		//tagCountText.setText(Integer.toString(++tagCount));
		
	
		
		// Close connection.
		book.close();
		book = null;
	}
	
    
	
	@SuppressLint("SetJavaScriptEnabled")
	private class MyWebView extends WebView {
		float xdown=0;
		float ydown=0;
		float xup=0;
		float yup=0;
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			//
			int action = event.getAction();
			Log.w("TouchEvent","Touch");
			//Log.w("HitResult",this.getHitTestResult().toString());
			switch (action) {
			case (MotionEvent.ACTION_DOWN): // Touch screen pressed
				xdown = event.getX();
   				ydown = event.getY();
   				Log.i("MyActivity", "xdown" + xdown);
			   
   				break;
			case (MotionEvent.ACTION_UP): // Touch screen touch ended
				xup = event.getX();
   				yup = event.getY();
   				Log.i("MyActivity", "xup" + xup);
   				
   				
   				break;
			case (MotionEvent.ACTION_MOVE): // Contact has moved across screen
				break;
			case (MotionEvent.ACTION_CANCEL): // Touch event cancelled
				break;
			}
		   
			float difx = Math.abs(xup-xdown);
   			float dify = Math.abs(yup-ydown);
			Log.i("MyActivity", "difx: " + difx + "  dify:" + dify);

    		if (difx>dify && xdown<200 && xup>400){
    			xdown=500;
    			ydown=500;
    			xup=0;
    			yup=0;
    			
    			Intent intent = new Intent(MainActivity.this, Settings.class);
    			Log.i("MyActivity", "start intent");
    			Bundle bndlanimation =	ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animationin,R.anim.animationout).toBundle();
    			destination = 1;
    			startActivity(intent, bndlanimation);
    	        //startActivity(intent);
    			
    		}

		   
		   return super.onTouchEvent(event);
		  }

		  public MyWebView(Context context) {
		   super(context);
		   this.getSettings().setJavaScriptEnabled(true);
		   HelloWebViewClient client = new HelloWebViewClient();
		   this.setWebViewClient(client);
		   this.loader();
		   
		  }
		  public void loader(){
			  SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			   String url2 = prefs2.getString("websitechoice", "http://www.dr.dk/");
			   			   
			   if (url2.substring(0, 3).equals("www")){
				   url2= "http://" + url2;
			   }
			   Log.i("URL","Loader " + url2);
			   
			   this.loadUrl(url2);
			  
		  }
		  
		  
		  
		 }
	private class HelloWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	 }
	}
	

		}

