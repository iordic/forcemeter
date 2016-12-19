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
 * @author Jordi
 *
 */
public class GUI extends JFrame {
	private static final long serialVersionUID = 2035189298017959734L;
	JLabel etiPorts, etiForce, etiVoltage, etiRaw, etiRate;
	Font fontValue;
	JComboBox <String> listaPuertos;
	JButton connectBtn, refreshPortsBtn;
	ImageIcon resetIcon;
	
	public GUI(JFreeChart chart) {
		getContentPane().setLayout(null);	// Para poder ubicar los objetos en la ventana
		resetIcon = new ImageIcon(this.getClass().getResource("res/resetIcon.png"));
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setFillZoomRectangle(true);
		chartPanel.setMouseWheelEnabled(false);
		
		etiPorts = new JLabel("Available serial ports:");
		etiForce = new JLabel("Force:");
		etiVoltage = new JLabel("Voltage:");
		etiRaw = new JLabel("Raw value:");
		etiRate = new JLabel("Data rate: " + Serial.DATA_RATE + " bps");
		etiRate.setFont(new Font(Font.SERIF, Font.BOLD, 11));	// Establecemos fuente para la etiqueta
		
		fontValue = new Font(Font.SANS_SERIF, Font.BOLD, 15);	// Creamos una fuente para las etiquetas de las lecturas
		etiForce.setFont(fontValue);	// Aplicamos la fuente
		etiVoltage.setFont(fontValue);
		etiRaw.setFont(fontValue);
		
		listaPuertos = new JComboBox<String>();		
		connectBtn = new JButton("Connect");
		refreshPortsBtn = new JButton();
		refreshPortsBtn.setIcon(resetIcon);
		refreshPortsBtn.setToolTipText("Refresh list");

		// Distribución de elementos
		etiPorts.setBounds(10, 20, 130, 20);
		listaPuertos.setBounds(140, 20, 135	, 20);
		refreshPortsBtn.setBounds(280, 20, 20, 20);
		connectBtn.setBounds(310, 20, 120, 20);
		etiForce.setBounds(15, 60, 300, 20);
		etiVoltage.setBounds(15, 80, 300, 20);
		etiRaw.setBounds(15, 100, 300, 20);
		etiRate.setBounds(335, 445, 120, 20);
		chartPanel.setBounds(15, 150, 400, 300);
		// Habilitar acción y comandos
		connectBtn.setActionCommand("connect");	
		connectBtn.setEnabled(false);

		// Añadir elementos a la ventana (para que se carguen y visualicen)
		add(etiPorts);
		add(listaPuertos);
		add(connectBtn);
		add(refreshPortsBtn);
		add(etiForce);
		add(etiVoltage);
		add(etiRaw);
		add(etiRate);
		add(chartPanel);
		// Parámetros de la ventana
		setTitle("Forcemeter");
		setSize(450, 500);
		centerFrame();
		setVisible(true);
		setResizable(false);
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);	// Para cerrar el proceso al cerrar la ventana.
	}
	
	/**
	 * Centrar la ventana en la pantalla. 
	 */
	private void centerFrame() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();	// Dimensiones en pixels de la pantalla
        Dimension ventana = getSize();										// Dimensiones en pixels de la ventana      
        setLocation((pantalla.width - ventana.width) / 2, (pantalla.height - ventana.height) / 2);	// Cálculo para centrar la ventana
	}
}
