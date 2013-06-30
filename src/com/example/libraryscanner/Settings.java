package com.example.libraryscanner;

import java.util.List;


import android.preference.PreferenceActivity;
import android.util.Log;


public class Settings extends PreferenceActivity {
    
	   //@Override
	  // public void onCreate(Bundle savedInstanceState) {       
	   //    super.onCreate(savedInstanceState);       
	  //     getFragmentManager().beginTransaction().replace(android.R.id.content, new fragments()).commit();    }
	   
	  
	       @Override
	       public void onBuildHeaders(List<Header> target) {
	    	   Log.i("MyActivity", "onBuildHeaders");
	    	   loadHeadersFromResource(R.xml.header, target);
	           Log.i("MyActivity", "onBuildHeaders2");
	       }
	       @Override
	      	public void onBackPressed() {
	      	    finish();
	      	    Log.i("MyActivity", "onBackPressed");
	      	    overridePendingTransition(R.anim.backanimationout, R.anim.backanimationin);
	      	}
}
