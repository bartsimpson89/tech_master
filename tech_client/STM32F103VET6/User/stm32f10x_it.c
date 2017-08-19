/**
  ******************************************************************************
  * @file    Project/Template/stm32f10x_it.c 
  * @author  MCD Application Team
  * @version V3.0.0
  * @date    04/06/2009
  * @brief   Main Interrupt Service Routines.
  *          This file provides template for all exceptions handler and 
  *          peripherals interrupt service routine.
  ******************************************************************************
  * @copy
  *
  * THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS
  * WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE
  * TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY
  * DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS ARISING
  * FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS OF THE
  * CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.
  *
  * <h2><center>&copy; COPYRIGHT 2009 STMicroelectronics</center></h2>
  */ 

/* Includes ------------------------------------------------------------------*/
#include "stm32f10x_it.h"
#include "stm32f10x.h"
#include "main.h"
#include "user_led.h"
#include "user_uart.h"
#include "user_msgheap.h"
#include "user_protocol.h"
#include "user_global.h"
#include "user_flash.h"
#include "user_display.h"


u8 volatile  exFlagCheckHeart = 0; //exFlagCheckHeart

static u16 volatile  gCheckHeartTime = 0;//gCheckHeartTime
static u32 volatile gCurrentMsTime = 0;//1ms时间
static u8 gblinkInfo[17];
static bool gOverRange=FALSE;
static bool gLeakCurrent = FALSE;
static bool gOverCurrent = FALSE;
enum SHOW_TYPE
{
	SHOW_NORMAL = 0,
	SHOW_BLINK = 1 ,
	SHOW_OK = 2 ,
	SHOW_LEFT = 3 ,
	SHOW_RIGHT = 4 ,
	SHOW_ALARM = 5,
};
struct ST_ShowInfo
{
	u8  (*pInfo)[17];
	u8  blink_enable;// 0 disable blink 1 enable blink
	u8  blink_state;//0 non_blink 1 blink
	u8  blink_pos;
	u8  blink_num; 
	u8  blink_line;
	u8  blink_preline;
	u8  page_no;
	u8  show_page_enable; // 0 showpage 1 showline
	
};

static struct ST_ShowInfo gShowInfo={0,0,0,0,0,0,0,1,0};
static u8 gDir=0;
static void ShowInfo(enum SHOW_TYPE type);
static void ShowOK(void);
static void ShowRight(void);
static void ShowLeft(void);
static void SetDevTimerState(void);
static void ShowAlarm(void);
static void CancelDevTimerState(void);
static void ProcTime(void);
/** @addtogroup Template_Project
  * @{
  */

/* Private typedef -----------------------------------------------------------*/
/* Private define ------------------------------------------------------------*/
/* Private macro -------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/
/* Private function prototypes -----------------------------------------------*/
/* Private functions ---------------------------------------------------------*/

/******************************************************************************/
/*            Cortex-M3 Processor Exceptions Handlers                         */
/******************************************************************************/

/**
  * @brief  This function handles NMI exception.
  * @param  None
  * @retval : None
  */
void NMI_Handler(void)
{
}

/**
  * @brief  This function handles Hard Fault exception.
  * @param  None
  * @retval : None
  */
void HardFault_Handler(void)
{
  /* Go to infinite loop when Hard Fault exception occurs */
  while (1)
  {
  }
}

/**
  * @brief  This function handles Memory Manage exception.
  * @param  None
  * @retval : None
  */
void MemManage_Handler(void)
{
  /* Go to infinite loop when Memory Manage exception occurs */
  while (1)
  {
  }
}

/**
  * @brief  This function handles Bus Fault exception.
  * @param  None
  * @retval : None
  */
void BusFault_Handler(void)
{
  /* Go to infinite loop when Bus Fault exception occurs */
  while (1)
  {
  }
}

/**
  * @brief  This function handles Usage Fault exception.
  * @param  None
  * @retval : None
  */
void UsageFault_Handler(void)
{
  /* Go to infinite loop when Usage Fault exception occurs */
  while (1)
  {
  }
}

/**
  * @brief  This function handles SVCall exception.
  * @param  None
  * @retval : None
  */
void SVC_Handler(void)
{
}

/**
  * @brief  This function handles Debug Monitor exception.
  * @param  None
  * @retval : None
  */
void DebugMon_Handler(void)
{
}

/**
  * @brief  This function handles PendSVC exception.
  * @param  None
  * @retval : None
  */
