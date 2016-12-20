# Forcemeter
A java & arduino tool to measure the force applied in a sensor (FSR).
![Formeter windows](https://bitbucket.org/iordic/forcemeter/raw/master/screenshots/connected_small.png)
## Required Java libraries
* **RXTX**, library for serial comunication.
* **JFreeChart**, library for graph drawing.
* **JCommon**, JFreeChart need it for working.

## Installation steps
1. Download RXTX binaries (You can download it from [here](http://rxtx.qbang.org/wiki/index.php/Download)).
2. Download JFreeChart & JCommon libraries from its [sourceforge](https://sourceforge.net/projects/jfreechart/files/).
3. When you download the binary package, you have to copy "**rxtxSerial.dll**" file to "*%PROGRAMFILES%\\Java\\jre.x.x_xxx\\bin*".
4. The project was made with eclipse. You have to import with eclipse and then import "**RXTXcomm.jar**" as an external jar file. (Also in RXTX downloaded package)
5. Finally, import the JFreeChart & JCommon needed libraries as an external jar files too.
## Required Hardware
* **Arduino board** (this project was implemented in *Mini Pro*).
* If arduino board doesn't have, an **usb-to-ttl** serial adapter.
* **16x2 LCD** Screen (optionally **LCD-I2C** adapter, less cables, there are two codes for each one).
* **FSR** Sensor (in this project we have used an Interlink 402 round from adafruit).
* Buttons, cables, case, etc...
