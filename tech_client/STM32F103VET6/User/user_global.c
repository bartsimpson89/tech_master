#include "user_global.h"
#include "user_flash.h"
#include "user_uart.h"
Device dev={1,DEV_UNCONENCT,DEV_UNCONENCT,CONNECT_FAILED,HEART_INIT,{0,0,0},\
            FALSE,FALSE,FALSE,FALSE,{0,0,0},{0,0,0},{0,0,0}};
ServerInfo server_info={0,0,0,0};
volatile u32  TimingDelay = 0;
u8 RxBuffer1[RxBufferSize];
volatile u16 RxCounter1 = 0x00;
u8 RxBuffer2[RxBufferSize];
volatile u16 RxCounter2 = 0x00;
u8 writeLogBuff[4096];
u16 logBuffIndex=0;
void WriteLog(u8 msg[])
{
  int len =	getStrLen(msg);
	int i=0;
	for(i=0;i<len;i++)
	{
		writeLogBuff[logBuffIndex++] = msg[i];
		if(logBuffIndex>=4096)
		{
			logBuffIndex = 0;
			clear_buff(writeLogBuff,4096);
		}
	}
}


void clear_Buffer1()
{
	int  i=0;
	for(i=0;i<RxBufferSize;i++)
	{
		RxBuffer1[i]= 0;
	}
	RxCounter1 = 0;
}
void clear_Buffer2()
{
	int  i=0;
	for(i=0;i<RxBufferSize;i++)
	{
		RxBuffer2[i]= 0;
	}
	RxCounter2 = 0;
}
void clear_buff(u8 buff[],u32 len)
{
	int  i=0;
	for(i=0;i<len;i++)
	{
		buff[i]=0;
	}
}
void intToCharBuff(int data,u8 buff[],int size)
{
	  u8 tmp;
	  int i=0;
	  while(data)
		{
			  if(!size)
				{
					break;
				}
			  size--;
			  tmp = data%10;
			  data = data/10;
		    buff[size] = tmp+'0';	  
		}
    if(size!=0)
    {
			  for(i=size-1;i>=0;i--)
			  {
					 buff[i] = '0';
			  }
		}			
}
int charBuffToInt(u8 buff[],int size)
{
	int i=0;
	int data = 0;
	for(i=0;i<size;i++)
	{
		   if(buff[i]==0)
			 {
				 break;
			 }
		   data = buff[i]-'0'+10*data;
	}
	return data;
}
u32 getStrLen(u8 buff[])
{
	int i =0 ;
	if(0 == buff)
	{
		return 0;
	}
	while(buff[i]!='\0')
	{
		i++;
	}
	return i;
}
bool compareStr(u8 str1[],u8 str2[]) //比较两个字符串
{
	int i=0;
	int len1 = getStrLen(str1);
	int len2 = getStrLen(str2);
	if(len1 != len2)
	{
		return FALSE;
	}
	for(i=0;i<len1;i++)
	{
		 if(str1[i]!=str2[i])
		 {
			 break;
		 }
	}
	if(i!=len1)
	{
		return FALSE;
	}
	else
	{
		return TRUE;
	}
}




void initGlobalVariable()
{
	u8 tips[8] ="id:";
	server_info.p_id = (u8 *)ADDR_ID;
	server_info.p_server_ip =(u8 *)ADDR_SERVER_IP;
	server_info.p_router_name = (u8 *)ADDR_ROUTER_NAME;
	server_info.p_router_pwd = (u8 *)ADDR_ROUTER_PWD;
	server_info.p_alarm1=(u8 *)ADDR_LEAKCURRENT;
	server_info.p_alarm2=(u8 *)ADDR_OVERRANGE;
	server_info.p_alarm3=(u8 *)ADDR_OVERCURRENT;
	
	dev.id =    server_info.p_id[0]+server_info.p_id[1]*10;
	dev.devState = DEV_UNCONENCT;
	dev.connectState = CONNECT_FAILED;
	dev.heartState = HEART_INIT;
	if(dev.id<0||dev.id>=100)
	{
		 dev.id = 0;
	}

}
