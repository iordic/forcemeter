package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.jfree.data.time.Millisecond;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class Forcemeter implements ActionListener, SerialPortEventListener {
	int rawValue, forceValue;
	Chart chart;
	Serial serial;
	GUI ventana;
	
	public Forcemeter() {
		chart = new Chart();
		serial = new Serial();
		ventana = new GUI(chart.getChart());
		ventana.connectBtn.addActionListener(this);
		ventana.refreshPortsBtn.addActionListener(this);
		fillPortList();		
	}
	
	private void fillPortList() {
		String [] portNames = serial.getPortNames();
		ventana.listaPuertos.removeAllItems();
		for(int i = 0; i < portNames.length; i++) {
			ventana.listaPuertos.addItem(portNames[i]);
		}
		try {
			ventana.connectBtn.setEnabled(true);
			ventana.listaPuertos.setSelectedIndex(0);
			ventana.connectBtn.setText("Connect");
			ventana.listaPuertos.setEnabled(true);
		}
		catch (IllegalArgumentException e) {
			ventana.connectBtn.setText("Any port found");
			ventana.listaPuertos.setEnabled(false);
			ventana.connectBtn.setEnabled(false);
		}
	}
	
	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = serial.input.readLine();
				if(inputLine.charAt(0) == 'R'){
					rawValue = Integer.parseInt(inputLine.substring(1));
				}
				if(inputLine.charAt(0) == 'F'){
					forceValue = Integer.parseInt(inputLine.substring(1));
					this.chart.timeSeriesCollection.getSeries(0).add(new Millisecond(),forceValue);
				}
				
				DecimalFormat fDec = new DecimalFormat("#.##");	// Formato de salida del número decimal (2 decimales).				
				ventana.etiForce.setText("Force: " + forceValue + " N");
				ventana.etiVoltage.setText("Voltage: " + fDec.format(((float) rawValue * 0.0048828125F)) + " V");
				ventana.etiRaw.setText("Raw Value: " + rawValue);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("connect")){
			initialize(serial.ports.get(ventana.listaPuertos.getSelectedIndex()));
			ventana.listaPuertos.setEnabled(false);
			ventana.refreshPortsBtn.setEnabled(false);
			ventana.connectBtn.setText("Disconnect");
			ventana.connectBtn.setActionCommand("disconnect");
			return;
		}
		if(event.getActionCommand().equals("disconnect")){
			close();
			ventana.listaPuertos.setEnabled(true);
			ventana.refreshPortsBtn.setEnabled(true);
			ventana.connectBtn.setText("Connect");
			ventana.connectBtn.setActionCommand("connect");
			return;
		}
		if(event.getSource() == ventana.refreshPortsBtn) {
			serial.searchPorts();
			fillPortList();
			return;
		}
	}

	/**
	 * Inicializa la conexión con el puerto pasado por parámetro.
	 * @param portId Identificador del puerto que queremos inicializar.
	 */
	public BufferedReader initialize(CommPortIdentifier portId) {
		try {
			// open serial port, and use class name for the appName.
			serial.serialPort = (SerialPort) portId.open(this.getClass().getName(), Serial.TIME_OUT);
			// set port parameters
			serial.serialPort.setSerialPortParams(
					Serial.DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			// open the streams
			serial.input = new BufferedReader(new InputStreamReader(serial.serialPort.getInputStream()));
			serial.serialPort.addEventListener(this);
			serial.serialPort.notifyOnDataAvailable(true);
			return serial.input;
		} 
		catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
	}
	
	/**
	 * Cierra la conexión con el puerto (recomendado en sistemas linux).
	 */
	public synchronized void close() {
		if (serial.serialPort != null) {
			serial.serialPort.removeEventListener();
			serial.serialPort.close();
		}
	}
	
	public static void main(String[] args) {
		new Forcemeter();
	}
}
