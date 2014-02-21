Technical overview
******************

This chapter will give an overview of the JWaveLib architecture and implementation.


History
=======

The JWaveLib has originally been developped to be integrated into embedded
Java hardware. The first running implentation was made for ACTL eWON's in 2007.
The application was designed to do some data acquisition at a given frequency.
Thus, the main development were focused on datalogging access. The library is
now opening to support all the types of communication a user can think about
using Coronis devices.

The JWaveLib is now running on different architectures from classic J2SE
environements to embedded platforms like the ACTL eWON or the Siemens TC65.

Architectural details
=====================

The JWaveLib hides all of the Wavenis protocol behind easy to use objects
(WavePort, WaveTherm, etc.). A rich set of exceptions are used through the
library allowing the user to catch and understand what the problem is.

Using the library is limited to declaring a WavePort instance, connecting them
to Modules (any Coronis module) and then using those modules. Depending on the
module type, a different set of methods are available.

If needed, existing module types can be extended to implement specific
functionalities.

WavePort
--------

The **com.coronis.module.WavePort** class is the central point. It is in charge
of doing all the job of talking and listening to an RS-232 connected WavePort.
It offers the needed methods to do point-to-point or multiframe requests. Some
broadcasting queries have already been tested but not officialy implemented in
the library.

This class is abstract because dependant on the hardware.

Module
------

The **com.coronis.module.Module** class is the parent of all the Coronis modules
(WaveTalk or DataloggingModule). This class contains all the methods shared by
the different Coronis modules (get/set firmware, get type, get/set datetime,
etc.).

A Module has also references to a list of repeaters if any. This list of
repeaters will automatically be used to optimize the type of request between the
WavePort and the Module (PTP, Repeated or Multiframe).

DataLoggingModule
-----------------

The **com.coronis.module.DataLoggingModule** class allow management of modules that have datalogging tables (simple or extended). 

The DataLoggingModule class is abstract as the methods reading the data from
the modules are module specific (e.g. WaveFlow stores the index information on
4 bytes while the WaveTherm Dallas stores information on 2 bytes). The specific
implementation are done inside the children classes :

* **com.coronis.module.WaveTherm**, 
* **com.coronis.module.WaveFlow**, 
* **com.coronis.module.WaveTank**
* **com.coronis.module.WaveSense**

When retrieving data from the sensors, the methods returns
**com.dipole.libs.DataSet** objects that holds the retrieved information. a
DataSet is a list of **com.dipole.libs.Measure** objects and some metadata
about them (last call date, ...).

This class is the primary input point when the user would probably need to implement specific behaviour dependant on their need.

Library packages
================

This section will describe each package of the library and will give a brief
overview of their content and use. For a more detailed information, please
refer to the javadoc.

The list of packages in the jwavelib.jar library :

com.coronis
-----------

General package holding classes shared all over the library


com.coronis.exceptions
----------------------

Exception package grouping a nice set of exception classes. The base class of all the exceptions is **CoronisException**.

com.coronis.frames
------------------

Every incoming and outgoing Coronis frames are managed by this package. A factory frame builder (**CoronisFrameBuilder**) and a state machine frame reader (**CoronisFrameReader**) can read or produce any of the Coronis frames existing in this package.

com.coronis.logging
-------------------

The JWaveLib uses a logging module a la log4j but very light. Everything is based on the **SimpleLogger** interface. The **Logger** class uses only static methods and is used all over the library. By default, it uses a **StdLogger** that outputs all the logging to the standard output. Depending on the debugging configuration, the different levels of logging will print messages. The **frame()** method allows logging of Coronis frames.

com.coronis.modules
-------------------

The physical Coronis modules have a class counterpart. The base module is the **WavePort** and the connected module to the WavePort are all children of the **Module** class. The hierarch is the following :

* WavePort
* Module

 * WaveTalk
 * DataloggingModule

  * WaveTherm

   * WaveThermDallas
   * WaveThermPT100

  * WaveFlow
  * WaveSense

   * WaveSense4_20
   * WaveSense5V
   * WaveTank


com.coronis.modules.platform
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

com.coronis.modules.requests
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

com.dipole.libs
---------------

Test packages
================


com.coronis.test
----------------

Integrating JWaveLib
====================

From an integrator point of view, the JWaveLib provides easy extension points
by the use of abstract classes and the object orientation of the package :

* The hardware specific classes have all been made abstract to ensure each platform has its implentation of the classes. 
* When a user wants to implement specific methods, he can derive from existing classes to implement the new methods or overload existing ones


 

