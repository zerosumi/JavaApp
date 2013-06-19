import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LownLoadFileThread extends Thread {
	private JProgressBar jProgressBar;
	String URL;
	long startPos;
	long endPos;
	int threadID;
	JTextArea textArea = new JTextArea();
	RandomAccessFile file;
	private int readPos = 0;
	private int in = 0;

	public LownLoadFileThread(String URL, long startPos, long endPos, int id,
			JTextArea textArea, JProgressBar jProgressBar, String saveURL)
			throws IOException {
		this.jProgressBar = jProgressBar;
		this.URL = URL;
		this.startPos = startPos;
		this.endPos = endPos;
		this.threadID = id;
		this.textArea = textArea;
		file = new RandomAccessFile(saveURL, "rw");
		file.seek(startPos);
	}

	public void run() {
		// DownLoad download = new DownLoad();
		try {
			URL url = new URL(URL);
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			String sProperty = "bytes=" + startPos + "-";
			httpConnection.setRequestProperty("RANGE", sProperty);
			// System.out.println("Thread no."+threadID+" is now downloading...");
			textArea.append("\nThread" + threadID + " is now downloading");
			InputStream input = httpConnection.getInputStream();
			byte[] buf = new byte[1024];
			int offset;
			offset = (int) endPos - (int) startPos;
			if (offset > 1024)
				offset = 1024;
			while ((in = input.read(buf, 0, offset)) > 0 && startPos < endPos) {
				if (((int) endPos - (int) startPos) < 1024)
					in = (int) endPos - (int) startPos;
				/*
				 * if(in<1024){
				 * System.out.println("in is :"+in+" -- startpos is "+startPos);
				 * }
				 */
				readPos += in;
				// System.out.println("read "+in+" bytes");
				textArea.append("\nThread" + threadID + " reads " + readPos
						+ "bytes, start is :" + startPos + " and end is:"
						+ endPos);
				offset = (int) endPos - (int) startPos;
				if (offset > 1024)
					offset = 1024;
				// System.out.println("threadID: "+threadID+" started: "+startPos+" offset: "+offset);
				file.write(buf, 0, in);
				startPos += in;

			}
			// System.out.println("thread "+threadID+" complete");

			textArea.append("\nThread" + threadID + " complete");
			file.close();
			input.close();
		} catch (Exception ex) {

		}
	}

	public int getReadPos() {
		return readPos;
	}

	public void setReadPos(int readPos) {
		this.readPos = readPos;
	}

}
