package wafflestomper.ghostwriter;

import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public class GhostwriterSignedPreviewScreen extends ReadBookScreen {
	private final GhostwriterEditBookScreen PARENT;
	
	public GhostwriterSignedPreviewScreen(GhostwriterEditBookScreen parent){
		this.PARENT = parent;
		this.bookInfo = new PreviewBookInfo(this.PARENT);
		this.currPage = this.PARENT.currPage;
	}
	
	@Override
	public void addDoneButton(){
		this.addButton(new Button(this.width / 2 - 100, 196, 200, 20,
				new StringTextComponent("Back to editor"), (p_214161_1_) -> {
			if (this.minecraft == null) return;
			this.minecraft.displayGuiScreen(this.PARENT);
		}));
	}
	
	@Override
	public void tick() {
		long lastFileMod = this.PARENT.autoReloadLastModified;
		this.PARENT.tick(); // Is something going to break if we do this?
		if (lastFileMod != this.PARENT.autoReloadLastModified){
			// File has been reloaded
			this.cachedPage = -1;
		}
	}
	
	// TODO: Add 'preview' text
	
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
