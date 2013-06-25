import java.util.ArrayList;
import java.util.List;


public class FileAggregate implements Aggregate{
	
	private String fpath;
	private List list = new ArrayList();
	
	public FileAggregate(String fpath) {
		this.fpath = fpath;
	}
	@Override
	public void add(Object file) {
		list.add(file);
	}

	@Override
	public void remove(Object file) {
		list.remove(file);
	}

	@Override
	public Iterator iterator() {
		return new FileIterator(list);
	}

	public List getList() {
		return this.list;
	}
}
