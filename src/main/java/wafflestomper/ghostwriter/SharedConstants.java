package wafflestomper.ghostwriter;

public class SharedConstants {
	
	/////////////////////////
	// Minecraft constants //
	/////////////////////////
	
	// Maximum length allowed before the book is considered invalid by the client (as of 1.16.1)
	public static final int BOOK_TITLE_MAX_LEN = 32;
	// Find this magic number inside EditBookScreen.addNewPage(). Correct as of 1.16.1
	public static final int MAX_BOOK_PAGES = 100;
	
	
	///////////////////////////
	// Ghostwriter constants //
	///////////////////////////
	public static final String GHB_PAGE_BREAK = ">>>>";
	public static final String GHB_FILE_EXTENSION = ".ghb";
}
