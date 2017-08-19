#include "user_protocol.h"
#include "user_global.h"
#include "string.h"
#include "user_led.h"
#include "user_msgheap.h"
#include "user_display.h"
#include "user_uart.h"
#include "user_flash.h"
static u8 send_cmd_rst[MSG_MAX_LENGTH] ="AT+RST\r\n";
static u8 send_cmd_set_ap[MSG_MAX_LENGTH] = "AT+CWMODE=1\r\n";
static u8 send_connect_router[MSG_MAX_LENGTH]="AT+CWJAP=\"TJKJ2016\",\"tuojiaokeji\"\r\n";
//static u8 send_connect_router[128]="AT+CWJAP=\"TP-LINK_710CC0\",\"hztj9999\"\r\n";
static u8 send_mul_connect[MSG_MAX_LENGTH] = "AT+CIPMUX=1\r\n";
static u8 send_connect_IP_Server[MSG_MAX_LENGTH]="AT+CIPSTART=1,\"TCP\",\"192.168.1.2\",8080\r\n";
//static u8 send_connect_IP_Server[64]="AT+CIPSTART=1,\"TCP\",\"192.168.2.121\",8080\r\n";
//////////////////////////connect Server//////////////
static bool isHelp = FALSE;

void MsgSendCmd(enum AT_CMD cmd,u16 milisec)
{
	u8 *ptr = 0;
	u32 len = 0;
	int i=0;
	switch(cmd)
	{
		case AT_RESTART:
			ptr = send_cmd_rst;
			break;
		case AT_SET_AP:
			ptr = send_cmd_set_ap;
			break;
		case AT_CONNECT_ROUTER:
			ptr = send_connect_router;
		  break;
		case AT_MUL_CONNECT:
			ptr = send_mul_connect;
		  break;
		case AT_CONNECT_IP_SERVER:
			ptr = send_connect_IP_Server;
		  break;
		default:
		  break;
	}
	len = getStrLen(ptr);
	pushSendMsg(ptr,len);
	//Uart1_PutString(ptr,len);
	for(i=0;i<milisec;i++)
	{
		 if(GetCmdMsgOk())
		 {
		    ClearCmdMsgOk();
		     break;
		 }
     DelayMs(1);		 
	}
	//Uart3_PutString(RxBuffer1,64);
}


