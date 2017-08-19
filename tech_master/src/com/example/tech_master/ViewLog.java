package com.example.tech_master;


import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.device.Device;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ViewLog extends Activity implements AdapterView.OnItemSelectedListener  {
	
	
	 private Spinner spSelect;  
	 private EditText etDevId;
	 private TextView tvDevState;
     private ArrayAdapter<String> adapter;
     private static final String[] opDev = {"��ѡ��:","�豸����","�豸�ػ�","��ʱ����","��ʱȡ��","��������","�������","���ñ��"};
	 protected void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.activity_view_log);
	        spSelect = (Spinner) findViewById(R.id.view_log_sp_cmd);
	        etDevId = (EditText) findViewById(R.id.view_log_et_devid);
	        tvDevState = (TextView) findViewById(R.id.view_log_tv_state);
	        
	        
	        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,opDev);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spSelect.setAdapter(adapter);
	        spSelect.setOnItemSelectedListener(this);
	        

	        ApplicationVar.getInstance().setViewLogHandler(myHandler);
	    }

	 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
	 {  
		 String strDevId = etDevId.getText().toString();
		 int op = 0;
		 if (0 == strDevId.length())
		 {
			 return;
		 }
		 if(!isVaildNum(strDevId))
		 {
			 Toast.makeText(ViewLog.this, "�����豸�ŷǷ�", Toast.LENGTH_SHORT).show(); 
			 return;
		 }
		 int  intDevId = Integer.valueOf(strDevId);
	     if(1==position) //���豸
	     {
	    	 Toast.makeText(ViewLog.this, "�豸"+String.valueOf(intDevId)+"����", Toast.LENGTH_SHORT).show();  
	     }
	     else if(2 == position)
	     {
	    	 Toast.makeText(ViewLog.this, "�豸"+String.valueOf(intDevId)+"�ػ�", Toast.LENGTH_SHORT).show();  
	     }
	     else if(3 == position)
	     {
	    	 Toast.makeText(ViewLog.this, "�豸"+String.valueOf(intDevId)+"��ʱ����", Toast.LENGTH_SHORT).show();  
	     }
	     else if(4 == position)
	     {
	    	 Toast.makeText(ViewLog.this, "�豸"+String.valueOf(intDevId)+"��ʱȡ��", Toast.LENGTH_SHORT).show(); 
	     }
	     else if(5 == position)
	     {
	    	 Toast.makeText(ViewLog.this, "�豸"+String.valueOf(intDevId)+"��������", Toast.LENGTH_SHORT).show();  
	     }
	     else if(6 == position)
	     {
	    	 Toast.makeText(ViewLog.this, "�豸"+String.valueOf(intDevId)+"�������", Toast.LENGTH_SHORT).show();  
	     }
	     else if(7 == position)
	     {
	    	 Toast.makeText(ViewLog.this, "�豸"+String.valueOf(intDevId)+"���ñ��", Toast.LENGTH_SHORT).show();
	     }
	     op = position;
	     sendMsgDev(intDevId,op);
	 }   
	  private void sendMsgDev(int id ,int op)
	  {
		  String str = "";
		  String msg_id = new DecimalFormat("0000").format(id);
		  if(1 == op)//&SS#1000#000001#0001#01$
		  {
			  str = "&SS#1000#000001#0000#01$";  
		  }
		  else if(2 == op)
		  {
			  str = "&SS#1000#000001#0000#02$";
		  }
		  else if(3 == op)
		  {
			  str = "&SS#1003#000001#0000#01#12#23#00#12#23#00$";	
		  }
		  else if(4 == op)
		  {
			  str = "&SS#1003#000001#0000#02#12#23#00#12#23#00$";
		  }
		  else if(5 == op)
		  {
			  str = "&SS#1001#000001#0000#01#0000#0000#0000$";
		  }
		  else if(6 == op)
		  {
			  str = "&SS#1001#000001#0000#02#0000#0000#0000$";
		  }
		  else if(7 == op)
		  {
			  str = "&SS#1006#000001#0000#00$";
		  }
		  if(str.length()!=0)
		  {
		
			  String str1 = (String) str.subSequence(0, 16);
			  String str2 = (String) str.subSequence(20,str.length());
			  
			  str=str1+msg_id+str2;
		      if(op!=7)
		      {
		          ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
		      }
		      else
		      {
		    	  ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
		      }
		  }
	  }
	  private final Handler myHandler= new Handler()
	   	{
	   	        public  void  handleMessage(Message message)
	   	        {
	   	            if(message.what==1)
	   	            {
	   	            	String strMsg = message.obj.toString();
	   	            	processMsg(strMsg);
	   	            }    
	   	        }
	   	}; 
	   	
	   
	   	private void processMsg(String msg)
	   	{
	   		String []msgSplit = msg.split("#");
	   		if(msgSplit.length<3)
	   		{
	   			return;
	   		}
	   		//&CS#8001#000001#0001#00#00#00$ �豸ע��
	   		if(msgSplit[0].equals("CS")&&msgSplit[1].equals("8001")&&msgSplit[2].equals("000001"))//�豸ע��
			{
	   		    if(!isVaildNum(msgSplit[3]))
	   		    {
	   		    	Toast.makeText(ViewLog.this, "�豸��ŷǷ�", Toast.LENGTH_SHORT).show(); 
	   		    	return;
	   		    }
	   			int devId = Integer.valueOf(msgSplit[3]);
	   			etDevId.setText(String.valueOf(devId));
	   		    tvDevState.setText("�豸״̬������");		
			}
	   		else if(msgSplit[0].equals("CS")&&msgSplit[1].equals("8002")&&msgSplit[2].equals("000001"))//&CS#8002#000001#0001#04$ ����
	   		{
	   			if(!isVaildNum(msgSplit[3]))
	   		    {
	   		    	Toast.makeText(ViewLog.this, "�豸��ŷǷ�", Toast.LENGTH_SHORT).show(); 
	   		    	return;
	   		    }
	   			int devId = Integer.valueOf(msgSplit[3]);
	   			etDevId.setText(String.valueOf(devId));
	   		    tvDevState.setText("�豸״̬������");
	   		}
	   		else if(msgSplit[0].equals("CS")&&msgSplit[1].equals("8003")&&msgSplit[2].equals("000001"))//&CS#8003#000001#0001#04$ ����
	   		{
	   			if(!isVaildNum(msgSplit[3]))
	   		    {
	   		    	Toast.makeText(ViewLog.this, "�豸��ŷǷ�", Toast.LENGTH_SHORT).show(); 
	   		    	return;
	   		    }
	   			int devId = Integer.valueOf(msgSplit[3]);
	   			etDevId.setText(String.valueOf(devId));
	   		    tvDevState.setText("�豸״̬��������");
	   		}
	   		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1000")&&msgSplit[2].equals("000000"))// &CR#1000#000000#0001#01$ ��ֹ����������
	   		{
	   			String str="";
	   			if(!isVaildNum(msgSplit[3]))
	   		    {
	   		    	Toast.makeText(ViewLog.this, "�豸��ŷǷ�", Toast.LENGTH_SHORT).show(); 
	   		    	return;
	   		    }
	   			int devId = Integer.valueOf(msgSplit[3]);
	   			etDevId.setText(String.valueOf(devId));
	   			if(msgSplit[4].equals("01"))
	   			{
	   				str = "�豸״̬�����عػ�����";
	   		        
	   			}
	   			else if(msgSplit[4].equals("02"))
	   			{
	   				str = "�豸״̬�����ؿ�������";
	   			}
	   			tvDevState.setText(str);
	   		}
	   		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1002")&&msgSplit[2].equals("000000"))// &CR#1002#000000#0001#01$ ��ֹ����������
	   		{
	   			String str="";
	   			if(!isVaildNum(msgSplit[3]))
	   		    {
	   		    	Toast.makeText(ViewLog.this, "�豸��ŷǷ�", Toast.LENGTH_SHORT).show(); 
	   		    	return;
	   		    }
	   			int devId = Integer.valueOf(msgSplit[3]);
	   			etDevId.setText(String.valueOf(devId));
	   			if(msgSplit[4].equals("01"))
	   			{
	   				str = "�豸״̬������(�ػ�)";
	   		        
	   			}
	   			else if(msgSplit[4].equals("02"))
	   			{
	   				str = "�豸״̬������(����)";
	   			}
	   			tvDevState.setText(str);
	   		}
	   		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1003")&&msgSplit[2].equals("000000"))// &CR#1003#000000#0001#01#12#23#00#12#23#00$  ���ػ�����
	   		{
	   			String str="";
	   			if(!isVaildNum(msgSplit[3]))
	   		    {
	   		    	Toast.makeText(ViewLog.this, "�豸��ŷǷ�", Toast.LENGTH_SHORT).show(); 
	   		    	return;
	   		    }
	   			int devId = Integer.valueOf(msgSplit[3]);
	   			etDevId.setText(String.valueOf(devId));
	   			if(msgSplit[4].equals("01"))
	   			{
	   				str = "�豸״̬������ʱ�䣺"+msgSplit[5]+":"+msgSplit[6]+":"+msgSplit[7]+"�ػ�ʱ�䣺"+msgSplit[8]+":"+msgSplit[9]+":"+msgSplit[10];
	   			}
	   			else if(msgSplit[4].equals("02"))
	   			{
	   				str = "�豸״̬��ȡ�����ػ�����";
	   			}
	   			tvDevState.setText(str);
	   		}
	   		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1001")&&msgSplit[2].equals("000000"))// &CR#1001#000000#0001#01#12#23#00#12#23#00$  ���ػ�����
	   		{
	   			String str="";
	   			if(!isVaildNum(msgSplit[3]))
	   		    {
	   		    	Toast.makeText(ViewLog.this, "�豸��ŷǷ�", Toast.LENGTH_SHORT).show(); 
	   		    	return;
	   		    }
	   			int devId = Integer.valueOf(msgSplit[3]);
	   			etDevId.setText(String.valueOf(devId));
	   			if(msgSplit[4].equals("01"))
	   			{
	   				str = "�豸״̬������������ȡ��"+msgSplit[5]+":"+msgSplit[6]+":"+msgSplit[7];
	   			}
	   			else if(msgSplit[4].equals("02"))
	   			{
	   				str = "�豸״̬�������������";
	   			}
	   			tvDevState.setText(str);
	   		}
	   		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1006")&&msgSplit[2].equals("000000"))// &CR#1001#000000#0001#01#12#23#00#12#23#00$  ���ػ�����
	   		{
	   			String str="";
	   			if(!isVaildNum(msgSplit[3]))
	   		    {
	   		    	Toast.makeText(ViewLog.this, "�豸��ŷǷ�", Toast.LENGTH_SHORT).show(); 
	   		    	return;
	   		    }
	   			int devId = Integer.valueOf(msgSplit[3]);
	   			etDevId.setText(String.valueOf(devId));
	   			if(msgSplit[4].equals("00"))
	   			{
	   				str = "�豸״̬���豸��ųɹ�";
	   			}
	   			tvDevState.setText(str);
	   		}
	   		
	   	}
		   	
		   	private Boolean isVaildNum(String value)//�Ƿ��ǺϷ�����
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

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
}