void PendSV_Handler(void)
{
}

/**
  * @brief  This function handles SysTick Handler.
  * @param  None
  * @retval : None
  */
void SysTick_Handler(void) //1ms检查
{
	  gCurrentMsTime++;
	  DealMsSendMsg();

	  if(gCurrentMsTime == 50)//50ms
	  {
       	    DealMsRecvMsg();
			gCurrentMsTime = 0;
			if(CONNECT_REGISTER == dev.connectState)
			{
				gCheckHeartTime++;
			}
	  }
	  
	  if(gCheckHeartTime == 1200)//50*20*60
	  {
			 exFlagCheckHeart = 1;
			 gCheckHeartTime = 0;
	  }

}
/////////////////////////////////////////////////////////////
void USART1_IRQHandler(void)
{
	Uart1_getString();
}
void USART3_IRQHandler(void)
{
	u8 tmp = 0;
  if( USART_GetITStatus(USART3, USART_IT_RXNE) != RESET )
  {
    tmp= USART_ReceiveData(USART3);
    if(RxCounter2 < RxBufferSize)
		{
			RxBuffer2[RxCounter2++] = tmp;
		}
    else 
		{
			RxCounter2 = 0;
		}
  }
}


void PVD_IRQHandler(void)
{
	
}
void TIM2_IRQHandler(void)
{
	static int half_second = 0;
	u8 info_open[] = "开";
	u8 info_close[] = "关";
	 if(TIM_GetFlagStatus(TIM2,TIM_FLAG_Update)!=RESET)
	 {
		  if(gShowInfo.blink_enable)
			{
				 if(0 == gShowInfo.blink_state)
				 {
					   ShowInfo(SHOW_NORMAL);
					   gShowInfo.blink_state =1;
				 }
				 else 
				 {
					   ShowInfo(SHOW_BLINK);
					   gShowInfo.blink_state =0;
				 }
			}
			else if(dev.id>=0)
			{
				  gShowInfo.blink_line = 2;
				  gShowInfo.blink_pos=4;
				  gShowInfo.pInfo = page_1;
				  gShowInfo.page_no = 1;
				  gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos]= dev.time[0]/10+'0';
				  gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos+1]= dev.time[0]%10+'0'; 
				  gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos+3]= dev.time[1]/10+'0';
				  gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos+4]= dev.time[1]%10+'0';
				  gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos+6]= dev.time[2]/10+'0';
				  gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos+7]= dev.time[2]%10+'0';
				  ShowInfo(SHOW_NORMAL);
				  gShowInfo.blink_line = 4;
				  gShowInfo.blink_pos=2;
				  if(DEV_DISABLE_OPEN == dev.devState || DEV_UNCONENCT == dev.devState)
					{
						 gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos] = info_close[0];
						 gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos+1] = info_close[1];
					}
					else if(DEV_ENABLE_OPEN == dev.devState)
					{
						 gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos] = info_open[0];
						 gShowInfo.pInfo[gShowInfo.blink_line-1][gShowInfo.blink_pos+1] = info_open[1];
					}
					gShowInfo.pInfo[gShowInfo.blink_line-1][14] = dev.id/10+'0';
					gShowInfo.pInfo[gShowInfo.blink_line-1][15] = dev.id%10+'0';
				    ShowInfo(SHOW_NORMAL); 
			}
			if(isGetHelp())
			{
				ShowOK();
				resetGetHelp();
			}
			if(gOverCurrent==TRUE||gLeakCurrent == TRUE||gOverRange == TRUE)
			{
				ShowInfo(SHOW_ALARM);
			}
			
			if(0 == half_second)
			{
			  half_second = 1;
			}
			else 
			{
				half_second = 0;
				ProcTime();
			}
		   TIM_ClearFlag(TIM2,TIM_FLAG_Update);
	     
	 }
}
void EXTI9_5_IRQHandler(void)
{
	if(EXTI_GetITStatus(EXTI_Line7)!=RESET)
	{
		ShowInfo(SHOW_OK);
		gDir = 0;
		EXTI_ClearITPendingBit(EXTI_Line7);

	}
	else if(EXTI_GetITStatus(EXTI_Line8)!=RESET)
	{	
		if(gDir == 0)
		{
			gDir = 1;
		}					
		else if(2 == gDir)
		{
			ShowInfo(SHOW_RIGHT);
			gDir = 0;//左到右
		}
		EXTI_ClearITPendingBit(EXTI_Line8);
	}
	else if(EXTI_GetITStatus(EXTI_Line9)!=RESET)
	{   
		if(gDir == 0)
		{
			gDir = 2;
		}					
		else if(1 == gDir)
		{
			ShowInfo(SHOW_LEFT);
			gDir = 0;//左到右
		}
		EXTI_ClearITPendingBit(EXTI_Line9);
	}

}
void EXTI15_10_IRQHandler(void)
{
	if(EXTI_GetITStatus(EXTI_Line13)!=RESET)
	{	    
		dev.alarm[2]++;
		gOverRange = TRUE;
		EXTI_ClearITPendingBit(EXTI_Line13);
	}
	else if(EXTI_GetITStatus(EXTI_Line14)!=RESET)
	{	
		dev.alarm[1]++;
		gLeakCurrent = TRUE;
		EXTI_ClearITPendingBit(EXTI_Line14);

	}
	else if(EXTI_GetITStatus(EXTI_Line15)!=RESET)
	{   
		dev.alarm[0]++;
		gOverCurrent = TRUE;
		EXTI_ClearITPendingBit(EXTI_Line15);
	}
	  
}




