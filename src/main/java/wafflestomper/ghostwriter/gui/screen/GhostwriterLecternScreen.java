package wafflestomper.ghostwriter.gui.screen;

import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import wafflestomper.ghostwriter.Ghostwriter;
import wafflestomper.ghostwriter.utilities.BookUtilities;
import wafflestomper.ghostwriter.gui.GhostLayer;
import wafflestomper.ghostwriter.gui.IGhostBook;
import wafflestomper.ghostwriter.utilities.Printer;

import java.util.ArrayList;
import java.util.List;

public class GhostwriterLecternScreen extends LecternScreen implements IGhostBook {
	
	private final GhostLayer ghostLayer;
	private final LecternMenu lecternContainer;
	private final Printer printer;
	
	
	public GhostwriterLecternScreen(ItemStack currStack, LecternMenu lecternContainer, Inventory playerInventory) {
		// Not sure why it needs the inventory and text. Both params are ignored by the constructor
		super(lecternContainer, playerInventory, Component.translatable(""));
		
		this.ghostLayer = new GhostLayer(this, this, false);
		this.lecternContainer = lecternContainer;
		this.ghostLayer.extractTitleAuthor(currStack);
		this.printer = new Printer();
		this.printer.gamePrint("WARNING! LECTERNS ARE NOT CURRENTLY SUPPORTED!!");
	}
	
	
	@Override  // From LecternScreen
	public void createMenuControls() {
		this.ghostLayer.updateButtons();
	} // TODO: Is this the correct override?
	
	
	@Override  // From IGhostBook
	public List<String> pagesAsList() {
		ItemStack book = this.menu.getBook();
		List<String> pages = new ArrayList<>();
		this.printer.gamePrint("WARNING! DEV VERSION - CAN'T GET PAGES!");
		// TODO: FIX AND ENABLE THIS BEFORE RELEASE
//		for (int i = 0; i < this.lecternContainer.lecternData.getCount(); i++) {
//			pages.add(BookUtilities.deJSONify(this.lecternContainer.lecternData.get(i).getString()));
//		}
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
		// TODO: This is causing a crash when opening a book because forwardButton is null on the lectern
		super.updateButtonVisibility();
	}
	
	
	@Override  // From IGhostBook
	public String getPageText(int pageNum) {

		this.printer.gamePrint("WARNING! DEV VERSION - CAN'T GET PAGE TEXT!");
		// TODO: REMOVE THIS TEMPORARY WORKAROUND ONCE YOU FIGURE OUT HOW TO GET PAGES!
		return "";
		//return this.lecternContainer.lecternData.get(pageNum).getString();
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
	public void addFormattingCode(String formattingCode) {
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
