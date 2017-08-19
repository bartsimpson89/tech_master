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
		builder.setTitle("�޸ı��","���豸���","�������豸���","���豸���","���������豸���",0);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which)
			{
				//������Ĳ�������
				String devId = builder.getEditName();
				String nDevId = builder.getEditPassWd();
				if(nDevId.isEmpty()||devId.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"��������Ч����" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if((!isVaildNum(devId))||(!isVaildNum(nDevId)))//�Ƿ��ǺϷ�����
				{
					Toast.makeText(DeviceManage.this,"��������Ч����" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				//�޸��豸��ţ����ϵ��豸���
				if(getDevNoIndex(devId)>=0&&getDevNoIndex(nDevId)<0)//����
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
					    Toast.makeText(DeviceManage.this,"���豸�Ų�����" , Toast.LENGTH_SHORT).show();
					}
					if(getDevNoIndex(nDevId)>=0)
					{
						Toast.makeText(DeviceManage.this,"���豸���Ѵ���" , Toast.LENGTH_SHORT).show();
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
		builder.setTitle("����豸","�豸���","���������ֱ��","","",1);

		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//������Ĳ�������
				String devNo = builder.getEditName();
				if(devNo.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"��������Ч���ֱ��" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if(!isVaildNum(devNo))//�Ƿ��ǺϷ�����
				{
					Toast.makeText(DeviceManage.this,"��������Ч���ֱ��" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				else
				{
					int no = Integer.valueOf(devNo);
					if(no<=0&&no>=100)
					{
						Toast.makeText(DeviceManage.this,"��������Ч���ֱ��" , Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
				}
				if(getDevNoIndex(devNo)>=0)//�Ƿ��Ѿ����ڸñ��
				{
					Toast.makeText(DeviceManage.this,"�Ѵ��ڸ��豸���" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				HashMap<String, Object> map = new HashMap<String,Object>();
				map.put("dev_name", "�豸");
				map.put("dev_id", devNo);
				map.put("dev_channel", "0");
				gDeviceItem.add(map);
				Toast.makeText(DeviceManage.this,"�Ѿ�����豸NO."+devNo , Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				
			}
		});
		builder.setNegativeButton("ȡ��",
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
		builder.setTitle("������Ϣ","","","","",3);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//������Ĳ�������
				ascDevByNo();
				ApplicationVar.getInstance().setArrayDevice(gDeviceItem);
				if(bModifyDevId)
				{
					sendMsgModifyId(Integer.valueOf(strOldDevId),Integer.valueOf(strNewDevId));
					bModifyDevId = false;
				}
				
				ApplicationVar.getInstance().initDevStateList();
				Toast.makeText(DeviceManage.this,"������Ϣ�ɹ�", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				
			}
		});
		builder.setNegativeButton("ȡ��",
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
		builder.setTitle("�޸�����","�豸���","�������豸���","�豸����","������������",0);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which)
			{
				//������Ĳ�������
				String devId = builder.getEditName();
				String devPassWd = builder.getEditPassWd();
				if(devPassWd.isEmpty()||devId.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"��������Ч����" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if((!isVaildNum(devId))||(!isVaildNum(devPassWd)))//�Ƿ��ǺϷ�����
				{
					Toast.makeText(DeviceManage.this,"��������Ч����" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if(devPassWd.length()!=4)
				{
					Toast.makeText(DeviceManage.this,"������4λ��Ч��������" , Toast.LENGTH_SHORT).show();
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
		builder.setTitle("�޸��豸","�豸���","�������豸���","�豸����","�������豸������",0);//�ڶ�����ʾ����ʾ������ֵ
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//������Ĳ�������
                String devNo = builder.getEditName();
                String devName = builder.getEditPassWd();
                if(devNo.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"��������Ч���ֱ��" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if(null == devNo||(!isVaildNum(devNo)))//�Ƿ��ǺϷ�����
				{
					Toast.makeText(DeviceManage.this,"��������Ч���ֱ��" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				else
				{
					int no = Integer.valueOf(devNo);
					if(no<=0&&no>=100)
					{
						Toast.makeText(DeviceManage.this,"��������Ч���ֱ��" , Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
				}
				int index=getDevNoIndex(devNo);

				if(index<0)//�Ƿ��Ѿ����ڸñ��
				{
					Toast.makeText(DeviceManage.this,"�����ڸ��豸���" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				gDeviceItem.get(index).put("dev_name", devName);
				
				dialog.dismiss();
				
				
			}
		});
		builder.setNegativeButton("ȡ��",
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
		builder.setTitle("ɾ���豸","�豸���","���������ֱ��","","",1);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//������Ĳ�������
                String devNo = builder.getEditName();
                if(devNo.isEmpty())
				{
					Toast.makeText(DeviceManage.this,"��������Ч���ֱ��" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				if(null == devNo||(!isVaildNum(devNo)))//�Ƿ��ǺϷ�����
				{
					Toast.makeText(DeviceManage.this,"��������Ч���ֱ��" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				else
				{
					int no = Integer.valueOf(devNo);
					if(no<=0&&no>=100)
					{
						Toast.makeText(DeviceManage.this,"��������Ч���ֱ��" , Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
				}
				int index=getDevNoIndex(devNo);

				if(index<0)//�Ƿ��Ѿ����ڸñ��
				{
					Toast.makeText(DeviceManage.this,"�����ڸ��豸���" , Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				gDeviceItem.remove(index);
				dialog.dismiss();
				
				
			}
		});
		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
    	
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
	private int getDevNoIndex(String devNo)//��ȡ�ñ�ŵ�index
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
	private void ascDevByNo() //�豸��������
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
////////////*****�·���Ϣ*********************/
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

	
/////////////*****������Ϣ****************************/
	 
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
		//&CR#1007#000000#0001#0000$ �豸�޸�����
		if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1007")&&msgSplit[2].equals("000000"))//�޸��豸����
	    {
		    if(!isVaildNum(msgSplit[3]))
		    {
		    	return;
		    }
		    int devId = Integer.valueOf(msgSplit[3]);
		    
		    Toast.makeText(DeviceManage.this, "�޸��豸"+String.valueOf(devId)+"����ɹ�", Toast.LENGTH_SHORT).show();
		    
	    }
		//  &CR#1009#000000#0001#0000$ 
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1009")&&msgSplit[2].equals("000000"))//
		{
			 if(!isVaildNum(msgSplit[3]))
			    {
			    	return;
			    }
			    
			    Toast.makeText(DeviceManage.this, "�޸��豸���豸Id�ɹ�", Toast.LENGTH_SHORT).show();
			
		}
	}
	
	
	
	
}
