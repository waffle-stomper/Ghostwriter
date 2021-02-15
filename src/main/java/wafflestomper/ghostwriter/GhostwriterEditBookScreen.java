package wafflestomper.ghostwriter;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@OnlyIn(Dist.CLIENT)
public class GhostwriterEditBookScreen extends EditBookScreen {
	
	private static final int BUTTON_HEIGHT = 20;
	private static final int COLOR_FORMAT_BUTTON_WIDTH = 20;
	
	private Button buttonDisableAutoReload;
	private Button buttonPasteBook;
	private Button buttonCutMultiplePages;
	private Button buttonSelectPageA;
	private Button buttonSelectPageB;
	private Button buttonCopySelectedPages;
	private Button buttonPasteMultiplePages;
	private Button buttonRemoveSelectedPages;
	private final List<Button> buttonsHideWhileSigning = new ArrayList<>();
	
	//Used for copying multiple pages at a time
	private int selectedPageA = -1;
	private int selectedPageB = -1;
	
	private Clipboard clipboard;
	
	private File autoReloadFile; // Auto Reload is active when this is not null
	public long autoReloadLastModified = 0;
	private long autoReloadLastCheck = 0;
	private Clipboard autoReloadClipboard;
	
	private static final Printer printer = new Printer();
	private final FileHandler fileHandler;
	
	private int currPageLineCount = 0;
	private long lastPageLineCountUpdate = 0;
	
	
	public GhostwriterEditBookScreen(PlayerEntity editingPlayer, ItemStack book, Hand hand, Clipboard clipboard) {
		super(editingPlayer, book, hand);
		this.clipboard = clipboard;
		this.fileHandler = new FileHandler(this.clipboard);
		
		// Swap out the title input util for one that allows longer titles
		// WrittenBookItem.validBookTagContents declares the book invalid if the title is over 32 characters
		field_238749_v_ = new TextInputUtil(
				() -> this.bookTitle,
				(p_238772_1_) -> this.bookTitle = p_238772_1_,
				this::func_238773_g_,  // getClipboardText
				this::func_238760_a_,  // setClipboardText
				(p_238771_0_) -> p_238771_0_.length() <= SharedConstants.BOOK_TITLE_MAX_LEN);
	}
	
