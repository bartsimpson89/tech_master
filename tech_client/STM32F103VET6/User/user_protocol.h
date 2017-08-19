#ifndef _USER_PROTOCAL_H
#define _USER_PROTOCAL_H
#include "stm32f10x.h"
#include "stm32f10x_it.h"
#include "user_uart.h"
#include "main.h"

#define MAX_RECV_BUFF_SIZE (255)



extern void MsgSendCmd(enum AT_CMD cmd,u16 milisec);
extern void initMsg(void);
extern void sendSMsg(enum AT_SEND_MSG SMSG_TYPE);
extern void DealMsg(u8 buff[]);
extern void SendUartMsg(u8 msg[]);
extern void SendUartLen(u8 msg[]);
extern bool isGetHelp();
extern void resetGetHelp();
extern enum MSG_SEND_TYPE GetMsgType(u8 msg[],int len);



static void SendRMsg(u8 buff[]);
static void ProcSRMsg(u8 buff[]);
static void ProcSSMsg(u8 buff[]);


static void initMsgRouter(void);
static  void initMsgServer(void);
extern void MsgSendLEN(u8 id,u8 data_send[]);
extern void MsgSendData(u8 data_send[]);
static void initStrRouterServer(void);
#endif