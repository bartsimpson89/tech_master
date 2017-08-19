package com.example.device;

import java.text.DecimalFormat;

import java.util.HashMap;



import android.content.Context;


import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.serve.MyTcpServerThread;
import com.example.tech_master.ApplicationVar;
import com.example.tech_master.R;



public class Device extends RelativeLayout
{
	private int gDevId = 0;
    private String  gStrOpenTime = ApplicationVar.timeSecond();
    private String  gStrTimer = ApplicationVar.timeSecond();
    private String  gStrDevState = "offline";
    
    private RelativeLayout ll;
    private TextView tv_title;
    private TextView tv_info;
    private ImageView    btn_close;
    private ImageView    btn_open;
    private ImageView    btn_timer;
    private TextView tv_close;
    private TextView tv_open;
    private TextView tv_timer ;
    
	//"个数 "   "x：y:h:w:间隔列:间隔行:行1个数:行2个数:行3个数：字体1：字体2：字体3"
	@SuppressWarnings("serial")
	HashMap<String,String> mapProperty = new HashMap<String,String>()
	{ 
		{ 
		  put("1", "450:180:350:490:50:0:1:0:0:30:30:20"); 
		  put("2", "300:260:300:360:50:0:2:0:0:30:30:20"); 
		  put("3", "200:260:250:300:50:0:3:0:0:30:30:20"); 
		  put("4", "150:260:200:240:50:0:4:0:0:30:30:20"); 
		 
		  put("5", "150:180:180:216:60:30:4:1:0:18:20:16"); 
		  put("6", "150:180:180:216:60:30:4:2:0:18:20:16"); 
		  put("7", "150:180:180:216:60:30:4:3:0:18:20:16"); 
		  put("8", "150:180:180:216:60:30:4:4:0:18:20:16"); 
		 
		  put("9", "120:180:180:200:40:30:5:4:0:18:18:12"); 
		  put("10", "120:180:180:200:40:30:5:5:0:18:18:12");  
		}

	};
	
