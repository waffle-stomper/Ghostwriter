package wafflestomper.ghostwriter;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;


@OnlyIn(Dist.CLIENT)
public class GhostwriterEditBookScreen extends EditBookScreen implements IGhostBook {
	
	protected final GhostLayer ghostLayer;
	private int currPageLineCount = 0;
	private long lastPageLineCountUpdate = 0;
	
	
	public GhostwriterEditBookScreen(PlayerEntity editingPlayer, ItemStack book, Hand hand) {
		super(editingPlayer, book, hand);
		this.ghostLayer = new GhostLayer(this, this, true);
		
		// Swap out the title input util for one that allows longer titles and updates the title in GhostLayer
		// WrittenBookItem.validBookTagContents declares the book invalid if the title is over 32 characters
		field_238749_v_ = new TextInputUtil(
				() -> this.bookTitle,
				(p_238772_1_) -> {
					this.bookTitle = p_238772_1_;
					this.ghostLayer.setBookTitle(this.bookTitle);
				},
				this::func_238773_g_,  // getClipboardText
				this::func_238760_a_,  // setClipboardText
				(p_238771_0_) -> p_238771_0_.length() <= SharedConstants.BOOK_TITLE_MAX_LEN);
	}
	
	
	@Override  // From EditBookScreen
	public void init() {
		super.init();
		this.ghostLayer.init();
		
		// Move standard buttons
		this.buttonSign.setWidth(SharedConstants.LARGE_BUTTON_WIDTH);
		this.buttonFinalize.setWidth(SharedConstants.LARGE_BUTTON_WIDTH);
		this.buttonDone.setWidth(SharedConstants.LARGE_BUTTON_WIDTH);
		this.buttonCancel.setWidth(SharedConstants.LARGE_BUTTON_WIDTH);
		this.buttonSign.x = SharedConstants.BUTTON_SIDE_OFFSET;
		this.buttonFinalize.x = SharedConstants.BUTTON_SIDE_OFFSET;
		this.buttonDone.x = SharedConstants.BUTTON_SIDE_OFFSET;
		this.buttonCancel.x = SharedConstants.BUTTON_SIDE_OFFSET;
		this.buttonSign.y = 120;
		this.buttonFinalize.y = 120;
		this.buttonDone.y = 145;
		this.buttonCancel.y = 145;
		
		this.updateVanillaButtons();
	}
	
	
	@Override  // From EditBookScreen
	public void tick() {
		this.ghostLayer.tick();
		super.tick();
	}
	
	
	@Override // From EditBookScreen
	public void updateButtons() {
		this.ghostLayer.updateButtons();
	}
	
	
	/**
	 * Counts the lines on the current page, returning a cached version most of the time
	 * <p>
	 * Unfortunately the vanilla code makes it borderline impossible to extract the word-wrapped lines that
	 * EditBookScreen uses in render(), so we have to split it ourselves
	 * <p>
	 * Note that this will need to be updated periodically so that it matches the code in EditBookScreen
	 */
	private int getCurrPageLineCount() {
		if (System.currentTimeMillis() - lastPageLineCountUpdate >= 250) {
			this.currPageLineCount = BookUtilities.splitIntoPages(this.getCurrPageText(), 0).get(0).lines.length;
			this.lastPageLineCountUpdate = System.currentTimeMillis();
		}
		return this.currPageLineCount;
	}
	
	
	/**
	 * Visual patches:
	 * - Warn when the title is over the vanilla 15 character limit
	 * - Warn when the page is over the vanilla 14 line limit
	 * - Warn when the page is over the multiplayer 256 character limit
	 * - Add a high contrast background behind extended length titles
	 */
	@Override  // From EditBookScreen
	@ParametersAreNonnullByDefault
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		
		// Render long title and warning (if necessary)
		if (this.bookGettingSigned && this.bookTitle.length() > 15) {
			// Show the title length
			String textLen = "Title length: " + this.bookTitle.length();
			// params are matrixStack, x, y, color
			this.font.drawString(matrixStack, textLen, 169, 20, 0xFF3333);
			
			// Add extra background width amd re-render the title because the new background covers up the vanilla title
			String textTitle = this.bookTitle;
			textTitle += (this.updateCount / 6 % 2 == 0 ? TextFormatting.BLACK : TextFormatting.GRAY) + "_";
			int bookLeftSide = (this.width - 192) / 2;
			int titleWidth = this.getTextWidth(textTitle);
			int titleMinX = bookLeftSide + 36 + (114 - titleWidth) / 2;
			int titleMaxX = titleMinX + titleWidth;
			// color for the fill() method is MSB->LSB: alpha, r, g, b, (each 8 bits)
			AbstractGui.fill(matrixStack, titleMinX - 5, 48, titleMaxX + 5, 60, 0xFFFFF9EC);
			this.font.drawString(matrixStack, textTitle, (float) (titleMinX), 50.0F, 0);
			
			// Show the long title warning
			String s = "Warning: the vanilla client restricts titles to 15 characters. " +
					"Set longer titles at your own risk";
			StringTextComponent lengthWarning = new StringTextComponent(s);
			// params are text, x, y, width, color
			this.font.func_238418_a_(lengthWarning, 153, 116, 114, 0xFF3333);
		}
		
