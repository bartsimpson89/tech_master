#ifndef _USER_UART_H
#define _USER_UART_H




void UART1_Configuration(void);
void UART1_GPIO_Configuration(void);
u8 Uart1_PutChar(u8 ch);
void Uart1_PutString(u8* buf , u8 len);
void Uart1_getString(void);
void DealMsSendMsg(void);
void DealMsRecvMsg(void);
void SendUartLen(u8 msg[]);
void SendUartMsg(u8 msg[]);

extern bool GetCmdMsgOk();
extern void ClearCmdMsgOk();

void UART3_Configuration(void);
void UART3_GPIO_Configuration(void);
u8 Uart3_PutChar(u8 ch);
void Uart3_PutString(u8* buf , u8 len);
u8 Uart3_ReadString(u8* buf,u8 buf_len);

enum MSG_SEND_TYPE
{
	  TYPE_CMD = 0,
    TYPE_MSG = 1,
    TYPE_UNKNOW = 2
};


#endif /* _USER_UART_H*/ 
