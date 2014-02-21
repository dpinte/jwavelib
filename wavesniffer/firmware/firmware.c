/*
 * main.c
 *
 *   Created on: 13-févr.-2009
 *       Author: Bertrand Antoine
 *      Project: WaveSniffer
 *  Description: Firmware for the sniffer
 *      version: 0.1
 */

#include <stdint.h>
#include <avr/io.h>
#include <avr/interrupt.h>

#include "usart.h"

/*==================
 * CONST define
 *==================*/
#ifndef F_CPU
	#warning "F_CPU not set"
	#define F_CPU 14745600UL    // FOSC in Hz
#endif
/* usart const */
/* from modem: 9600b */
#define USART0_BAUD 9600UL
#define UBRR0_VAL ((F_CPU / (USART0_BAUD * 16UL)) - 1)
/* from term: 9600b */
#define USART2_BAUD 9600UL
#define UBRR2_VAL ((F_CPU / (USART2_BAUD * 16UL)) - 1)
/* to PC: 115200b*/
#define USART1_BAUD 115200UL
#define UBRR1_VAL ((F_CPU / (USART1_BAUD * 16UL)) - 1)

/* timer const */
#define OCR1A_VAL 14745UL	/* timer for 1 ms */

/* helper const */
#define MAX_BUF_LEN 512UL	/* reveive buffer length */

/* CMD const */
#define FROM_MOD 0x4D		/* modem direction byte (M) */
#define FROM_MOD_OVR 0x4E	/* modem direction byte with time over */
#define FROM_TER 0x54		/* terminal direction byte (T) */
#define FROM_TER_OVR 0X55	/* terminal direction byte with time over */
#define SYNC_BYTE 0xFF		/* sync byte */

/*==================
 * custom type
 *==================*/
struct data {
	uint8_t RXbyte;
	uint8_t timestamp;
	
};
typedef struct data BUF_DATA;

/*==================
 * global vars
 *==================*/
volatile BUF_DATA mod_buf[MAX_BUF_LEN];	/* from modem buffer */
volatile BUF_DATA ter_buf[MAX_BUF_LEN];	/* from terminal buffer */
volatile uint8_t ts_mod;				/* modem timestamp */
volatile uint8_t ts_ter;				/* terminal timestamp */
volatile uint8_t ts_mod_ovr;			/* modem timestamp over */
volatile uint8_t ts_ter_ovr;			/* terminal timestamp over */
volatile uint16_t mod_wr_ind;
volatile uint16_t mod_rd_ind;
volatile uint16_t ter_wr_ind;
volatile uint16_t ter_rd_ind;

/*==================
 * local functions declaration
 *==================*/
void io_init(void);
void timer_init(void);
void int_init(void);
void send_sync(void);
void send_mod_byte(BUF_DATA);
void send_ter_byte(BUF_DATA);

/*==================
 * main loop
 *==================*/
/*
 * We just need to read modem and terminal buffer
 * to send each received byte stored in buffers to the PC
 */
int main(void){
	BUF_DATA read_buf_b;

	cli();			/* disable global interrupt */

	/* init all needed modules */
	io_init();
	usart0_init(UBRR0_VAL);
	usart1_init(UBRR1_VAL);
	usart2_init(UBRR2_VAL);
	timer_init();
	int_init();

	/* force default index value */
	mod_rd_ind = 0;
	mod_wr_ind = 0;
	ter_rd_ind = 0;
	ter_wr_ind = 0;

	sei();			/* enable global interrupt */

    /* toggle led */
    /* change to your need:
     *    - (see datacheet for pin)
     *    - PL0 -> pin 35 (used for the dev board)
     *    - PL6 -> pin 41 (used on the final board)
     *SEE IO_INIT() TOO
     */
	PORTL ^= _BV(PL6);

	/* send sync to warn software the sniffing is running */
	send_sync();

	for(;;){
		/* send byte received from modem */
		cli();
		if(mod_rd_ind != mod_wr_ind){
			read_buf_b = mod_buf[mod_rd_ind];
			mod_rd_ind++;

			if(mod_rd_ind >= MAX_BUF_LEN){
				mod_rd_ind = 0;
			}
			send_mod_byte(read_buf_b);
		}
		sei();

		/* send byte received from terminal */
		cli();
		if(ter_rd_ind != ter_wr_ind){
			read_buf_b = ter_buf[ter_rd_ind];
			ter_rd_ind++;

			if(ter_rd_ind >= MAX_BUF_LEN){
				ter_rd_ind = 0;
			}
			send_ter_byte(read_buf_b);
		}
		sei();
	}

	return 0;
}

