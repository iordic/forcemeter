package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.jfree.data.time.Millisecond;
import org.json.JSONObject;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * Controller class
 * @author Jordi Castelló
 *
 */
public class Forcemeter implements ActionListener, SerialPortEventListener {
	int rawValue, forceValue;
	Chart chart;
	Serial serial;
	GUI window;
	
	public Forcemeter() {
		chart = new Chart();
		serial = new Serial();
		window = new GUI(chart.getChart());
		window.connectBtn.addActionListener(this);
		window.refreshPortsBtn.addActionListener(this);
		fillPortList();		
	}
	
	/**
	 * Fill combobox list of available ports. If contains values, empty first
	 */
	private void fillPortList() {
		String [] portNames = serial.getPortNames();
		window.portsList.removeAllItems();
		for(int i = 0; i < portNames.length; i++) {
			window.portsList.addItem(portNames[i]);
		}
		try {
			window.connectBtn.setEnabled(true);
			window.portsList.setSelectedIndex(0);
			window.connectBtn.setText("Connect");
			window.portsList.setEnabled(true);
		}
		catch (IllegalArgumentException e) {
			window.connectBtn.setText("Any port found");
			window.portsList.setEnabled(false);
			window.connectBtn.setEnabled(false);
		}
	}
	
	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = serial.input.readLine();
				JSONObject values = new JSONObject(inputLine);	// JSON decode info
				rawValue = values.getInt("raw");
				forceValue = values.getInt("force");
				this.chart.timeSeriesCollection.getSeries(0).add(new Millisecond(),forceValue);			
				DecimalFormat fDec = new DecimalFormat("#.##");	// Output decimal format				
				window.forceLabel.setText("Force: " + forceValue + " N");
				window.voltageLabel.setText("Voltage: " + fDec.format(((float) rawValue * 0.0048828125F)) + " V");
				window.rawLabel.setText("Raw Value: " + rawValue);
			} catch (Exception e) {
				//System.out.println(e); // Annoying in console
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("connect")){
			initialize(serial.ports.get(window.portsList.getSelectedIndex()));
			window.portsList.setEnabled(false);
			window.refreshPortsBtn.setEnabled(false);
			window.connectBtn.setText("Disconnect");
			window.connectBtn.setActionCommand("disconnect");
			return;
		}
		if(event.getActionCommand().equals("disconnect")){
			close();
			window.portsList.setEnabled(true);
			window.refreshPortsBtn.setEnabled(true);
			window.connectBtn.setText("Connect");
			window.connectBtn.setActionCommand("connect");
			return;
		}
		if(event.getSource() == window.refreshPortsBtn) {
			serial.searchPorts();
			fillPortList();
			return;
		}
	}

	/**
	 * Initializes port passed by parameter
	 * @param portId id of port to initialize
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
	 * Close connection with the port (recommended in linux systems)
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
