package wafflestomper.ghostwriter;

import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;


public class BookUtilities {
	public static final int BOOK_TEXT_WIDTH = 114;
	public static final int BOOK_MAX_LINES = 14;
	private static final Minecraft mc = Minecraft.getInstance();
	
	
	/**
	 * Removes duplicate or unnecessary formatting characters
	 * Note that a color code will cancel a style code (e.g. §n§cX will just show as red and not underlined)
	 * I'm using 'style' here to refer to all of the non-color codes (e.g. bold)
	 * Note that a sequence like Red Bold Red will be preserved because the second red is needed to cancel the bold
	 */
	public static String removeRedundantFormatChars(String in, String pageBreakString){
		// TODO: Find out where the extra format characters are coming from and try to suppress them upstream
		//       rather than just removing them later. I suspect the issue is books that have been saved on servers
		//       running old Minecraft versions, but I'm not totally sure
		// Split the string into pages
		String[] splitByPage = in.split(pageBreakString);
		StringBuilder cleanedPage = new StringBuilder();
		StringBuilder cleanedOut = new StringBuilder();
		char currentColor = '0';  // Black
		List<String> currentStyles = new ArrayList<>();  // No style
		for(int pageNum=0; pageNum < splitByPage.length; pageNum++){
			String page = splitByPage[pageNum];
			// Loop through each character of the page, adding all characters except redundant formatting
			boolean formatPrefixFlag = false;
			for(int i=0; i<page.length(); i++){
				char c = page.charAt(i);
				if (formatPrefixFlag){
					formatPrefixFlag = false;
					String lowerChar = String.valueOf(c).toLowerCase();
					// Previous character was a format prefix.
					if ("0123456789abcdef".contains(lowerChar)){
						// Color code (which also cancels any existing style codes)
						if (currentColor == c && currentStyles.size() == 0){
							// Color code isn't needed
							continue;
						}
						currentColor = c;
						currentStyles.clear();
					}
					else if ("klmnor".contains(lowerChar)){
						// Style code
						if (lowerChar.equals("r")){
							// Reset char
							if (currentColor == '0' && currentStyles.size() == 0) {
								// Reset char isn't required
								continue;
							}
							else{
								// Reset format
								currentColor = '0';
								currentStyles.clear();
							}
						}
						else{
							// Some other format char
							if (currentStyles.contains(lowerChar)){
								// Style character is already active
								continue;
							}
							currentStyles.add(lowerChar);
						}
					}
					// Format character is necessary. Add it to the cleaned string
					cleanedPage.append('\u00a7').append(c);
					continue;
				}
				else if (c == '\u00a7'){
					formatPrefixFlag = true;
					continue;
				}
				cleanedPage.append(c);
			}
			// Add the page text (and a page break if it's not the last page) to the output
			// and reset the formatting
			cleanedOut.append(cleanedPage.toString());
			if (pageNum < splitByPage.length - 1){
				cleanedOut.append(pageBreakString);
			}
			cleanedPage = new StringBuilder();
			currentColor = '0';
			currentStyles.clear();
		}
		// Reassemble the cleaned string
		return cleanedOut.toString();
	}
	
	
	/**
	 * Splits a string into pages of word-wrapped lines
	 * Inspired by code in EditBookScreen that splits the current page for rendering
	 * Note that this function preserves newline characters at the end of lines, where the vanilla code removes them
	 *
	 * @param inStr String to split
	 * @param maxLinesPerPage Maximum number of lines on a single page (<= 0 if you want all lines on a single page)
	 * @return List of pages
	 */
	public static Pages splitIntoPages(String inStr, int maxLinesPerPage){
		Pages pages = new Pages();
		IntList lineStartIndices = new IntArrayList();
		List<String> lines = new ArrayList<>();
		List<ITextComponent> stylizedLines = new ArrayList<>();
		MutableInt pageStartPos = new MutableInt(0);
		CharacterManager charactermanager = Minecraft.getInstance().fontRenderer.func_238420_b_();
		charactermanager.func_238353_a_(inStr, BOOK_TEXT_WIDTH, Style.EMPTY, true,
				(style, start, end) -> {
			lineStartIndices.add(start - pageStartPos.getValue());
			String line = inStr.substring(start, end);
			lines.add(line);
			stylizedLines.add(new StringTextComponent(line).setStyle(style));
			
			if (lines.size() == maxLinesPerPage){
				// The current page is full. Store it and start a new one
				String currPageText = inStr.substring(pageStartPos.getValue(), end);
				pages.add(new PageDetails(currPageText, lineStartIndices, lines, stylizedLines));
				pageStartPos.setValue(end);
				lineStartIndices.clear();
				lines.clear();
				stylizedLines.clear();
			}
		});
		
		// Add anything remaining to last page
		if (lines.size() > 0) {
			String currPageText = inStr.substring(pageStartPos.getValue());
			pages.add(new PageDetails(currPageText, lineStartIndices, lines, stylizedLines));
		}
		
		return pages;
	}
	
	
	/**
	 * Helper function that first splits by page break, then into book sized pages and lines
	 * @param strIn String to split
	 * @param maxLinesPerPage Maximum number of lines to allow on a single page. 0 is infinite
	 * @param pageBreak Page break symbol
	 */
	public static Pages splitIntoPages(String strIn, int maxLinesPerPage, String pageBreak){
		Pages pages = new Pages();
		for (String pageBroken: strIn.split(pageBreak)){
			pages.addAll(splitIntoPages(pageBroken, maxLinesPerPage));
		}
		return pages;
	}
	
	
	/**
	 * Shortens a string to a specific width
	 * Substitute chars are optional, but if they are set to anything other than a blank string, they will be counted
	 * as part of the length of the output string.
	 * KeepRightSide determines whether the right (end of the string) or left (start of the string) should be kept
	 * (i.e. that the opposite end should be removed).
	 */
	public static String truncateStringPixels(String strIn, String substituteChars, int maxWidth, boolean keepRightSide){
		FontRenderer f = mc.fontRenderer;
		if (f.getStringWidth(strIn) <= maxWidth){
			return strIn;
		}
		String strOut = "";
		int subCharsWidth = f.getStringWidth(substituteChars);
		
		int startPos = 0;
		int endPos = strIn.length()-1;
		int direction = 1;
		if (keepRightSide){
			startPos = strIn.length()-1;
			endPos = -1;
			direction = -1;
		}
		for (int i=startPos; i!=endPos; i+=direction){
			char c = strIn.charAt(i);
			if (f.getStringWidth(c + strOut) + subCharsWidth <= maxWidth){
				if (keepRightSide){
					strOut = c + strOut;
				}
				else{
					strOut = strOut + c;
				}
			}
			else{
				break;
			}
		}
		if (keepRightSide){
			return substituteChars + strOut;
		}
		else{
			return strOut + substituteChars;
		}
		
	}
	
	
	/**
	 * Truncates a string to the number of characters in maxChars
	 * The character count includes the substitute characters (which will only be added if the string is truncated)
	 */
	public static String truncateStringChars(String strIn, String substituteChars, int maxChars, boolean keepRightSide){
		if (strIn.length() <= maxChars){return strIn;}
		strIn = strIn.replaceAll(" ", "");
		if (strIn.length() <= maxChars){return strIn;}
		if (keepRightSide){
			strIn = substituteChars + strIn.substring(strIn.length()-(maxChars-substituteChars.length()));
		}
		else{
			strIn = strIn.substring(0, maxChars-substituteChars.length()) + substituteChars;
		}
		return strIn;
	}
	
	
	/**
	 * Converts the new JSON strings with their escaped quotation marks back into regular old strings
	 * Hopefully this is just temporary.
	 * EditBookScreen seems to work with normal strings, but ReadBookScreen is converting the pages to JSON
	 */
	public static String deJSONify(String jsonIn){
		try{
			// func_240643_a_() is fromJson()
			ITextComponent i = ITextComponent.Serializer.func_240643_a_(jsonIn);
			if (i != null){
				return(i.getString());
			}
		}
		catch (JsonParseException jsonparseexception){
			// jsonIn was probably just a normal string, so there's no need to freak out the end user
		}
		return(jsonIn);
	}
	
	
	static class PageDetails{
		public final String fullPageText;
		public final int[] lineStartIndices;
		public final String[] lines;
		public final ITextComponent[] stylizedLines;
		
