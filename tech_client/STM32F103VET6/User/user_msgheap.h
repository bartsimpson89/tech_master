#ifndef _MSG_HEAP_H
#define _MSG_HEAP_H
#define HEAP_SIZE (32)
#define MSG_MAX_LENGTH (64)
#define MSG_HEAP_SUCCESS (1)
#define MSG_HEAP_FALSE   (0)

#define HEAP_ISFULL  (1)
#define HEAP_NOTFULL (0)
#define HEAP_ISEMPTY  (1)
#define HEAP_NOTEMPTY  (0)

extern u8 pushMsg(u8 msg[],u8 len);
extern u8 popMsg(u8 msg[],u8 len);
extern u8 clearHeap(void);
extern u8  isHeapFull(void);
extern u8  isHeapEmpty(void);





extern u8 isHeapSendMsgFull(void);
extern u8 isHeapSendMsgEmpty(void);
extern u8 pushSendMsg(u8 msg[],u8 len);
extern u8 popSendMsg(u8 msg[]);


static int heap_sp;
static u8 heap_msg[][64];
static int heap_sp_send ;
static u8 heap_send_msg[][MSG_MAX_LENGTH];
#endif