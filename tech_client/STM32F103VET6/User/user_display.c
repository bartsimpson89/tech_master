#include "user_display.h"
u8 page_1[][17]={"智能设备管理系统",
	               "    00:00:00    ",
                 "                ",
                 "  关       NO.00"};
u8 page_2[][17]={"    状态查询    ",
	               "    参数设置    ",
                 "    呼叫帮助    ",
                 "      返回      "};
u8 page_3[][17]={"开机时间 00:00  ",
	               "关机时间 00:00  ",
	               "报警次数 00     ",
	               "返回            "};
u8 page_4[][17]={"    密码输入    ",
	               "      ****      ",
	               "                ",
	               "返回        确认"};
u8 page_5[][17]={"本地开关  关    ",
	               "开机时间  00:00 ",
	               "关机时间  00:00 ",
	               "取消        确认"};
u8 page_6[][17]={"                ",
	               "    请等待      ",
	               "  正在呼叫老师  ",
	               "  返回按确认键  "};
u8 page_7[][17]={"                ",
	               "    设备漏电    ",
	               "                ",
	               "                "};
u8 page_8[][17]={"                ",
	               "    超量程      ",
	               "                ",
	               "                "};
u8 page_9[][17]={"                ",
	               "      过流      ",
	               "                ",
	               "                "};

u8 pwd[] =       "      ****      ";
u8 devPwd[] =    "0000";
//void setBackLight()
//{
	  // GPIO_SetBits(GPIOA,GPIO_Pin_6);
//}
 void reSetBackLight()
{
	   GPIO_ResetBits(GPIOA,GPIO_Pin_6);
}
void setCS()
{
	  GPIO_SetBits(GPIOE,GPIO_Pin_11);// GPIO_SetBits(GPIOE,GPIO_Pin_14);
}
void reSetCS()
{
	 GPIO_ResetBits(GPIOE,GPIO_Pin_11);//  GPIO_ResetBits(GPIOE,GPIO_Pin_14);
}
 void setCLK()
{
	   GPIO_SetBits(GPIOE,GPIO_Pin_13);//GPIO_SetBits(GPIOB,GPIO_Pin_10);
}
void reSetCLK()
{
	   GPIO_ResetBits(GPIOE,GPIO_Pin_13); //GPIO_ResetBits(GPIOB,GPIO_Pin_10); 
}
 void setDA()
{
	   GPIO_SetBits(GPIOE,GPIO_Pin_12);//GPIO_SetBits(GPIOB,GPIO_Pin_11);
}
void reSetDA()
{
	   GPIO_ResetBits(GPIOE,GPIO_Pin_12);//GPIO_ResetBits(GPIOB,GPIO_Pin_11);
}

 u8 readDA()
{
	   return GPIO_ReadInputDataBit(GPIOE,GPIO_Pin_12);//GPIO_ReadInputDataBit(GPIOB,GPIO_Pin_11);
}


void DelayUS(u8 delay)				//微秒延时
{
	while(--delay);
}
void DelayMS(u16 delay)				//毫秒延时
{
	  u16 i;
    for(;delay>0;delay--)
        for(i=0;i<453;i++);
}
void Read_LCD_Busy(void)				//查忙
{
		u8 temp;
	while(1)
	{
		Write_LCD_Comd(readbusy);	//写读忙指令
		temp = 	Read_LCD_Data();	//读忙数据
		if((temp & 0x80) != 0x80)	//不忙，则跳出，忙，则继续查询忙状态
		break;
	}
}
void Write_LCD_Comd(u8 commond)		//LCD写指令
{
	u8 i;
	setCS();//GPIOE->BSRR = GPIO_Pin_14;//CS = 1;					//使能
	reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;
	for(i=0;i<8;i++)		//写8位指令
	{
		
		if(commond & 0x80)
		{
			setDA();//GPIOB->BSRR = GPIO_Pin_11;//RE1 = 1;
		}
		else
		{
			reSetDA();//GPIOB->BRR = GPIO_Pin_11;//RE1 = 0;
		}
		setCLK();//GPIOB->BSRR = GPIO_Pin_10;//SCLK = 1;
		
		commond <<= 1;
		DelayUS(20);
		reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;
	}
	reSetCS();//GPIOE->BRR = GPIO_Pin_14;//CS = 0;
}
void Write_LCD_Data(u8 Data)		//LCD写数据
{
	   Write_LCD_Data_H(Data);
	   Write_LCD_Data_L(Data);
}
void Init_12864LCD(void)				//12864LCD初始化
{
	Read_LCD_Busy();			//读忙
    Write_LCD_Comd(writecom);	//写"写指令，指示接下来写的是指令
    Write_LCD_Data(0x30);		//写基本功能指令
    DelayMS(1);
	Read_LCD_Busy();
    Write_LCD_Comd(writecom);	//写"写指令，指示接下来写的是指令
    Write_LCD_Data(0x0c);       //显示开
    DelayMS(1);
	Read_LCD_Busy();
    Write_LCD_Comd(writecom);	//写"写指令，指示接下来写的是指令
    Write_LCD_Data(0x01);       //清除显示
    DelayMS(15);
	Read_LCD_Busy();
    Write_LCD_Comd(writecom);	//写"写指令，指示接下来写的是指令
    Write_LCD_Data(0x06);       //进入设定点
    DelayMS(1);
		
}
void Write_LCD_addr(u8 addr)
{
		Read_LCD_Busy();			//读忙
    Write_LCD_Comd(writecom);	//写"写指令，指示接下来写的是指令
    Write_LCD_Data(addr);       //写地址
}
//void Write_LCD_corsion(u8 addr)
//{
	//Write_LCD_addr(addr);
	 // Read_LCD_Busy();			//读忙
   // Write_LCD_Comd(0x30);	//写"写指令，指示接下来写的是指令
   // Write_LCD_Data(0x36);       //写地址
