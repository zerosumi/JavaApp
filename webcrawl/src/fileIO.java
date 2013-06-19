
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

public class fileIO {
	public final int FL_50K = 50 * 1024;
	public final int FL_100K = 100 * 1024;
	public final int FL_500K = 500 * 1024;
	public final int FL_1M = 1024 * 1024;
	public final int FL_5M = 5 * 1024 * 1024;

	int rlength = 0;
	int nlength = 0;
	private int nUrlPath = 0;
	private String[] urlPath = new String[100];

	static long nSaveFileCount = 0;
	static String saveLoc = "";
	
	private final int nSLEEPTIME = 1 * 60000;
	byte[] buf = new byte[10240];
	byte[] inbuf = new byte[10240];
	byte[] tembuf = new byte[10240];

	public fileIO() {

	}

	public void memcpy(byte[] src, int srcpos, byte[] dest, int destpos,
			int length) {
		for (int i = 0; i < length; i++) {
			dest[destpos + i] = src[srcpos + i];
		}
	}

	public String findCharset(byte[] buf) {
		if (buf == null || buf.length <= 0)
			return "";
		String strcharset = "";
		String addEr = "";
		String strtem;
		final String strCHARSETMARK = "charset=";
		int n = 0;
		int pos = 0;
		int i = 0;
		try {
			strtem = new String(buf);
			strtem = strtem.toLowerCase();
			n = strtem.indexOf(strCHARSETMARK);
			if (n > -1) {
				pos = n + strCHARSETMARK.length();
				for (i = pos; (i < pos + 30) && (i < buf.length); i++) {
					if ((strtem.charAt(i) == '"') || (strtem.charAt(i) == '\'')
							|| (strtem.charAt(i) == '/')
							|| (strtem.charAt(i) == '>')
							|| (strtem.charAt(i) == 0x20)
							|| (strtem.charAt(i) == 0x0d)
							|| (strtem.charAt(i) == 0x0a)
							|| (strtem.charAt(i) == ',')
							|| (strtem.charAt(i) == ';')) {
						break;
					}
				}
				strcharset = strtem.substring(pos, i);
			}
			Charset cs = Charset.forName(strcharset);
		} catch (UnsupportedCharsetException e) {
			strcharset = "";
		} catch (IllegalCharsetNameException e) {
			strcharset = "";
		} catch (IllegalArgumentException e) {
			strcharset = "";
		} catch (Exception e) {
			strcharset = "";
		}
		return strcharset;
	}

