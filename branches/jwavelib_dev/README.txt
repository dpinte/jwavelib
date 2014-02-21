README for JWaveLib:
=====================

1) requirement:
----------------
  Here is the list of all needed library and tools:
  
  - The Sun JDK of OpenJDK
  - RxTx
  - Ant 1.7 (with junit task)
  - JUnit4   => optional
  - proguard => optional
  
  1.1) installing on Debian:
  ---------------------------
    Here is the list of .deb needed:
    
      - librxtx-java
      - ant
      - ant-optional
      - junit4
      - proguard
      
2) HowTo build the JWaveLib:
-----------------------------

   2.1) setup environment:
   -------------------------
     edit the build.xml and chane the folowing var to your need:
     
       - rxtx     : path to the RXTXcomm.jar 
                        (default: /usr/share/java/RXTXcomm.jar)
       - junit    : path to the junit4.jar
                        (default: /usr/share/java/junit4.jar)
       - proguard : path to the ant-progard.jar
                        (linux default: /usr/share/java/ant-proguard.java) 
       
   2.2) create a .jar (default task):
   -----------------------------------
      to compile and create the .jar:
      
        ant dist
        
   2.3) running test:
   -------------------
     to run the test with junit4:
     
       ant run-test
   
   2.4) obfuscate:
   -----------------
     to obfuscate the .jar with proguard:
     
       ant obfuscate
       
   2.5) generate doc:
   -------------------
     to generate the javadoc for the complete library:
     
        ant javadoc
        
      to generate the javadoc for the API version of the library:
      
        ant javadoc-api
 