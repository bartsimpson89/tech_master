package com.example.tech_master;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View.OnClickListener;
public class AlarmState extends Activity {
	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	private final int FP = ViewGroup.LayoutParams.FILL_PARENT;
    private TableRow []trAlarm;
    private TextView []tvAlarm;
    private ArrayList<HashMap<String, Object>> gDeviceItem;
    private ArrayList<HashMap<String, String>> gDeviceStateAlarm;
    
	 protected void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.activity_alarm_state);
	        trAlarm = new TableRow[30];
	        tvAlarm =  new TextView[30*6];
	        gDeviceItem  = new ArrayList<HashMap<String, Object>>();
	       gDeviceStateAlarm  = new ArrayList<HashMap<String, String>>();
	        
	        
	        
	        
	        showTableView();
	        
	        
	          
	        initDevStateAlarm();
	        initSetCallBack();

	        sendReadAlarm();
	        timer.schedule(task,1000,2000); //50ms数据处理
	        
	    }
	 private void initDevStateAlarm()
	 {
		 ApplicationVar.getInstance().getArrayDevice(gDeviceItem);
		 for(int i=0;i<gDeviceItem.size();i++)
		 {
			 HashMap<String, String> map = new HashMap<String,String>();
			 map.put("dev_id",(String) gDeviceItem.get(i).get("dev_id"));
			 map.put("alarm1", "0");
			 map.put("alarm2", "0");
			 map.put("alarm3", "0");
			 map.put("alarm4", "0");
			 gDeviceStateAlarm.add(map);
		 }
		 
		 
		 
	 }
	  private void showTableView()
	  {
		  TableLayout  tableLayout = (TableLayout)findViewById(R.id.alarm_state_t2);
	        tableLayout.setStretchAllColumns(true);
	        for(int row=0;row<30;row++)
	        {
	        	trAlarm[row] = new TableRow(this);
	        	trAlarm[row].setBackgroundColor(Color.rgb(222, 220, 210));
	        	for(int col=0;col<6;col++)
	        	{
	        		tvAlarm[col+row*6] = new TextView(this);
	        		if(col>=0&&col<5)
	        		{
	        			
		        		tvAlarm[col+row*6].setBackgroundResource(R.drawable.shape_table);
	        			if(col == 0)
		        		{
		        		    tvAlarm[col+row*6].setText("设备No."+String.valueOf(row+1));
		        		}
		        		else 
		        		{
		        			tvAlarm[col+row*6].setText("0");
		        		}
	        		  
	        		}
	        		else
	        		{
		        		tvAlarm[col+row*6].setBackgroundResource(R.drawable.shape_table_grey);
		        		tvAlarm[col+row*6].setClickable(true);
		        		tvAlarm[col+row*6].setFocusable(true);
		        		tvAlarm[col+row*6].setId(row+1);
		        		tvAlarm[col+row*6].setOnClickListener(new OnClickListener() 
		        		{ 
		        			public void onClick(View view) 
		        			{
		        				int id = view.getId();
		        				Toast.makeText(AlarmState.this, "设备 No."+String.valueOf(id)+"清除", Toast.LENGTH_SHORT).show();
		        				sendClearAlarm(id);
		        			}
		        		});
		        		tvAlarm[col+row*6].setText("清除");
	        		}
	        		  trAlarm[row].addView(tvAlarm[col+row*6]);
	        	}
	            tableLayout.addView(trAlarm[row],new TableLayout.LayoutParams(WC, FP));
	        } 
		  
	  }
/////////////*****发送信息****************************/
////////////*****下发信息*********************/
private void sendReadAlarm()
{
	for(int i=0;i<gDeviceItem.size();i++)
	{
	  String id = (String)gDeviceItem.get(i).get("dev_id");
	  int dev_id = Integer.valueOf(id);
	  String str = "";
	  String msg_id = new DecimalFormat("0000").format(dev_id);
	  str = "&SS#1001#000001#0000#0000$";  
	  String str1 = (String) str.subSequence(0, 16);
	  str=str1+msg_id+"#01#0000#0000#0000$";
	  ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id,str);
	}
}	  
private void sendClearAlarm(int id)
{
	  int dev_id = Integer.valueOf(id);
	  String str = "";
	  String msg_id = new DecimalFormat("0000").format(dev_id);
	  str = "&SS#1001#000001#0000#0000$";  
	  String str1 = (String) str.subSequence(0, 16);
	  str=str1+msg_id+"#02#0000#0000#0000$";
	  ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id,str);
	
}
	  
	  
/////////////////////发送end/////////////////////////
		
