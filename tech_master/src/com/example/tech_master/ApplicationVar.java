package com.example.tech_master;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import com.example.serve.CallBack;
import com.example.serve.MyTcpServerThread;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ApplicationVar extends Application {

	private ArrayList<HashMap<String, Object>> gDeviceItem  = new ArrayList<HashMap<String, Object>>();
	private HashMap<String,Object> gChannelInfo   = new HashMap<String,Object>();
	private ArrayList<HashMap<String,Object>> gUsrInfo   = new  ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String,String>> gListDeviceState = new  ArrayList<HashMap<String,String>>();
	
	private static ApplicationVar gInstance;
	private static FileOutputStream fosLog;
	private static  FileInputStream fisLog;
	private CallBackDevAlarm mCallBackAlarm;
	
	public enum LoginState{LoginOk,LoginNull,LoginErrRouter,LoginErrIp};

	//////////////////////////////////////////////
	Queue<String> gQueRecvMsg = new LinkedList<String>();
	Queue<String> gQueSendMsg = new LinkedList<String>();
	
	public void initDevStateList()
	{
		getArrayDevice(gDeviceItem);
		 gListDeviceState.clear();
		 for(int i = 0; i < gDeviceItem.size(); i++)
		 {
			 String strDevId = (String)gDeviceItem.get(i).get("dev_id");
			 String strDevName = (String)gDeviceItem.get(i).get("dev_name");
			 
			 HashMap<String, String> map = new HashMap<String,String>();
			 map.put("dev_id",strDevId);
			 map.put("dev_name", strDevName);
			 map.put("dev_state", "offline");
			 map.put("dev_set_timer", timeSecond());
			 map.put("dev_open_timer", timeSecond());
			 map.put("dev_help", "FALSE");
			 gListDeviceState.add(map);
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
	public ArrayList<HashMap<String,String>> getDevStateList()
	{	
		return gListDeviceState;
	}
	private void updateDevStateList(String strDevId,String strItem,String strValue)
	{
		int idx = 0;
		int iDevId = Integer.valueOf(strDevId).intValue();
		if ((idx = findIdxByDevId(iDevId))<0 )
		{
			return;
		}
		gListDeviceState.get(idx).put(strItem, strValue);
		
	}
	
	
	
	
	
	
	private final Handler myHandler= new Handler()
	{
	        public  void  handleMessage(Message message)
	        {
	            if(message.what==1)
	            {
	            	if(gQueRecvMsg.size()>1000)
	            	{
	            		writeLog(new Exception(),"RecvMsg size is full");
	            		return;
	            	}
	            	gQueRecvMsg.offer(message.obj.toString());
	            }    
	        }
	};  
	private MyTcpServerThread tcpThread = new MyTcpServerThread(myHandler);

//将发送消息压入到队列中
	/*
	 *strIdThread thread_id和msg组合压入到gQueSendMsg队列
	 * */
	private void sendMsgClient(String strIdThread,String msg)
	{
		if(gQueSendMsg.size()>1000)
    	{
			writeLog(new Exception(),"SendMsg size is full");
			return;
    	}
		gQueSendMsg.offer(strIdThread+":"+msg); 
	}
	//每个一段时间（2s）给对应的设备发送心跳
		/*
		 *strIdThread thread_id和msg组合压入到gQueSendMsg队列
		 * */
	
	////////////////////////////////
	private final Timer timer = new Timer();  
	private TimerTask task = new TimerTask() {  
	        @Override  
	        public void run() {  
	        	 try {
	                 sendMsgHeart();
	                }
	            	
	             catch (Exception e) 
	            {
	            }
	        }  
	    }; 
		
	    //消息接收转发过程
		/*
		 *消息接收（从设备）队列gQueRecvMsg
		 *消息发送（给设备）队列gQueSendMsg
		 * */
	
	////////////////////////////////
	    
	    
    private final Timer timer_msg = new Timer();  
	private TimerTask task_msg= new TimerTask() {  
	        @Override  
	        public void run() {  
	        	 try {
	                 if(!gQueRecvMsg.isEmpty())
	                 {
	                	 String msg = gQueRecvMsg.poll();
	                	 processRecvMsg(msg);
	                 }
	                 if(!gQueSendMsg.isEmpty())
	                 {
	                	 String msg = gQueSendMsg.poll();
	                	 String str[] = msg.split(":");
	                	 tcpThread.tcpSendMsg(str[0], str[1]); //str[0] threadid str[1] msg
	                 }
	                }
	            	
	             catch (Exception e) 
	            {
	            }
	        }  
	    }; 
	    //发送心跳接口
	  		/*
	  		 *
	  		 *
	  		 * */
	  	
	  	////////////////////////////////
    private void sendMsgHeart()
    {
    	  String msgHead = "SS";
    	  String msgCmd ="1002";
    	  String msgDir = "000001";
    	  String msgId;
    	  String msgState = "00";
    	  
    	 for(int i=0;i<gDeviceItem.size();i++)
    	 {
    		 String devId = (String)gDeviceItem.get(i).get("dev_id");
    	     int id = Integer.valueOf(devId).intValue();
    		 msgId =  new DecimalFormat("0000").format(id);
    		 String heartMsgString = "&"+msgHead+"#"+msgCmd+"#"+msgDir+"#"+msgId+"#"+msgState+"#00#00#00"+"$";
    		 ActivitySendMsgToApplication(msgId,heartMsgString);
    		 
    	 }
      }
	private Handler ioControlHandler  = null;
	private Handler alarmStateHandler = null;
	private Handler viewLogHandler = null;

	public void setAlarmStateHandler(Handler handle)
	{
		alarmStateHandler = handle;
	}
	public void setIOControlHandler(Handler handle)
	{
		ioControlHandler = handle;
	}
	public void setViewLogHandler(Handler handle)
	{
		viewLogHandler = handle;
	}
	public void ActivitySendMsgToApplication(String devId,String msg)
	{
		 String strThreadId = tcpThread.findServerThreadIdByDevId(devId);
		 if(strThreadId == null)
		 {
			 return;
		 }
		 
		 sendMsgClient(strThreadId, msg);
	}
	////////////////////////////
	
	private String getMsgThreadId(String msg)
	{
		int index_start = msg.indexOf("(");
		int index_end = msg.indexOf(")");
		if(-1 == index_start||-1 == index_end)
		{
			return null;
		}
		String result = msg.substring(index_start+1, index_end);
		return result;
		
	}
	private String getMsgBody(String msg)
	{
		int index_start = msg.indexOf("&");
		int index_end = msg.indexOf("$");
		if(-1 == index_start||-1 == index_end)
		{
			return null;
		}
		
		String result = msg.substring(index_start+1, index_end);
		return result;
	}
	
	///////////////////////
	private void processRecvMsg(String str)
	{
		String msg;
		msg = getMsgBody(str);
		
		if(null == msg)
		{
			return;
		}
		String []msgSplit = msg.split("#");
		if(msgSplit.length<3)
		{
			return;
		}
		
		if(null!=viewLogHandler)
		{				
		    viewLogHandler.obtainMessage(1,-1,-1,msg).sendToTarget();
		}
		
		if(msgSplit[0].equals("CS")&&msgSplit[1].equals("8001")&&msgSplit[2].equals("000001"))//注册编号
		{
			 
			Calendar.getInstance().setTimeInMillis(System.currentTimeMillis());
	        long lHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	        long lMinute = Calendar.getInstance().get(Calendar.MINUTE);
	        long lSecond = Calendar.getInstance().get(Calendar.SECOND);
			msgSplit[0] = "SR";
			msgSplit[2] = "000000";
			msgSplit[4] = new DecimalFormat("00").format(lHour);
			msgSplit[5] = new DecimalFormat("00").format(lMinute);
			msgSplit[6] = new DecimalFormat("00").format(lSecond);
			String recvMsg = "&"+msgSplit[0]+"#"+msgSplit[1]+"#"+msgSplit[2]+"#"+msgSplit[3]+"#"+msgSplit[4]+
					         "#"+msgSplit[5]+"#"+msgSplit[6]+"$";
			
			String strThreadId = getMsgThreadId(str);
			sendMsgClient(strThreadId, recvMsg);
			String strDevId = msgSplit[3];
			tcpThread.setRegisterTimeAndId(strThreadId, ApplicationVar.timeSecond(), strDevId);
			//mCallBack.UpdateDevState(msgSplit[3],"online"); //04表示在线
			updateDevStateList(strDevId,"dev_state","online");
			
			
		}
		//&CS#8002#000001#0001#04$ 举手
		else if(msgSplit[0].equals("CS")&&msgSplit[1].equals("8002")&&msgSplit[2].equals("000001"))//学生举手
		{
			
			msgSplit[0] = "SR";
			msgSplit[2] = "000000";
			
			String recvMsg = "&"+msgSplit[0]+"#"+msgSplit[1]+"#"+msgSplit[2]+"#"+msgSplit[3]+"#"
			                  +msgSplit[4]+ "$";
		  
			ApplicationVar.getInstance().writeLog(new Exception(), str);
			
			String strThreadId = getMsgThreadId(str);
			sendMsgClient(strThreadId, recvMsg);
			//mCallBack.SendMsgDevHelp(msgSplit[3], " ");
			updateDevStateList(msgSplit[3],"dev_help","TRUE");
			
		}
		else if(msgSplit[0].equals("CS")&&msgSplit[1].equals("8003")&&msgSplit[2].equals("000001"))//请求设置编号
		{			
			msgSplit[0] = "SR";
			msgSplit[2] = "000000";
			
			String recvMsg = "&"+msgSplit[0]+"#"+msgSplit[1]+"#"+msgSplit[2]+"#"+msgSplit[3]+"#"
			                  +msgSplit[4]+ "$";
			String strThreadId = getMsgThreadId(str);
			sendMsgClient(strThreadId, recvMsg);
			

			
		}
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1002")&&msgSplit[2].equals("000000"))//读取设备心跳状态
		{
		
			
			tcpThread.setHeartTime(msgSplit[3], ApplicationVar.timeSecond());//msgSplit[3] strDevId
			//在这里更新设备状态
			String strState;
			if(msgSplit[4].equals("01"))
			{
				strState = "close";
			}
			else if(msgSplit[4].equals("02"))
			{
				strState = "open";
			}
			else if(msgSplit[4].equals("03"))
			{
				strState = "offline";
			}
			else if(msgSplit[4].equals("04"))
			{
				strState = "timer_open";
			}
			else if(msgSplit[4].equals("05"))
			{
				strState = "timer_close";
			}
			else
			{
				return;
			}

			//mCallBack.UpdateDevState(msgSplit[3],strState);
			updateDevStateList(msgSplit[3],"dev_state",strState);
			if(msgSplit[4].equals("04")||msgSplit[4].equals("05"))
			{
				//mCallBack.UpdateDevTimer(msgSplit[3],timeDate()+" "+msgSplit[5]+":"+msgSplit[6]+":"+msgSplit[7]);
				updateDevStateList(msgSplit[3],"dev_set_timer",timeDate()+" "+msgSplit[5]+":"+msgSplit[6]+":"+msgSplit[7]);
				
			}
			
		}
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1000")&&msgSplit[2].equals("000000"))//返回设备状态
		{
			String strState = (msgSplit[4].equals("01"))?"close":"open";
			//mCallBack.UpdateDevState(msgSplit[3],strState);
			updateDevStateList(msgSplit[3],"dev_state",strState);
			if(strState.equals("open"))
			{
				updateDevStateList(msgSplit[3],"dev_open_timer",timeSecond());
			}
			
			
		}
		
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1001")&&msgSplit[2].equals("000000"))//返回报警状态
		{
			if(null!=alarmStateHandler)
			{
				   alarmStateHandler.obtainMessage(1,-1,-1,msg).sendToTarget();  
			}
			
			mCallBackAlarm.UpdateDevStateAlarm(msgSplit[3], msgSplit[5]+":"+msgSplit[6]+":"+msgSplit[7]);
		   
		}
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1005")&&msgSplit[2].equals("000000"))//输入状态
		{			
			if(null != ioControlHandler)
			{
				ioControlHandler.obtainMessage(1,-1,-1,msg).sendToTarget();
			}
		}
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1004")&&msgSplit[2].equals("000000"))//输入状态
		{			
			if(null != ioControlHandler)
			{
				ioControlHandler.obtainMessage(1,-1,-1,msg).sendToTarget();
			}
		}
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1003")&&msgSplit[2].equals("000000"))//定时
		{			
			String strTimerState;
			if(msgSplit[4].equals("01"))//启用定时开机
			{
				strTimerState = "timer_open";
			}
			else if(msgSplit[4].equals("03"))//启用定时关机
			{
				strTimerState = "timer_close";
			}
			else 
			{
				return;
			}
			
			//mCallBack.UpdateDevState(msgSplit[3],strTimerState);
			updateDevStateList(msgSplit[3],"dev_state",strTimerState);
			updateDevStateList(msgSplit[3],"dev_set_timer",timeDate()+" "+msgSplit[5]+":"+msgSplit[6]+":"+msgSplit[7]);
			//mCallBack.UpdateDevTimer(msgSplit[3],timeDate()+" "+msgSplit[5]+":"+msgSplit[6]+":"+msgSplit[7]);
		}
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1006")&&msgSplit[2].equals("000000"))//完成注册设备
		{
	
			//if(null != deviceManageHandler)
			//{
				//deviceManageHandler.obtainMessage(1,-1,-1,msg).sendToTarget();	
			//}
		}
		else if(msgSplit[0].equals("CR")&&msgSplit[1].equals("1007")&&msgSplit[2].equals("000000"))//设备设置密码
		{
			//if(null != deviceManageHandler)
			//{
				//deviceManageHandler.obtainMessage(1,-1,-1,msg).sendToTarget();
			//}
		}
		
	}
	

	
	
	 public void onCreate() 
	 {
	        super.onCreate();
	        gInstance = this;
	        timer.schedule(task, 10000,10000); //10s心跳
	        timer_msg.schedule(task_msg,50,50); //50ms数据处理
	        clearLog();
	        openLog();
	        
	        
	        initDevStateList();
	        
	}
	  
	public LoginState onStart() //LoginOk,LoginNull,LoginErrRouter,LoginErrIp
	{
	   tcpThread.tcpServerStart(8080);
	   String ip = tcpThread.getLocalIP(this);
	   String routerName = tcpThread.getRouterName(this);
	    if (ip==null || routerName == null)
	    {
	    	return LoginState.LoginNull;
	    }
	    
	    if (ip.equals("192.168.1.2") && routerName.equals("\"TJKJ2016\""))
	    {
	    	return LoginState.LoginOk;
	    }
	    else if (!routerName.equals("\"TJKJ2016\""))
	    {
			return LoginState.LoginErrRouter;
		}
	    else 
	    {
		    return LoginState.LoginErrIp;	
		}
	}
	public void onQuit() throws IOException 
	{ 
		tcpThread.tcpServerStop(8080);
		closeLog();
	}    
	
	    
	    
	
////////////////////////////	 File ////////////////////  
	  public static ApplicationVar getInstance()
	  {
          // TODO Auto-generated method stub
          return gInstance;
      }
	  
	  ////////////// DevInfo //////////////////////////////
	public void getArrayDevice(ArrayList<HashMap<String, Object>> listItem) 
	{  
		try 
		{
			readHashmapFileDevInfo(listItem);
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }  
  
	
	public void setArrayDevice(ArrayList<HashMap<String, Object>> arry)
	{  
		gDeviceItem = arry; 
		try 
		{
			writeHashmapFileDevInfo(gDeviceItem);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } 

	 private void readHashmapFileDevInfo(ArrayList<HashMap<String, Object>> arry)throws Exception
	    {
	    	String FILE_NAME = "device_info.inf";  
	        arry.clear();
	    	byte[] b = new byte[1024];  
	    	String str = new String();         
	    	FileInputStream fis = openFileInput(FILE_NAME);  
	    	int num;  

	    	while ((num = fis.read(b)) != -1) 
	    	{  
	    	    str += (new String(b, 0, num));  
	    	}  
	    	String [] str_1 = str.split("#&");
	    	String key = new String();
	    	String value = new String();
	    	for(int i=0;i<str_1.length;i++)
	    	{
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    	    String [] str_2 = str_1[i].split(",");
	    	    for(int j=0;j<str_2.length;j++)
	    	    {
	    	    	String [] str_3 = str_2[j].split(":");
	    	    	key = str_3[0];
	    	    	value = str_3[1];
	    	    	if(key.equals("dev_id"))
	    	    	{
	    	    		map.put("dev_id", value);
	    	    	}
	    	    	else if(key.equals("dev_name"))
	    	    	{
	    	    		map.put("dev_name", value);
	    	    	}
	    	    	else if(key.equals("dev_channel"))
	    	    	{
	    	    		map.put("dev_channel", value);
	    	    	}
	    	    }
	    	    arry.add(map);
	    	}
	    	fis.close();  
	    }
	 
	  private void writeHashmapFileDevInfo(ArrayList<HashMap<String, Object>> arry) throws Exception
	  {
	    	String FILE_NAME = "device_info.inf";   
	        String []info = new String[arry.size()];
	        for(int i=0;i<arry.size();i++)
	        {
	        	info[i] ="";
	        	info[i] +="dev_id"+":"+(String)arry.get(i).get("dev_id") ;
	        	info[i] +=",";
	        	info[i] +="dev_name"+":"+(String)arry.get(i).get("dev_name") ;
	        	info[i] +=",";
	        	info[i] +="dev_channel"+":"+(String)arry.get(i).get("dev_channel") ;
	        	info[i] += "#&";
	        }
	        
	    	FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);  
	        for(int i=0;i<arry.size();i++)
	        {
	    	    fos.write(info[i].getBytes());
	        }
	    	fos.close();
	       
	   }
///////////////// DevInfo End  ////////////////////////////////
///////////////// userInfo ////////////////////////////////
	  
	  public void getArrayUsrInfo(ArrayList<HashMap<String, Object>> listInfo)
		{  
			try {
				  readHashmapFileUsrInfo(listInfo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
	    }  
	  
		public void setArrayUsrInfo(ArrayList<HashMap<String, Object>> arry)
		{  
			gUsrInfo = arry; 
			try 
			{
				writeHashmapFileUsrInfo(gUsrInfo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } 
	  
		 private void writeHashmapFileUsrInfo(ArrayList<HashMap<String, Object>> array) throws Exception
		 {
			 String FILE_NAME = "usr_info.inf";   
		        String []info = new String[array.size()];
		        for(int i=0;i<array.size();i++)
		        {
		        	info[i] ="";
		        	info[i] +="name"+":"+(String)array.get(i).get("name") ;
		        	info[i] +=",";
		        	info[i] +="pwd"+":"+(String)array.get(i).get("pwd") ;
		        	info[i] +=",";
		        	info[i] +="auth"+":"+(String)array.get(i).get("auth") ;
		        	info[i] += "#&";
		        }
		        
		    	FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);  
		        for(int i=0;i<array.size();i++)
		        {
		    	    fos.write(info[i].getBytes());
		        }
		    	fos.close();
		       
		 }
		 private void readHashmapFileUsrInfo(ArrayList<HashMap<String, Object>> arry) throws Exception
		  {
			 String FILE_NAME = "usr_info.inf";  
		        arry.clear();
		    	byte[] b = new byte[1024];  
		    	String str = new String();         
		    	FileInputStream fis = openFileInput(FILE_NAME);  
		    	int num;  

		    	while ((num = fis.read(b)) != -1) 
		    	{  
		    	    str += (new String(b, 0, num));  
		    	}  
		    	String [] str_1 = str.split("#&");
		    	String key = new String();
		    	String value = new String();
		    	for(int i=0;i<str_1.length;i++)
		    	{
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    	    String [] str_2 = str_1[i].split(",");
		    	    for(int j=0;j<str_2.length;j++)
		    	    {
		    	    	String [] str_3 = str_2[j].split(":");
		    	    	key = str_3[0];
		    	    	value = str_3[1];
		    	    	if(key.equals("name"))
		    	    	{
		    	    		map.put("name", value);
		    	    	}
		    	    	else if(key.equals("pwd"))
		    	    	{
		    	    		map.put("pwd", value);
		    	    	}
		    	    	else if(key.equals("auth"))
		    	    	{
		    	    		map.put("auth", value);
		    	    	}
		    	    }
		    	    arry.add(map);
		    	}
		    	fis.close();
		       
		   }
	  
	  
	  
	  
///////////////////userInfo End////////////////////
	  
	  
	  
	  
	  
///////////////// PageInfo 	  //////////////////////////////////
	  
	  public HashMap<String, Object> getChannelInfo()
		{  
			try {
				gChannelInfo = readHashmapFileChannelInfo();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return gChannelInfo; 
	    }  
	  
		public void setChannelInfo(HashMap<String, Object> map)
		{  
			gChannelInfo = map; 
			try 
			{
				writeHashmapFileChannelInfo(gChannelInfo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } 
	  
		 private void writeHashmapFileChannelInfo(HashMap<String, Object> map) throws Exception
		 {
		    	String FILE_NAME = "channel_info.inf";   
		        String info = new String();
		     
		        info ="";
		        info +="DevId"+":"+(String)map.get("DevId") ;
		        info +=",";
		        info +="InputChannel"+":"+(String)map.get("InputChannel") ;
		        info +=",";
		        info +="OutputChannel"+":"+(String)map.get("OutputChannel") ;
		        info += "#&";
		        
		    	FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);  
		    	fos.write(info.getBytes());
		    	fos.close();
		       
		 }
		 private HashMap<String, Object>  readHashmapFileChannelInfo()throws Exception
		 {
			    HashMap<String,Object> map = new HashMap<String,Object>();
		    	String FILE_NAME = "channel_info.inf";  
		        map.clear();
		    	byte[] b = new byte[1024];  
		    	String str = new String();         
		    	FileInputStream fis = openFileInput(FILE_NAME);  
		    	int num;  

		    	while ((num = fis.read(b)) != -1) 
		    	{  
		    	    str += (new String(b, 0, num));  
		    	}  
		    	
		    	String [] str_1 = str.split("#&");
		    	String key = new String();
		    	String value = new String();
		    	
		    	
		    	String [] str_2 = str_1[0].split(",");
		    	for(int j=0;j<str_2.length;j++)
		    	{
		    	    	String [] str_3 = str_2[j].split(":");
		    	    	key = str_3[0];
		    	    	value = str_3[1];
		    	    	if(key.equals("InputChannel"))
		    	    	{
		    	    		map.put("InputChannel", value);
		    	    	}
		    	    	else if(key.equals("OutputChannel"))
		    	    	{
		    	    		map.put("OutputChannel", value);
		    	    	}
		    	    	else if(key.equals("DevId"))
		    	    	{
		    	    		map.put("DevId", value);
		    	    	}
		    	 }
		    	
		    	fis.close();  
		    	return map;
		 }
	  
	  
	  
	  
//////////////////PageInfo End//////////////////////////////////
 
////////////////Log///////////////////////////////	  
	   public void closeLog()
	   {
		   try {
				fosLog.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	   public void openLog()
	   {
		   String FILE_NAME = "info.log"; 
		    try {
				fosLog = openFileOutput(FILE_NAME,Context.MODE_APPEND);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	   }
		 
		 
	   public void writeLog(Exception exp,String info) 
	  {
		   
		    info =  timeSecond()+" "+ CommonFunc.getFileName(exp) + " " + CommonFunc.getLineNumber(exp)+ ":" + info + "\n";
	    	try {
				fosLog.write(info.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   
	  }
	  public String readLog()
	  {
		  String FILE_NAME = "info.log"; 
		  try {
			fisLog = openFileInput(FILE_NAME);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  String str = new String();    
		  byte[] b = new byte[1024]; 
		  int num; 
		  
	       try {
			while ((num = fisLog.read(b)) != -1) {  

				    str += (new String(b, 0, num));  
			        
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    try {
			fisLog.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return str;
	  }
	  public void clearLog()
	  {
		  String FILE_NAME = "info.log"; 
		    try {
				fosLog = openFileOutput(FILE_NAME,Context.MODE_PRIVATE);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		   String info =" ";
		    if(!info.substring(info.length()-1,info.length()).equals("\n"))
		    {
		    	info += "\n";
		    }
	    	try {
				fosLog.write(info.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	try {
				fosLog.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
/////////////////Log End//////////////////////	  
	  
	  
	  
	  @SuppressLint("SimpleDateFormat") 
	  static public String timeDate() 
	  {
		  SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		  String date = sDateFormat.format(new java.util.Date());
		  return date;
	  }
	  @SuppressLint("SimpleDateFormat") 
	  static public String timeSecond()
	  {
		  SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  String date = sDateFormat.format(new java.util.Date());
		  return date;
		  
	  }
	  static public Date getCurDate()
	  {
		  Date date = new Date(System.currentTimeMillis());
		  return date;
	  }
	 static public boolean isNumber(String str)
      {
    	  for (int i = 0; i < str.length(); i++)
    	  {
    		   if (!Character.isDigit(str.charAt(i)))
    		   {
    		    return false;
    		   }
    	   }
    		return true;
      }

	 public void setCallBackAlarm(CallBackDevAlarm back)
	 {
		     this.mCallBackAlarm = back;
		 
	 }
	  
}
