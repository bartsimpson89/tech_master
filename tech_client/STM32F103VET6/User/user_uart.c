#include "stm32f10x.h"
#include "stm32f10x_it.h"
#include "main.h"
#include "user_uart.h"
#include "user_global.h"
#include "user_protocol.h"
#include "user_msgheap.h"
static u8 u8SendMsg[64];
static enum MSG_SEND_TYPE volatile flagSendMsgType = TYPE_UNKNOW;
static bool volatile cmdMsgOk = FALSE;
static u32 volatile sendLenTime = 0;  
static bool volatile sendMsgFlag = FALSE;
//串口1初始化
void UART1_Configuration(void)
{
  USART_InitTypeDef USART_InitStructure;

  USART_InitStructure.USART_BaudRate = 115200;
  USART_InitStructure.USART_WordLength = USART_WordLength_8b;
  USART_InitStructure.USART_StopBits = USART_StopBits_1;
  USART_InitStructure.USART_Parity = USART_Parity_No ;
  USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
  USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;
  
  //初始化串口1
  USART_Init(USART1, &USART_InitStructure);

  //使能串口1接收发送中断
  USART_ITConfig(USART1, USART_IT_RXNE, ENABLE);
//  USART_ITConfig(USART1, USART_IT_TXE, ENABLE);

  //使能串口1 
  USART_Cmd(USART1, ENABLE);
  
	/* CPU的小缺陷：串口配置好，如果直接Send，则第1个字节发送不出去
		如下语句解决第1个字节无法正确发送出去的问题 */
//	USART_ClearFlag(USART1, USART_FLAG_TC);     /* 清发送外城标志，Transmission Complete flag */
	  
}
//串口1 IO口初始化
void UART1_GPIO_Configuration(void)
{
  GPIO_InitTypeDef GPIO_InitStructure;
  // Configure USART1_Tx as alternate function push-pull 
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_9;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;
  GPIO_Init(GPIOA, &GPIO_InitStructure);

  // Configure USART1_Rx as input floating 
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_10;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;
  GPIO_Init(GPIOA, &GPIO_InitStructure);  
}
//串口1 发送一个字符
u8 Uart1_PutChar(u8 ch)
{
	// Write a character to the USART 
	USART_SendData(USART1, (u8) ch);
	while(USART_GetFlagStatus(USART1, USART_FLAG_TXE) == RESET)
	{
	}
	return ch;
}
//串口1发送一个字符串
void Uart1_PutString(u8* buf , u8 len)
{
    u8 i;
    for(i=0;i<len;i++)
    {
        Uart1_PutChar(*buf++);
    }
    USART_ITConfig(USART1, USART_IT_TXE, DISABLE);                //关闭USART1的TXE中断
}
void Uart1_getString()
{
	 u8 tmp;
	 if(USART_GetITStatus(USART1, USART_IT_RXNE)!= RESET )
	 {
		 tmp= USART_ReceiveData(USART1);
        if(RxCounter1 < RxBufferSize)
			  {
				   RxBuffer1[RxCounter1++] = tmp;
			  }
				else
				{
					 RxCounter1 = 0;
					 RxBuffer1[RxCounter1++] = tmp;
				}
	}
}

