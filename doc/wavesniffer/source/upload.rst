Upload firmware in WaveSniffer
******************************

Once the firmware or thye bootloarder or both are build. 
The nex step is to upload the result to the atmel.

There are two ways to do that. The first one is to use the STK500.
The second one is to use a serial port.

The second one is possible only if a bootloader has been uploaded.
So the upload of the bootloader can be done only with the STK500.

If you have uploaded a bootloader, the firmware MUST be uploaded 
with by the bootloader (the second way). Otherelse, the bootloader will
be overwrited by the firmware

Upload with the STK500:
=======================

Upload the bootloader:
----------------------

avrdude -m m640 -c stk500 -P /dev/ttyUSB0 -U flash:w:main.hex:a

Upload the firmware:
--------------------

avrdude -m 640 -c stk500 -P /dev/ttyUSB0 -U flash:w:firmware.hex:a

Upload with a bootloarder:
==========================

Connect the WaveSniffer to you PC via the USB port

avrdude -m m640 -c butterfly -p /dev/ttyUSB0 -b 19600 -U flash:w:firmware.hex:a


