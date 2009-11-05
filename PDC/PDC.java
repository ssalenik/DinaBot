import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import lejos.pc.comm.*;
import java.io.*;

public class PDC extends JFrame implements Runnable {
	/* Protocol Constants */

	static final int ERROR = -1;

	static final int PRINT = 0;
	static final int UPDATE = 1;
	static final int PROMPT = 2;
	static final int QUERY = 3;

	static final int PROMPT_RESPONSE = 3;
	static final int QUERY_RESPONSE = 3;

	/* GUI Vars */
	
	JPanel battery_panel;
	JProgressBar battery_level;
	JTextField battery_message;

	Map odometry_map;
	
	JPanel odometry_panel;
	JTextField odometry_x;
	JTextField odometry_y;
	JTextField odometry_t;
	
	JPanel connect_panel;
	JList connect_choices;
	JButton connect;
	
	JPanel console_panel;
	JTextArea console;
	
	JPanel prompt_panel;
	JTextField prompt_message;
	JTextField prompt_response;

	JPanel query_panel;
	JTextField query_message;
	JButton confirm;
	JButton deny;
	JButton send;

	/* Vars */
	
	int battery_value;
	double x;
	double y;
	double t;
	

	/* Connection */
		
	NXTConnector connection = new NXTConnector();

	DataInputStream input_stream;
	DataOutputStream output_stream;