	// TODO: Why do we use this for cut/copy/paste but not for saving?
	@Deprecated  // I don't think this is needed for unsigned books. As far as I can tell, JSON is only used for signed
	private List<String> pagesAsList(){
		List<String> pages = new ArrayList<>();
		for (int i=0; i<this.getPageCount(); i++){
			// Ugly hack to convert the new JSON "Yo dawg I heard you like strings, so I put a string in your string" strings
			//  back to the old-style literal strings that everyone knows and loves. I'll update this to do the opposite once
			//  we're finally allowed to send JSON strings to the server. It also converts to old-school formatting codes
			String pageText = BookUtilities.deJSONify(this.bookPages.get(i));
			pages.add(pageText);
		}
		return pages;
	}
	
	
	private void bookChanged(boolean resetPageSelection){
		this.bookIsModified = true;
		this.cachedPage = -1;
		
		if (resetPageSelection){
			this.selectedPageA = -1;
			this.selectedPageB = -1;
		}
		
		this.updateButtons();
		
		// field_238748_u_ is the new TextInputUtil. If we don't move the cursor, the game can crash because
		// text in it doesn't match what's in the current bookPages String
		this.field_238748_u_.moveCursorToEnd();
		
		// This is some kind of new display update/refresh function
		// It must be called every time the book's content changes
		this.func_238751_C_();
	}
	
	
	private void removePages(int start, int end){
		int from = Math.min(start, end);
		int to = Math.max(start, end);
		
		// Switch to single page mode if necessary
		if (from < 0 || from >= this.bookPages.size() || to >= this.bookPages.size()) {
			from = this.currPage;
			to = this.currPage;
		}
		
		//Make sure we're not going to find ourselves in a page that's being removed
		if (from > 0){
			this.currPage = from-1;
		}
		else{
			this.currPage = 0;
		}
		
		List<String> oldPages = pagesAsList();
		int newBookSize = this.bookPages.size() - ((to-from)+1);
		for (int i=bookPages.size()-1; i>=from; i--){
			if (i > newBookSize-1){
				if (i == 0){
					this.bookPages.set(0, "");
				}
				else{
					//remove excess page
					this.bookPages.remove(i);
				}
			}
			else{
				this.bookPages.set(i, oldPages.get(i + (to-from) + 1));
			}
		}

		// Ensure we're not left with a truly empty book
		if (this.bookPages.isEmpty()) {
			this.bookPages.add("");
		}
		
		this.bookChanged(true);
	}
	
	
	/**
	 * Removes whitespace from the top and bottom of the current page without affecting subsequent pages
	 */
	private void collapseTop() {
		String trimmed = this.getCurrPageText().trim();
		this.bookPages.set(this.currPage, trimmed);
		this.bookChanged(false);
		printer.gamePrint(Printer.GRAY + "Leading whitespace removed");
	}
	
	
	/*
	 * Copies a book from the clipboard into the 'real' book
	 */
	public void clipboardToBook(Clipboard fromBook){
		// Reset anything in the current book
		this.bookPages.clear();
		this.bookChanged(true);
		
		this.bookTitle = fromBook.title;
		this.field_238749_v_.moveCursorToEnd();  // field_238749_v_ is titleInput
		
		this.bookPages.addAll(fromBook.pages);
		this.bookIsModified = true;
		
		if (this.bookPages.isEmpty()) {
			this.bookPages.add("");
		}
		this.updateButtons();
	}
	
	
	private void copyBook(){
		this.clipboard.author = "";
		this.clipboard.title = this.bookTitle;
		this.clipboard.pages.clear();
		this.clipboard.pages.addAll(this.pagesAsList());
		this.clipboard.bookInClipboard = true;
		printer.gamePrint(Printer.GRAY + "Book copied");
		this.updateButtons();
	}
	
	
	private void copySelectedPagesToClipboard(){
		int firstPage = Math.min(this.selectedPageA, this.selectedPageB);
		int lastPage = Math.max(this.selectedPageA, this.selectedPageB);
		
		// Handle the case where A or B is -1 (i.e. no selection)
		if (firstPage == -1 || lastPage == -1) {
			firstPage = this.currPage;
			lastPage = this.currPage;
		}
		
		if (firstPage >= 0 && lastPage >= firstPage && lastPage < this.bookPages.size()){
			this.clipboard.miscPages.clear();
			List<String> pagesAsList = this.pagesAsList();
			for (int i=firstPage; i<=lastPage; i++){
				this.clipboard.miscPages.add(pagesAsList.get(i));
			}
			printer.gamePrint(Printer.GRAY + "Selection copied");
		}
		else{
			printer.gamePrint(Printer.RED + "Invalid selection! Copy aborted.");
		}
		this.updateButtons();
	}
	
	
	private void cutMultiplePages(){
		int from = Math.min(this.selectedPageA, this.selectedPageB);
		int to = Math.max(this.selectedPageA, this.selectedPageB);
		
		// Switch to single page mode if necessary
		if (from < 0 || from >= this.bookPages.size() || to >= this.bookPages.size()) {
			from = this.currPage;
			to = this.currPage;
		}
		
		this.clipboard.miscPages.clear();
		List<String> pagesAsList = this.pagesAsList();
		for (int i=from; i<=to; i++){
			this.clipboard.miscPages.add(pagesAsList.get(i));
		}
		
		this.removePages(this.selectedPageA, this.selectedPageB);
		this.bookChanged(true);
		printer.gamePrint(Printer.GRAY + "" + this.clipboard.miscPages.size() + " page" + (this.clipboard.miscPages.size()==1?"":"s") + " cut");
	}
	
	
	private void insertPage() {
		if (this.bookPages.size() < SharedConstants.MAX_BOOK_PAGES) {
			this.bookPages.add(this.currPage, "");
			printer.gamePrint(Printer.GRAY + "Page inserted");
			this.bookChanged(false);
		}
		else {
			printer.gamePrint(Printer.RED + "Cannot add another page! Book is full!");
		}
	}
	
	
	private void pasteBook(){
		this.clipboardToBook(this.clipboard);
		this.bookChanged(true);
		printer.gamePrint(Printer.GRAY + "Book pasted");
	}


