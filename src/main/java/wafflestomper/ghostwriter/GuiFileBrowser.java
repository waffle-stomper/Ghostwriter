package wafflestomper.ghostwriter;

import java.io.File;
import java.util.List;

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
import wafflestomper.ghostwriter.modified_mc_files.ReadBookScreenMod;

public class GuiFileBrowser extends Screen{ 
	// TODO: Add overwrite warning when saving over another file
	// TODO: Add file hashes. Maybe color coded for easy recognition?
	// TODO: Add autoreload button in here
	
	private FileSelectionList fileSelectionList;
	public int slotSelected = -1;
	private TextFieldWidget filenameField;
	private boolean directoryDirty = false;
	private File cachedPath;
	private File selectedFile = null;
	
	private Screen parentGui; // TODO: genericize somehow?
	private Clipboard tempClipboard = new Clipboard();
	private static Printer printer = new Printer();
	
	private FileHandler fileHandler;
	private String displayPath = "";
	
	private static final int BUTTONWIDTH = 60;
	private static final int BUTTONHEIGHT = 20;
	
	private Button btnAutoReload;
	private Button btnLoad;
	private Button btnSave;
	
	private boolean initialized = false;
	
	private static final int SLOT_HEIGHT = 12;
	

	public GuiFileBrowser(Screen _parentGui){
		super(new StringTextComponent("Title I think?")); // TODO
		this.parentGui = _parentGui;
		this.fileHandler = new FileHandler(this.tempClipboard);
		this.fileHandler.currentPath = this.fileHandler.getSavePath();
	}
	
	
	private void driveButtonClicked(File root) {
		this.fileHandler.currentPath = root;
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
			this.minecraft.displayGuiScreen(this);
		}
	}
	
	
	private void loadClicked(boolean autoReload) {
		// The book should already be in the temp clipboard at this point, right?
		if (!this.tempClipboard.bookInClipboard) {
			printer.gamePrint(Printer.RED + "Error loading book - no book in temp clipboard!");
		}
		else if (this.parentGui instanceof GhostwriterEditBookScreen) {
			GhostwriterEditBookScreen parent = (GhostwriterEditBookScreen)this.parentGui;
			if (autoReload) {
				parent.enableAutoReload(this.fileHandler.lastLoadedBook, this.tempClipboard);
				parent.clipboardToBook(this.tempClipboard);
			}
			else {
				parent.setClipboard(this.tempClipboard);
				new Printer().gamePrint(Printer.GRAY + "Book loaded into clipboard");
			}
		}
		else {
			printer.gamePrint(Printer.RED + "Error loading book - you can't load to a non-writable book!");
		}
		this.minecraft.displayGuiScreen(this.parentGui);
	}
	
	
	private void saveClicked() {
		// Template for this found in the delete button on MultiplayerScreen
		File savepath = new File(this.fileHandler.currentPath, this.filenameField.getText());
		if (savepath.exists()){
			ITextComponent itextcomponent = new StringTextComponent("Are you sure you wish to overwrite this file?");
			ITextComponent itextcomponent1 = new StringTextComponent(this.filenameField.getText());
			String s1 = "Yes";
			String s2 = "Cancel";
			this.minecraft.displayGuiScreen(new ConfirmScreen(this::saveCallback, itextcomponent, itextcomponent1, s1, s2));
		}
		else {
			this.saveBook();
		}
	}
	
	
	public void init(){
		System.out.println("Init() called");
		super.init();
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		if (this.initialized == false) {
			this.fileSelectionList = new FileSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, SLOT_HEIGHT);
			
			this.filenameField = new TextFieldWidget(this.minecraft.fontRenderer, this.width/2-125, this.height-32, 250, 20, "filename");
			this.filenameField.setMaxStringLength(100);
			// Add default filename to filenameField
			String ftitle = "";
			String fauthor = "";
			if (this.parentGui instanceof GhostwriterEditBookScreen) {
				GhostwriterEditBookScreen parentBook = (GhostwriterEditBookScreen)this.parentGui;
				ftitle = parentBook.getBookTitle();
			}
			if (this.parentGui instanceof ReadBookScreenMod) { //  TODO: Change this placeholder to the Ghostwriter version
				ItemStack itemstack = this.minecraft.player.getHeldItem(Hand.MAIN_HAND); // TODO: Off hand?
			    if (itemstack.getItem() == Items.WRITTEN_BOOK){
			    	CompoundNBT compoundnbt = itemstack.getTag();
			        fauthor = compoundnbt.getString("author"); // TODO: Error handling
			        ftitle = compoundnbt.getString("title");
				}
			}
			
			ftitle = ftitle.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9\\.]", "");
			fauthor = fauthor.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9\\.]", "");
			String defaultFilename = ftitle + "_" + fauthor + "_" + this.fileHandler.getUTC() + ".ghb";
			this.filenameField.setText(defaultFilename);
			
			this.initialized = true;
		}
		
		this.btnAutoReload = this.addButton(new Button(this.width-BUTTONWIDTH-5, this.height-85, BUTTONWIDTH, BUTTONHEIGHT, "AutoReload", (pressedButton) ->{
			this.loadClicked(true);
		}));
		this.btnLoad = this.addButton(new Button(this.width-(BUTTONWIDTH+5), this.height-65, BUTTONWIDTH, BUTTONHEIGHT, "Load", (pressedButton) ->{
			this.loadClicked(false);
		}));
		this.btnSave = this.addButton(new Button(this.width-(BUTTONWIDTH+5), this.height-45, BUTTONWIDTH, BUTTONHEIGHT, "Save", (pressedButton) ->{
			this.saveClicked();
		}));
		this.addButton(new Button(this.width-(BUTTONWIDTH+5), this.height-25, BUTTONWIDTH, BUTTONHEIGHT, "Cancel", (pressedButton) ->{
			goBackToParentGui();
		}));
		
		//Add buttons for each non-empty drive letter
		int rootNum = 100;
		List<File> roots = this.fileHandler.getValidRoots();
		for (File root : roots){
			this.addButton(new Button(5, 35 + 21*(rootNum-100), 50, 20, root.getAbsolutePath(), (pressedButton)->{
				this.driveButtonClicked(root); // TODO: Test this to make sure it actually works
			}));
			rootNum++;
			//
		}
		
		// TODO: Do resizing here
		System.out.println("RESIZE ME!");
		this.children.add(this.fileSelectionList);
		this.populateFileList();
	}
	
	
	public void setDirectoryDirty() {
		this.directoryDirty = true;
	}
	
	
	public void navigateUp() {
		this.fileHandler.navigateUp();
		this.directoryDirty = true;
		this.displayPath = this.fileHandler.currentPath.getAbsolutePath();
	}
	
	
	private boolean isFilenameValid(){
		String fn = this.filenameField.getText();
		if (!fn.equals("")){
			return true;
		}
		return false;
	}
	
	/**
	 * replaces drawScreen?
	 */
	@Override
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) { 
		this.displayPath = this.fileHandler.currentPath.getAbsolutePath();
		this.btnSave.active = this.isFilenameValid();
		this.fileSelectionList.render(p_render_1_, p_render_2_, p_render_3_);
		super.render(p_render_1_, p_render_2_, p_render_3_);
		this.drawCenteredString(this.minecraft.fontRenderer, BookUtilities.truncateStringPixels(this.displayPath,"...", 200, true), this.width / 2, 20, 0xDDDDDD);
		this.filenameField.render(p_render_1_, p_render_2_, p_render_3_);
	}
	
	
	public void setSelectedSlot(FileSelectionList.Entry entry) {
		this.fileSelectionList.setSelected(entry);
		if (entry instanceof FileSelectionList.PathItemEntry) {
			FileSelectionList.PathItemEntry p = (FileSelectionList.PathItemEntry)entry;
			if (p.path == this.selectedFile) {
				return;
			}
			else if (p.path.isFile()) {
				this.btnLoad.active = this.fileHandler.loadBook(p.path);
				this.btnAutoReload.active = this.btnLoad.active;
				this.selectedFile = p.path;
				this.filenameField.setText(p.path.getName());
				return;
			}
		}
		this.btnLoad.active = false;
		this.selectedFile = null;
	}
	
	
	@Override
	public void tick(){
		this.filenameField.tick();
		this.btnSave.active = this.isFilenameValid();
		if (this.fileHandler.currentPath != this.cachedPath) {
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
		this.fileSelectionList.updateFileList(this.fileHandler.listFiles(this.fileHandler.currentPath, this.directoryDirty));
		this.directoryDirty = false;
		this.cachedPath = this.fileHandler.currentPath;
	}
	
	
	private void goBackToParentGui(){
		this.minecraft.displayGuiScreen(this.parentGui);
	}
	
	
	/**
	 * Handles special characters
	 */
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
			return true;
		}
		// TODO: Escape should kick back to the parent GUI
		// TODO: Add ctrl+s to save, maybe ctrl+d to load?
		return this.filenameField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}
	
	/**
	 * Handles ordinary characters
	 */
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		if (super.charTyped(p_charTyped_1_, p_charTyped_2_)) {
			return true;
		} 
		else {
			return this.filenameField.charTyped(p_charTyped_1_, p_charTyped_2_);
		}    	
	}
	
	
	private File getSavePath(){
		return(new File(this.fileHandler.currentPath, this.filenameField.getText()));
	}
	
	
	/**
	 * This actually calls the save function and kicks back to the book screen
	 */
	private void saveBook(){
		if (this.parentGui instanceof GhostwriterEditBookScreen) {
			GhostwriterEditBookScreen parent = (GhostwriterEditBookScreen)this.parentGui;
			parent.saveBookToDisk(this.getSavePath());
		}
		else {
			printer.gamePrint(Printer.RED + "Saving not implemented for the parent screen!");
		}
		this.minecraft.displayGuiScreen(this.parentGui);
	}
	
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton){
		this.filenameField.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}


	public void navigateInto(File path) {
		if (path.isFile()) {
			if (this.tempClipboard.bookInClipboard) {
				// Handles double-click
				this.loadClicked(false);
			}
		}
		else {
			this.fileHandler.currentPath = path;
		}
	}
	
}
