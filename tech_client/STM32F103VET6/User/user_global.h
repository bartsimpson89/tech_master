#ifndef USER_GLOBAL_H_
#define USER_GLOBAL_H_
#include "stm32f10x.h"
#include "stm32f10x_it.h"
#include "main.h"
#define RxBufferSize    512
extern u8 RxBuffer1[RxBufferSize];
extern volatile u16 RxCounter1;
extern u8 RxBuffer2[RxBufferSize];
extern volatile u16 RxCounter2;
extern Device dev;
extern ServerInfo server_info;
extern volatile u32 TimingDelay;

extern void clear_Buffer1();
extern void clear_Buffer2();
extern void clear_buff(u8 buff[],u32 len);
extern void intToCharBuff(int data,u8 buff[],int size);
extern int charBuffToInt(u8 buff[],int size);
extern u32 getStrLen(u8 buff[]);
extern void initGlobalVariable();
extern bool compareStr(u8 str1[],u8 str2[]);
extern void  WriteLog(u8 msg[]);
#endif
