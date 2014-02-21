#!/usr/bin/env python
# -*- coding: utf-8 -*- 
# Author:  Didrik Pinte -- <dpinte@dipole-consulting.com>
# Purpose: Monitor bytes from the serial port and print them out splitting them 
#          according to Coronis frame definition
# Created: 07/04/2008

import logging
import signal
import datetime

try:
    import serial
except ImportError:
    print '''serial_monitor depends on pyserial that seems to be unavailable. 
             See http://pyserial.wiki.sourceforge.net/pySerial to download the pySerial
             Aborting.
          '''

archive = dict()
    
class SigHandler:    
    def __init__(self, executeOnCall):
        print "Initialized"
        self.signaled = 0
        self.sn=None
        self.executeOnCall = executeOnCall
    def __call__(self, sn, sf):
        self.executeOnCall() 
        self.sn = sn 
        self.signaled += 1

def getHexaValue(bvalue):
    ''' Returns the hexadecimal string value of the input byte without
        the leading 0X and always on 2 characters
    '''
    strHexa = hex(ord(bvalue))[2:]
    return (len(strHexa) == 1) and ("0" + strHexa) or strHexa
        
        
def executeOnCall():
    # save archive to file
    f = open("dump.txt", "w")
    keys = archive.keys()
    keys.sort()
    for key in keys :
        f.write("%s\t%s\t%s\n"%  (key, ((key.minute * 60 )+ key.second) * 1E6 + key.microsecond, getHexaValue(archive[key])))
    f.close()
    logging.info("Analysing %s bits" % len(archive))
    # parse the archived bytes
    for (index, value) in enumerate(keys):
        if index > 0:
            delta = value - keys[index-1] 
            if delta > datetime.timedelta(milliseconds=10):
                logging.warn("More than 10 milliseconde between byte %d and byte %d" % (index-1, index))
                logging.warn("Diff is %s and bytes where '%s' and '%s'" %
                             (delta, getHexaValue(archive[keys[index-1]]), 
                              getHexaValue(archive[value])))
    # compute the diff time and extrac bytes with more than 10 ms between them
    # extract Coronis frames


# TODO : extract this as input parameter of the script
serial_port = '/dev/ttyS0'
#serial_port = '/dev/ttyUSB0'

# logging configuration
logging.basicConfig()
logger = logging.getLogger()
logger.setLevel(logging.INFO)


if __name__ == '__main__':
    print '''To have a valid output, please check that your serial port 
            do have the following configuration :
            
             setserial /dev/ttyS0 uart 8250 low_latency
             
             Otherwise the timings will not be correct !
             '''
    sh = SigHandler(executeOnCall)
    SIGINT_Handler = signal.signal(signal.SIGINT, sh)
    logging.info('Type Ctrl-C or Ctrl-Break to stop')
    
    # open the serial port
    s = serial.Serial(serial_port, timeout=0.001)
    s.open()
    logging.info('Serial port opened')
    ctime = datetime.datetime.now

    while not sh.signaled:
        aByte = s.read(1)    
        now = ctime()
        if aByte != '':
            archive[now] = aByte