	 public Device(Context context) 
	 {
		super(context);
		// TODO Auto-generated constructor stub
	 }
	public Device(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub

		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_device, this);
		 ll = (RelativeLayout)findViewById(R.id.view_ll_device);
	    tv_title = (TextView)findViewById(R.id.view_tv_device_title);
	    tv_info = (TextView)findViewById(R.id.view_tv_device_info);
	    btn_close = (ImageView)findViewById(R.id.view_btn_close);
	    btn_open = (ImageView )findViewById(R.id.view_btn_open);
	    btn_timer = (ImageView )findViewById(R.id.view_btn_timer);
	    tv_close = (TextView)findViewById(R.id.view_tv_close);
	    tv_open = (TextView)findViewById(R.id.view_tv_open);
	    tv_timer = (TextView)findViewById(R.id.view_tv_timer);
		
	}
	public void renameDevice(int dev_id,String name)
	{
		String dev_id_name = "";
    	dev_id_name = "NO."+String.valueOf(dev_id);
		tv_title.setText(name+dev_id_name);
	}

    public void setDeviceId(int index,int num,int id) //0-9 10-20 20-30
    {
    	gDevId = id;
    	String dev_id_name = "";
    	dev_id_name = "NO."+String.valueOf(id);
    	String key = String.valueOf(num);
    	String value = mapProperty.get(key);
    	if(value==null)
    	{
    		return ;
    	}
    	String str[] = value.split(":");
    	int x = Integer.valueOf(str[0]);
    	int y = Integer.valueOf(str[1]);
    	int w = Integer.valueOf(str[2]);
    	int h = Integer.valueOf(str[3]);
    	
    	int spc = Integer.valueOf(str[4]);
    	int spc2 = Integer.valueOf(str[5]);
    	
    	int line1_num = Integer.valueOf(str[6]);
    	int line2_num = Integer.valueOf(str[7]);
    	//int line3_num = Integer.valueOf(str[8]);
    	
    	int title_bold = Integer.valueOf(str[9]);
    	int info_bold = Integer.valueOf(str[10]);
    	int btn_bold = Integer.valueOf(str[11]);
    	
    	TextView tv_title = (TextView)findViewById(R.id.view_tv_device_title);
    	tv_title.setText("设备"+dev_id_name);
    	
    	int res_x=0;
    	int res_y=0;
    	
    	index = index%10; //10 20 30
    	   	
    	if(index<line1_num)
    	{
    		res_x = x+(w+spc)*index;
    		res_y = y;
    	}
    	else if(index>=line1_num&&index<(line2_num+line1_num))
    	{
    		res_x = x+(w+spc)*(index-line1_num);
    		res_y = y+h+spc2;
    	}
    	resizeItem(res_x,res_y,w,h);
    	resizeTextbold(title_bold,info_bold,btn_bold);
    	
    }
    
    private void resizeTextbold(int x1,int x2, int x3)
    {        
         tv_title.setTextSize(x1);
         tv_info.setTextSize(x2);
         tv_close.setTextSize(x3);
         tv_open.setTextSize(x3); 
         tv_timer.setTextSize(x3);
         
         btn_close.setOnClickListener(new MyClickListener());
         btn_open.setOnClickListener(new MyClickListener());
         btn_timer.setOnClickListener(new MyClickListener());
    	
    }
    
    private void resizeItem(int x,int y, int w,int h)
    {
        ll.setX(x);
        ll.setY(y);
    	tv_title.setWidth(w);
    	tv_title.setHeight(h/5);

    	tv_info.setWidth(w);
    	tv_info.setHeight(h/2);
    	
    	         
         btn_close.setLayoutParams(new LinearLayout.LayoutParams(w/3-10,w/3-10));
         btn_open.setLayoutParams(new LinearLayout.LayoutParams(w/3-10,w/3-10));
         btn_timer.setLayoutParams(new LinearLayout.LayoutParams(w/3-10,w/3-10));
         
         tv_close.setLayoutParams(new LinearLayout.LayoutParams(w/3,w/6));
         tv_open.setLayoutParams(new LinearLayout.LayoutParams(w/3,w/6));
         tv_timer.setLayoutParams(new LinearLayout.LayoutParams(w/3,w/6));

    }
    class MyClickListener implements OnClickListener 
    {
        @Override
        public void onClick(View v) 
        {
        	int click_type =0; //点击的类型 开机关机定时
            switch (v.getId()) 
            {
            case R.id.view_btn_close:
            	click_type = 1;
            	icallBack.onClickButton("type:"+String.valueOf(click_type)+"#"+"id:"+String.valueOf(gDevId)); 
            	break;
            case R.id.view_btn_open:
            	click_type = 2;
            	icallBack.onClickButton("type:"+String.valueOf(click_type)+"#"+"id:"+String.valueOf(gDevId)); 
            	break;
            case R.id.view_btn_timer:
            	click_type = 3;
            	icallBack.onClickButton("type:"+String.valueOf(click_type)+"#"+"id:"+String.valueOf(gDevId)); 
                break;
            default:
            	click_type = 0;
                break;
            }
      }
    }
    
    public int getDevId()
    {
    	return gDevId;
    }
    
    public String getDevState()
    {
    	return gStrDevState;
    }
    
    public void setDevTimer(String strDevTimer)
    {
    	if(strDevTimer.equals(gStrTimer))
    	{
    		return;
    	}
    	gStrTimer = strDevTimer;
    }
  
    public void setDevOpenTime(String strDevTime)
    {
    	if(strDevTime.equals(gStrOpenTime))
    	{
    		return;
    	}
    	gStrOpenTime = strDevTime;
    }
    public void setDevText(String strText)
    {
    	tv_info.setText(strText);
    }
    
	public void setDevState(String state)//设置设备状态
    {
		
    	if(state.equals("offline"))
    	{
    		tv_info.setBackgroundResource(R.drawable.shape_device_frame_offline);
    		tv_info.setText("离线");
    	}
    	else if(state.equals("online"))
    	{
    		tv_info.setBackgroundResource(R.drawable.shape_device_frame_online);
    		tv_info.setText("在线");

    	}
    	else if(state.equals("close"))
    	{
    		tv_info.setBackgroundResource(R.drawable.shape_device_frame_online);
    		tv_info.setText("关机");

    	}
    	else if(state.equals("open"))
    	{
    		tv_info.setBackgroundResource(R.drawable.shape_device_frame_online);
    		tv_info.setText("运行时间\n"+getDiffTimer(gStrOpenTime,ApplicationVar.timeSecond()));
    		
    	}
    	else if(state.equals("timer_open")) 
    	{
   		
    		tv_info.setBackgroundResource(R.drawable.shape_device_frame_online);
    		
    		tv_info.setText("距离开机时间\n"+getDiffTimer(ApplicationVar.timeSecond(), gStrTimer));
    		
    	}
    	else if(state.equals("timer_close"))
    	{
    		tv_info.setBackgroundResource(R.drawable.shape_device_frame_online);
  
    		tv_info.setText("距离关机时间\n"+getDiffTimer(ApplicationVar.timeSecond(), gStrTimer));
    	}
    	gStrDevState = state;
    	
    	
    	
    }
	public String getDiffTimer(String strTimer,String strCurTime)
	{
		long lDiffTime =  MyTcpServerThread.DiffTimeSec(strTimer, strCurTime);
		if(lDiffTime<0)
		{
			lDiffTime = 0;
		}
		long lSecTime = lDiffTime/1000;
		String strHour = new DecimalFormat("00").format(lSecTime/3600);
		String strMin =new DecimalFormat("00").format(lSecTime/60%60);
		String strSec = new DecimalFormat("00").format(lSecTime%60);
		return strHour+":"+strMin+":"+strSec;
	}
	
	
	
    ICoallBack icallBack = null;  
    public interface ICoallBack{  
        public void onClickButton(String s);  
    } 
    public void setonClick(ICoallBack iBack)  
    {  
        icallBack = iBack;  
    }  
    
	   
}
