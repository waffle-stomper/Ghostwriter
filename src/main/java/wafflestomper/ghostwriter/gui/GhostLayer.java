package wafflestomper.ghostwriter.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import wafflestomper.ghostwriter.Ghostwriter;
import wafflestomper.ghostwriter.gui.screen.GhostwriterEditBookScreen;
import wafflestomper.ghostwriter.gui.screen.GhostwriterFileBrowserScreen;
import wafflestomper.ghostwriter.gui.screen.GhostwriterSignedPreviewScreen;
import wafflestomper.ghostwriter.utilities.Clipboard;
import wafflestomper.ghostwriter.utilities.FileHandler;
import wafflestomper.ghostwriter.utilities.Printer;
import wafflestomper.ghostwriter.utilities.SharedConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Generic Ghostwriter functions to supplement the vanilla book screens
 */
public class GhostLayer {
	
	private Button buttonDisableAutoReload;
	private Button buttonPasteBook;
	private Button buttonCutMultiplePages;
	private Button buttonSelectPageA;
	private Button buttonSelectPageB;
	private Button buttonCopySelectedPages;
	private Button buttonPasteMultiplePages;
	private Button buttonRemoveSelectedPages;
	private final List<Button> buttonsHideWhileSigning = new ArrayList<>();
	private final List<Button> buttonsEditOnly = new ArrayList<>();
	private int colorFormatButtonX = 0;
	
	// Used for cutting/copying multiple pages at a time
	private int selectedPageA = -1;
	private int selectedPageB = -1;
	
	private File autoReloadFile; // Auto Reload is active when this is not null
	public long autoReloadLastModified = 0;
	private long autoReloadLastCheck = 0;
	private Clipboard autoReloadClipboard;
	
	private static final Minecraft MINECRAFT = Minecraft.getInstance();
	private final IGhostBook parent;
	public final Screen screen;
	public final boolean bookIsEditable;
	
	// These are mostly used for loaded books
	// Note that BookEditScreen has its own bookTitle field that we need to keep track of
	private String bookTitle = "";
	private String bookAuthor = "";
	
