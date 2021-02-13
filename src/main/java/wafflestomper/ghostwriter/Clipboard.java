package wafflestomper.ghostwriter;

import java.util.ArrayList;
import java.util.List;

public class Clipboard {
	//Stores entire books
	public String title = ""; 
	public String author = ""; // TODO: Remove author now that we can't set it?
	public final List<String> pages = new ArrayList<>();
	public boolean bookInClipboard = false;
	/** Used for copying one or more pages (so we don't have to wipe out a book in the clipboard) */
	public final List<String> miscPages = new ArrayList<>();
	
	
	public Clipboard(){}
	
	public Clipboard(Clipboard _clipboard){
		this.clone(_clipboard);
	}
	
	public void clearBook(){
		this.title = "";
		this.author = "";
		this.pages.clear();
		this.bookInClipboard = false;
	}
	
	public void clone(Clipboard _clipboard){
		this.title = _clipboard.title;
		this.author = _clipboard.author;
		this.pages.clear();
		this.pages.addAll(_clipboard.pages);
		this.bookInClipboard = _clipboard.bookInClipboard;
		this.miscPages.clear();
		this.miscPages.addAll(_clipboard.miscPages);
	}
}
