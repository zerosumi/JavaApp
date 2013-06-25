import java.io.File;


public class SizeDecoShow extends AttrDecoShow{

	public SizeDecoShow(ObjectShow tobj) {
		super(tobj);
	}

	@Override
	public String getDetail() {
		return objshow.getDetail()+String.valueOf(super.tfile.length()+"Bytes ");
	}

	@Override
	public File getFile() {
		// TODO Auto-generated method stub
		return tfile;
	}


}
