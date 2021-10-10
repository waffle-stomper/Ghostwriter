package wafflestomper.ghostwriter.gui.screen;

import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import wafflestomper.ghostwriter.utilities.BookUtilities;
import wafflestomper.ghostwriter.gui.GhostLayer;
import wafflestomper.ghostwriter.gui.IGhostBook;

import java.util.ArrayList;
import java.util.List;

public class GhostwriterReadBookScreen extends BookViewScreen implements IGhostBook {
	
	private final GhostLayer ghostLayer;
	
	
	public GhostwriterReadBookScreen(BookViewScreen.BookAccess bookInfoIn, ItemStack currStack) {
		super(bookInfoIn);
		this.ghostLayer = new GhostLayer(this, this, false);
		this.ghostLayer.extractTitleAuthor(currStack);
	}
	
	
	@Override  // From ReadBookScreen
	public void init() {
		super.init();
		this.ghostLayer.init();
		this.updateButtonVisibility();
	}
	
	
	@Override  // From ReadBookScreen
	public void updateButtonVisibility() {
		this.ghostLayer.updateButtons();
	}
	
	
	@Override  // From IGhostBook
	public List<String> pagesAsList() {
		List<String> pages = new ArrayList<>();
		for (int i = 0; i < this.getNumPages(); i++) {
			pages.add(BookUtilities.deJSONify(this.bookAccess.getPage(i).getString()));
		}
		return pages;
	}
	
	
	@Override  // From IGhostBook
	public int getBookPageCount() {
		return this.getNumPages();
	}
	
	
	@Override  // From IGhostBook
	public void updateVanillaButtons() {
		super.updateButtonVisibility();
	}
	
	
	@Override  // From IGhostBook
	public Button addGhostButton(Button button) {
		return this.addRenderableWidget(button);
	}
	
	
	@Override  // From IGhostBook
	public String getPageText(int pageNum) {
		// getPage() either returns the page text or a blank Component
		return this.bookAccess.getPage(pageNum).toString();
	}
	
	
	@Override  // From IGhostBook
	public int getCurrPage() {
		return this.currentPage;
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