	private void pasteMultiplePages(int startPos) {
		// Idiot proofing
		if (startPos < 0) {
			startPos = 0;
		}
		else if (startPos >= this.bookPages.size()) {
			startPos = this.bookPages.size()-1;
		}
		
		List<String> oldBook = this.pagesAsList();
		int newBookSize = this.bookPages.size() + this.clipboard.miscPages.size();
		
		for (int i=startPos; i<newBookSize; i++){
			if (i >= this.bookPages.size()){
				addNewPage();
			}
			if (i >= (startPos + this.clipboard.miscPages.size())){
				this.bookPages.set(i, oldBook.get(i-this.clipboard.miscPages.size()));
			}
			else{
				this.bookPages.set(i, this.clipboard.miscPages.get(i-startPos));
			}
		}
		this.bookChanged(false);
		printer.gamePrint(Printer.GRAY + "" + this.clipboard.miscPages.size() + " page" +
				(this.clipboard.miscPages.size()==1?"":"s") + " pasted");
	}
	
	
	private void addSignaturePages(){
		Clipboard temp = new Clipboard();
		Clipboard clip = new Clipboard();
		temp.clone(this.clipboard);
		this.clipboard.clearBook();
		this.clipboard.miscPages.clear();
		FileHandler fh = new FileHandler(clip);
		File sigFile = new File(this.fileHandler.getSignaturePath(), "default.ghb");
		if (fh.loadBook(sigFile) && clip.bookInClipboard){
			addNewPage();
			this.clipboard.miscPages.addAll(clip.pages);
			pasteMultiplePages(this.bookPages.size()-1);
			printer.gamePrint(Printer.GRAY + "Signature pages added");
			removePages(this.bookPages.size()-1, this.bookPages.size()-1);
		}
		else{
			printer.gamePrint(Printer.RED + "Couldn't load " + sigFile + " Does it exist?");
		}
		this.clipboard.clone(temp);
	}

	
	public void insertTextIntoPage(String text) {
		if (this.bookGettingSigned){
			this.field_238749_v_.putText(text);
			return;
		}
		this.field_238748_u_.putText(text);
	}
	
