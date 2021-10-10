package wafflestomper.ghostwriter.gui;

import net.minecraft.client.gui.components.Button;

import java.util.List;


public interface IGhostBook {
	/**
	 * Adds a GhostLayer button to the screen
	 */
	Button addGhostButton(Button button);
	
	/**
	 * @return Page number that the book is open to
	 */
	int getCurrPage();
	
	/**
	 * Sets which page the book is open to
	 */
	void setCurrPage(int pageNum);
	
	/**
	 * @return How many pages are in the book
	 */
	int getBookPageCount();
	
	/**
	 * @return List of strings, each representing a single page, with old-school formatting codes
	 */
	List<String> pagesAsList();
	
	/**
	 * Calls the vanilla updateButtons() method
	 */
	void updateVanillaButtons();
	
	/**
	 * @return Text from the page at pageNum
	 */
	String getPageText(int pageNum);
	
	/**
	 * Adds text to the open page (or the title if the book is being signed)
	 * Only used with unsigned books
	 */
	void insertText(String insertChars);
	
	/**
	 * Adds a new page (if possible) into the book at the given page number, with the given text
	 * This shifts any subsequent pages up one
	 * Only used with unsigned books
	 */
	void insertNewPage(int atPageNum, String pageText);
	
	/**
	 * Removes a page at the given page number
	 * Only used with unsigned books
	 */
	void removePage(int pageNum);
	
	/**
	 * Removes all existing pages and replaces them with newPages
	 * Only used with unsigned books
	 */
	void replaceBookPages(List<String> newPages);
	
	/**
	 * Replaces the text on a single page with the supplied text
	 * Only used with unsigned books
	 */
	void setPageText(int pageNum, String pageText);
	
	/**
	 * @return true if the book signing screen is open
	 * Only used with unsigned books
	 */
	boolean isBookBeingSigned();
	
	/**
	 * Sets the internal bookTitle field
	 * Only used with unsigned books
	 */
	void setBookTitle(String title);
	
	/**
	 * Updates a vanilla book after a change is made that didn't involve the vanilla TextFieldHelper
	 * e.g. removing a page
	 * If this isn't called after GhostLayer makes a change, the game will probably crash (or at least misbehave)
	 */
	void bookChanged(boolean setModifiedFlag);
}
