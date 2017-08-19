#include "stm32f10x.h"
#include "main.h"
#include "user_init.h"

void TIMER2_Configuration(void) 
{
	 TIM_TimeBaseInitTypeDef TIM_TimeBaseStructure;
	 RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM2,ENABLE);
	 TIM_DeInit(TIM2);
	 TIM_TimeBaseStructure.TIM_Period=17999;
	 TIM_TimeBaseStructure.TIM_Prescaler=1999;
	 TIM_TimeBaseStructure.TIM_ClockDivision=TIM_CKD_DIV1;
	 TIM_TimeBaseStructure.TIM_CounterMode=TIM_CounterMode_Up;
	 TIM_TimeBaseInit(TIM2,&TIM_TimeBaseStructure);
	 TIM_ClearFlag(TIM2,TIM_FLAG_Update);
	 TIM_ITConfig(TIM2,TIM_IT_Update,ENABLE);
	 TIM_Cmd(TIM2,ENABLE);
	
}


void EXTI_Configuration(void)
{
	EXTI_InitTypeDef EXTI_InitStructure;

	/* Configure EXTI Line16(PVD Output) to generate an interrupt on rising and
	 falling edges */
	EXTI_ClearITPendingBit(EXTI_Line16); 
	EXTI_InitStructure.EXTI_Line = EXTI_Line16;// PVD??????16? 
	EXTI_InitStructure.EXTI_Mode = EXTI_Mode_Interrupt;//?????? 
	EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Rising_Falling;//????????????????????
	EXTI_InitStructure.EXTI_LineCmd = ENABLE;// ?????
	EXTI_Init(&EXTI_InitStructure);// ??


	EXTI_ClearITPendingBit(EXTI_Line7);
	GPIO_EXTILineConfig(GPIO_PortSourceGPIOB,GPIO_PinSource7);
	EXTI_InitStructure.EXTI_Line= EXTI_Line7 ; //??????2 3 5
	EXTI_InitStructure.EXTI_Mode= EXTI_Mode_Interrupt; //???????,?????
	EXTI_InitStructure.EXTI_Trigger= EXTI_Trigger_Falling; //???????????????
	EXTI_InitStructure.EXTI_LineCmd=ENABLE;                                          //??????
	EXTI_Init(&EXTI_InitStructure);

	EXTI_ClearITPendingBit(EXTI_Line8);
	EXTI_ClearITPendingBit(EXTI_Line9);
	GPIO_EXTILineConfig(GPIO_PortSourceGPIOB,GPIO_PinSource8);
	GPIO_EXTILineConfig(GPIO_PortSourceGPIOB,GPIO_PinSource9);
	EXTI_InitStructure.EXTI_Line= EXTI_Line8|EXTI_Line9 ; //??????2 3 5
	EXTI_InitStructure.EXTI_Mode= EXTI_Mode_Interrupt; //???????,?????
	EXTI_InitStructure.EXTI_Trigger= EXTI_Trigger_Rising_Falling; //???????????????
	EXTI_InitStructure.EXTI_LineCmd=ENABLE;                                          //??????
	EXTI_Init(&EXTI_InitStructure);

	EXTI_ClearITPendingBit(EXTI_Line13);
	EXTI_ClearITPendingBit(EXTI_Line14);
	EXTI_ClearITPendingBit(EXTI_Line15);
	GPIO_EXTILineConfig(GPIO_PortSourceGPIOD,GPIO_PinSource13);
	GPIO_EXTILineConfig(GPIO_PortSourceGPIOD,GPIO_PinSource14);
	GPIO_EXTILineConfig(GPIO_PortSourceGPIOD,GPIO_PinSource15);
	EXTI_InitStructure.EXTI_Line= EXTI_Line13 | EXTI_Line14 |EXTI_Line15 ; //??????2 3 5
	EXTI_InitStructure.EXTI_Mode= EXTI_Mode_Interrupt; //???????,?????
	EXTI_InitStructure.EXTI_Trigger= EXTI_Trigger_Falling; //???????????????
	EXTI_InitStructure.EXTI_LineCmd=ENABLE;                                          //??????
	EXTI_Init(&EXTI_InitStructure);
	
	
}

void NVIC_Configuration(void)
{
	NVIC_InitTypeDef NVIC_InitStructure;
/*       
//默认情况下向量表在FLASH中，基址是0x0
#ifdef  VECT_TAB_RAM
  // Set the Vector Table base location at 0x20000000
  NVIC_SetVectorTable(NVIC_VectTab_RAM, 0x0);
#else  // VECT_TAB_FLASH  
  // Set the Vector Table base location at 0x08000000
  NVIC_SetVectorTable(NVIC_VectTab_FLASH, 0x0);
#endif
*/	 
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_0);
	
	// Enable the USART1 Interrupt
	NVIC_InitStructure.NVIC_IRQChannel = USART1_IRQn;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure);
	
	NVIC_InitStructure.NVIC_IRQChannel = USART3_IRQn;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 2;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure);
	
  
  /* Configure one bit for preemption priority */
  NVIC_PriorityGroupConfig(NVIC_PriorityGroup_1);//??????? 
  
  /* Enable the PVD Interrupt */ //??PVD??
  NVIC_InitStructure.NVIC_IRQChannel = TIM2_IRQn;
  NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 1;
  NVIC_InitStructure.NVIC_IRQChannelSubPriority = 1;
  NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
  NVIC_Init(&NVIC_InitStructure);
	
  NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);                          //??????2
	NVIC_InitStructure.NVIC_IRQChannel= EXTI9_5_IRQn;     //??????2
  NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority= 2; //???????????0
  NVIC_InitStructure.NVIC_IRQChannelSubPriority= 2;        //???????????0
  NVIC_InitStructure.NVIC_IRQChannelCmd=ENABLE;                                   //????
  NVIC_Init(&NVIC_InitStructure);
	
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_3);                          //??????2
	NVIC_InitStructure.NVIC_IRQChannel= EXTI15_10_IRQn;     //??????2
  NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority= 3; //???????????0
  NVIC_InitStructure.NVIC_IRQChannelSubPriority= 3;        //???????????0
  NVIC_InitStructure.NVIC_IRQChannelCmd=ENABLE;                                   //????
  NVIC_Init(&NVIC_InitStructure);
   
}


/*******************************************************************************
* 函数名	：EnablePeriphClk
* 功能		：使能用到的外设时钟
* 输入参数：无
* 返回值	：无
*******************************************************************************/
void EnablePeriphClk(void)
{ 
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA | RCC_APB2Periph_AFIO, ENABLE);
	//RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA, ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB | RCC_APB2Periph_AFIO, ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOC, ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOD | RCC_APB2Periph_AFIO, ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOE, ENABLE);
	
	//使能串口1时钟
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_USART1, ENABLE);
	//使能串口3时钟
	RCC_APB1PeriphClockCmd(RCC_APB1Periph_USART3, ENABLE);

}



















