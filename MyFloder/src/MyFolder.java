import java.io.File;
import java.util.List;

public class MyFolder {
	
	private FileAggregate folder;// = new File("");
	private FileIterator folderit;
	private File tfile;
	private int filesCount;
	
	public MyFolder (String fpath) {
		tfile = new File(fpath);
		filesCount = 0;
		if(tfile.isDirectory()) {
			folder = new FileAggregate(fpath);
			File[] listOfFiles = tfile.listFiles();
			for (File f:listOfFiles) {
				folder.add(f);
				filesCount++;
			}
			folderit = (FileIterator) folder.iterator();
		}
		else {
			System.out.println("Path is not a directory!");
		}
	}
	public List getList() {
		return folder.getList();
	}
	public File getFile() {
		return tfile;
	}
	public File getFileAtIndex(int index) {
		return folderit.getIndex(index);
	}
	public int getCount() {
		return this.filesCount;
	}
	public void listAll(){
		FileShow fshow;// = new FileShow(folderit.next());
		while (folderit.hasNext()){
			fshow = new FileShow(folderit.next());
			System.out.println(fshow.getDetail());
		}
	}
	
//	public void listAFile(int index, String args) {
//		char[] chars = args.toLowerCase().toCharArray();
//		File tfile = folderit.getIndex(index); 
//		FileShow fshow = new FileShow(tfile);
//		String tString = new String(fshow.getDetail());
//		for(int i = 0; i < args.length(); i++){
//			switch(chars[i]) {
//			case 's':
//			{
//				SizeDecoShow sizedeco = new SizeDecoShow(tfile);
//				tString += sizedeco.getDetail();
//				break;
//			}
//			case 'm':
//			{
//				ModfDecoShow modfdeco = new ModfDecoShow(tfile);
//				tString += modfdeco.getDetail();
//				break;
//			}
//			default:
//				break;
//			}
//		}
//		System.out.println(tString);
//	}
	
	
	
	
	
}
