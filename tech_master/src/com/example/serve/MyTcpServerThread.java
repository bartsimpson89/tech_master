package com.example.serve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import com.example.tech_master.ApplicationVar;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.content.Context;

public class MyTcpServerThread 
{
	private boolean isListened=false;
	private Socket socket=null;
	private ServerSocket socketServer=null;

	
	private  final Handler mHandler;
	private ArrayList<HashMap<String, Object>> gListSocketInfo  = new ArrayList<HashMap<String, Object>>();//ServerThread,devid,connect_time,register_time,heart_time
	
	
	private class TcpServerThread extends Thread
	{
		private int serverPort = 0;
		public TcpServerThread(int port)
		{
			serverPort = port;
		}
		public void run()
		{
			
			try 
			{
				socketServer =new ServerSocket(serverPort);
				timer.schedule(task,20000, 20000);
				
			} catch (IOException e) 
			{
				
				
			}
			while(isListened)
			{
				try 
				{
					socket=socketServer.accept();
					//�����������յ��ͻ�������
					ServerThread thread = new ServerThread(socket);
					thread.setCallBack(new CallBack() 
					{
			            public void recvMsgFromClient(String strMsg) 
			            {
			            	sendMsgToApplication("recvMsg:"+strMsg);
			            }
                    });
					thread.start();
				
                    HashMap<String, Object> map= new HashMap<String ,Object>();
					map.put("server_thread", thread);
					map.put("thread_id",String.valueOf(thread.getId()));
					map.put("dev_id", String.valueOf(0));
					map.put("connect_time", ApplicationVar.timeSecond());
					map.put("register_time", null);
					map.put("heart_time", null);
					
					gListSocketInfo.add(map);	
					
					
					String clientIP=socket.getInetAddress().toString();
					clientIP = clientIP.substring(clientIP.lastIndexOf("/") + 1 );
					String strMsg ="infoMsg:"+"���ӿͻ��˳ɹ���"+clientIP+"(�ͻ��˵�ַ)"
							+"ThreadId"+String.valueOf(thread.getId()+"socketlist size"+String.valueOf(gListSocketInfo.size()));
					//sendMsgToApplication(receiveInfoServer);
					ApplicationVar.getInstance().writeLog(new Exception(),strMsg);
				
				} catch (IOException e) 
				{
					String strMsg = "infoMsg:"+e.getMessage() + "\n";
					ApplicationVar.getInstance().writeLog(new Exception(),strMsg);
					//sendMsgToApplication(receiveInfoServer);
				}				
			}
		}
	}
	
	
	private void sendMsgToApplication(String msg)
	{
		mHandler.obtainMessage(1,-1,-1,msg).sendToTarget();
	}
///////////////
/*
* ͨ��threadid ��ȡserverthread
* */
	private ServerThread findServerThreadByThreadId(String strThreadId)
	{
		int i = 0;
		ServerThread thread = null;
		for(i=0;i<gListSocketInfo.size();i++)
		{
			String strListThreadId = (String)gListSocketInfo.get(i).get("thread_id");
			if(strThreadId.equals(strListThreadId))
			{
				thread = (ServerThread)gListSocketInfo.get(i).get("server_thread");
			    break;
			}
		}
		return (i==gListSocketInfo.size())?null:thread;
		
	}
///////////////
/*
* ͨ��devId ��ȡthreadId
* */
	public String findServerThreadIdByDevId(String strDevId)
	{
		int i = findServerListIndexByDevId(strDevId);
		if (i < 0)
		{
			return null;
		}
		return (String)gListSocketInfo.get(i).get("thread_id");
	}
	///////////////
	/*
	 * ͨ��devId ��ȡgListSocketInfo��index
	 * */
	
	private int findServerListIndexByDevId(String strDevId)
	{
		int i= 0;
		for(i=gListSocketInfo.size()-1;i>=0;i--)
		{
			String strListDevId = (String)gListSocketInfo.get(i).get("dev_id");
			if(strListDevId.equals(strDevId))
			{
			    break;
			}
		}
		return (i==gListSocketInfo.size())?-1:i;
	}
	
