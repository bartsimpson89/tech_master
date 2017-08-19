#ifndef USER_DISPLAY_
#define USER_DISPLAY_
#include "stm32f10x.h"
#include "main.h"

//static void  setBackLight();
static  void reSetBackLight(void);
static void setCS(void);
static  void reSetCS(void);
static  void setCLK(void);
static  void reSetCLK(void);
static  void setDA(void);
static  void reSetDA(void);
static   u8 readDA(void);

static void DelayUS(u8 delay);				//΢����ʱ
static void DelayMS(u16 delay);				//������ʱ
static void Read_LCD_Busy(void);				//��æ
static void Write_LCD_Comd(u8 commond);		//LCDдָ��
static void Write_LCD_addr(u8 addr);
static void Write_LCD_Data(u8 Data);		//LCDд����
static void Write_LCD_Data_H(const unsigned char ch);
static void Write_LCD_Data_L(const unsigned char ch);
//static void Write_LCD_corsion(u8 addr);

extern void Init_12864LCD(void);				//12864LCD��ʼ��
extern void Write_LCD_Line(u8 line,const unsigned char* str);	//д��
extern void Clear_LCD_Data(void);
extern void DISPLAY_init(void);
extern void Display_Page(int page);
static u8 Read_LCD_Data(void);

extern u8 page_1[][17];	
extern u8 page_2[][17];	
extern u8 page_3[][17];	
extern u8 page_4[][17];	
extern u8 page_5[][17];	
extern u8 page_6[][17];	
extern u8 page_7[][17];	
extern u8 page_8[][17];	
extern u8 page_9[][17];	
extern u8 pwd[];
extern u8 devPwd[];
#define readbusy  0xFC		//��æָ��
#define readdata  0xFE		//������ָ��
#define writecom  0xF8		//д����ָ��
#define writedata 0xFA		//д����ָ��

#endif
