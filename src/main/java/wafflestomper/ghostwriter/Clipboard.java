package wafflestomper.ghostwriter;

import java.util.ArrayList;
import java.util.List;

public class Clipboard {
	//Stores entire books
	public String title = ""; 
	public String author = ""; // TODO: Remove author now that we can't set it?
	public List<String> pages = new ArrayList();
	public boolean bookInClipboard = false;
	/** Used for copying one or more pages (so we don't have to wipe out a book in the clipboard) */
	public List<String> miscPages = new ArrayList();
	
	
	public Clipboard(){
		//Debugging
		//this.singlePage = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";
	}
	
	public Clipboard(Clipboard _clipboard){
		this.title = _clipboard.title;
		this.author = _clipboard.author;
		this.pages.clear();
		for (String page : _clipboard.pages){this.pages.add(page);}
		this.bookInClipboard = _clipboard.bookInClipboard;
		this.miscPages.clear();
		for (String page : _clipboard.miscPages){this.miscPages.add(page);}
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
