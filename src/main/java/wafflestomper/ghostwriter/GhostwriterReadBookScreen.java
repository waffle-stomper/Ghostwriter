package wafflestomper.ghostwriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import wafflestomper.ghostwriter.modified_mc_files.ReadBookScreenMod;

public class GhostwriterReadBookScreen extends ReadBookScreenMod {
	
	private Button buttonSelectPageA;
	private Button buttonSelectPageB;
	
    //Used for copying multiple pages at a time
    private int selectedPageA = -1;
    private int selectedPageB = -1;
    
    private Clipboard clipboard;
    private static final Printer printer = new Printer();
	private static final Logger LOG = LogManager.getLogger();
	private final FileHandler fileHandler;
	private Button buttonCopySelectedPages;
	private String bookTitle = "";
	private String bookAuthor = "";
	
	private LecternContainer lecternContainer;


	public GhostwriterReadBookScreen(ReadBookScreenMod.IBookInfo bookInfoIn, boolean pageTurnSoundsIn, ItemStack currStack, Clipboard globalClipboard, LecternContainer lecternContainer) {
        super(bookInfoIn, pageTurnSoundsIn);
        this.clipboard = globalClipboard;
        this.fileHandler = new FileHandler(this.clipboard);
        this.lecternContainer = lecternContainer;
    	if (currStack != null){
    		Item currItem = currStack.getItem();
    		if (currItem != null){
    	        CompoundNBT compoundnbt = currStack.getTag();
    	        if (compoundnbt != null) {
    	        	this.bookTitle = compoundnbt.getString("title");
    	        	this.bookAuthor = compoundnbt.getString("author");
    	        }
    		}
    	}
    }
	
//  The vanilla ReadBookScreen has these other constructors but I don't think we need them	
//	public GhostwriterReadBookScreen(ReadBookScreenMod.IBookInfo bookInfoIn) {
//        this(bookInfoIn, true);
//    }
//
//	
//    public GhostwriterReadBookScreen() {
//        this(EMPTY_BOOK, false);
//    }
    
    
    /**
     * Helper function that extracts the pages from the read book until I find a cleaner way to do this
     * @return
     */
    public List<String> bookPages(){
    	if (this.bookInfo instanceof ReadBookScreenMod.WrittenBookInfo) {
    		ReadBookScreenMod.WrittenBookInfo b = (ReadBookScreenMod.WrittenBookInfo)this.bookInfo;
    		return b.pages;
    	}
    	else if (this.bookInfo instanceof ReadBookScreenMod.UnwrittenBookInfo) {
    		ReadBookScreenMod.UnwrittenBookInfo b = (ReadBookScreenMod.UnwrittenBookInfo)this.bookInfo;
    		return b.pages;
    	}
    	else {
    		return new ArrayList<String>();
    	}
	}
    
    
    private List<String> pagesAsList(){
    	List<String> pages = new ArrayList<String>();
    	for (int i=0; i<this.getPageCount(); i++){
            // Ugly hack to convert the new JSON "Yo dawg I heard you like strings, so I put a string in your string" strings 
            //  back to the old-style literal strings that everyone knows and loves. I'll update this to do the opposite once
            //  we're finally allowed to send JSON strings to the server. It also converts to oldschool formatting codes
    		String pageText = BookUtilities.deJSONify(this.bookPages().get(i)); // TODO: Should this use the getPage function from IBookInfo instead?
    		pages.add(pageText);
    	}
    	return pages;
    }
    
    
    private void copyBook() {
		this.clipboard.author = this.bookAuthor;
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
    	
    	if (firstPage >= 0 && lastPage >= firstPage && lastPage < this.bookPages().size()){
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
    
    
    private void takeLecternBook(int p_214179_1_){
    	if (this.lecternContainer == null) {
    		printer.gamePrint(Printer.RED + "Error: LecternContainer is null!");
    		return;
    	}
        this.minecraft.playerController.sendEnchantPacket(this.lecternContainer.windowId, p_214179_1_);
    }
    
    
    @Override
	public void addDoneButton() {
        if (this.minecraft.player.isAllowEdit() && this.lecternContainer != null){
           this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.format("gui.done"), (p_214181_1_) -> {
              this.minecraft.displayGuiScreen((Screen)null);
           }));
           this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.format("lectern.take_book"), (p_214178_1_) -> {
              this.takeLecternBook(3);
           }));
        } else {
           super.addDoneButton();
        }

     }
    
    
    @Override
	public void init() {
		// TODO: Should this only happen once? (i.e. have an initialized field)
		
		// Note that you can use the parameter? in the lambda function like this:
		// pressed_button.x += 20; // neat!
		
		//KeyboardHelper.enableRepeatEvents(true);
		
        int buttonWidth = 120;
        int buttonHeight = 20;
        int buttonSideOffset = 5;
        
        // Is this no longer a thing?
        //ScaledResolution scaledResolution = new ScaledResolution(this.mc);
  		//int rightXPos = scaledResolution.getScaledWidth() - (buttonWidth + buttonSideOffset);
        // Temporary hack
        int rightXPos = this.width-(buttonWidth+buttonSideOffset);
		
		this.addButton(new Button(5, 5, buttonWidth, buttonHeight, "File Browser", (pressed_button) -> {
			this.minecraft.displayGuiScreen(new GhostwriterFileBrowserScreen(this));
		}));
		
		this.addButton(new Button(rightXPos, 5, buttonWidth, buttonHeight, "Copy Book", (pressed_button) -> {
			this.copyBook();
		}));
		
		this.buttonSelectPageA = 			this.addButton(new Button(rightXPos, 50, buttonWidth/2, buttonHeight, "A", (pressed_button) -> {
			this.selectedPageA = this.currPage;
			this.updateButtons();
		}));
		
		this.buttonSelectPageB = 			this.addButton(new Button(rightXPos+buttonWidth/2, 50, buttonWidth/2, buttonHeight, "B", (pressed_button) -> {
			this.selectedPageB = this.currPage;
			this.updateButtons();
		}));
		
		this.buttonCopySelectedPages = 		this.addButton(new Button(rightXPos, 70, buttonWidth, buttonHeight, "Copy This Page", (pressed_button) -> {
			this.copySelectedPagesToClipboard();
		}));

		super.init();		
		this.updateButtons();
	}
	
	
	public void saveBookToDisk(File filepath) {
		List<String> pages = new ArrayList();
		for (int i=0; i<this.getPageCount(); i++) {
			String s = this.bookInfo.getPageText(i).getFormattedText();
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
    		String xPages = (Math.abs(this.selectedPageB-this.selectedPageA)+1) + " Page"  + ((this.selectedPageA!=this.selectedPageB)?"s":"");
    		this.buttonCopySelectedPages.setMessage("Copy " + xPages);
    		this.buttonSelectPageA.setMessage("A: " + (this.selectedPageA+1));
    		this.buttonSelectPageB.setMessage("B: " + (this.selectedPageB+1));
    	}
    	else{
    		this.buttonCopySelectedPages.setMessage("Copy This Page");
    		this.buttonSelectPageA.setMessage("A");
    		this.buttonSelectPageB.setMessage("B");
    		if (this.selectedPageA >= 0) {
    			this.buttonSelectPageA.setMessage("A: " + (this.selectedPageA+1));
    		}
    		if (this.selectedPageB >= 0) {
    			this.buttonSelectPageB.setMessage("B: " + (this.selectedPageB+1));
    		}
    	}
	}
	
	

}
