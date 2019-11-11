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

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@OnlyIn(Dist.CLIENT)
public class GuiGhostwriterBook extends wafflestomper.ghostwriter.modified_mc_files.EditBookScreenMod{
	
	private static final int ID_DONE = 0;
	private static final int ID_NEXT_PAGE = 1;
	private static final int ID_PREVIOUS_PAGE = 2;
	private static final int ID_SIGN = 3;
	private static final int ID_CANCEL = 4;
	private static final int ID_FINALIZE = 5;
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
	private static final int ID_VIEW_AS_SIGNED_UNSIGNED = 24;
	
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
	
	private static final Logger LOGGER = LogManager.getLogger();
	private Button butt_test;
	private Button formatBlack;
	private Button formatDarkBlue;
	

	public GuiGhostwriterBook(PlayerEntity editingPlayer, ItemStack book, Hand hand) {
		super(editingPlayer, book, hand);
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
	 * What is this for?
	 */
	public boolean charTyped(char char_typed, int shift_held_i_think) {
		return super.charTyped(char_typed, shift_held_i_think);
	}
	
	
	void insertFormatChar(String formatChar) {
		insertTextIntoPage(formatChar);
	}
		
	
	protected void init() {
		super.init();
		                     		
		//The horror...
		// TODO: Compress this into a more sensible data structure?
		int colorButY = this.height - 40;
		int formatButY = this.height - 20;
		
		this.formatBlack = this.addButton(new Button(getColorButX(ID_BLACK), colorButY, 20, 20, "\u00a70A", (pressed_button) -> {
	         this.insertFormatChar("\u00a70");
	         //pressed_button.x += 20; // neat!
	    }));
		
		this.formatDarkBlue = this.addButton(new Button(getColorButX(ID_DARK_BLUE), colorButY, 20, 20, "\u00a71A", (pressed_button) -> {
	         this.insertFormatChar("\u00a71");
	    }));
		
		LOGGER.info("init done I guess");
	}

}
