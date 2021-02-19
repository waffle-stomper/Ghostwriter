package wafflestomper.ghostwriter;

import net.minecraft.client.gui.widget.button.Button;
import java.util.List;


public interface IGhostBook {
	Button addGhostButton(Button button);
	int getCurrPage();
	void setCurrPage(int pageNum);
	int getBookPageCount();
	List<String> pagesAsList();
	void updateVanillaButtons();
	void insertText(String insertChars);
	void insertNewPage(int atPageNum, String pageText);
	void removePage(int pageNum);
	void replaceBookPages(List<String> newPages);
	String getPageText(int pageNum);
	void setPageText(int pageNum, String pageText);
	boolean isBookBeingSigned();
	String getBookTitle();
	void setBookTitle(String title);
	
	/**
	 * Used to update the vanilla book after a change is made without using the vanilla TextInputUtil
	 * e.g. removing a page
	 */
	void bookChanged(boolean setModifiedFlag);
}
