package com.example.tech_master;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.tech_master.PowerManage.MyClickListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChannelManage extends Activity 
{
	private Button btnChannelInputAdd;
	private Button btnChannelInputDel;
	private Button btnChannelOutputAdd;
	private Button btnChannelOutputDel;
	
    private Button btnChannelSave;	

	private Button []channelInput=new Button[10];
	private Button []channelOutput=new Button[10];
	
	private EditText etDevId;
	
	
	private int curVisibleChannelInput=0;
	private int curVisibleChannelOutput=0;
	private int devId=0;
	
	public HashMap<String, Object> gChannelInfo;
	protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_channel_manage);
        
        btnChannelInputAdd = (Button) findViewById(R.id.channel_manage_btn_4_1);
        btnChannelInputDel = (Button) findViewById(R.id.channel_manage_btn_4_2);
        btnChannelOutputAdd = (Button) findViewById(R.id.channel_manage_btn_4_3);
        btnChannelOutputDel = (Button) findViewById(R.id.channel_manage_btn_4_4);
        btnChannelSave = (Button) findViewById(R.id.channel_manage_btn_7_1);
        etDevId = (EditText) findViewById(R.id.channel_manage_2_2);
        
        
        
        btnChannelInputAdd.setOnClickListener(new MyClickListener());
        btnChannelInputDel.setOnClickListener(new MyClickListener());
        btnChannelOutputAdd.setOnClickListener(new MyClickListener());
        btnChannelOutputDel.setOnClickListener(new MyClickListener());
        btnChannelSave.setOnClickListener(new MyClickListener());
        
        channelInput[0] = (Button) findViewById(R.id.channel_manage_btn_in_1);
        channelInput[1] = (Button) findViewById(R.id.channel_manage_btn_in_2);
        channelInput[2] = (Button) findViewById(R.id.channel_manage_btn_in_3);
        channelInput[3] = (Button) findViewById(R.id.channel_manage_btn_in_4);
        channelInput[4] = (Button) findViewById(R.id.channel_manage_btn_in_5);
        channelInput[5] = (Button) findViewById(R.id.channel_manage_btn_in_6);
        channelInput[6] = (Button) findViewById(R.id.channel_manage_btn_in_7);
        channelInput[7] = (Button) findViewById(R.id.channel_manage_btn_in_8);
        channelInput[8] = (Button) findViewById(R.id.channel_manage_btn_in_9);
        channelInput[9] = (Button) findViewById(R.id.channel_manage_btn_in_10);
        
        
        channelOutput[0] = (Button) findViewById(R.id.channel_manage_btn_out_1);
        channelOutput[1] = (Button) findViewById(R.id.channel_manage_btn_out_2);
        channelOutput[2] = (Button) findViewById(R.id.channel_manage_btn_out_3);
        channelOutput[3] = (Button) findViewById(R.id.channel_manage_btn_out_4);
        channelOutput[4] = (Button) findViewById(R.id.channel_manage_btn_out_5);
        channelOutput[5] = (Button) findViewById(R.id.channel_manage_btn_out_6);
        channelOutput[6] = (Button) findViewById(R.id.channel_manage_btn_out_7);
        channelOutput[7] = (Button) findViewById(R.id.channel_manage_btn_out_8);
        channelOutput[8] = (Button) findViewById(R.id.channel_manage_btn_out_9);
        channelOutput[9] = (Button) findViewById(R.id.channel_manage_btn_out_10);
        
        
        gChannelInfo = ApplicationVar.getInstance().getChannelInfo();
        
        String strDevId = (String)gChannelInfo.get("DevId");
        String strChannelInput = (String)gChannelInfo.get("InputChannel");
        String strChannelOutput = (String)gChannelInfo.get("OutputChannel");
        if((strDevId!=null)&&(strChannelInput!=null)&&(strChannelOutput!=null))
        {
	        devId = Integer.valueOf(strDevId);
	        curVisibleChannelInput = Integer.valueOf(strChannelInput);
	        curVisibleChannelOutput = Integer.valueOf(strChannelOutput);
        }
        else
        {
        	devId = 0;
	        curVisibleChannelInput = 0;
	        curVisibleChannelOutput = 0;
        }
        etDevId.setText(String.valueOf(devId));
        for(int i=0;i<10;i++)
        {
        	if(i<curVisibleChannelInput)
        	{
        	    channelInput[i].setVisibility(View.VISIBLE);
        	}
        	else 
        	{
        		channelInput[i].setVisibility(View.INVISIBLE);
        	}
        	if(i<curVisibleChannelOutput)
        	{
        		channelOutput[i].setVisibility(View.VISIBLE);
        	}
        	else
        	{
        		channelOutput[i].setVisibility(View.INVISIBLE);
        	}
        }
        
    }
	public class MyClickListener implements OnClickListener 
    {
        public void onClick(View v) 
        {
        	
            switch (v.getId()) 
            {
            case R.id.channel_manage_btn_4_1:
            	inputChannelAdd();
            	break;
            case R.id.channel_manage_btn_4_2:
            	inputChannelDel();
            	break;
            case R.id.channel_manage_btn_4_3:
            	outputChannelAdd();
            	break;
            case R.id.channel_manage_btn_4_4:
            	outputChannelDel();
            	break;
            case R.id.channel_manage_btn_7_1:
            	saveChannelInfo();
            	break;
            }
              
        }
      
    }
	private void inputChannelAdd()
	{
		if(curVisibleChannelInput>=10)
		{
			Toast.makeText(ChannelManage.this, "添加输入设备大于10个", Toast.LENGTH_SHORT).show();
			return;
		}
		channelInput[curVisibleChannelInput].setVisibility(View.VISIBLE);
		curVisibleChannelInput++;
	}
	private void inputChannelDel()
	{
		curVisibleChannelInput--;
		if(curVisibleChannelInput<0)
		{
			Toast.makeText(ChannelManage.this, "无设备可删除", Toast.LENGTH_SHORT).show();
			return;
		}
		channelInput[curVisibleChannelInput].setVisibility(View.INVISIBLE);
		
	}
	private void outputChannelAdd()
	{
		if(curVisibleChannelOutput>=10)
		{
			Toast.makeText(ChannelManage.this, "添加输出设备大于10个", Toast.LENGTH_SHORT).show();
			return;
		}
		channelOutput[curVisibleChannelOutput].setVisibility(View.VISIBLE);
		curVisibleChannelOutput++;
		
	}
	private void outputChannelDel()
	{
		curVisibleChannelOutput--;
		if(curVisibleChannelOutput<0)
		{
			Toast.makeText(ChannelManage.this, "无设备可删除", Toast.LENGTH_SHORT).show();
			return;
		}
		channelOutput[curVisibleChannelOutput].setVisibility(View.INVISIBLE);
	}
	private void saveChannelInfo()
	{
	    String strDevId = etDevId.getText().toString();
	    if(strDevId.isEmpty())
	    {
	    	Toast.makeText(ChannelManage.this, "设备输入编号为空", Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    if(!isVaildNum(strDevId))
	    {
	    	Toast.makeText(ChannelManage.this, "设备输入编号不为数字"+strDevId, Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    HashMap<String,Object> map = new HashMap<String,Object>();
	    map.put("DevId", strDevId);
	    map.put("InputChannel", String.valueOf(curVisibleChannelInput));
	    map.put("OutputChannel", String.valueOf(curVisibleChannelOutput));
	    ApplicationVar.getInstance().setChannelInfo(map);

	    
	}
	private Boolean isVaildNum(String value)//是否是合法数字
	{
		if(value == null)
		{
			return false;
		}
		Pattern p = Pattern.compile("[0-9]*"); 
	     Matcher m = p.matcher(value); 
	     if(m.matches())
	     {
	        return true;
	     }
	     else
	     {
		   return false;
	     }
	}
    
}
