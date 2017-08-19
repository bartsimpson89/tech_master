package com.example.tech_master;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.dialog.LoginDialog;
import com.example.tech_master.PowerManage.MyClickListener;

import android.R.integer;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;



public class DeviceManage extends Activity 
{
	private Button btnAddDev;
	private Button btnSaveDev;
	private Button btnModifyDev;
    private Button btnDelDev;
    private Button btnModifyPassWd;
    private Button btnModifyNo;
	private ArrayList<HashMap<String, Object>> gDeviceItem  = new ArrayList<HashMap<String, Object>>();
	private boolean bModifyDevId = false;
	private String     strNewDevId;
	private String     strOldDevId;
	
	protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_manage);
        
        btnAddDev = (Button)findViewById(R.id.devmanage_btn_add);
        btnSaveDev = (Button)findViewById(R.id.devmanage_btn_save);
        btnModifyDev = (Button)findViewById(R.id.devmanage_btn_modify_dev);
        btnDelDev = (Button)findViewById(R.id.devmanage_btn_del_dev);
        btnModifyPassWd = (Button)findViewById(R.id.devmanage_btn_modify_passwd);
        btnModifyNo = (Button)findViewById(R.id.devmanage_btn_modify_no);
        
        btnAddDev.setOnClickListener(new MyClickListener());
        btnSaveDev.setOnClickListener(new MyClickListener());
        btnModifyDev.setOnClickListener(new MyClickListener());
        btnDelDev.setOnClickListener(new MyClickListener());
        btnModifyPassWd.setOnClickListener(new MyClickListener());
        btnModifyNo.setOnClickListener(new MyClickListener());
        ApplicationVar.getInstance().getArrayDevice(gDeviceItem);
        //ApplicationVar.getInstance().setDeviceManageHandler(myHandler);
        
        
        
    }
	public class MyClickListener implements OnClickListener 
    {
     
        int type;
        public void onClick(View v) 
        {
        	
            switch (v.getId()) 
            {
            case R.id.devmanage_btn_add:
                showDialogAddDev();
                break;
            case R.id.devmanage_btn_save:
            	showDialogSave();
            	break;
            case R.id.devmanage_btn_modify_dev:
            	showDialogModifyDev();
            	break;
            case R.id.devmanage_btn_del_dev:
            	showDialogDeleteDev();
            	break;
            case R.id.devmanage_btn_modify_passwd:
            	showDialogDModifyPassWd();
            	break;
            case R.id.devmanage_btn_modify_no:
            	showDialogModifyNo();
            	break;
        }
      }
    }
	private void showDialogModifyNo()
	{
		final LoginDialog.Builder builder = new LoginDialog.Builder(this);
		builder.setTitle("修改编号","旧设备编号","请输入设备编号","新设备编号","请输入新设备编号",0);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which)
			{
				//设置你的操作事项
				String devId = builder.getEditName();
				String nDevId = builder.getEditPassWd();
				if(nDevId.isEmpty()||devId.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if((!isVaildNum(devId))||(!isVaildNum(nDevId)))//是否是合法数字
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				//修改设备编号，对老的设备编号
				if(getDevNoIndex(devId)>=0&&getDevNoIndex(nDevId)<0)//存在
				{
					gDeviceItem.get(getDevNoIndex(devId)).put("dev_id", String.valueOf(nDevId));
					bModifyDevId = true;
					strNewDevId = nDevId;
					strOldDevId  = devId;
					
				}
				else
				{
					if(getDevNoIndex(devId)<0)
					{
					    Toast.makeText(DeviceManage.this,"旧设备号不存在" , Toast.LENGTH_SHORT).show();
					}
					if(getDevNoIndex(nDevId)>=0)
					{
						Toast.makeText(DeviceManage.this,"新设备号已存在" , Toast.LENGTH_SHORT).show();
					}
				}
				
				dialog.dismiss();
			}
		
	  });
		builder.create().show();
		
	}
	
	private void showDialogAddDev()
	{
		final LoginDialog.Builder builder = new LoginDialog.Builder(this);
		builder.setTitle("添加设备","设备编号","请输入数字编号","","",1);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//设置你的操作事项
				String devNo = builder.getEditName();
				if(devNo.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字编号" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if(!isVaildNum(devNo))//是否是合法数字
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字编号" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				else
				{
					int no = Integer.valueOf(devNo);
					if(no<=0&&no>=100)
					{
						Toast.makeText(DeviceManage.this,"请输入有效数字编号" , Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
				}
				if(getDevNoIndex(devNo)>=0)//是否已经存在该编号
				{
					Toast.makeText(DeviceManage.this,"已存在该设备编号" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				HashMap<String, Object> map = new HashMap<String,Object>();
				map.put("dev_name", "设备");
				map.put("dev_id", devNo);
				map.put("dev_channel", "0");
				gDeviceItem.add(map);
				Toast.makeText(DeviceManage.this,"已经添加设备NO."+devNo , Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				
			}
		});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}
	private void showDialogSave()
	{
		final LoginDialog.Builder builder = new LoginDialog.Builder(this);
		builder.setTitle("保存信息","","","","",3);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//设置你的操作事项
				ascDevByNo();
				ApplicationVar.getInstance().setArrayDevice(gDeviceItem);
				if(bModifyDevId)
				{
					sendMsgModifyId(Integer.valueOf(strOldDevId),Integer.valueOf(strNewDevId));
					bModifyDevId = false;
				}
				
				ApplicationVar.getInstance().initDevStateList();
				Toast.makeText(DeviceManage.this,"保存信息成功", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				
			}
		});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
	private void showDialogDModifyPassWd()
	{
		final LoginDialog.Builder builder = new LoginDialog.Builder(this);
		builder.setTitle("修改密码","设备编号","请输入设备编号","设备密码","请输入新密码",0);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which)
			{
				//设置你的操作事项
				String devId = builder.getEditName();
				String devPassWd = builder.getEditPassWd();
				if(devPassWd.isEmpty()||devId.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if((!isVaildNum(devId))||(!isVaildNum(devPassWd)))//是否是合法数字
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if(devPassWd.length()!=4)
				{
					Toast.makeText(DeviceManage.this,"请输入4位有效数字密码" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				int id = Integer.valueOf(devId);
				sendMsgPassWd(id,devPassWd);
				
				
				
				dialog.dismiss();
			}
		
	  });
		builder.create().show();
	}
	
	
	
	
    private void showDialogModifyDev()
    {
    	final LoginDialog.Builder builder = new LoginDialog.Builder(this);
		builder.setTitle("修改设备","设备编号","请输入设备编号","设备名字","请输入设备新名字",0);//第二个显示框显示具体数值
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//设置你的操作事项
                String devNo = builder.getEditName();
                String devName = builder.getEditPassWd();
                if(devNo.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字编号" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if(null == devNo||(!isVaildNum(devNo)))//是否是合法数字
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字编号" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				else
				{
					int no = Integer.valueOf(devNo);
					if(no<=0&&no>=100)
					{
						Toast.makeText(DeviceManage.this,"请输入有效数字编号" , Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
				}
				int index=getDevNoIndex(devNo);

				if(index<0)//是否已经存在该编号
				{
					Toast.makeText(DeviceManage.this,"不存在该设备编号" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				gDeviceItem.get(index).put("dev_name", devName);
				
				dialog.dismiss();
				
				
			}
		});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
    }
    private void showDialogDeleteDev()
    {
    	final LoginDialog.Builder builder = new LoginDialog.Builder(this);
		builder.setTitle("删除设备","设备编号","请输入数字编号","","",1);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//设置你的操作事项
                String devNo = builder.getEditName();
                if(devNo.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字编号" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if(null == devNo||(!isVaildNum(devNo)))//是否是合法数字
				{
					Toast.makeText(DeviceManage.this,"请输入有效数字编号" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				else
				{
					int no = Integer.valueOf(devNo);
					if(no<=0&&no>=100)
					{
						Toast.makeText(DeviceManage.this,"请输入有效数字编号" , Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
				}
				int index=getDevNoIndex(devNo);

				if(index<0)//是否已经存在该编号
				{
					Toast.makeText(DeviceManage.this,"不存在该设备编号" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				gDeviceItem.remove(index);
				dialog.dismiss();
				
				
			}
		});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
    	
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
	private int getDevNoIndex(String devNo)//获取该编号的index
	{
		int i =0;
		for(i=0;i<gDeviceItem.size();i++)
		{
			String gDevNo = (String)gDeviceItem.get(i).get("dev_id");
			if(gDevNo.equals(devNo))
			{
				break;
			}
		}
		if(i == gDeviceItem.size())
		{
			return -1;
		}
		else
		{
			return i;
		}
	}
	private void ascDevByNo() //设备升序排列
	{
		Comparator<HashMap<String,Object>> comparator = new Comparator<HashMap<String,Object>>()
		{  
			   public int compare(HashMap<String,Object> s1, HashMap<String,Object> s2)
			   {  
				    int id_1 = Integer.valueOf((String)s1.get("dev_id"));
				    int id_2 = Integer.valueOf((String)s2.get("dev_id"));
				    if(id_1!=id_2)
				    {  
				        return id_1-id_2;  
				    }
				    else
				    {
					    return 0;  
				    }
			   }  
		};  
		 Collections.sort(gDeviceItem,comparator); 
	
	}
////////////*****下发信息*********************/
private void sendMsgPassWd(int id,String devPassWd)
{
	
		  String str = "";
		  String msg_id = new DecimalFormat("0000").format(id);
		  str = "&SS#1007#000001#0000#0000$";  
		  String str1 = (String) str.subSequence(0, 16);
		  str=str1+msg_id+"#"+devPassWd+"$";
		  
	      ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
	 
}
private void sendMsgModifyId(int id,int newId)
{
	  String str = "";
	  String msg_id = new DecimalFormat("0000").format(id);
	  String new_id = new DecimalFormat("0000").format(newId);
	  str = "&SS#1009#000001#0000#0000$";  
	  String str1 = (String) str.subSequence(0, 16);
	  str=str1+msg_id+"#"+new_id+"$";
	  
      ApplicationVar.getInstance().ActivitySendMsgToApplication(msg_id, str);
	
}

	
/////////////*****接受信息****************************/
	 
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
		//&CR#1007#000000#0001#0000$ 设备修改密码
		if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1007")&&msgSplit[2].equals("000000"))//修改设备密码
	    {
		    if(!isVaildNum(msgSplit[3]))
		    {
		    	return;
		    }
		    int devId = Integer.valueOf(msgSplit[3]);
		    
		    Toast.makeText(DeviceManage.this, "修改设备"+String.valueOf(devId)+"密码成功", Toast.LENGTH_SHORT).show();
		    
	    }
		//  &CR#1009#000000#0001#0000$ 
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1009")&&msgSplit[2].equals("000000"))//
		{
			 if(!isVaildNum(msgSplit[3]))
			    {
			    	return;
			    }
			    
			    Toast.makeText(DeviceManage.this, "修改设备旧设备Id成功", Toast.LENGTH_SHORT).show();
			
		}
	}
	
	
	
	
}
