#include "stm32f10x.h"
#include "main.h"
#include "user_led.h"
#include "user_uart.h"
static u8 len_on = 0;
//led口初始化
void GPIO_Configuration(void)
{
  GPIO_InitTypeDef GPIO_InitStructure;

  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_3|GPIO_Pin_1|GPIO_Pin_2|GPIO_Pin_6;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
  GPIO_Init(GPIOA, &GPIO_InitStructure); 
	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_10|GPIO_Pin_11;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
  GPIO_Init(GPIOB, &GPIO_InitStructure); 
	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0|GPIO_Pin_1|GPIO_Pin_2|GPIO_Pin_3|GPIO_Pin_13;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
  GPIO_Init(GPIOC, &GPIO_InitStructure); 
	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_4|GPIO_Pin_5|GPIO_Pin_6|GPIO_Pin_11|GPIO_Pin_12|GPIO_Pin_13;//GPIO_Pin_14;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
  GPIO_Init(GPIOE, &GPIO_InitStructure);
  
  
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0|GPIO_Pin_11|GPIO_Pin_12|GPIO_Pin_8;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;
  GPIO_Init(GPIOA, &GPIO_InitStructure); 
	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_6|GPIO_Pin_7|GPIO_Pin_8|GPIO_Pin_9;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;
  GPIO_Init(GPIOC, &GPIO_InitStructure); 
	
	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_7|GPIO_Pin_8|GPIO_Pin_9; //按键输入
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;
  GPIO_Init(GPIOB, &GPIO_InitStructure); 
	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_13|GPIO_Pin_14|GPIO_Pin_15;//报警输入
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;
  GPIO_Init(GPIOD, &GPIO_InitStructure); 
	
}
//led灯控制25 方波 24 开关
void LED_ON(void)
{
   GPIO_SetBits(GPIOA,GPIO_Pin_3);
	 len_on = 1;
}
void LED_OFF(void)
{
	 GPIO_ResetBits(GPIOA,GPIO_Pin_3);
	 len_on = 0;
}
//普通开关 12 设备开关
//port 11:20
void output_ON(int port)
{
	switch(port)
	{
		case 11:
			 GPIO_SetBits(GPIOA,GPIO_Pin_2);
			break;
		case 12:
			 GPIO_SetBits(GPIOA,GPIO_Pin_1);
			break;
		case 13:
			 GPIO_SetBits(GPIOC,GPIO_Pin_3);
			break;
		case 14:
			GPIO_SetBits(GPIOC,GPIO_Pin_2);
			break;
		case 15:
			GPIO_SetBits(GPIOC,GPIO_Pin_1);
			break;
	  case 16:
			GPIO_SetBits(GPIOC,GPIO_Pin_0);
			break;
		case 17:
			GPIO_SetBits(GPIOC,GPIO_Pin_13);
			break;
		case 18:
			GPIO_SetBits(GPIOE,GPIO_Pin_6);
			break;
		case 19:
			GPIO_SetBits(GPIOE,GPIO_Pin_5);
			break;
		case 20:
			GPIO_SetBits(GPIOE,GPIO_Pin_4);
			break;
		default:
			break;
	}
  
}
void output_OFF(int port)
{
	 switch(port)
	{
		case 11:
			 GPIO_ResetBits(GPIOA,GPIO_Pin_2);
			break;
		case 12:
			 GPIO_ResetBits(GPIOA,GPIO_Pin_1);
			break;
		case 13:
			 GPIO_ResetBits(GPIOC,GPIO_Pin_3);
			break;
		case 14:
			GPIO_ResetBits(GPIOC,GPIO_Pin_2);
			break;
		case 15:
			GPIO_ResetBits(GPIOC,GPIO_Pin_1);
			break;
	  case 16:
			GPIO_ResetBits(GPIOC,GPIO_Pin_0);
			break;
		case 17:
			GPIO_ResetBits(GPIOC,GPIO_Pin_13);
			break;
		case 18:
			GPIO_ResetBits(GPIOE,GPIO_Pin_6);
			break;
		case 19:
			GPIO_ResetBits(GPIOE,GPIO_Pin_5);
			break;
		case 20:
			GPIO_ResetBits(GPIOE,GPIO_Pin_4);
			break;
		default:
			break;
	}
}

u8 readPort(int port)
{
	u8 state;
	switch(port)
	{
		case 1:
			state =  GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_11);
			break;
		case 2:
			state = GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_12);
			break;
		case 3:
			state = GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_8);
			break;
		case 4:
			state = GPIO_ReadInputDataBit(GPIOC,GPIO_Pin_9);
			break;
		case 5:
			state = GPIO_ReadInputDataBit(GPIOC,GPIO_Pin_8);
			break;
	  case 6:
			state = GPIO_ReadInputDataBit(GPIOC,GPIO_Pin_7);
			break;
		case 7:
			state = GPIO_ReadInputDataBit(GPIOC,GPIO_Pin_6);
			break;
		case 8:
			state = GPIO_ReadInputDataBit(GPIOD,GPIO_Pin_15);
			break;
		case 9:
			state = GPIO_ReadInputDataBit(GPIOD,GPIO_Pin_14);
			break;
		case 10:
			state = GPIO_ReadInputDataBit(GPIOD,GPIO_Pin_13);
			break;
		default:
			break;
	}
	return state;
}



u8 IS_LED_ON(void)
{
	 return len_on;
}
u8 ReadGPIOMode(void)
{
	GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_0);
}