	/*
	 * ͨ��threadid ��ȡgListSocketInfo��index
	 * */
	private int findServerListIndexByThreadId(String strThreadId)
	{
		int i= 0;
		for(i=0;i<gListSocketInfo.size();i++)
		{
			String strThreadListId = (String)gListSocketInfo.get(i).get("thread_id");
			if(strThreadId.equals(strThreadListId))
			{
			    break;
			}
		}
		return (i==gListSocketInfo.size())?-1:i;
	}
	
	
	//ȥ����Ч��socket����
	private boolean removeSocketThreadId(String threadId) 
	{
		int i = findServerListIndexByThreadId(threadId);
		if(i<0)
		{
			return false;
		}
		ServerThread thread = (ServerThread)gListSocketInfo.get(i).get("server_thread");
		String strDevId = (String)gListSocketInfo.get(i).get("dev_id");
		
		try {
			thread.s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread.setSocketState(false);
		thread = null;
		gListSocketInfo.remove(i);
		
		SendMsgDevOffLine(threadId, strDevId);
		return true;
	}
	//֪ͨ���豸ID�Ѿ�����
	private void SendMsgDevOffLine(String strThreadId, String strDevId)
	{
		String strMsg = "("+strThreadId+")"+"&CR#1002#000000#"+strDevId+"#03$";
		sendMsgToApplication("recvMsg:"+strMsg);
	}
	//��ѯ��ǰSocket�����Ƿ���Ч
	/*����gListSocketInfo ÿ��idx ��ѯ�����Ƿ���Ч
	 *
	 */
	private boolean checkSocketIsVaild(int idx)
	{
			String strConnectTime = (String)gListSocketInfo.get(idx).get("connect_time");
		    String strRegisterTime = (String)gListSocketInfo.get(idx).get("register_time");
		    String strHeartTime =  (String)gListSocketInfo.get(idx).get("heart_time");
		    String strCurTime = ApplicationVar.timeSecond();
		    long lDiffSec = 0;
		    
	    	lDiffSec = DiffTimeSec(strConnectTime,strCurTime); //����ʱ��͵�ǰʱ��Ƚ�
	    	
	    
	    	
	    	if(lDiffSec < 20*1000) //����ʱ��͵�ǰʱ��<20 ������Ч
	    	{
	    		return true;
	    	}
	    	
	    	if(strRegisterTime == null) //��������ʱ��,��û��ע�� ������Ч
	    	{
	    		ApplicationVar.getInstance().writeLog(new Exception(),"socket is not register but connected");
	    		return false;
	    	}
	    	
	    	lDiffSec = DiffTimeSec(strRegisterTime,strCurTime); 
	    	
	    	if(lDiffSec < 20*1000) //ע���͵�ǰʱ��<60 ������Ч
	    	{
	    		return true;
	    	}
	    	
	    	if(strHeartTime == null) //ע��ʱ�䳬����ǰʱ��20 ��û������ʱ��
	    	{
	    		ApplicationVar.getInstance().writeLog(new Exception(),"socket is register but no heart");
	    		return false;
	    	}
	    	
	    	lDiffSec = DiffTimeSec(strHeartTime,strCurTime); //����ʱ��͵�ǰʱ��Ƚ�
	    	
	    	if(lDiffSec < 60*1000) //����ʱ��͵�ǰʱ�䡶20 ������Ч
	    	{
	    		return true;
	    	}	    	
	    	ApplicationVar.getInstance().writeLog(new Exception() , "socket heart is >60s");
		    return false;
	}
	
	/*
	 * ����������ʱ����ʱ���ֵ
	 * */
	@SuppressLint("SimpleDateFormat")
	public static long DiffTimeSec(String strTime,String strCurTime)
	{
		if(strTime==null||strCurTime==null)
		{
			return -1;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
		Date dateTime;
		Date dateCurTime;
	    
		try 
		{
			dateTime = (Date) sdf.parse(strTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	    
	    try 
	    {
			dateCurTime = (Date) sdf.parse(strCurTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	    return dateCurTime.getTime()-dateTime.getTime();
	    
		
	}
	/*
	 * ������ÿ��20s ���ն�������Ч�Լ�飨����δע�ᣬע����������������ʱ��
	 * */
	private final Timer timer = new Timer();  
    private TimerTask task = new TimerTask() 
    {  
    	Stack<String> stThreadId = new Stack<String>();
        @Override  
        public void run() 
        {
        	 try 
        	 {
        		 for(int i=0;i<gListSocketInfo.size();i++)
        		 {
        			   if(!checkSocketIsVaild(i))
        			   {   
        				   stThreadId.push((String)gListSocketInfo.get(i).get("thread_id"));
        			   }
        		 }
        		
        		 while(stThreadId.size()>0)
        		 {
        			 String strThreadId = stThreadId.pop();
        			 removeSocketThreadId(strThreadId);//gListSockeInfo Ҳ��remove
        			 
        			 ApplicationVar.getInstance().writeLog(new Exception() , strThreadId+"is remove");
        			 
        		 }
        		 
              }
             catch (Exception e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("exception...");
                ApplicationVar.getInstance().writeLog(new Exception() , "exception.."+e.getMessage());
            }
        }  
    };   
	
	
	//���͸�ApplicationVar���handler
	public MyTcpServerThread(Handler handler) {
		// TODO Auto-generated constructor stub
		mHandler = handler;
	}
	
	//�õ�����IP��ַ��WIFI��
	public String getLocalIP(Context text)
	{  
        WifiManager wifiManager = (WifiManager)text.getSystemService(Context.WIFI_SERVICE);    
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();    
        int ipAddress = wifiInfo.getIpAddress();                
        if(ipAddress==0)return null;  
        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."  
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));  
    }
	//�õ�����·�������ƣ�WIFI��
	public String getRouterName(Context text)
	{  
        WifiManager wifiManager = (WifiManager)text.getSystemService(Context.WIFI_SERVICE);    
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();    
        String routerName = wifiInfo.getSSID();           
        return routerName; 
    }
	/*�ⲿ�ӿ�
	 * ��ʼtcpserver
	 * ��Σ�port(int)
	 */
	public void tcpServerStart(int port)
	{
			isListened=true;
			new TcpServerThread(port).start();
	}
	/*�ⲿ�ӿ�
	 * ��ͣtcpserver
	 * ��Σ�port(int)
	 */
	public void tcpServerStop(int port)
	{
		isListened=false;
		if(socketServer!=null)
		{
			try 
			{
				socketServer.close();
				socketServer=null;
				gListSocketInfo.clear();
				
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		new TcpServerThread(port).interrupt();
	}
    //�ⲿ�ӿ�
	/*��������ӵ�threadID������Ϣ
	 * ��Σ�ThreadID��String) ��Ϣ��String)
	 * */
	public boolean tcpSendMsg(String strThreadId, String info)
	{
		ServerThread thread = findServerThreadByThreadId(strThreadId);
		if(null == thread)
		{
			return false;
		}
		thread.sendMsgClient(info);
		return true;
	}
	/* �ⲿ����
	 * �����豸��ע��ID��ע��ʱ�䣺���յ�ע���豸ID��ע��ʱ��
	 * ��Σ��豸ID��String) ��������ʱ�䣨String)
	 * */
	
	public boolean setRegisterTimeAndId(String strThreadId, String strTime, String strDevId)
	{
		int i = findServerListIndexByThreadId(strThreadId);
		if(i<0)
		{
			return false;
		}
		
		gListSocketInfo.get(i).put("register_time", ApplicationVar.timeSecond());
		gListSocketInfo.get(i).put("dev_id", strDevId);
		
		ApplicationVar.getInstance().writeLog(new Exception(), strThreadId+strDevId);
		return true;
	}
	/*�����豸������ʱ�䣺���յ���������ʱ��ʱ����
	 * ��Σ��豸ID(String) ��������ʱ��(String)
	 * */
	public boolean setHeartTime(String strDevId, String strTime)
	{
		int i = findServerListIndexByDevId(strDevId);
		if(i<0)
		{
			return false;
		}
		gListSocketInfo.get(i).put("heart_time", ApplicationVar.timeSecond());
		return true;
	
	}
	
	
	
   
	
	
}
