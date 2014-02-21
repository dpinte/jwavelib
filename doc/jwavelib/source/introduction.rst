Introduction
************

JWaveLib offers a simple middleware to interface Coronis [#]_ sensors using the Wavenis© protocol. It allows easy communication between any Java enabled device and a network of Wavenis© sensor modules using the Coronis Systems products.

JWaveLib focuses on integrator needs by decreasing their development time, assuring code quality and fully tested Wavenis© protocol implementation. JWaveLib respects the ultra-low power philosophy of the Wavenis© modules by limiting to the strict minimum the number of requests between the WavePort and the network of modules.

Features
========

Wavenis protocol and Coronis modules
------------------------------------

* Full Coronis sensor integration managing alarms, datalogging, extended datalogging, etc. for :

 * Wavetherm Dallas (1 sensor)
 * Waveflow  (1 sensor)
 * Wavesense (4-20mA, 0-5V, Wavetank)

* Partial support for WaveTherm Pt100/Pt1000

* Facility functions like getAdvancedDatalog(), startDatalog(), etc.
* Minimal communication between the Waveport and the modules by using the most appropriate communication mode (PTP or MULTIFRAME)

Q&A
---

* Fully unit tested and complete javadoc available
* Javadoc and class diagram can be downloaded from the Download section of our website.

Integration
-----------

* Object-oriented architecture allowing easy integration with custom applications
* Simple customization of the library’s behaviour by configuration files and abstract classes.
* Library size is less than 60 Kb

J2ME
^^^^

* Java J2ME CLDC 1.1 compliant. The library can be used in most embedded  Java platforms but also with the latest Java Runtime Environment.      
* Integrated platforms : 

    * Siemens TC65, 
    * ACTL eWON’s 4101 GPRS

J2SE
^^^^

* Support multiple RS-232 Waveport and unlimited numbers of sensors.
* Officially supported J2SE Virtual Machines : 

    * Sun JDK >= 1.3, 
    * OpenJDK >= 1.6 
    * with Java Communication API 3.0 or RXTX >= 2.1.7



How does it work
================

The JWaveLib hides all the Wavenis protocol complexity behing simple objects
and data structures. 

.. image:: images/schema.png 
      :align: center

The following snippet shows how easy it can be ::
     
    import com.coronis.modules.platform.SerialWavePort;
    import com.coronis.modules.WaveFlow;
    import com.coronis.modules.WavePort;
    import com.coronis.modules.WaveTherm;
    // initialise Waveport
    WavePort wpt = new SerialWavePort(“waveport1”, “/dev/ttyUSB0”);
    // initialise a Wavetherm using no Wavetalk
    JWaveLib WaveTherm wth1 = new WaveTherm(“031907301989”, wpt, null);
    int temperature = wth1.getCurrentValue();
    // initialise Waveflow using no Wavetalk
    WaveFlow wfl1 = new WaveFlow(“021607314289”, wpt, null);
    int index = wfl1.getCurrentValue();
    DataSet dst = wfl1.getDailyData();

.. rubric:: Footnotes

.. [#] `Coronis website <http://www.coronis.com>`_ 
