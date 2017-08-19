package com.example.tech_master;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import com.example.device.Device;
import com.example.device.Device.ICoallBack;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.tech_master.PowerManage;
public class IOControl extends Activity {
	
	private Device []devPower;
    private Button btnAllOn;
    private Button btnAllClose;
    private Button btnTimer;
    private RelativeLayout rlActivity;
     
    private ArrayList<HashMap<String, String>> gListDeviceState;//dev_state;dev_set_timer;dev_id;dev_name; 
    
    private int gShowCurPage = 0;

	 protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_io_control);
        initWidgetHandle();
        initDevClickCallBack();//设备点击回调函数
        gListDeviceState = ApplicationVar.getInstance().getDevStateList();
        initDevPowerShow(gShowCurPage);
       
    } 
	 
	 private void initWidgetHandle()
	 {
		    devPower = new Device[10] ;//每个显示设备状态
		    
		    devPower[0] = (Device) findViewById(R.id.my_device_1);
	        devPower[1] = (Device) findViewById(R.id.my_device_2);
	        devPower[2] = (Device) findViewById(R.id.my_device_3);
	        devPower[3] = (Device) findViewById(R.id.my_device_4);
	        devPower[4] = (Device) findViewById(R.id.my_device_5);
	        devPower[5] = (Device) findViewById(R.id.my_device_6);
	        devPower[6] = (Device) findViewById(R.id.my_device_7);
	        devPower[7] = (Device) findViewById(R.id.my_device_8);
	        devPower[8] = (Device) findViewById(R.id.my_device_9);
	        devPower[9] = (Device) findViewById(R.id.my_device_10);
	        
	        btnAllOn = (Button) findViewById(R.id.io_control_btn_all_on);
	        btnAllClose = (Button)findViewById(R.id.io_control_btn_all_off);
	        btnTimer = (Button)findViewById(R.id.io_control_btn_all_timer);
	        rlActivity = (RelativeLayout)findViewById(R.id.io_control_rl);
	 }
	 
	 private void initDevPowerShow(int iPage)//0-10 1-20 2-30
	 {
		  int iDevNum = gListDeviceState.size();
		  
		  if(iPage*10>iDevNum)
		  {
			  return;
		  }
		  
		  int iShowDevNum = ((iDevNum-iPage*10)/10>0)?10:iDevNum%10;
		  
		  for(int i = 0; i < iShowDevNum; i++)
		  {
			    String strDevId = (String)gListDeviceState.get(i+iPage*10).get("dev_id");
			    String strDevName = (String)gListDeviceState.get(i+iPage*10).get("dev_name");
			    String strDevState = (String)gListDeviceState.get(i+iPage*10).get("dev_state");
		        
			    devPower[i].setDeviceId(i,iShowDevNum,Integer.valueOf(strDevId).intValue());
		        devPower[i].renameDevice(Integer.valueOf(strDevId).intValue(),strDevName);
		        devPower[i].setDevState(strDevState);
		        devPower[i].setDevText("");
		  }
		  for(int i = 0 ;i < iShowDevNum;i++)
		  {
			  devPower[i].setVisibility(View.VISIBLE);
		  }

	      for(int i = iShowDevNum; i < 10; i++)
	      {
	            devPower[i].setVisibility(View.INVISIBLE); 
	      }
	 }
	 
	 private void initDevClickCallBack()
	 {
		 for(int i=0;i<10;i++)
	     {
		        devPower[i].setonClick(new ICoallBack() {  
		              
		            @Override  
		            public void onClickButton(String s) {  
	
		            	String str_sub[]=s.split("#");
		            	if(str_sub.length<2)
		            	{
		            		return;
		            	}
		            	String str_type[]=str_sub[0].split(":");
		            	String str_id[] = str_sub[1].split(":");
		            	
		            	if(str_type.length<2||str_id.length<2)
		            	{
		            		return;
		            	}
		            	int devType = Integer.valueOf(str_type[1]);
		            	int devId = Integer.valueOf(str_id[1]); 
		            	
		            	if(1==devType)
		            	{
		            	    Toast.makeText(IOControl.this, "主机No"+String.valueOf(devId)+"关闭", Toast.LENGTH_SHORT).show();
		            	    closeDevice(devId);
		            	}
		            	else if(2==devType)
		            	{
		            		Toast.makeText(IOControl.this, "主机No"+String.valueOf(devId)+"打开", Toast.LENGTH_SHORT).show();
		            		openDevice(devId);
		            	}
		            	else if(3==devType)
		            	{
		            		/*Toast.makeText(PowerManage.this, "设备No"+String.valueOf(devId)+"定时", Toast.LENGTH_SHORT).show();
		            		StringBuilder strBlderErr = new StringBuilder();
		            		StringBuilder strBlderDevState = new StringBuilder();
		            		if(!checkDeviceStateCloseOrOpen(devId, strBlderErr, strBlderDevState))
		            		{
		            			Toast.makeText(PowerManage.this,strBlderErr.toString(), Toast.LENGTH_SHORT).show();
		            			return;
		            		}
		            		popDialogAndSendMsgTimer(devId,strBlderDevState.toString());*/
		            	}
		            }  
		        });  
	     }
		 btnAllOn.setOnClickListener(new MyClickListener());
	     btnAllClose.setOnClickListener(new MyClickListener());
	     btnTimer.setOnClickListener(new MyClickListener());
	     rlActivity.setOnTouchListener(new MyTouchListener());
	     
	     
	 }
	 private void openDevice(int devId)
	 {
		 if(devId != 0)
		 {
			 sendMsgCloseDevice(devId);
			 return;
		 }
		 for(int i=0;i<gListDeviceState.size();i++)
		 {
			 String strDevId = (String)gListDeviceState.get(i).get("dev_id");
			 int devIdTmp = Integer.valueOf(strDevId);
			 sendMsgCloseDevice(devIdTmp);
		 }
	 }
	 private void closeDevice(int devId)
	 {
		 if(devId != 0)
		 {
			 sendMsgOpenDevice(devId);
			 return;
		 }
		 for(int i=0;i<gListDeviceState.size();i++)
		 {
			 String strDevId = (String)gListDeviceState.get(i).get("dev_id");
			 int devIdTmp = Integer.valueOf(strDevId);
			 sendMsgOpenDevice(devIdTmp);
		 }
	 }
	 
	 private void sendMsgCloseDevice(int id)
	 {
		  String str = "";
		  String msg_id = new DecimalFormat("0000").format(id);
		  str = "&SS#1004#000001#0000#01#14$";  
		  
		  String str1 = (String) str.subSequence(0, 16);
		  String str2 = (String) str.subSequence(20,str.length());
		  
		  str=str1+msg_id+str2;
	      ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
	     
	      
	 }
	 private void sendMsgOpenDevice(int id)
	 {
		  String str = "";
		  String msg_id = new DecimalFormat("0000").format(id);
		  str = "&SS#1004#000001#0000#02#14$";  
		  String str1 = (String) str.subSequence(0, 16);
		  String str2 = (String) str.subSequence(20,str.length());
		  str=str1+msg_id+str2;
		  ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
		 
	 }
	 
	 public class MyClickListener implements OnClickListener 
	    {
	     
	        int type;
	        public void onClick(View v) 
	        {
	        	
	            switch (v.getId()) 
	            {
	            case R.id.power_manage_btn_all_on:
	            	//Toast.makeText(PowerManage.this, "所有设备打开", Toast.LENGTH_SHORT).show();
	            	openDevice(0);
	            	break;
	            case R.id.power_manage_btn_all_off:
	            	//Toast.makeText(PowerManage.this, "所有设备关闭", Toast.LENGTH_SHORT).show();
	            	closeDevice(0);
	            	break;
	            case R.id.power_manage_btn_all_timer:
	            	//Toast.makeText(PowerManage.this, "所有设备定时", Toast.LENGTH_SHORT).show();               	
	            	//StringBuilder strBlderErr = new StringBuilder();
	            	//StringBuilder strBlderDevState = new StringBuilder();
	        		//if(!checkDeviceStateCloseOrOpen(0 ,strBlderErr, strBlderDevState))
	        		//{
	        			//Toast.makeText(PowerManage.this,strBlderErr.toString(), Toast.LENGTH_SHORT).show();
	        			//return;
	        		//}
	        		//popDialogAndSendMsgTimer(0,strBlderDevState.toString());
	            	break;
	              
	            }
	      }
	    }
	 
	 	public class MyTouchListener implements OnTouchListener
		 {
	        float mPosX;
	        float mPosY;
	        float mCurPosX;
	        float mCurPosY;
	        
	        
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				
				// TODO Auto-generated method stub
				switch (event.getAction()) {

	            case MotionEvent.ACTION_DOWN:
	                mPosX = event.getX();
	                mPosY = event.getY();
	                break;
	            case MotionEvent.ACTION_MOVE:
	                mCurPosX = event.getX();
	                mCurPosY = event.getY();
	                break;
	            case MotionEvent.ACTION_UP:
	                if (mCurPosY - mPosY > 0
	                     && (Math.abs(mCurPosY - mPosY) > 25)) 
	                {
	                    //向下滑動
	                	if((gShowCurPage+1)*10>=gListDeviceState.size())
	                	{
	                		Toast.makeText(IOControl.this, "无可加载更多设备", Toast.LENGTH_SHORT).show();
	                		return false;
	                	}
	                	initDevPowerShow(++gShowCurPage);
	                	
	                } else if (mCurPosY - mPosY < 0
	                        && (Math.abs(mCurPosY - mPosY) > 25)) 
	                {
	                    //向上滑动
	                	if(gShowCurPage<=0)
	                	{
	                		Toast.makeText(IOControl.this, "无可加载更多设备", Toast.LENGTH_SHORT).show();
	                		return false;
	                	}
	                	initDevPowerShow(--gShowCurPage);

	                }
	                
	                break;
	            } 
				return true;
			}
			 
		 }
    
   
    
}