	public PDC(String name) {
		super(name);
		
		battery_panel = new JPanel();
		//battery_panel.setBorder(new LineBorder(Color.BLACK));
		battery_panel.setSize(150,75);
		battery_panel.setLocation(125,10);
		battery_level = new JProgressBar(0,9000);
		battery_message = new JTextField("Batter Level: N/A");
		battery_message.setEnabled(false);
		battery_panel.add(battery_level);
		battery_panel.add(battery_message);
		
		odometry_map = new Map();
		//odometry_map.setBorder(new LineBorder(Color.BLACK));
		odometry_map.setSize(400,400);
		odometry_map.setLocation(10,100);
		
		odometry_panel = new JPanel();
		//odometry_panel.setBorder(new LineBorder(Color.BLACK));
		odometry_panel.setSize(150,100);
		odometry_panel.setLocation(125,510);
		odometry_x = new JTextField("x:");
		odometry_y = new JTextField("y:");
		odometry_t = new JTextField("t:");
		odometry_x.setEnabled(false);
		odometry_y.setEnabled(false);
		odometry_t.setEnabled(false);
		odometry_panel.add(odometry_x);
		odometry_panel.add(odometry_y);
		odometry_panel.add(odometry_t);
		
		connect_panel = new JPanel();
		connect_panel.setBorder(new LineBorder(Color.BLACK));
		connect_panel.setSize(100,100);
		connect_panel.setLocation(275,10);
		connect_choices = new JList(new String[] {"asdf", "s", "fd"});
		connect = new JButton("Connect");
		connect_panel.add(connect_choices);
		connect_panel.add(connect);
		
		console_panel = new JPanel();
		//console_panel.setBorder(new LineBorder(Color.BLACK));
		console_panel.setSize(275,400);
		console_panel.setLocation(420,100);
		console = new JTextArea(24,20);
		console.setEnabled(false);
		console.setBorder(new LineBorder(Color.BLACK));
		console_panel.add(console);

		prompt_panel = new JPanel();
		prompt_panel.setBorder(new LineBorder(Color.BLACK));
		prompt_panel.setSize(275,100);
		prompt_panel.setLocation(420,510);
		prompt_message = new JTextField("Prompt Field");
		prompt_message.setMinimumSize(new Dimension(100,10));
		prompt_message.setEnabled(false);
		prompt_response = new JTextField("Prompt Answer");
		//prompt_response.setEnabled(false);
		send = new JButton("Send");
		//send.setEnabled(false);
		prompt_panel.add(prompt_message);
		prompt_panel.add(prompt_response);
		prompt_panel.add(send);
	
		query_panel = new JPanel();
		query_panel.setBorder(new LineBorder(Color.BLACK));
		query_panel.setSize(275,75);
		query_panel.setLocation(420,620);
		query_message = new JTextField("This is Query Field. Answer Yes or No?");
		query_message.setEnabled(false);
		confirm = new JButton("Yes");
		//confirm.setEnabled(false);
		deny = new JButton("No");
		//deny.setEnabled(false);
		query_panel.add(query_message);
		query_panel.add(confirm);
		query_panel.add(deny);

		this.getContentPane().setLayout(null);
		this.getContentPane().add(battery_panel);
		this.getContentPane().add(odometry_map);
		this.getContentPane().add(odometry_panel);
		//this.getContentPane().add(connect_panel);
		this.getContentPane().add(console_panel);
		this.getContentPane().add(prompt_panel);
		this.getContentPane().add(query_panel);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.exit(0);         
			}
		});
		
		connection = new NXTConnector();
		if (!connection.connectTo("TERMINATOR", "00165301741F", 2)) {
		//if (!connection.connectTo("Pr0nBOT", "00165300E8DA", 2)) {
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}
		
		input_stream = connection.getDataIn();
		output_stream = connection.getDataOut();
		
		(new Thread(this)).start();
	}

	public static void main(String args[]){
		PDC pdc = new PDC("Penultimate Debug Console");
		pdc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pdc.pack();
		pdc.setSize(new Dimension(700,900));
		pdc.setVisible(true);
	}
	
	public void run() {
		while(true) {
			try {
				int command = input_stream.readInt();
				int len = 0;
				String incomming = "";
				switch(command) {
					case PRINT:
						len = input_stream.readInt();
						for(int i = 0;i < len;i++) {
							incomming += input_stream.readChar();
						}
						console.setText(console.getText()+incomming);
						break;
					case UPDATE:
						battery_value = input_stream.readInt();
						battery_level.setValue(battery_value);
						battery_message.setText(battery_value+" mv");
						x = input_stream.readDouble();
						y = input_stream.readDouble();
						t = input_stream.readDouble();
						odometry_x.setText("x: "+x);
						odometry_y.setText("y: "+y);
						odometry_t.setText("t: "+t);
						break;
					case PROMPT:
						len = input_stream.readInt();
						for(int i = 0;i < len;i++) {
							incomming += input_stream.readChar();
						}
						prompt_message.setText(incomming);
						prompt_response.setText("");
						break;
					case QUERY:
						len = input_stream.readInt();
						for(int i = 0;i < len;i++) {
							incomming += input_stream.readChar();
						}
						query_message.setText(incomming);
						break;
				}
			} catch (IOException ioe) {
				System.err.println("IO Exception reading bytes:");
				System.err.println(ioe.getMessage());
				break;
			}
		}

		try {
			input_stream.close();
			output_stream.close();
			
			connection.close();
		} catch (IOException ioe) 	{
			System.err.println("IOException closing connections:");
			System.err.println(ioe.getMessage());
		}
		
		System.exit(0);
	}

	class Map extends JPanel {

		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Dimension d = getSize();
			BasicStroke stroke = new BasicStroke(2.0f);
		    BasicStroke wideStroke = new BasicStroke(8.0f);
		 
			g2.setColor(Color.white);
			g2.drawRect(0, 0, (int)d.getWidth(), (int)d.getHeight());
			g2.fillRect(0, 0, (int)d.getWidth(), (int)d.getHeight());
		
			g2.setColor(Color.black);
			g2.setStroke(stroke);
			for(int i = 0;i < 11;i++) {
				g2.draw(new Line2D.Double(0, d.getHeight()/12*(i+1), d.getWidth(), d.getHeight()/12*(i+1)));
				g2.draw(new Line2D.Double(d.getWidth()/12*(i+1), 0, d.getWidth()/12*(i+1), d.getHeight()));
			}
		}
	}

}