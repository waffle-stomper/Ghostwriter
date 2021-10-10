package wafflestomper.ghostwriter.gui.screen;

import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import wafflestomper.ghostwriter.utilities.SharedConstants;

public class GhostwriterSignedPreviewScreen extends BookViewScreen {
	private final GhostwriterEditBookScreen parent;
	
	public GhostwriterSignedPreviewScreen(GhostwriterEditBookScreen parent) {
		this.parent = parent;
		this.bookAccess = new PreviewBookInfo(this.parent);
		this.currentPage = this.parent.currentPage;
	}
	
	@Override
	public void createMenuControls() {
		this.addRenderableWidget(new Button(this.width / 2 - 100, 196, 200, 20,
				new TextComponent("Back to editor"), (p_214161_1_) -> {
			if (this.minecraft == null) return;
			this.minecraft.setScreen(this.parent);
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
			this.minecraft.setScreen(this.parent);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	
	public static class PreviewBookInfo implements BookViewScreen.BookAccess {
		private final GhostwriterEditBookScreen PARENT;
		
		public PreviewBookInfo(GhostwriterEditBookScreen parent) {
			this.PARENT = parent;
		}
		
		/**
		 * Returns the size of the book
		 */
		public int getPageCount() {
			return this.PARENT.pages.size();
		}
		
		// TODO: I have no idea if this is right
		public FormattedText getPageRaw(int pageNum) {
			return FormattedText.of(this.PARENT.pages.get(pageNum));
		}
	}
}
