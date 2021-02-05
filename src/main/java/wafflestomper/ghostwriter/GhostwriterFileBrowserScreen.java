package wafflestomper.ghostwriter;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.io.File;
import java.util.List;

// TODO: Focus on the filename field with the name (and not extension) highlighted to make saving with a custom name
//       faster

public class GhostwriterFileBrowserScreen extends Screen{
	
	private FileSelectionList fileSelectionList;
	private TextFieldWidget filenameField;
	private boolean directoryDirty = false;
	private File cachedPath;
	private File selectedFile = null;
	
	private final Screen PARENT_GUI; // TODO: genericize somehow?
	private final Clipboard TEMP_CLIPBOARD = new Clipboard();
	private static final Printer PRINTER = new Printer();
	
	private final FileHandler FILE_HANDLER;
	private String displayPath = "";
	
	private static final int BORDER_HEIGHT = 2;
	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_HEIGHT = 20;
	private static final int SLOT_HEIGHT = 12;
	
	private Button btnAutoReload;
	private Button btnLoad;
	private Button btnSave;
	
	private boolean initialized = false;
	private boolean enableLoading = false;
	private String hoveringText;
	
	

	public GhostwriterFileBrowserScreen(Screen _parentGui){
		super(new StringTextComponent("Title I think?")); // TODO
		this.PARENT_GUI = _parentGui;
		this.FILE_HANDLER = new FileHandler(this.TEMP_CLIPBOARD);
		if (Ghostwriter.currentPath == null) {
			Ghostwriter.currentPath = this.FILE_HANDLER.getSavePath();
		}
		this.FILE_HANDLER.currentPath = Ghostwriter.currentPath;
		if (_parentGui instanceof GhostwriterEditBookScreen) {
			this.enableLoading = true;
		}
	}
	
	
	private void driveButtonClicked(File root) {
		this.FILE_HANDLER.currentPath = root;
	}
	
	
	/**
	 * Called when the user clicks a button on the overwrite confirmation screen
	 * @param confirmed true if they clicked the left button, false if they clicked the one on the right
	 */
	public void saveCallback(boolean confirmed){
		if (confirmed) {
			// Do the save, then kick back to the book
			saveBook();
		}
		else {
			this.goBackToParentGui();
		}
	}
	
	
	private void loadClicked(boolean autoReload) {
		// The book should already be in the temp clipboard at this point, right?
		if (!this.TEMP_CLIPBOARD.bookInClipboard) {
			PRINTER.gamePrint(Printer.RED + "Error loading book - no book in temp clipboard!");
		}
		else if (this.PARENT_GUI instanceof GhostwriterEditBookScreen) {
			GhostwriterEditBookScreen parent = (GhostwriterEditBookScreen)this.PARENT_GUI;
			if (autoReload) {
				parent.enableAutoReload(this.FILE_HANDLER.lastLoadedBook, this.TEMP_CLIPBOARD);
				parent.clipboardToBook(this.TEMP_CLIPBOARD);
			}
			else {
				parent.setClipboard(this.TEMP_CLIPBOARD);
				new Printer().gamePrint(Printer.GRAY + "Book loaded into clipboard");
			}
		}
		else {
			PRINTER.gamePrint(Printer.RED + "Error loading book - you can't load to a non-writable book!");
		}
		this.goBackToParentGui();
	}
	
	
	private void saveClicked() {
		// Template for this found in the delete button on MultiplayerScreen
		File savepath = new File(this.FILE_HANDLER.currentPath, this.filenameField.getText());
		if (savepath.exists()){
			ITextComponent itextcomponent = new StringTextComponent("Are you sure you wish to overwrite this file?");
			ITextComponent itextcomponent1 = new StringTextComponent(this.filenameField.getText());
			ITextComponent s1 = new StringTextComponent("Yes");
			ITextComponent s2 = new StringTextComponent("Cancel");
			Ghostwriter.currentPath = this.FILE_HANDLER.currentPath;
			assert this.minecraft != null;
			this.minecraft.displayGuiScreen(new ConfirmScreen(this::saveCallback, itextcomponent, itextcomponent1, s1, s2));
		}
		else {
			this.saveBook();
		}
	}
	
	
	public void init(){
		super.init();
		if (this.minecraft != null) this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		if (!this.initialized) {
			this.fileSelectionList = new FileSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, SLOT_HEIGHT);
			
			this.filenameField = new TextFieldWidget(this.minecraft.fontRenderer, this.width/2-125, this.height-BORDER_HEIGHT-BUTTON_HEIGHT, 250, BUTTON_HEIGHT, new StringTextComponent("filename"));
			this.filenameField.setMaxStringLength(100);
			// Add default filename to filenameField
			String ftitle = "";
			String fauthor = "";
			if (this.PARENT_GUI instanceof GhostwriterEditBookScreen) {
				GhostwriterEditBookScreen parentBook = (GhostwriterEditBookScreen)this.PARENT_GUI;
				ftitle = parentBook.getBookTitle();
			}
			if (this.PARENT_GUI instanceof GhostwriterReadBookScreen && this.minecraft.player != null) {
				ItemStack itemstack = this.minecraft.player.getHeldItem(Hand.MAIN_HAND); // TODO: Off hand?
				if (itemstack.getItem() == Items.WRITTEN_BOOK){
					CompoundNBT compoundnbt = itemstack.getTag();
					if (compoundnbt != null) {
						fauthor = compoundnbt.getString("author");
						ftitle = compoundnbt.getString("title");
					}
				}
			}
			
			ftitle = ftitle.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9\\.]", "");
			fauthor = fauthor.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9\\.]", "");
			String defaultFilename = ftitle + "_" + fauthor + "_" + this.FILE_HANDLER.getUTC() + ".ghb";
			this.filenameField.setText(defaultFilename);
			this.initialized = true;
		}
		
		// For some reason the button list is cleared when the window is resized
		int mainButtonsY = this.height-BORDER_HEIGHT-BUTTON_HEIGHT;
		int loadX = this.width/2 -127;
		this.btnLoad = this.addButton(new Button(loadX, mainButtonsY, BUTTON_WIDTH, BUTTON_HEIGHT,
				new StringTextComponent("Load"), (pressedButton) -> this.loadClicked(false)));
		
		int autoReloadX = loadX + BUTTON_WIDTH;
		this.btnAutoReload = this.addButton(new Button(autoReloadX, mainButtonsY, BUTTON_WIDTH, BUTTON_HEIGHT,
				new StringTextComponent("AutoReload"), (pressedButton) -> this.loadClicked(true)));
		
		int saveX = autoReloadX + BUTTON_WIDTH + 7;
		this.btnSave = this.addButton(new Button(saveX, mainButtonsY, BUTTON_WIDTH, BUTTON_HEIGHT,
				new StringTextComponent("Save"), (pressedButton) -> this.saveClicked()));
		
		int cancelX = this.width/2+127 - BUTTON_WIDTH;
		this.addButton(new Button(cancelX, mainButtonsY, BUTTON_WIDTH, BUTTON_HEIGHT,
				new StringTextComponent("Cancel"), (pressedButton) -> goBackToParentGui()));
		
		//Add buttons for each non-empty drive letter
		int rootNum = 100;
		List<File> roots = this.FILE_HANDLER.getValidRoots();
		for (File root : roots){
			this.addButton(new Button(5, 35 + 21*(rootNum-100), 50, 20, new StringTextComponent(root.getAbsolutePath()), (pressedButton)->{
				this.driveButtonClicked(root); // TODO: Test this to make sure it actually works
			}));
			rootNum++;
		}
		
		this.children.add(this.fileSelectionList);
		this.populateFileList();
		
		// Make sure everything is in place in case the window was resized
		this.fileSelectionList.updateSize(this.width, this.height, 32, this.height - 64);
		this.filenameField.x = this.width/2-125;
		this.filenameField.y = this.height-BORDER_HEIGHT*2-BUTTON_HEIGHT*2;
		
		this.updateButtons();
	}
	
	
	public void setDirectoryDirty() {
		this.directoryDirty = true;
	}
	
	
	public void navigateUp() {
		this.FILE_HANDLER.navigateUp();
		this.directoryDirty = true;
		this.displayPath = this.FILE_HANDLER.currentPath.getAbsolutePath();
	}
	
	
	// TODO: This could probably use some more checks
	private boolean isFilenameValid(){
		String fn = this.filenameField.getText();
		return !fn.equals("");
	}
	
	// TODO: Check that this still works
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)  {
		this.displayPath = this.FILE_HANDLER.currentPath.getAbsolutePath();
		this.btnSave.active = this.isFilenameValid();
		this.hoveringText = null;
		this.fileSelectionList.render(matrixStack, mouseX, mouseY, partialTicks);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		if (this.minecraft == null) return;
		this.drawCenteredString(matrixStack, this.minecraft.fontRenderer, BookUtilities.truncateStringPixels(this.displayPath,"...", 200, true), this.width / 2, 20, 0xDDDDDD);
		this.filenameField.render(matrixStack, mouseX, mouseY, partialTicks);
		
		// Draw tooltip if the path is hovered
		if (mouseX >= this.width/2-100 && mouseX <= this.width/2+100 && mouseY >= 20 && mouseY <= 27) {
			this.renderTooltip(matrixStack, new StringTextComponent(this.displayPath), 0, 0);
		}
		else if (this.hoveringText != null) {
			this.renderTooltip(matrixStack, new StringTextComponent(this.hoveringText), mouseX, mouseY);
		}
	}
	
	
	public void setSelectedSlot(FileSelectionList.Entry entry) {
		this.fileSelectionList.setSelected(entry);
		if (this.enableLoading && entry instanceof FileSelectionList.PathItemEntry) {
			FileSelectionList.PathItemEntry p = (FileSelectionList.PathItemEntry)entry;
			if (p.path == this.selectedFile) {
				return;
			}
			else if (p.path.isFile()) {
				this.FILE_HANDLER.loadBook(p.path);
				this.selectedFile = p.path;
				this.filenameField.setText(p.path.getName());
				this.updateButtons();
				return;
			}
		}
		this.TEMP_CLIPBOARD.clearBook();
		this.selectedFile = null;
		this.updateButtons();
	}
	
	
	@Override
	public void tick(){
		this.filenameField.tick();
		this.btnSave.active = this.isFilenameValid();
		if (this.FILE_HANDLER.currentPath != this.cachedPath) {
			this.directoryDirty = true; // This probably isn't necessary - it forces a refresh
			this.populateFileList();
		}
		super.tick();
	}
	
	
	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
		this.filenameField.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
	}
	
	
	private void populateFileList(){
		this.fileSelectionList.updateFileList(this.FILE_HANDLER.listFiles(this.FILE_HANDLER.currentPath, this.directoryDirty));
		this.directoryDirty = false;
		this.cachedPath = this.FILE_HANDLER.currentPath;
	}
	
	
	private void goBackToParentGui(){
		Ghostwriter.currentPath = this.FILE_HANDLER.currentPath;
		assert this.minecraft != null;
		this.minecraft.displayGuiScreen(this.PARENT_GUI);
	}
	
	
	
	public void updateButtons() {
		if (this.enableLoading) {
			this.btnLoad.active = this.TEMP_CLIPBOARD.bookInClipboard;
			this.btnAutoReload.active = this.TEMP_CLIPBOARD.bookInClipboard;
		}
		else {
			this.btnAutoReload.active = false;
			this.btnAutoReload.visible = false;
			this.btnLoad.active = false;
			this.btnLoad.visible = false;
		}
	}
	
	/**
	 * Handles everything, which eventually filters down to charTyped if the key is printable
	 */
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
			return true;
		}
		// TODO: Escape should kick back to the parent GUI
		// TODO: Add ctrl+s to save, maybe ctrl+d to load?
		this.TEMP_CLIPBOARD.clearBook();
		this.updateButtons();
		return this.filenameField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}
	
	
	/**
	 * Handles ordinary (printable) characters
	 */
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		if (super.charTyped(p_charTyped_1_, p_charTyped_2_)) {
			return true;
		} 
		else {
			return this.filenameField.charTyped(p_charTyped_1_, p_charTyped_2_);
		} 	
	}
	
	
	private File getSavePath(){
		return(new File(this.FILE_HANDLER.currentPath, this.filenameField.getText()));
	}
	
	
	/**
	 * This actually calls the save function and kicks back to the book screen
	 */
	private void saveBook(){
		if (this.PARENT_GUI instanceof GhostwriterEditBookScreen) {
			GhostwriterEditBookScreen parent = (GhostwriterEditBookScreen)this.PARENT_GUI;
			parent.saveBookToDisk(this.getSavePath());
		}
		else if (this.PARENT_GUI instanceof GhostwriterLecternScreen){
			GhostwriterLecternScreen parent = (GhostwriterLecternScreen)this.PARENT_GUI;
			parent.saveBookToDisk(this.getSavePath());
		}
		else if (this.PARENT_GUI instanceof GhostwriterReadBookScreen) {
			GhostwriterReadBookScreen parent = (GhostwriterReadBookScreen)this.PARENT_GUI;
			parent.saveBookToDisk(this.getSavePath());
		}
		else {
			PRINTER.gamePrint(Printer.RED + "Saving not implemented for the parent screen!");
		}
		this.goBackToParentGui();
	}
	
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton){
		this.filenameField.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}


	public void navigateInto(File path) {
		if (path.isFile()) {
			if (this.TEMP_CLIPBOARD.bookInClipboard) {
				// Handles double-click
				this.loadClicked(false);
			}
		}
		else {
			this.FILE_HANDLER.currentPath = path;
		}
	}	
	
	
	public void setHoveringText(String text) {
		this.hoveringText = text;
	}
}
