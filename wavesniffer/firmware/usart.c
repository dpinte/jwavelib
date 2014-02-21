/*
 * usart.c
 *
 *   Created on: 13-févr.-2009
 *       Author: Bertrand Antoine
 *      Project: WaveSniffer
 *  Description: atmega640 usart lib
 *
 */

#include <stdint.h>
#include <avr/io.h>
#include "usart.h"

/*==================
 * USART0
 *==================*/
/* init USART0 */
void usart0_init(uint16_t ubrr){
	/* set baudrate */
	UBRR0L = ubrr;
	UBRR0H = (ubrr >> 8);

	/* enable 8 bits / 1 stop RX TX */
	UCSR0B |= _BV(RXEN0) | _BV(TXEN0);
	UCSR0C |= _BV(UCSZ00) | _BV(UCSZ01);
}

/* send a byte */
void usart0_putc(uint8_t c){
	/* wait until buffer is empty, the fill it */
	while(!(UCSR0A & _BV(UDRE0)));
	UDR0 = c;
}

/* send an array of byte */
void usart0_puts(int8_t *s){
	while(*s){
		usart0_putc(*s);
		s++;
	}
}

/* get a byte */
uint8_t usart0_getc(void){
	/* wait untill buffer is full then read it */
	while(!(UCSR0A & _BV(RXC0)));
	return UDR0 ;
}

/* get a string */
void usart0_gets(int8_t * buffer, uint8_t len){
	while(len > 0){
		*buffer++ = usart0_getc();
		len--;
	}
	*buffer = '\0';
}

/*==================
 * USART1
 *==================*/
/* init USART1 */
void usart1_init(uint16_t ubrr){
	/* set baudrate */
	UBRR1L = ubrr;
	UBRR1H = (ubrr >> 8);

	/* enable 8 bits / 1 stop RX TX */
	UCSR1B |= _BV(RXEN1) | _BV(TXEN1);
	UCSR1C |= _BV(UCSZ10) | _BV(UCSZ11);
}

/* send a byte */
void usart1_putc(uint8_t c){
	/* wait until buffer is empty, the fill it */
	while(!(UCSR1A & _BV(UDRE1)));
	UDR1 = c;
}

/* send an array of byte */
void usart1_puts(int8_t *s){
	while(*s){
		usart1_putc(*s);
		s++;
	}
}

/* get a byte */
uint8_t usart1_getc(void){
	/* wait untill buffer is full then read it */
	while(!(UCSR1A & _BV(RXC1)));
	return UDR1;
}

/* get a string */
void usart1_gets(int8_t * buffer, uint8_t len){
	while(len > 0){
		*buffer++ = usart1_getc();
		len--;
	}
	*buffer = '\0';
}

/*==================
 * USART2
 *==================*/
/* init USART2 */
void usart2_init(uint16_t ubrr){
	/* set baudrate */
	UBRR2L = ubrr;
	UBRR2H = (ubrr >> 8);

	/* enable 8 bits / 1 stop RX TX */
	UCSR2B |= _BV(RXEN2) | _BV(TXEN2);
	UCSR2C |= _BV(UCSZ20) | _BV(UCSZ21);
}

/* send a byte */
void usart2_putc(uint8_t c){
	/* wait until buffer is empty, the fill it */
	while(!(UCSR2A & _BV(UDRE2)));
	UDR2 = c;
}

/* send an array of byte */
void usart2_puts(int8_t *s){
	while(*s){
		usart2_putc(*s);
		s++;
	}
}

/* get a byte */
uint8_t usart2_getc(void){
	/* wait untill buffer is full then read it */
	while(!(UCSR2A & _BV(RXC2)));
	return UDR2;
}

/* get a string */
void usart2_gets(int8_t * buffer, uint8_t len){
	while(len > 0){
		*buffer++ = usart2_getc();
		len--;
	}
	*buffer = '\0';
}

/*==================
 * USART3
 *==================*/
/* init USART3 */
void usart3_init(uint16_t ubrr){
	/* set baudrate */
	UBRR3L = ubrr;
	UBRR3H = (ubrr >> 8);

	/* enable 8 bits / 1 stop RX TX */
	UCSR3B |= _BV(RXEN3) | _BV(TXEN3);
	UCSR3C |= _BV(UCSZ30) | _BV(UCSZ31);
}

/* send a byte */
void usart3_putc(uint8_t c){
	/* wait until buffer is empty, the fill it */
	while(!(UCSR3A & _BV(UDRE3)));
	UDR3 = c;
}

/* send an array of byte */
void usart3_puts(int8_t *s){
	while(*s){
		usart3_putc(*s);
		s++;
	}
}

/* get a byte */
uint8_t usart3_getc(void){
	/* wait untill buffer is full then read it */
	while(!(UCSR3A & _BV(RXC3)));
	return UDR3;
}

/* get a string */
void usart3_gets(int8_t * buffer, uint8_t len){
	while(len > 0){
		*buffer++ = usart3_getc();
		len--;
	}
	*buffer = '\0';
}
