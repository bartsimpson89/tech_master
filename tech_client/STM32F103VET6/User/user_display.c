#include "user_display.h"
u8 page_1[][17]={"�����豸����ϵͳ",
	               "    00:00:00    ",
                 "                ",
                 "  ��       NO.00"};
u8 page_2[][17]={"    ״̬��ѯ    ",
	               "    ��������    ",
                 "    ���а���    ",
                 "      ����      "};
u8 page_3[][17]={"����ʱ�� 00:00  ",
	               "�ػ�ʱ�� 00:00  ",
	               "�������� 00     ",
	               "����            "};
u8 page_4[][17]={"    ��������    ",
	               "      ****      ",
	               "                ",
	               "����        ȷ��"};
u8 page_5[][17]={"���ؿ���  ��    ",
	               "����ʱ��  00:00 ",
	               "�ػ�ʱ��  00:00 ",
	               "ȡ��        ȷ��"};
u8 page_6[][17]={"                ",
	               "    ��ȴ�      ",
	               "  ���ں�����ʦ  ",
	               "  ���ذ�ȷ�ϼ�  "};
u8 page_7[][17]={"                ",
	               "    �豸©��    ",
	               "                ",
	               "                "};
u8 page_8[][17]={"                ",
	               "    ������      ",
	               "                ",
	               "                "};
u8 page_9[][17]={"                ",
	               "      ����      ",
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


void DelayUS(u8 delay)				//΢����ʱ
{
	while(--delay);
}
void DelayMS(u16 delay)				//������ʱ
{
	  u16 i;
    for(;delay>0;delay--)
        for(i=0;i<453;i++);
}
void Read_LCD_Busy(void)				//��æ
{
		u8 temp;
	while(1)
	{
		Write_LCD_Comd(readbusy);	//д��æָ��
		temp = 	Read_LCD_Data();	//��æ����
		if((temp & 0x80) != 0x80)	//��æ����������æ���������ѯæ״̬
		break;
	}
}
void Write_LCD_Comd(u8 commond)		//LCDдָ��
{
	u8 i;
	setCS();//GPIOE->BSRR = GPIO_Pin_14;//CS = 1;					//ʹ��
	reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;
	for(i=0;i<8;i++)		//д8λָ��
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
void Write_LCD_Data(u8 Data)		//LCDд����
{
	   Write_LCD_Data_H(Data);
	   Write_LCD_Data_L(Data);
}
void Init_12864LCD(void)				//12864LCD��ʼ��
{
	Read_LCD_Busy();			//��æ
    Write_LCD_Comd(writecom);	//д"дָ�ָʾ������д����ָ��
    Write_LCD_Data(0x30);		//д��������ָ��
    DelayMS(1);
	Read_LCD_Busy();
    Write_LCD_Comd(writecom);	//д"дָ�ָʾ������д����ָ��
    Write_LCD_Data(0x0c);       //��ʾ��
    DelayMS(1);
	Read_LCD_Busy();
    Write_LCD_Comd(writecom);	//д"дָ�ָʾ������д����ָ��
    Write_LCD_Data(0x01);       //�����ʾ
    DelayMS(15);
	Read_LCD_Busy();
    Write_LCD_Comd(writecom);	//д"дָ�ָʾ������д����ָ��
    Write_LCD_Data(0x06);       //�����趨��
    DelayMS(1);
		
}
void Write_LCD_addr(u8 addr)
{
		Read_LCD_Busy();			//��æ
    Write_LCD_Comd(writecom);	//д"дָ�ָʾ������д����ָ��
    Write_LCD_Data(addr);       //д��ַ
}
//void Write_LCD_corsion(u8 addr)
//{
	//Write_LCD_addr(addr);
	 // Read_LCD_Busy();			//��æ
   // Write_LCD_Comd(0x30);	//д"дָ�ָʾ������д����ָ��
   // Write_LCD_Data(0x36);       //д��ַ
//}
void Write_LCD_Data_H(const unsigned char ch)
{
	u8 i,temp;
	temp = ch & 0xF0;	//ȡ��д���ݸ�4λ
	setCS();//GPIOE->BSRR = GPIO_Pin_14;//CS = 1;
	reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;
	for(i=0;i<8;i++)	//д��4λ+��4λ0000
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
	//TRISE1 = 1;			//SID������Ϊ����
	setCS();//GPIOE->BSRR = GPIO_Pin_14;//CS = 1;				//ʹ��
  temp = ch & 0x0F;	//ȡ��д���ݵ���λ
	for(i=0;i<8;i++)	//д��λ����
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


void Write_LCD_Line(u8 line,const unsigned char* str)	//д��
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
    for(i=0;i<16;i++)           //ѭ��16��
    {  
	   Read_LCD_Busy();		//��æ
     Write_LCD_Comd(writedata);		//д"д����ָ�ָʾ������д��������			
     Write_LCD_Data(str[i]);	//д����
    }
}
u8 Read_LCD_Data(void)
{
	u8 i,temp,temp1;
	//TRISE1 = 1;			//SID������Ϊ����
	setCS();//GPIOE->BSRR = GPIO_Pin_14;//CS = 1;				//ʹ��
	reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;			//ʱ������Ч
	for(i=0;i<8;i++)	//��ȡ��4λ����
	{
		temp <<= 1;
		setCLK();//GPIOB->BSRR = GPIO_Pin_10;//SCLK = 1;		//ʱ����Ч
		if(readDA())			//����Ϊ1
		{
			temp |= 0x01;
		}
		reSetCLK();//GPIOB->BRR = GPIO_Pin_10;//SCLK = 0;		//ʱ����Ч
	}
	temp &= 0xF0;		//�����4λ
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
	temp1 &= 0xF0;		//�����4Ϊ
	temp = temp | (temp1 >> 4);	//����8λ����
	reSetCS();//GPIOE->BRR = GPIO_Pin_14;//CS = 0;				
	//TRISE1 = 0;	
	return (temp);		//����8λ����
}
void DISPLAY_init()
{
	  reSetBackLight();//GPIOA->BRR = GPIO_Pin_6;//��Ļ����
    Init_12864LCD();        //��ʼ��12864LCD
    Display_Page(1);
 }
void Clear_LCD_Data()
{
		Read_LCD_Busy();
    Write_LCD_Comd(writecom);	//д"дָ�ָʾ������д����ָ��
    Write_LCD_Data(0x01);       //�����ʾ
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
