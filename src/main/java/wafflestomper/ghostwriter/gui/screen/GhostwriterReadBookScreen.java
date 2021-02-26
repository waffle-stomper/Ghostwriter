package wafflestomper.ghostwriter.gui.screen;

import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import wafflestomper.ghostwriter.utilities.BookUtilities;
import wafflestomper.ghostwriter.GhostLayer;
import wafflestomper.ghostwriter.IGhostBook;

import java.util.ArrayList;
import java.util.List;

public class GhostwriterReadBookScreen extends ReadBookScreen implements IGhostBook {
	
	private final GhostLayer ghostLayer;
	
	
	public GhostwriterReadBookScreen(ReadBookScreen.IBookInfo bookInfoIn, ItemStack currStack) {
		super(bookInfoIn);
		this.ghostLayer = new GhostLayer(this, this, false);
		this.ghostLayer.extractTitleAuthor(currStack);
	}
	
	
	@Override  // From ReadBookScreen
	public void init() {
		super.init();
		this.ghostLayer.init();
		this.updateButtons();
	}
	
	
	@Override  // From ReadBookScreen
	public void updateButtons() {
		this.ghostLayer.updateButtons();
	}
	
	
	@Override  // From IGhostBook
	public List<String> pagesAsList() {
		List<String> pages = new ArrayList<>();
		for (int i = 0; i < this.getPageCount(); i++) {
			pages.add(BookUtilities.deJSONify(this.bookInfo.func_238806_b_(i).getString()));
		}
		return pages;
	}
	
	
	@Override  // From IGhostBook
	public int getBookPageCount() {
		return this.getPageCount();
	}
	
	
	@Override  // From IGhostBook
	public void updateVanillaButtons() {
		super.updateButtons();
	}
	
	
	@Override  // From IGhostBook
	public Button addGhostButton(Button button) {
		return this.addButton(button);
	}
	
	
	@Override  // From IGhostBook
	public String getPageText(int pageNum) {
		// func_238806_b_() either returns the page text or a blank ITextComponent
		return this.bookInfo.func_238806_b_(pageNum).toString();
	}
	
	
	@Override  // From IGhostBook
	public int getCurrPage() {
		return this.currPage;
	}
	
	// Unused methods that only apply to unsigned books
	@Override  // From IGhostBook
	public void setCurrPage(int pageNum) {
	}
	
	@Override  // From IGhostBook
	public boolean isBookBeingSigned() {
		return false;
	}
	
	@Override  // From IGhostBook
	public void bookChanged(boolean setModifiedFlag) {
		this.cachedPage = -1;
	}
	
	@Override  // From IGhostBook
	public void setBookTitle(String title) {
	}
	
	@Override  // From IGhostBook
	public void setPageText(int pageNum, String pageText) {
	}
	
	@Override  // From IGhostBook
	public void insertText(String insertChars) {
	}
	
	@Override  // From IGhostBook
	public void insertNewPage(int atPageNum, String pageText) {
	}
	
	@Override  // From IGhostBook
	public void removePage(int pageNum) {
	}
	
	@Override  // From IGhostBook
	public void replaceBookPages(List<String> newPages) {
	}
}
