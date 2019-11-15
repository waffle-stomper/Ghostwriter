package wafflestomper.ghostwriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonParseException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

public class BookUtilities {
	public static final int BOOK_TEXT_WIDTH = 116;
	public static final char SPLIT_CHAR = '\u1337';
	private static final Minecraft mc = Minecraft.getInstance();
	
	
	/**
	 * Prefix and suffix are optional, but if they are set to anything other than a blank string, they will be counted
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
			strIn = substituteChars + strIn.substring(strIn.length()-(maxChars-substituteChars.length()), strIn.length());
		}
		else{
			strIn = strIn.substring(0, maxChars-substituteChars.length()) + substituteChars;
		}
		return strIn;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
    /**
     * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
     */
    private static boolean isFormatSpecial(char par0)
    {
        return par0 >= 107 && par0 <= 111 || par0 >= 75 && par0 <= 79 || par0 == 114 || par0 == 82;
    }

    
    /**
     * Digests a string for nonprinting formatting characters then returns a string containing only that formatting.
     */
    private static String getFormatFromString(String par0Str)
    {
        String s1 = "";
        int i = -1;
        int j = par0Str.length();

        while ((i = par0Str.indexOf(167, i + 1)) != -1)
        {
            if (i < j - 1)
            {
                char c0 = par0Str.charAt(i + 1);

                if (isFormatColor(c0))
                {
                    s1 = "\u00a7" + c0;
                }
                else if (isFormatSpecial(c0))
                {
                    s1 = s1 + "\u00a7" + c0;
                }
            }
        }

        return s1;
    }
	
    
	/**
     * Checks if the char code is a hexadecimal character, used to set colour.
     */
    private static boolean isFormatColor(char par0)
    {
        return par0 >= 48 && par0 <= 57 || par0 >= 97 && par0 <= 102 || par0 >= 65 && par0 <= 70;
    }

	
	/**
     * Determines how many characters from the string will fit into the specified width.
     */
    private static int sizeStringToWidth(String par1Str, int par2)
    {
        int j = par1Str.length();
        int k = 0;
        int l = 0;
        int i1 = -1;

        for (boolean flag = false; l < j; ++l)
        {
            char c0 = par1Str.charAt(l);

            switch (c0)
            {
                case 10:
                    --l;
                    break;
                case 167:
                    if (l < j - 1)
                    {
                        ++l;
                        char c1 = par1Str.charAt(l);

                        if (c1 != 108 && c1 != 76)
                        {
                            if (c1 == 114 || c1 == 82 || isFormatColor(c1))
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = true;
                        }
                    }

                    break;
                case 32:
                    i1 = l;
                default:
                    k += mc.fontRenderer.getCharWidth(c0);

                    if (flag)
                    {
                        ++k;
                    }
            }

            if (c0 == 10)
            {
                ++l;
                i1 = l;
                break;
            }

            if (k > par2)
            {
                break;
            }
        }

        return l != j && i1 != -1 && i1 < l ? i1 : l;
    }
	
    
    /**
     * Inserts splitchar into a string to wrap it within the specified width.
     */
    private static String wrapFormattedStringToWidth(String strIn, int maxWidth){
        int maxCharsInWidth = sizeStringToWidth(strIn, maxWidth);

        if (strIn.length() <= maxCharsInWidth){
            return strIn;
        }
        else{
        	//grab the most characters you can fit into maxWidth and put it in s1
            String s1 = strIn.substring(0, maxCharsInWidth);
            //grab the very next character after that string and put it in c0
            char c0 = strIn.charAt(maxCharsInWidth);
            boolean newlineOrSpace = c0 == 32 || c0 == 10; //Check if it's a newline character or a space
            String s2 = strIn.substring(maxCharsInWidth + (newlineOrSpace ? 1 : 0));
            if (newlineOrSpace){s1 += c0;}
            return s1 + SPLIT_CHAR + wrapFormattedStringToWidth(s2, maxWidth);
        }
    }
    
    
    
    
    
    
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		

    
	/**
	 * Removes trailing newline characters then splits the string into a list
	 * of 116 pixel wide strings (since that's the width of a book).
	 * Note: this won't split it up into pages. If you want to do that too,
	 * you should use stringToPages()
	 * Note 2: this preserves trailing whitespace (unlike FontRenderer.listFormattedStringToWidth())
	 */
	public static List<String> splitStringIntoLines(String str){
		//Trim trailing newline characters
		while (str.endsWith("\n") || str.endsWith(" ")){
			str = str.substring(0, str.length() - 1);
		}
		//Split string at newline characters
		String[] lines = str.split("\\n");
		List<String> out = new ArrayList<String>();
		for (String line : lines){
			out.addAll(Arrays.asList(wrapFormattedStringToWidth(line, BOOK_TEXT_WIDTH).split("" + SPLIT_CHAR)));
		}
		//return Arrays.asList(wrapFormattedStringToWidth(str, BOOK_TEXT_WIDTH).split("" + SPLIT_CHAR));
		return out;
	}
	

