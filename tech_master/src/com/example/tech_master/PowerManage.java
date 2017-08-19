package com.example.tech_master;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


import com.example.device.Device;
import com.example.device.Device.ICoallBack;
import com.example.dialog.HelpDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.view.KeyEvent;

import android.view.View;

import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;



 public class PowerManage extends Activity {
	
	private Device []devPower;
    private Button btnAllOn;
    private Button btnAllClose;
    private Button btnTimer;
    private RelativeLayout rlActivity;
    Ringtone r;
    
    static private ArrayList<HashMap<String, String>> gListDeviceState ; 
    
    private int gShowCurPage = 0;

    
    protected void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_power_manage);
        initMediaPlay();
        initWidgetHandle();
        initDevClickCallBack();//�豸����ص�����

     
        initShowCurPage();
        
        initDevPowerShow(gShowCurPage);//��ʼ��������ʾ
        timer.schedule(task,1000,1000); 
    }
	 private void initMediaPlay()
	 {
		    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			r = RingtoneManager.getRingtone(getApplicationContext(), notification);	
			
	 }
	 private void startMediaPlay()
	 {
		 r.play();
	 }
	 private void stopMediaPlay()
	 {
		 r.stop();
	 }
		    	     
    
	 
	 private void initWidgetHandle()
	 {
		    devPower = new Device[10] ;//ÿ����ʾ�豸״̬
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
	        
	        btnAllOn = (Button) findViewById(R.id.power_manage_btn_all_on);
	        btnAllClose = (Button)findViewById(R.id.power_manage_btn_all_off);
	        btnTimer = (Button)findViewById(R.id.power_manage_btn_all_timer);
	        rlActivity = (RelativeLayout)findViewById(R.id.power_manage_rl);
	 }
	 
	 private void initDevPowerShow(int iPage)//0-10 1-20 2-30
	 {
		 
		  gListDeviceState = ApplicationVar.getInstance().getDevStateList();
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
	 private void initShowCurPage()
	 {
		 gShowCurPage = 0;
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
		            	    Toast.makeText(PowerManage.this, "�豸No"+String.valueOf(devId)+"�ر�", Toast.LENGTH_SHORT).show();
		            	    closeDevice(devId);
		            	}
		            	else if(2==devType)
		            	{
		            		Toast.makeText(PowerManage.this, "�豸No"+String.valueOf(devId)+"��", Toast.LENGTH_SHORT).show();
		            		openDevice(devId);
		            	}
		            	else if(3==devType)
		            	{
		            		Toast.makeText(PowerManage.this, "�豸No"+String.valueOf(devId)+"��ʱ", Toast.LENGTH_SHORT).show();
		            		StringBuilder strBlderErr = new StringBuilder();
		            		StringBuilder strBlderDevState = new StringBuilder();
		            		if(!checkDeviceStateCloseOrOpen(devId, strBlderErr, strBlderDevState))
		            		{
		            			Toast.makeText(PowerManage.this,strBlderErr.toString(), Toast.LENGTH_SHORT).show();
		            			return;
		            		}
		            		popDialogAndSendMsgTimer(devId,strBlderDevState.toString());
		            	}
		            }  
		        });  
	     }
		 btnAllOn.setOnClickListener(new MyClickListener());
	     btnAllClose.setOnClickListener(new MyClickListener());
	     btnTimer.setOnClickListener(new MyClickListener());
	     rlActivity.setOnTouchListener(new MyTouchListener());
	     
	     
	 }
	 
	 
	 public class MyClickListener implements OnClickListener 
    {
     
        int type;
        public void onClick(View v) 
        {
        	
            switch (v.getId()) 
            {
            case R.id.power_manage_btn_all_on:
            	Toast.makeText(PowerManage.this, "�����豸��", Toast.LENGTH_SHORT).show();
            	openDevice(0);
            	break;
            case R.id.power_manage_btn_all_off:
            	Toast.makeText(PowerManage.this, "�����豸�ر�", Toast.LENGTH_SHORT).show();
            	closeDevice(0);
            	break;
            case R.id.power_manage_btn_all_timer:
            	//Toast.makeText(PowerManage.this, "�����豸��ʱ", Toast.LENGTH_SHORT).show();               	
            	StringBuilder strBlderErr = new StringBuilder();
            	StringBuilder strBlderDevState = new StringBuilder();
        		if(!checkDeviceStateCloseOrOpen(0 ,strBlderErr, strBlderDevState))
        		{
        			Toast.makeText(PowerManage.this,strBlderErr.toString(), Toast.LENGTH_SHORT).show();
        			return;
        		}
        		popDialogAndSendMsgTimer(0,strBlderDevState.toString());
            	break;
              
            }
      }
    }
	 
	 
	 
	 private int findIdxByDevId(int iDevId)
	 {
		 int i = 0;
		 for ( i = 0; i < gListDeviceState.size(); i++)
		 {
			 String strDevId = gListDeviceState.get(i).get("dev_id");
			 
			 if(iDevId == Integer.valueOf(strDevId).intValue())
			 {
				 break;
			 }
		 }
		 
		 return (i < gListDeviceState.size())? i : -1;
	 }
	 
	 
	 /////////////*****�·�����****************************/
	 private boolean checkDeviceStateCloseOrOpen(int devId ,StringBuilder strBlderErr, StringBuilder strBlderDevState)
	 {
		 strBlderErr.delete( 0, strBlderErr.length());
		 
		 int idx = (devId == 0)?0:findIdxByDevId(devId);
		 if(idx<0)
		 {
			 strBlderErr.append("δ���ҵ����豸");
			 return false;
		 }
		 String strDevState = gListDeviceState.get(idx).get("dev_state");
		 String strDevName = gListDeviceState.get(idx).get("dev_name");
		
		 if((!strDevState.equals("open"))&&(!strDevState.equals("close")))
		 {
			 strBlderErr.append(strDevName+"δ�ڿ�����ػ�״̬");
			 return false;
		 }
  
		 if(devId == 0)//�����豸��ʱ����
		 {
			 for(int i=1;i<gListDeviceState.size();i++)
			 {
				 String strDevStateTmp = gListDeviceState.get(i).get("dev_state"); 
				 String strDevNameTmp = gListDeviceState.get(i).get("dev_name"); 
				 if(!strDevState.equals(strDevStateTmp))
				 {
					 strBlderErr.append(strDevName+"��"+strDevNameTmp+"״̬��һ��");
					 return false;
				 }
			 }			 			 
		 }
		 strBlderDevState.append(strDevState);
		 return true;
	 }
	
	 private boolean popDialogAndSendMsgTimer(int devId,String strDevState)
	 {
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);  
         View view = View.inflate(this, R.layout.msg_date_time_dialog, null);  
         final TimePicker timePickerStartTime = (android.widget.TimePicker) view.findViewById(R.id.time_picker1); 
         final int iDevId = devId;
         final int showType = (strDevState.equals("close")?0:1);
         builder.setView(view);  

         Calendar cal = Calendar.getInstance();  
         cal.setTimeInMillis(System.currentTimeMillis());  

         timePickerStartTime.setIs24HourView(true);  
         timePickerStartTime.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));  
         timePickerStartTime.setCurrentMinute(Calendar.MINUTE); 
         if(0==showType)
         {
        	 builder.setTitle("����ʱ��"); 
         }
         else
         {
        	 builder.setTitle("�ػ�ʱ��"); 
         }
         
          
         builder.setPositiveButton("ȷ  ��", new DialogInterface.OnClickListener() {  

             @Override  
             public void onClick(DialogInterface dialog, int which)
             {  
                 String  strTime=(new DecimalFormat("00").format(timePickerStartTime.getCurrentHour()))  
                             +(":")+(new DecimalFormat("00").format(timePickerStartTime.getCurrentMinute()))  
                             +(":")+("00");
                 if(iDevId != 0)
                 {
                	 sendMsgTimerDevice(iDevId,strTime,showType);
                	 
                 }
                 else
                 {
                	 for(int i=0;i<gListDeviceState.size();i++)
                	 {
                		 String strDevId = gListDeviceState.get(i).get("dev_id");
                		 sendMsgTimerDevice(Integer.valueOf(strDevId).intValue(),strTime,showType);
                	 }
                 }
                 dialog.cancel();  
             }  
         });  
         builder.show();
		 return true;
	 }
	 
	 
	 private void openDevice(int devId)
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
	 private void closeDevice(int devId)
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
	 
