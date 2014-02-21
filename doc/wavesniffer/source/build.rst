Build the firmware and the bootloader
*************************************

Once the WaveSnifer has been confifured, we need to build the firmware.

The firmware is divided in two parts:
* The firmware itsef
* A bootloader to have a easy way to update firwave without the STK500

Building the firmware:
======================

1) Configure the Makefile:
--------------------------

Edit the Makefile and change the folowwing line to fit yout configuration:

* CC = avr-gcc
* OBJCOPY = avr-objcopy
* OBJDUMP = avr-objdump
* AVRDUDE = avrdude

2) Configure the firmware:
--------------------------

Edit firmware.c and check the following lines with the WwaveSniffer schhematic.
The default value ar for the prototype:

* F_CPU 14745600UL
* USART0_BAUD 9600UL
* USART2_BAUD 9600UL
* USART1_BAUD 115200UL

2) Building:
------------

make

Building the bootloader:
========================

1) Configure the Makefile:
--------------------------

Same than configuring the firmware Makefile

2) Configure the bootloader:
----------------------------

Chack the following lines on main.c:

* F_CPU = 14745600UL
* BAUDRATE = 19600
* UART_USE_SECOND for mega640
* START_WAIT
  WAIT_VALUE 100

3) Building:
------------

make



