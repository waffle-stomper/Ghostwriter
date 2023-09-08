package wafflestomper.ghostwriter.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import wafflestomper.ghostwriter.Ghostwriter;
import wafflestomper.ghostwriter.gui.GhostLayer;
import wafflestomper.ghostwriter.gui.GuiUtils;
import wafflestomper.ghostwriter.gui.widget.FileSelectionList;
import wafflestomper.ghostwriter.gui.widget.SelectableFilenameField;
import wafflestomper.ghostwriter.utilities.Clipboard;
import wafflestomper.ghostwriter.utilities.FileHandler;
import wafflestomper.ghostwriter.utilities.Printer;
import wafflestomper.ghostwriter.utilities.SharedConstants;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;


public class GhostwriterFileBrowserScreen extends Screen {
	
	private FileSelectionList fileSelectionList;
	private SelectableFilenameField filenameField;
	
	private boolean directoryDirty = false;
	private File cachedPath;
	private File selectedFile = null;
	
	private final GhostLayer parent;
	private final Clipboard TEMP_CLIPBOARD = new Clipboard();
	private static final Printer PRINTER = new Printer();
	
	private final FileHandler FILE_HANDLER;
	
	private static final int BORDER_HEIGHT = 2;
	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_HEIGHT = 20;
	private static final int SLOT_HEIGHT = 12;
	private static final int DISPLAY_PATH_WIDTH = 200;
	
	private Button btnAutoReload;
	private Button btnLoad;
	private Button btnSave;
	private Button btnEditExtension;
	
