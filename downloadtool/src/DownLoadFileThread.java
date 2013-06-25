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

public class DownLoadFileThread extends Thread {
	private JProgressBar jProgressBar;
	String URL;
	JTextArea textArea = new JTextArea();
	//start and end position for current child thread
	long startPos;
	long endPos;
	int threadID;
	//allow mutilthreads read/write
	RandomAccessFile file;
	//how many finished
	private int readPos = 0;
	//everytime read block size
	private int inc = 0;

	public DownLoadFileThread(String URL, long startPos, long endPos, int id,
			JTextArea textArea, JProgressBar jProgressBar, String diskPath)
			throws IOException {
		this.jProgressBar = jProgressBar;
		this.URL = URL;
		this.startPos = startPos;
		this.endPos = endPos;
		this.threadID = id;
		this.textArea = textArea;
		file = new RandomAccessFile(diskPath, "rw");
		file.seek(startPos);
	}

	public void run() {
		
		try {
			URL url = new URL(URL);
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			String sProperty = "bytes=" + startPos + "-";
			//using net.httpURLConnection to set property
			httpConnection.setRequestProperty("RANGE", sProperty);
			// System.out.println("Thread no."+threadID+" is now downloading...");
			textArea.append("\nThread" + threadID + " is now downloading");
			InputStream input = httpConnection.getInputStream();
			byte[] buf = new byte[1024];
			//read 1024b everytime
			int offset;
			offset = (int) endPos - (int) startPos;
			if (offset > 1024)
				offset = 1024;
			while ((inc = input.read(buf, 0, offset)) > 0 && startPos < endPos) {
				//last part of file < 1024
				if (((int) endPos - (int) startPos) < 1024)
					inc = (int) endPos - (int) startPos;
				/*
				 * if(inc<1024){
				 * System.out.println("inc is :"+inc+" -- startpos is "+startPos);
				 * }
				 */
				readPos += inc;
				// System.out.println("read "+inc+" bytes");
				textArea.append("\nThread" + threadID + " reads " + readPos
						+ "bytes, start is :" + startPos + " and end is:"
						+ endPos);
				offset = (int) endPos - (int) startPos;
				if (offset > 1024)
					offset = 1024;
				// System.out.println("threadID: "+threadID+" started: "+startPos+" offset: "+offset);
				//write to file
				file.write(buf, 0, inc);
				startPos += inc;

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