	public void savetofile(String strPath, String strFileExt, byte[] buf) {
		try {
			new File(strPath).mkdirs();
			String strfile = "";
			strfile = strfile.format(strPath + "%08X." + strFileExt,
					nSaveFileCount++);
			FileOutputStream fos = new FileOutputStream(strfile);
			fos.write(buf);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int writeData(URLConnection con, String strurl, boolean bSaveToFile)
			throws Exception {
		int n = 0;
		String strhtml = "";
		String strcharset = "";
		String strHost = "noname";
		String strWritePath = saveLoc;
		String strSubPath = "";
		try {
			int len = 0;
			nlength = 0;

			nUrlPath = parseURLPath(strurl);
			strHost = new URL(strurl).getHost();
			if (strHost == null || strHost.length() == 0)
				strHost = "noname";
			strWritePath = saveLoc + strHost + "\\";
			// ------------------------------------------------------------
			HttpURLConnection httpurlconn;
			httpurlconn = (HttpURLConnection) con;
			int nnnn = 0;
			String strHeaderField = "";
			if (!bSaveToFile) {
				while (strHeaderField != null) {
					strHeaderField = httpurlconn.getHeaderField(nnnn++);

					if (strHeaderField != null) {
						if (strHeaderField.toLowerCase().contains("charset=")) {
							strcharset = findCharset(strHeaderField.getBytes());
							if (!strcharset.isEmpty())
								break;
						}
					}
				}
			}
			InputStream br = con.getInputStream();
			while ((len = br.read(inbuf)) > 0) {
				tembuf = new byte[nlength];
				memcpy(buf, 0, tembuf, 0, nlength);
				buf = new byte[nlength + len];
				memcpy(tembuf, 0, buf, 0, nlength);
				memcpy(inbuf, 0, buf, nlength, len);
				nlength += len;
			}
			br.close();
			if (bSaveToFile) {
				strSubPath = "";
				if (nlength >= 5120 && nlength < FL_100K) {
					// strSubPath = "FL_100K\\";
				} else if (nlength >= FL_100K && nlength < FL_500K) {
					strSubPath = "FL_500K\\";
				} else if (nlength >= FL_500K && nlength < FL_1M) {
					strSubPath = "FL_1M\\";
				} else if (nlength >= FL_1M) {
					strSubPath = "FL_Over1M\\";
				} else {
					strSubPath = "";
					System.out
							.println("------------------------>this length is smaller than desired length, Discard!"
									+ nlength);
				}
				if (!strSubPath.isEmpty())
					savetofile(strWritePath + strSubPath,
							strurl.substring(strurl.length() - 4), buf);
			} else {
				if (strcharset.isEmpty()) {
					strcharset = findCharset(buf);
				}
				if (strcharset.isEmpty()) {
					strcharset = "utf-8";
				}
				strhtml = new String(buf, 0, nlength, strcharset);
				getIMGLinkText(strhtml);
				getBackgroundLinkText(strhtml);
				getOtherUrlLink(strhtml, strcharset);
			}
		} catch (Exception e) {
			System.out.println("Error:_Writer:��" + e);
		}
		// ---------------------------------------------------------------------------
		return n;
	}

	public int parseURLPath(String strurl) {
		int re = 0;
		int nCount = 0;
		if ((null == strurl) || (strurl.length() == 0))
			return 0;
		strurl = strurl.toLowerCase();
		try {
			URL url = new URL(strurl);
			urlPath[nCount++] = "http://" + url.getHost() + "/";
			String strpath = url.getPath();
			if (strpath.length() > 0)
				strpath = url.getPath().substring(1);
			while (strpath.contains("/")) {
				int pos = strpath.indexOf("/") + 1;
				urlPath[nCount++] = strpath.substring(0, pos);
				strpath = strpath.substring(pos);
			}
			re = nCount;
		} catch (Exception e) {
			System.out.println("Error:_HTML_parseURLPath:" + strurl
					+ e.getMessage());
		}

		return re;
	}

	public void getIMGLinkText(String strHtml) {
		String line = "";
		String strBZ = "<img src=";
		String strmylinktext = "";

		int nstartpos = 0;
		int nstoppos = 0;
		int pos = 0;
		int i = 0;
		boolean bSaveToFile;
		int nCount = 0;
		int nCount2 = 0;

		if (strHtml == null || strHtml.isEmpty())
			return;
		try {
			strHtml = strHtml.toLowerCase();

			while (pos != -1) {
				pos = strHtml.indexOf(strBZ, nstoppos);
				if (-1 == pos)
					break;
				nCount++;
				char c = strHtml.charAt(pos + strBZ.length());
				if ('"' == c || '\'' == c)
					nstartpos = pos + strBZ.length() + 1;
				else
					nstartpos = pos + strBZ.length();

				for (i = nstartpos; i < strHtml.length(); i++) {
					if ((strHtml.charAt(i) == '"')
							|| (strHtml.charAt(i) == '\'')
							|| (strHtml.charAt(i) == '>')
							|| (strHtml.charAt(i) == 0x20)
							|| (strHtml.charAt(i) == 0x0d)
							|| (strHtml.charAt(i) == 0x0a)
							|| (strHtml.charAt(i) == ',')
							|| (strHtml.charAt(i) == ';')) {
						break;
					}
				}
				nstoppos = i;

				strmylinktext = strHtml.substring(nstartpos, i);
				strmylinktext = completeURL(strmylinktext);
				// ----------------------------------------------
				crawlThread g = new crawlThread();
				g.strurl = strmylinktext;
				if (strmylinktext.endsWith(".jpg")
						|| strmylinktext.endsWith(".bmp")
						|| strmylinktext.endsWith(".jpeg")) {
					if (!crawlControler.IMGList.contains(strmylinktext)) {
						crawlControler.IMGList.add(strmylinktext);
						crawlControler.MissionList.add(strmylinktext);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error:" + e.getMessage());
		}
	}

	public void getBackgroundLinkText(String strHtml) {
		String line = "";
		String strBZ = "background=";
		String strmylinktext = "";

		int nstartpos = 0;
		int nstoppos = 0;
		int pos = 0;
		int i = 0;
		boolean bSaveToFile;
		int nCount = 0;
		int nCount2 = 0;

		if (strHtml == null || strHtml.isEmpty())
			return;
		try {
			strHtml = strHtml.toLowerCase();

			while (pos != -1) {
				pos = strHtml.indexOf(strBZ, nstoppos);
				if (-1 == pos)
					break;
				nCount++;
				char c = strHtml.charAt(pos + strBZ.length());
				if ('"' == c || '\'' == c)
					nstartpos = pos + strBZ.length() + 1;
				else
					nstartpos = pos + strBZ.length();

				for (i = nstartpos; i < strHtml.length(); i++) {
					if ((strHtml.charAt(i) == '"')
							|| (strHtml.charAt(i) == '\'')
							|| (strHtml.charAt(i) == '>')
							|| (strHtml.charAt(i) == 0x20)
							|| (strHtml.charAt(i) == 0x0d)
							|| (strHtml.charAt(i) == 0x0a)
							|| (strHtml.charAt(i) == ',')
							|| (strHtml.charAt(i) == ';')) {
						break;
					}
				}
				nstoppos = i;

				strmylinktext = strHtml.substring(nstartpos, i);
				strmylinktext = completeURL(strmylinktext);
				// ----------------------------------------------
				crawlThread g = new crawlThread();
				g.strurl = strmylinktext;
				if (strmylinktext.endsWith(".jpg")
						|| strmylinktext.endsWith(".bmp")
						|| strmylinktext.endsWith(".jpeg")) {
					if (!crawlControler.IMGList.contains(strmylinktext)) {
						crawlControler.IMGList.add(strmylinktext);
						crawlControler.MissionList.add(strmylinktext);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error:" + e.getMessage());
		}
	}

	public synchronized void writeStringToFile(String str) {
		try {
			str += "\r\n";
			new File(saveLoc).mkdirs();
			String strfile = saveLoc+"AlreadyProcessedURL.txt";
			FileOutputStream fos = new FileOutputStream(strfile, true);
			fos.write(str.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getOtherUrlLink(String strHtml, String strcharset)
			throws Exception {
		String line = "";
		String strBZ = "href=";
		String strmylinktext = "";

		int nstartpos = 0;
		int nstoppos = 0;
		int pos = 0;
		int i = 0;
		boolean bSaveToFile;
		char c_start = 0;
		boolean b_havestartchar;

		if (strHtml == null || strHtml.isEmpty())
			return;
		try {
			strHtml = strHtml.toLowerCase();

			while (pos != -1) {
				pos = strHtml.indexOf(strBZ, nstoppos);
				if (-1 == pos)
					break;
				char c = strHtml.charAt(pos + strBZ.length());
				if ('\\' == c)
					continue;
				if ('"' == c || '\'' == c) {
					c_start = c;
					b_havestartchar = true;
					nstartpos = pos + strBZ.length() + 1;
				} else {
					b_havestartchar = false;
					nstartpos = pos + strBZ.length();
				}

				for (i = nstartpos; i < strHtml.length(); i++) {
					if (b_havestartchar && (strHtml.charAt(i) == c_start)) {
						break;
					} else if ((strHtml.charAt(i) == '>')
							|| (strHtml.charAt(i) == 0x20)
							|| (strHtml.charAt(i) == 0x0d)
							|| (strHtml.charAt(i) == 0x0a)
							|| (strHtml.charAt(i) == ',')
							|| (strHtml.charAt(i) == ';')) {
						break;
					}
				}
				nstoppos = i;

				strmylinktext = strHtml.substring(nstartpos, i);

				// writeStringToFile(strmylinktext);
				// ---------------------------------------------------------
				if (strmylinktext.equals("../")
						|| strmylinktext.endsWith(".css"))
					continue;
				strmylinktext = completeURL(strmylinktext);
				// ----------------------------------------------
				if (strmylinktext.endsWith(".jpg")
						|| strmylinktext.endsWith(".bmp")
						|| strmylinktext.endsWith(".jpeg")) {
					if (!crawlControler.IMGList.contains(strmylinktext)) {
						crawlControler.IMGList.add(strmylinktext);
						crawlControler.MissionList.add(strmylinktext);
					}
				} else if (strmylinktext.startsWith(urlPath[0])) {
					if ((!crawlControler.URLList.contains(strmylinktext))) {
						crawlControler.URLList.add(strmylinktext);
						crawlControler.MissionList.add(strmylinktext);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error:" + e.getMessage());
		}
	}

	public static boolean isTrimEmpty(String astr) {
		if ((null == astr) || (astr.length() == 0)) {
			return true;
		}
		if (isBlank(astr.trim())) {
			return true;
		}
		return false;
	}

	public static boolean isBlank(String astr) {
		if ((null == astr) || (astr.length() == 0)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isNumeral(String str) {
		if ((null == str) || (str.length() == 0)) {
			return true;
		}
		str = filtedString(str);
		String strSYMBOL = "0123456789";
		for (int i = 0; i < strSYMBOL.length(); i++) {
			String oldC = strSYMBOL.substring(i, i + 1);
			str = str.replaceAll(oldC, "");
		}
		if (str.isEmpty())
			return true;
		return false;
	}

	private String filtedString(String instr) {
		if ((null == instr) || (instr.length() == 0))
			return "";
		String str = "";
		str = instr;
		String strSYMBOL = "`-=[]\\;',./~!@#$%^&*()_+{}|:\"<>?\r\n\t ";
		str = str.replace("&nbsp", "");
		for (int i = 0; i < strSYMBOL.length(); i++) {
			String oldC = strSYMBOL.substring(i, i + 1);
			str = str.replace(oldC, "");
		}
		return str;
	}

	public String completeURL(String strNewUrl) {
		if (strNewUrl == null || strNewUrl.length() == 0)
			return "";
		if ((strNewUrl.startsWith("http://")) || (strNewUrl.startsWith("#"))
				|| (nUrlPath <= 0))
			return strNewUrl;

		if (strNewUrl.startsWith("javascript:mm_openbrwindow(")) {
			int nstartpos = "javascript:mm_openbrwindow(".length();
			char c = strNewUrl.charAt(nstartpos);
			if ('"' == c || '\'' == c) {
				strNewUrl = strNewUrl.substring(nstartpos + 1,
						strNewUrl.length() - 2);
			} else {
				strNewUrl = strNewUrl.substring(nstartpos,
						strNewUrl.length() - 1);
			}
		}
		String strcompleteurl = "";
		String strParentURL = "";

		try {
			if (strNewUrl.startsWith("/")) {
				strParentURL = urlPath[0];
				strNewUrl = strNewUrl.substring(1);
			} else if (strNewUrl.startsWith("./")) {
				int nNeedUrlPath = nUrlPath;
				while (strNewUrl.startsWith("./")) {
					nNeedUrlPath--;
					strNewUrl = strNewUrl.substring(2);
				}
				if (nNeedUrlPath <= 0)
					strParentURL = urlPath[0];
				else {
					for (int t = 0; t < nNeedUrlPath; t++) {
						strParentURL += urlPath[t];
					}
				}
			} else if (strNewUrl.startsWith("../")) {
				int nNeedUrlPath = nUrlPath;
				while (strNewUrl.startsWith("../")) {
					nNeedUrlPath--;
					strNewUrl = strNewUrl.substring(3);
				}
				if (nNeedUrlPath <= 0)
					strParentURL = urlPath[0];
				else {
					for (int t = 0; t < nNeedUrlPath; t++) {
						strParentURL += urlPath[t];
					}
				}
			} else {
				for (int t = 0; t < nUrlPath; t++) {
					strParentURL += urlPath[t];
				}
			}
			strcompleteurl = strParentURL + strNewUrl;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strcompleteurl;
	}

}
