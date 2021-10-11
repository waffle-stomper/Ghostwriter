package wafflestomper.ghostwriter.gui.screen;

import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.TextComponent;
import wafflestomper.ghostwriter.Ghostwriter;
import wafflestomper.ghostwriter.utilities.BookUtilities;
import wafflestomper.ghostwriter.gui.GhostLayer;
import wafflestomper.ghostwriter.gui.IGhostBook;

import java.util.ArrayList;
import java.util.List;

public class GhostwriterLecternScreen extends LecternScreen implements IGhostBook {
	
	private final GhostLayer ghostLayer;
	private final LecternMenu lecternContainer;
	
	
	public GhostwriterLecternScreen(ItemStack currStack, LecternMenu lecternContainer, Inventory playerInventory) {
		// Not sure why it needs the inventory and text. Both params are ignored by the constructor
		super(lecternContainer, playerInventory, new TextComponent(""));
		
		this.ghostLayer = new GhostLayer(this, this, false);
		this.lecternContainer = lecternContainer;
		this.ghostLayer.extractTitleAuthor(currStack);
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
	
	
	@Override  // From LecternScreen
	public void init() {
		super.init();
		this.ghostLayer.init();
		this.updateButtonVisibility();
		// I'm not sure why, but this is now necessary before the lectern will show text
		this.bookChanged(true);
		// This is a hack based on LecternScreen.func_214176_h()
		// Books can be left open to a specific page on a lectern. This displays that page.
		// Otherwise we'd just be showing the first page every time
		this.setPage(Math.max(this.lecternContainer.getPage(), 0));
		Ghostwriter.LOG.info("Page set to: " + this.currentPage);
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
	public String getPageText(int pageNum) {
		return this.bookAccess.getPage(pageNum).getString();
	}
	
	
	@Override  // From IGhostBook
	public boolean isBookBeingSigned() {
		return false;
	}
	
	
	@Override  // From IGhostBook
	public void bookChanged(boolean setModifiedFlag) {
		super.bookChanged();
	}
	
	
	@Override  // From IGhostBook
	public Button addGhostButton(Button button) {
		return this.addRenderableWidget(button);
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
