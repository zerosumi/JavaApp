import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextArea;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.CheckboxGroup;

public class DownLoad extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JPanel progressPane;
	private JTextField downloadURL = new JTextField();
	private JTextField savepath = new JTextField("c:\\");
	private JButton start = new JButton("Start");
	private JButton open = new JButton("Open");
	private JButton stop = new JButton("Stop");
	private JComboBox nThreadBox;
	private JCheckBox proxybutton = new JCheckBox();
	private JLabel targetLabel = new JLabel("Target");
	private JLabel saveLabel = new JLabel("Save As");
	private JLabel proxyLabel = new JLabel("Proxy Setting");
	private JLabel progessLabel = new JLabel("Progress");
	private JLabel threadsLabel = new JLabel("Threads");
	private JTextArea textArea = new JTextArea();
	private JProgressBar jProgressBar = new JProgressBar();
	private int nTread = 1;
	static String host = "";
	static String port = "";
	DownLoadFile downFile;

	public DownLoad() {
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(null);

		this.setSize(new Dimension(600, 400));
		this.setLocation(100, 100);
		this.setTitle("Mutilthread Download");

		textArea.setEnabled(false);
		textArea.setForeground(Color.black);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(new Rectangle(20, 20, 560, 170));

		targetLabel.setBounds(new Rectangle(20, 200, 120, 20));
		downloadURL.setBounds(new Rectangle(100, 200, 480, 20));
		downloadURL.setText("http://www.bdinfo.net/down/wo3G.rar");

		saveLabel.setBounds(new Rectangle(20, 240, 120, 20));
		savepath.setBounds(new Rectangle(100, 240, 400, 20));
		savepath.setText("");
		open.setBounds(520, 240, 60, 20);
		open.addActionListener(this);

		proxyLabel.setBounds(20, 280, 120, 20);
		proxybutton.setBounds(110, 280, 30, 20);
		proxybutton.addActionListener(this);

		nThreadBox = new JComboBox(new String[] { "1", "2", "3", "4", "5", "6",
				"7", "8", "9", "10" });
		threadsLabel.setBounds(170, 280, 60, 20);
		nThreadBox.setBounds(230, 280, 70, 25);
		nThreadBox.addActionListener(this);

		start.setBounds(new Rectangle(350, 280, 100, 20));
		start.addActionListener(this);
		stop.setBounds(480, 280, 100, 20);
		stop.addActionListener(this);
		stop.setEnabled(false);

		progressPane = new JPanel();
		progressPane.setBounds(20, 320, 560, 30);
		progressPane.add(progessLabel, null);
		jProgressBar.setPreferredSize(new Dimension(460, 30));
		progressPane.add(jProgressBar, null);

		contentPane.add(scrollPane, null);
		contentPane.add(downloadURL, null);
		contentPane.add(savepath, null);
		contentPane.add(targetLabel, null);
		contentPane.add(saveLabel, null);
		contentPane.add(open, null);
		contentPane.add(start, null);
		contentPane.add(stop, null);
		contentPane.add(proxyLabel, null);
		contentPane.add(proxybutton, null);
		contentPane.add(threadsLabel);
		contentPane.add(nThreadBox);
		contentPane.add(progressPane);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DownLoad download = new DownLoad();
		download.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		// TODO Auto-generated method stub
		if (e.getSource() == open) {
			JFileChooser fc = new JFileChooser();
			if (fc.showSaveDialog(this) == fc.APPROVE_OPTION) {
				File f = fc.getSelectedFile();
				savepath.setText(f.getAbsolutePath());
			}
		}
		if (e.getSource() == start) {
			textArea.setText("");
			String URL = downloadURL.getText();
			String saveURL = savepath.getText();
			if (URL.compareTo("") == 0 || saveURL.compareTo("") == 0) {
				textArea.setText("Please input URL and save location");
			} else {
				try {
					downFile = new DownLoadFile(URL, saveURL, textArea, nTread,
							jProgressBar);
					downFile.start();
					textArea.append("Main thread starts");
					stop.setEnabled(true);
					start.setEnabled(false);

				} catch (Exception ex) {
					ex.printStackTrace();
					stop.setEnabled(false);
					start.setEnabled(true);
				}

			}
		}
		if (e.getSource() == nThreadBox) {
			String item = nThreadBox.getSelectedItem().toString();
			//System.out.println("# of threads is:" + item);
			nTread = Integer.parseInt(item);
		}
		if (e.getSource() == stop) {
			stop.setEnabled(false);
			start.setEnabled(true);
			downFile.shutdown();
			textArea.append("\nStop Download");
			
		}
		if (e.getSource() == proxybutton) {
			if (proxybutton.isSelected()) {
				// proxybutton.get
				textArea.append("\nProxy is On");
				Point point = this.getLocation();
				int x = this.getHeight() / 2 + point.x;
				int y = this.getWidth() / 2 + point.y;
				ProxyPanel proxypanel = new ProxyPanel(x, y);
				/*
				 * System.getProperties().put("proxySet","true");
				 * System.getProperties().put("proxyHost",host);
				 * System.getProperties().put("proxyPort",port);
				 */
			} else {
				textArea.append("\nProxy is Off");
				System.getProperties().clear();
			}
		}
	}

}