void DealMsSendMsg(void)
{ 
		if((sendMsgFlag == FALSE)&&(MSG_HEAP_SUCCESS == popSendMsg(u8SendMsg))) //cmd msg
		{
				flagSendMsgType = GetMsgType(u8SendMsg,getStrLen(u8SendMsg));
			  
			  if(TYPE_CMD == flagSendMsgType)
				{
					 Uart1_PutString(u8SendMsg ,getStrLen(u8SendMsg));
				}
				else if(TYPE_MSG == flagSendMsgType)
				{
						
							SendUartLen(u8SendMsg);
							sendMsgFlag = TRUE;
				}	
		}
		if(sendMsgFlag == TRUE)
		{
			 if(sendLenTime >= 100)
			 {
				 SendUartMsg(u8SendMsg);
				 sendMsgFlag = FALSE;
				 sendLenTime = 0;
			 }
			 sendLenTime++;
		}
	
    
}
void DealMsRecvMsg(void)
{
	int i,start_id,j,id;
	u8 recvMsg[64];
  for(i=0;i<RxBufferSize;i++)
	{
		 if(RxBuffer1[i]=='O'&&RxBuffer1[i+1]=='K')
		 {
			 //WriteLog(RxBuffer1);
			 cmdMsgOk = TRUE;
			 clear_Buffer1();
			 break;
		 }
		 else 
		 { 
				if((('&' == RxBuffer1[i])&&('S' == RxBuffer1[i+1])))
				{
						start_id = i;
						id = 0;
						for(j=start_id;j<RxBufferSize;j++)
						{
								if(id>=64)
								{
										break;
								}
								recvMsg[id]=RxBuffer1[j];
								id++;
								if(RxBuffer1[j] == '$' || id>=64)
								{
									break;
								}
						}
						if(j == RxBufferSize && id < 64)
						{
								for(j=0;j<RxBufferSize;j++)
								{
									if(id>=64)
									{
										break;
									}
									recvMsg[id]=RxBuffer1[j];
									id++;
									if(RxBuffer1[j] == '$')
									{
										break;
									}	 
								}
						}
						pushMsg(recvMsg,getStrLen(recvMsg));
						clear_Buffer1();
						break;
		    }
		 }
	}
}
bool GetCmdMsgOk()
{
	 return cmdMsgOk;
}
void ClearCmdMsgOk()
{
	 cmdMsgOk = FALSE;
}


void SendUartLen(u8 msg[])
{
			  MsgSendLEN(1,msg);
}
void SendUartMsg(u8 msg[])
{
	      MsgSendData(msg);
}















//串口2初始化
void UART3_Configuration(void)
{
  USART_InitTypeDef USART_InitStructure;

  USART_InitStructure.USART_BaudRate = 9600;
  USART_InitStructure.USART_WordLength = USART_WordLength_8b;
  USART_InitStructure.USART_StopBits = USART_StopBits_1;
  USART_InitStructure.USART_Parity = USART_Parity_No ;
  USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
  USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;
  
  USART_Init(USART3, &USART_InitStructure);
  
  //使能串口2接收
  USART_ITConfig(USART3, USART_IT_RXNE, ENABLE);

  USART_Cmd(USART3, ENABLE);  
}
//串口2 IO口初始化
void UART3_GPIO_Configuration(void)
{
  GPIO_InitTypeDef GPIO_InitStructure;
  // Configure USART1_Tx as alternate function push-pull 
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_10;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;
  GPIO_Init(GPIOB, &GPIO_InitStructure);

  // Configure USART1_Rx as input floating 
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_11;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;
  GPIO_Init(GPIOB, &GPIO_InitStructure);
}
//串口2 发送一个字符
//串口3 发送一个字符
u8 Uart3_PutChar(u8 ch)
{
  USART_SendData(USART3, (u8)ch);
  while(USART_GetFlagStatus(USART3, USART_FLAG_TXE) == RESET)
  {
  }
  return ch;
}
//串口3发送一个字符串
void Uart3_PutString(u8* buf , u8 len)
{
    u8 i;
    for(i=0;i<len;i++)
    {
        Uart3_PutChar(*buf++);
    }
		Uart3_PutChar('\r');
		Uart3_PutChar('\n');
}
u8 Uart3_ReadString(u8* buf,u8 buf_len)
{
	u8 i = 0;
	u8 len = 0;
	if(RxCounter2!=0)
	{
		for(i=0;i<RxCounter2;i++)
		{
			if(((i+1)<RxCounter2)&&RxBuffer2[i]==0x0D&&RxBuffer2[i+1]==0x0A)
		  {
			    break;
		  }
			buf[i] = RxBuffer2[i];
		}
	}
	return i;
}