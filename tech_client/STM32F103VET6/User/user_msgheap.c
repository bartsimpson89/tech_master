#include "stm32f10x_it.h"
#include "stm32f10x.h"
#include "user_msgheap.h"
#include "user_uart.h"
#include "user_global.h"
static int heap_sp = 0;
static u8 heap_msg[HEAP_SIZE][MSG_MAX_LENGTH];

static int heap_sp_send = 0;
static u8 heap_send_msg[HEAP_SIZE][MSG_MAX_LENGTH];
u8 pushMsg(u8 msg[],u8 len)
{
	int i=0;
	u8 *ptr = 0;
	if(len>MSG_MAX_LENGTH || isHeapFull())
	{
		  return MSG_HEAP_FALSE;
	}
	ptr = heap_msg[heap_sp];
	for(i=0;i<len;i++)
	{
		*(ptr+i) = msg[i];
	}
	*(ptr+i)='\0';
	heap_sp++;
	return MSG_HEAP_SUCCESS;
}
u8 popMsg(u8 msg[],u8 len)
{
	int i=0;
	u8 *ptr = 0;
	if(isHeapEmpty())
	{
		  return MSG_HEAP_FALSE;
	}
	heap_sp--;
	ptr = heap_msg[heap_sp];
	for(i=0;i<len;i++)
	{
		 msg[i] = *(ptr+i) ;
	}
	msg[i]='\0';
	return MSG_HEAP_SUCCESS;
	
}
u8 clearHeap(void)
{
	int i=0;
	int j=0;
	for(i=0;i<HEAP_SIZE;i++)
	{
		for(j=0;j<MSG_MAX_LENGTH;j++)
		{
			heap_msg[i][j] = 0;
		}
	}
  heap_sp = 0;	
  return 	0;
}
u8  isHeapFull(void)
{
	if(heap_sp ==HEAP_SIZE)
	{
		return HEAP_ISFULL;
	}
	else
	{
		return HEAP_NOTFULL;
	}
}
u8 isHeapEmpty(void)
{
	 if(heap_sp == 0)
	 {
		 return HEAP_ISEMPTY;
	 }
	 else
	 {
		 return HEAP_NOTEMPTY;
	 }
}
///////////////////////////////////////////////////

u8  isHeapSendMsgFull(void)
{
	if(heap_sp_send ==HEAP_SIZE)
	{
		return HEAP_ISFULL;
	}
	else
	{
		return HEAP_NOTFULL;
	}
}
u8 isHeapSendMsgEmpty(void)
{
	 if(heap_sp_send == 0)
	 {
		 return HEAP_ISEMPTY;
	 }
	 else
	 {
		 return HEAP_NOTEMPTY;
	 }
}

u8 pushSendMsg(u8 msg[],u8 len)
{
	int i=0;
	u8 *ptr = 0;
	if(len>MSG_MAX_LENGTH || isHeapSendMsgFull())
	{
		  return MSG_HEAP_FALSE;
	}
	ptr = heap_send_msg[heap_sp_send];
	for(i=0;i<len;i++)
	{
		*(ptr+i) = msg[i];
	}
	*(ptr+i)='\0';
	heap_sp_send++;
	return MSG_HEAP_SUCCESS;
	
}
u8 popSendMsg(u8 msg[])
{
	
	int i=0;
	u8 *ptr = 0;
	if(isHeapSendMsgEmpty())
	{
		  return MSG_HEAP_FALSE;
	}
	heap_sp_send--;
	ptr = heap_send_msg[heap_sp_send];
	for(i=0;i<MSG_MAX_LENGTH;i++)
	{
		 msg[i] = *(ptr+i) ;
		 if(*(ptr+i) == '\0')
		 {
			 break;
		 }
	}
	 return MSG_HEAP_SUCCESS;
}
