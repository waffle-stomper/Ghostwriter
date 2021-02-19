package wafflestomper.ghostwriter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

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
	// Note that EditBookScreen has its own bookTitle field that we need to keep track of
	public String bookTitle = "";
	public String bookAuthor = "";
	
	private boolean buttonsInitialized = false;
	
	
	public GhostLayer(IGhostBook parent, Screen screenParent, boolean bookIsEditable){
		this.parent = parent;
		this.screen = screenParent;  // TODO: Is there a cleaner way to do this?
		this.bookIsEditable = bookIsEditable;
	}
	
	
	public void setTitleAuthor(String title, String author) {
		this.bookTitle = title;
		this.bookAuthor = author;
	}
	
	
	private void bookChanged(boolean resetPageSelection) {
		if (resetPageSelection) {
			this.selectedPageA = -1;
			this.selectedPageB = -1;
		}
		this.parent.bookChanged(true);
		this.updateButtons();
	}
	
	private void copyBook(){
		Ghostwriter.GLOBAL_CLIPBOARD.author = "";
		Ghostwriter.GLOBAL_CLIPBOARD.title = this.bookTitle;
		Ghostwriter.GLOBAL_CLIPBOARD.pages.clear();
		Ghostwriter.GLOBAL_CLIPBOARD.pages.addAll(this.parent.pagesAsList());
		Ghostwriter.GLOBAL_CLIPBOARD.bookInClipboard = true;
		Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Book copied");
		this.updateButtons();
	}
	
	
	private void copySelectedPagesToClipboard(){
		int firstPage = Math.min(this.selectedPageA, this.selectedPageB);
		int lastPage = Math.max(this.selectedPageA, this.selectedPageB);
		
		// Handle the case where A or B is -1 (i.e. no selection)
		if (firstPage == -1 || lastPage == -1) {
			firstPage = this.parent.getCurrPage();
			lastPage = this.parent.getCurrPage();
		}
		
		if (firstPage >= 0 && lastPage >= firstPage && lastPage < this.parent.getBookPageCount()){
			Ghostwriter.GLOBAL_CLIPBOARD.miscPages.clear();
			List<String> pagesAsList = this.parent.pagesAsList();
			for (int i=firstPage; i<=lastPage; i++){
				Ghostwriter.GLOBAL_CLIPBOARD.miscPages.add(pagesAsList.get(i));
			}
			Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Selection copied");
		}
		else{
			Ghostwriter.PRINTER.gamePrint(Printer.RED + "Invalid selection! Copy aborted.");
		}
		this.updateButtons();
	}
	
	
	private void cutMultiplePages(){
		int from = Math.min(this.selectedPageA, this.selectedPageB);
		int to = Math.max(this.selectedPageA, this.selectedPageB);
		
		// Switch to single page mode if necessary
		if (from < 0 || from >= this.parent.getBookPageCount() || to >= this.parent.getBookPageCount()) {
			from = this.parent.getCurrPage();
			to = this.parent.getCurrPage();
		}
		
		Ghostwriter.GLOBAL_CLIPBOARD.miscPages.clear();
		List<String> pagesAsList = this.parent.pagesAsList();
		for (int i=from; i<=to; i++){
			Ghostwriter.GLOBAL_CLIPBOARD.miscPages.add(pagesAsList.get(i));
		}
		
		this.removePages(this.selectedPageA, this.selectedPageB);
		this.bookChanged(true);
		Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "" + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() + " page" + (Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size()==1?"":"s") + " cut");
	}
	
	
	private void insertPage() {
		if (this.parent.getBookPageCount() < SharedConstants.MAX_BOOK_PAGES) {
			this.parent.insertNewPage(this.parent.getCurrPage(), "");
			Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Page inserted");
			this.bookChanged(false);
		}
		else {
			Ghostwriter.PRINTER.gamePrint(Printer.RED + "Cannot add another page! Book is full!");
		}
	}
	
	
	private void pasteBook(){
		this.clipboardToBook(Ghostwriter.GLOBAL_CLIPBOARD);
		this.bookChanged(true);
		Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Book pasted");
	}
	
	
	private void pasteMultiplePages(int startPos) {
		// Idiot proofing
		if (startPos < 0) {
			startPos = 0;
		}
		else if (startPos >= this.parent.getBookPageCount()) {
			startPos = this.parent.getBookPageCount()-1;
		}
		
		List<String> oldBook = this.parent.pagesAsList();
		int newBookSize = this.parent.getBookPageCount() + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size();
		
		for (int i=startPos; i<newBookSize; i++){
			if (i >= this.parent.getBookPageCount()){
				// Add a blank page at the end of the book
				this.parent.insertNewPage(this.parent.getBookPageCount(), "");  // This used to use the vanilla addNewPage() method
			}
			if (i >= (startPos + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size())){
				this.parent.setPageText(i, oldBook.get(i-Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size()));
			}
			else{
				this.parent.setPageText(i, Ghostwriter.GLOBAL_CLIPBOARD.miscPages.get(i-startPos));
			}
		}
		this.bookChanged(true);
		Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "" + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() + " page" +
				(Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size()==1?"":"s") + " pasted");
	}
	
	
	private void addSignaturePages(){
		Clipboard temp = new Clipboard();
		Clipboard clip = new Clipboard();
		temp.clone(Ghostwriter.GLOBAL_CLIPBOARD);
		Ghostwriter.GLOBAL_CLIPBOARD.clearBook();
		Ghostwriter.GLOBAL_CLIPBOARD.miscPages.clear();
		FileHandler fh = new FileHandler(clip);
		File sigFile = new File(Ghostwriter.FILE_HANDLER.getSignaturePath(), "default.ghb");
		if (fh.loadBook(sigFile) && clip.bookInClipboard){
			this.parent.insertNewPage(this.parent.getBookPageCount(), "");  // TODO: Is this necessary?
			Ghostwriter.GLOBAL_CLIPBOARD.miscPages.addAll(clip.pages);
			pasteMultiplePages(this.parent.getBookPageCount()-1);
			Ghostwriter.PRINTER.gamePrint(Printer.GRAY + "Signature pages added");
			removePages(this.parent.getBookPageCount()-1, this.parent.getBookPageCount()-1);
		}
		else{
			Ghostwriter.PRINTER.gamePrint(Printer.RED + "Couldn't load " + sigFile + " Does it exist?");
		}
		Ghostwriter.GLOBAL_CLIPBOARD.clone(temp);
	}
	
	
	private void removePages(int start, int end){
		int from = Math.min(start, end);
		int to = Math.max(start, end);
		
		// Switch to single page mode if necessary
		if (from < 0 || from >= this.parent.getBookPageCount() || to >= this.parent.getBookPageCount()) {
			from = this.parent.getCurrPage();
			to = this.parent.getCurrPage();
		}
		
		//Make sure we're not going to find ourselves in a page that's being removed
		if (from > 0){
			this.parent.setCurrPage(from-1);
		}
		else{
			this.parent.setCurrPage(0);
		}
		
		List<String> oldPages = this.parent.pagesAsList();
		int newBookSize = this.parent.getBookPageCount() - ((to-from)+1);
		for (int i=this.parent.getBookPageCount()-1; i>=from; i--){
			if (i > newBookSize-1){
				if (i == 0){
					this.parent.setPageText(0, "");
				}
				else{
					//remove excess page
					this.parent.removePage(i);
				}
			}
			else{
				this.parent.setPageText(i, oldPages.get(i + (to-from) + 1));
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
	public void clipboardToBook(Clipboard fromBook){
		this.bookTitle = fromBook.title;
		this.parent.setBookTitle(this.bookTitle);
		this.parent.replaceBookPages(fromBook.pages);
		if (this.parent.getBookPageCount() == 0) this.parent.insertNewPage(0,"");
		this.bookChanged(true);
	}
	
	
	public void enableAutoReload(File path, Clipboard initialBookState) {
		this.autoReloadClipboard = initialBookState;
		this.autoReloadFile = path;
		this.autoReloadLastModified = path.lastModified();
		this.autoReloadLastCheck = System.currentTimeMillis();
		this.updateButtons();
	}
	
	
	public void tick(){
		// Handle auto reload
		if (this.autoReloadFile != null && System.currentTimeMillis()-this.autoReloadLastCheck > 1000) {
			if (this.autoReloadFile.exists()) {
				if (this.autoReloadFile.lastModified() != this.autoReloadLastModified) {
					FileHandler f = new FileHandler(this.autoReloadClipboard);
					if (f.loadBook(this.autoReloadFile)) {
						this.clipboardToBook(this.autoReloadClipboard);
						Ghostwriter.PRINTER.gamePrint(Printer.AQUA + "Automatically loaded new book version");
						this.autoReloadLastModified = this.autoReloadFile.lastModified();
					}
					else {
						Ghostwriter.PRINTER.gamePrint(Printer.RED + "Book failed to automatically reload!");
						this.disableAutoReload();
					}
					this.autoReloadLastCheck = System.currentTimeMillis();
				}
			}
			else {
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
		for (Button b: this.buttonsHideWhileSigning){
			b.visible = !this.parent.isBookBeingSigned();
		}
		
		// Set visibility for buttons that aren't visible in read-only modes
		// Note that if the button has been hidden by the loop above, it won't be shown by this loop
		for (Button b: this.buttonsEditOnly){
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
			this.buttonCopySelectedPages.setMessage(new StringTextComponent("Copy " + xPages));
			this.buttonCutMultiplePages.setMessage(new StringTextComponent("Cut " + xPages));
			this.buttonRemoveSelectedPages.setMessage(new StringTextComponent("Remove " + xPages));
			this.buttonSelectPageA.setMessage(new StringTextComponent("A: " + (this.selectedPageA + 1)));
			this.buttonSelectPageB.setMessage(new StringTextComponent("B: " + (this.selectedPageB + 1)));
		} else {
			this.buttonCopySelectedPages.setMessage(new StringTextComponent("Copy This Page"));
			this.buttonCutMultiplePages.setMessage(new StringTextComponent("Cut This Page"));
			this.buttonRemoveSelectedPages.setMessage(new StringTextComponent("Remove This Page"));
			this.buttonSelectPageA.setMessage(new StringTextComponent("A"));
			this.buttonSelectPageB.setMessage(new StringTextComponent("B"));
			if (this.selectedPageA >= 0) {
				this.buttonSelectPageA.setMessage(new StringTextComponent("A: " + (this.selectedPageA + 1)));
			}
			if (this.selectedPageB >= 0) {
				this.buttonSelectPageB.setMessage(new StringTextComponent("B: " + (this.selectedPageB + 1)));
			}
		}
		
		this.buttonPasteBook.active = Ghostwriter.GLOBAL_CLIPBOARD.bookInClipboard;
		
		this.buttonPasteMultiplePages.active = (Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() > 0);
		if (this.buttonPasteMultiplePages.active){
			this.buttonPasteMultiplePages.setMessage(new StringTextComponent("Paste " + Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size() + " Page" + ((Ghostwriter.GLOBAL_CLIPBOARD.miscPages.size()==1)?"":"s")));
		}
		else{
			this.buttonPasteMultiplePages.setMessage(new StringTextComponent("Paste Multiple"));
		}
		
		this.buttonDisableAutoReload.active = this.autoReloadFile != null;
		
		// TODO: Is there a better place to do this?
		// Trim book title to a max of 32 characters. Anything longer an the book will be marked invalid by
		// the client when you try to read it
		// updateButtons() is called when the 'sign' button is clicked, so it's a convenient time to check this
		if (this.bookTitle.length() > 32){
			this.bookTitle = this.bookTitle.substring(0, 32);
		}
	}
	
	
	/**
	 * Helper method for buttons that need to be selectively hidden
	 */
	protected Button addPageButton(Button button, boolean hideInReadOnlyMode) {
		Button b = this.parent.addGhostButton(button);
		this.buttonsHideWhileSigning.add(b);
		if (hideInReadOnlyMode) this.buttonsEditOnly.add(b);
		return b;
	}

	
	private Button addColorFormatButton(int y, String label, String insertChars){
		Button b = this.addPageButton(new Button(this.colorFormatButtonX, y,  SharedConstants.COLOR_FORMAT_BUTTON_WIDTH,
				SharedConstants.BUTTON_HEIGHT, new StringTextComponent(label),
				(pressed_button) -> this.parent.insertText(insertChars)), true);
		this.colorFormatButtonX += SharedConstants.COLOR_FORMAT_BUTTON_WIDTH;
		return b;
	}
	
	
	// TODO: Add a method with default button params so we don't have to specify them for every button (especially height)
	public void init(){
		this.buttonsEditOnly.clear();
		this.buttonsHideWhileSigning.clear();
		int rightXPos = this.screen.width-(SharedConstants.LARGE_BUTTON_WIDTH+SharedConstants.BUTTON_SIDE_OFFSET);
		
		////////////////////////////////////  Left side buttons  ///////////////////////////////////////////////
		this.addPageButton(new Button(5, 5, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("File Browser"), (pressed_button) -> {
						// TODO: Pass the IGhostBook this file browser rather than the parent screen
						MINECRAFT.displayGuiScreen(new GhostwriterFileBrowserScreen(this));
					}
				), false);
		
		this.buttonDisableAutoReload = this.addPageButton(new Button(5, 45, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Disable AutoReload"), (pressed_button) -> this.disableAutoReload()),
				true);
		
		this.addPageButton(new Button(5, 70, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Add Signature Pages"), (pressed_button) -> this.addSignaturePages()), true);
		
		this.addPageButton(new Button(5, 95, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Preview Signed Book"), (pressed_button) ->
				MINECRAFT.displayGuiScreen(new GhostwriterSignedPreviewScreen((GhostwriterEditBookScreen) this.screen))), true);
		
		
		////////////////////////////////////  Right side buttons  ////////////////////////////////////////////
		this.addPageButton(new Button(rightXPos, 5, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Copy Book"), (pressed_button) -> this.copyBook()), false);
		
		this.buttonPasteBook = this.addPageButton(new Button(rightXPos, 25, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Paste Book"), (pressed_button) -> this.pasteBook()), true);
		
		this.buttonSelectPageA = this.addPageButton(new Button(rightXPos, 50, SharedConstants.LARGE_BUTTON_WIDTH/2, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("A"), (pressed_button) -> {
			this.selectedPageA = this.parent.getCurrPage();
			this.updateButtons();
		}), false);

		this.buttonSelectPageB = this.addPageButton(new Button(rightXPos+SharedConstants.LARGE_BUTTON_WIDTH/2, 50, SharedConstants.LARGE_BUTTON_WIDTH/2,
				SharedConstants.BUTTON_HEIGHT, new StringTextComponent("B"), (pressed_button) -> {
			this.selectedPageB = this.parent.getCurrPage();
			this.updateButtons();
		}), false);

		this.buttonCopySelectedPages = this.addPageButton(new Button(rightXPos, 70, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Copy This Page"), (pressed_button) -> this.copySelectedPagesToClipboard()), false);
		
		this.buttonCutMultiplePages = this.addPageButton(new Button(rightXPos, 90, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Cut This Page"), (pressed_button) -> this.cutMultiplePages()), true);
		
		this.buttonRemoveSelectedPages = this.addPageButton(new Button(rightXPos, 110, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Remove This Page"),
				(pressed_button) -> this.removePages(this.selectedPageA, this.selectedPageB)), true);
		
		this.buttonPasteMultiplePages = this.addPageButton(new Button(rightXPos, 130, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Paste This Page"),
				(pressed_button) -> this.pasteMultiplePages(this.parent.getCurrPage())), true);
		
		this.addPageButton(new Button(rightXPos, 155, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Insert Blank Page"), (pressed_button) -> this.insertPage()), true);
		
		this.addPageButton(new Button(rightXPos, 175, SharedConstants.LARGE_BUTTON_WIDTH, SharedConstants.BUTTON_HEIGHT,
				new StringTextComponent("Remove Top Space"), (pressed_button) -> this.collapseTop()), true);
		
		///////////////////////////////////////  Underside buttons  ///////////////////////////////////////////
		
		this.colorFormatButtonX = this.screen.width/2 - (SharedConstants.COLOR_FORMAT_BUTTON_WIDTH * 8);
		int colorButY = this.screen.height - 40;
		this.addColorFormatButton(colorButY, "\u00a70A", "\u00a70");  // BLACK
		this.addColorFormatButton(colorButY, "\u00a71A", "\u00a71");  // DARK_BLUE
		this.addColorFormatButton(colorButY, "\u00a72A", "\u00a72");  // DARK_GREEN
		this.addColorFormatButton(colorButY, "\u00a73A", "\u00a73");  // DARK_AQUA
		this.addColorFormatButton(colorButY, "\u00a74A", "\u00a74");  // DARK_RED
		this.addColorFormatButton(colorButY, "\u00a75A", "\u00a75");  // DARK_PURPLE
		this.addColorFormatButton(colorButY, "\u00a76A", "\u00a76");  // GOLD
		this.addColorFormatButton(colorButY, "\u00a77A", "\u00a77");  // GRAY
		this.addColorFormatButton(colorButY, "\u00a78A", "\u007a8");  // DARK_GRAY
		this.addColorFormatButton(colorButY, "\u00a79A", "\u00a79");  // BLUE
		this.addColorFormatButton(colorButY, "\u00a7aA", "\u00a7a");  // GREEN
		this.addColorFormatButton(colorButY, "\u00a7bA", "\u00a7b");  // AQUA
		this.addColorFormatButton(colorButY, "\u00a7cA", "\u00a7c");  // RED
		this.addColorFormatButton(colorButY, "\u00a7dA", "\u00a7d");  // LIGHT_PURPLE
		this.addColorFormatButton(colorButY, "\u00a7eA", "\u00a7e");  // YELLOW
		this.addColorFormatButton(colorButY, "\u00a7fA", "\u00a7f");  // WHITE
		
		this.colorFormatButtonX = this.screen.width/2 - (SharedConstants.COLOR_FORMAT_BUTTON_WIDTH * 5);
		int formatButY = this.screen.height - 20;
		this.addColorFormatButton(formatButY, "\u00a7kA", "\u00a7k");  // OBFUSCATED
		this.addColorFormatButton(formatButY, "\u00a7lA", "\u00a7l");  // BOLD
		this.addColorFormatButton(formatButY, "\u00a7mA", "\u00a7m");  // STRIKETHROUGH
		this.addColorFormatButton(formatButY, "\u00a7nA", "\u00a7n");  // UNDERLINE
		this.addColorFormatButton(formatButY, "\u00a7oA", "\u00a7o");  // ITALIC
		this.addColorFormatButton(formatButY, "Reset Formatting", "\u00a7r").setWidth(100);  // Reset
		
		this.buttonsInitialized = true;
		this.updateButtons();
	}
	
	public void saveBookToDisk(File filepath) {
		List<String> pages = this.parent.pagesAsList();
		Ghostwriter.FILE_HANDLER.saveBookToGHBFile(this.bookTitle, this.bookAuthor, pages, filepath);
	}
}
