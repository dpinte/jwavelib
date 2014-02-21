Configure
*********

Once we have all tools, we need to configure the Atmel to fit
our need.

The configuration will be realized with a stk500 on linux.

Step by step procedure
======================

* Record the "RS232 CTRL" comm port to your computer
* Record the "ISP6PIN" connector from the STK500 on the 6 pin conector on the WaveSniffer
  /!\ on the cable, the red wire need to be on the pin n°1
* Switch the STK500 on
* Configure the fuses of the Atmel (see next "Configure Fuses")
* Read the fuses to check if everything is OK
* Reboot the WaveSniffer with the pushbutton or switch off the stk500

Configure the fuses
===================

This paragraph will discribe how to configure the fuses (internal configuration) of the Atmel used
on the WaveSnifer.

To have more information about fuses configuration, see page 338 of the ATMEGA640 datasheet

/!\ WARNING /!\ : The following step are dagerous. Be carefull.

To fit our need, the fuses need to be configured like this:

* High fuse: 0x9C 
* Low fuse:  0xFF

Write Fuse with avrdude
-----------------------

avrdude -m m640 -c stk500 -P /dev/ttyUSB0 -U hfuse:w:0x9C:m -U lfuse:w:0x00:m

explanation:
^^^^^^^^^^^^

* -m m640           : the Atmel product type
* -c                : the component used to program the atmel
* -P /dev/ttyUSB0   : the serial port recorded to the programmer
* -U                : the action to do

Read Fuse with avrdude
^^^^^^^^^^^^^^^^^^^^^^

avrdude -m m640 -c stk500 -P /dev/ttyUSB0 -U hfuse:r:-:h -U lfuse:r:-:h


