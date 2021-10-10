package wafflestomper.ghostwriter.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import wafflestomper.ghostwriter.utilities.SharedConstants;

public class SelectableFilenameField extends EditBox {
	private final StringSplitter CHARACTER_MANAGER;
	public boolean allowExtensionModifications = false;
	private long lastClickTime = 0;
	
	public SelectableFilenameField(Font fontRenderer, int x, int y, int width, int height, Component text) {
		super(fontRenderer, x, y, width, height, text);
		this.CHARACTER_MANAGER = fontRenderer.getSplitter();
	}
	
	
	public void toggleExtensionModifications() {
		this.allowExtensionModifications = !this.allowExtensionModifications;
		// Ensure cursor is in a valid position
		this.setCursorPosition(Math.min(this.getCursorPosition(), this.getEditableLength()));
		// Cancel any text selection
		this.setHighlightPos(this.getCursorPosition());
	}
	
	
	/**
	 * @return Extension for the current filename (or a blank string if it doesn't have an extension)
	 */
	public String getExtension() {
		int dotPos = this.getValue().lastIndexOf('.');
		if (dotPos == -1) return "";
		return this.getValue().substring(dotPos);
	}
	
	/**
	 * @return Length of the filename without the extension (or dot separator)
	 */
	public int getEditableLength() {
		if (this.allowExtensionModifications) {
			return this.getValue().length();
		}
		return this.getValue().length() - this.getExtension().length();
	}
	
	
	/**
	 * Selects the filename
	 */
	public void highlightFilename() {
		this.moveCursorToStart();
		this.setHighlightPos(this.getEditableLength());
	}
	
	
	/**
	 * This field in EditBox was causing the bug where single clicking after using the shift key
	 * to type upper case text would erroneously select text
	 */
	public void updateShiftKeyStatus() {
		this.shiftPressed = Screen.hasShiftDown();
	}
	
	
	/**
	 * Prevents deleting from or moving into the 'uneditable' region
	 * and adds Up and Down keys as Home and End aliases
	 */
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.canConsumeInput()) return false;
		
		this.updateShiftKeyStatus();
		
		// Handle select all
		if (Screen.isSelectAll(keyCode)) {
			this.highlightFilename();
			return true;
		}
		
		switch (keyCode) {
			
			case SharedConstants.KEY_DEL:
			case SharedConstants.KEY_RIGHT:
				if (this.getCursorPosition() >= this.getEditableLength()) return false;
				break;
			
			case SharedConstants.KEY_DOWN:
			case SharedConstants.KEY_END:
				this.setCursorPosition(this.getEditableLength());
				return true;
			
			case SharedConstants.KEY_UP:
				this.moveCursorToStart();
				return true;
		}
		
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	
	/**
	 * Selects the editable part of the filename on double-click and cancels text selection on single click
	 * <p>
	 * This also fixes a bug where single clicking will cause text to be selected
	 * Steps to reproduce (with this method disabled):
	 * 1) Hold shift and type S, then release shift
	 * 2) Click somewhere in the text field.
	 * 3) Note that text is erroneously selected
	 * Moving the cursor with the arrow keys seems to fix it
	 */
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// This should fix the bug described above
		this.updateShiftKeyStatus();
		
		if (super.mouseClicked(mouseX, mouseY, button)) {
			// Mouse was clicked within this component
			if (Util.getMillis() - this.lastClickTime < 250L) {
				// Double click - highlight the filename without the extension
				this.highlightFilename();
				this.lastClickTime = 0; // Prevent triple-click
			} else {
				this.lastClickTime = Util.getMillis();
				// Ensure the cursor is within the range they're allowed to edit
				int editableLength = this.getEditableLength();
				if (this.getCursorPosition() > editableLength) {
					this.setCursorPosition(editableLength);
				}
				// Collapse the selection onto the cursor position (no selection)
				// This shouldn't be necessary now that we're updating the shift key status field, but
				// it's relatively harmless performance-wise
				this.setHighlightPos(this.getCursorPosition());
			}
			return true;
		}
		return false;
	}
	
	
	/**
	 * Handles text selection
	 * Co-ords are relative to the game window
	 * Called by mouseDragged() when the left mouse button is held
	 *
	 * @param mouseX Current horizontal position
	 * @param mouseY Current vertical position
	 * @param dragX  How far the mouse was dragged horizontally (usually < 1 pixel)
	 * @param dragY  How far the mouse was dragged vertically (usually < 1 pixel)
	 */
	protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
		// Bail out if the drag didn't start in this component
		if (!this.isFocused()) return;
		
		// Calculate mouse X relative to the left side of this component
		int relativeX = (int) mouseX - this.x;
		
		// If the mouse is dragged beyond the left side, we cap it at 0
		int dragCharPos = 0;
		
		// If the mouse is somewhere to the right of the start of this component, we calculate how many characters
		// from the filename could fit to the left of the cursor
		if (relativeX > 0) {
			// was trimStringToWidth in Forge
			dragCharPos = this.CHARACTER_MANAGER.plainHeadByWidth(this.getValue(), relativeX, Style.EMPTY).length();
		}
		
		// Ensure that only editable content is selected
		dragCharPos = Math.min(dragCharPos, this.getEditableLength());
		
		this.setHighlightPos(dragCharPos);
	}
}