///////////////////////sendMsg////////////////////
void MsgSendLEN(u8 id,u8 data_send[])
{
	u32 len = 0;
	u8 len_buff[3];
	u8 id_buff[2];
	u8 send_msg[20] = "AT+CIPSEND=";//发送报文长度的指令
	u32 append_id = getStrLen(send_msg);
	if(data_send == 0)
	{
		return;
	}
	len =  getStrLen(data_send);
	intToCharBuff(id,id_buff,2);
	intToCharBuff(len,len_buff,3);
	send_msg[append_id++]=id_buff[0];
	send_msg[append_id++]=id_buff[1];
	send_msg[append_id++]=',';
	send_msg[append_id++]=len_buff[0];
	send_msg[append_id++]=len_buff[1];
	send_msg[append_id++]=len_buff[2];
  send_msg[append_id++]='\r';
  send_msg[append_id++]='\n';
	send_msg[append_id++] = 0;
	Uart1_PutString(send_msg,getStrLen(send_msg));
}
void MsgSendData(u8 data_send[])
{
	u32 len = getStrLen(data_send);
	Uart1_PutString(data_send,len);
}
bool isGetHelp()
{
	return isHelp;
}
void resetGetHelp()
{
	 isHelp = FALSE;
}
void sendSMsg(enum AT_SEND_MSG SMSG_TYPE)
{
	u8 head[] = "CS";
	u8 cmd[5];
	u8 dir[] = "000001";
	u8 id[4];
	u8 msg[64];
	u8 state[] = "00";
	u8 time[] = "00";
	int index=0;
	int i=0;
	switch(SMSG_TYPE)
	{
		case SMSG_REGISTER:
			   memcpy(cmd,"8001",5);
		     break;
	  case SMSG_HEART:
			   memcpy(cmd,"8000",5);
		     state[1] = '0';
		     break;
		case SMSG_HELP:
			   memcpy(cmd,"8002",5);
		     state[1] = '4';
		     break;
		case SMSG_SETID:
			   memcpy(cmd,"8003",5);
		     state[1] = '1';
		     break;
	}
	intToCharBuff(dev.id,id,4);
	msg[index++] = '&';
	for(i=0;i<getStrLen(head);i++)
	{
		msg[index++] = head[i];
	}
	msg[index++] = '#';
	for(i=0;i<getStrLen(cmd);i++)
	{
		msg[index++] = cmd[i];
	}
	 msg[index++] = '#';
	for(i=0;i<getStrLen(dir);i++)
	{
		msg[index++] = dir[i];
	}
	 msg[index++] = '#';
	for(i=0;i<4;i++)
	{
		msg[index++] = id[i];
	}
	 msg[index++] = '#';
	if(SMSG_REGISTER == SMSG_TYPE)
	{
			for(i=0;i<getStrLen(time);i++)
			{
				msg[index++] = time[i];
			}
			 msg[index++] = '#';
	    for(i=0;i<getStrLen(time);i++)
			{
				msg[index++] = time[i];
			}
			 msg[index++] = '#';
			for(i=0;i<getStrLen(time);i++)
			{
				msg[index++] = time[i];
			}	 
	}
	else
	{
		for(i=0;i<getStrLen(state);i++)
			{
				msg[index++] = state[i];
			}	
	}
	msg[index++] = '$';
	msg[index++] = '\0';
	pushSendMsg(msg,getStrLen(msg));
	
}
void DealMsg(u8 buff[])
{
	if(buff[0] == '&' && buff[1] == 'S' && buff[2] == 'S')
	{
		ProcSSMsg(buff);
		SendRMsg(buff);
	}
	else if(buff[0] == '&' && buff[1] == 'S' && buff[2] == 'R')
	{
		ProcSRMsg(buff);
	}
	
}
static void ProcSSMsg(u8 buff[])
{
	u8 cmd_heart[] = "1002";
	u8 cmd_enable[] = "1000";
	u8 cmd_timer[] = "1003";
	u8 cmd_io_control[] = "1004";
	u8 cmd_alarm[] = "1001";
	u8 cmd_set_pwd[] = "1007";
	u8 cmd_recv_help[] = "1008";
	u8 cmd_modify_id[] ="1009";
	u8 cmd[5];
	int pos=0;
	int i=0;
	int port = 0;
//	u32 id=0;
	for(i=0;i<getStrLen(buff);i++)
	{
		if(buff[i]=='#')
		{
			pos = i;
			break;
		}
	}
	pos++;
	for(i=0;i<4;i++)
	{
		cmd[i] = buff[pos+i];
	}
	cmd[i] = '\0';
	if(compareStr(cmd,cmd_heart))
	{
		dev.heartState = HEART_ALIVE;
	}
	else if(compareStr(cmd,cmd_enable))
	{
		if(buff[22] == '1') //禁止开机
		{
			
			dev.devState = DEV_DISABLE_OPEN;
			LED_OFF();
			output_OFF(12);
		}
		else if(buff[22] == '2')
		{
			
			dev.devState = DEV_ENABLE_OPEN;
			LED_ON();
			output_ON(12);
		}
		
	}
	else if(compareStr(cmd,cmd_timer))
	{
		 if(buff[21] == '0' && buff[22] == '1')
		 {
			 dev.enable_opentime_flag = TRUE;
			 dev.openTime[0] = (buff[24]-'0')*10+buff[25]-'0';
			 dev.openTime[1] = (buff[27]-'0')*10+buff[28]-'0';
			 dev.openTime[2] = (buff[30]-'0')*10+buff[31]-'0';
			 dev.devState = DEV_ON_TIME; 
		 }
		  else if(buff[21] == '0' && buff[22] == '2')
		 {
			 dev.enable_opentime_flag  = FALSE;
		 }
		 else if(buff[21] == '0' && buff[22] == '3')
		 {
			 dev.enable_closetime_flag = TRUE;
			 dev.closeTime[0] = (buff[24]-'0')*10+buff[25]-'0';
			 dev.closeTime[1] = (buff[27]-'0')*10+buff[28]-'0';
			 dev.closeTime[2] = (buff[30]-'0')*10+buff[31]-'0';
			 dev.devState = DEV_OFF_TIME;
		 }
		 else if(buff[21] == '0' && buff[22] == '4')
		 {
			 dev.enable_closetime_flag = FALSE;
			 
		 }
		 
	}
	else if(compareStr(cmd,cmd_io_control))
	{
		 if(buff[21] == '0' && buff[22] == '2')//打开
		 {
			    port = (buff[24]-'0')*10+(buff[25]-'0');
			    output_ON(port);
			    //dev.devState = DEV_ENABLE_OPEN;
		 }
		 else if(buff[21] == '0'&&buff[22] == '1')//关闭
		 {
			    port = (buff[24]-'0')*10+(buff[25]-'0');
			    output_OFF(port);
			    //dev.devState = DEV_DISABLE_OPEN;
		 }
	}
	else if(compareStr(cmd,cmd_alarm))
	{
		 if(buff[21] == '0' && buff[22] == '2')//清除
		 {
			 dev.alarm[0] = 0;
			 dev.alarm[1] = 0;
			 dev.alarm[2] = 0;
		 }
	}
	else if(compareStr(cmd,cmd_set_pwd))
	{
		   devPwd[0] = buff[21];
		   devPwd[1] = buff[22];
		   devPwd[2] = buff[23];
		   devPwd[3]  = buff[24];
	}
	else if(compareStr(cmd,cmd_recv_help))
	{
		   isHelp = TRUE;
	}
	else if(compareStr(cmd,cmd_modify_id))
	{
		   dev.id = (buff[23]-'0')*10+(buff[24]-'0');
		   writeIdToFlash(dev.id);
	}
}


