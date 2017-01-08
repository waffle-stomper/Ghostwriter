package wafflestomper.ghostwriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;

public class GuiSaveAs extends GuiScreen implements GuiYesNoCallback{
	
	private List<File> listItems;
	private GuiSaveAs.ScrollList scrollList;
	public int slotSelected = -1;
	private GuiTextField filenameField;
	private boolean directoryDirty = false;
	
	private GuiGhostwriterBook parentGui;
	private Clipboard tempClipboard = new Clipboard();
	
	private FileHandler fileHandler;
	private String displayPath = "";
	
	private static final int BUTTONWIDTH = 60;
	private static final int BUTTONHEIGHT = 20;
	
	private static final int BTN_SAVE = 0;
	private static final int BTN_CANCEL = 1;
	
	private GuiButton btnSave;
	private GuiButton btnCancel;
	
	private String bookTitle = "";
	private String bookAuthor = "";
	private List<String> bookPages;
	

	public GuiSaveAs(GuiGhostwriterBook _parentGui, String _title, String _author, List<String> _pages){
		this.parentGui = _parentGui;
		this.fileHandler = new FileHandler(this.tempClipboard);
		this.fileHandler.currentPath = this.fileHandler.getSavePath();
        this.displayPath = this.fileHandler.currentPath.getAbsolutePath();
		this.bookTitle = _title;
		this.bookAuthor = _author;
		this.bookPages = _pages;
	}
	
	
	public void initGui(){
		Keyboard.enableRepeatEvents(true);
        this.buttonList.add(btnSave = new GuiButton(BTN_SAVE, this.width-(BUTTONWIDTH+5), this.height-50, BUTTONWIDTH, BUTTONHEIGHT, "Save"));
        this.buttonList.add(btnCancel = new GuiButton(BTN_CANCEL, this.width-(BUTTONWIDTH+5), this.height-25, BUTTONWIDTH, BUTTONHEIGHT, "Cancel"));
        //Add buttons for each non-empty drive letter
        int rootNum = 100;
        List<File> roots = this.fileHandler.getValidRoots();
        for (File root : roots){
        	this.buttonList.add(new GuiButton(rootNum, 5, 35 + 21*(rootNum-100), 50, 20, root.getAbsolutePath()));
        	rootNum++;
        }
        populateFileList();
        this.scrollList = new ScrollList();
        this.scrollList.registerScrollButtons(4, 5);
        this.filenameField = new GuiTextField(0, this.fontRendererObj, this.width/2-125, this.height-32, 250, 20);
        this.filenameField.setMaxStringLength(100);
        String ftitle = this.bookTitle.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9\\.]", "");
		String fauthor = this.bookAuthor.trim().replaceAll(" ", ".").replaceAll("[^a-zA-Z0-9\\.]", "");
		String defaultFilename = ftitle + "_" + fauthor + "_" + this.fileHandler.getUTC() + ".ghb";
		this.filenameField.setText(defaultFilename);
	}
	
	
	private boolean isFilenameValid(){
		String fn = this.filenameField.getText();
		if (!fn.equals("")){
			return true;
		}
		return false;
	}
	
	
	public void drawScreen(int par1, int par2, float par3){
		this.displayPath = this.fileHandler.currentPath.getAbsolutePath();
		this.btnSave.enabled = this.isFilenameValid();
		populateFileList();
		this.scrollList.drawScreen(par1, par2, par3);
		super.drawScreen(par1, par2, par3);
		this.drawCenteredString(this.fontRendererObj, BookUtilities.truncateStringPixels(this.displayPath,"...", 200, true), this.width / 2, 20, 0xDDDDDD);
		this.filenameField.drawTextBox();
	}
	
	
	public void updateScreen(){
        this.filenameField.updateCursorCounter();
    }
	
	
	private void populateFileList(){
		this.listItems = this.fileHandler.listFiles(this.fileHandler.currentPath, this.directoryDirty);
		this.directoryDirty = false;
	}
	
	
	private void goBackToParentGui(){
		this.mc.displayGuiScreen(this.parentGui);
	}
	
	
    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char typedChar, int keyCode){
        if (keyCode == Keyboard.KEY_ESCAPE){
        	goBackToParentGui();
        }
        this.filenameField.textboxKeyTyped(typedChar, keyCode);
    }
    
    
    public void confirmClicked(boolean result, int id)
    {
    	if (id == 1000){
    		if (result == true){
    			saveBook();
    		}
    		else{
    			this.mc.displayGuiScreen(this);
    		}
    	}
    }
    
    
    private File getSavePath(){
    	return(new File(this.fileHandler.currentPath, this.filenameField.getText()));
    }
    
    
    private void saveBook(){
		if (this.fileHandler.saveBookToGHBFile(this.bookTitle, this.bookAuthor, this.bookPages, getSavePath())){
			this.mc.displayGuiScreen(GuiSaveAs.this.parentGui);
		}
    }
    
    
    protected void actionPerformed(GuiButton buttonPressed){
    	if (!buttonPressed.enabled){return;}
    	
    	switch (buttonPressed.id){
    		case BTN_SAVE:
    			if (getSavePath().exists()){
    				String ynmessage = "Are you sure you wish to overwrite";
    				this.mc.displayGuiScreen(new GuiYesNo(this, ynmessage, this.filenameField.getText() + "?", "Yes", "No", 1000));
    			}
    			else{
    				saveBook();
    			}
    			break;
    		case BTN_CANCEL:
    			Minecraft.getMinecraft().displayGuiScreen(GuiSaveAs.this.parentGui);
    			break;
    		default:
    			break;
    	}
    	
    	//Handle the drive letter buttons
    	if (buttonPressed.id >= 100){
    		this.fileHandler.currentPath = new File(buttonPressed.displayString);
    	}
    }
    
    
    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.scrollList.handleMouseInput();
    }
    
    
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.filenameField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    
    class ScrollList extends GuiSlot{
    	
    	
    	private static final int SLOT_HEIGHT = 12;
    	
        public ScrollList(){
        	super(GuiSaveAs.this.mc, GuiSaveAs.this.width, GuiSaveAs.this.height, 32, GuiSaveAs.this.height - 64, SLOT_HEIGHT);
        }

        
        protected int getPaddedSize(){
        	int scrollHeight = GuiSaveAs.this.height - 96;
        	int minSlots = (int)Math.ceil(scrollHeight/SLOT_HEIGHT);
        	
        	if (GuiSaveAs.this.listItems.size() >= minSlots){
        		//The extra slot is for the parent directory item (..)
        		return GuiSaveAs.this.listItems.size() + 1;
        	}
        	else{
        		return minSlots;
        	}
        }
        
        
        protected int getSize(){
            return getPaddedSize();
        }
        

        /**
         * The element in the slot that was clicked, boolean for whether it was double clicked or not
         */
        protected void elementClicked(int slotClicked, boolean doubleClicked, int clickXPos, int clickYPos){
        	this.setShowSelectionBox(true);
            if (doubleClicked){
            	if (slotClicked == 0){
            		//Go up to the parent directory
                	GuiSaveAs.this.fileHandler.navigateUp();
                	GuiSaveAs.this.slotSelected = -1;
                	this.setShowSelectionBox(false);
                	return;
                }
                else if (slotClicked <= GuiSaveAs.this.listItems.size()){
                	File itemClicked = GuiSaveAs.this.listItems.get(slotClicked-1);
                	if (itemClicked.isDirectory()){
                		//Go into the clicked directory
                		GuiSaveAs.this.fileHandler.currentPath = itemClicked;
                		GuiSaveAs.this.slotSelected = -1;
                		this.setShowSelectionBox(false);
                		return;
                	}
                }
            }
            else if (slotClicked > 0 && slotClicked <= GuiSaveAs.this.listItems.size()){
            	//A file or directory has been single-clicked
            	File selectedFile = GuiSaveAs.this.listItems.get(slotClicked-1);
            	if (selectedFile.isFile() && !isSelected(slotClicked)){
            		GuiSaveAs.this.filenameField.setText(selectedFile.getName());
            	}
            }
            else{
            	//This is called when an empty slot is selected
            	//It probably doesn't need to do anything
        	}
            GuiSaveAs.this.slotSelected = slotClicked;
        }
        

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int pos){
            return pos == GuiSaveAs.this.slotSelected;
        }
        

        /**
         * Return the height of the content being scrolled
         * This is used to determine the scrollable area. It doesn't affect the actual slot height
         */
        protected int getContentHeight(){
            return getPaddedSize() * SLOT_HEIGHT;
        }

        
        protected void drawBackground(){
            GuiSaveAs.this.drawDefaultBackground();
        }

        
        protected void drawSlot(int slotNum, int slotX, int slotY, int four__UNKNOWN_USE__, int mouseX, int mouseY){
        	List<File> list= GuiSaveAs.this.listItems;
        	//Empty padding slots at the bottom
        	if (slotNum > list.size()){return;}
        	
        	String s = "";
        	int color = 0xFFFFFF;
        	
        	if (slotNum == 0){
        		s = "..";
        		color = 0x00FF00;
        	}
        	else{
        		s = BookUtilities.truncateStringPixels(list.get(slotNum-1).getName(), "...", 200, false);
        		if (!list.get(slotNum-1).exists()){
        			color = 0x333333;
        			GuiSaveAs.this.directoryDirty = true;
        		}
        		else if (list.get(slotNum-1).isFile()){
    				color = 0xFF0000;
    			}
    			else if (list.get(slotNum-1).isDirectory()){
    				color = 0x00FF00;
    			}
        	}
            GuiSaveAs.this.drawString(GuiSaveAs.this.fontRendererObj, s, slotX + 2, slotY + 1, color);
        }
    }
}
