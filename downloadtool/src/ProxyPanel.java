import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProxyPanel extends JFrame implements ActionListener {
	
	private String host = "000.000.000.000";
	private String port = "0000";
	private JLabel label1 = new JLabel("Proxy IP:");
	private JLabel label2 = new JLabel("Proxy Port:");
	private JTextField hosttext = new JTextField();
	private JTextField porttext = new JTextField();
	private JButton button = new JButton("Done");

	public ProxyPanel(int x, int y) {
		// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		JPanel panel = new JPanel();
		panel.setLayout(null);
		label1.setBounds(20, 20, 40, 20);
		hosttext.setBounds(60, 20, 140, 20);
		panel.add(label1, null);
		panel.add(hosttext, null);
		hosttext.setText(host);
		label2.setBounds(20, 60, 40, 20);
		porttext.setBounds(60, 60, 140, 20);
		panel.add(label2);
		panel.add(porttext);
		porttext.setText(port);
		button.setBounds(85, 100, 60, 20);
		button.addActionListener(this);
		panel.add(button);
		getContentPane().add(panel);
		setSize(new Dimension(240, 160));
		setVisible(true);
		setLocation(x, y);

	}

	public String getHost() {
		return this.host;
	}

	public String getPort() {
		return this.port;
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource() == button) {
			host = hosttext.getText();
			// System.out.println(host);
			port = porttext.getText();
			// System.out.println(port);
			System.getProperties().put("proxySet", "true");
			System.getProperties().put("proxyHost", host);
			System.getProperties().put("proxyPort", port);
			this.hide();
			this.setDefaultCloseOperation(HIDE_ON_CLOSE);

		}
	}

}
