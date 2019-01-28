package windowManager;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import javax.swing.JRadioButton;
import java.awt.SystemColor;
import java.awt.Window;

import javax.swing.JProgressBar;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.event.ChangeListener;
import javax.xml.ws.Service;
import javax.swing.event.ChangeEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Window.Type;
import java.awt.BorderLayout;
import java.awt.Font;

public class RnLog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<Spectra> spectraList = new ArrayList<Spectra>();
	//the currently selected spectrum of the list
	public int selectedSpecIdx = 0;
	public Spectra RefSpec;
	private JTable table;
	public static String SoftwareVersion = "AutoRnLog 2.0";
	public JProgressBar progressBar;
	public JLabel lblProgress1;
	public JLabel lblProgress2;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RnLog frame = new RnLog();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RnLog() {
		getContentPane().setBackground(Color.WHITE);
		setResizable(false);
		setBackground(Color.RED);
		
		//load *.ini file
		//iniFile ini = new iniFile();
		
		setTitle(SoftwareVersion);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 466, 142);
		getContentPane().setLayout(null);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setForeground(Color.GREEN);
		progressBar.setBackground(Color.WHITE);
		progressBar.setBounds(62, 64, 351, 14);
		getContentPane().add(progressBar);
		
		JLabel lblProgress1 = new JLabel("Starting");
		lblProgress1.setHorizontalAlignment(SwingConstants.CENTER);
		lblProgress1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblProgress1.setBounds(10, 11, 440, 25);
		getContentPane().add(lblProgress1);
		
		JLabel lblProgress2 = new JLabel("");
		lblProgress2.setHorizontalAlignment(SwingConstants.CENTER);
		lblProgress2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblProgress2.setBounds(10, 39, 440, 25);
		getContentPane().add(lblProgress2);
		
		XYSeries series = new XYSeries("");
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		series.add(0.0, 0.0);
        series.add(128.0, 50.0);
        
		JFreeChart chart = ChartFactory.createXYLineChart("", "" /*x-axis label*/, "" /*y-axis label*/, dataset);
		chart.removeLegend();
		chart.setTitle("");
		chart.getPlot().setBackgroundPaint( Color.WHITE );
		
		//opening a thread to make the rest, otherwise the GUI would be blocked
		Thread t1 = new Thread(new ComputationThread (lblProgress1, lblProgress2, progressBar));
		t1.start();
		
	}


}


