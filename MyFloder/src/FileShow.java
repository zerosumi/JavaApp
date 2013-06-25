import java.io.File;


public class FileShow implements ObjectShow{

	protected File file;
	
	public FileShow(File tfile) {
		this.file = tfile;
	}
	
	@Override
	public String getDetail() {

		return file.getName()+" ";
	}

	@Override
	public File getFile() {
		return file;
	}


}
