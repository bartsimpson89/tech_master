#include "user_flash.h"
volatile FLASH_Status FLASHStatus = FLASH_COMPLETE; 
int ReadFlashNBtye(uint32_t ReadAddress, uint8_t *ReadBuf, int32_t ReadNum) 
{
    int DataNum = 0;
    while(DataNum < ReadNum) 
		{
          *(ReadBuf + DataNum) = *(__IO uint8_t*) ReadAddress++;
          DataNum++;
    }
    return DataNum;
}


static void WriteFlashOneWord(uint32_t WriteAddress,uint32_t WriteData)
{
		FLASHStatus = FLASH_ProgramWord(WriteAddress, WriteData);    //flash.c ?API??
		//FLASHStatus = FLASH_ProgramWord(StartAddress+4, 0x56780000);                      //???????????
		//FLASHStatus = FLASH_ProgramWord(StartAddress+8, 0x87650000);                      //???????????
}
void WriteFlashWords(uint32_t WriteAddress,uint32_t WriteBuff[],uint8_t len)
{
	int i=0;
	for(i=0;i<len;i++)
	{
	    WriteFlashOneWord(WriteAddress+4*i,WriteBuff[i]);
	}
}
void Flash_init(void)
{
	  FLASH_Unlock();
	  FLASH_ClearFlag(FLASH_FLAG_EOP | FLASH_FLAG_PGERR | FLASH_FLAG_WRPRTERR); 
    
}

void writeIdToFlash(u32 id)
{
	u32 buff_id[1] ;
    buff_id[0] = id;	
	FLASH_ErasePage(ADDR_ID);
	WriteFlashWords(ADDR_ID,buff_id,1);
}