	/**
	 * Splits a monolithic string into a list of strings, each representing one
	 * book page.
	 */
	public static List<String> stringToPages(String str){
		String wrapped = wrapFormattedStringToWidth(str, BOOK_TEXT_WIDTH);
		List<String> pages = new ArrayList<String>();
		int newLineCount = 0;
		int charCount = 0;
		char currChar;
		int lastSubstringEnd = 0;
		for (int i=0; i<wrapped.length(); i++){
			currChar = wrapped.charAt(i);
			if (currChar == SPLIT_CHAR){
				newLineCount++;
			}
			else{
				charCount++;
			}
			
			if (newLineCount == 13){
				//we can throw away this line break
				pages.add(wrapped.substring(lastSubstringEnd, i).replaceAll("" + SPLIT_CHAR, ""));
				lastSubstringEnd = i+1;
				newLineCount = 0;
				charCount = 0;
			}
			else if (charCount == 256){
				//go back and find the last instance of a space or newline
				while(i>=0 && currChar != '\n' && currChar != ' ' && currChar != SPLIT_CHAR){
					i--;
					currChar = wrapped.charAt(i);
				}
				pages.add(wrapped.substring(lastSubstringEnd, i).replaceAll("" + SPLIT_CHAR, ""));
				lastSubstringEnd = i+1;
				newLineCount = 0;
				charCount = 0;
			}
			//add the last little bit of the string as a page
			if (i == wrapped.length()-1 && lastSubstringEnd < i){
				pages.add(wrapped.substring(lastSubstringEnd, wrapped.length()).replaceAll("" + SPLIT_CHAR, ""));
			}
		}
		return pages;
	}
	
	/**
	 * Splits a monolithic string into a list of strings, each representing one 
	 * book page. New pages are started after 13 lines, 256 character, or the page break symbol (whichever is first)
	 * @param str Input string
	 * @param pageBreakString The symbol that denotes a page break (this will be removed during conversion)
	 * @return ArrayList of Strings representing pages
	 */
	public static List<String> stringWithPageBreaksToPages(String str, String pageBreakString){
		//Remove any page breaks from the beginning of the string
		while (str.startsWith(pageBreakString)){
			str = str.substring(pageBreakString.length(), str.length()-1);
		}
		String[] pageBroken = str.split(pageBreakString);
		List<String> out = new ArrayList<String>();
		for (String largePage : pageBroken){
			out.addAll(stringToPages(largePage));
		}
		
		// This is a quick hack to remove blank pages until I can figure out why they're
		//   being inserted erroneously
		// TODO: FIX THIS PROPERLY
		List<String> cleanedOut = new ArrayList<String>();
		for (String page : out){
			if (page.replaceAll("[ \n\r\t]|(\\u00A7.)", "").length() > 0){
				cleanedOut.add(page);
			}
		}
		
		return cleanedOut;
	}
	
	
	/**
	 * Converts the new JSON strings with their escaped quotation marks back into regular old strings
	 * Hopefully this is just temporary.
	 */
	public static String deJSONify(String jsonIn){
		/*
		 //This is from the rendering algorithm and seems to produce usable results
		 //It looks like it iterates through line by line, converting them and then printing them on the screen
		 i1 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.field_175386_A.size());

        for (int k1 = 0; k1 < i1; ++k1)
        {
            IChatComponent ichatcomponent2 = (IChatComponent)this.field_175386_A.get(k1);
            this.fontRendererObj.drawString(ichatcomponent2.getUnformattedText(), k + 36, b0 + 16 + 16 + k1 * this.fontRendererObj.FONT_HEIGHT, 0);
        }
		 */
		
		try{
    		ITextComponent i = ITextComponent.Serializer.fromJson(jsonIn);
    		if (i != null){
    			String out = i.getFormattedText();
    			return(out);
    		}
        }
        catch (JsonParseException jsonparseexception){
        	//Do nothing for now
            //jsonparseexception.printStackTrace();
        }
		return(jsonIn);
	}
}
