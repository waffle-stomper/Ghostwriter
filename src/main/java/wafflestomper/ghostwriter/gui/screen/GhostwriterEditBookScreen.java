package wafflestomper.ghostwriter.gui.screen;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import wafflestomper.ghostwriter.*;
import wafflestomper.ghostwriter.gui.GhostLayer;
import wafflestomper.ghostwriter.gui.IGhostBook;
import wafflestomper.ghostwriter.utilities.BookUtilities;
import wafflestomper.ghostwriter.utilities.SharedConstants;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;


@OnlyIn(Dist.CLIENT)
public class GhostwriterEditBookScreen extends BookEditScreen implements IGhostBook {
	
	protected final GhostLayer ghostLayer;
	private int currPageLineCount = 0;
	private long lastPageLineCountUpdate = 0;
	
	
	public GhostwriterEditBookScreen(Player editingPlayer, ItemStack book, InteractionHand hand) {
		super(editingPlayer, book, hand);
		this.ghostLayer = new GhostLayer(this, this, true);
		
		// Swap out the title input util for one that allows longer titles and updates the title in GhostLayer
		// WrittenBookItem.validBookTagContents declares the book invalid if the title is over 32 characters
		titleEdit = new TextFieldHelper(
				() -> this.title,
				(p_238772_1_) -> {
					this.title = p_238772_1_;
					this.ghostLayer.setBookTitle(this.title);
				},
				this::getClipboard,  // getClipboard
				this::setClipboard,  // setClipboard
				(p_238771_0_) -> p_238771_0_.length() <= SharedConstants.BOOK_TITLE_MAX_LEN);
	}
	
	
	@Override  // From BookEditScreen
	public void init() {
		super.init();
		this.ghostLayer.init();
		
		// Move standard buttons
		this.signButton.setWidth(SharedConstants.LARGE_BUTTON_WIDTH);
		this.finalizeButton.setWidth(SharedConstants.LARGE_BUTTON_WIDTH);
		this.doneButton.setWidth(SharedConstants.LARGE_BUTTON_WIDTH);
		this.cancelButton.setWidth(SharedConstants.LARGE_BUTTON_WIDTH);
		this.signButton.x = SharedConstants.BUTTON_SIDE_OFFSET;
		this.finalizeButton.x = SharedConstants.BUTTON_SIDE_OFFSET;
		this.doneButton.x = SharedConstants.BUTTON_SIDE_OFFSET;
		this.cancelButton.x = SharedConstants.BUTTON_SIDE_OFFSET;
		this.signButton.y = 120;
		this.finalizeButton.y = 120;
		this.doneButton.y = 145;
		this.cancelButton.y = 145;
		
		this.updateVanillaButtons();
	}
	
	
	@Override  // From BookEditScreen
	public void tick() {
		this.ghostLayer.tick();
		super.tick();
	}
	
	
	@Override // From BookEditScreen
	public void updateButtonVisibility() {
		this.ghostLayer.updateButtons();
	}
	
	
	/**
	 * Counts the lines on the current page, returning a cached version most of the time
	 * <p>
	 * Unfortunately the vanilla code makes it borderline impossible to extract the word-wrapped lines that
	 * BookEditScreen uses in render(), so we have to split it ourselves
	 * <p>
	 * Note that this will need to be updated periodically so that it matches the code in BookEditScreen
	 */
	private int getCurrPageLineCount() {
		if (System.currentTimeMillis() - lastPageLineCountUpdate >= 250) {
			this.currPageLineCount = BookUtilities.splitIntoPages(this.getCurrentPageText(), 0).get(0).lines.length;
			this.lastPageLineCountUpdate = System.currentTimeMillis();
		}
		return this.currPageLineCount;
	}
	
	
	/**
	 * Visual patches:
	 * - Warn when the title is over the vanilla 15-character limit
	 * - Warn when the page is over the vanilla 14 line limit
	 * - Warn when the page is over the multiplayer 256-character limit
	 * - Add a high contrast background behind extended length titles
	 */
	@Override  // From BookEditScreen
	@ParametersAreNonnullByDefault
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);
		
		// Render long title and warning (if necessary)
		if (this.isSigning && this.title.length() > 15) {
			// Show the title length
			String textLen = "Title length: " + this.title.length();
			// params are PoseStack, x, y, color
			// this was drawString() in the old money
			this.font.draw(poseStack, textLen, 169, 20, 0xFF3333);
			
			// Add extra background width amd re-render the title because the new background covers up the vanilla title
			String textTitle = this.title;
			textTitle += (this.frameTick / 6 % 2 == 0 ? ChatFormatting.BLACK : ChatFormatting.GRAY) + "_";
			int bookLeftSide = (this.width - 192) / 2;
			int titleWidth = this.font.width(textTitle);
			int titleMinX = bookLeftSide + 36 + (114 - titleWidth) / 2;
			int titleMaxX = titleMinX + titleWidth;
			// color for the fill() method is MSB->LSB: alpha, r, g, b, (each 8 bits)
			GuiComponent.fill(poseStack, titleMinX - 5, 48, titleMaxX + 5, 60, 0xFFFFF9EC);
			this.font.draw(poseStack, textTitle, (float) (titleMinX), 50.0F, 0);
			
			// Show the long title warning
			String s = "Warning: the vanilla client restricts titles to 15 characters. " +
					"Set longer titles at your own risk";
			Component lengthWarning = Component.translatable(s);
			// params are text, x, y, width, color
			this.font.drawWordWrap(poseStack, lengthWarning, 153, 116, 114, 0xFF3333);
		}
		
		// Add warnings about character and line limits
		// Things get weird over 256 characters, so we don't bother showing the line warning in that case
		if (!this.isSigning) {
			String warning = "";
			if (this.getCurrentPageText().length() > 256) {
				warning = "Over 256 char limit!";
			} else if (this.getCurrPageLineCount() > SharedConstants.BOOK_MAX_LINES) {
				warning = "Over " + SharedConstants.BOOK_MAX_LINES + " line limit!";
			}
			
			if (!warning.isEmpty()) {
				this.font.draw(poseStack, "Warning:", 5, 176, 0xFF3333);
				this.font.draw(poseStack, warning, 5, 185, 0xFF3333);
			}
		}
	}
	
	
	/**
	 * Patch to enable pasting from the clipboard into the title of a book when signing it
	 */
	@Override  // From BookEditScreen
	public boolean titleKeyPressed(int keyCode, int scanCode, int modifiers) {
		if (Screen.isPaste(keyCode)) {
			this.titleEdit.paste();
			return true;
		}
		return super.titleKeyPressed(keyCode, scanCode, modifiers);
	}
	
	
	/**
	 * @return A deep copy of the book's pages
	 */
	@Override  // From IGhostBook
	public List<String> pagesAsList() {
		// As of 1.16.1, unsigned books just use plain strings for book pages, so we don't need to remove
		// any JSON weirdness
		return new ArrayList<>(this.pages);
	}


	@Override  // From IGhostBook
	public void bookChanged(boolean setModifiedFlag) {
		if (setModifiedFlag) this.isModified = true;
		this.clearDisplayCache();  // TODO: is this all we need to do now?
	}


	@Override  // From IGhostBook
	public void addFormattingCode(String formattingCode) {
		// Get a reference to the appropriate editor (title or page, depending on whether we're signing)
		TextFieldHelper editor = this.isSigning ? this.titleEdit : this.pageEdit;

		// If text is selected, we need to temporarily remove the selection so the text isn't replaced by the
		// formatting character
		int cursorPos = editor.getCursorPos();
		int selectionPos = editor.getSelectionPos();
		if (cursorPos != selectionPos){
			// Note that when text is selected right to left, selectionPos will be lower than cursorPos.
			// The second argument to this function controls whether the selectionPos should be preserved. When
			// set to false, the selectionPos will be set to the new cursorPos
			editor.setCursorPos(Math.min(cursorPos, selectionPos), false);
		}

		// Next we add the formatting character at the start of the selection (which will also be to the left
		// of the cursor in the case that no text is selected)
		editor.insertText(formattingCode);

		// If text was selected, we need to re-select the text (optionally adding a reset sequence at the end
		// of the selection if the character we're inserting isn't a reset char
		if (cursorPos != selectionPos){
			int extraCharCount = 2;

			// Optionally add reset character at the end of selection
			if (!formattingCode.equals("§r")){
				editor.setCursorPos(Math.max(cursorPos, selectionPos) + extraCharCount, false);
				editor.insertText("§r");
				extraCharCount = 4;
			}

			// Re-select the text (note that it's two characters longer, and we have to deal with the case where
			// the text was selected right-to-left
			if (cursorPos < selectionPos){
				editor.setSelectionRange(cursorPos, selectionPos + extraCharCount);
			}
			else{
				editor.setSelectionRange(selectionPos, cursorPos + extraCharCount);
			}
		}

		// Mark the book as changed, which will also update the display
		this.bookChanged(true);
	}

	
	@Override  // From IGhostBook
	public boolean isBookBeingSigned() {
		return this.isSigning;
	}
	
	
	@Override  // From IGhostBook
	public void insertNewPage(int atPageNum, String pageText) {
		// Idiot proofing
		if (atPageNum >= SharedConstants.MAX_BOOK_PAGES){
			Ghostwriter.LOG.error("Cannot insert a page at index " + atPageNum + ". It would make the book too long");
			return;
		}
		
		// Add blank pages if necessary to prevent IndexOutOfBoundsException
		while(this.pages.size() < atPageNum) this.pages.add("");
		
		this.pages.add(atPageNum, pageText);
		this.bookChanged(true);
	}
	
	
	@Override  // From IGhostBook
	public void setPageText(int pageNum, String pageText) {
		// Idiot proofing
		if (pageNum < 0 || pageNum > this.pages.size() - 1){
			Ghostwriter.LOG.error("Couldn't set text on page " + pageNum + " because it doesn't exist");
			return;
		}
		
		this.pages.set(pageNum, pageText);
		this.bookChanged(true);
	}
	
	
	@Override  // From IGhostBook
	public String getPageText(int pageNum) {
		// Idiot proofing
		if (pageNum < 0 || pageNum > this.pages.size() - 1) return "";
		
		return this.pages.get(pageNum);
	}
	
	
	@Override  // From IGhostBook
	public void removePage(int pageNum) {
		// Prevent IndexOutOfBoundsException
		if (pageNum > this.pages.size() - 1){
			Ghostwriter.LOG.error("Can't remove page " + pageNum + ". It doesn't exist");
			return;
		}
		
		this.pages.remove(pageNum);
		// Add a blank page if the book is empty
		if (this.pages.isEmpty()) this.pages.add("");
		this.bookChanged(true);
	}
	
	
	@Override  // From IGhostBook
	public void replaceBookPages(List<String> newPages) {
		this.pages.clear();
		this.pages.addAll(newPages);
		if (this.pages.isEmpty()) this.pages.add("");
		this.bookChanged(true);
	}
	
	
	@Override  // From IGhostBook
	public void setBookTitle(String title) {
		this.title = title;
		this.titleEdit.setCursorToEnd();
	}
	
	
	@Override  // From IGhostBook
	public Button addGhostButton(Button button) {
		return this.addRenderableWidget(button);
	}
	
	
	@Override  // From IGhostBook
	public int getCurrPage() {
		return this.currentPage;
	}
	
	
	@Override  // From IGhostBook
	public void setCurrPage(int pageNum) {
		// Idiot proofing
		if (pageNum < 0 || pageNum > this.pages.size() - 1){
			Ghostwriter.LOG.error("Couldn't move to page " + pageNum + ". It doesn't exist");
			pageNum = 0;
		}
		
		this.currentPage = pageNum;
		this.bookChanged(false);
	}
	
	@Override  // From IGhostBook
	public int getBookPageCount() {
		return this.getNumPages();
	}
	
	
	@Override  // From IGhostBook
	public void updateVanillaButtons() {
		super.updateButtonVisibility();
	}
}
