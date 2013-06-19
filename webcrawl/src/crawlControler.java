import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;

public class crawlControler {
	public static LinkedList<String> URLList = new LinkedList<String>();
	public static LinkedList<String> IMGList = new LinkedList<String>();
	public static LinkedList<String> MissionList = new LinkedList<String>();
	public static int nThreadCount = 0;
	public final static int nMaxThread = 50;
	public final static int nMaxURLLink = 10000;
	public static String saveLoc = "";

	public crawlControler() {
	}

	public static void start() {
		String strlinktext = "";
		boolean bsavetofile = false;
		int nAutoStopSignal = 0;
		while (true) {
			try {
				System.out.println("Current threads:" + nThreadCount
						+ "; missions:" + crawlControler.MissionList.size());
				if ((nThreadCount == 0)
						&& (crawlControler.MissionList.size() == 0)) {
					nAutoStopSignal++;
					if (nAutoStopSignal >= 60) {
						System.out
								.println("No more mission, will terminate in 60s...");
						break;
					}
				}
				if ((nThreadCount < nMaxThread)) {
					strlinktext = (String) crawlControler.MissionList
							.pollFirst();
					if (null == strlinktext || strlinktext.length() <= 0) {
						System.out.println("Mission list is empty");
						Thread.sleep(1000);
						continue;
					}
					strlinktext = strlinktext.toLowerCase();
					System.out.println("------------------------>"
							+ strlinktext);
					if (strlinktext.endsWith(".css")
							|| strlinktext.equals("../"))
						continue;
					if (strlinktext.endsWith(".jpg")
							|| strlinktext.endsWith(".bmp")
							|| strlinktext.endsWith(".jpeg")) {
						// savetofile
						bsavetofile = true;
						new File(saveLoc).mkdirs();
						FileOutputStream fos = new FileOutputStream(
								saveLoc+"savetofile.txt", true);
						String strfile = strlinktext + "\r\n";
						fos.write(strfile.getBytes());
						fos.close();
					} else {

						bsavetofile = false;
						crawlControler.URLList.add(strlinktext);
						new File(saveLoc).mkdirs();
						FileOutputStream fos = new FileOutputStream(
								saveLoc+"Processed URL.txt", true);
						String strfile = strlinktext + "\r\n";
						fos.write(strfile.getBytes());
						fos.close();
					}
					if (strlinktext.length() > 0) {
						Thread g = new crawlThread(strlinktext, bsavetofile);
						g.start();//
					}
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				// System.out.println("ERROR: controler_start_"+e);
			}
		}
		System.out.println("all complete");
	}

	public static void main(String[] args) {
		String str = "";
		try {
			// str="http://www.chinatuku.com/fg/dongxuemeijing/index.htm";
			// str="http://www.8825.com/6_Class.asp";
			str = "http://www.ivsky.com/";
			// str = "http://www.bzkoo.com/";
			// str = "http://www.mydeskcity.com/";
			// str = "http://www.tuhai.org/Untitled-1.html";
			// str = "http://www.ivsky.com/Photo/835/87278.html";

			crawlControler.MissionList.add(str);
			crawlControler.start();
		} catch (Exception e) {
			System.out.println("Current nRunCount:" + crawlThread.nRunCount);
			e.printStackTrace();
		}
	}

}
