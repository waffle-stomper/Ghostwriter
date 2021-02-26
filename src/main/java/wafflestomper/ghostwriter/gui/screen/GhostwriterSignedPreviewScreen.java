package wafflestomper.ghostwriter.gui.screen;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import wafflestomper.ghostwriter.utilities.SharedConstants;

public class GhostwriterSignedPreviewScreen extends ReadBookScreen {
	private final GhostwriterEditBookScreen parent;
	
	public GhostwriterSignedPreviewScreen(GhostwriterEditBookScreen parent) {
		this.parent = parent;
		this.bookInfo = new PreviewBookInfo(this.parent);
		this.currPage = this.parent.currPage;
	}
	
	@Override
	public void addDoneButton() {
		this.addButton(new Button(this.width / 2 - 100, 196, 200, 20,
				new StringTextComponent("Back to editor"), (p_214161_1_) -> {
			if (this.minecraft == null) return;
			this.minecraft.displayGuiScreen(this.parent);
		}));
	}
	
	@Override
	public void tick() {
		long lastFileMod = this.parent.ghostLayer.autoReloadLastModified;
		this.parent.tick(); // Is something going to break if we do this?
		if (lastFileMod != this.parent.ghostLayer.autoReloadLastModified) {
			// File has been reloaded
			this.cachedPage = -1;
		}
	}
	
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// Patch Esc function so it kicks back to parent screen rather than exiting completely
		if (keyCode == SharedConstants.KEY_ESC && this.minecraft != null) {
			this.minecraft.displayGuiScreen(this.parent);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	
	@MethodsReturnNonnullByDefault
	public static class PreviewBookInfo implements ReadBookScreen.IBookInfo {
		private final GhostwriterEditBookScreen PARENT;
		
		public PreviewBookInfo(GhostwriterEditBookScreen parent) {
			this.PARENT = parent;
		}
		
		/**
		 * Returns the size of the book
		 */
		public int getPageCount() {
			return this.PARENT.bookPages.size();
		}
		
		// getPageContent
		public ITextProperties func_230456_a_(int pageNum) {
			return ITextProperties.func_240652_a_(this.PARENT.bookPages.get(pageNum));
		}
	}
}