void ShowInfo(enum SHOW_TYPE type)
{
	int i=0;
	switch(type)
	{
		case SHOW_NORMAL:    
			break;
		case SHOW_BLINK:	
			memcpy(gblinkInfo,gShowInfo.pInfo[gShowInfo.blink_line-1],16);
			for(i=gShowInfo.blink_pos;i<gShowInfo.blink_pos+gShowInfo.blink_num;i++)
			{
				gblinkInfo[i] =' ';
			}
			break;
		case SHOW_OK:
			ShowOK();
		break;
		case SHOW_RIGHT:
			ShowRight();
		break;
		case SHOW_LEFT:
			ShowLeft();
		break;
		case SHOW_ALARM:
			ShowAlarm();
		break;
	}
	if(gShowInfo.show_page_enable)
	{
		Display_Page(gShowInfo.page_no);
		gShowInfo.show_page_enable=0;
	}
	else
	{
		if(SHOW_BLINK == type)
		{
			if((0<gShowInfo.blink_line&&gShowInfo.blink_line<5))
			{
				Write_LCD_Line(gShowInfo.blink_line,gblinkInfo);
			}
		}
		else if(SHOW_NORMAL == type)
		{
			if((0<gShowInfo.blink_line&&gShowInfo.blink_line<5)&&(0!=gShowInfo.pInfo[gShowInfo.blink_line-1]))
			{
				Write_LCD_Line(gShowInfo.blink_line,gShowInfo.pInfo[gShowInfo.blink_line-1]);
		    }
	     }
		else
		{
			if((0<gShowInfo.blink_preline&&gShowInfo.blink_preline<5)&&(0!=gShowInfo.pInfo[gShowInfo.blink_preline-1]))
			{
				Write_LCD_Line(gShowInfo.blink_preline,gShowInfo.pInfo[gShowInfo.blink_preline-1]);
			}
		}
	}
}
void ShowAlarm(void)
{
	if(gOverCurrent == TRUE)
	{
		gShowInfo.page_no=7;
		gShowInfo.show_page_enable = 1;
		gShowInfo.blink_enable = 1;
		gShowInfo.blink_line = 0;
		gShowInfo.blink_preline = 0;
		gShowInfo.blink_state = 0;
		gShowInfo.blink_pos = 0;
		gShowInfo.blink_num = 0;
		gShowInfo.pInfo = page_7;
		gOverCurrent  = FALSE;	      
	}
    
	if(gOverRange == TRUE)
	{
		gShowInfo.page_no=8;
		gShowInfo.show_page_enable = 1;
		gShowInfo.blink_enable = 1;
		gShowInfo.blink_line = 0;
		gShowInfo.blink_preline = 0;
		gShowInfo.blink_state = 0;
		gShowInfo.blink_pos = 0;
		gShowInfo.blink_num = 0;
		gShowInfo.pInfo = page_8;
		gOverRange  = FALSE;

	}
	if(gLeakCurrent == TRUE)
	{
		gShowInfo.page_no=9;
		gShowInfo.show_page_enable = 1;
		gShowInfo.blink_enable = 1;
		gShowInfo.blink_line = 0;
		gShowInfo.blink_preline = 0;
		gShowInfo.blink_state = 0;
		gShowInfo.blink_pos = 0;
		gShowInfo.blink_num = 0;
		gShowInfo.pInfo = page_9;
		gLeakCurrent  = FALSE;

	}
}
void ShowOK(void)
{
	u8 tmp;
	u8 info[] ="    密码错误    ";
	u8 alarm = 0;
	u8 buff[] = "&CS#8002#000001#0001#04$";

	switch(gShowInfo.page_no)
	{
		case 1:
			gShowInfo.page_no=2;
			gShowInfo.show_page_enable = 1;
			gShowInfo.blink_enable = 1;
			gShowInfo.blink_line = 1;
			gShowInfo.blink_preline = 1;
			gShowInfo.blink_state = 0;
			gShowInfo.blink_pos = 4;
			gShowInfo.blink_num = 8;
			gShowInfo.pInfo = page_2;
		break;
		case 2:
			if(4==gShowInfo.blink_line)
			{
				gShowInfo.page_no=1;
				gShowInfo.show_page_enable = 1;
				gShowInfo.blink_enable = 0;
				gShowInfo.blink_line = 0;
				gShowInfo.blink_preline = 0;
				gShowInfo.blink_state = 0;
				gShowInfo.blink_pos = 0;
				gShowInfo.blink_num = 0;
				gShowInfo.pInfo = page_1;
			}
			else if(1 == gShowInfo.blink_line)
			{
				if(TRUE == dev.enable_opentime_flag)
				{							
					page_3[0][9] = dev.openTime[0]/10+'0';
					page_3[0][10] = dev.openTime[0]%10+'0';

					page_3[0][12] = dev.openTime[1]/10+'0';
					page_3[0][13] = dev.openTime[1]%10+'0';
				}
				else
				{
					page_3[0][9] = '0';
					page_3[0][10] = '0';

					page_3[0][12] = '0';
					page_3[0][13] = '0';
				}

				if(TRUE == dev.enable_closetime_flag)
				{
					page_3[1][9] = dev.closeTime[0]/10+'0';
					page_3[1][10] = dev.closeTime[0]%10+'0';

					page_3[1][12] = dev.closeTime[1]/10+'0';
					page_3[1][13] = dev.closeTime[1]%10+'0';
				}
				else
				{
					page_3[1][9] = '0';
					page_3[1][10] = '0';

					page_3[1][12] = '0';
					page_3[1][13] = '0';
				}
				alarm =  dev.alarm[0]+dev.alarm[1]+dev.alarm[2];
				if(alarm>=100)
				{
					alarm = 0;
					dev.alarm[0] = 0;
					dev.alarm[1] = 0;
					dev.alarm[2] = 0;
				}
				page_3[2][9] = alarm/10+'0';
				page_3[2][10] = alarm%10+'0';
				gShowInfo.page_no=3;
				gShowInfo.show_page_enable = 1;
				gShowInfo.blink_enable = 1;
				gShowInfo.blink_line = 4;
				gShowInfo.blink_preline = 4;
				gShowInfo.blink_state = 0;
				gShowInfo.blink_pos = 0;
				gShowInfo.blink_num = 4;
				gShowInfo.pInfo = page_3;
			}
			else if(2 == gShowInfo.blink_line)
			{
				gShowInfo.page_no=4;
				gShowInfo.show_page_enable = 1;
				gShowInfo.blink_enable = 1;
				gShowInfo.blink_line = 2;
				gShowInfo.blink_preline = 2;
				gShowInfo.blink_state = 0;
				gShowInfo.blink_pos = 6;
				gShowInfo.blink_num = 1;
				gShowInfo.pInfo = page_4;
				memcpy(page_4[gShowInfo.blink_line-1],pwd,16);
			}
			else if(3 == gShowInfo.blink_line) //呼叫帮助
			{
				gShowInfo.page_no=6;
				gShowInfo.show_page_enable = 1;
				gShowInfo.blink_enable = 1;
				gShowInfo.blink_line = 0;
				gShowInfo.blink_preline = 0;
				gShowInfo.blink_state = 0;
				gShowInfo.blink_pos = 0;
				gShowInfo.blink_num = 0;
				gShowInfo.pInfo = page_6;
				buff[18] = dev.id/10+'0';
				buff[19] = dev.id%10+'0';
				pushSendMsg(buff,getStrLen(buff));
			}
			break;
		case 3:
			gShowInfo.page_no=2;
			gShowInfo.show_page_enable = 1;
			gShowInfo.blink_enable = 1;
			gShowInfo.blink_line = 1;
			gShowInfo.blink_preline = 1;
			gShowInfo.blink_state = 0;
			gShowInfo.blink_pos = 4;
			gShowInfo.blink_num = 8;
			gShowInfo.pInfo = page_2;
		break;
		case 4:
			if(2 ==  gShowInfo.blink_line)
			{
				tmp = page_4[gShowInfo.blink_line-1][gShowInfo.blink_pos];
				if('*' == tmp)
				{
					page_4[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
				}							
				else 
				{
					if(gShowInfo.blink_pos<9)
					{
						gShowInfo.blink_pos++;
					}
					else
					{
						gShowInfo.blink_line = 4;
						gShowInfo.blink_pos = 0;
						gShowInfo.blink_num = 4;
					}
				}
			}
			else if(4 == gShowInfo.blink_line)
			{
				if(0== gShowInfo.blink_pos)
				{
					gShowInfo.page_no=2;
					gShowInfo.show_page_enable = 1;
					gShowInfo.blink_enable = 1;
					gShowInfo.blink_line = 1;
					gShowInfo.blink_preline = 1;
					gShowInfo.blink_state = 0;
					gShowInfo.blink_pos = 4;
					gShowInfo.blink_num = 8;
					gShowInfo.pInfo = page_2;
				}
				else if(12 == gShowInfo.blink_pos)
				{
					if(page_4[1][6]==devPwd[0]&& page_4[1][7] == devPwd[1]&&page_4[1][8] == devPwd[2]&& page_4[1][9] == devPwd[3])
					{
						gShowInfo.page_no=5;
						gShowInfo.show_page_enable = 1;
						gShowInfo.blink_enable = 1;
						gShowInfo.blink_line = 1;
						gShowInfo.blink_preline = 1;
						gShowInfo.blink_state = 0;
						gShowInfo.blink_pos = 0;
						gShowInfo.blink_num = 8;
						gShowInfo.pInfo = page_5;
					}
					else
					{
						memcpy(page_4[2],info,16);
						gShowInfo.show_page_enable = 1;
					}

				}
			}

		break;
		case 5:
			if(1 ==  gShowInfo.blink_line)
			{
				if(0 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 10;
					gShowInfo.blink_num = 2;
				}
				else if(10 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 0;
					gShowInfo.blink_num = 8;
				}
			}				
			else if(2 == gShowInfo.blink_line)
			{
				if(0 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 10;
					gShowInfo.blink_num = 1;
				}
				else if(10 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 11;
					gShowInfo.blink_num = 1;
				}
				else if(11 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 13;
					gShowInfo.blink_num = 1;
				}
				else if(13 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 14;
					gShowInfo.blink_num = 1;
				}
				else if(14 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 0;
					gShowInfo.blink_num = 8;
				}
			}
			else if(3 == gShowInfo.blink_line)
			{
				if(0 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 10;
					gShowInfo.blink_num = 1;
				}
				else if(10 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 11;
					gShowInfo.blink_num = 1;
				}
				else if(11 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 13;
					gShowInfo.blink_num = 1;
				}
				else if(13 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 14;
					gShowInfo.blink_num = 1;
				}
				else if(14 == gShowInfo.blink_pos)
				{
					gShowInfo.blink_pos = 0;
					gShowInfo.blink_num = 8;
				}
			}
			else if(4 == gShowInfo.blink_line)
			{

				if(0== gShowInfo.blink_pos)
				{
					gShowInfo.page_no=4;
					gShowInfo.show_page_enable = 1;
					gShowInfo.blink_enable = 1;
					gShowInfo.blink_line = 2;
					gShowInfo.blink_preline = 2;
					gShowInfo.blink_state = 0;
					gShowInfo.blink_pos = 6;
					gShowInfo.blink_num = 1;
					gShowInfo.pInfo = page_4;
					memcpy(page_4[gShowInfo.blink_line-1],pwd,16);
					CancelDevTimerState();
				}
				else if(12 == gShowInfo.blink_pos)
				{
					gShowInfo.page_no=2;
					gShowInfo.show_page_enable = 1;
					gShowInfo.blink_enable = 1;
					gShowInfo.blink_line = 1;
					gShowInfo.blink_preline = 1;
					gShowInfo.blink_state = 0;
					gShowInfo.blink_pos = 4;
					gShowInfo.blink_num = 8;
					gShowInfo.pInfo = page_2;
					SetDevTimerState();
				}
			}

		break;
		case 6:
		case 7:
		case 8:
		case 9:
			gShowInfo.page_no=1;
			gShowInfo.show_page_enable = 1;
			gShowInfo.blink_enable = 0;
			gShowInfo.blink_line = 0;
			gShowInfo.blink_preline = 0;
			gShowInfo.blink_state = 0;
			gShowInfo.blink_pos = 0;
			gShowInfo.blink_num = 0;
			gShowInfo.pInfo = page_1;
		break;

	}

}
void ShowRight(void)
{
	 u8 tmp;
	 u8 info_close[]="关";
	 u8 info_open[]="开";
	 switch(gShowInfo.page_no)
		{
			case 1:
			break;
		  case 2:
				  gShowInfo.blink_preline = gShowInfo.blink_line;
				  gShowInfo.blink_line++;
				  if(gShowInfo.blink_line>4)
					{
						 gShowInfo.blink_line = 1;
					}
					if(gShowInfo.blink_line != 4)
					{
						 gShowInfo.blink_pos = 4;
			       gShowInfo.blink_num = 8;
					}
					else
					{
						  gShowInfo.blink_pos = 6;
			        gShowInfo.blink_num = 4;
					}
			break;
			case 3:
			break;
			case 4:
				gShowInfo.blink_preline = gShowInfo.blink_line;
				if(2 ==  gShowInfo.blink_line)
				{
					 tmp = page_4[gShowInfo.blink_line-1][gShowInfo.blink_pos];
					 if(tmp>='9')
					 {
					    page_4[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
					 }
					 else if(tmp>='0'&&tmp<'9')
					 {
						  page_4[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
					 }
					 else if(tmp == '*')
					 {
						 if(gShowInfo.blink_pos<9)
							{
							   gShowInfo.blink_pos++;
							}
							else
							{
								 gShowInfo.blink_line = 4;
								 gShowInfo.blink_pos = 0;
								 gShowInfo.blink_num = 4;
							}
					 }
				}
				else if(4 == gShowInfo.blink_line)
				{
					if(0 == gShowInfo.blink_pos)
					{
					   gShowInfo.blink_pos = 12;
					   gShowInfo.blink_num = 4;
					}
					else if(12 == gShowInfo.blink_pos)
					{
						gShowInfo.blink_pos = 6;
					  gShowInfo.blink_num = 1;
						gShowInfo.blink_line = 2;
					}
				}
			break;
			case 5:
				gShowInfo.blink_preline = gShowInfo.blink_line;
				if(1 ==  gShowInfo.blink_line)
					{
						if(0==gShowInfo.blink_pos)
						{
						   gShowInfo.blink_line = 2;
						   gShowInfo.blink_pos = 0;
			               gShowInfo.blink_num = 8;
						}
						else if(10 == gShowInfo.blink_pos)
						{
							if(page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] == info_close[0])
							{
							    page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]=info_open[0];
							    page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos+1] = info_open[1];
							
							}
							else
							{
								page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]=info_close[0];
							    page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos+1] = info_close[1];
							}
						}
					}
			    else if(2 == gShowInfo.blink_line)
					{
						  if(0==gShowInfo.blink_pos)
							{
						      gShowInfo.blink_line = 3;
						      gShowInfo.blink_pos = 0;
			            gShowInfo.blink_num = 8;
							}
							else if(10 == gShowInfo.blink_pos)
							{
								 if('2'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
								 }
							}
							else if(11 == gShowInfo.blink_pos)
							{
								 if('2' == page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos-1])
								 {
									    if('3'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
											 }
											 else 
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
											 }
								 }
								 else
								 {
											 if('9'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
											 }
											 else 
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
											 }
							   }
							}
							else if(13 == gShowInfo.blink_pos)
							{
								 if('5'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
								 }
							}
							else if(14 == gShowInfo.blink_pos)
							{
								if('9'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
								 }
								
							}
					}
					else if(3 == gShowInfo.blink_line)
					{
						if(0==gShowInfo.blink_pos)
							{
						      gShowInfo.blink_line = 4;
						      gShowInfo.blink_pos = 0;
			            gShowInfo.blink_num = 4;
							}
							else if(10 == gShowInfo.blink_pos)
							{
								 if('2'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
								 }
							}
							else if(11 == gShowInfo.blink_pos)
							{
								 if('2' == page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos-1])
								 {
									    if('3'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
											 }
											 else 
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
											 }
								 }
								 else
								 {
											 if('9'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
											 }
											 else 
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
											 }
							   }
							}
							else if(13 == gShowInfo.blink_pos)
							{
								 if('5'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
								 }
							}
							else if(14 == gShowInfo.blink_pos)
							{
								if('9'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '0';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]++;
								 }
								
							}
					}
			    else if(4 == gShowInfo.blink_line)
					{
						   if(0 == gShowInfo.blink_pos)
							 {
								    gShowInfo.blink_line = 4;
						        gShowInfo.blink_pos = 12;
			              gShowInfo.blink_num = 4; 
							 }
							 else if(12 == gShowInfo.blink_pos)
							 {
								   gShowInfo.blink_line = 1;
								   gShowInfo.blink_pos = 0;
			             gShowInfo.blink_num = 8;
							 }
					}
			
			break;
		}
}
void ShowLeft(void)
{
	u8 tmp;
	u8 info_close[]="关";
	u8 info_open[]="开";
	 switch(gShowInfo.page_no)
		{
			case 1:
			break;
		  case 2:
				gShowInfo.blink_preline = gShowInfo.blink_line;
				  gShowInfo.blink_line--;
				  if(gShowInfo.blink_line<1)
					{
						 gShowInfo.blink_line = 4;
					}
					if(gShowInfo.blink_line != 4)
					{
						 gShowInfo.blink_pos = 4;
			       gShowInfo.blink_num = 8;
					}
					else
					{
						  gShowInfo.blink_pos = 6;
			        gShowInfo.blink_num = 4;
					}
			    
			break;
			case 3:
			break;
			case 4:
				gShowInfo.blink_preline = gShowInfo.blink_line;
				if(2 ==  gShowInfo.blink_line)
				{
					 tmp = page_4[gShowInfo.blink_line-1][gShowInfo.blink_pos];
					 if(tmp<='0'&&tmp!='*')
					 {
					    page_4[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '9';
					 }
					 else if('0'<tmp&&tmp<='9')
					 {
						  page_4[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
					 }
					 else if(tmp == '*')
					 {
						 if(gShowInfo.blink_pos>6)
							{
							   gShowInfo.blink_pos--;
							}
							else
							{
								 gShowInfo.blink_line = 4;
								 gShowInfo.blink_pos = 12;
								 gShowInfo.blink_num = 4;
							}
					 }
				}
				else if(4 == gShowInfo.blink_line)
				{
					if(0 == gShowInfo.blink_pos)
					{
					  gShowInfo.blink_pos = 9;
					  gShowInfo.blink_num = 1;
						gShowInfo.blink_line = 2;
					}
					else if(12 == gShowInfo.blink_pos)
					{
						gShowInfo.blink_pos = 0;
					  gShowInfo.blink_num = 4;
						gShowInfo.blink_line = 4;
					}
				}
			
			
			break;
			case 5:
				gShowInfo.blink_preline = gShowInfo.blink_line;
			  if(1 ==  gShowInfo.blink_line)
					{
						if(0==gShowInfo.blink_pos)
						{
						   gShowInfo.blink_line = 4;
						   gShowInfo.blink_pos = 12;
			         gShowInfo.blink_num = 4;
						}
						else if(10 == gShowInfo.blink_pos)
						{
							 if(page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] == info_close[0])
							{
							    page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]=info_open[0];
							    page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos+1] = info_open[1];
							
							}
							else
							{
								  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]=info_close[0];
							    page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos+1] = info_close[1];
							}
						}
						   
					}
			    else if(2 == gShowInfo.blink_line)
					{
						if(0== gShowInfo.blink_pos)
						{
						   gShowInfo.blink_line = 1;
						   gShowInfo.blink_pos = 0;
			         gShowInfo.blink_num = 8;
						}
						else if(10 == gShowInfo.blink_pos)
						{
								 if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '2';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
								 }
						}
						else if(11 == gShowInfo.blink_pos)
						{
								 if('2' == page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos-1])
								 {
									    if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '3';
											 }
											 else 
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
											 }
								 }
								 else
								 {
											 if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '9';
											 }
											 else 
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
											 }
							   }
						}
						else if(13 == gShowInfo.blink_pos)
						{
								 if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '5';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
								 }
						}
						else if(14 == gShowInfo.blink_pos)
						{
								if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '9';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
								 }
								
						}
					}
					else if(3 == gShowInfo.blink_line)
					{
						if(0== gShowInfo.blink_pos)
						{
						   gShowInfo.blink_line = 2;
						   gShowInfo.blink_pos = 0;
			         gShowInfo.blink_num = 8;
						}
						else if(10 == gShowInfo.blink_pos)
						{
								 if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 { 
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '2';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
								 }
						}
						else if(11 == gShowInfo.blink_pos)
						{
								 if('2' == page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos-1])
								 {
									    if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '3';
											 }
											 else 
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
											 }
								 }
								 else
								 {
											 if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '9';
											 }
											 else 
											 {
													page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
											 }
							   }
						}
						else if(13 == gShowInfo.blink_pos)
						{
								 if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '5';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
								 }
						}
						else if(14 == gShowInfo.blink_pos)
						{
								if('0'== page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos])
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos] = '9';
								 }
								 else 
								 {
									  page_5[gShowInfo.blink_line-1][gShowInfo.blink_pos]--;
								 }
								
						}
					}
			    else if(4 == gShowInfo.blink_line)
					{
						   if(0 == gShowInfo.blink_pos)
							 {
								    gShowInfo.blink_line = 3;
						        gShowInfo.blink_pos = 0;
			              gShowInfo.blink_num = 8; 
							 }
							 else if(12 == gShowInfo.blink_pos)
							 {
								   gShowInfo.blink_line = 4;
								   gShowInfo.blink_pos = 0;
			             gShowInfo.blink_num = 4;
							 }
					}
			break;
		}
}
void SetDevTimerState(void)
{
	   u8 info[] ="关";
	   if(page_5[0][10] == info[0]) //关
		 {
			 dev.devState = DEV_DISABLE_OPEN;
			 LED_OFF();
			 output_OFF(12);
		 }
		 else 
		 {
			 dev.devState = DEV_ENABLE_OPEN;
			 LED_OFF();
			 output_ON(12);
		 }
		 dev.openTime[0] = (page_5[1][10]-'0')*10+(page_5[1][11]-'0');
		 dev.openTime[1] =  (page_5[1][13]-'0')*10+(page_5[1][14]-'0');
		 dev.openTime[2] = 0;
		 dev.closeTime[0] = (page_5[2][10]-'0')*10+(page_5[2][11]-'0');
		 dev.closeTime[1] =  (page_5[2][13]-'0')*10+(page_5[2][14]-'0');
		 dev.closeTime[2] = 0;
		 dev.enable_opentime_flag = TRUE;
		 dev.enable_closetime_flag = TRUE;
	
}
void CancelDevTimerState(void)
{
	   dev.openTime[0] = 0;
		 dev.openTime[1] =  0;
		 dev.openTime[2] = 0;
	   dev.closeTime[0] = 0;
		 dev.closeTime[1] =  0;
		 dev.closeTime[2] = 0;
		 dev.enable_closetime_flag = FALSE;
	   dev.enable_opentime_flag = FALSE;
	   
}
void ProcTime(void)
{
	    u8 i = 0;
	    dev.time[2]++;
			if(60 == dev.time[2])
			{
				dev.time[1]++;
				dev.time[2] = 0;
			}
			if(60 == dev.time[1])
			{
				dev.time[0]++;
				dev.time[1] = 0;
			}
			if(24 == dev.time[0])
			{
				dev.time[0] = 0;
			}
			if(dev.enable_opentime_flag)
			{
				if((dev.time[0] == dev.openTime[0]) && (dev.time[1] == dev.openTime[1]) \
					 &&(dev.time[2] == dev.openTime[2]))
				{
			        dev.devState = DEV_ENABLE_OPEN;
			        LED_ON();
					output_ON(12);
					dev.enable_opentime_flag = FALSE;
					dev.is_opentime_flag = TRUE;
				}
			}
			else if(dev.enable_closetime_flag)
			{
				if((dev.time[0] == dev.closeTime[0]) && (dev.time[1] == dev.closeTime[1]) \
					 &&(dev.time[2] == dev.closeTime[2]))
				{
			        dev.devState = DEV_DISABLE_OPEN;
			        LED_OFF();
					output_OFF(12);
					dev.enable_closetime_flag = FALSE;
					dev.is_closetime_flag = TRUE;
				}
			}
			
			
}
/******************* (C) COPYRIGHT 2009 STMicroelectronics *****END OF FILE****/
