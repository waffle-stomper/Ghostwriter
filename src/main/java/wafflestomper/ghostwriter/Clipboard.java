package wafflestomper.ghostwriter;

import java.util.ArrayList;
import java.util.List;

public class Clipboard {
	public final List<String> pages = new ArrayList<>();
	/**
	 * Used for copying one or more pages (so we don't have to wipe out a book in the clipboard)
	 */
	public final List<String> miscPages = new ArrayList<>();
	//Stores entire books
	public String title = "";
	public String author = "";  // Even though we can't set it when signing, this is still read from signed books
	public boolean bookInClipboard = false;
	
	public void clearBook() {
		this.title = "";
		this.author = "";
		this.pages.clear();
		this.bookInClipboard = false;
	}
	
	
	public void clone(Clipboard _clipboard) {
		this.title = _clipboard.title;
		this.author = _clipboard.author;
		this.pages.clear();
		this.pages.addAll(_clipboard.pages);
		this.bookInClipboard = _clipboard.bookInClipboard;
		this.miscPages.clear();
		this.miscPages.addAll(_clipboard.miscPages);
	}
}