		// Add warnings about character and line limits
		// Things get weird over 256 characters, so we don't bother showing the line warning in that case
		if (!this.bookGettingSigned) {
			String warning = "";
			if (this.getCurrPageText().length() > 256) {
				warning = "Over 256 char limit!";
			} else if (this.getCurrPageLineCount() > SharedConstants.BOOK_MAX_LINES) {
				warning = "Over " + SharedConstants.BOOK_MAX_LINES + " line limit!";
			}
			
			if (warning.length() > 0) {
				this.font.drawString(matrixStack, "Warning:", 5, 176, 0xFF3333);
				this.font.drawString(matrixStack, warning, 5, 185, 0xFF3333);
			}
		}
	}
	
	
	/**
	 * Patch to enable pasting from the clipboard into the title of a book when signing it
	 */
	@Override  // From EditBookScreen
	public boolean keyPressedInTitle(int keyCode, int scanCode, int modifiers) {
		if (Screen.isPaste(keyCode)) {
			this.field_238749_v_.insertClipboardText();
			return true;
		}
		return super.keyPressedInTitle(keyCode, scanCode, modifiers);
	}
	
	
	/**
	 * @return A deep copy of the book's pages
	 */
	@Override  // From IGhostBook
	public List<String> pagesAsList() {
		// As of 1.16.1, unsigned books just use plain strings for book pages so we don't need to remove
		// any JSON weirdness
		return new ArrayList<>(this.bookPages);
	}
	
	
	@Override  // From IGhostBook
	public void insertText(String text) {
		if (this.bookGettingSigned) {
			// Put the text into the title
			this.field_238749_v_.putText(text);
			return;
		}
		// Put the text into the page
		this.field_238748_u_.putText(text);
	}
	
	
	@Override  // From IGhostBook
	public void bookChanged(boolean setModifiedFlag) {
		if (setModifiedFlag) this.bookIsModified = true;
		this.cachedPage = -1;
		
		// field_238748_u_ is the new TextInputUtil. If we don't move the cursor, the game can crash because
		// text in it doesn't match what's in the current bookPages String
		this.field_238748_u_.moveCursorToEnd();
		
		// This is some kind of new display update/refresh function
		// It must be called every time the book's content changes
		this.func_238751_C_();
	}
	
	
	@Override  // From IGhostBook
	public boolean isBookBeingSigned() {
		return this.bookGettingSigned;
	}
	
	
	@Override  // From IGhostBook
	public void insertNewPage(int atPageNum, String pageText) {
		// Idiot proofing
		if (atPageNum >= SharedConstants.MAX_BOOK_PAGES){
			Ghostwriter.LOG.error("Cannot insert a page at index " + atPageNum + ". It would make the book too long");
			return;
		}
		
		// Add blank pages if necessary to prevent IndexOutOfBoundsException
		while(this.bookPages.size() < atPageNum) this.bookPages.add("");
		
		this.bookPages.add(atPageNum, pageText);
		this.bookChanged(true);
	}
	
	
	@Override  // From IGhostBook
	public void setPageText(int pageNum, String pageText) {
		// Idiot proofing
		if (pageNum < 0 || pageNum > this.bookPages.size() - 1){
			Ghostwriter.LOG.error("Couldn't set text on page " + pageNum + " because it doesn't exist");
			return;
		}
		
		this.bookPages.set(pageNum, pageText);
		this.bookChanged(true);
	}
	
	
	@Override  // From IGhostBook
	public String getPageText(int pageNum) {
		// Idiot proofing
		if (pageNum < 0 || pageNum > this.bookPages.size() - 1) return "";
		
		return this.bookPages.get(pageNum);
	}
	
	
	@Override  // From IGhostBook
	public void removePage(int pageNum) {
		// Prevent IndexOutOfBoundsException
		if (pageNum > this.bookPages.size() - 1){
			Ghostwriter.LOG.error("Can't remove page " + pageNum + ". It doesn't exist");
			return;
		}
		
		this.bookPages.remove(pageNum);
		// Add a blank page if the book is empty
		if (this.bookPages.size() == 0) this.bookPages.add("");
		this.bookChanged(true);
	}
	
	
	@Override  // From IGhostBook
	public void replaceBookPages(List<String> newPages) {
		this.bookPages.clear();
		this.bookPages.addAll(newPages);
		if (this.bookPages.isEmpty()) this.bookPages.add("");
		this.bookChanged(true);
	}
	
	
	@Override  // From IGhostBook
	public void setBookTitle(String title) {
		this.bookTitle = title;
		this.field_238749_v_.moveCursorToEnd();  // field_238749_v_ is titleInput
	}
	
	
	@Override  // From IGhostBook
	public Button addGhostButton(Button button) {
		return this.addButton(button);
	}
	
	
	@Override  // From IGhostBook
	public int getCurrPage() {
		return this.currPage;
	}
	
	
	@Override  // From IGhostBook
	public void setCurrPage(int pageNum) {
		// Idiot proofing
		if (pageNum < 0 || pageNum > this.bookPages.size() - 1){
			Ghostwriter.LOG.error("Couldn't move to page " + pageNum + ". It doesn't exist");
			pageNum = 0;
		}
		
		this.currPage = pageNum;
		this.bookChanged(false);
	}
	
	@Override  // From IGhostBook
	public int getBookPageCount() {
		return this.getPageCount();
	}
	
	
	@Override  // From IGhostBook
	public void updateVanillaButtons() {
		super.updateButtons();
	}
}
