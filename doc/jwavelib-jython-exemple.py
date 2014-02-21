#!/usr/bin/env jython

#====== CONFIG ==================================

jwavelibPath    = './jwavelib.jar'
rxtxPath        = '/usr/share/java/RXTXcomm.jar'

#================================================

import sys
import java

# add needed java .jar
sys.path.append(rxtxPath)
sys.path.append(jwavelibPath)

# then import needed stuff
from com.coronis.modules.platform import RxTxSerialWavePort
from com.coronis.modules import WaveFlow
from com.coronis.logging import BasicLogger
from com.coronis import Config
from com.coronis import CoronisLib

def main():
    # setup logger
    logger = BasicLogger()
    Config.setLogger(logger)


    try:
        # setup waveport then connect
        wp = RxTxSerialWavePort('test', '/dev/ttyUSB0')
        wp.connect

        if wp.checkConnection:
            logger.log('Waveport connection OK')

            # create a new module (WaveFlow)
            # then ask it's firmware and dataLogging table
            wfl =  WaveFlow(CoronisLib.moduleIdFromString('051606304A44'), wp, None)
            logger.log(wfl.getFirmware())
            logger.log(wfl.getDatalog())
        else:
            logger.log('Waveport connection FAILED')

    except Exception, e:
        logger.error("Error during test");
        e.printStackTrace()
    finally:
        wp.disconnect()

if __name__ == "__main__":
    main()

