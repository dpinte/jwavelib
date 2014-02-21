Implementing the JWaveLib
*************************

This chapter will list the implementation of the JWaveLib into different
architectures.

Java 2 Standard Edition
=======================

The JWaveLib is also supported by standard Java runtime environment. Using the
Java Communication API or the RXTX library, it can very easily be integrated in
any application to offer the full access to the Coronis modules. 

ACTL eWON implementation
========================

This is the original development platform of the JWaveLib and as thus been
thorougly tested. This is the reference implementation of the JWaveLib.

The eWON SDK offered an easy interface to the JWaveLib. The development was mainly focused on interacting with the eWON (using the filesystem, GPRS, leds, etc.) as the JWaveLib integration was limited to implementing the WavePort abstract class for the eWON Java environment.

Further work is planned to allow more interactions with the eWON tags and other configuration.


Siemens TC65 implementation
============================

The JWaveLib implementation into the TC65 has been tested in our lab but has
not been putted under production condition. 
