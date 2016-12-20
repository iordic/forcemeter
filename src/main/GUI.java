package main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * View class
 * @author Jordi Castelló
 *
 */
public class GUI extends JFrame {
	private static final long serialVersionUID = 2035189298017959734L;
	JLabel portsLabel, forceLabel, voltageLabel, rawLabel, rateLabel;
	Font fontValue;
	JComboBox <String> portsList;
	JButton connectBtn, refreshPortsBtn;
	ImageIcon resetIcon;
	
	public GUI(JFreeChart chart) {
		getContentPane().setLayout(null);	// Freedom for locate the elements in the frame
		resetIcon = new ImageIcon(this.getClass().getResource("res/resetIcon.png"));
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setFillZoomRectangle(true);
		chartPanel.setMouseWheelEnabled(false);
		
		portsLabel = new JLabel("Available serial ports:");
		forceLabel = new JLabel("Force:");
		voltageLabel = new JLabel("Voltage:");
		rawLabel = new JLabel("Raw value:");
		rateLabel = new JLabel("Data rate: " + Serial.DATA_RATE + " bps");
		rateLabel.setFont(new Font(Font.SERIF, Font.BOLD, 11));	// Establish label font
		
		fontValue = new Font(Font.SANS_SERIF, Font.BOLD, 15);	// Create a font for labels that contains the values
		forceLabel.setFont(fontValue);							// Apply fonts
		voltageLabel.setFont(fontValue);
		rawLabel.setFont(fontValue);
		
		portsList = new JComboBox<String>();		
		connectBtn = new JButton("Connect");
		refreshPortsBtn = new JButton();
		refreshPortsBtn.setIcon(resetIcon);
		refreshPortsBtn.setToolTipText("Refresh list");

		// Distribution of the elements
		portsLabel.setBounds(10, 20, 130, 20);
		portsList.setBounds(140, 20, 135	, 20);
		refreshPortsBtn.setBounds(280, 20, 20, 20);
		connectBtn.setBounds(310, 20, 120, 20);
		forceLabel.setBounds(15, 60, 300, 20);
		voltageLabel.setBounds(15, 80, 300, 20);
		rawLabel.setBounds(15, 100, 300, 20);
		rateLabel.setBounds(335, 445, 120, 20);
		chartPanel.setBounds(15, 150, 400, 300);
		// Set button command and disable
		connectBtn.setActionCommand("connect");	
		connectBtn.setEnabled(false);

		// Add elements to the window
		add(portsLabel);
		add(portsList);
		add(connectBtn);
		add(refreshPortsBtn);
		add(forceLabel);
		add(voltageLabel);
		add(rawLabel);
		add(rateLabel);
		add(chartPanel);
		// Window configuration
		setTitle("Forcemeter");
		setSize(450, 500);
		centerFrame();
		setVisible(true);
		setResizable(false);
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);	// Finish program on close window
	}
	
	/**
	 * Centrar la ventana en la pantalla. 
	 */
	private void centerFrame() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();	// Screen dimension in pixels
        Dimension window = getSize();										// Windows dimension in pixels     
        setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);	// Center the frame
	}
}