/////////////*****�·���Ϣ��ʼ****************************/	 
	 private void sendMsgCloseDevice(int id)
	 {
		  String str = "";
		  String msg_id = new DecimalFormat("0000").format(id);
		  str = "&SS#1000#000001#0000#01$";  
		  
		  String str1 = (String) str.subSequence(0, 16);
		  String str2 = (String) str.subSequence(20,str.length());
		  
		  str=str1+msg_id+str2;
	      ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
	     
	      
	 }
	 private void sendMsgOpenDevice(int id)
	 {
		  String str = "";
		  String msg_id = new DecimalFormat("0000").format(id);
		  str = "&SS#1000#000001#0000#02$";  
		  String str1 = (String) str.subSequence(0, 16);
		  String str2 = (String) str.subSequence(20,str.length());
		  str=str1+msg_id+str2;
		  ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
		 
	 }
	 
	 private void sendMsgTimerDevice(int id,String time,int type)
	 {
		  String str_open[] = time.split(":");
		  String str = "";
		  if(str_open.length<3)
		  {
			  return;
		  }
		  String msg_id = new DecimalFormat("0000").format(id);
		  if(0==type)
		  {
			  str = "&SS#1003#000001#0000#01#12#23#00$"; //���ö�ʱ��
		  }
		  else
		  {
			  str = "&SS#1003#000001#0000#03#12#23#00$"; //���ö�ʱ��	  
		  }
		  
		  String str_sub[] = str.split("#");
		  str_sub[3]=msg_id;
		  str_sub[5]=str_open[0];
		  str_sub[6]=str_open[1];
		  str_sub[7]=str_open[2];
		  str = "";
		  for(int i=0;i<7;i++)
		  {
			  str += str_sub[i];
			  str +="#";
		  }
		  str += str_sub[7];
		  str +="$";
		  ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
	 }
	 private void sendMsgRecvHelp(int id)
	 {
		  String str = "";
		  String msg_id = new DecimalFormat("0000").format(id);
		  str = "&SS#1008#000001#0000#00$";  
		  String str1 = (String) str.subSequence(0, 16);
		  String str2 = (String) str.subSequence(20,str.length());
		  
		  str=str1+msg_id+str2;
		  ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
	      //updateDevState(id,"help");
	 }
	 
