/**
 * 
 * 

NNNNNNNN        NNNNNNNN     OOOOOOOOO     TTTTTTTTTTTTTTTTTTTTTTTEEEEEEEEEEEEEEEEEEEEEE   SSSSSSSSSSSSSSS 
N:::::::N       N::::::N   OO:::::::::OO   T:::::::::::::::::::::TE::::::::::::::::::::E SS:::::::::::::::S
N::::::::N      N::::::N OO:::::::::::::OO T:::::::::::::::::::::TE::::::::::::::::::::ES:::::SSSSSS::::::S
N:::::::::N     N::::::NO:::::::OOO:::::::OT:::::TT:::::::TT:::::TEE::::::EEEEEEEEE::::ES:::::S     SSSSSSS
N::::::::::N    N::::::NO::::::O   O::::::OTTTTTT  T:::::T  TTTTTT  E:::::E       EEEEEES:::::S            
N:::::::::::N   N::::::NO:::::O     O:::::O        T:::::T          E:::::E             S:::::S            
N:::::::N::::N  N::::::NO:::::O     O:::::O        T:::::T          E::::::EEEEEEEEEE    S::::SSSS         
N::::::N N::::N N::::::NO:::::O     O:::::O        T:::::T          E:::::::::::::::E     SS::::::SSSSS    
N::::::N  N::::N:::::::NO:::::O     O:::::O        T:::::T          E:::::::::::::::E       SSS::::::::SS  
N::::::N   N:::::::::::NO:::::O     O:::::O        T:::::T          E::::::EEEEEEEEEE          SSSSSS::::S 
N::::::N    N::::::::::NO:::::O     O:::::O        T:::::T          E:::::E                         S:::::S
N::::::N     N:::::::::NO::::::O   O::::::O        T:::::T          E:::::E       EEEEEE            S:::::S
N::::::N      N::::::::NO:::::::OOO:::::::O      TT:::::::TT      EE::::::EEEEEEEE:::::ESSSSSSS     S:::::S
N::::::N       N:::::::N OO:::::::::::::OO       T:::::::::T      E::::::::::::::::::::ES::::::SSSSSS:::::S
N::::::N        N::::::N   OO:::::::::OO         T:::::::::T      E::::::::::::::::::::ES:::::::::::::::SS 
NNNNNNNN         NNNNNNN     OOOOOOOOO           TTTTTTTTTTT      EEEEEEEEEEEEEEEEEEEEEE SSSSSSSSSSSSSSS  




#########################################################################
#																		#
#                  FEATURE REQUESTS & STUFF TO DO                       #
#																		#
#########################################################################

TODO: Figure out what to do with books over 50 pages long (maybe put them in miscPages?)

TODO: text justification somehow?
  
  -automatically generate title page?
  


#########################################################################
#																		#
#                             BOOK FORMATS                              #
#																		#
#########################################################################

  ~~~~~~~~~~~~~~~~~~~~
  | GHB File format! |
  ~~~~~~~~~~~~~~~~~~~~
  * Java style comments, both single and multi-line, either of which are allowed to start at any point though a line (not just at the start)
  * Author and title are optional, but they must each be on their own line, and prefaced by author: and title: respectively
    For example:
      title:Kicking Over Sandcastles
      author:HCF_Kids
    The title and author keys can appear anywhere within the file, and are case insensitive
    Whitespace on either side of the value (e.g. 'title: The day of the Triffids ') will be ignored (the title would be read as 'The day of the Triffids')
    Only the first instance of each title and author will be accepted. Subsequent lines will be treated as part of the book.
  * Linebreaks are indicated by a pair of hashes (##) which can be repeated as many times as the user wants. The can be separated by a space but don't have to
    be. Any whitespace preceding a pair of hashes will be removed.
  * Ordinary linebreaks will be ignored
  * Pagebreaks are denoted by four 'greater than' angled brackets (>>>>)
    Whitespace preceding a pagebreak will be removed.
  * The linebreak and pagebreak symbols can be escaped with a single backslash if they need to be used literally
  * Blank lines will be removed
  * Whitespace at the end of a page will be removed
    
  Example file:
  
  	//This is a single line comment by waffle_stomper on 2014-05-28. It will not appear in the book
  	author:PETN //Comments can start at any point on a line
  	title:Truncating excessively long names
  	/* This is a multi-line comment
  	   None of this will be included in the book
  	   Please note that I had to put a space between the asterisk
  	   and forward-slash to prevent it from terminating the 
  	   comment that this is being posted in. In practice there should
  	   be no space between those two 
  	* /
  	This is the first page.##
  	This is going to be on a new line!
  	>>>>
  	This is the second page. The pagebreak character can go on the end of the line if you'd like.>>>>
  	This is the third page.
  	You 
  	can 
  	use 
  	as 
  	many 
  	lines 
  	as 
  	you 
  	like 
  	but anything between pagebreaks will be considered as one page (unless it's too long to fit on a single page).
  	Also, don't forget to insert a space 
  	if you're splitting a line //See the space there?
  	otherwise your words will be joined together.
  	>>>>
  	title: This is considered to be part of the text for the book.


- Inserting signature pages

- Get rid of the singlePage string in the clipboard. It should be handled in miscPages instead


- GUI pops up with the opening of both a signed and unsigned book (I found it unnecessary to have to press a key for that).

- Separate buttons for the formatting codes or some other way of easily entering them:
  1) Italic 2) Bold 3) Underline 4) Strikethrough 5) Obfuscated 6) Reset
  (and all the colours, if possible)
  
 - Some method to copy over the .txt format BookWorm books more easily, if at all possible
 
 
- Bookworm uses a double colon (::) as a paragraph break. We need to find a suitable substitute. Single linebreaks aren't enough, but page breaks are too
  much, espeically if someone uses a bunch of paragraph breaks in a row. (currently using hybrid approach)


#########################################################################
#																		#
#                          OTHER NOTES                                  #
#																		#
#########################################################################

Note on these notes: Some of the following information may be out of date. 
Mojang loves to change things for seemingly no reason.
Please double-check anything you find here to make sure it's still relevant.

 - TITLES CANNOT BE OVER 16 CHARACTERS LONG. THIS IS GOING TO BE AN ISSUE (for now I'm truncating them)

 - There is a 256 character/page limit. Thanks Mojang!

 - Are Bookworm pagebreaks always preceded by a space?

 - 13 lines long, but the font isn't monospaced, so there are an arbitrary number of characters on each line.

 - If you set the title before you hit the signing screen, the book is automatically signed. This probably isn't a huge issue though.

 - Bookworm save algorithm:
    writer.write(id+"");
	writer.newLine();
	writer.write(title);
	writer.newLine();
	writer.write(author);
	writer.newLine();
	for (String s : hiddenData.keySet()) {
	        writer.write("|!|" + s + "|" + hiddenData.get(s));
	        writer.newLine();
	}
	writer.write(text);
	writer.newLine();
	writer.close();
	
I imagine the files look like this:

###################################
46
Valentino Rossi - Portrait of a speed god
Mat Oxley
|!|hiddenkey0|hiddendata0
|!|hiddenkey1|hiddendata1
"The first time you ride the 500, it's like, FUCK!" -Valentino Rossi

###################################
 */