	private boolean buttonsInitialized = false;
	
	
	public GhostLayer(IGhostBook parent, Screen screenParent, boolean bookIsEditable) {
		this.parent = parent;
		this.screen = screenParent;
		this.bookIsEditable = bookIsEditable;
	}
	
	
	public String getBookAuthor(){
		return this.bookAuthor;
	}
	
	
	public String getBookTitle(){
		return this.bookTitle;
	}
	
	
	/**
	 * Set the internal bookTitle field, enforcing the maximum length from SharedConstants
	 */
	public void setBookTitle(String title){
		if (title.length() < SharedConstants.BOOK_TITLE_MAX_LEN) {
			this.bookTitle = title;
		} else {
			this.bookTitle = title.substring(0, SharedConstants.BOOK_TITLE_MAX_LEN);
		}
	}
	
	
	/**
	 * Called by GhostwriterLecternScreen and GhostwriterReadBookScreen
	 * Extracts title and author
	 */
	public void extractTitleAuthor(ItemStack bookStack) {
		if (bookStack == null) return;
		CompoundTag compoundnbt = bookStack.getTag();
		if (compoundnbt == null) return;
		this.setBookTitle(compoundnbt.getString("title"));
		this.bookAuthor = compoundnbt.getString("author");
	}
	
	
	private void bookChanged(boolean resetPageSelection) {
		if (resetPageSelection) {
			this.selectedPageA = -1;
			this.selectedPageB = -1;
		}
		this.parent.bookChanged(true);
		this.updateButtons();
	}
	
	
	private void copyBook() {
		Ghostwriter.GLOBAL_CLIPBOARD.author = "";
		Ghostwriter.GLOBAL_CLIPBOARD.title = this.bookTitle;
		Ghostwriter.GLOBAL_CLIPBOARD.pages.clear();
		Ghostwriter.GLOBAL_CLIPBOARD.pages.addAll(this.parent.pagesAsList());
		Ghostwriter.GLOBAL_CLIPBOARD.bookInClipboard = true;
		Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Book copied");
		this.updateButtons();
	}
	
	
	private void copySelectedPagesToClipboard() {
		int firstPage = Math.min(this.selectedPageA, this.selectedPageB);
		int lastPage = Math.max(this.selectedPageA, this.selectedPageB);
		
		// Handle the case where A or B is -1 (i.e. no selection)
		if (firstPage == -1 || lastPage == -1) {
			firstPage = this.parent.getCurrPage();
			lastPage = this.parent.getCurrPage();
		}
		
		if (firstPage >= 0 && lastPage >= firstPage && lastPage < this.parent.getBookPageCount()) {
			Ghostwriter.GLOBAL_CLIPBOARD.miscPages.clear();
			List<String> pagesAsList = this.parent.pagesAsList();
			for (int i = firstPage; i <= lastPage; i++) {
				Ghostwriter.GLOBAL_CLIPBOARD.miscPages.add(pagesAsList.get(i));
			}
			Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Selection copied");
		} else {
			Ghostwriter.PRINTER.gamePrint(Printer.RED + "Invalid selection! Copy aborted.");
		}
		this.updateButtons();
	}
	
	
	private void cutMultiplePages() {
		int from = Math.min(this.selectedPageA, this.selectedPageB);
		int to = Math.max(this.selectedPageA, this.selectedPageB);
		
		// Switch to single page mode if necessary
		if (from < 0 || from >= this.parent.getBookPageCount() || to >= this.parent.getBookPageCount()) {
			from = this.parent.getCurrPage();
			to = this.parent.getCurrPage();
		}
		
		Ghostwriter.GLOBAL_CLIPBOARD.miscPages.clear();
		List<String> pagesAsList = this.parent.pagesAsList();
		for (int i = from; i <= to; i++) {
			Ghostwriter.GLOBAL_CLIPBOARD.miscPages.add(pagesAsList.get(i));
		}
		
		this.removePages(this.selectedPageA, this.selectedPageB);
		this.bookChanged(true);
		Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "" + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() + " page" + (Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() == 1 ? "" : "s") + " cut");
	}
	
	
	private void insertPage() {
		if (this.parent.getBookPageCount() < SharedConstants.MAX_BOOK_PAGES) {
			this.parent.insertNewPage(this.parent.getCurrPage(), "");
			Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Page inserted");
			this.bookChanged(false);
		} else {
			Ghostwriter.PRINTER.gamePrint(Printer.RED + "Cannot add another page! Book is full!");
		}
	}
	
	
	private void pasteBook() {
		this.clipboardToBook(Ghostwriter.GLOBAL_CLIPBOARD);
		this.bookChanged(true);
		Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Book pasted");
	}
	
	
	private void pasteMultiplePages(int startPos) {
		// Idiot proofing
		if (startPos < 0) {
			startPos = 0;
		} else if (startPos >= this.parent.getBookPageCount()) {
			startPos = this.parent.getBookPageCount() - 1;
		}
		
		List<String> oldBook = this.parent.pagesAsList();
		int newBookSize = this.parent.getBookPageCount() + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size();
		
		for (int i = startPos; i < newBookSize; i++) {
			if (i >= this.parent.getBookPageCount()) {
				// Add a blank page at the end of the book
				this.parent.insertNewPage(this.parent.getBookPageCount(), "");  // This used to use the vanilla addNewPage() method
			}
			if (i >= (startPos + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size())) {
				this.parent.setPageText(i, oldBook.get(i - Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size()));
			} else {
				this.parent.setPageText(i, Ghostwriter.GLOBAL_CLIPBOARD.miscPages.get(i - startPos));
			}
		}
		this.bookChanged(true);
		Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "" + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() + " page" +
				(Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() == 1 ? "" : "s") + " pasted");
	}
	
	
	private void addSignaturePages() {
		Clipboard temp = new Clipboard();
		Clipboard clip = new Clipboard();
		temp.clone(Ghostwriter.GLOBAL_CLIPBOARD);
		Ghostwriter.GLOBAL_CLIPBOARD.clearBook();
		Ghostwriter.GLOBAL_CLIPBOARD.miscPages.clear();
		FileHandler fh = new FileHandler(clip);
		File sigFile = new File(Ghostwriter.FILE_HANDLER.getSignaturePath(), "default.ghb");
		if (fh.loadBook(sigFile) && clip.bookInClipboard) {
			this.parent.insertNewPage(this.parent.getBookPageCount(), "");
			Ghostwriter.GLOBAL_CLIPBOARD.miscPages.addAll(clip.pages);
			pasteMultiplePages(this.parent.getBookPageCount() - 1);
			Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Signature pages added");
			removePages(this.parent.getBookPageCount() - 1, this.parent.getBookPageCount() - 1);
		} else {
			Ghostwriter.PRINTER.gamePrint(Printer.RED + "Couldn't load " + sigFile + " Does it exist?");
		}
		Ghostwriter.GLOBAL_CLIPBOARD.clone(temp);
	}
	
	
	private void removePages(int start, int end) {
		int from = Math.min(start, end);
		int to = Math.max(start, end);
		
		// Switch to single page mode if necessary
		if (from < 0 || from >= this.parent.getBookPageCount() || to >= this.parent.getBookPageCount()) {
			from = this.parent.getCurrPage();
			to = this.parent.getCurrPage();
		}
		
		//Make sure we're not going to find ourselves in a page that's being removed
		if (from > 0) {
			this.parent.setCurrPage(from - 1);
		} else {
			this.parent.setCurrPage(0);
		}
		
		List<String> oldPages = this.parent.pagesAsList();
		int newBookSize = this.parent.getBookPageCount() - ((to - from) + 1);
		for (int i = this.parent.getBookPageCount() - 1; i >= from; i--) {
			if (i > newBookSize - 1) {
				if (i == 0) {
					this.parent.setPageText(0, "");
				} else {
					//remove excess page
					this.parent.removePage(i);
				}
			} else {
				this.parent.setPageText(i, oldPages.get(i + (to - from) + 1));
			}
		}
		
		// Ensure we're not left with a truly empty book
		if (this.parent.getBookPageCount() == 0) {
			this.parent.insertNewPage(0, "");
		}
		
		this.bookChanged(true);
	}
	
	
	/**
	 * Removes whitespace from the top and bottom of the current page without affecting subsequent pages
	 */
	private void collapseTop() {
		String trimmed = this.parent.getPageText(this.parent.getCurrPage()).trim();
		this.parent.setPageText(this.parent.getCurrPage(), trimmed);
		this.bookChanged(false);
		Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Leading whitespace removed");
	}
	
	
	/**
	 * Copies a book from the clipboard into the 'real' book
	 */
	public void clipboardToBook(Clipboard fromBook) {
		this.setBookTitle(fromBook.title);
		this.parent.setBookTitle(this.bookTitle);
		this.parent.replaceBookPages(fromBook.pages);
		if (this.parent.getBookPageCount() == 0) this.parent.insertNewPage(0, "");
		this.bookChanged(true);
	}
	
	
	public void enableAutoReload(File path, Clipboard initialBookState) {
		this.autoReloadClipboard = initialBookState;
		this.autoReloadFile = path;
		this.autoReloadLastModified = path.lastModified();
		this.autoReloadLastCheck = System.currentTimeMillis();
		this.updateButtons();
	}
	
	
	public void tick() {
		// Handle auto reload
		if (this.autoReloadFile != null && System.currentTimeMillis() - this.autoReloadLastCheck > 1000) {
			if (this.autoReloadFile.exists()) {
				if (this.autoReloadFile.lastModified() != this.autoReloadLastModified) {
					FileHandler f = new FileHandler(this.autoReloadClipboard);
					if (f.loadBook(this.autoReloadFile)) {
						this.clipboardToBook(this.autoReloadClipboard);
						Ghostwriter.PRINTER.gamePrint(Printer.AQUA + "Automatically loaded new book version");
						this.autoReloadLastModified = this.autoReloadFile.lastModified();
					} else {
						Ghostwriter.PRINTER.gamePrint(Printer.RED + "Book failed to automatically reload!");
						this.disableAutoReload();
					}
					this.autoReloadLastCheck = System.currentTimeMillis();
				}
			} else {
				Ghostwriter.PRINTER.gamePrint(Printer.RED + "Source file disappeared!");
				this.disableAutoReload();
			}
		}
	}
	
	
	public void disableAutoReload() {
		this.autoReloadFile = null;
		Ghostwriter.PRINTER.gamePrint(Printer.AQUA + "Auto reload disabled");
		this.updateButtons();
	}
	
	
	public void updateButtons() {
		this.parent.updateVanillaButtons();
		if (!this.buttonsInitialized) return;
		
		// Set visibility for buttons hidden while signing
		for (Button b : this.buttonsHideWhileSigning) {
			b.visible = !this.parent.isBookBeingSigned();
		}
		
		// Set visibility for buttons that aren't visible in read-only modes
		// Note that if the button has been hidden by the loop above, it won't be shown by this loop
		for (Button b : this.buttonsEditOnly) {
			b.visible = this.bookIsEditable && b.visible;
		}
		
		// Reset invalid selection
		if (this.selectedPageA < -1 || this.selectedPageA >= this.parent.getBookPageCount()) {
			this.selectedPageA = -1;
		}
		if (this.selectedPageB < -1 || this.selectedPageB >= this.parent.getBookPageCount()) {
			this.selectedPageB = -1;
		}
		
		if (this.selectedPageA >= 0 && this.selectedPageB >= 0 && this.selectedPageA != this.selectedPageB) {
			// Multi page selection
			this.buttonCopySelectedPages.active = true;
			String xPages = (Math.abs(this.selectedPageB - this.selectedPageA) + 1) + " Pages";
			this.buttonCopySelectedPages.setMessage(Component.translatable("Copy " + xPages));
			this.buttonCutMultiplePages.setMessage(Component.translatable("Cut " + xPages));
			this.buttonRemoveSelectedPages.setMessage(Component.translatable("Remove " + xPages));
			this.buttonSelectPageA.setMessage(Component.translatable("A: " + (this.selectedPageA + 1)));
			this.buttonSelectPageB.setMessage(Component.translatable("B: " + (this.selectedPageB + 1)));
		} else {
			this.buttonCopySelectedPages.setMessage(Component.translatable("Copy This Page"));
			this.buttonCutMultiplePages.setMessage(Component.translatable("Cut This Page"));
			this.buttonRemoveSelectedPages.setMessage(Component.translatable("Remove This Page"));
			this.buttonSelectPageA.setMessage(Component.translatable("A"));
			this.buttonSelectPageB.setMessage(Component.translatable("B"));
			if (this.selectedPageA >= 0) {
				this.buttonSelectPageA.setMessage(Component.translatable("A: " + (this.selectedPageA + 1)));
			}
			if (this.selectedPageB >= 0) {
				this.buttonSelectPageB.setMessage(Component.translatable("B: " + (this.selectedPageB + 1)));
			}
		}
		
		this.buttonPasteBook.active = Ghostwriter.GLOBAL_CLIPBOARD.bookInClipboard;
		
		this.buttonPasteMultiplePages.active = (Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() > 0);
		if (this.buttonPasteMultiplePages.active) {
			this.buttonPasteMultiplePages.setMessage(Component.translatable("Paste " + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() + " Page" + ((Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() == 1) ? "" : "s")));
		} else {
			this.buttonPasteMultiplePages.setMessage(Component.translatable("Paste Multiple"));
		}
		
		this.buttonDisableAutoReload.active = this.autoReloadFile != null;
	}

	
	/**
	 * Helper method for buttons that need to be selectively hidden
	 * By default it creates the widest ghostwriter buttons, but you can use Button.setWidth() to make them thinner
	 */
	private Button addPageButton(int x, int y, String label, Button.OnPress action, boolean hideInReadOnlyMode){
		int width = SharedConstants.LARGE_BUTTON_WIDTH;
		int height = SharedConstants.BUTTON_HEIGHT;
		Button b = this.parent.addGhostButton(GuiUtils.buttonFactory(x, y, width, height, label, action));
		this.buttonsHideWhileSigning.add(b);
		if (hideInReadOnlyMode) this.buttonsEditOnly.add(b);
		return b;
	}
	

	private Button addColorFormatButton(int y, String label, String formattingCode) {
		Button b = this.addPageButton(
			this.colorFormatButtonX,
			y,
			label,
			(pressed_button) -> this.parent.addFormattingCode(formattingCode),
			true
		);
		b.setWidth(SharedConstants.COLOR_FORMAT_BUTTON_WIDTH);
		this.colorFormatButtonX += SharedConstants.COLOR_FORMAT_BUTTON_WIDTH;
		return b;
	}
	
	
	public void init() {
		this.buttonsEditOnly.clear();
		this.buttonsHideWhileSigning.clear();
		int rightXPos = this.screen.width - (SharedConstants.LARGE_BUTTON_WIDTH + SharedConstants.BUTTON_SIDE_OFFSET);
		
		////////////////////////////////////  Left side buttons  ///////////////////////////////////////////////
		this.addPageButton(5, 5, "File Browser",
				(pressed_button) -> MINECRAFT.setScreen(new GhostwriterFileBrowserScreen(this)),false);
		
		this.buttonDisableAutoReload = this.addPageButton(5, 45, "Disable AutoReload",
				(pressed_button) -> this.disableAutoReload(),true);
		
		this.addPageButton(5, 70, "Add Signature Pages", (pressed_button) -> this.addSignaturePages(), true);
		
		this.addPageButton(5, 95, "Preview Signed Book", (pressed_button) -> MINECRAFT.setScreen(
				new GhostwriterSignedPreviewScreen((GhostwriterEditBookScreen) this.screen)), true);
		
		////////////////////////////////////  Right side buttons  ////////////////////////////////////////////
		this.addPageButton(rightXPos, 5, "Copy Book", (pressed_button) -> this.copyBook(), false);
		
		this.buttonPasteBook = this.addPageButton(rightXPos, 25, "Paste Book",
				(pressed_button) -> this.pasteBook(), true);
		
		this.buttonSelectPageA = this.addPageButton(rightXPos, 50, "A", (pressed_button) -> {
			this.selectedPageA = this.parent.getCurrPage();
			this.updateButtons();
		}, false);
		this.buttonSelectPageA.setWidth(SharedConstants.LARGE_BUTTON_WIDTH / 2);
		
		int buttonBX = rightXPos + SharedConstants.LARGE_BUTTON_WIDTH / 2;
		this.buttonSelectPageB = this.addPageButton(buttonBX, 50, "B", (pressed_button) -> {
			this.selectedPageB = this.parent.getCurrPage();
			this.updateButtons();
		}, false);
		this.buttonSelectPageB.setWidth(SharedConstants.LARGE_BUTTON_WIDTH / 2);
		
		this.buttonCopySelectedPages = this.addPageButton(rightXPos, 70, "Copy This Page",
				(pressed_button) -> this.copySelectedPagesToClipboard(), false);
		
		this.buttonCutMultiplePages = this.addPageButton(rightXPos, 90, "Cut This Page",
				(pressed_button) -> this.cutMultiplePages(), true);
		
		this.buttonRemoveSelectedPages = this.addPageButton(rightXPos, 110, "Remove This Page",
				(pressed_button) -> this.removePages(this.selectedPageA, this.selectedPageB), true);
		
		this.buttonPasteMultiplePages = this.addPageButton(rightXPos, 130, "Paste This Page",
				(pressed_button) -> this.pasteMultiplePages(this.parent.getCurrPage()), true);
		
		this.addPageButton(rightXPos, 155,"Insert Blank Page", (pressed_button) -> this.insertPage(), true);
		
		this.addPageButton(rightXPos, 175, "Remove Top Space", (pressed_button) -> this.collapseTop(), true);
		
		///////////////////////////////////////  Underside buttons  ///////////////////////////////////////////
		this.colorFormatButtonX = this.screen.width / 2 - (SharedConstants.COLOR_FORMAT_BUTTON_WIDTH * 8);
		int colorButY = this.screen.height - 40;
		this.addColorFormatButton(colorButY, "§0A", "§0");  // BLACK
		this.addColorFormatButton(colorButY, "§1A", "§1");  // DARK_BLUE
		this.addColorFormatButton(colorButY, "§2A", "§2");  // DARK_GREEN
		this.addColorFormatButton(colorButY, "§3A", "§3");  // DARK_AQUA
		this.addColorFormatButton(colorButY, "§4A", "§4");  // DARK_RED
		this.addColorFormatButton(colorButY, "§5A", "§5");  // DARK_PURPLE
		this.addColorFormatButton(colorButY, "§6A", "§6");  // GOLD
		this.addColorFormatButton(colorButY, "§7A", "§7");  // GRAY
		this.addColorFormatButton(colorButY, "§8A", "§8");  // DARK_GRAY
		this.addColorFormatButton(colorButY, "§9A", "§9");  // BLUE
		this.addColorFormatButton(colorButY, "§aA", "§a");  // GREEN
		this.addColorFormatButton(colorButY, "§bA", "§b");  // AQUA
		this.addColorFormatButton(colorButY, "§cA", "§c");  // RED
		this.addColorFormatButton(colorButY, "§dA", "§d");  // LIGHT_PURPLE
		this.addColorFormatButton(colorButY, "§eA", "§e");  // YELLOW
		this.addColorFormatButton(colorButY, "§fA", "§f");  // WHITE
		
		this.colorFormatButtonX = this.screen.width / 2 - (SharedConstants.COLOR_FORMAT_BUTTON_WIDTH * 5);
		int formatButY = this.screen.height - 20;
		this.addColorFormatButton(formatButY, "§kA", "§k");  // OBFUSCATED
		this.addColorFormatButton(formatButY, "§lA", "§l");  // BOLD
		this.addColorFormatButton(formatButY, "§mA", "§m");  // STRIKETHROUGH
		this.addColorFormatButton(formatButY, "§nA", "§n");  // UNDERLINE
		this.addColorFormatButton(formatButY, "§oA", "§o");  // ITALIC
		this.addColorFormatButton(formatButY, "Reset Formatting", "§r").setWidth(100);
		
		this.buttonsInitialized = true;
		this.updateButtons();
	}
	
	public void saveBookToDisk(File filepath) {
		List<String> pages = this.parent.pagesAsList();
		Ghostwriter.FILE_HANDLER.saveBookToGHBFile(this.bookTitle, this.bookAuthor, pages, filepath);
	}
}
