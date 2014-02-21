/*
 * usart.h
 *
 *   Created on: 13-févr.-2009
 *       Author: Bertrand Antoine
 *      Project: WaveSniffer
 *  Description: atmega640 usart lib
 *
 */

#ifndef USART_H_
#define USART_H_

#include <stdint.h>

/* usart0 declartion */
void usart0_init(uint16_t);
void usart0_putc(uint8_t);
void usart0_puts(int8_t *);
uint8_t usart0_getc(void);
void usart0_gets(int8_t *, uint8_t);

/* usart1 declartion */
void usart1_init(uint16_t);
void usart1_putc(uint8_t);
void usart1_puts(int8_t *);
uint8_t usart1_getc(void);
void usart1_gets(int8_t *, uint8_t);

/* usart2 declartion */
void usart2_init(uint16_t);
void usart2_putc(uint8_t);
void usart2_puts(int8_t *);
uint8_t usart2_getc(void);
void usart2_gets(int8_t *, uint8_t);

/* usart3 declartion */
void usart3_init(uint16_t);
void usart3_putc(uint8_t);
void usart3_puts(int8_t *);
uint8_t usart3_getc(void);
void usart3_gets(int8_t *, uint8_t);

#endif /* USART_H_ */
