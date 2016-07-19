//TODO: Standardize function names

package wafflestomper.ghostwriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;

public class FileHandler {
	private File defaultPath;
	private File bookSavePath;
	private File signaturePath;
	private Printer printer = new Printer();
	private Clipboard clipboard;
	
	public File currentPath;
	private List<File> lastListing = new ArrayList();
	private String lastCheckedPath = "";
	
	//File formats
	public static final int FORMAT_GHOSTWRITER = 0;
	public static final int FORMAT_BOOKWORM = 1;
	
	public FileHandler(Clipboard _clipboard){
		this.clipboard = _clipboard;
		String path = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
		if (path.endsWith(".")){
			path = path.substring(0, path.length()-2);
		}
		this.defaultPath = new File(path, "mods" + File.separator + "Ghostwriter");
		if (!this.defaultPath.exists()) this.defaultPath.mkdirs();
		this.bookSavePath = new File(defaultPath, "SavedBooks");
		if (!this.bookSavePath.exists()) this.bookSavePath.mkdirs();
		this.signaturePath = new File(defaultPath, "Signatures");
		if (!this.signaturePath.exists()) this.signaturePath.mkdirs();
	}
	
	
	public File getDefaultPath(){
		return this.defaultPath;
	}
	
	public File getSignaturePath(){
		return this.signaturePath;
	}
	
	public File getSavePath(){
		return this.bookSavePath;
	}
	
	public List<File> listFiles(File path){
		if (!path.getAbsolutePath().equals(this.lastCheckedPath)){
			this.lastCheckedPath = path.getAbsolutePath();
			this.lastListing.clear();
			File[] newList = path.listFiles();
			List<File> files = new ArrayList();
			for (File f : newList){
				if (f.isDirectory()){
					this.lastListing.add(f);
				}
				else{
					files.add(f);
				}
			}
			this.lastListing.addAll(files);
		}
		return this.lastListing;
	}
	
	/**
	 * Navigates into the parent folder (of this.currentPath)
	 */
	public void navigateUp(){
		for (File root : File.listRoots()){
			if (this.currentPath.equals(root)){
				return;
			}
		}
		this.currentPath = this.currentPath.getParentFile();
	}
	
	public List<File> getValidRoots(){
		List<File> outList = new ArrayList();
		for (File root : File.listRoots()){
			if (root.listFiles() != null){
				outList.add(root);
			}
		}
		return outList;
	}
	
	public List<String> readFile(File path){
		return readFile(path, "UTF-8");
	}
	