/*==================
 * interruptions
 *==================*/
/* USART0 interrupt */
/* Store the received byte form the modem */
ISR(USART0_RX_vect){
	BUF_DATA recb;
	uint8_t b;

	b = usart0_getc();

	recb.RXbyte = b;
	recb.timestamp = ts_mod;
	ts_mod = 0;

	mod_buf[mod_wr_ind] = recb;
	mod_wr_ind++;

	if(mod_wr_ind >= MAX_BUF_LEN){
		mod_wr_ind = 0;
	}
}

/* USART2 interrupt */
/* Store the received byte form the terminal */
ISR(USART2_RX_vect){
	BUF_DATA recb;
	uint8_t b;

	b = usart2_getc();

	recb.RXbyte = b;
	recb.timestamp = ts_mod;
	ts_mod = 0;

	ter_buf[ter_wr_ind] = recb;
	ter_wr_ind++;

	if(ter_wr_ind >= MAX_BUF_LEN){
		ter_wr_ind = 0;
	}
}

/* timer compare interrupt */
/* update the modem and terminal timestamp */
ISR(TIMER1_COMPA_vect){
	/*if(ts_mod == 255){
		ts_mod = 0;
		ts_mod_ovr = 1;
	} else {
		ts_mod++;
	}

	if(ts_ter == 255){
		ts_ter = 0;
		ts_ter_ovr = 1;
	} else {
		ts_ter++;
	}*/

    if(ts_mod < 255)
        ts_mod++;

    if(ts_ter < 255)
        ts_ter++;

	TCNT1 = 0;	/* reset timer */
}

/*==================
 * local functions implementation
 *==================*/
void io_init(){
	DDRL |= _BV(PL6);	/* LED0 on PL0 */
	//PORTL ^= _BV(PL6);	/* switch off LEDO */

//	DDRL |= _BV(PL1) | _BV(PL2) | _BV(PL3);
}

void timer_init(){
	TCCR1B |= _BV(CS10);	/* no prescaling */
	OCR1A |= OCR1A_VAL;
}

void int_init(void){
	UCSR0B |= _BV(RXCIE0);	/* USARTO interrupt */
	UCSR2B |= _BV(RXCIE2);	/* USART1 interrupt */
	TIMSK1 |= _BV(OCIE1A);	/* timer1 interrupt */
}

/* send the first sync frame to PC */
void send_sync(void){
	usart1_putc(SYNC_BYTE);
	usart1_putc(SYNC_BYTE);
	usart1_putc(SYNC_BYTE);
}

/* read data in modem buffer then send it */
void send_mod_byte(BUF_DATA byte){
	/* warn for timer overflow */
	/*if(ts_mod_ovr){
		usart2_putc(FROM_MOD_OVR);
		ts_mod_ovr = 0;
	} else {
		usart2_putc(FROM_MOD);
	}*/

	usart1_putc(FROM_MOD);
	usart1_putc(byte.RXbyte);
	usart1_putc(byte.timestamp);

/*	usart2_putc('*');
	usart2_putc(mod_rd_ind);
	usart2_putc(mod_wr_ind);*/
}

/*read data in modem buffer then send it */
void send_ter_byte(BUF_DATA byte){
	/* warn for timer overflow */
	/*if(ts_ter_ovr){
		usart2_putc(FROM_TER_OVR);
		ts_ter_ovr = 0;
	} else {
		usart2_putc(FROM_TER);
	}*/

	usart1_putc(FROM_TER);
	usart1_putc(byte.RXbyte);
	usart1_putc(byte.timestamp);
}
