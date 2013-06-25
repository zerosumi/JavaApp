import java.io.File;
import java.util.Date;


public class ModfDecoShow extends AttrDecoShow{

	public ModfDecoShow(ObjectShow tobj) {
		super(tobj);
	}

	@Override
	public String getDetail() {
		Date dt = new Date(super.tfile.lastModified());
		return objshow.getDetail()+String.valueOf(dt +" ");
	}

	@Override
	public File getFile() {
		// TODO Auto-generated method stub
		return tfile;
	}


}