	public List<String> readFile(File path, String encoding){
		List<String> out = new ArrayList();
		BufferedReader br = null;
		
		CharsetDecoder decoder = Charset.forName(encoding).newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPORT);
		decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), decoder));
		} catch (FileNotFoundException e) {
			this.printer.gamePrint(Printer.RED + "File not found! " + path.getAbsolutePath());
			return null;
		}
	    try {
	        String line = br.readLine();
	        while (line != null) {
	        	out.add(line);
	            line = br.readLine();
	        }
	    } 
	    catch (CharacterCodingException e){
	    	// ICEBERG! It seems we've hit a character that's not encoded with the specified encoding
	    	if (encoding == "UTF-8"){
	    		this.printer.gamePrint(Printer.DARK_GRAY + path.getAbsolutePath() + " doesn't seem to be UTF-8 encoded...");
	    		// Try ISO-8859-15
	    		try {
					br.close();
				} catch (IOException exc) {
					e.printStackTrace();
				}
	    		return readFile(path, "ISO-8859-15");
	    	}
	    	this.printer.gamePrint(Printer.RED + "Couldn't find a suitable decoder for " + path.getAbsolutePath());
	    	return null;
	    }
	    catch (IOException e) {
			e.printStackTrace();
			this.printer.gamePrint(Printer.RED + "Error reading file! " + path.getAbsolutePath());
			return null;
		} 
	    finally {
	        try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		return out;
	}
	
	/**
	 * Loads a bookworm book from filePath into the clipboard
	 */
	private boolean loadBookwormBook(File filePath){
		List<String> f = readFile(filePath);
		
		/*
		Bookworm format:
		
		<id number>
		<title>
		<author>
		(optional)|!|hidden_key_n>|<hidden_data_n> (this may be repeated on successive lines)
		<Book text as a single line>
		<empty line>
		
		e.g.
		###################################
		46
		Valentino Rossi - Portrait of a speed god
		Mat Oxley
		|!|hiddenkey0|hiddendata0
		|!|hiddenkey1|hiddendata1
		"The first time you ride the 500, it's like, F**K!" -Valentino Rossi ::This is the next paragraph.

		###################################
		*/
		
		//Bookworm txt files are always at least 4 lines long
		//The first line is the ID number
		if (f.size() >= 4 && StringUtils.isNumeric(f.get(0))){	
			//There's a good chance this is a bookworm book
			this.clipboard.clearBook();
			this.clipboard.title = BookUtilities.truncateStringChars(f.get(1), "..", 16, false);
			this.clipboard.author = f.get(2);
			String bookText = f.get(f.size()-1);
			
			//split the book string anywhere there are two or more double colons
			String[] largePages = bookText.split("(\\s::){2,}");
			for (String largePage : largePages){
				largePage = largePage.replaceAll("\\s*::\\s*", "\n  ");
				this.clipboard.pages.addAll(BookUtilities.stringToPages(largePage));
			}
			
			this.clipboard.bookInClipboard = true;
			return true;
		}
		return false;
	}
	
	
	public boolean writeFile(List<String> toWrite, File filePath){
		boolean failedFlag = false;
		
		//Create directory if it doesn't exist
		File path = filePath.getParentFile();
		if (!path.exists()){
			if (!path.mkdirs()){
				failedFlag = true;
			}
		}
		
		//Write file
		if (!failedFlag){
			try {
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
				try{
					for (String s : toWrite){
						out.write(s + '\n');
					}
				}
				catch (IOException e){
					failedFlag = true;
					System.out.println("Ghostwriter: Write failed!");
					System.out.println(e.getMessage());
					return false;
				}
				finally{
					out.close();
				}
			} 
			catch (IOException e) {
				failedFlag = true;
				System.out.println("Ghostwriter: Write failed!");
				System.out.println(e.getMessage());
				return false;
			}
		}
		
		if (failedFlag){
			this.printer.gamePrint(printer.RED + "WRITING TO DISK FAILED!");
			return false;
		}		
		return true;
	}
	
	
	public boolean loadBook(File filePath){
		// Handle bookwork books in .txt files
		if (filePath.getName().endsWith(".txt")){
			System.out.println("Trying to load .txt as bookworm book...");
			if (loadBookwormBook(filePath)){return true;}
			System.out.println("Trying to load .txt as regular text file...");
			if (loadPlainText(filePath)){return true;}
		}
		//Handle Ghostwriter books in .ghb
		if (filePath.getName().endsWith(".ghb")){
			System.out.println("Loading GHB book..." + filePath);
			if (loadBookFromGHBFile(filePath)){return true;}
		}
		//This was not a valid book
		return false;
	}
	
	
	/**
	 * Removes Java-style comments, whitespace preceding linebreak and pagebreak symbols, and newline characters (\n)
	 */
	public static String cleanGHBString(String strIn){
		//Remove single-line comments
		//strIn = strIn.replaceAll("//.*?(##|\\n|>>>>)","$1"); //what a moran
		strIn = strIn.replaceAll("(?s)//.*?((\\n)|(\\r\\n)|(\\Z))","\n");
		//Remove multi-line comments
		strIn = strIn.replaceAll("(?s)((/\\*).*?((\\*/)|(\\Z)))|(((/\\*)|(\\A)).*?(\\*/))", "");
		//remove whitespace preceding linebreak and pagebreak characters
		strIn = strIn.replaceAll("[\\t\\r\\n ]+(##|>>>>)", "$1");
		//remove newline and carriage return characters
		strIn = strIn.replaceAll("[\\r\\n]", "");
		return strIn;
	}
	
	
	public boolean loadPlainText(File filePath){
		Clipboard book = new Clipboard();
		List<String> rawFile = readFile(filePath);
		if (rawFile == null || rawFile.isEmpty()){
			//File was not read successfully
			return false;
		}
		//Remove comments and anything else that can't be stored in a Minecraft book
		String concatFile = "";
		for (String line : rawFile){
			concatFile += line + "\n";
		}
		book.pages.addAll(BookUtilities.stringWithPageBreaksToPages(concatFile, ">>>><<<<>>>><<<<"));
		book.bookInClipboard = true;
		this.clipboard.clone(book);
		return true;
	}
	
	
	public boolean loadBookFromGHBFile(File filePath){
		Clipboard book = new Clipboard();
		List<String> rawFile = readFile(filePath);
		if (rawFile == null || rawFile.isEmpty()){
			//File was not read successfully
			return false;
		}
		//Remove comments and anything else that can't be stored in a Minecraft book
		String concatFile = "";
		for (String line : rawFile){
			//TODO: Eliminate this code repetition
			if (line.toLowerCase().startsWith("title:") && book.title.isEmpty()){
				if (line.length() >= 7){
					book.title = cleanGHBString(line.substring(6)).trim();
					if (line.contains("/*")){
						concatFile += line.substring(line.indexOf("/*")) + "\\n";
					}
				}
			}
			else if (line.toLowerCase().startsWith("author:") && book.author.isEmpty()){
				if (line.length() >= 8){
					book.author = cleanGHBString(line.substring(7)).trim();
					if (line.contains("/*")){
						concatFile += line.substring(line.indexOf("/*")) + "\\n";
					}
				}
			}
			else{
				concatFile += line + "\n";
			}
		}
		concatFile = cleanGHBString(concatFile);
		
		//convert all the linebreak characters (##) to newline characters (\n) and split into pages
		concatFile = concatFile.replaceAll("##", "\\\n");

		book.pages.addAll(BookUtilities.stringWithPageBreaksToPages(concatFile, ">>>>"));
		book.bookInClipboard = true;
		this.clipboard.clone(book);
		return true;
	}
	
	
	public boolean saveBookToGHBFile(String title, String author, List<String> pages, File savePath){
		this.printer.gamePrint(Printer.GRAY + "Saving book to file...");
		List<String> toWrite = new ArrayList();
		toWrite.add("//Book saved in GHB format at " + getUTC());
		if (!title.isEmpty()){toWrite.add("title:" + title);}
		if (!author.isEmpty()){toWrite.add("author:" + author);}
		toWrite.add("//=======================================");
		for (int i=0; i<pages.size(); i++){
			String pageAsString = pages.get(i);
			//Strip the bizarre quote marks from the start and end of the string
			while (pageAsString.startsWith("\"") && pageAsString.endsWith("\"")){
				pageAsString = pageAsString.substring(1, pageAsString.length()-1);
			}
			//convert all escaped newline characters to real newline characters
			pageAsString = pageAsString.replaceAll("\\\\n", "\\\n");
			//Split the string into 116 pixel maximum lines
			List<String> currPage = BookUtilities.splitStringIntoLines(pageAsString);
			// Replace newline characters with double hashes and add the double hashes to the end of each line
			for (String line : currPage){
				toWrite.add(line.replaceAll("\\n", "##") + "##");
			}
			//Add pagebreaks
			if (i < pages.size()-1){
				toWrite.add(">>>>");
			}
		}
		if (writeFile(toWrite, savePath)){
			this.printer.gamePrint(Printer.GREEN + "Book saved to: " + savePath);
			return true;
		}
		else{
			this.printer.gamePrint(printer.RED + "WRITING BOOK TO DISK FAILED!");
			return false;
		}
	}
	
	
	/**
	 * This supports legacy type saving where the filename and path are automatically generated
	 * @param title
	 * @param author
	 * @param pages
	 * @return Success
	 */
	public boolean saveBookToGHBFile(String title, String author, List<String> pages){
		String utcTime = getUTC();
		title = title.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9\\.]", "");
		author = author.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9\\.]", "");
		File saveFile = new File(this.bookSavePath, title + "_" + author + "_" + utcTime + ".ghb");
		return(saveBookToGHBFile(title, author, pages, saveFile));
	}
	
	
	public String getUTC(){
		TimeZone tz = TimeZone.getTimeZone("UTC");
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HHmm.S'Z'");
	    df.setTimeZone(tz);
	    return df.format(new Date());
	}
	
	
}
