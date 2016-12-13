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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.realmsclient.gui.ChatFormatting;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGhostwriterBook extends GuiScreen
{	
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation("textures/gui/book.png");
    /** The player editing the book */
    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;
    /** Whether the book is signed or can still be edited */
    private final boolean bookIsUnsigned;
    private boolean bookModified;
    private boolean bookGettingSigned;
    /** Used in signing mode to switch between editing the title and author */
    private boolean titleSelected;
    /** Update ticks since the gui was opened */
    private int updateCount;
    private int bookImageWidth = 192;
    private int bookImageHeight = 192;
    private int bookTotalPages = 1;
    private int currPage;
    private int cachedPage = -1;
    private NBTTagList bookPages;
    private String bookTitle = "";
    private String bookAuthor = "";
    private List<ITextComponent> cachedComponents;
    private List field_175386_A;
    private int field_175387_B = -1;
    private boolean viewAsUnsigned = false;
    
    private Printer printer = new Printer();
	private Clipboard clipboard;
	private FileHandler fileHandler;
	
	private Clipboard autoReloadBookClipboard;
	private boolean autoReloadBookEnabled = false;
	private File autoReloadBookPath = null;
	private long autoReloadLastModified = 0;
	private long autoReloadLastCheck = 0;
    
    private GuiGhostwriterBook.NextPageButton buttonNextPage;
    private GuiGhostwriterBook.NextPageButton buttonPreviousPage;
    
    //Used for copying multiple pages at a time
    private int selectedPageA = -1;
    private int selectedPageB = -1;
    
    private static final int BTN_DONE = 0;
	private static final int BTN_NEXT_PAGE = 1;
	private static final int BTN_PREVIOUS_PAGE = 2;
	private static final int BTN_SIGN = 3;
	private static final int BTN_CANCEL = 4;
	private static final int BTN_FINALIZE = 5;
	private static final int BTN_SAVE_BOOK = 6;
	private static final int BTN_LOAD_BOOK = 7;
	private static final int BTN_COPY_BOOK = 10;
	private static final int BTN_PASTE_BOOK = 11;
	private static final int BTN_CUT_MULTIPLE_PAGES = 14;
	private static final int BTN_SELECT_PAGE_A = 15;
	private static final int BTN_SELECT_PAGE_B = 16;
	private static final int BTN_COPY_SELECTED_PAGES = 17;
	private static final int BTN_PASTE_MULTIPLE_PAGES = 18;
	private static final int BTN_INSERT_PAGE = 19;
	private static final int BTN_COLLAPSE_TOP = 20;
	private static final int BTN_ADD_SIGNATURE_PAGES = 21;
	private static final int BTN_REMOVE_SELECTED_PAGES = 22;
	private static final int BTN_AUTO_RELOAD_BOOK = 23;
	//private static final int BTN_VIEW_AS_SIGNED_UNSIGNED = 24;
	
	private static final int BTN_BLACK = 50;
	private static final int BTN_DARK_BLUE = 51;
	private static final int BTN_DARK_GREEN = 52;
	private static final int BTN_DARK_AQUA = 53;
	private static final int BTN_DARK_RED = 54;
	private static final int BTN_DARK_PURPLE = 55;
	private static final int BTN_GOLD = 56;
	private static final int BTN_GRAY = 57;
	private static final int BTN_DARK_GRAY = 58;
	private static final int BTN_BLUE = 59;
	private static final int BTN_GREEN = 60;
	private static final int BTN_AQUA = 61;
	private static final int BTN_RED = 62;
	private static final int BTN_LIGHT_PURPLE = 63;
	private static final int BTN_YELLOW = 64;
	private static final int BTN_WHITE = 65;
	private static final int BTN_OBFUSCATED = 66;
	private static final int BTN_BOLD = 67;
	private static final int BTN_STRIKETHROUGH = 68;
	private static final int BTN_UNDERLINE = 69;
	private static final int BTN_ITALIC = 70;
	private static final int BTN_RESET_FORMAT = 71;
	//private static final int BTN_CURRENT_FORMAT = 72;
	
	/** Note that these are in the same order as the format button IDs. 
	 * Subtract 50 from the ID and that's the index of that formatting code */
	private static final String[] FORMAT_CODES = {
		//Color codes
		"\u00a70", "\u00a71", "\u00a72", "\u00a73", "\u00a74", "\u00a75", "\u00a76", "\u00a77", 
		"\u00a78", "\u00a79", "\u00a7a", "\u00a7b", "\u00a7c", "\u00a7d", "\u00a7e", "\u00a7f", 
		//Formatting codes
		"\u00a7k", "\u00a7l", "\u00a7m", "\u00a7n", "\u00a7o", "\u00a7r"};
    
    private GuiButton buttonDone;
    private GuiButton buttonSign;
    private GuiButton buttonFinalize;
    private GuiButton buttonCancel;
	private GuiButton buttonSaveBook;
	private GuiButton buttonLoadBook;
	private GuiButton buttonAutoReloadBook;
	private GuiButton buttonCopyBook;
	private GuiButton buttonPasteBook;
	private GuiButton buttonCutMultiplePages;
	private GuiButton buttonSelectPageA;
	private GuiButton buttonSelectPageB;
	private GuiButton buttonCopySelectedPages;
	private GuiButton buttonPasteMultiplePages;
	private GuiButton buttonInsertPage;
	private GuiButton buttonCollapseTop;
	private GuiButton buttonAddSignaturePages;
	private GuiButton buttonRemoveSelectedPages;
	//private GuiButton buttonViewAsSignedUnsigned;
	
	private GuiButton buttonBlack;
	private GuiButton buttonDarkBlue;
	private GuiButton buttonDarkGreen;
	private GuiButton buttonDarkAqua;
	private GuiButton buttonDarkRed;
	private GuiButton buttonDarkPurple;
	private GuiButton buttonGold;
	private GuiButton buttonGray;
	private GuiButton buttonDarkGray;
	private GuiButton buttonBlue;
	private GuiButton buttonGreen;
	private GuiButton buttonAqua;
	private GuiButton buttonRed;
	private GuiButton buttonLightPurple;
	private GuiButton buttonYellow;
	private GuiButton buttonWhite;
	private GuiButton buttonObfuscated;
	private GuiButton buttonBold;
	private GuiButton buttonStrikethrough;
	private GuiButton buttonUnderline;
	private GuiButton buttonItalic;
	private GuiButton buttonResetFormat;
	

    public GuiGhostwriterBook(EntityPlayer _editingPlayer, ItemStack _bookObj, boolean _bookIsUnsigned, Clipboard _clipboard) {
		this.clipboard = _clipboard;
		this.fileHandler = new FileHandler(this.clipboard);
        this.editingPlayer = _editingPlayer;
        this.bookObj = _bookObj;
        this.bookIsUnsigned = _bookIsUnsigned;
        this.autoReloadBookPath = null;
        this.autoReloadBookEnabled = false;

        if (_bookObj.hasTagCompound()){
            NBTTagCompound nbttagcompound = _bookObj.getTagCompound();
            this.bookPages = nbttagcompound.getTagList("pages", 8);

            if (this.bookPages != null){
                this.bookPages = (NBTTagList)this.bookPages.copy();
                this.bookTotalPages = this.bookPages.tagCount();

                if (this.bookTotalPages < 1){
                    this.bookTotalPages = 1;
                }
            }
            
            if (!_bookIsUnsigned){
                String s = nbttagcompound.getString("author");
                if (!StringUtils.isNullOrEmpty(s)){
                    this.bookAuthor = s;
                }
                s = nbttagcompound.getString("title");
                if (!StringUtils.isNullOrEmpty(s)){
                    this.bookTitle = s;
                }
            }
        }
        
        if (this.bookPages == null && _bookIsUnsigned){
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
        }       
    }
    
    public void setClipboard(Clipboard _clipboard){
    	this.clipboard = _clipboard;
    }
    
    
    public void setupAutoReload(Clipboard initial_book_state, File path){
    	this.autoReloadBookClipboard = initial_book_state;
    	this.autoReloadBookPath = path;
    	this.autoReloadBookEnabled = true;
    	this.autoReloadLastModified = path.lastModified();
    	this.autoReloadLastCheck = System.currentTimeMillis();
    	this.clipboardToBook(this.autoReloadBookClipboard);
    }
    
    
    public void autoReloadCheck(){
    	long elapsed = System.currentTimeMillis() - this.autoReloadLastCheck;
    	if (elapsed < 1000){
    		return;
    	}
    	this.autoReloadLastCheck = System.currentTimeMillis();
    	System.out.println("Checking book...");
    	long newModTime = this.autoReloadBookPath.lastModified();
    	if (newModTime == 0){
    		this.printer.gamePrint(Printer.RED + "It looks like the source file has disappeared! Disabling auto-reload.");
			this.autoReloadBookEnabled = false;
			this.autoReloadBookClipboard.clearBook();
			this.autoReloadBookPath = null;
			return;
    	}
    	if (newModTime > this.autoReloadLastModified){
    		FileHandler fh = new FileHandler(this.autoReloadBookClipboard);
    		fh.loadBook(this.autoReloadBookPath);
    		if (!this.autoReloadBookClipboard.bookInClipboard){
    			this.printer.gamePrint(Printer.RED + "Something went wrong while trying to reload the book. Disabling auto-reload.");
    			this.autoReloadBookEnabled = false;
    			this.autoReloadBookClipboard.clearBook();
    			this.autoReloadBookPath = null;
    			return;
    		}
    		this.autoReloadLastModified = newModTime;
    		this.clipboardToBook(this.autoReloadBookClipboard);
    		this.printer.gamePrint(Printer.DARK_GRAY + "Loaded updated book!");
    	}
    }
    
    
    private List<String> pagesAsList(){
    	List<String> pages = new ArrayList();
    	for (int i=0; i<this.bookTotalPages; i++){
            // Ugly hack to convert the new JSON "Yo dawg I heard you like strings, so I put a string in your string" strings 
            //  back to the old-style literal strings that everyone knows and loves. I'll update this to do the opposite once
            //  we're finally allowed to send JSON strings to the server. It also converts to oldschool formatting codes
    		String pageText = BookUtilities.deJSONify(this.bookPages.getStringTagAt(i));
    		pages.add(pageText);
    	}
    	return pages;
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
			pasteMultiplePages(this.bookTotalPages-1);
			this.printer.gamePrint(Printer.GRAY + "Signature pages added");
			removePages(this.bookTotalPages-1, this.bookTotalPages-1);
		}
		else{
			this.printer.gamePrint(Printer.RED + "Couldn't load " + sigFile + " Does it exist?");
		}
		this.clipboard.clone(temp);
    }
    
    
    private void copyBook(){
		this.clipboard.author = this.bookAuthor;
		this.clipboard.title = this.bookTitle;
		this.clipboard.pages.clear();
		this.clipboard.pages.addAll(this.pagesAsList());
		this.clipboard.bookInClipboard = true;
		this.printer.gamePrint(Printer.GRAY + "Book copied");
	}
    
    
    /*
     * Copies a book from the clipboard into the 'real' book
     */
    private void clipboardToBook(Clipboard fromBook){
    	this.bookAuthor = fromBook.author;
    	this.bookTitle = fromBook.title;
		String pageNewText = "";
		int maxPage = fromBook.pages.size();
		if (this.bookTotalPages > maxPage){maxPage = this.bookTotalPages;}
		
		for (int i=0; i<maxPage; i++){
			if (i > this.bookTotalPages-1){addNewPage();}
			if (i > (fromBook.pages.size()-1)){
				pageNewText = "";
			}
			else{
				pageNewText = fromBook.pages.get(i);
			}
    		this.bookPages.set(i, new NBTTagString(pageNewText));
    	}
		this.bookModified = true;
    }
    
    
    private void pasteBook(){
    	this.clipboardToBook(this.clipboard);
		this.printer.gamePrint(Printer.GRAY + "Book pasted");
    }
    
       
    private void pasteMultiplePages(int startPos){
    	List<String> oldBook = this.pagesAsList();
    	int newBookSize = this.bookTotalPages + this.clipboard.miscPages.size();
    	
    	//generate extra pages if startPos > this.book.totalPages
    	for (int i=this.bookTotalPages; i<startPos; i++){
    		addNewPage();
    	}
    	
    	String pageNewText = "";
    	for (int i=startPos; i<newBookSize; i++){
    		if (i == this.bookTotalPages){addNewPage();}
    		if (i >= (startPos + this.clipboard.miscPages.size())){
				pageNewText = oldBook.get(i-this.clipboard.miscPages.size());
			}
			else{
				pageNewText = this.clipboard.miscPages.get(i-startPos);
			}
    		this.bookPages.set(i, new NBTTagString(pageNewText)); //func_150304_a
    	}
    	this.bookModified = true;
        this.printer.gamePrint(Printer.GRAY + "" + this.clipboard.miscPages.size() + " page" + (this.clipboard.miscPages.size()==1?"":"s") + " pasted");
    }
    
    
    private void copySelectedPagesToClipboard(int firstSelected, int lastSelected){
    	if (firstSelected >= 0 && lastSelected >= firstSelected && lastSelected < this.bookTotalPages){
    		this.clipboard.miscPages.clear();
    		List<String> pagesAsList = this.pagesAsList();
    		for (int i=firstSelected; i<=lastSelected; i++){
    			this.clipboard.miscPages.add(pagesAsList.get(i));
    		}
    		this.printer.gamePrint(Printer.GRAY + "Selection copied");
    	}
    	else{
    		this.printer.gamePrint(Printer.RED + "Invalid selection! Copy aborted.");
    	}
    }
    
    
    private void insertPage(int pos){
    	//generate at least one new page, and any other new pages if necessary
    	int i = this.bookTotalPages;
    	do{
    		if (addNewPage() == false){
        		this.printer.gamePrint(Printer.RED + "New page couldn't be added. Insert aborted.");
        		return;
        	}
    	}while(i++ < pos);
    	
    	//move all other pages one to the right
    	for (i=this.bookTotalPages-1; i>this.currPage; i--){
    		String currPageText = this.bookPages.getStringTagAt(i-1);
    		this.bookPages.set(i, new NBTTagString(currPageText)); //func_150304_a
    	}
    	//blank out the page at pos
    	this.bookPages.set(pos, new NBTTagString("")); //func_150304_a
    }
    
    
    private void cutMultiplePages(int from, int to){
    	if (from < 0){from = 0;}
    	if (to > this.bookTotalPages-1){to = this.bookTotalPages-1;}
    	if (from > to){from = to;}
    	if (to < from){to = from;}
    	
    	this.clipboard.miscPages.clear();
		List<String> pagesAsList = this.pagesAsList();
		for (int i=from; i<=to; i++){
			this.clipboard.miscPages.add(pagesAsList.get(i));
		}
		removePages(from, to);
		
		this.printer.gamePrint(Printer.GRAY + "" + this.clipboard.miscPages.size() + " page" + (this.clipboard.miscPages.size()==1?"":"s") + " cut");
    }
    
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen(){
        super.updateScreen();
        ++this.updateCount;
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

    
    /**
     * Adds the buttons (and other controls) to the screen
     */
    public void initGui(){
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        
        int buttonWidth = 120;
        int buttonHeight = 20;
        int buttonSideOffset = 5;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
  		int rightXPos = scaledResolution.getScaledWidth() - (buttonWidth + buttonSideOffset);

        if (this.bookIsUnsigned){
        	/*
        	//Original Sign, Done, Finalize, & Cancel buttons (this is mostly so I know where they were on the screen)
            this.buttonList.add(this.buttonSign = new GuiButton(BTN_SIGN, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton", new Object[0])));
            this.buttonList.add(this.buttonDone = new GuiButton(BTN_DONE, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done", new Object[0])));
            this.buttonList.add(this.buttonFinalize = new GuiButton(BTN_FINALIZE, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton", new Object[0])));
            this.buttonList.add(this.buttonCancel = new GuiButton(BTN_CANCEL, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel", new Object[0])));
            */
            this.buttonList.add(this.buttonSign = new GuiButton(BTN_SIGN, 5, 115, buttonWidth, buttonHeight, I18n.format("book.signButton", new Object[0])));
            this.buttonList.add(this.buttonDone = new GuiButton(BTN_DONE, 5, 135, buttonWidth, buttonHeight, I18n.format("gui.done", new Object[0])));
            this.buttonList.add(this.buttonFinalize = new GuiButton(BTN_FINALIZE, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton", new Object[0])));
            this.buttonList.add(this.buttonCancel = new GuiButton(BTN_CANCEL, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel", new Object[0])));
            
            //this.buttonList.add(this.buttonViewAsSignedUnsigned = new GuiButton(BTN_VIEW_AS_SIGNED_UNSIGNED, 5, 170, buttonWidth, buttonHeight, "View As Unsigned"));
        
            this.buttonList.add(this.buttonPasteBook = new GuiButton(BTN_PASTE_BOOK, rightXPos, 25, buttonWidth, buttonHeight, "Paste Book"));
      		this.buttonList.add(this.buttonPasteMultiplePages = new GuiButton(BTN_PASTE_MULTIPLE_PAGES, rightXPos, 130, buttonWidth, buttonHeight, "Paste Page"));
      		this.buttonList.add(this.buttonLoadBook = new GuiButton(BTN_LOAD_BOOK, 5, 25, buttonWidth, buttonHeight, "Load Book"));
      		this.buttonList.add(this.buttonAutoReloadBook = new GuiButton(BTN_AUTO_RELOAD_BOOK, 5, 45, buttonWidth, buttonHeight, "AutoReload Book"));
      		this.buttonList.add(this.buttonInsertPage = new GuiButton(BTN_INSERT_PAGE, rightXPos, 155, buttonWidth, buttonHeight, "Insert Blank Page"));
      		this.buttonList.add(this.buttonCollapseTop = new GuiButton(BTN_COLLAPSE_TOP, rightXPos, 175, buttonWidth, buttonHeight, "Remove Top Space"));
      		this.buttonList.add(this.buttonAddSignaturePages = new GuiButton(BTN_ADD_SIGNATURE_PAGES, 5, 80, buttonWidth, buttonHeight, "Add Signature Pages"));

      		
            //The horror...
            int colorButY = this.height - 40;
            int formatButY = this.height - 20;
            this.buttonList.add(this.buttonBlack = new GuiButton(BTN_BLACK, getColorButX(BTN_BLACK), colorButY, 20, 20, "\u00a70A"));
            this.buttonList.add(this.buttonDarkBlue = new GuiButton(BTN_DARK_BLUE, getColorButX(BTN_DARK_BLUE), colorButY, 20, 20, "\u00a71A"));
            this.buttonList.add(this.buttonDarkGreen = new GuiButton(BTN_DARK_GREEN, getColorButX(BTN_DARK_GREEN), colorButY, 20, 20, "\u00a72A"));
            this.buttonList.add(this.buttonDarkAqua = new GuiButton(BTN_DARK_AQUA, getColorButX(BTN_DARK_AQUA), colorButY, 20, 20, "\u00a73A"));
            this.buttonList.add(this.buttonDarkRed = new GuiButton(BTN_DARK_RED, getColorButX(BTN_DARK_RED), colorButY, 20, 20, "\u00a74A"));
            this.buttonList.add(this.buttonDarkPurple = new GuiButton(BTN_DARK_PURPLE, getColorButX(BTN_DARK_PURPLE), colorButY, 20, 20, "\u00a75A"));
            this.buttonList.add(this.buttonGold = new GuiButton(BTN_GOLD, getColorButX(BTN_GOLD), colorButY, 20, 20, "\u00a76A"));
            this.buttonList.add(this.buttonGray = new GuiButton(BTN_GRAY, getColorButX(BTN_GRAY), colorButY, 20, 20, "\u00a77A"));
            this.buttonList.add(this.buttonDarkGray = new GuiButton(BTN_DARK_GRAY, getColorButX(BTN_DARK_GRAY), colorButY, 20, 20, "\u00a78A"));
            this.buttonList.add(this.buttonBlue = new GuiButton(BTN_BLUE, getColorButX(BTN_BLUE), colorButY, 20, 20, "\u00a79A"));
            this.buttonList.add(this.buttonGreen = new GuiButton(BTN_GREEN, getColorButX(BTN_GREEN), colorButY, 20, 20, "\u00a7aA"));
            this.buttonList.add(this.buttonAqua = new GuiButton(BTN_AQUA, getColorButX(BTN_AQUA), colorButY, 20, 20, "\u00a7bA"));
            this.buttonList.add(this.buttonRed = new GuiButton(BTN_RED, getColorButX(BTN_RED), colorButY, 20, 20, "\u00a7cA"));
            this.buttonList.add(this.buttonLightPurple = new GuiButton(BTN_LIGHT_PURPLE, getColorButX(BTN_LIGHT_PURPLE), colorButY, 20, 20, "\u00a7dA"));
            this.buttonList.add(this.buttonYellow = new GuiButton(BTN_YELLOW, getColorButX(BTN_YELLOW), colorButY, 20, 20, "\u00a7eA"));
            this.buttonList.add(this.buttonWhite = new GuiButton(BTN_WHITE, getColorButX(BTN_WHITE), colorButY, 20, 20, "\u00a7fA"));
            this.buttonList.add(this.buttonObfuscated = new GuiButton(BTN_OBFUSCATED, getFormatButX(BTN_OBFUSCATED), formatButY, 20, 20, "#"));
            this.buttonList.add(this.buttonBold = new GuiButton(BTN_BOLD, getFormatButX(BTN_BOLD), formatButY, 20, 20, "\u00a7lB"));
            this.buttonList.add(this.buttonStrikethrough = new GuiButton(BTN_STRIKETHROUGH, getFormatButX(BTN_STRIKETHROUGH), formatButY, 20, 20, "\u00a7mS"));
            this.buttonList.add(this.buttonUnderline = new GuiButton(BTN_UNDERLINE, getFormatButX(BTN_UNDERLINE), formatButY, 20, 20, "\u00a7nU"));
            this.buttonList.add(this.buttonItalic = new GuiButton(BTN_ITALIC, getFormatButX(BTN_ITALIC), formatButY, 20, 20, "\u00a7oI"));
            this.buttonList.add(this.buttonResetFormat = new GuiButton(BTN_RESET_FORMAT, getFormatButX(BTN_RESET_FORMAT), formatButY, 100, 20, "Reset Formatting"));
        }
        else{
            this.buttonList.add(this.buttonDone = new GuiButton(BTN_DONE, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done", new Object[0])));
        }
        
        
        //Buttons on the left side
  		this.buttonList.add(this.buttonSaveBook = new GuiButton(BTN_SAVE_BOOK, 5, 5, buttonWidth, buttonHeight, "Save Book"));
  		
  		//Buttons on the right side
  		this.buttonList.add(this.buttonCopyBook = new GuiButton(BTN_COPY_BOOK, rightXPos, 5, buttonWidth, buttonHeight, "Copy Book"));
  		this.buttonList.add(this.buttonSelectPageA = new GuiButton(BTN_SELECT_PAGE_A, rightXPos, 50, buttonWidth/2, buttonHeight, "A"));
  		this.buttonList.add(this.buttonSelectPageB = new GuiButton(BTN_SELECT_PAGE_B, rightXPos+buttonWidth/2, 50, buttonWidth/2, buttonHeight, "B"));
  		this.buttonList.add(this.buttonCopySelectedPages = new GuiButton(BTN_COPY_SELECTED_PAGES, rightXPos, 70, buttonWidth, buttonHeight, "Copy This Page"));

  		this.buttonList.add(this.buttonCutMultiplePages = new GuiButton(BTN_CUT_MULTIPLE_PAGES, rightXPos, 90, buttonWidth, buttonHeight, "Cut This Page"));
  		this.buttonList.add(this.buttonRemoveSelectedPages = new GuiButton(BTN_REMOVE_SELECTED_PAGES, rightXPos, 110, buttonWidth, buttonHeight, "Remove This Page"));  		

  		//Standard navigation buttons inside the book
        int i = (this.width - this.bookImageWidth) / 2;
        byte b0 = 2;
        this.buttonList.add(this.buttonNextPage = new GuiGhostwriterBook.NextPageButton(BTN_NEXT_PAGE, i + 120, b0 + 154, true));
        this.buttonList.add(this.buttonPreviousPage = new GuiGhostwriterBook.NextPageButton(BTN_PREVIOUS_PAGE, i + 38, b0 + 154, false));
                
       this.updateButtons();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed(){
    	try {
			sendBookToServer(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
        Keyboard.enableRepeatEvents(false);
    }

    private void updateButtons(){
        this.buttonNextPage.visible = !this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
        this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
        this.buttonDone.visible = !this.bookIsUnsigned || !this.bookGettingSigned;

        if (this.bookIsUnsigned){
            this.buttonSign.visible = !this.bookGettingSigned;
            this.buttonCancel.visible = this.bookGettingSigned;
            this.buttonFinalize.visible = this.bookGettingSigned;
            this.buttonFinalize.enabled = this.bookTitle.trim().length() > 0 && this.bookAuthor.trim().length() > 0;
            //this.buttonViewAsSignedUnsigned.visible = !this.bookGettingSigned;
            
        	this.buttonSaveBook.visible = !this.bookGettingSigned;
        	this.buttonLoadBook.visible = !this.bookGettingSigned;
        	this.buttonAutoReloadBook.visible = !this.bookGettingSigned;
        	this.buttonCopyBook.visible = !this.bookGettingSigned;
        	this.buttonPasteBook.visible = !this.bookGettingSigned;
        	this.buttonCutMultiplePages.visible = !this.bookGettingSigned;
        	this.buttonRemoveSelectedPages.visible = !this.bookGettingSigned;
        	this.buttonSelectPageA.visible = !this.bookGettingSigned;
        	this.buttonSelectPageB.visible = !this.bookGettingSigned;
        	this.buttonCopySelectedPages.visible = !this.bookGettingSigned;
        	this.buttonPasteMultiplePages.visible = !this.bookGettingSigned;
        	this.buttonInsertPage.visible = !this.bookGettingSigned;
        	this.buttonCollapseTop.visible = !this.bookGettingSigned;
        	this.buttonAddSignaturePages.visible = !this.bookGettingSigned;
        	
        	this.buttonBlack.visible = !this.bookGettingSigned;
        	this.buttonDarkBlue.visible = !this.bookGettingSigned;
        	this.buttonDarkGreen.visible = !this.bookGettingSigned;
        	this.buttonDarkAqua.visible = !this.bookGettingSigned;
        	this.buttonDarkRed.visible = !this.bookGettingSigned;
        	this.buttonDarkPurple.visible = !this.bookGettingSigned;
        	this.buttonGold.visible = !this.bookGettingSigned;
        	this.buttonGray.visible = !this.bookGettingSigned;
        	this.buttonDarkGray.visible = !this.bookGettingSigned;
        	this.buttonBlue.visible = !this.bookGettingSigned;
        	this.buttonGreen.visible = !this.bookGettingSigned;
        	this.buttonAqua.visible = !this.bookGettingSigned;
        	this.buttonRed.visible = !this.bookGettingSigned;
        	this.buttonLightPurple.visible = !this.bookGettingSigned;
        	this.buttonYellow.visible = !this.bookGettingSigned;
        	this.buttonWhite.visible = !this.bookGettingSigned;
        	this.buttonObfuscated.visible = !this.bookGettingSigned;
        	this.buttonBold.visible = !this.bookGettingSigned;
        	this.buttonStrikethrough.visible = !this.bookGettingSigned;
        	this.buttonUnderline.visible = !this.bookGettingSigned;
        	this.buttonItalic.visible = !this.bookGettingSigned;
        	this.buttonResetFormat.visible = !this.bookGettingSigned;
        }
        else{
        	this.buttonCutMultiplePages.visible = false;
        	this.buttonRemoveSelectedPages.visible = false;
        }
    }
    
    
    private void sendBookToServer(boolean publish) throws IOException{
    	// Quick sanity check to make sure we haven't been disconnected from the server
    	if (this.mc.theWorld == null){
    		return;
    	}
        if (this.bookIsUnsigned && this.bookModified){
            if (this.bookPages != null){
                String s;
                
                // Remove blank pages at the end of the book
                while (this.bookPages.tagCount() > 1){
                    s = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);
                    if (s.length() != 0){
                        break;
                    }
                    this.bookPages.removeTag(this.bookPages.tagCount() - 1);
                }
                
                if (this.bookObj.hasTagCompound()){
                    NBTTagCompound nbttagcompound = this.bookObj.getTagCompound();
                    nbttagcompound.setTag("pages", this.bookPages);
                }
                else{
                    this.bookObj.setTagInfo("pages", this.bookPages);
                }

                s = "MC|BEdit";
                if (publish){
                    s = "MC|BSign";
                    this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getName()));
                    this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));
                    // I'm not completely sure why we're doing this, so it's disabled. I have a feeling that
                    //  it was a hack to make something work properly in 1.8
                    /*
                    for (int i = 0; i < this.bookPages.tagCount(); ++i){
                        String s1 = this.bookPages.getStringTagAt(i);
                        TextComponentString chatcomponenttext = new TextComponentString(s1);
                        s1 = ITextComponent.Serializer.componentToJson(chatcomponenttext);
                        this.bookPages.set(i, new NBTTagString(s1));
                    }
                    */
                }

                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
                packetbuffer.writeItemStackToBuffer(this.bookObj);
                this.mc.getConnection().sendPacket(new CPacketCustomPayload(s, packetbuffer));
            }
        }
    }
    
    

    protected void actionPerformed(GuiButton buttonPressed){
    	if (!buttonPressed.enabled){return;}
    	
    	switch (buttonPressed.id){
    		case BTN_DONE:
    			this.mc.displayGuiScreen((GuiScreen)null);
				try {
					this.sendBookToServer(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
    			break;
    			
    		case BTN_NEXT_PAGE:
    			if (this.currPage < this.bookTotalPages - 1){++this.currPage;}
                else if (this.bookIsUnsigned){
                    this.addNewPage();
                    if (this.currPage < this.bookTotalPages - 1){++this.currPage;}
                }
    			break;
    			
    		case BTN_PREVIOUS_PAGE:
    			if (this.currPage > 0){--this.currPage;}
    			break;
    			
    		case BTN_SIGN:
    			if (this.bookIsUnsigned){
    				this.bookGettingSigned = true;
    				this.titleSelected = true;
    				if (this.bookAuthor.isEmpty()){
    					this.bookAuthor = this.editingPlayer.getCommandSenderEntity().getName();
    				}
    				updateButtons();
    			}
    			break;
    			
    		case BTN_CANCEL:
    			if (this.bookGettingSigned){
    				this.bookGettingSigned = false;
    				updateButtons();
    			}
    			break;
    			
    		case BTN_FINALIZE:
    			if (this.bookGettingSigned){
    	            try {
						this.sendBookToServer(true);
					} catch (IOException e) {
						e.printStackTrace();
					}
    	            this.mc.displayGuiScreen((GuiScreen)null);
    	        }
    			break;
    			
    		/*
    		case BTN_VIEW_AS_SIGNED_UNSIGNED:
    			this.viewAsUnsigned = !this.viewAsUnsigned;
    			break;
    		*/
    			
    		case BTN_SAVE_BOOK:
    			this.mc.displayGuiScreen(new GuiSaveAs(this, this.bookTitle, this.bookAuthor, this.pagesAsList()));
    			break;
    			
    		case BTN_LOAD_BOOK:
    			this.mc.displayGuiScreen(new GuiFileSelection(this));
    			break;
    			
    		case BTN_AUTO_RELOAD_BOOK:
    			this.mc.displayGuiScreen(new GuiFileSelection(this, true));
    			break;
    			
    		case BTN_COPY_BOOK:
    			copyBook();
    			break;
    			
    		case BTN_PASTE_BOOK:
    			pasteBook();
    			break;
    			
    		case BTN_SELECT_PAGE_A:
    			this.selectedPageA = this.currPage;
    			break;
    		
    		case BTN_SELECT_PAGE_B:
    			this.selectedPageB = this.currPage;
    			break;
    			
    		case BTN_COPY_SELECTED_PAGES:
    			if (this.selectedPageA != -1 && this.selectedPageB != -1 && this.selectedPageA >= 0 && 
    					this.selectedPageA <= this.selectedPageB && this.selectedPageB < this.bookTotalPages){
    				copySelectedPagesToClipboard(this.selectedPageA, this.selectedPageB);
    			}
    			else{
    				copySelectedPagesToClipboard(this.currPage, this.currPage);
    			}
    			break;
    			
    		case BTN_PASTE_MULTIPLE_PAGES:
    			pasteMultiplePages(this.currPage);
    			break;
    			
    		case BTN_INSERT_PAGE:
    			insertPage(this.currPage);
    			break;
    		
    		case BTN_CUT_MULTIPLE_PAGES:
    			if (this.selectedPageA != -1 && this.selectedPageB != -1 && this.selectedPageA >= 0 && 
				this.selectedPageA <= this.selectedPageB && this.selectedPageB < this.bookTotalPages){
    				cutMultiplePages(this.selectedPageA, this.selectedPageB);
				}
				else{
					cutMultiplePages(this.currPage, this.currPage);
				}
				break;
			
    		case BTN_REMOVE_SELECTED_PAGES:
    			if (this.selectedPageA != -1 && this.selectedPageB != -1 && this.selectedPageA >= 0 && 
				this.selectedPageA <= this.selectedPageB && this.selectedPageB < this.bookTotalPages){
    				removePages(this.selectedPageA, this.selectedPageB);
				}
				else{
					removePages(this.currPage, this.currPage);
				}
				break;
				
    		case BTN_COLLAPSE_TOP:
    			String currPage = getCurrentPageText();
    			int i;
    			for (i=0; i<currPage.length(); i++){
    				if (!Character.isWhitespace(currPage.charAt(i))){
    					break;
    				}
    			}
    			if (i>0){
	    			setCurrPageText(currPage.substring(i));
	    			this.printer.gamePrint(Printer.GRAY + "Leading whitespace removed");
    			}
    			break;
    			
    		case BTN_ADD_SIGNATURE_PAGES:
    			addSignaturePages();
    			break;
    			
    		default:
    			break;
    	}
    	
    	//Handle the formatting buttons
    	if (buttonPressed.id >= 50 && buttonPressed.id <= 71){
    		int pos = buttonPressed.id - 50;
    		this.addTextToPage(FORMAT_CODES[pos]);
    	}
    	
        this.updateButtons();
    }

    /**
     * Adds a new 'page' (string tag) to the bookPages NBTTagList
     * @return true if a new page was successfully added
     */
    private boolean addNewPage(){
        if (this.bookPages != null && this.bookPages.tagCount() < 50){
            this.bookPages.appendTag(new NBTTagString(""));
            ++this.bookTotalPages;
            this.bookModified = true;
            return true;
        }
        return false;
    }
    
    private void removePages(int from, int to){
    	//push it to the limit
    	//You know what? I'm going to be nice about this.
    	if (from < 0){from = 0;}
    	if (to > this.bookTotalPages-1){to = this.bookTotalPages-1;}
    	if (from > to){from = to;}
    	if (to < from){to = from;}
    	
    	//Make sure we're not going to find ourselves in a page that's being removed
    	if (from > 0){
    		this.currPage = from-1;
    	}
    	else{
    		this.currPage = 0;
    	}
    	
    	List<String> oldPages = pagesAsList();
    	int newBookSize = this.bookTotalPages - ((to-from)+1);
    	int copyTarget;
    	for (int i=bookTotalPages-1; i>=from; i--){
    		if (i > newBookSize-1){
    			if (i == 0){
    				setCurrPageText("");
    			}
    			else{
	    			//remove excess pages
	    			this.bookPages.removeTag(i);
	        		this.bookTotalPages = this.bookPages.tagCount();
    			}
    		}
    		else{
    			this.bookPages.set(i, new NBTTagString(oldPages.get(i + (to-from) + 1))); //func_150304_a
    		}
    		
    		
    	}
    	
        this.bookModified = true;
    }



    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char character, int keycode){
        try {
			super.keyTyped(character, keycode);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        if (this.bookIsUnsigned){
            if (this.bookGettingSigned){
                this.keyTypedInSigningPage(character, keycode);
            }
            else{
                this.keyTypedInBook(character, keycode);
            }
        }
    }

    
    /**
     */
    private void keyTypedInBook(char character, int keycode){
        switch (character){
            case 22:
                this.addTextToPage(GuiScreen.getClipboardString());
                return;
                
            default:
                switch (keycode){
                    case Keyboard.KEY_BACK: //Backspace
                        String s = this.getCurrentPageText();
                        if (s.length() > 0){
                            this.setCurrPageText(s.substring(0, s.length() - 1));
                        }
                        return;
                        
                    case Keyboard.KEY_RETURN:      //Enter
                    case Keyboard.KEY_NUMPADENTER: //Numpad enter
                        this.addTextToPage("\n");
                        return;
                        
                    default:
                        if (ChatAllowedCharacters.isAllowedCharacter(character)){
                            this.addTextToPage(Character.toString(character));
                        }
                }
        }
    }

    /**
     * Called when a key is typed while editing the title/author of a book
     */
    private void keyTypedInSigningPage(char character, int keycode){
        switch (keycode){
            case Keyboard.KEY_BACK: //Backspace
            	if (this.titleSelected){
	                if (!this.bookTitle.isEmpty()){
	                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
	                }
            	}
            	else{
            		if (!this.bookAuthor.isEmpty()){
	                    this.bookAuthor = this.bookAuthor.substring(0, this.bookAuthor.length() - 1);
	                }
            	}
            	this.updateButtons();
                this.bookModified = true;
                return;
                
            case Keyboard.KEY_TAB:
            	if (this.bookGettingSigned){
            		// Disabled since you can no longer set the author with the default server
            		//this.titleSelected = !this.titleSelected;
            	}
            	return;
            	
            default:
            	if (this.titleSelected){
	                if (this.bookTitle.length() < 32 && ChatAllowedCharacters.isAllowedCharacter(character)){
	                    this.bookTitle = this.bookTitle + Character.toString(character);
	                }
            	}
            	else{
            		if (this.bookAuthor.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(character)){
	                    this.bookAuthor = this.bookAuthor + Character.toString(character);
	                }
            	}
            	this.updateButtons();
                this.bookModified = true;
                return;
        }
    }

    /**
     * I'm pretty sure this fetches the text from the current page
     * @return The text from the current page if it exists, otherwise it returns a blank string
     */
    private String getCurrentPageText()
    {
        return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
    }

    /**
     * This looks like it stores the new/modified page in bookPages
     * This could be really handy for making books without the GUI
     */
    private void setCurrPageText(String text){
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()){
            this.bookPages.set(this.currPage, new NBTTagString(text)); //func_150304_a
            this.bookModified = true;
        }
    }

    /**
     * This seems to get called whenever a key is typed (or if you paste text)
     */
    private void addTextToPage(String textToAdd)
    {
    	//Grab the current text from the page
        String s1 = this.getCurrentPageText();
        //Append the new text to it
        String s2 = s1 + textToAdd;
        //Add the cursor
        int i = this.fontRendererObj.splitStringWidth(s2 + "" + ChatFormatting.BLACK + "_", 118);
        //store it
        if (i <= 118 && s2.length() < 256){this.setCurrPageText(s2);}
    }
    
    
    /*
    ###########################################################################################################################
    ###########################################################################################################################
    ##                                                                                                                       ##
    ##  THESE TWO FUNCTIONS ARE BEING USED TO TEMPORARILY BREAK FORMAT-WRAPPING TO MIMIC THE BEHAVIOR OF THE VANILLA CLIENT  ##
    ##                                They were taken from GuiUtilRenderComponents                                           ##
    ##                                                                                                                       ##
    ###########################################################################################################################
    ###########################################################################################################################
    */
    /*
    public static String removeTextColorsIfConfigured(String text, boolean forceColor)
    {
        return !forceColor && !Minecraft.getMinecraft().gameSettings.chatColours ? TextFormatting.getTextWithoutFormattingCodes(text) : text;
    }

    public static List<ITextComponent> splitText(ITextComponent textComponent, int maxTextLenght, FontRenderer fontRendererIn, boolean p_178908_3_, boolean forceTextColor)
    {
        int i = 0;
        ITextComponent itextcomponent = new TextComponentString("");
        List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
        List<ITextComponent> list1 = Lists.newArrayList(textComponent);

        for (int j = 0; j < ((List)list1).size(); ++j)
        {
            ITextComponent itextcomponent1 = (ITextComponent)list1.get(j);
            String s = itextcomponent1.getUnformattedComponentText();
            boolean flag = false;

            if (s.contains("\n"))
            {
                int k = s.indexOf(10);
                String s1 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                TextComponentString textcomponentstring = new TextComponentString(s1);
                textcomponentstring.setStyle(itextcomponent1.getStyle().createShallowCopy());
                list1.add(j + 1, textcomponentstring);
                flag = true;
            }

            String s4 = removeTextColorsIfConfigured(itextcomponent1.getStyle().getFormattingCode() + s, forceTextColor);
            String s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
            int i1 = fontRendererIn.getStringWidth(s5);
            TextComponentString textcomponentstring1 = new TextComponentString(s5);
            textcomponentstring1.setStyle(itextcomponent1.getStyle().createShallowCopy());

            if (i + i1 > maxTextLenght)
            {
                String s2 = fontRendererIn.trimStringToWidth(s4, maxTextLenght - i, false);
                String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;

                if (s3 != null && !s3.isEmpty())
                {
                    int l = s2.lastIndexOf(" ");

                    if (l >= 0 && fontRendererIn.getStringWidth(s4.substring(0, l)) > 0)
                    {
                        s2 = s4.substring(0, l);

                        if (p_178908_3_)
                        {
                            ++l;
                        }

                        s3 = s4.substring(l);
                    }
                    else if (i > 0 && !s4.contains(" "))
                    {
                        s2 = "";
                        s3 = s4;
                    }
                    //Note that this next line has been commented out to remove the Forge fix.
                    //s3 = FontRenderer.getFormatFromString(s2) + s3; //Forge: Fix chat formatting not surviving line wrapping.
                    TextComponentString textcomponentstring2 = new TextComponentString(s3);
                    textcomponentstring2.setStyle(itextcomponent1.getStyle().createShallowCopy());
                    list1.add(j + 1, textcomponentstring2);
                }

                i1 = fontRendererIn.getStringWidth(s2);
                textcomponentstring1 = new TextComponentString(s2);
                textcomponentstring1.setStyle(itextcomponent1.getStyle().createShallowCopy());
                flag = true;
            }

            if (i + i1 <= maxTextLenght)
            {
                i += i1;
                itextcomponent.appendSibling(textcomponentstring1);
            }
            else
            {
                flag = true;
            }

            if (flag)
            {
                list.add(itextcomponent);
                i = 0;
                itextcomponent = new TextComponentString("");
            }
        }

        list.add(itextcomponent);
        return list;
    }
    */
    
    
    
    /** 
     * Helper function for drawing centered strings on the screen
     * @param str
     */
    private void drawCenteredString(String str, int yPos, int color, boolean dropShadow){
    	int xPos = this.width/2 - this.fontRendererObj.getStringWidth(str)/2;
    	this.fontRendererObj.drawString(str, xPos, yPos, color, dropShadow);
    }
    
    
    /**
     * @param p_175385_1_ --> Mouse X
     * @param p_175385_2_ --> Mouse Y
     */
    public ITextComponent getClickedComponentAt(int p_175385_1_, int p_175385_2_)
    {
        if (this.cachedComponents == null)
        {
            return null;
        }
        else
        {
            int i = p_175385_1_ - (this.width - this.bookImageWidth) / 2 - 36;
            int j = p_175385_2_ - 2 - 16 - 16;

            if (i >= 0 && j >= 0)
            {
                int k = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.cachedComponents.size());

                if (i <= 116 && j < this.mc.fontRendererObj.FONT_HEIGHT * k + k)
                {
                    int l = j / this.mc.fontRendererObj.FONT_HEIGHT;

                    if (l >= 0 && l < this.cachedComponents.size())
                    {
                        ITextComponent itextcomponent = (ITextComponent)this.cachedComponents.get(l);
                        int i1 = 0;

                        for (ITextComponent itextcomponent1 : itextcomponent)
                        {
                            if (itextcomponent1 instanceof TextComponentString)
                            {
                                i1 += this.mc.fontRendererObj.getStringWidth(((TextComponentString)itextcomponent1).getText());

                                if (i1 > i)
                                {
                                    return itextcomponent1;
                                }
                            }
                        }
                    }

                    return null;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
    }
    
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	if (this.bookIsUnsigned){
    		if (this.autoReloadBookEnabled){
    			this.autoReloadCheck();
    		}
            this.buttonPasteBook.enabled = this.clipboard.bookInClipboard;
            
            this.buttonPasteMultiplePages.enabled = (this.clipboard.miscPages.size() > 0);
            if (this.buttonPasteMultiplePages.enabled){
            	this.buttonPasteMultiplePages.displayString = "Paste " + this.clipboard.miscPages.size() + " Page" + ((this.clipboard.miscPages.size()==1)?"":"s");
            }
            else{
            	this.buttonPasteMultiplePages.displayString = "Paste Multiple";
            }
            /*
            if (this.viewAsUnsigned){
            	this.buttonViewAsSignedUnsigned.displayString = "View As Signed";
            }
            else{
            	this.buttonViewAsSignedUnsigned.displayString = "View As Unsigned";
            }
            */
    	}
    	if (this.selectedPageA >= this.bookTotalPages || this.selectedPageB >= this.bookTotalPages){
    		this.selectedPageA = -1;
    		this.selectedPageB = -1;
    	}
    	if (this.selectedPageA != -1 && this.selectedPageB != -1 && this.selectedPageA >= 0 && 
    			this.selectedPageA <= this.selectedPageB && this.selectedPageB < this.bookTotalPages){
    		this.buttonCopySelectedPages.enabled = true;
    		String xPages = ((this.selectedPageB-this.selectedPageA)+1) + " Page"  + ((this.selectedPageA!=this.selectedPageB)?"s":"");
    		this.buttonCopySelectedPages.displayString = "Copy " + xPages;
    		this.buttonCutMultiplePages.displayString = "Cut " + xPages;
    		this.buttonRemoveSelectedPages.displayString = "Remove " + xPages;
    		this.buttonSelectPageA.displayString = "A: " + (this.selectedPageA+1);
    		this.buttonSelectPageB.displayString = "B: " + (this.selectedPageB+1);
    	}
    	else if (this.selectedPageA != -1){
    		this.buttonSelectPageA.displayString = "A: " + (this.selectedPageA+1);
    		this.buttonCopySelectedPages.displayString = "Copy This Page";
    		this.buttonCutMultiplePages.displayString = "Cut This Page";
    		this.buttonRemoveSelectedPages.displayString = "Remove This Page";
    	}
    	else if (this.selectedPageB != -1){
    		this.buttonSelectPageB.displayString = "B: " + (this.selectedPageB+1);
    		this.buttonCopySelectedPages.displayString = "Copy This Page";
    		this.buttonCutMultiplePages.displayString = "Cut This Page";
    		this.buttonRemoveSelectedPages.displayString = "Remove This Page";
    	}
    	else{
    		this.buttonCopySelectedPages.displayString = "Copy This Page";
    		this.buttonCutMultiplePages.displayString = "Cut This Page";
    		this.buttonRemoveSelectedPages.displayString = "Remove This Page";
    		this.buttonSelectPageA.displayString = "A";
    		this.buttonSelectPageB.displayString = "B";
    	}
    	
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
        int i = (this.width - 192) / 2;
        int j = 2;
        this.drawTexturedModalRect(i, 2, 0, 0, 192, 192);

        if (this.bookGettingSigned)
        {
            String s = this.bookTitle;

            if (this.bookIsUnsigned)
            {
                if (this.updateCount / 6 % 2 == 0)
                {
                    s = s + "" + TextFormatting.BLACK + "_";
                }
                else
                {
                    s = s + "" + TextFormatting.GRAY + "_";
                }
            }

            String s1 = I18n.format("book.editTitle", new Object[0]);
            int k = this.fontRendererObj.getStringWidth(s1);
            this.fontRendererObj.drawString(s1, i + 36 + (116 - k) / 2, 34, 0);
            int l = this.fontRendererObj.getStringWidth(s);
            this.fontRendererObj.drawString(s, i + 36 + (116 - l) / 2, 50, 0);
            String s2 = I18n.format("book.byAuthor", new Object[] {this.editingPlayer.getName()});
            int i1 = this.fontRendererObj.getStringWidth(s2);
            this.fontRendererObj.drawString(TextFormatting.DARK_GRAY + s2, i + 36 + (116 - i1) / 2, 60, 0);
            String s3 = I18n.format("book.finalizeWarning", new Object[0]);
            this.fontRendererObj.drawSplitString(s3, i + 36, 82, 116, 0);
        }
        else
        {
            String s4 = I18n.format("book.pageIndicator", new Object[] {Integer.valueOf(this.currPage + 1), Integer.valueOf(this.bookTotalPages)});
            String s5 = "";

            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount())
            {
                s5 = this.bookPages.getStringTagAt(this.currPage);
            }

            if (this.bookIsUnsigned)
            {
                if (this.fontRendererObj.getBidiFlag())
                {
                    s5 = s5 + "_";
                }
                else if (this.updateCount / 6 % 2 == 0)
                {
                    s5 = s5 + "" + TextFormatting.BLACK + "_";
                }
                else
                {
                    s5 = s5 + "" + TextFormatting.GRAY + "_";
                }
            }
            else if (this.cachedPage != this.currPage)
            {
                if (ItemWrittenBook.validBookTagContents(this.bookObj.getTagCompound()))
                {
                    try
                    {
                        ITextComponent itextcomponent = ITextComponent.Serializer.jsonToComponent(s5);
                        this.cachedComponents = itextcomponent != null ? GuiUtilRenderComponents.splitText(itextcomponent, 116, this.fontRendererObj, true, true) : null;
                    }
                    catch (JsonParseException var13)
                    {
                        this.cachedComponents = null;
                    }
                }
                else
                {
                    TextComponentString textcomponentstring = new TextComponentString(TextFormatting.DARK_RED + "* Invalid book tag *");
                    this.cachedComponents = Lists.newArrayList(textcomponentstring);
                }

                this.cachedPage = this.currPage;
            }

            int j1 = this.fontRendererObj.getStringWidth(s4);
            this.fontRendererObj.drawString(s4, i - j1 + 192 - 44, 18, 0);

            if (this.cachedComponents == null)
            {
                this.fontRendererObj.drawSplitString(s5, i + 36, 34, 116, 0);
            }
            else
            {
                int k1 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.cachedComponents.size());

                for (int l1 = 0; l1 < k1; ++l1)
                {
                    ITextComponent itextcomponent2 = (ITextComponent)this.cachedComponents.get(l1);
                    this.fontRendererObj.drawString(itextcomponent2.getUnformattedText(), i + 36, 34 + l1 * this.fontRendererObj.FONT_HEIGHT, 0);
                }

                ITextComponent itextcomponent1 = this.getClickedComponentAt(mouseX, mouseY);

                if (itextcomponent1 != null)
                {
                    this.handleComponentHover(itextcomponent1, mouseX, mouseY);
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    /*
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	if (this.bookIsUnsigned){
    		if (this.autoReloadBookEnabled){
    			this.autoReloadCheck();
    		}
            this.buttonPasteBook.enabled = this.clipboard.bookInClipboard;
            
            this.buttonPasteMultiplePages.enabled = (this.clipboard.miscPages.size() > 0);
            if (this.buttonPasteMultiplePages.enabled){
            	this.buttonPasteMultiplePages.displayString = "Paste " + this.clipboard.miscPages.size() + " Page" + ((this.clipboard.miscPages.size()==1)?"":"s");
            }
            else{
            	this.buttonPasteMultiplePages.displayString = "Paste Multiple";
            }
            if (this.viewAsUnsigned){
            	this.buttonViewAsSignedUnsigned.displayString = "View As Signed";
            }
            else{
            	this.buttonViewAsSignedUnsigned.displayString = "View As Unsigned";
            }
    	}
    	if (this.selectedPageA >= this.bookTotalPages || this.selectedPageB >= this.bookTotalPages){
    		this.selectedPageA = -1;
    		this.selectedPageB = -1;
    	}
    	if (this.selectedPageA != -1 && this.selectedPageB != -1 && this.selectedPageA >= 0 && 
    			this.selectedPageA <= this.selectedPageB && this.selectedPageB < this.bookTotalPages){
    		this.buttonCopySelectedPages.enabled = true;
    		String xPages = ((this.selectedPageB-this.selectedPageA)+1) + " Page"  + ((this.selectedPageA!=this.selectedPageB)?"s":"");
    		this.buttonCopySelectedPages.displayString = "Copy " + xPages;
    		this.buttonCutMultiplePages.displayString = "Cut " + xPages;
    		this.buttonRemoveSelectedPages.displayString = "Remove " + xPages;
    		this.buttonSelectPageA.displayString = "A: " + (this.selectedPageA+1);
    		this.buttonSelectPageB.displayString = "B: " + (this.selectedPageB+1);
    	}
    	else if (this.selectedPageA != -1){
    		this.buttonSelectPageA.displayString = "A: " + (this.selectedPageA+1);
    		this.buttonCopySelectedPages.displayString = "Copy This Page";
    		this.buttonCutMultiplePages.displayString = "Cut This Page";
    		this.buttonRemoveSelectedPages.displayString = "Remove This Page";
    	}
    	else if (this.selectedPageB != -1){
    		this.buttonSelectPageB.displayString = "B: " + (this.selectedPageB+1);
    		this.buttonCopySelectedPages.displayString = "Copy This Page";
    		this.buttonCutMultiplePages.displayString = "Cut This Page";
    		this.buttonRemoveSelectedPages.displayString = "Remove This Page";
    	}
    	else{
    		this.buttonCopySelectedPages.displayString = "Copy This Page";
    		this.buttonCutMultiplePages.displayString = "Cut This Page";
    		this.buttonRemoveSelectedPages.displayString = "Remove This Page";
    		this.buttonSelectPageA.displayString = "A";
    		this.buttonSelectPageB.displayString = "B";
    	}
    	
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
        int bookX = (this.width - this.bookImageWidth) / 2; //bookX formerly i [1.9.4]
        int bookY = 2; //bookY formerly j [1.9.4]
        this.drawTexturedModalRect(bookX, bookY, 0, 0, this.bookImageWidth, this.bookImageHeight);

        if (this.bookGettingSigned)
        {
            String s = this.bookTitle;

            if (this.bookIsUnsigned)
            {
            	//Add the cursor
                if (this.updateCount / 6 % 2 == 0)
                {
                    s = s + "" + TextFormatting.BLACK + "_";
                }
                else
                {
                    s = s + "" + TextFormatting.GRAY + "_";
                }
            }
            
            //Draw the header
            String signPageHeader = I18n.format("book.editTitle", new Object[0]);
            int k = this.fontRendererObj.getStringWidth(signPageHeader);
            this.fontRendererObj.drawString(signPageHeader, bookX + 36 + (116 - k) / 2, bookY + 16 + 16, 0);
            //Draw the title line
            int l = this.fontRendererObj.getStringWidth(s);
            this.fontRendererObj.drawString(s, bookX + 36 + (116 - l) / 2, bookY + 48, 0);
            //Draw the author line
            String s2 = I18n.format("book.byAuthor", new Object[] {this.editingPlayer.getName()});
            int i1 = this.fontRendererObj.getStringWidth(s2);
            this.fontRendererObj.drawString(TextFormatting.DARK_GRAY + s2, bookX + 36 + (116 - i1) / 2, bookY + 48 + 10, 0);
            //Draw the finalize warning
            String s3 = I18n.format("book.finalizeWarning", new Object[0]);
            this.fontRendererObj.drawSplitString(s3, bookX + 36, bookY + 80, 116, 0);
        }
        else
        {
            String pageIndicator = I18n.format("book.pageIndicator", new Object[] {Integer.valueOf(this.currPage + 1), Integer.valueOf(this.bookTotalPages)});
            String pageText = "";

            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount())
            {
                pageText = this.bookPages.getStringTagAt(this.currPage);
            }

            if (this.bookIsUnsigned)
            {
            	//Add cursor
                if (this.fontRendererObj.getBidiFlag())
                {
                    pageText = pageText + "_";
                }
                else if (this.updateCount / 6 % 2 == 0)
                {
                    pageText = pageText + "" + TextFormatting.BLACK + "_";
                }
                else
                {
                    pageText = pageText + "" + TextFormatting.GRAY + "_";
                }
                if (!this.viewAsUnsigned){
		            // Convert to JSON
	                TextComponentString chatcomponenttext = new TextComponentString(pageText);
	                pageText = ITextComponent.Serializer.componentToJson(chatcomponenttext);
	            }
            }
            
            // This was wrapped in some other conditionals, but since we want to use it with unsigned books too, those have been removed
            try
            {
                ITextComponent itextcomponent = ITextComponent.Serializer.jsonToComponent(pageText);
                // Note that we're using a local version of the splitText method with the format wrapping fix removed
                this.cachedComponents = itextcomponent != null ? GuiUtilRenderComponents.splitText(itextcomponent, 116, this.fontRendererObj, true, true) : null;
            }
            catch (JsonParseException var13)
            {
                this.cachedComponents = null;
            }

            //Draw page indicator
            int pageIndWidth = this.fontRendererObj.getStringWidth(pageIndicator);
            this.fontRendererObj.drawString(pageIndicator, bookX - pageIndWidth + this.bookImageWidth - 44, bookY + 16, 0);

            
            if (this.cachedComponents == null)
            {
            	// Render as an unsigned book
                this.fontRendererObj.drawSplitString(pageText, bookX + 36, bookY + 16 + 16, 116, 0);
                this.drawCenteredString("Using unsigned book formatting", 186, 0xff0000, true);
            }
            else
            {
            	//Render as signed book
            	if (this.bookIsUnsigned){
            		this.drawCenteredString("Using signed book formatting", 186, 0xff0000, true);
            	}
                int k1 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.cachedComponents.size());

                for (int l1 = 0; l1 < k1; ++l1)
                {
                    ITextComponent itextcomponent2 = (ITextComponent)this.cachedComponents.get(l1);
                    this.fontRendererObj.drawString(itextcomponent2.getUnformattedText(), bookX + 36, bookY + 16 + 16 + l1 * this.fontRendererObj.FONT_HEIGHT, 0);
                }

                ITextComponent itextcomponent1 = this.getClickedComponentAt(mouseX, mouseY);

                if (itextcomponent1 != null)
                {
                    this.handleComponentHover(itextcomponent1, mouseX, mouseY);
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    */
        
    
    @SideOnly(Side.CLIENT)
    static class NextPageButton extends GuiButton{
            private final boolean isForward;

            public NextPageButton(int par1, int par2, int par3, boolean par4){
                super(par1, par2, par3, 23, 13, "");
                this.isForward = par4;
            }
            
            public void drawButton(Minecraft mc, int p_146112_2_, int p_146112_3_){
                if (this.visible){
                    boolean flag = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(GuiGhostwriterBook.BOOK_GUI_TEXTURES);
                    int k = 0;
                    int l = 192;
                    if (flag){k += 23;}
                    if (!this.isForward){l += 13;}
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, k, l, 23, 13);
                }
            }
        }
}