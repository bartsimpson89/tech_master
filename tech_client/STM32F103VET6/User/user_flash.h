#ifndef USER_FLASH_H_
#define USER_FLASH_H_
#include "stm32f10x_flash.h"  
#include "stm32f10x.h"
#define  STARTADDR  0x08010000 
#define  ADDR_ID    0x08010000       //4
#define  ADDR_SERVER_IP 0x08010004  //16
#define  ADDR_ROUTER_NAME 0x08010014 //64
#define  ADDR_ROUTER_PWD  0x08010054 //64
#define  ADDR_LEAKCURRENT  0x08010200 //4
#define  ADDR_OVERRANGE    0x08010204 //4
#define  ADDR_OVERCURRENT  0x08010208 //4
extern int ReadFlashNBtye(uint32_t ReadAddress, uint8_t *ReadBuf, int32_t ReadNum);
static void WriteFlashOneWord(uint32_t WriteAddress,uint32_t WriteData);
extern void WriteFlashWords(uint32_t WriteAddress,uint32_t WriteBuff[],uint8_t len);
extern void Flash_init();
extern void writeIdToFlash(u32 id);
#endif