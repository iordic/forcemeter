package main;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

/**
 * IO Serial ports
 * @author Jordi Castelló
 *
 */
public class Serial {
	SerialPort serialPort;
	ArrayList <CommPortIdentifier> ports;
	BufferedReader input;
	public static final int TIME_OUT = 2000;
	public static final int DATA_RATE = 9600;
	int rawValue, forceValue;
	
	public Serial() {
		searchPorts();	// Fill ports array
	}
	
	/**
	 * Search available ports and fills array with them.
	 */
	public void searchPorts() {
		ArrayList<CommPortIdentifier> portsFound = new ArrayList<CommPortIdentifier>();
        @SuppressWarnings("unchecked")	// Annoying, supressed warning
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            portsFound.add(portIdentifier);
        } 
        ports = portsFound;
    }
	
	/**
	 * Return the array that contains the available ports.
	 * @return list of available ports
	 */
	public ArrayList<CommPortIdentifier> getPorts() {
		return ports;
	}
	
	/**
	 * Extract names of available ports from ports array and puts into another array.
	 * @return array that contain all names of available ports
	 */
	public String [] getPortNames() {
		String [] portNames = new String[ports.size()];		// This array will contain the name of available ports.
		for (int i = 0; i < portNames.length; i++) {
			if (ports.get(i).getPortType() == CommPortIdentifier.PORT_SERIAL) {	
				portNames[i] = ports.get(i).getName();		// Get only names of serial ports
			}
		}	
		return portNames;
	}
}
