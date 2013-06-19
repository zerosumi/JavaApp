
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;

public class crawlThread extends Thread {

	static int nRunCount = 0;
	int nID = 0;
	String strurl;
	URL url;
	URLConnection urlconnection;
	InputStream inputstream;
	BufferedReader bufreader;
	boolean bSaveToFile;
	private Date date;
	private boolean exit;

	private String[] strCNMsgOfResponse = new String[1024];
	private final int nMaxTimeOfTimeout = 15 * 60000;
	private int nTimeOfTimeout = 60000;
	private int nCountOfTimeout = 0;

	// -------------------------------------------------------
	public crawlThread() {
		initgather();
	}

	public crawlThread(String strInUrl, boolean bIn) {
		this.strurl = strInUrl;
		this.bSaveToFile = bIn;
		initgather();
	}

	public void initgather() {
		date = new Date();
		// ------------------------------------------
		int i = 0;
		for (i = 0; i < 1024; i++) {
			strCNMsgOfResponse[i] = "";
		}
		strCNMsgOfResponse[201] = "";
		strCNMsgOfResponse[202] = "202 error";
		strCNMsgOfResponse[203] = "203 error";
		strCNMsgOfResponse[204] = "204 error";
		strCNMsgOfResponse[205] = "";
		strCNMsgOfResponse[206] = "";

		strCNMsgOfResponse[300] = "";
		strCNMsgOfResponse[301] = "";
		strCNMsgOfResponse[302] = "";
		strCNMsgOfResponse[303] = "";
		strCNMsgOfResponse[304] = "";
		strCNMsgOfResponse[305] = "";
		strCNMsgOfResponse[307] = "";

		strCNMsgOfResponse[400] = "";
		strCNMsgOfResponse[401] = "";
		strCNMsgOfResponse[402] = "";
		strCNMsgOfResponse[403] = "";
		strCNMsgOfResponse[404] = "404 error";
		strCNMsgOfResponse[405] = "";
		strCNMsgOfResponse[406] = "";
		strCNMsgOfResponse[407] = "";
		strCNMsgOfResponse[408] = "";
		strCNMsgOfResponse[409] = "";
		strCNMsgOfResponse[410] = "";
		strCNMsgOfResponse[411] = "";
		strCNMsgOfResponse[412] = "";
		strCNMsgOfResponse[413] = "";
		strCNMsgOfResponse[414] = "";
		strCNMsgOfResponse[415] = "";
		strCNMsgOfResponse[416] = "";
		strCNMsgOfResponse[417] = "";

		strCNMsgOfResponse[500] = "";
		strCNMsgOfResponse[501] = "";
		strCNMsgOfResponse[502] = "";
		strCNMsgOfResponse[503] = "";
		strCNMsgOfResponse[504] = "";
		strCNMsgOfResponse[505] = "";

		strCNMsgOfResponse[999] = "999 error";
	}

	public void exitThread() {
		exit = true;
	}

	private void establishConnection() throws Exception {
		int nHttpResponseCode = -1;
		String strHttpResponse = "";
		String strResponseMsg = "";
		String strErrorMsg = "";
		String strDateString = "";
		try {
			date.getTime();
			strDateString = "[" + date.toString() + "] ";

			url = new URL(this.strurl);
			urlconnection = url.openConnection();
			urlconnection.setReadTimeout(nTimeOfTimeout);
			if (urlconnection instanceof HttpURLConnection) {
				try {
					HttpURLConnection httpurlconn = (HttpURLConnection) urlconnection;
					nHttpResponseCode = httpurlconn.getResponseCode();
					strHttpResponse = httpurlconn.getResponseMessage();

					if (nHttpResponseCode >= 1024)
						strErrorMsg = "ResponseCode > 1024[" + this.strurl
								+ "]";
					else if (nHttpResponseCode < 0)
						strErrorMsg = "ResponseCode < 0[" + this.strurl + "]";
					else
						strResponseMsg = "Response Code:[" + nHttpResponseCode
								+ "," + strHttpResponse + "] "
								+ strCNMsgOfResponse[nHttpResponseCode];
					if (nHttpResponseCode > 600)
						System.out.println(strResponseMsg + "URL:"
								+ this.strurl);
				} catch (IOException ex) {
					strErrorMsg = " IOException[" + ex + "]";
				} catch (Exception ex) {
					strErrorMsg = " Unknown Exception[" + ex + "]";
				}
			}
			if ((nHttpResponseCode == 200)
					|| (strHttpResponse.equalsIgnoreCase("ok"))) {
				// System.out.println(" Success ![" +this.strurl+"]-->nCount:" +
				// nRunCount++);
				new fileIO().writeData(urlconnection, this.strurl,
						this.bSaveToFile);
			} else {
				if (strResponseMsg.isEmpty())
					strResponseMsg = "Link Error, please check URL";
				strErrorMsg = strResponseMsg;
			}
		} catch (MalformedURLException e_url) {
			strErrorMsg = "URL error";
		} catch (ConnectException e_con) {
			strErrorMsg = "Connection error";
		} catch (UnknownHostException e_con) {
			strErrorMsg = "Unknown Host";
		} catch (SocketTimeoutException e_con) {
			strErrorMsg = "Time out";
			nCountOfTimeout++;
			if (nCountOfTimeout >= 100) {
				nCountOfTimeout = 0;
				if (nTimeOfTimeout < nMaxTimeOfTimeout) {
					nTimeOfTimeout *= 2;
					System.out.println("Times of timeout is set to be "
							+ nTimeOfTimeout);
				}
			}
		} catch (FileNotFoundException e_url) {
			strErrorMsg = "File not found";
		} catch (Exception e_url) {
			strErrorMsg = "Unknown error![" + e_url + "]";
			e_url.printStackTrace();
		}
		if (!strErrorMsg.isEmpty()) {
			strErrorMsg = strDateString + strErrorMsg;
			System.out.println("URL:" + this.strurl + strErrorMsg);
		}
	}

	public void run() {
		if (this.strurl == null || this.strurl.isEmpty())
			return;
		crawlControler.nThreadCount++;
		try {
			this.strurl = this.strurl.toLowerCase();
			if (!this.strurl.startsWith("http://")
					&& !this.strurl.startsWith("ftp://")) {
				this.strurl = "http://" + this.strurl;
			}
			this.strurl = this.strurl.replace('\\', '/');
			establishConnection();

		} catch (Exception e) {
			System.out.println("ERROR:-->" + e);
		}

		// --------------------------------------------
		finally {
			crawlControler.nThreadCount--;
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			crawlThread gg = new crawlThread();
			gg.strurl = "http://www.ivsky.com/";
			gg.bSaveToFile = false;
			gg.start();
			crawlControler.URLList.add(gg.strurl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