		PageDetails(String fullPageText, IntList lineStartIndices, List<String> lines, List<ITextComponent> stylizedLines){
			this.fullPageText = fullPageText;
			this.lineStartIndices = lineStartIndices.toIntArray();
			this.lines = lines.toArray(new String[0]);
			this.stylizedLines = stylizedLines.toArray(new ITextComponent[0]);
		}
	}
	
	
	static class Pages{
		private final List<BookUtilities.PageDetails> pages = new ArrayList<>();
		
		/**
		 * @return The requested page, or a blank page if index is invalid
		 */
		public BookUtilities.PageDetails get(int index){
			if (index >= 0 && index < this.pages.size()){
				return this.pages.get(index);
			}
			// Return an empty page
			IntList lineStartIndices = new IntArrayList();
			lineStartIndices.add(0);
			List<String> lines = new ArrayList<>();
			lines.add("");
			List<ITextComponent> stylizedLines = new ArrayList<>();
			stylizedLines.add(new StringTextComponent(""));
			return new PageDetails("", lineStartIndices, lines, stylizedLines);
		}
		
		
		/**
		 * Returns the pages as a list of strings
		 */
		public List<String> asStrings() {
			List<String> pageStrings = new ArrayList<>();
			for (BookUtilities.PageDetails page: this.pages){
				pageStrings.add(page.fullPageText);
			}
			return pageStrings;
		}
		
		
		public void add(BookUtilities.PageDetails page){
			this.pages.add(page);
		}
		
		
		public void addAll(Pages pagesToAdd){
			for (int i=0; i < pagesToAdd.pages.size(); i++){
				this.add(pagesToAdd.get(i));
			}
		}
	}
}
