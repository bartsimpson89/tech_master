package com.example.tech_master;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; 
import android.os.Message;

public class MainEntry extends Activity {

	
	Handler handler = new Handler();  
	Intent intent = new Intent();
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entry);
        ApplicationVar.getInstance().writeLog(new Exception() , "tech_master start");
        
        
        handler.postDelayed(runnable, 2000); //2s∫Û÷¥––
    }
    
    Runnable runnable = new Runnable()
    {      	  
        @Override  
        public void run() {  
        	intent.setClass(MainEntry.this, Login.class);
        	startActivity(intent); 
        	MainEntry.this.finish();
        	handler.removeCallbacks(runnable);  
        }  
    };  


}
