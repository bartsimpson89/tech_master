#ifndef _USER_LED_H
#define _USER_LED_H

#define LED8    GPIOD
#define LED8_IO GPIO_Pin_8 

extern void GPIO_Configuration(void);
extern void LED_ON(void);
extern void LED_OFF(void);
extern u8 IS_LED_ON(void);
extern u8 ReadGPIOMode(void);
extern void output_ON(int port);
extern void output_OFF(int port);
extern u8  readPort(int port);


#endif /* _USER_LED_H */
