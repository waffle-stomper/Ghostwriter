package wafflestomper.ghostwriter;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Util;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

public class SelectableFilenameField extends TextFieldWidget {
	private final CharacterManager CHARACTER_MANAGER;
	private long lastClickTime = 0;
	
	public SelectableFilenameField(FontRenderer p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, ITextComponent p_i232260_6_, CharacterManager characterManager) {
		super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
		this.CHARACTER_MANAGER = characterManager;
	}
	
	
	/**
	 * Selects the filename without the extension
	 */
	public void highlightFilename(){
		this.setCursorPositionZero();
		this.setSelectionPos(this.getText().length()-4);
	}
	
	
	/**
	 * Cancels text selection on mouse click
	 * This fixes a bug where clicking will cause text to be selected
	 * Steps to reproduce (with this method disabled):
	 * 1) Hold shift and type S, then release shift
	 * 2) Click somewhere in the text field.
	 * 3) Note that text is erroneously selected
	 * Moving the cursor with the arrow keys seems to fix it
	 */
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)){
			// Mouse was clicked within this component.
			if (Util.milliTime() - this.lastClickTime < 250L) {
				// Double click - highlight the filename without the extension
				this.highlightFilename();
				this.lastClickTime = 0; // Prevent triple-click
			}
			else {
				this.lastClickTime = Util.milliTime();
				this.setSelectionPos(this.getCursorPosition());
			}
			return true;
		}
		return false;
	}
	
	
	/**
	 * Handles text selection
	 * Co-ords are relative to the game window
	 * called by mouseDragged() when it's the left button
	 * @param mouseX Current horizontal position
	 * @param mouseY Current vertical position
	 * @param dragX How far the mouse was dragged horizontally (usually < 1 pixel)
	 * @param dragY How far the mouse was dragged vertically (usually < 1 pixel)
	 */
	protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
		// TODO: Keep an eye on Widget.onDrag() because future versions will probably implement code like this
		// Bail out if the drag didn't start in this component
		if (!this.isFocused()) return;
		
		int relativeX = (int)mouseX - this.x;
		int dragCharPos = 0;
		if (relativeX > 0){
			//func_238361_b_ is trimStringToWidth;
			dragCharPos = this.CHARACTER_MANAGER.func_238361_b_(this.getText(), relativeX, Style.EMPTY).length();
		}
		
		this.setSelectionPos(dragCharPos);
	}
}