static void ProcSRMsg(u8 buff[])
{
	u8 cmd_heart[] = "8000";
	u8 cmd_register[] = "8001";
	u8 cmd[5];
//	u8 tips[] ="setid:init";
	int pos=0;
	int i=0;
	for(i=0;i<getStrLen(buff);i++)
	{
		if(buff[i]=='#')
		{
			pos = i;
			break;
		}
	}
	pos++;
	for(i=0;i<4;i++)
	{
		cmd[i] = buff[pos+i];
	}
	cmd[i] = '\0';
	if(compareStr(cmd,cmd_heart))
	{
		  dev.heartState = HEART_ALIVE;
	}
	else if(compareStr(cmd,cmd_register))
	{
		dev.time[0] = (buff[21]-'0')*10+(buff[22]-'0');
		dev.time[1] = (buff[24]-'0')*10+(buff[25]-'0');
		dev.time[2] = (buff[27]-'0')*10+(buff[28]-'0');
		dev.connectState = CONNECT_REGISTER;
    if(dev.preDevState != DEV_UNCONENCT)
		{
		   dev.devState = dev.preDevState;  
		}
		else
		{
		   dev.devState = DEV_DISABLE_OPEN;
		}
		TimingDelay = 0;
	}
	
}
static void SendRMsg(u8 buff[])
{
	u8 cmd_heart[] = "1002";
	u8 cmd_enable[] = "1000";
//	u8 cmd_timer[] = "1003";
	u8 cmd_io_state[] = "1005";
	u8 cmd_alarm[] = "1001";
	u8 cmd_setid[]="1009";
	u8 cmd[5];
	u8 heart[] = "&CR#1002#000000#0000#00#00#00#00$";
	u8 id[4]; 
	int pos=0;
	int i=0;
	for(i=0;i<getStrLen(buff);i++)
	{
		if(buff[i]=='#')
		{
			pos = i;
			break;
		}
	}
	pos++;
	for(i=0;i<4;i++)
	{
		cmd[i] = buff[pos+i];
	}
	cmd[i] = '\0';
	if(compareStr(cmd,cmd_enable))
	{
		if(dev.devState == DEV_DISABLE_OPEN)
		{
		     buff[22] = '1';
		}
		else if(dev.devState == DEV_ENABLE_OPEN)
		{
			   buff[22] = '2';
		}
		
		
	}
  else if(compareStr(cmd,cmd_io_state))	
	{
		for(i=0;i<10;i++)
		{
			 if(Bit_SET == readPort(i+1))
			 {
				    buff[21+i*2] = '2';
			 }
			 else
			 {
				    buff[21+i*2] = '1';
			 }
			 
		}
	}
	else if(compareStr(cmd,cmd_alarm))
	{
		 buff[24] = dev.alarm[0]/1000+'0';
		 buff[25] = dev.alarm[0]/100+'0';
		 buff[26] = dev.alarm[0]/10+'0';
		 buff[27] = dev.alarm[0]%10+'0';
		
		 buff[29] = dev.alarm[1]/1000+'0';
		 buff[30] = dev.alarm[1]/100+'0';
		 buff[31] = dev.alarm[1]/10+'0';
		 buff[32] = dev.alarm[1]%10+'0';
		
		
		 buff[34] = dev.alarm[2]/1000+'0';
		 buff[35] = dev.alarm[2]/1000+'0';
		 buff[36] = dev.alarm[2]/10+'0';
		 buff[37] = dev.alarm[2]%10+'0';
		
	}		
	
	  buff[1] = 'C';
	  buff[2] = 'R';
	  buff[14] ='0';
	if(compareStr(cmd,cmd_heart))
	{
		//"&CR#1002#000000#0001#01$"
		intToCharBuff(dev.id,id,4);
		heart[16] = id[0];
		heart[17] = id[1];
		heart[18] = id[2];
		heart[19] = id[3];
		if(dev.devState == DEV_DISABLE_OPEN)
		{
		     heart[22] = '1';
		}
		else if(dev.devState == DEV_ENABLE_OPEN)
		{
			   heart[22] = '2';
		} 
		else if(dev.devState == DEV_ON_TIME) //&SS#1002#000001#0001#00#00#00#00$
		{
			   heart[22] = '4';
			   heart[24] = dev.openTime[0]/10+'0';
			   heart[25] = dev.openTime[0]%10+'0';
			   heart[27] = dev.openTime[1]/10+'0';
			   heart[28] = dev.openTime[1]%10+'0';
			   heart[30] = dev.openTime[2]/10+'0';
				 heart[31] = dev.openTime[2]%10+'0';
		}
		else if(dev.devState == DEV_OFF_TIME)
		{
			   heart[22] ='5';
			   heart[24] = dev.closeTime[0]/10+'0';
			   heart[25] = dev.closeTime[0]%10+'0';
			   heart[27] = dev.closeTime[1]/10+'0';
			   heart[28] = dev.closeTime[1]%10+'0';
			   heart[30] = dev.closeTime[2]/10+'0';
			   heart[31] = dev.closeTime[2]%10+'0';
		}
		
		pushSendMsg(heart,getStrLen(heart));
		
	}
	else if(compareStr(cmd,cmd_setid))
	{
		 dev.connectState = CONNECT_SETID;
		 pushSendMsg(buff,getStrLen(buff));
	}
	else
	{
	   pushSendMsg(buff,getStrLen(buff));
	}
	  
}

enum MSG_SEND_TYPE GetMsgType(u8 msg[],int len)
{
    if(len<3)
	{
	   return TYPE_UNKNOW;
	}
    if((msg[0] == '&')&& (msg[1] == 'C'))//"&C"开头的是具体信息
	{
	   return TYPE_MSG;
	}
	else if((msg[0] == 'A') && (msg[1] == 'T') &&  (msg[2] == '+')) //AT+CIPSEND=
	{
	   return  TYPE_CMD;
	}
	else
	{
		return TYPE_UNKNOW;
	}
	
     
}
