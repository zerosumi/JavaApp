import java.io.File;
import java.util.Comparator;


public class SizeSorting implements Comparator{


	@Override
	public int compare(Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		File f1 = (File) arg1;
		File f2 = (File) arg2;
		if (f1.length() > f2.length()) {
			return 1;
		}
		else if (f1.length() == f2.length()) {
			return 0;
		}
		else {
			return -1;
		}
	}

}
