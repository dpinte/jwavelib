'''
Coronis simulator to test Audiotel modems
'''
import logging
import datetime

from twisted.python import threadable
threadable.init()
from twisted.internet import reactor
from twisted.internet import task

from audiotel import AudiotelDatagramProtocol
from crncomu  import *

####################################
# MAIN VARIABLES

MODULE_IP   = '192.168.253.129'
MODULE_PORT = 8001
SERVER_PORT = 8000

WAIT_TIME = 50 

####################################


class UDPServer(AudiotelDatagramProtocol):

  stats    = dict()

  def __init__(self, sent, received):
    self.stats['ack'] = 0
    self.stats['sids'] = 0
    self.stats['received'] = 0
    self.stats['send'] = 0
    self.logger   = logging.getLogger('Listener')
    self.logger.info('Initializing the server')
    print self.stats

    AudiotelDatagramProtocol.__init__(self)

    self.received = received
    self.audiotel_ip = MODULE_IP
    self.audiotel_port = MODULE_PORT
    self.sent = sent

  def startProtocol(self):
    self.transport.connect(self.audiotel_ip, self.audiotel_port)
    self.task = task.LoopingCall(self.sendDatagram)
    self.task.start(WAIT_TIME)

  def datagramReceived(self, datagram, address):
    try:
      rtuple = self.read_datagram( datagram)
      (proto, frameid, dtg_len, dtg, crc) = rtuple
      if dtg is not None :
        self.received.append([datetime.datetime.now(), frameid, dtg])
        self.logger.debug('Reading (%s,%s,%s) from %s' % ( frameid, dtg_len, dtg, address ))
        response = self.build_ack(frameid, dtg_len)
        self.transport.write(response, address)
        self.stats['ack'] +=1
      else:
        print "Read an ACK for frameid %s" % frameid 
        # received an ack
        if self.sent.has_key(frameid):
          print "Poped framedid %s" % frameid
          self.sent.pop(frameid)
        else:
          self.stats['sids'] +=1
    except Exception, e:
      self.logger.error(e)
      raise

  def sendDatagram(self):
    module_id = '\x05\x19\x06\x30\x09\x6F'
    data = module_id + '\x01'
    crmsg = CoronisMessage(0x20, data)
    self.logger.debug('Running task - %s' % datetime.datetime.now())
    message = crmsg.get_msg()
    self.transport.write(self.build_datagram(message))
    self.sent[self.get_current_fid()] =  message
    self.logger.debug('Sent : %u - %s' % (self.get_current_fid(), message))
    self.stats['send'] += 1

class CoronisSimulator:
  '''
  Base class implenting the simulator : has a udp client and a udp server
  '''

  def __init__(self):
    '''
    Launch a udp server and a method that will randomly send queries to the 
    Audiotel modem
    '''
    self.client_port = MODULE_PORT
    self.server_port = SERVER_PORT
    self.send = {}
    self.received = []
    self.listener = UDPServer(self.send, self.received)
    reactor.listenUDP(self.server_port, self.listener) 
    reactor.run()

def run():
  logging.basicConfig()
  logger = logging.getLogger()
  logger.setLevel(logging.DEBUG)
  c = CoronisSimulator()
  reactor.run()

if __name__ == '__main__':
  run() 
