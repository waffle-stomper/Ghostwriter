package wafflestomper.ghostwriter;

public class SharedConstants {
	
	/////////////////////////
	// Minecraft constants //
	/////////////////////////
	
	// Maximum length allowed before the book is considered invalid by the client (as of 1.16.1)
	public static final int BOOK_TITLE_MAX_LEN = 32;
	// Find this magic number inside EditBookScreen.addNewPage(). Correct as of 1.16.1
	public static final int MAX_BOOK_PAGES = 100;
	// Width of text in a book (as of 1.16.1)
	public static final int BOOK_TEXT_WIDTH = 114;
	// Maximum lines of text in a signed book as of 1.16.1 (unsigned books allow 15)
	public static final int BOOK_MAX_LINES = 14;
	
	// Keycodes can be found in net.minecraft.client.util.InputMappings (correct as of 1.16.1)
	public static final int KEY_ESC = 256;
	public static final int KEY_DEL = 261;
	public static final int KEY_RIGHT = 262;
	public static final int KEY_DOWN = 264;
	public static final int KEY_UP = 265;
	public static final int KEY_END = 269;
	
	
	///////////////////////////
	// Ghostwriter constants //
	///////////////////////////
	public static final String GHB_PAGE_BREAK = ">>>>";
	public static final String GHB_FILE_EXTENSION = ".ghb";
}