	private boolean initialized = false;
	private boolean enableLoading = false;
	private String hoveringText;
	
	
	public GhostwriterFileBrowserScreen(GhostLayer parent) {
		super(Component.translatable("Ghostwriter File Browser"));
		this.parent = parent;
		this.FILE_HANDLER = new FileHandler(this.TEMP_CLIPBOARD);
		if (Ghostwriter.currentPath == null) {
			Ghostwriter.currentPath = this.FILE_HANDLER.getSavePath();
		}
		this.FILE_HANDLER.currentPath = Ghostwriter.currentPath;
		if (parent.bookIsEditable) {
			this.enableLoading = true;
		}
	}
	
	
	private void driveButtonClicked(File root) {
		this.FILE_HANDLER.currentPath = root;
	}
	
	
	/**
	 * Called when the user clicks a button on the overwrite confirmation screen
	 *
	 * @param confirmed true if they clicked the left button, false if they clicked the one on the right
	 */
	public void saveCallback(boolean confirmed) {
		if (confirmed) {
			// Do the save, then kick back to the book
			this.saveBook();
		} else {
			this.goBackToParentGui();
		}
	}
	
	
	private void loadClicked(boolean autoReload) {
		// The book should already be in the temp clipboard at this point, right?
		if (!this.TEMP_CLIPBOARD.bookInClipboard) {
			PRINTER.gamePrint(Printer.RED + "Error loading book - no book in temp clipboard!");
		} else if (!this.parent.bookIsEditable) {
			PRINTER.gamePrint(Printer.RED + "Error loading book - you can't load to a non-writable book!");
		}
		
		if (autoReload) {
			parent.enableAutoReload(this.FILE_HANDLER.lastLoadedBook, this.TEMP_CLIPBOARD);
			parent.clipboardToBook(this.TEMP_CLIPBOARD);
		} else {
			Ghostwriter.GLOBAL_CLIPBOARD.clone(this.TEMP_CLIPBOARD);
			new Printer().gamePrint(Printer.GRAY + "Book loaded into clipboard");
		}
		
		this.goBackToParentGui();
	}
	
	
	private void saveClicked() {
		// Template for this found in the delete button on MultiplayerScreen
		File save_path = new File(this.FILE_HANDLER.currentPath, this.filenameField.getValue());
		if (save_path.exists()) {
			Component overwriteText = Component.translatable("Are you sure you wish to overwrite this file?");
			Component filenameText = Component.translatable(this.filenameField.getValue());
			Component s1 = Component.translatable("Yes");
			Component s2 = Component.translatable("Cancel");
			Ghostwriter.currentPath = this.FILE_HANDLER.currentPath;
			assert this.minecraft != null;
			this.minecraft.setScreen(new ConfirmScreen(this::saveCallback, overwriteText, filenameText, s1, s2));
		} else {
			this.saveBook();
		}
	}
	
	
	public void init() {
		super.init();
		if (this.minecraft == null) return;

		// I can't remember what this did but setSendRepeatsToGui() is no longer a valid function...
		// this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		
		if (!this.initialized) {
			this.fileSelectionList = new FileSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, SLOT_HEIGHT);
			
			this.filenameField = new SelectableFilenameField(this.minecraft.font, this.width / 2 - 125,
					this.height - BORDER_HEIGHT - BUTTON_HEIGHT, 250, BUTTON_HEIGHT,
					Component.translatable("filename"));
			this.filenameField.setMaxLength(100);
			// Add default filename to filenameField
			String file_title = this.parent.getBookTitle();
			String file_author = this.parent.getBookAuthor();
			
			file_title = file_title.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9.]", "");
			file_author = file_author.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9.]", "");
			String defaultFilename = file_title + "_" + file_author + "_" + this.FILE_HANDLER.getUTC() + ".ghb";
			this.filenameField.setValue(defaultFilename);
			
			// Focus on the filename field and highlight the filename (without the extension)
			this.filenameField.setFocused(true);
			this.filenameField.highlightFilename();
			
			// Prevent re-initializing element on resize
			this.initialized = true;
		}
		
		// For some reason the button list is cleared when the window is resized
		int mainButtonsY = this.height - BORDER_HEIGHT - BUTTON_HEIGHT;
		int loadX = this.width / 2 - 127;

		this.btnLoad = this.addRenderableWidget(
				GuiUtils.buttonFactory(
						loadX, mainButtonsY, BUTTON_WIDTH, BUTTON_HEIGHT, "Load",
						(pressedButton) -> this.loadClicked(false)
				)
		);
		
		int autoReloadX = loadX + BUTTON_WIDTH;
		this.btnAutoReload = this.addRenderableWidget(
				GuiUtils.buttonFactory(
						autoReloadX, mainButtonsY, BUTTON_WIDTH, BUTTON_HEIGHT, "AutoReload",
						(pressedButton) -> this.loadClicked(true)
				)
		);
		
		int saveX = autoReloadX + BUTTON_WIDTH + 7;
		this.btnSave = this.addRenderableWidget(
				GuiUtils.buttonFactory(
						saveX, mainButtonsY, BUTTON_WIDTH, BUTTON_HEIGHT, "Save",
						(pressedButton) -> this.saveClicked()
				)
		);
		
		int cancelX = this.width / 2 + 127 - BUTTON_WIDTH;
		this.addRenderableWidget(
				GuiUtils.buttonFactory(
						cancelX, mainButtonsY, BUTTON_WIDTH, BUTTON_HEIGHT, "Cancel",
						(pressedButton) -> goBackToParentGui()
				)
		);
		
		//Add buttons for each non-empty drive letter
		int rootNum = 100;
		List<File> roots = this.FILE_HANDLER.getValidRoots();
		for (File root : roots) {
			this.addRenderableWidget(
					GuiUtils.buttonFactory(
							5, 35 + 21 * (rootNum - 100), 50, 20, root.getAbsolutePath(),
							(pressedButton) -> this.driveButtonClicked(root)
					)
			);
			rootNum++;
		}
		
		this.children.add(this.fileSelectionList);
		this.populateFileList();
		
		// Make sure everything is in place in case the window was resized
		this.fileSelectionList.updateSize(this.width, this.height, 32, this.height - 64);
		this.filenameField.x = this.width / 2 - 125;
		this.filenameField.y = this.height - BORDER_HEIGHT * 2 - BUTTON_HEIGHT * 2;
		
		// Add button for enabling file extension editing
		this.btnEditExtension = this.addRenderableWidget(
				GuiUtils.buttonFactory(
						this.filenameField.x + this.filenameField.getWidth() + 3,
						this.filenameField.y,
						25,
						this.filenameField.getHeight(),
						"EXT",
						(pressedButton) -> {
							this.filenameField.toggleExtensionModifications();
							this.updateButtons();
						}
				)
		);
		
		this.updateButtons();
	}
	
	
	public void setDirectoryDirty() {
		this.directoryDirty = true;
	}
	
	
	public void navigateUp() {
		this.FILE_HANDLER.navigateUp();
		this.directoryDirty = true;
	}
	
	
	private boolean isFilenameValid() {
		String fn = this.filenameField.getValue();
		return !fn.isEmpty();
	}
	
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.btnSave.active = this.isFilenameValid();
		this.hoveringText = null;
		this.fileSelectionList.render(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		
		// Render the current directory, truncating the left side if it becomes too long
		String displayPath = this.FILE_HANDLER.currentPath.getAbsolutePath();
		int displayPathSize = this.font.width(displayPath);
		if (displayPathSize > DISPLAY_PATH_WIDTH) {
			int allowedSize = DISPLAY_PATH_WIDTH - this.font.width("...");
			String reversed = new StringBuilder(displayPath).reverse().toString();
			// func_238361_b_() is trimStringToWidth()
			reversed = this.font.getSplitter().plainHeadByWidth(reversed, allowedSize, Style.EMPTY);
			displayPath = "..." + new StringBuilder(reversed).reverse();
		}
		guiGraphics.drawCenteredString(this.font, displayPath, this.width / 2, 20, 0xDDDDDD);
		
		this.filenameField.render(guiGraphics, mouseX, mouseY, partialTicks);
		
		// Draw tooltip if the path is hovered
		if (mouseX >= this.width / 2 - 100 && mouseX <= this.width / 2 + 100 && mouseY >= 20 && mouseY <= 27) {
			guiGraphics.renderTooltip(this.font, Component.translatable(displayPath), 0, 0);
		}
		// Draw any other hover text
		else if (this.hoveringText != null) {
			guiGraphics.renderTooltip(this.font, Component.translatable(this.hoveringText), mouseX, mouseY);
		}
	}
	
	
	public void setSelectedSlot(FileSelectionList.Entry entry) {
		this.fileSelectionList.setSelected(entry);
		if (this.enableLoading && entry instanceof FileSelectionList.PathItemEntry p) {
			if (p.path == this.selectedFile) {
				return;
			} else if (p.path.isFile()) {
				this.FILE_HANDLER.loadBook(p.path);
				this.selectedFile = p.path;
				this.filenameField.setValue(p.path.getName());
				this.updateButtons();
				return;
			}
		}
		this.TEMP_CLIPBOARD.clearBook();
		this.selectedFile = null;
		this.updateButtons();
	}
	
	
	@Override
	public void tick() {
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
	
	
	private void populateFileList() {
		this.fileSelectionList.updateFileList(this.FILE_HANDLER.listFiles(this.FILE_HANDLER.currentPath, this.directoryDirty));
		this.directoryDirty = false;
		this.cachedPath = this.FILE_HANDLER.currentPath;
	}
	
	
	private void goBackToParentGui() {
		if (this.minecraft == null) return;
		Ghostwriter.currentPath = this.FILE_HANDLER.currentPath;
		this.minecraft.setScreen(this.parent.screen);
	}
	
	
	public void updateButtons() {
		if (this.enableLoading) {
			this.btnLoad.active = this.TEMP_CLIPBOARD.bookInClipboard;
			this.btnAutoReload.active = this.TEMP_CLIPBOARD.bookInClipboard;
		} else {
			this.btnAutoReload.active = false;
			this.btnAutoReload.visible = false;
			this.btnLoad.active = false;
			this.btnLoad.visible = false;
		}
		
		if (this.filenameField.allowExtensionModifications) {
			this.btnEditExtension.setMessage(Component.translatable("EXT"));
		} else {
			this.btnEditExtension.setMessage(Component.translatable("§mEXT"));
		}
	}
	
	
	/**
	 * Handles everything, which eventually filters down to charTyped if the key is printable
	 */
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// Patch Esc function to kicking back to parent screen rather than exiting completely
		if (keyCode == SharedConstants.KEY_ESC) {
			goBackToParentGui();
			return true;
		}
		
		if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
		
		this.TEMP_CLIPBOARD.clearBook();
		this.updateButtons();
		return this.filenameField.keyPressed(keyCode, scanCode, modifiers);
	}
	
	
	/**
	 * Handles ordinary (printable) characters
	 */
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		if (super.charTyped(p_charTyped_1_, p_charTyped_2_)) {
			return true;
		} else {
			return this.filenameField.charTyped(p_charTyped_1_, p_charTyped_2_);
		}
	}
	
	
	private File getSavePath() {
		return (new File(this.FILE_HANDLER.currentPath, this.filenameField.getValue()));
	}
	
	
	/**
	 * This actually calls the save function and kicks back to the book screen
	 */
	private void saveBook() {
		this.parent.saveBookToDisk(this.getSavePath());
		this.goBackToParentGui();
	}
	
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		this.filenameField.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	
	public void navigateInto(File path) {
		if (path.isFile()) {
			if (this.TEMP_CLIPBOARD.bookInClipboard) {
				// Handles double-click
				this.loadClicked(false);
			}
		} else {
			this.FILE_HANDLER.currentPath = path;
		}
	}
	
	
	public void setHoveringText(String text) {
		this.hoveringText = text;
	}
}