	/**
	 * Helper method for buttons that need to be hidden when the signing screen is active
	 */
	protected Button addPageButton(Button button) {
		Button b = super.addButton(button);
		this.buttonsHideWhileSigning.add(b);
		return b;
	}
	
	
	/**
	 * @return X co-ordinate of the next button
	 */
	private int addColorFormatButton(int x, int y, String label, String insertChars, int width){
		this.addButton(new Button(x, y,  width, BUTTON_HEIGHT, new StringTextComponent(label),
				(pressed_button) -> this.insertTextIntoPage(insertChars)));
		return x + 20;
	}
	
	
	/**
	 * Helper overload so we don't have to specify width for most buttons
	 * @return X co-ordinate of the next button
	 */
	private int addColorFormatButton(int x, int y, String label, String insertChars){
		return this.addColorFormatButton(x, y, label, insertChars, COLOR_FORMAT_BUTTON_WIDTH);
	}
	
	
	@Override
	public void init(){
		this.buttonsHideWhileSigning.clear();
		
		int buttonWidth = 120;
		int buttonSideOffset = 5;
		int rightXPos = this.width-(buttonWidth+buttonSideOffset);
		
		this.addPageButton(new Button(5, 5, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("File Browser"),(pressed_button) -> {
					if (this.minecraft != null) {
						this.minecraft.displayGuiScreen(new GhostwriterFileBrowserScreen(this));
					}
				}));
		
		this.buttonDisableAutoReload = this.addPageButton(new Button(5, 45, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Disable AutoReload"), (pressed_button) -> this.disableAutoReload()));
		
		this.addPageButton(new Button(rightXPos, 5, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Copy Book"), (pressed_button) -> this.copyBook()));
		
		this.buttonPasteBook = this.addPageButton(new Button(rightXPos, 25, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Paste Book"), (pressed_button) -> this.pasteBook()));
		
		this.buttonSelectPageA = this.addPageButton(new Button(rightXPos, 50, buttonWidth/2, BUTTON_HEIGHT,
				new StringTextComponent("A"), (pressed_button) -> {
			this.selectedPageA = this.currPage;
			this.updateButtons();
		}));
		
		this.buttonSelectPageB = this.addPageButton(new Button(rightXPos+buttonWidth/2, 50, buttonWidth/2,
				BUTTON_HEIGHT, new StringTextComponent("B"), (pressed_button) -> {
			this.selectedPageB = this.currPage;
			this.updateButtons();
		}));
		
		this.buttonCopySelectedPages = this.addPageButton(new Button(rightXPos, 70, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Copy This Page"),
				(pressed_button) -> this.copySelectedPagesToClipboard()));
		
		this.buttonCutMultiplePages = this.addPageButton(new Button(rightXPos, 90, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Cut This Page"), (pressed_button) -> this.cutMultiplePages()));
		
		this.buttonPasteMultiplePages = this.addPageButton(new Button(rightXPos, 130, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Paste This Page"),
				(pressed_button) -> this.pasteMultiplePages(this.currPage)));
		
		this.addPageButton(new Button(rightXPos, 155, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Insert Blank Page"), (pressed_button) -> this.insertPage()));
		
		this.addPageButton(new Button(rightXPos, 175, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Remove Top Space"), (pressed_button) -> this.collapseTop()));
		
		this.addPageButton(new Button(5, 70, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Add Signature Pages"), (pressed_button) -> this.addSignaturePages()));
		
		this.addPageButton(new Button(5, 95, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Preview Signed Book"), (pressed_button) -> {
			if (this.minecraft != null) {
				this.minecraft.displayGuiScreen(new GhostwriterSignedPreviewScreen(this));
			}
		}));
		
		this.buttonRemoveSelectedPages = this.addPageButton(new Button(rightXPos, 110, buttonWidth, BUTTON_HEIGHT,
				new StringTextComponent("Remove This Page"),
				(pressed_button) -> this.removePages(this.selectedPageA, this.selectedPageB)));
		
		int colorButX = this.width/2 - (COLOR_FORMAT_BUTTON_WIDTH * 8);
		int colorButY = this.height - 40;
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a70A", "\u00a70");  // BLACK
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a71A", "\u00a71");  // DARK_BLUE
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a72A", "\u00a72");  // DARK_GREEN
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a73A", "\u00a73");  // DARK_AQUA
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a74A", "\u00a74");  // DARK_RED
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a75A", "\u00a75");  // DARK_PURPLE
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a76A", "\u00a76");  // GOLD
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a77A", "\u00a77");  // GRAY
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a78A", "\u007a8");  // DARK_GRAY
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a79A", "\u00a79");  // BLUE
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a7aA", "\u00a7a");  // GREEN
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a7bA", "\u00a7b");  // AQUA
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a7cA", "\u00a7c");  // RED
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a7dA", "\u00a7d");  // LIGHT_PURPLE
		colorButX = addColorFormatButton(colorButX, colorButY, "\u00a7eA", "\u00a7e");  // YELLOW
		addColorFormatButton(colorButX, colorButY, "\u00a7fA", "\u00a7f");              // WHITE

		int formatButX = this.width/2 - (COLOR_FORMAT_BUTTON_WIDTH * 5);
		int formatButY = this.height - 20;
		formatButX = addColorFormatButton(formatButX, formatButY, "\u00a7kA", "\u00a7k");  // OBFUSCATED
		formatButX = addColorFormatButton(formatButX, formatButY, "\u00a7lA", "\u00a7l");  // BOLD
		formatButX = addColorFormatButton(formatButX, formatButY, "\u00a7mA", "\u00a7m");  // STRIKETHROUGH
		formatButX = addColorFormatButton(formatButX, formatButY, "\u00a7nA", "\u00a7n");  // UNDERLINE
		formatButX = addColorFormatButton(formatButX, formatButY, "\u00a7oA", "\u00a7o");  // ITALIC
		addColorFormatButton(formatButX, formatButY, "Reset Formatting", "\u00a7r", 100);
		
		super.init();
		
		// Move standard buttons
		this.buttonSign.setWidth(buttonWidth);
		this.buttonFinalize.setWidth(buttonWidth);
		this.buttonDone.setWidth(buttonWidth);
		this.buttonCancel.setWidth(buttonWidth);
		this.buttonSign.x = buttonSideOffset;
		this.buttonFinalize.x = buttonSideOffset;
		this.buttonDone.x = buttonSideOffset;
		this.buttonCancel.x =  buttonSideOffset;
		this.buttonSign.y = 120;
		this.buttonFinalize.y = 120;
		this.buttonDone.y = 145;
		this.buttonCancel.y = 145;
		
		this.updateButtons();
	}
	
	
	public void saveBookToDisk(File filepath) {
		this.fileHandler.saveBookToGHBFile("", "", this.bookPages, filepath);
	}
	
	
	@Override
	public void tick() {
		// Handle auto reload
		if (this.autoReloadFile != null && System.currentTimeMillis()-this.autoReloadLastCheck > 1000) {
			if (this.autoReloadFile.exists()) {
				if (this.autoReloadFile.lastModified() != this.autoReloadLastModified) {
					FileHandler f = new FileHandler(this.autoReloadClipboard);
					if (f.loadBook(this.autoReloadFile)) {
						this.clipboardToBook(this.autoReloadClipboard);
						printer.gamePrint(Printer.AQUA + "Automatically loaded new book version");
						this.autoReloadLastModified = this.autoReloadFile.lastModified();
					}
					else {
						printer.gamePrint(Printer.RED + "Book failed to automatically reload!");
						this.disableAutoReload();
					}
					this.autoReloadLastCheck = System.currentTimeMillis();
				}
			}
			else {
				printer.gamePrint(Printer.RED + "Source file disappeared!");
				this.disableAutoReload();
			}
		}
		
		// Update the count for flashing cursors etc.
		++this.updateCount;
	}
	
	
	@Override
	public void updateButtons() {
		super.updateButtons();
		
		// Set visibility for buttons hidden while signing
		// This has to be done first because some buttons will be hidden below (e.g. disable auto reload)
		for (Button b: this.buttonsHideWhileSigning){
			b.visible = !this.bookGettingSigned;
		}
		
		// Reset invalid selection
		if (this.selectedPageA < -1 || this.selectedPageA >= this.getPageCount()) {
			this.selectedPageA = -1;
		}
		if (this.selectedPageB < -1 || this.selectedPageB >= this.getPageCount()) {
			this.selectedPageB = -1;
		}
		
		if (this.selectedPageA >= 0 && this.selectedPageB >= 0 && this.selectedPageA != this.selectedPageB){
			// Multi page selection
			this.buttonCopySelectedPages.active = true;
			String xPages = (Math.abs(this.selectedPageB-this.selectedPageA)+1) + " Pages";
			this.buttonCopySelectedPages.setMessage(new StringTextComponent("Copy " + xPages));
			this.buttonCutMultiplePages.setMessage(new StringTextComponent("Cut " + xPages));
			this.buttonRemoveSelectedPages.setMessage(new StringTextComponent("Remove " + xPages));
			this.buttonSelectPageA.setMessage(new StringTextComponent("A: " + (this.selectedPageA+1)));
			this.buttonSelectPageB.setMessage(new StringTextComponent("B: " + (this.selectedPageB+1)));
		}
		else{
			this.buttonCopySelectedPages.setMessage(new StringTextComponent("Copy This Page"));
			this.buttonCutMultiplePages.setMessage(new StringTextComponent("Cut This Page"));
			this.buttonRemoveSelectedPages.setMessage(new StringTextComponent("Remove This Page"));
			this.buttonSelectPageA.setMessage(new StringTextComponent("A"));
			this.buttonSelectPageB.setMessage(new StringTextComponent("B"));
			if (this.selectedPageA >= 0) {
				this.buttonSelectPageA.setMessage(new StringTextComponent("A: " + (this.selectedPageA+1)));
			}
			if (this.selectedPageB >= 0) {
				this.buttonSelectPageB.setMessage(new StringTextComponent("B: " + (this.selectedPageB+1)));
			}
		}
		
		this.buttonPasteBook.active = this.clipboard.bookInClipboard;
		
		this.buttonPasteMultiplePages.active = (this.clipboard.miscPages.size() > 0);
		if (this.buttonPasteMultiplePages.active){
			this.buttonPasteMultiplePages.setMessage(new StringTextComponent("Paste " + this.clipboard.miscPages.size() + " Page" + ((this.clipboard.miscPages.size()==1)?"":"s")));
		}
		else{
			this.buttonPasteMultiplePages.setMessage(new StringTextComponent("Paste Multiple"));
		}
		
		this.buttonDisableAutoReload.visible = this.autoReloadFile != null && !this.bookGettingSigned;
		
		// Trim book title to a max of 32 characters. Anything longer an the book will be marked invalid by
		// the client when you try to read it
		// updateButtons() is called when the 'sign' button is clicked, so it's a convenient time to check this
		if (this.bookTitle.length() > 32){
			this.bookTitle = this.bookTitle.substring(0, 32);
		}
	}
	
	
	public String getBookTitle() {
		return this.bookTitle;
	}
	
	
	public void setClipboard(Clipboard _clipboard){
		this.clipboard = _clipboard;
	}
	
	
	public void enableAutoReload(File path, Clipboard initialBookState) {
		this.autoReloadClipboard = initialBookState;
		this.autoReloadFile = path;
		this.autoReloadLastModified = path.lastModified();
		this.autoReloadLastCheck = System.currentTimeMillis();
		this.updateButtons();
	}
	
	
	public void disableAutoReload() {
		this.autoReloadFile = null;
		printer.gamePrint(Printer.AQUA + "Auto reload disabled");
		this.updateButtons();
	}
	
	
	/**
	 * Counts the lines on the current page, returning a cached version most of the time
	 *
	 * Unfortunately the vanilla code makes it borderline impossible to extract the word-wrapped lines that
	 * EditBookScreen uses in render(), so we have to split it ourselves
	 *
	 * Note that this will need to be updated periodically so that it matches the code in EditBookScreen
	 */
	public int getCurrPageLineCount(){
		if (System.currentTimeMillis() - lastPageLineCountUpdate >= 250) {
			this.currPageLineCount = BookUtilities.splitIntoPages(this.getCurrPageText(), 0).get(0).lines.length;
			this.lastPageLineCountUpdate = System.currentTimeMillis();
		}
		return this.currPageLineCount;
	}
	
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(MatrixStack matrixStack , int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		
		// Render long title and warning (if necessary)
		if (this.bookGettingSigned && this.bookTitle.length() > 15){
			// Show the title length
			String textLen = "Title length: " + this.bookTitle.length();
			// params are matrixStack, x, y, color
			this.font.drawString(matrixStack, textLen, 169, 20, 0xFF3333);
			
			// Add extra background width amd re-render the title because the new background covers up the vanilla title
			String textTitle = this.bookTitle;
			textTitle += (this.updateCount / 6 % 2 == 0 ? TextFormatting.BLACK : TextFormatting.GRAY) + "_";
			int bookLeftSide = (this.width - 192) / 2;
			int titleWidth = this.getTextWidth(textTitle);
			int titleMinX = bookLeftSide + 36 + (114 - titleWidth) / 2;
			int titleMaxX = titleMinX + titleWidth;
			// color for the fill() method is MSB->LSB: alpha, r, g, b, (each 8 bits)
			AbstractGui.fill(matrixStack, titleMinX - 5, 48, titleMaxX + 5, 60, 0xFFFFF9EC);
			this.font.drawString(matrixStack, textTitle, (float)(titleMinX), 50.0F, 0);
			
			// Show the long title warning
			String s = "Warning: the vanilla client restricts titles to 15 characters. " +
					   "Set longer titles at your own risk";
			StringTextComponent lengthWarning = new StringTextComponent(s);
			// params are text, x, y, width, color
			this.font.func_238418_a_(lengthWarning, 153, 116, 114, 0xFF3333);
		}
		
		// Add warnings about character and line limits
		// Things get weird over 256 characters, so we don't bother showing the line warning in that case
		if (!this.bookGettingSigned) {
			String warning = "";
			if (this.getCurrPageText().length() > 256){
				warning = "Over 256 char limit!";
			}
			else if (this.getCurrPageLineCount() > BookUtilities.BOOK_MAX_LINES){
				warning = "Over " + BookUtilities.BOOK_MAX_LINES + " line limit!";
			}
			
			if (warning.length() > 0) {
				this.font.drawString(matrixStack, "Warning:", 5, 176, 0xFF3333);
				this.font.drawString(matrixStack, warning, 5, 185, 0xFF3333);
			}
		}
	}
	
	
	@Override
	public boolean keyPressedInTitle(int keyCode, int scanCode, int modifiers) {
		if (Screen.isPaste(keyCode)) {
			this.field_238749_v_.insertClipboardText();
			return true;
		}
		return super.keyPressedInTitle(keyCode, scanCode, modifiers);
	}
}
