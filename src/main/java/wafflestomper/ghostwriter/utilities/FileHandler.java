package wafflestomper.ghostwriter.utilities;

import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wafflestomper.ghostwriter.datastructures.PageDetails;
import wafflestomper.ghostwriter.datastructures.Pages;

import java.io.*;
import java.nio.charset.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class FileHandler {
	public static final String GHB_PAGE_BREAK = SharedConstants.GHB_PAGE_BREAK;
	public static final String GHB_FILE_EXTENSION = SharedConstants.GHB_FILE_EXTENSION;
	private static final int BOOK_TITLE_MAX_LEN = SharedConstants.BOOK_TITLE_MAX_LEN;
	private static final Printer PRINTER = new Printer();
	private static final Logger LOG = LogManager.getLogger();
	private final File bookSavePath;
	private final File signaturePath;
	private final Clipboard clipboard;
	private final List<File> lastListing = new ArrayList<>();
	public File currentPath;
	public File lastLoadedBook;
	private String lastCheckedPath = "";
	
	public FileHandler(Clipboard _clipboard) {
		this.clipboard = _clipboard;
		String path = Minecraft.getInstance().gameDirectory.getAbsolutePath();
		if (path.endsWith(".")) {
			path = path.substring(0, path.length() - 2);
		}
		// Create directories if necessary
		boolean dirSuccess = true;
		File defaultPath = new File(path, "mods" + File.separator + "Ghostwriter");
		if (!defaultPath.exists() && !defaultPath.mkdirs()) dirSuccess = false;
		this.bookSavePath = new File(defaultPath, "SavedBooks");
		if (!this.bookSavePath.exists() && !this.bookSavePath.mkdirs()) dirSuccess = false;
		this.signaturePath = new File(defaultPath, "Signatures");
		if (!this.signaturePath.exists() && this.signaturePath.mkdirs()) dirSuccess = false;
		this.currentPath = bookSavePath;
		if (!dirSuccess) {
			PRINTER.gamePrint(Printer.RED + "Couldn't create one or more directories. Things will probably break");
		}
	}
	
	/**
	 * Removes Java-style comments, whitespace preceding linebreak and page break symbols, and newline characters (\n)
	 */
	public static String cleanGHBString(String strIn) {
		// Remove single-line comments and any whitespace preceding them
		strIn = strIn.replaceAll("(?s)[\\t\\r\\n ]*//.*?((\\n)|(\\r\\n)|(\\Z))", "\n");
		// Remove multi-line comments
		strIn = strIn.replaceAll("(?s)((/\\*).*?((\\*/)|(\\Z)))|(((/\\*)|(\\A)).*?(\\*/))", "");
		// Remove whitespace preceding linebreak and page break characters
		strIn = strIn.replaceAll("[\\t\\r\\n ]+(##|" + GHB_PAGE_BREAK + ")", "$1");
		// Remove newline and carriage return characters
		strIn = strIn.replaceAll("[\\r\\n]", "");
		return strIn;
	}
	
	public File getSignaturePath() {
		return this.signaturePath;
	}
	
	public File getSavePath() {
		return this.bookSavePath;
	}
	
	public List<File> listFiles(File path, boolean forceRefresh) {
		if (!path.getAbsolutePath().equals(this.lastCheckedPath) || forceRefresh) {
			this.lastCheckedPath = path.getAbsolutePath();
			this.lastListing.clear();
			File[] newList = path.listFiles();
			if (newList == null) return this.lastListing;
			Arrays.sort(newList);
			List<File> files = new ArrayList<>();
			for (File f : newList) {
				if (f.isDirectory()) {
					this.lastListing.add(f);
				} else {
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
	public void navigateUp() {
		if (this.currentPath.getParentFile() == null) {
			return;
		}
		for (File root : File.listRoots()) {
			if (this.currentPath.equals(root)) {
				return;
			}
		}
		this.currentPath = this.currentPath.getParentFile();
	}
	
	public List<File> getValidRoots() {
		List<File> outList = new ArrayList<>();
		for (File root : File.listRoots()) {
			if (root.listFiles() != null) {
				outList.add(root);
			}
		}
		return outList;
	}
	
	public List<String> readFile(File path) {
		return readFile(path, "UTF-8");
	}
	
	public List<String> readFile(File path, String encoding) {
		List<String> out = new ArrayList<>();
		BufferedReader br;
		
		CharsetDecoder decoder = Charset.forName(encoding).newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPORT);
		decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), decoder));
		} catch (FileNotFoundException e) {
			PRINTER.gamePrint(Printer.RED + "File not found! " + path.getAbsolutePath());
			return null;
		}
		try {
			String line = br.readLine();
			while (line != null) {
				out.add(line);
				line = br.readLine();
			}
		} catch (CharacterCodingException e) {
			// ICEBERG! It seems we've hit a character that's not encoded with the specified encoding
			if (encoding.equals("UTF-8")) {
				PRINTER.gamePrint(Printer.DARK_GRAY + path.getAbsolutePath() + " doesn't seem to be UTF-8 encoded...");
				// Try ISO-8859-15
				try {
					br.close();
				} catch (IOException exc) {
					e.printStackTrace();
				}
				return readFile(path, "ISO-8859-15");
			}
			PRINTER.gamePrint(Printer.RED + "Couldn't find a suitable decoder for " + path.getAbsolutePath());
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			PRINTER.gamePrint(Printer.RED + "Error reading file! " + path.getAbsolutePath());
			return null;
		} finally {
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
	private boolean loadBookwormBook(File filePath) {
		List<String> f = readFile(filePath);
		// Bookworm txt files are always at least 4 lines long
		// The first line is the ID number
		if (f.size() >= 4 && StringUtils.isNumeric(f.get(0))) {
			// There's a good chance this is a bookworm book
			this.clipboard.clearBook();
			this.clipboard.title = f.get(1);
			if (this.clipboard.title.length() > BOOK_TITLE_MAX_LEN) {
				this.clipboard.title = this.clipboard.title.substring(0, BOOK_TITLE_MAX_LEN - 2) + "..";
			}
			this.clipboard.author = f.get(2);
			String bookText = f.get(f.size() - 1);
			
			// Split the book string anywhere there are two or more double colons, then split those into minecraft pages
			String[] largePages = bookText.split("(\\s::){2,}");
			for (String largePage : largePages) {
				largePage = largePage.replaceAll("\\s*::\\s*", "\n  ");
				Pages pages = BookUtilities.splitIntoPages(largePage, SharedConstants.BOOK_MAX_LINES);
				this.clipboard.pages.addAll(pages.asStrings());
			}
			
			this.clipboard.bookInClipboard = true;
			this.lastLoadedBook = filePath;
			return true;
		}
		return false;
	}
	
	public boolean writeFile(List<String> toWrite, File filePath) {
		boolean failedFlag = false;
		
		//Create directory if it doesn't exist
		File path = filePath.getParentFile();
		if (!path.exists()) {
			if (!path.mkdirs()) {
				failedFlag = true;
			}
		}
		
		//Write file
		if (!failedFlag) {
			try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
				for (String s : toWrite) {
					out.write(s + '\n');
				}
			} catch (IOException e) {
				LOG.error("Ghostwriter: Write failed!");
				LOG.error(e.getMessage());
				return false;
			}
		}
		
		if (failedFlag) {
			PRINTER.gamePrint(Printer.RED + "WRITING TO DISK FAILED!");
			return false;
		}
		return true;
	}
	
	public boolean loadBook(File filePath) {
		// Handle bookwork books in .txt files
		if (filePath.getName().endsWith(".txt")) {
			LOG.info("Trying to load .txt as bookworm book...");
			if (loadBookwormBook(filePath)) {
				return true;
			}
			LOG.info("Trying to load .txt as regular text file...");
			if (loadPlainText(filePath)) {
				return true;
			}
		}
		//Handle Ghostwriter books in .ghb
		if (filePath.getName().endsWith(GHB_FILE_EXTENSION)) {
			LOG.info("Loading GHB book..." + filePath);
			return loadBookFromGHBFile(filePath);
		}
		//This was not a valid book
		return false;
	}
	
	public boolean loadPlainText(File filePath) {
		Clipboard book = new Clipboard();
		List<String> rawFile = readFile(filePath);
		if (rawFile == null || rawFile.isEmpty()) {
			//File was not read successfully
			return false;
		}
		// Add newline characters back in
		StringBuilder concatFile = new StringBuilder();
		for (String line : rawFile) {
			concatFile.append(line).append("\n");
		}
		// Split into pages
		String pageBreak = ">>>><<<<>>>><<<<";
		Pages splitPages;
		splitPages = BookUtilities.splitIntoPages(concatFile.toString(), SharedConstants.BOOK_MAX_LINES, pageBreak);
		book.pages.addAll(splitPages.asStrings());
		book.bookInClipboard = true;
		this.clipboard.clone(book);
		this.lastLoadedBook = filePath;
		return true;
	}
	
	
	public boolean loadBookFromGHBFile(File filePath) {
		Clipboard book = new Clipboard();
		List<String> rawFile = readFile(filePath);
		if (rawFile == null || rawFile.isEmpty()) {
			//File was not read successfully
			return false;
		}
		// Load title and author (if they exist)
		StringBuilder concatFile = new StringBuilder();
		for (String line : rawFile) {
			if (line.toLowerCase().startsWith("title:") && book.title.isEmpty()) {
				if (line.length() >= 7) {
					book.title = cleanGHBString(line.substring(6)).trim();
					if (line.contains("/*")) {
						concatFile.append(line.substring(line.indexOf("/*"))).append("\\n");
					}
				}
			} else if (line.toLowerCase().startsWith("author:") && book.author.isEmpty()) {
				if (line.length() >= 8) {
					book.author = cleanGHBString(line.substring(7)).trim();
					if (line.contains("/*")) {
						concatFile.append(line.substring(line.indexOf("/*"))).append("\\n");
					}
				}
			} else {
				concatFile.append(line).append("\n");
			}
		}
		
		// Remove comments and newlines
		String concatFileStr = cleanGHBString(concatFile.toString());
		
		// Strip extra format characters
		concatFileStr = BookUtilities.removeRedundantFormatChars(concatFileStr, GHB_PAGE_BREAK);
		
		//convert all the linebreak characters (##) to newline characters (\n) and split into pages
		concatFileStr = concatFileStr.replaceAll("##", "\\\n");
		Pages splitPages;
		splitPages = BookUtilities.splitIntoPages(concatFileStr, SharedConstants.BOOK_MAX_LINES, GHB_PAGE_BREAK);
		book.pages.addAll(splitPages.asStrings());
		book.bookInClipboard = true;
		this.clipboard.clone(book);
		this.lastLoadedBook = filePath;
		return true;
	}
	
	
	public void saveBookToGHBFile(String title, String author, List<String> pages, File savePath) {
		PRINTER.gamePrint(Printer.GRAY + "Saving book to file...");
		List<String> toWrite = new ArrayList<>();
		toWrite.add("//Book saved in GHB format at " + this.getUTC());
		if (!title.isEmpty()) {
			toWrite.add("title:" + title);
		}
		if (!author.isEmpty()) {
			toWrite.add("author:" + author);
		}
		toWrite.add("//=======================================");
		for (int i = 0; i < pages.size(); i++) {
			String pageAsString = pages.get(i);
			//Strip the bizarre quote marks from the start and end of the string
			while (pageAsString.startsWith("\"") && pageAsString.endsWith("\"")) {
				pageAsString = pageAsString.substring(1, pageAsString.length() - 1);
			}
			// Convert all escaped newline characters to real newline characters
			pageAsString = pageAsString.replaceAll("\\\\n", "\\\n");
			
			// Split the string into book width lines. Since the text should be coming from a valid
			// book page, we shouldn't have to split it into pages
			PageDetails currPage = BookUtilities.splitIntoPages(pageAsString, 0).get(0);
			
			// Replace newline characters with double hashes
			for (String line : currPage.lines) {
				toWrite.add(line.replaceAll("\\n", "##"));
			}
			
			// Add page breaks with line numbers
			if (i < pages.size() - 1) {
				toWrite.add(GHB_PAGE_BREAK + "  // Page " + (i + 2));
			}
		}
		if (writeFile(toWrite, savePath)) {
			PRINTER.gamePrint(Printer.GREEN + "Book saved to: " + savePath);
		} else {
			PRINTER.gamePrint(Printer.RED + "WRITING BOOK TO DISK FAILED!");
		}
	}
	
	
	public String getUTC() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss'Z'");
		df.setTimeZone(tz);
		return df.format(new Date());
	}
}
