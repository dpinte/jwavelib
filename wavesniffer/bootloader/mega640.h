#ifndef _MEGA640_H_
#define _MEGA640_H_

/* fake ATmega64 part-code */
/* Part-Code ISP */
#define DEVTYPE_ISP     0x45
/* Part-Code Boot */
#define DEVTYPE_BOOT    0x46

#define SIG_BYTE1	0x1E
#define SIG_BYTE2	0x96
#define SIG_BYTE3	0x08

/* use the third UART */
#define UART_BAUD_HIGH  UBRR2H
#define UART_BAUD_LOW   UBRR2L
#define UART_STATUS     UCSR2A
#define UART_TXREADY    UDRE2
#define UART_RXREADY    RXC2
#define UART_DOUBLE     U2X2
#define UART_CTRL       UCSR2B
#define UART_CTRL_DATA  ((1<<TXEN2) | (1<<RXEN2))
#define UART_CTRL2      UCSR2C
#define UART_CTRL2_DATA ((1<<UCSZ21) | (1<<UCSZ20))
#define UART_DATA       UDR2

#ifdef RAMPZ
    #warning RAMPZ defined (avr-libc bug)
    #undef RAMPZ
    //#undef RAPZ0
#endif

#define WDT_OFF_SPECIAL
static inline void bootloader_wdt_off(void)
{
    cli();
    wdt_reset();
    /* Clear WDRF in MCUSR */
    MCUSR &= ~(1<<WDRF);
    /* Write logical one to WDCE and WDE */
    /* Keep old prescaler setting to prevent unintentional time-out */
    WDTCSR |= (1<<WDCE) | (1<<WDE);
    /* Turn off WDT */
    WDTCSR = 0x00;
}

#endif /* __MEGA640_H_ */
