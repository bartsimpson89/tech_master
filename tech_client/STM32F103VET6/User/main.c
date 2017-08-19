#include "stm32f10x.h"
#include "stm32f10x_it.h"
#include "main.h"
#include "user_led.h"
#include "user_uart.h"
#include "user_protocol.h"
#include "user_msgheap.h"
#include "user_global.h"
#include "user_flash.h"
#include "user_display.h"
#include "user_init.h"
//������
int main(void)
{
	/* Setup STM32 system (clock, PLL and Flash configuration) */
	u8 RecvMsg[64];
	SystemInit();
	
	NVIC_Configuration();        //����ʹ�õ������ж�
	//�ı�ָ���ܽŵ�ӳ��  GPIO_Remap_SWJ_JTAGDisable  JTAG-DP ʧ�� + SW-DPʹ��
	GPIO_PinRemapConfig(GPIO_Remap_SWJ_JTAGDisable , ENABLE);
	EnablePeriphClk();          //ʹ���õ�������ʱ��
    UART1_Configuration();		//����1��ʼ��
    //UART3_Configuration();		//����2��ʼ��       
	UART1_GPIO_Configuration();	//��ʼ������1IO��
	//UART3_GPIO_Configuration();	//��ʼ������2IO��  

	TIMER2_Configuration();
	EXTI_Configuration();
    GPIO_Configuration();
	initGlobalVariable();
	SysTick_Config(SystemFrequency/1000 * 4);	// 1ms��ʱ�ж�
	// ���� SysTick ��ʱ��Ϊ  1 ms �ж�
	if (SysTick_Config(SystemFrequency / 1000))
	{  
		while (1);
	}
	DelayMs(20);
	DISPLAY_init();
	Flash_init();
    while(1)
	{
		 if(dev.connectState != CONNECT_REGISTER)
		 {
				MsgSendCmd(AT_RESTART,200);
				MsgSendCmd(AT_SET_AP,100);
				MsgSendCmd(AT_CONNECT_ROUTER,500);
				MsgSendCmd(AT_MUL_CONNECT,100);
				MsgSendCmd(AT_CONNECT_IP_SERVER,500);	
				LED_OFF();
				RegisterId();
				dev.connectState = (TRUE == WaitRegisterResult()?CONNECT_REGISTER : CONNECT_FAILED);
				
		}  
		else
		{	
			if(popMsg(RecvMsg,64))
			{
					DealMsg(RecvMsg);	
			}
			
			CheckHeartState();
			if(dev.connectState == CONNECT_SETID)
			{
				 DelayMs(1000);
				 NVIC_SystemReset();//ϵͳ��λ
			}
		}
	}
}
// NVIC_SystemReset();//ϵͳ��λ
void CheckHeartState(void)
{
	if(exFlagCheckHeart == 1)//60s �������
	{
		if(HEART_INIT == dev.heartState )
		{

			dev.connectState = CONNECT_FAILED;
			dev.preDevState = dev.devState;
		}
		dev.heartState = HEART_INIT;
		exFlagCheckHeart = 0;
	}

}


void RegisterId(void)//ע����
{
	if(dev.id > 100||dev.id==0)
	{
			dev.id = 0;
	}
	sendSMsg(SMSG_REGISTER);
}

bool WaitRegisterResult(void)
{
	u8 RecvMsg[64];
    int i = 0;
	for(i=0;i<100;i++)
	{
		if(popMsg(RecvMsg,64))
	    {
			DealMsg(RecvMsg);	
			break;
	    }
		DelayMs(1);
    }
	return (i<100)?TRUE:FALSE;  
}



//��ʱ����
extern void delay(void)
{
	int i;
	for (i=0; i<0xfffff; i++)
	;
}
//��ʱ����
extern void DelayMs(u32 ms)
{
	int i,j;
	for(j=0;j<ms;j++)
	{
 	  for (i=0; i<0x124F8; i++)
		{
			;
		}
	}
}




#ifdef  USE_FULL_ASSERT

/**
  * @brief  Reports the name of the source file and the source line number
  *   where the assert_param error has occurred.
  * @param file: pointer to the source file name
  * @param line: assert_param error line source number
  * @retval : None
  */
void assert_failed(uint8_t* file, uint32_t line)
{ 
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */

  /* Infinite loop */
  while (1)
  {
  }
}
#endif

/**
  * @}
  */


/******************* (C) COPYRIGHT 2009 STMicroelectronics *****END OF FILE****/