/////////////*****�·���Ϣ����****************************/
	 
	   	
////////////////////��ʱˢ����ʾ���� ÿ��1sִ��/////////
	   	
	   	public boolean onKeyDown(int keyCode, KeyEvent event) { 
	    	 if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
	    	 { 
	    		
	    		 int i=0;
	    		for(i=0;i<gListDeviceState.size();i++)
	    		{
	    			if(gListDeviceState.get(i).get("dev_state").equals("timer_open")||gListDeviceState.get(i).get("dev_state").equals("timer_open"))
	    			{
	    				break;
	    			}
	    		}
	    		if(i!=gListDeviceState.size())
	    		{
	    			Toast.makeText(PowerManage.this, "��ǰ���豸���� ��ʱ״̬���˳�ʧ��", Toast.LENGTH_SHORT).show();
	    			return false;
	    		}
	    		 timer.cancel();
	    		//moveTaskToBack(false);
	    		 
	    	 } 
	    	 
	    	 return super.onKeyDown(keyCode, event) ;
	    	 
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
	                    //���»���
	                	if((gShowCurPage+1)*10>=gListDeviceState.size())
	                	{
	                		Toast.makeText(PowerManage.this, "�޿ɼ��ظ����豸", Toast.LENGTH_SHORT).show();
	                		return false;
	                	}
	                	initDevPowerShow(++gShowCurPage);
	                	
	                } else if (mCurPosY - mPosY < 0
	                        && (Math.abs(mCurPosY - mPosY) > 25)) 
	                {
	                    //���ϻ���
	                	if(gShowCurPage<=0)
	                	{
	                		Toast.makeText(PowerManage.this, "�޿ɼ��ظ����豸", Toast.LENGTH_SHORT).show();
	                		return false;
	                	}
	                	initDevPowerShow(--gShowCurPage);

	                }
	                
	                break;
	            } 
				return true;
			}
			 
		 }
        
	    private final Timer timer = new Timer();  
		private TimerTask task = new TimerTask() 
		{  
		        @Override  
		        public void run() 
		        {  
		        	 try {
		        		
		        		 Message message = new Message();
		                 message.what = 1;
		                 handler.sendMessage(message);
			              
		                }
		        	 
		            	
		             catch (Exception e) 
		            {
		            }
		        }  
		    }; 
		    final Handler handler = new Handler() 
		    {
		        public void handleMessage(Message msg) 
		        {
		            switch (msg.what) 
		            {
		            case 1:
		                update();
		                break;
		            }
		            super.handleMessage(msg);
		        }
		        void update() 
		        {
		        	//gListDeviceState = ApplicationVar.getInstance().getDevStateList();
		        	for(int i = 0;i < 10;i++)
		              {  
		            	  if(View.INVISIBLE == devPower[i].getVisibility())
		            	  {
		            		  continue;
		            	  }
	            		  int devId = devPower[i].getDevId();
	            		  int idx = findIdxByDevId(devId);
	            		  if ( idx<0 )
	            		  {
	            			  continue;
	            		  }
	            		  String strDevState = gListDeviceState.get(idx).get("dev_state");
	            		  if(strDevState.equals("timer_open")||strDevState.equals("timer_close"))//����Ƕ�ʱ����
	            		  {
	            			  String strSetTimer = gListDeviceState.get(idx).get("dev_set_timer");
	            			  devPower[i].setDevTimer(strSetTimer);         			 
	            		  }
	            		  
	            		  if(strDevState.equals("open"))
	            		  {
	            			  String strOpenTimer = gListDeviceState.get(idx).get("dev_open_timer");
	            			  devPower[i].setDevOpenTime(strOpenTimer);
	            		  }
	            		  
	            		  if(gListDeviceState.get(idx).get("dev_help").equals("TRUE"))
	            		  {
	            			  // msg help
	            			  gListDeviceState.get(idx).put("dev_help", "FALSE");
	            			  showDialogHelp(devId);
	            		  }
	            		  
	            		  
	            		  devPower[i].setDevState(strDevState);
		            }
		        }
		    };
	 private void showDialogHelp(final int devId)
	   {
		   HelpDialog.Builder builder = new HelpDialog.Builder(this);
		   
			builder.setTitle("�豸No."+String.valueOf(devId)+"�������");
			builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//������Ĳ�������
					dialog.dismiss();
					sendMsgRecvHelp(devId);
					stopMediaPlay();
				}
			});
			builder.create().show();	
			startMediaPlay();
	   }
		        
 }
	   	
	   	
	   	


 