package wafflestomper.ghostwriter;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhostwriterEditBookScreen extends EditBookScreen {
	
	private static final int ID_SAVE_BOOK = 6;
	private static final int ID_LOAD_BOOK = 7;
	private static final int ID_COPY_BOOK = 10;
	private static final int ID_PASTE_BOOK = 11;
	private static final int ID_CUT_MULTIPLE_PAGES = 14;
	private static final int ID_SELECT_PAGE_A = 15;
	private static final int ID_SELECT_PAGE_B = 16;
	private static final int ID_COPY_SELECTED_PAGES = 17;
	private static final int ID_PASTE_MULTIPLE_PAGES = 18;
	private static final int ID_INSERT_PAGE = 19;
	private static final int ID_COLLAPSE_TOP = 20;
	private static final int ID_ADD_SIGNATURE_PAGES = 21;
	private static final int ID_REMOVE_SELECTED_PAGES = 22;
	private static final int ID_AUTO_RELOAD_BOOK = 23;
	
	private static final int ID_BLACK = 50;
	private static final int ID_DARK_BLUE = 51;
	private static final int ID_DARK_GREEN = 52;
	private static final int ID_DARK_AQUA = 53;
	private static final int ID_DARK_RED = 54;
	private static final int ID_DARK_PURPLE = 55;
	private static final int ID_GOLD = 56;
	private static final int ID_GRAY = 57;
	private static final int ID_DARK_GRAY = 58;
	private static final int ID_BLUE = 59;
	private static final int ID_GREEN = 60;
	private static final int ID_AQUA = 61;
	private static final int ID_RED = 62;
	private static final int ID_LIGHT_PURPLE = 63;
	private static final int ID_YELLOW = 64;
	private static final int ID_WHITE = 65;
	private static final int ID_OBFUSCATED = 66;
	private static final int ID_BOLD = 67;
	private static final int ID_STRIKETHROUGH = 68;
	private static final int ID_UNDERLINE = 69;
	private static final int ID_ITALIC = 70;
	private static final int ID_RESET_FORMAT = 71;
	private static final int ID_CURRENT_FORMAT = 72;
	
	private Button buttonFileBrowser;
	private Button buttonDisableAutoReload;
	private Button buttonCopyBook;
	private Button buttonPasteBook;
	private Button buttonCutMultiplePages;
	private Button buttonSelectPageA;
	private Button buttonSelectPageB;
	private Button buttonCopySelectedPages;
	private Button buttonPasteMultiplePages;
	private Button buttonInsertPage;
	private Button buttonCollapseTop;
	private Button buttonAddSignaturePages;
	private Button buttonRemoveSelectedPages;
	
	private Button formatBlack;
	private Button formatDarkBlue;
	private Button formatDarkGreen;
	private Button formatDarkAqua;
	private Button formatDarkRed;
	private Button formatDarkPurple;
	private Button formatGold;
	private Button formatGray;
	private Button formatDarkGray;
	private Button formatBlue;
	private Button formatGreen;
	private Button formatAqua;
	private Button formatRed;
	private Button formatLightPurple;
	private Button formatYellow;
	private Button formatWhite;
	private Button formatObfuscated;
	private Button formatBold;
	private Button formatStrikethrough;
	private Button formatUnderline;
	private Button formatItalic;
	private Button formatResetFormat;
	
    //Used for copying multiple pages at a time
    private int selectedPageA = -1;
    private int selectedPageB = -1;

    // Note that end can be less than start if you select text right to left
	// TODO: Incorporate text selection back into the new version
    private int selectionEnd = 0;
    private int selectionStart = 0;
    
    private Clipboard clipboard;
    
    private File autoReloadFile; // Auto Reload is active when this is not nullprivate Clipboard autoReloadBookClipboard;
	private long autoReloadLastModified = 0;
	private long autoReloadLastCheck = 0;
	private Clipboard autoReloadClipboard;
	
    private static final Printer printer = new Printer();
	private static final Logger LOG = LogManager.getLogger();
	private final FileHandler fileHandler;
	private static final int MAX_BOOK_PAGES = 100; // Find this magic number inside EditBookScreen.addNewPage()
	

	public GhostwriterEditBookScreen(PlayerEntity editingPlayer, ItemStack book, Hand hand, Clipboard clipboard) {
		super(editingPlayer, book, hand);
		this.clipboard = clipboard;
		this.fileHandler = new FileHandler(this.clipboard);
	}
	
	
    private List<String> pagesAsList(){
    	List<String> pages = new ArrayList<String>();
    	for (int i=0; i<this.getPageCount(); i++){
            // Ugly hack to convert the new JSON "Yo dawg I heard you like strings, so I put a string in your string" strings 
            //  back to the old-style literal strings that everyone knows and loves. I'll update this to do the opposite once
            //  we're finally allowed to send JSON strings to the server. It also converts to oldschool formatting codes
    		String pageText = BookUtilities.deJSONify(this.bookPages.get(i));
    		pages.add(pageText);
    	}
    	return pages;
    }
    
    
    private void bookChanged(boolean resetPageSelection){
    	this.bookIsModified = true;
		this.selectionStart = 0;
		this.selectionEnd = 0;
		this.cachedPage = -1;
		
		if (resetPageSelection){
			this.selectedPageA = -1;
			this.selectedPageB = -1;
		}
		
		this.updateButtons();
		// This is some kind of new display update/refresh function
		// It must be called every time the book's content changes
		this.func_238751_C_();
    }
    
    
    private void removePages(int start, int end){
    	int from = Math.min(start, end);
    	int to = Math.max(start, end);
    	
    	// Switch to single page mode if necessary
    	if (from < 0 || from >= this.bookPages.size() || to < 0 || to >= this.bookPages.size()) {
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
    	
    	// Ensure we're not left with a truely empty book
    	if (this.bookPages.isEmpty()) {
    		printer.gamePrint("Wow, I had to add a page");
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
		
		for (int i=0; i<fromBook.pages.size(); i++){
    		this.bookPages.add(fromBook.pages.get(i));
    	}
		this.bookIsModified = true;
		
		if (this.bookPages.isEmpty()) {
			this.bookPages.add("");
		}
		this.updateButtons();
    }
    
	
    private void copyBook(){
		this.clipboard.author = ""; // TODO: Do I need this field any more? Was this.author in old versions
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
    	if (from < 0 || from >= this.bookPages.size() || to < 0 || to >= this.bookPages.size()) {
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
    	if (this.bookPages.size() < MAX_BOOK_PAGES) {
    		this.bookPages.add(this.currPage, "");
    		printer.gamePrint(Printer.GRAY + "Page inserted");
    		this.bookChanged(false);
    	}
    	else {
    		printer.gamePrint(Printer.RED + "Cannot add another page! Book is already " + MAX_BOOK_PAGES + " pages long!");
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
    	    	
    	String pageNewText = "";
    	for (int i=startPos; i<newBookSize; i++){
    		if (i >= this.bookPages.size()){
    			addNewPage();
    		}
    		if (i >= (startPos + this.clipboard.miscPages.size())){
				pageNewText = oldBook.get(i-this.clipboard.miscPages.size());
			}
			else{
				pageNewText = this.clipboard.miscPages.get(i-startPos);
			}
    		this.bookPages.set(i, pageNewText);
    	}
    	this.bookChanged(false);
        printer.gamePrint(Printer.GRAY + "" + this.clipboard.miscPages.size() + " page" + (this.clipboard.miscPages.size()==1?"":"s") + " pasted");
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
    
	
	/**
	 * Helper function for laying out the color buttons
	 */
	public int getColorButX(int buttonNum){
		int middle = this.width/2;
		int leftMost = middle - 160;
		return leftMost + 20 * (buttonNum-50);
	}
	
	
	/**
	 * Helper function for laying out the format buttons
	 */
	public int getFormatButX(int buttonNum){
		int middle = this.width/2;
		int leftMost = middle - 100;
		return leftMost + 20 * (buttonNum-66);
	}


	// TODO: Check that this works as intended
	// TODO: The 1.15 version of this used to delete selected text. Does that still happen?
	public void insertTextIntoPage(String text) {
		this.field_238748_u_.putText(text);
	}

	
	@Override
	public void init() {
		// TODO: Should this only happen once? (i.e. have an initialized field)
		
		// Note that you can use the parameter? in the lambda function like this:
		// pressed_button.x += 20; // neat!
			
        int buttonWidth = 120;
        int buttonHeight = 20;
        int buttonSideOffset = 5;
        
        int rightXPos = this.width-(buttonWidth+buttonSideOffset);
		
		this.buttonFileBrowser = 			this.addButton(new Button(5, 5, buttonWidth, buttonHeight, new StringTextComponent("File Browser"), (pressed_button) -> {
			this.minecraft.displayGuiScreen(new GhostwriterFileBrowserScreen(this));
		}));
		
		this.buttonDisableAutoReload = 		this.addButton(new Button(5, 45, buttonWidth, buttonHeight, new StringTextComponent("Disable AutoReload"), (pressed_button) -> {
			this.disableAutoReload();
		}));
		
		this.buttonCopyBook = 				this.addButton(new Button(rightXPos, 5, buttonWidth, buttonHeight, new StringTextComponent("Copy Book"), (pressed_button) -> {
			this.copyBook();
		}));
		
		this.buttonPasteBook = 				this.addButton(new Button(rightXPos, 25, buttonWidth, buttonHeight, new StringTextComponent("Paste Book"), (pressed_button) -> {
			this.pasteBook();
		}));
		
		this.buttonSelectPageA = 			this.addButton(new Button(rightXPos, 50, buttonWidth/2, buttonHeight, new StringTextComponent("A"), (pressed_button) -> {
			this.selectedPageA = this.currPage;
			this.updateButtons();
		}));
		
		this.buttonSelectPageB = 			this.addButton(new Button(rightXPos+buttonWidth/2, 50, buttonWidth/2, buttonHeight, new StringTextComponent("B"), (pressed_button) -> {
			this.selectedPageB = this.currPage;
			this.updateButtons();
		}));
		
		this.buttonCopySelectedPages = 		this.addButton(new Button(rightXPos, 70, buttonWidth, buttonHeight, new StringTextComponent("Copy This Page"), (pressed_button) -> {
			this.copySelectedPagesToClipboard();
		}));
		
		this.buttonCutMultiplePages = 		this.addButton(new Button(rightXPos, 90, buttonWidth, buttonHeight, new StringTextComponent("Cut This Page"), (pressed_button) -> {
			this.cutMultiplePages();
		}));
		
		this.buttonPasteMultiplePages = 	this.addButton(new Button(rightXPos, 130, buttonWidth, buttonHeight, new StringTextComponent("Paste This Page"), (pressed_button) -> {
			this.pasteMultiplePages(this.currPage);
		}));
		
		this.buttonInsertPage = 			this.addButton(new Button(rightXPos, 155, buttonWidth, buttonHeight, new StringTextComponent("Insert Blank Page"), (pressed_button) -> {
			this.insertPage();
		}));
		
		this.buttonCollapseTop = 			this.addButton(new Button(rightXPos, 175, buttonWidth, buttonHeight, new StringTextComponent("Remove Top Space"), (pressed_button) -> {
			this.collapseTop();
		}));
		
		this.buttonAddSignaturePages = 		this.addButton(new Button(5, 80, buttonWidth, buttonHeight, new StringTextComponent("Add Signature Pages"), (pressed_button) -> {
			this.addSignaturePages();
		}));
		
		this.buttonRemoveSelectedPages = 	this.addButton(new Button(rightXPos, 110, buttonWidth, buttonHeight, new StringTextComponent("Remove This Page"), (pressed_button) -> {
			this.removePages(this.selectedPageA, this.selectedPageB);
		}));
		                     		
		//The horror...
		// TODO: Compress this into a more sensible data structure?
		int colorButY = this.height - 40;
		int formatButY = this.height - 20;
		
		this.formatBlack =     		this.addButton(new Button(getColorButX(ID_BLACK), 			colorButY, 20, 20, new StringTextComponent("\u00a70A)"), (pressed_button) -> {this.insertTextIntoPage("\u00a70");}));
		this.formatDarkBlue =  		this.addButton(new Button(getColorButX(ID_DARK_BLUE), 		colorButY, 20, 20, new StringTextComponent("\u00a71A"), (pressed_button) -> {this.insertTextIntoPage("\u00a71");}));
		this.formatDarkGreen = 		this.addButton(new Button(getColorButX(ID_DARK_GREEN), 		colorButY, 20, 20, new StringTextComponent("\u00a72A"), (pressed_button) -> {this.insertTextIntoPage("\u00a72");}));
		this.formatDarkAqua =  		this.addButton(new Button(getColorButX(ID_DARK_AQUA),		colorButY, 20, 20, new StringTextComponent("\u00a73A"), (pressed_button) -> {this.insertTextIntoPage("\u00a73");}));
		this.formatDarkRed = 		this.addButton(new Button(getColorButX(ID_DARK_RED), 		colorButY, 20, 20, new StringTextComponent("\u00a74A"), (pressed_button) -> {this.insertTextIntoPage("\u00a74");}));
		this.formatDarkPurple = 	this.addButton(new Button(getColorButX(ID_DARK_PURPLE), 	colorButY, 20, 20, new StringTextComponent("\u00a75A"), (pressed_button) -> {this.insertTextIntoPage("\u00a75");}));
		this.formatGold = 			this.addButton(new Button(getColorButX(ID_GOLD), 			colorButY, 20, 20, new StringTextComponent("\u00a76A"), (pressed_button) -> {this.insertTextIntoPage("\u00a76");}));
		this.formatGray = 			this.addButton(new Button(getColorButX(ID_GRAY), 			colorButY, 20, 20, new StringTextComponent("\u00a77A"), (pressed_button) -> {this.insertTextIntoPage("\u00a77");}));
		this.formatDarkGray = 		this.addButton(new Button(getColorButX(ID_DARK_GRAY), 		colorButY, 20, 20, new StringTextComponent("\u00a78A"), (pressed_button) -> {this.insertTextIntoPage("\u007a8");}));
		this.formatBlue = 			this.addButton(new Button(getColorButX(ID_BLUE), 			colorButY, 20, 20, new StringTextComponent("\u00a79A"), (pressed_button) -> {this.insertTextIntoPage("\u00a79");}));
		this.formatGreen = 			this.addButton(new Button(getColorButX(ID_GREEN), 			colorButY, 20, 20, new StringTextComponent("\u00a7aA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7a");}));
		this.formatAqua = 			this.addButton(new Button(getColorButX(ID_AQUA), 			colorButY, 20, 20, new StringTextComponent("\u00a7bA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7b");}));
		this.formatRed = 			this.addButton(new Button(getColorButX(ID_RED), 			colorButY, 20, 20, new StringTextComponent("\u00a7cA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7c");}));
		this.formatLightPurple = 	this.addButton(new Button(getColorButX(ID_LIGHT_PURPLE),	colorButY, 20, 20, new StringTextComponent("\u00a7dA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7d");}));
		this.formatYellow = 		this.addButton(new Button(getColorButX(ID_YELLOW), 			colorButY, 20, 20, new StringTextComponent("\u00a7eA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7e");}));
		this.formatWhite = 			this.addButton(new Button(getColorButX(ID_WHITE), 			colorButY, 20, 20, new StringTextComponent("\u00a7fA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7f");}));
		
		this.formatObfuscated = 	this.addButton(new Button(getFormatButX(ID_OBFUSCATED), 	formatButY, 20, 20, new StringTextComponent("\u00a7kA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7k");}));
		this.formatBold = 			this.addButton(new Button(getFormatButX(ID_BOLD), 			formatButY, 20, 20, new StringTextComponent("\u00a7lA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7l");}));
		this.formatStrikethrough = 	this.addButton(new Button(getFormatButX(ID_STRIKETHROUGH),	formatButY, 20, 20, new StringTextComponent("\u00a7mA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7m");}));
		this.formatUnderline = 		this.addButton(new Button(getFormatButX(ID_UNDERLINE), 		formatButY, 20, 20, new StringTextComponent("\u00a7nA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7n");}));
		this.formatItalic = 		this.addButton(new Button(getFormatButX(ID_ITALIC), 		formatButY, 20, 20, new StringTextComponent("\u00a7oA"), (pressed_button) -> {this.insertTextIntoPage("\u00a7o");}));
		this.formatResetFormat = 	this.addButton(new Button(getFormatButX(ID_RESET_FORMAT), 	formatButY, 100, 20, new StringTextComponent("Reset Formatting"), (pressed_button) -> {this.insertTextIntoPage("\u00a7r");}));

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
		this.fileHandler.saveBookToGHBFile("", "", this.bookPages, filepath); // TODO: Populate the author and title params with something?
	}
	
	
	@Override
	public void tick() {
		// Handle autoreload
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
        
        this.buttonDisableAutoReload.visible = this.autoReloadFile != null;
	}
	
	
	public String getBookTitle() {
		return this.bookTitle;
	}
	
	
	public void setClipboard(Clipboard _clipboard){
    	this.clipboard = _clipboard;
    }
	
	
	public void enableAutoReload(File path, Clipboard initalBookState) {
		this.autoReloadClipboard = initalBookState;
		this.autoReloadFile = path;
		this.autoReloadLastModified = path.lastModified();
		this.autoReloadLastCheck = System.currentTimeMillis();
		this.updateButtons();
	}
	
	
	public void disableAutoReload() {
		this.autoReloadFile = null;
		printer.gamePrint(Printer.AQUA + "Autoreload disabled");
		this.updateButtons();
	}
	
//	@Override
//	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
//		super.render(p_render_1_, p_render_2_, p_render_3_);
//		this.font.drawStringWithShadow("Auto Reload Active!", 5, 45, 0x800888);
//	}

}
