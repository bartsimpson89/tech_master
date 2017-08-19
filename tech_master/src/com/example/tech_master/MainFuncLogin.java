package com.example.tech_master;
import java.util.HashMap;

import com.example.dialog.LoginDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainFuncLogin extends Activity{
	
	private Intent intent = new Intent();
	private Button btnPowerManage;
	private Button btnDeviceManage;
	private Button btnViewLog;
	private Button btnAlarmState;
	private Button btnChannelManage;
    private Button btnDeviceControl;
	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_mainfunclogin);
   	        btnPowerManage = (Button) findViewById(R.id.mainfunc_btn_powermanage);
		    btnDeviceManage = (Button) findViewById(R.id.mainfunc_btn_devicemanage);
		    btnViewLog = (Button) findViewById(R.id.mainfunc_btn_checklog);
	        btnAlarmState = (Button) findViewById(R.id.mainfunc_btn_alarmstate);
	        btnChannelManage = (Button) findViewById(R.id.mainfunc_btn_channelmanage);
	        btnDeviceControl = (Button) findViewById(R.id.mainfunc_btn_iocontrol);
	        
	        
	        btnPowerManage.setOnClickListener(new MyClickListener());
	        btnDeviceManage.setOnClickListener(new MyClickListener());
	        btnViewLog.setOnClickListener(new MyClickListener());
	        btnAlarmState.setOnClickListener(new MyClickListener());
	        btnChannelManage.setOnClickListener(new MyClickListener());
	        btnDeviceControl.setOnClickListener(new MyClickListener());
	    }
	 
	 
	 class MyClickListener implements OnClickListener 
	    {
	        @Override
	        public void onClick(View v) 
	        {
	        	
	            switch (v.getId()) 
	            {
	            case R.id.mainfunc_btn_powermanage:
	            	intent.setClass(MainFuncLogin.this, PowerManage.class);  
	            	startActivity(intent);
	            	break;
	            case R.id.mainfunc_btn_devicemanage:
	            	intent.setClass(MainFuncLogin.this, DeviceManage.class);  
	            	startActivity(intent);
	               break;
	            case R.id.mainfunc_btn_checklog:
	            	intent.setClass(MainFuncLogin.this, ViewLog.class);  
	            	startActivity(intent);
	               break;
	            case R.id.mainfunc_btn_alarmstate:
	            	intent.setClass(MainFuncLogin.this, AlarmState.class);  
	            	startActivity(intent);
	               break;
	            case R.id.mainfunc_btn_channelmanage:
	            	intent.setClass(MainFuncLogin.this,ChannelManage.class);  
	            	startActivity(intent);
	               break;
	            case R.id.mainfunc_btn_iocontrol:
	            	intent.setClass(MainFuncLogin.this,IOControl.class);  
	            	startActivity(intent);
	               break;
	        }
	      }
	    }

}