/////////////*****接受信息****************************/

private void initSetCallBack()
{
	 ApplicationVar.getInstance().setCallBackAlarm(new CallBackDevAlarm()
		{
			@Override
			public void UpdateDevStateAlarm(String strDevId, String strState) 
			{
				// TODO Auto-generated method stub
				
				
				int idx = findIdxByDevId(Integer.valueOf(strDevId).intValue());
				
				if(idx < 0)
				{
					return;
				}
				String []msgSplit =strState.split(":");
				
				if(isVaildNum(strDevId)&&isVaildNum(msgSplit[0])&&isVaildNum(msgSplit[1])&&isVaildNum(msgSplit[2]))
				{
					
					    int alarm1 = Integer.valueOf(msgSplit[0]).intValue();
					    int alarm2 = Integer.valueOf(msgSplit[1]).intValue();
					    int alarm3 = Integer.valueOf(msgSplit[2]).intValue();
					    int alarm4 = alarm1+alarm2+alarm3;
					    
					    gDeviceStateAlarm.get(idx).put("alarm1",String.valueOf(alarm1));
					    gDeviceStateAlarm.get(idx).put("alarm2",String.valueOf(alarm2));
					    gDeviceStateAlarm.get(idx).put("alarm3",String.valueOf(alarm3));
					    gDeviceStateAlarm.get(idx).put("alarm4",String.valueOf(alarm4));
					    
					    
					    
				}
				
			}
			

    });
} 

		

		private int findIdxByDevId(int iDevId)
		{
			 int i = 0;
			 for ( i = 0; i < gDeviceStateAlarm.size(); i++)
			 {
				 String strDevId = (String)gDeviceStateAlarm.get(i).get("dev_id");
				 
				 if(iDevId == Integer.valueOf(strDevId).intValue())
				 {
					 break;
				 }
			 }
			 
			 return (i < gDeviceStateAlarm.size())? i : -1;
		}
		private Boolean isVaildNum(String value)//是否是合法数字
		{
			if(value == null)
			{
				return false;
			}
			Pattern p = Pattern.compile("[0-9]*"); 
		     Matcher m = p.matcher(value); 
		     if(m.matches() )
		     {
		        return true;
		     }
		     else
		     {
			   return false;
		     }
		}
		   
	 
		 
		 private final Timer timer = new Timer();  
			private TimerTask task = new TimerTask() {  
			        @Override  
			        public void run() {  
			        	 try {
			        		  
					    	  sendReadAlarm();
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
			        	for(int i = 0;i < gDeviceStateAlarm.size();i++)
			              {  
			            	    int devId = Integer.valueOf(gDeviceStateAlarm.get(i).get("dev_id")).intValue();
			            	    tvAlarm[1+(devId-1)*6].setText(gDeviceStateAlarm.get(i).get("alarm1"));
							    tvAlarm[2+(devId-1)*6].setText(gDeviceStateAlarm.get(i).get("alarm2"));
							    tvAlarm[3+(devId-1)*6].setText(gDeviceStateAlarm.get(i).get("alarm3"));
							    tvAlarm[4+(devId-1)*6].setText(gDeviceStateAlarm.get(i).get("alarm4"));	            	  
			              } 
			        }
			    };
		 
		
		
	    
	    public boolean onKeyDown(int keyCode, KeyEvent event) { 
	    	 if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
	    	 { 
	 
	    		 timer.cancel();
	    		
	    	 } 
	    	 return super.onKeyDown(keyCode, event) ;
	    	 
	    	}
		 
		
}
	  
   
  