//}
void Write_LCD_Data_H(const unsigned char ch)
{
	u8 i,temp;
	temp = ch & 0xF0;	//取待写数据高4位
	setCS();//GPIOE->BSRR = GPIO_Pin_14;//CS = 1;
	reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;
	for(i=0;i<8;i++)	//写高4位+低4位0000
	{
		
		if(temp & 0x80)
		{
			setDA();//GPIOB->BSRR = GPIO_Pin_11;//RE1 = 1;
		}
		else
		{
			reSetDA();//GPIOB->BRR = GPIO_Pin_11;//RE1 = 0;
		}
		setCLK();//GPIOB->BSRR = GPIO_Pin_10;//SCLK = 1;
		temp <<= 1;
		DelayUS(20);
		reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;
	}
	 reSetCS();
}
void Write_LCD_Data_L(const unsigned char ch)
{
	u8 i,temp;
	//TRISE1 = 1;			//SID口设置为输入
	setCS();//GPIOE->BSRR = GPIO_Pin_14;//CS = 1;				//使能
  temp = ch & 0x0F;	//取待写数据低四位
	for(i=0;i<8;i++)	//写低位数据
	{
		if(temp & 0x08)
		{
			setDA();//GPIOB->BSRR = GPIO_Pin_11;//RE1 = 1;
		}
		else
		{
			reSetDA();//GPIOB->BRR = GPIO_Pin_11;//RE1 = 0;
		}
		setCLK();//GPIOB->BSRR = GPIO_Pin_10;//SCLK = 1;
		temp <<= 1;
		DelayUS(20);
		reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;
	}
	reSetCS();//GPIOE->BRR = GPIO_Pin_14;//CS = 0;	
}


void Write_LCD_Line(u8 line,const unsigned char* str)	//写行
{
	  u8 i;
	  u8 addr = 0x80;
	  switch(line)
		{
			case 1:
				addr = 0x80;
			  break;
			case 2:
				addr = 0x90;
			  break;
			case 3:
				addr = 0x88;
			  break;
			case 4:
				addr = 0x98;
			  break;
				
		}
    Write_LCD_addr(addr);
    for(i=0;i<16;i++)           //循环16次
    {  
	   Read_LCD_Busy();		//读忙
     Write_LCD_Comd(writedata);		//写"写数据指令，指示接下来写的是数据			
     Write_LCD_Data(str[i]);	//写数据
    }
}
u8 Read_LCD_Data(void)
{
	u8 i,temp,temp1;
	//TRISE1 = 1;			//SID口设置为输入
	setCS();//GPIOE->BSRR = GPIO_Pin_14;//CS = 1;				//使能
	reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;			//时钟线无效
	for(i=0;i<8;i++)	//读取高4位数据
	{
		temp <<= 1;
		setCLK();//GPIOB->BSRR = GPIO_Pin_10;//SCLK = 1;		//时钟有效
		if(readDA())			//数据为1
		{
			temp |= 0x01;
		}
		reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;		//时钟无效
	}
	temp &= 0xF0;		//保存高4位
	for(i=0;i<8;i++)
	{
		temp1 <<= 1;
		setCLK();//GPIOB->BSRR = GPIO_Pin_10;//SCLK = 1;
		if(readDA())
		{
			temp1 |= 0x01;
		}
		reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;		
	}
	temp1 &= 0xF0;		//保存低4为
	temp = temp | (temp1 >> 4);	//保存8位数据
	reSetCS();//GPIOE->BRR = GPIO_Pin_14;//CS = 0;				
	//TRISE1 = 0;	
	return (temp);		//返回8位数据
}
void DISPLAY_init()
{
	  reSetBackLight();//GPIOA->BRR = GPIO_Pin_6;//屏幕背光
    Init_12864LCD();        //初始化12864LCD
    Display_Page(1);
 }
void Clear_LCD_Data()
{
		Read_LCD_Busy();
    Write_LCD_Comd(writecom);	//写"写指令，指示接下来写的是指令
    Write_LCD_Data(0x01);       //清除显示
}
void Display_Page(int page)
{
	  u8 i=0;
	  u8 (*pPage)[17] = 0;
	  switch(page)
		{
			case 1:
				 pPage = page_1;
			   break;
			case 2:
				 pPage = page_2;
			   break;
			case 3:
				 pPage = page_3;
			   break;
			case 4:
				 pPage = page_4;
			   break;
			case 5:
				 pPage = page_5;
			   break;
		  case 6:
				 pPage = page_6;
			   break;
			case 7:
				 pPage = page_7;
			   break;
			case 8:
				 pPage = page_8;
			   break;
			case 9:
				 pPage = page_9;
			   break;
			}
		if(0!=pPage)
		{
			 for(i=0;i<4;i++)
			 {
				  Write_LCD_Line(i+1,pPage[i]);
			 }
		}
}
