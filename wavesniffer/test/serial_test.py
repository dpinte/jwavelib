#!/usr/bin/env python
# -*- coding: utf-8 -*- 
#       Author: Bertrand Antoine
#  Description: Simple test to check if the sniffer firmware is OK

from optparse import OptionParser
import os
import logging
import random
import time
import signal

try:
    import serial
except:
    print "Serial module is not present"

total = 0

def sendPerByte(sr):
    print "type 'quit' to exit"
    input = ""
    global total
    while not input == "quit":
        input = raw_input("input: ")
        sr.write(input)
        total = total + len(input)
    print "\ntotal bytes sent:", total

def sendPerFile(sr, filePath):
    if filePath == "random":
        print "random"
        i = 0
        global total
        while i < 10:
            i = i + 1
            frame = ""
            # generate a random length frame
            frameLen = random.randint(3, 255)
            while not frameLen == 0:
                #only use char that can be displayed
                byte = random.randint(33, 125)
                sr.write(chr(byte))
                frame = frame + chr(byte)
                frameLen = frameLen - 1
            print "\nsend:", len(frame), "bytes:\n", frame
            total = total + len(frame)
            time.sleep(0.1)
        print "\ntotal bytes sent:", total
    else:
        inFile = open(filePath, 'r')
        try:
            for line in inFile.readline():
                sr.write(line)
                total = total + len(line)
                print "sent", line.count(), "bytes"
                print "frame was:", line, "\n"
            print "\ntotal bytes sent:", total
        finally:
            inFile.close()

def readBytes(sr):
    print "Exit when hit 'CTRL+C'"
    #check if got the sync frame (0xff 0xff 0xFF)
    sync = False
    byte = ''
    while not sync:
        byte = sr.read(1)
        if int(ord(byte)) == 255:
            byte = sr.read(1)
            if int(ord(byte)) == 255:
                byte = sr.read(1)
                if int(ord(byte)) == 255:
                    print "Got Sync frame, now sniffing :D\n"
                    sync = True
    #now read all data from USART by 3 bytes pack
    frame = ""
    global total
    #test = ""
    while 1:
        frame = sr.read(3)
        print "got frame:", hex(ord(frame[0])), hex(ord(frame[1])), hex(ord(frame[2]))
        print "     data:", frame[1], "from:", frame[0], "time:", int(ord(frame[2]))
        total = total + 1
        #test = sr.read(3)
        #print "     indic: ", hex(ord(test[0])), hex(ord(test[1])), hex(ord(test[2]))

#read command line arguments
parser = OptionParser();
parser.set_defaults(write=False)
parser.set_defaults(baud=9600);
parser.add_option("-w", "--write", action="store_true", help="use it for write frames", dest="write")
parser.add_option("-p", "--port", help="Serial port", dest="port")
parser.add_option("-b", "--baud", type="int", help="Serial baudrate", dest="baud")
parser.add_option("-f", "--file", help="input file with generated frames ('random' to generate random frames)", dest="file")
(options, args) = parser.parse_args()
 
#init logging
#logging.basicConfig()
#logger = logging.getLogger()
#logger.setLevel(logging.INFO)

if __name__ == "__main__":
    comPort = serial.Serial(port=options.port, baudrate=options.baud)
    try:
        if options.write:
            #send sync byte
            comPort.write(chr(255))
            if options.file == None:
                print "write bytes"
                sendPerByte(comPort)
            else:
                print "write file"
                sendPerFile(comPort, options.file)
        else:
            print "read bytes"
            readBytes(comPort)
    except serial.SerialException:
        print "error pyserial"
    except KeyboardInterrupt:
        if not options.write:
            print "\ngot a total of", total, "data byte"
        print "END"
    finally:
        comPort.close()
