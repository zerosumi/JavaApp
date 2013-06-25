import java.util.Collections;


public class MyFolderRunner {
	public static void main(String[] args) {
		
		//FUNCTION 1 - LISTING, ITERATOR
		MyFolder myfolder = new MyFolder("/Users/ElliottRen/Desktop");
//		myfolder.listAll();

		//FUNCTION 2 - DISPLAYING DETAILS
//		FileShow fileshow = new FileShow(myfolder.getFileAtIndex(6));
//		System.out.println(fileshow.getDetail());
//		SizeDecoShow sizeshow = new SizeDecoShow(fileshow);
//		System.out.println(sizeshow.getDetail());
//		ModfDecoShow decoshow = new ModfDecoShow(sizeshow);
//		System.out.println(decoshow.getDetail());
		
		//FUNCTION 3 - SORTING
		SizeSorting szcomp = new SizeSorting();
		Collections.sort(myfolder.getList(),szcomp);
		
		for (int i = 0; i < myfolder.getCount(); i++) {
			FileShow fileshow1 = new FileShow(myfolder.getFileAtIndex(i));
			SizeDecoShow sizeshow1 = new SizeDecoShow(fileshow1);
			System.out.println(sizeshow1.getDetail());
		}
		
		ModfSorting mdcomp = new ModfSorting();
		Collections.sort(myfolder.getList(),mdcomp);
		
		for (int i = 0; i < myfolder.getCount(); i++) {
			FileShow fileshow2 = new FileShow(myfolder.getFileAtIndex(i));
			ModfDecoShow modfshow2 = new ModfDecoShow(fileshow2);
			System.out.println(modfshow2.getDetail());
		}
			
	}

}
