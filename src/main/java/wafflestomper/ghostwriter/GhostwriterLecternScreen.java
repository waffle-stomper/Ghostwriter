package wafflestomper.ghostwriter;

import net.minecraft.client.gui.screen.LecternScreen;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GhostwriterLecternScreen extends LecternScreen {

	private Button buttonSelectPageA;
	private Button buttonSelectPageB;

	//Used for copying multiple pages at a time
	private int selectedPageA = -1;
	private int selectedPageB = -1;

	private final Clipboard CLIPBOARD;
	private static final Printer printer = new Printer();
	private final FileHandler fileHandler;
	private Button buttonCopySelectedPages;
	private String bookTitle = "";
	private String bookAuthor = "";

	private final LecternContainer LECTERN_CONTAINER;


	public GhostwriterLecternScreen(ItemStack currStack,
									Clipboard globalClipboard, LecternContainer LECTERN_CONTAINER,
									PlayerInventory playerInventory) {
		// Not sure why it needs the inventory and text. Both params are ignored by the constructor
		super(LECTERN_CONTAINER, playerInventory, new StringTextComponent(""));
		this.LECTERN_CONTAINER = LECTERN_CONTAINER;
		this.CLIPBOARD = globalClipboard;
		this.fileHandler = new FileHandler(this.CLIPBOARD);
		if (currStack != null){
			CompoundNBT compoundnbt = currStack.getTag();
			if (compoundnbt != null) {
				this.bookTitle = compoundnbt.getString("title");
				this.bookAuthor = compoundnbt.getString("author");
			}
		}
	}


	/**
	 * Helper function that extracts the pages from the read book until I find a cleaner way to do this
	 * @return Pages as a list of Strings
	 */
	public List<String> bookPages(){
		if (this.bookInfo instanceof ReadBookScreen.WrittenBookInfo) {
			ReadBookScreen.WrittenBookInfo b = (ReadBookScreen.WrittenBookInfo)this.bookInfo;
			return b.pages;
		}
		else if (this.bookInfo instanceof ReadBookScreen.UnwrittenBookInfo) {
			ReadBookScreen.UnwrittenBookInfo b = (ReadBookScreen.UnwrittenBookInfo)this.bookInfo;
			return b.pages;
		}
		else {
			return new ArrayList<>();
		}
	}

	@Deprecated  // We should be able to just use bookPages directly now
	private List<String> pagesAsList(){
		List<String> pages = new ArrayList<>();
		for (int i=0; i<this.getPageCount(); i++){
			// Ugly hack to convert the new JSON "Yo dawg I heard you like strings, so I put a string in your string" strings
			//  back to the old-style literal strings that everyone knows and loves. I'll update this to do the opposite once
			//  we're finally allowed to send JSON strings to the server. It also converts to old-school formatting codes
			String pageText = BookUtilities.deJSONify(this.bookPages().get(i)); // TODO: Should this use the getPage function from IBookInfo instead?
			pages.add(pageText);
		}
		return pages;
	}


	private void copyBook() {
		this.CLIPBOARD.author = this.bookAuthor;
		this.CLIPBOARD.title = this.bookTitle;
		this.CLIPBOARD.pages.clear();
		this.CLIPBOARD.pages.addAll(this.pagesAsList());
		this.CLIPBOARD.bookInClipboard = true;
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

		if (firstPage >= 0 && lastPage >= firstPage && lastPage < this.bookPages().size()){
			this.CLIPBOARD.miscPages.clear();
			List<String> pagesAsList = this.pagesAsList();
			for (int i=firstPage; i<=lastPage; i++){
				this.CLIPBOARD.miscPages.add(pagesAsList.get(i));
			}
			printer.gamePrint(Printer.GRAY + "Selection copied");
		}
		else{
			printer.gamePrint(Printer.RED + "Invalid selection! Copy aborted.");
		}
		this.updateButtons();
	}

	@Override
	public void init() {
		int buttonWidth = 120;
		int buttonHeight = 20;
		int buttonSideOffset = 5;
		int rightXPos = this.width-(buttonWidth+buttonSideOffset);

		this.addButton(new Button(5, 5, buttonWidth, buttonHeight, new StringTextComponent("File Browser"), (pressed_button) -> {
			if (this.minecraft != null) {
				this.minecraft.displayGuiScreen(new GhostwriterFileBrowserScreen(this));
			}
		}));

		this.addButton(new Button(rightXPos, 5, buttonWidth, buttonHeight, new StringTextComponent("Copy Book"),
				(pressed_button) -> this.copyBook()));

		this.buttonSelectPageA = this.addButton(new Button(rightXPos, 50, buttonWidth/2, buttonHeight,
				new StringTextComponent("A"), (pressed_button) -> {
			this.selectedPageA = this.currPage;
			this.updateButtons();
		}));

		this.buttonSelectPageB = this.addButton(new Button(rightXPos+buttonWidth/2, 50, buttonWidth/2,
				buttonHeight, new StringTextComponent("B"), (pressed_button) -> {
			this.selectedPageB = this.currPage;
			this.updateButtons();
		}));

		this.buttonCopySelectedPages = 		this.addButton(new Button(rightXPos, 70, buttonWidth, buttonHeight,
				new StringTextComponent("Copy This Page"),
				(pressed_button) -> this.copySelectedPagesToClipboard()));

		super.init();
		this.updateButtons();
		// This is a hack based on LecternScreen.func_214176_h()
		// Books can be left open to a specific page on a lectern. This displays that page.
		// Otherwise we'd just be showing the first page every time
		this.showPage(this.LECTERN_CONTAINER.getPage());
	}


	public void saveBookToDisk(File filepath) {
		List<String> pages = new ArrayList<>();
		for (int i=0; i<this.getPageCount(); i++) {
			// func_230456_a_ is the old getPageText and getString() converts it to a string with formatting codes
			String s = this.bookInfo.func_230456_a_(i).getString();
			pages.add(s);
		}
		this.fileHandler.saveBookToGHBFile(this.bookTitle, this.bookAuthor, pages, filepath);
	}


	@Override
	public void updateButtons() {
		super.updateButtons();

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
			this.buttonSelectPageA.setMessage(new StringTextComponent("A: " + (this.selectedPageA+1)));
			this.buttonSelectPageB.setMessage(new StringTextComponent("B: " + (this.selectedPageB+1)));
		}
		else{
			this.buttonCopySelectedPages.setMessage(new StringTextComponent("Copy This Page"));
			this.buttonSelectPageA.setMessage(new StringTextComponent("A"));
			this.buttonSelectPageB.setMessage(new StringTextComponent("B"));
			if (this.selectedPageA >= 0) {
				this.buttonSelectPageA.setMessage(new StringTextComponent("A: " + (this.selectedPageA+1)));
			}
			if (this.selectedPageB >= 0) {
				this.buttonSelectPageB.setMessage(new StringTextComponent("B: " + (this.selectedPageB+1)));
			}
		}
	}
}
