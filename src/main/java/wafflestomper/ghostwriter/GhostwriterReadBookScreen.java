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
	
	/**
	 * Override from ReadBookScreen
	 */
	@Override
	public void init(){
		super.init();
		this.ghostLayer.init();
		this.updateButtons();
	}
	
	/**
	 * Override from ReadBookScreen
	 */
	@Override
	public void updateButtons(){
		this.ghostLayer.updateButtons();
	}
	
	
	/**
	 * Called by GhostLayer
	 */
	@Override
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
	
	
	/**
	 * Called by GhostLayer
	 */
	@Override
	public int getBookPageCount(){
		return this.getPageCount();
	}
	
	
	/**
	 * Called by GhostLayer
	 */
	@Override
	public void updateVanillaButtons(){
		super.updateButtons();
	}
	
	
	/**
	 * Called by GhostLayer
	 */
	@Override
	public Button addGhostButton(Button button) {
		return this.addButton(button);
	}
	
	
	@Override
	public String getPageText(int pageNum) {
		// func_238806_b_() either returns the page text or a blank ITextComponent
		return this.bookInfo.func_238806_b_(pageNum).toString();
	}
	
	
	/**
	 * Called by GhostLayer
	 */
	@Override
	public int getCurrPage() {
		return this.currPage;
	}
	
	@Override
	public void setCurrPage(int pageNum) {
	
	}
	
	/**
	 * Called by GhostLayer
	 * Unused because signed books are read-only
	 */
	@Override
	public void insertTextIntoPage(String insertChars) {}
	
	@Override
	public void insertNewPage(int atPageNum, String pageText) {}
	
	@Override
	public void removePage(int pageNum) {}
	
	@Override
	public void replaceBookPages(List<String> newPages) {}
	

	@Override
	public void setPageText(int pageNum, String pageText) {
	
	}
	
	@Override
	public boolean isBookBeingSigned() {
		return false;
	}
	
	
	@Override
	public void setBookTitle(String title) {}
	
	@Override
	public void bookChanged() {}
	
	
	@Override
	public String getBookTitle() {
		return this.ghostLayer.bookTitle;
	}
}
