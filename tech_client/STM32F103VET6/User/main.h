#ifndef _MAIN_H
#define _MAIN_H

enum AT_CMD
{
	AT_RESTART = 0,
	AT_SET_AP,
	AT_CONNECT_ROUTER,
	AT_MUL_CONNECT,
	AT_CONNECT_IP_SERVER,
};
enum AT_SEND_MSG
{
	SMSG_REGISTER = 0,//�豸ע��
	SMSG_HEART, //�豸��������
	SMSG_HELP,//�豸���Ͱ���
	SMSG_SETID,
};
enum AT_RECV_MSG
{
	RMSG_HEART = 0,  //�豸�ظ�����
	RMSG_DEV_STATE, //�豸�ظ�����
	RMSG_DEV_SET_TIMER,//�豸�ظ����ػ�ʱ��
};

enum CONNECT_STATE
{
	CONNECT_FAILED = 0,
	CONNECT_SUCCESS,
	CONNECT_CLOSED,
	CONNECT_REGISTER,
	CONNECT_SETID,
};
enum DEV_STATE
{
	DEV_UNCONENCT = 0,
	DEV_DISABLE_OPEN = 1 ,
	DEV_ENABLE_OPEN = 2 ,
	DEV_STATE_OPEN = 3 ,
	DEV_STATE_HELP = 4 ,
	DEV_ON_TIME = 5,
	DEV_OFF_TIME = 6,
};
enum HEART_STATE
{
	HEART_INIT=0,
	HEART_ALIVE,
};
typedef struct Device
{
	int id;//�豸���
	enum DEV_STATE devState;//���� �ػ�״̬
	enum DEV_STATE preDevState;
	enum CONNECT_STATE connectState;//����״̬
	enum HEART_STATE  heartState;
	u8 time[3];//Сʱ ���� ��
	bool enable_opentime_flag;
	bool enable_closetime_flag;
	bool is_opentime_flag;
	bool is_closetime_flag;
	u8 openTime[3];
	u8 closeTime[3];
	u32 alarm[3];
	
}Device;

typedef struct Server_Info
{
	u8 *p_id;
	u8 *p_router_name;
	u8 *p_router_pwd;
	u8 *p_server_ip;
	u8 *p_alarm1;
	u8 *p_alarm2;
	u8 *p_alarm3;
}ServerInfo;




static void SetId(void);//���ñ��
static void RegisterId(void);


static bool WaitRegisterResult(void);
static void CheckHeartState(void);
extern void DelayMs(u32 nTime);
extern bool isOpenDevice(u8 buff[]);
extern bool isCloseDevice(u8 buff[]);
#endif /* _MAIN_H */
