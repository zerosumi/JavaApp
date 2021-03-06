import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class DownLoadFile extends Thread {
	//display
	private JPanel progressPane;
	private String URL;
	private String diskPath;
	String info = new String();
	JTextArea textArea = new JTextArea();
	JProgressBar jProgressBar;
	//threads #
	int nthread;
	//every child threads range
	long[] startPos;
	long[] endPos;
	long fileLength;
	//child threads
	DownLoadFileThread[] downFileSplitter;
	
	Timer timer;

	public DownLoadFile(String URL, String diskPath, JTextArea textArea,
			int nthread, JProgressBar jProgressBar) {
		this.jProgressBar = jProgressBar;
		this.URL = URL;
		this.diskPath = diskPath;
		this.nthread = nthread;
		this.startPos = new long[nthread];
		this.endPos = new long[nthread];
		this.textArea = textArea;
	}

	public void run() {
		
		info = "Target:" + URL;
		textArea.append("\n" + info);
		info = "\n# of Threads:" + nthread;
		textArea.append("\n" + info);
		try {
			//get size first, then slice to child threads
			fileLength = getFileSize(URL);
			// System.out.println("fileLength is :"+fileLength);
			if (fileLength == -1) {
				textArea.append("\nUnknown file length, please retry!");
			} else {
				if (fileLength == -2) {
					textArea.append("\nCannot access target file, please retry!");
				} else {
					//slice file
					for (int i = 0; i < startPos.length; i++)
						startPos[i] = (long) (i * (fileLength / startPos.length));
					for (int i = 0; i < endPos.length - 1; i++)
						endPos[i] = startPos[i + 1];
					endPos[endPos.length - 1] = fileLength;
					for (int i = 0; i < startPos.length; i++) {
						info = "Thread" + i + " download range: " + startPos[i]
								+ "--" + endPos[i];
						// System.out.println(info);
						textArea.append("\n" + info);
					}
					downFileSplitter = new DownLoadFileThread[startPos.length];
					jProgressBar.setMaximum(100);
					jProgressBar.setMinimum(0);
					// jProgressBar.isStringPainted();
					jProgressBar.setStringPainted(true);
					jProgressBar.setString("0%");
					// progressPane.add(jProgressBar);

					//child threads start
					for (int i = 0; i < startPos.length; i++) {
						downFileSplitter[i] = new DownLoadFileThread(URL,
								startPos[i], endPos[i], i, textArea,
								jProgressBar, diskPath);
						info = "Thread" + i + " starts";
						textArea.append("\n" + info);
						downFileSplitter[i].start();
						// System.out.println(info);
					}
					timer = new Timer(100, new ActionListener() {
						//listen until finished, meanwhile update jprogressbar
						//seems not efficient cuz every time should recount readdata, not by increment
						public void actionPerformed(ActionEvent arg0) {
							int readTotal = 0;
							boolean finished = true;
							for (int i = 0; i < startPos.length; i++) {
								if (downFileSplitter[i].isAlive())
									finished = false;

								readTotal += downFileSplitter[i].getReadPos();
								// System.out.println("readTotal is :"+readTotal);

							}
							jProgressBar
									.setValue((int) ((long) (readTotal) * 100f / fileLength));
							jProgressBar
									.setString((int) ((long) (readTotal) * 100f / fileLength)
											+ "%");

							if (finished) {
								if ((long) readTotal == fileLength)
									JOptionPane.showMessageDialog(null,
											"Complete!!!");
								else
									JOptionPane.showMessageDialog(null,
											"Fail download!!!");
								timer.stop();
							}
						}

					});
					timer.start();

				}
			}
		} catch (Exception ex) {

		}
	}

	//shutdown, kill childthreads first
	public void shutdown() {
		for (int i = 0; i < startPos.length; i++) {
			downFileSplitter[i].stop();
		}
		this.stop();
	}

	//to get size from header
	public long getFileSize(String URL) {
		int fileLength = -1;
		try {
			URL url = new URL(URL);
			HttpURLConnection httpConnection = (HttpURLConnection) (url
					.openConnection());
			int responseCode = httpConnection.getResponseCode();
			if (responseCode >= 400) {
				System.out.println("Web server doesnot response");
				return -2;// Webserver donot response
			}
			String sHeader;
			for (int i = 1;; i++)// get file length
			{
				sHeader = httpConnection.getHeaderFieldKey(i);
				if (sHeader != null) {
					if (sHeader.equals("Content-Length")) {
						//int length = httpConnection.getContentLength();
						//textArea.append("\nFile length:---" + length);
						fileLength = Integer.parseInt(httpConnection
								.getHeaderField(sHeader));
						textArea.append("\nFile length:" + fileLength);
						break;
					}
				} else {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fileLength;

	}
}