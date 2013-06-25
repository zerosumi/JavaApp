import java.io.File;


public abstract class AttrDecoShow implements ObjectShow{
	
	protected ObjectShow objshow;
	protected File tfile;
	
	public AttrDecoShow (ObjectShow tobj) {
		this.objshow=tobj;
		this.tfile=tobj.getFile();
	}
	
}
