package com.example.serve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.example.tech_master.ApplicationVar;

class ServerThread extends Thread
{
	public Socket s=null;
	private BufferedReader th_bufferedReaderServer ;
	private PrintWriter th_printWriteServer;
	private volatile  boolean isSocketAlive = true;
	private CallBack mCallBack;
	//private String msg;
	public ServerThread(Socket s)
	{
		this.s=s;
		try 
		{
			th_bufferedReaderServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			th_printWriteServer = new PrintWriter(s.getOutputStream(), true);
		} catch (IOException e) 
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public void run()
	{
		char[] buffer = new char[256];
		
		
		while (isSocketAlive)
		{
			try
			{
				//读到来自某客户端的消息
				if((th_bufferedReaderServer.read(buffer))>0)
				{		
					String strMsg = String.valueOf(buffer);
					mCallBack.recvMsgFromClient("("+String.valueOf(getId())+")"+strMsg);
					for(int i=0;i<buffer.length;i++)
					{
					    buffer[i] = '\0';
					}
					
					
				}
			}
			catch (Exception e)
			{
				
			}
		}
		ApplicationVar.getInstance().writeLog(new Exception() , "threaid"+String.valueOf(getId())+"is quit!");
		
	}
    public void setSocketState(boolean state)
    {
    	isSocketAlive = state;
    }
    //向某客户端发送消息

	public void sendMsgClient(String info)
	{
		th_printWriteServer.print(info);
		th_printWriteServer.flush();
		
	} 
    public void setCallBack(CallBack back)
    {
    	this.mCallBack =back;
    }
}
