package wafflestomper.ghostwriter;

import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GhostwriterReadBookScreen extends ReadBookScreen implements IGhostBook{
	
	private final GhostLayer ghostLayer;
	

	public GhostwriterReadBookScreen(ReadBookScreen.IBookInfo bookInfoIn, ItemStack currStack){
		super(bookInfoIn);
		this.ghostLayer = new GhostLayer(this, this, false);
		if (currStack != null){
			CompoundNBT compoundnbt = currStack.getTag();
			if (compoundnbt != null) {
				this.ghostLayer.setTitleAuthor(compoundnbt.getString("title"), compoundnbt.getString("author"));
			}
		}
	}
	
	
	/**
	 * Helper function that extracts the pages from the book until I find a cleaner way to do this
	 */
	private List<String> extractBookPages(){
		// TODO: Perhaps we should use the getPage() method provided by IBookInfo instead? I think that would negate the
		//       JSON weirdness we get from some read books
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
	
	
	/**
	 * Called by file browser
	 */
	public void saveBookToDisk(File savePath){
		// TODO: Refactor this away
		this.ghostLayer.saveBookToDisk(savePath);
	}
	
	
	@Override  // From ReadBookScreen
	public void init(){
		super.init();
		this.ghostLayer.init();
		this.updateButtons();
	}
	
	
	@Override  // From ReadBookScreen
	public void updateButtons(){
		this.ghostLayer.updateButtons();
	}
	
	
	@Override  // From IGhostBook
	public List<String> pagesAsList(){
		List<String> pages = new ArrayList<>();
		for (int i=0; i<this.getPageCount(); i++){
			// Ugly hack to convert the new JSON "Yo dawg I heard you like strings, so I put a string in your string" strings
			//  back to the old-style literal strings that everyone knows and loves. I'll update this to do the opposite once
			//  we're finally allowed to send JSON strings to the server. It also converts to old-school formatting codes
			String pageText = BookUtilities.deJSONify(this.extractBookPages().get(i));
			pages.add(pageText);
		}
		return pages;
	}
	
	
	@Override  // From IGhostBook
	public int getBookPageCount(){
		return this.getPageCount();
	}
	
	
	@Override  // From IGhostBook
	public void updateVanillaButtons(){
		super.updateButtons();
	}
	
	
	@Override  // From IGhostBook
	public Button addGhostButton(Button button) {
		return this.addButton(button);
	}
	
	
	@Override  // From IGhostBook
	public String getPageText(int pageNum) {
		// func_238806_b_() either returns the page text or a blank ITextComponent
		return this.bookInfo.func_238806_b_(pageNum).toString();
	}
	
	
	@Override  // From IGhostBook
	public int getCurrPage() {
		return this.currPage;
	}
	
	
	@Override  // From IGhostBook
	public boolean isBookBeingSigned() {
		return false;
	}
	
	
	@Override  // From IGhostBook
	public void bookChanged(boolean setModifiedFlag) {
		this.cachedPage = -1;
		// TODO: Do we need to do anything else?
	}
	
	
	@Override  // From IGhostBook
	public String getBookTitle() {
		return this.ghostLayer.bookTitle;
	}
	
	
	// Unused methods that only apply to unsigned books
	@Override  // From IGhostBook
	public void setCurrPage(int pageNum){}
	@Override  // From IGhostBook
	public void setBookTitle(String title){}
	@Override  // From IGhostBook
	public void setPageText(int pageNum, String pageText){}
	@Override  // From IGhostBook
	public void insertText(String insertChars){}
	@Override  // From IGhostBook
	public void insertNewPage(int atPageNum, String pageText){}
	@Override  // From IGhostBook
	public void removePage(int pageNum){}
	@Override  // From IGhostBook
	public void replaceBookPages(List<String> newPages){}
}
