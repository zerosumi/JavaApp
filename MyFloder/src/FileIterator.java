import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileIterator implements Iterator{
	
	private List list = new ArrayList();
	private int cursor = 0;
	
	public FileIterator(List list) {
		this.list = list;
	}
	
	@Override
	public File next() {
		File obj = null;
		if(this.hasNext()) {
			obj = (File) this.list.get(cursor++);
		}
		return obj;
	}

	@Override
	public boolean hasNext() {
		if(cursor == list.size()) {
			return false;
		}
		return true;
	}
	
	public File getIndex(int index) {
		return (File) this.list.get(index);
	}
	

}
