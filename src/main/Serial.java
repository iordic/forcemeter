package main;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class Serial {
	SerialPort serialPort;
	ArrayList <CommPortIdentifier> ports;
	BufferedReader input;
	public static final int TIME_OUT = 2000;
	public static final int DATA_RATE = 9600;
	int rawValue, forceValue;
	
	public Serial() {
		searchPorts();
	}
	
	/**
	 * Busca los puertos disponibles y los devuelve en un array.
	 * @return puertos disponibles.
	 */
	public void searchPorts() {
		ArrayList<CommPortIdentifier> portsFound = new ArrayList<CommPortIdentifier>();
        @SuppressWarnings("unchecked")	// Esto daba la brasa
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            portsFound.add(portIdentifier);
        } 
        ports = portsFound;
    }
	
	public ArrayList<CommPortIdentifier> getPorts() {
		return ports;
	}
	
	public String [] getPortNames() {
		String [] portNames = new String[ports.size()];	// Creamos un array que contendrá los nombres de los puertos
		for (int i = 0; i < portNames.length; i++) {
			if (ports.get(i).getPortType() == CommPortIdentifier.PORT_SERIAL) {	// Solo queremos listar puertos serie
				portNames[i] = ports.get(i).getName();
			}
		}	
		return portNames;
	}
}
