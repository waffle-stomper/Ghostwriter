package wafflestomper.ghostwriter;

//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//import net.minecraft.client.gui.screen.Screen;


// public class GuiFileSelection extends Screen{
//	
//	private List<File> listItems;
//	private GuiFileSelection.ScrollList scrollList;
//	public int slotSelected = -1;
//	private boolean directoryDirty = false;
//	
//	private GuiGhostwriterBook parentGui;
//	private Clipboard tempClipboard = new Clipboard();
//	
//	private FileHandler fileHandler;
//	private String displayPath = "";
//	
//	private static final int BUTTONWIDTH = 60;
//	private static final int BUTTONHEIGHT = 20;
//	
//	private static final int BTN_LOAD = 0;
//	private static final int BTN_CANCEL = 1;
//	
//	private GuiButton btnLoad;
//	private GuiButton btnCancel;
//	
//	private String previewTitle = "";
//	private String previewAuthor = "";
//	private String previewPage = "";
//	
//	private File lastLoadedPath = null;
//	
//	private boolean autoReloadBook = false;
//
//
//	public GuiFileSelection(GuiGhostwriterBook _parentGui){
//		this.parentGui = _parentGui;
//		this.fileHandler = new FileHandler(this.tempClipboard);
//	}
//	
//	
//	public GuiFileSelection(GuiGhostwriterBook _parentGui, boolean _autoReload){
//		this.autoReloadBook = _autoReload;
//		this.parentGui = _parentGui;
//		this.fileHandler = new FileHandler(this.tempClipboard);
//	}
//	
//	
//	public void initGui(){
//        this.fileHandler.currentPath = this.fileHandler.getDefaultPath();
//        this.displayPath = this.fileHandler.currentPath.getAbsolutePath();
//        this.buttonList.add(btnLoad = new GuiButton(BTN_LOAD, this.width-(BUTTONWIDTH+5), this.height-50, BUTTONWIDTH, BUTTONHEIGHT, "Load"));
//        this.buttonList.add(btnCancel = new GuiButton(BTN_CANCEL, this.width-(BUTTONWIDTH+5), this.height-25, BUTTONWIDTH, BUTTONHEIGHT, "Cancel"));
//        //Add buttons for each non-empty drive letter
//        int rootNum = 100;
//        List<File> roots = this.fileHandler.getValidRoots();
//        for (File root : roots){
//        	this.buttonList.add(new GuiButton(rootNum, 5, 35 + 21*(rootNum-100), 50, 20, root.getAbsolutePath()));
//        	rootNum++;
//        }
//        populateFileList();
//        this.scrollList = new ScrollList();
//        this.scrollList.registerScrollButtons(4, 5);
//	}
//	
//	
//	public void drawScreen(int par1, int par2, float par3){
//		this.displayPath = this.fileHandler.currentPath.getAbsolutePath();
//		this.btnLoad.enabled = this.tempClipboard.bookInClipboard;
//		populateFileList();
//		this.scrollList.drawScreen(par1, par2, par3);
//		super.drawScreen(par1, par2, par3);
//		this.drawCenteredString(this.fontRenderer, BookUtilities.truncateStringPixels(this.displayPath,"...", 200, true), this.width / 2, 20, 0xDDDDDD);
//		if (!this.previewAuthor.equals("") || !this.previewTitle.equals("") || !this.previewPage.equals("")){
//			this.drawCenteredString(this.fontRenderer, "Author: " + this.previewAuthor, this.width / 2, this.height-50, 0xFFFFFF);
//			this.drawCenteredString(this.fontRenderer, "Title: " + this.previewTitle, this.width / 2, this.height-40, 0xFFFFFF);
//			this.drawCenteredString(this.fontRenderer, "Page 1: " + this.previewPage, this.width / 2, this.height-30, 0xFFFFFF);
//		}
//    }
//	
//	
//	private void populateFileList(){
//		this.listItems = this.fileHandler.listFiles(this.fileHandler.currentPath, this.directoryDirty);
//		this.directoryDirty = false;
//	}
//	
//	
//	private void loadPreview(File file){
//		if(this.fileHandler.loadBook(file) && this.tempClipboard.pages.size() > 0){
//			this.previewAuthor = this.tempClipboard.author;
//			this.previewTitle = this.tempClipboard.title;
//			this.previewPage = BookUtilities.truncateStringPixels(this.tempClipboard.pages.get(0).replaceAll("\n", " "), "...", 200, false);
//		}
//		else{
//			this.previewTitle = "";
//    		this.previewAuthor = "";
//    		this.previewPage = "";
//    		this.tempClipboard.clearBook();
//		}
//	}
//	
//	
//	private void goBackToParentGui(){
//		this.mc.displayGuiScreen(this.parentGui);
//	}
//	
//	
//    /**
//     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
//     */
//    protected void keyTyped(char par1, int par2){
//        if (par2 == Keyboard.KEY_ESCAPE){
//        	goBackToParentGui();
//        }
//    }
//    
//    
//    protected void actionPerformed(GuiButton buttonPressed){
//    	if (!buttonPressed.enabled){return;}
//    	
//    	switch (buttonPressed.id){
//    		case BTN_LOAD:
//    			if (this.tempClipboard.bookInClipboard){
//    				if (this.autoReloadBook == false){
//	        			this.parentGui.setClipboard(GuiFileSelection.this.tempClipboard);
//	        			new Printer().gamePrint(Printer.GRAY + "Book loaded into clipboard");
//    				}
//    				else{
//        				GuiFileSelection.this.parentGui.setupAutoReload(GuiFileSelection.this.tempClipboard, lastLoadedPath);
//        				new Printer().gamePrint(Printer.DARK_AQUA + "Loaded book and continuing to monitor...");
//        			}
//        			Minecraft.getMinecraft().displayGuiScreen(GuiFileSelection.this.parentGui);
//        		}
//    			break;
//    		case BTN_CANCEL:
//    			Minecraft.getMinecraft().displayGuiScreen(GuiFileSelection.this.parentGui);
//    			break;
//    		default:
//    			break;
//    	}
//    	
//    	//Handle the drive letter buttons
//    	if (buttonPressed.id >= 100){
//    		this.fileHandler.currentPath = new File(buttonPressed.displayString);
//    	}
//    }
//    
//    
//    /**
//     * Handles mouse input.
//     */
//    public void handleMouseInput() throws IOException
//    {
//        super.handleMouseInput();
//        this.scrollList.handleMouseInput();
//    }
//    
//    
//    class ScrollList extends GuiSlot{
//    	
//    	
//    	private static final int SLOT_HEIGHT = 12;
//    	
//        public ScrollList(){
//        	super(GuiFileSelection.this.mc, GuiFileSelection.this.width, GuiFileSelection.this.height, 32, GuiFileSelection.this.height - 64, SLOT_HEIGHT);
//        }
//
//        
//        protected int getPaddedSize(){
//        	int scrollHeight = GuiFileSelection.this.height - 96;
//        	int minSlots = (int)Math.ceil(scrollHeight/SLOT_HEIGHT);
//        	
//        	if (GuiFileSelection.this.listItems.size() >= minSlots){
//        		//The extra slot is for the parent directory item (..)
//        		return GuiFileSelection.this.listItems.size() + 1;
//        	}
//        	else{
//        		return minSlots;
//        	}
//        }
//        
//        
//        protected int getSize(){
//            return getPaddedSize();
//        }
//        
//
//        /**
//         * The element in the slot that was clicked, boolean for whether it was double clicked or not
//         */
//        protected void elementClicked(int slotClicked, boolean doubleClicked, int clickXPos, int clickYPos){
//        	this.setShowSelectionBox(true);
//            if (doubleClicked){
//            	if (slotClicked == 0){
//            		//Go up to the parent directory
//                	GuiFileSelection.this.fileHandler.navigateUp();
//                	GuiFileSelection.this.slotSelected = -1;
//                	this.setShowSelectionBox(false);
//                	return;
//                }
//                else if (slotClicked <= GuiFileSelection.this.listItems.size()){
//                	File itemClicked = GuiFileSelection.this.listItems.get(slotClicked-1);
//                	if (itemClicked.isDirectory()){
//                		//Go into the clicked directory
//                		GuiFileSelection.this.fileHandler.currentPath = itemClicked;
//                		GuiFileSelection.this.slotSelected = -1;
//                		this.setShowSelectionBox(false);
//                		return;
//                	}
//                	else{
//                		//We've double-clicked on a file
//                		//It should already be loaded in if it's a real file
//                		if (GuiFileSelection.this.tempClipboard.bookInClipboard){
//                			if (GuiFileSelection.this.autoReloadBook == false){
//                				GuiFileSelection.this.parentGui.setClipboard(GuiFileSelection.this.tempClipboard);
//                				new Printer().gamePrint(Printer.GRAY + "Book loaded into clipboard");
//                			}
//                			else{
//                				GuiFileSelection.this.parentGui.setupAutoReload(GuiFileSelection.this.tempClipboard, lastLoadedPath);
//                				new Printer().gamePrint(Printer.DARK_AQUA + "Loaded book and continuing to monitor...");
//                			}
//                			Minecraft.getMinecraft().displayGuiScreen(GuiFileSelection.this.parentGui);
//                		}
//                	}
//                }
//            }
//            else if (slotClicked > 0 && slotClicked <= GuiFileSelection.this.listItems.size()){
//            	//A file or directory has been single-clicked
//            	File selectedFile = GuiFileSelection.this.listItems.get(slotClicked-1);
//            	if (selectedFile.isFile() && !isSelected(slotClicked)){
//            		GuiFileSelection.this.loadPreview(selectedFile);
//            		GuiFileSelection.this.lastLoadedPath = selectedFile;
//            	}
//            }
//            else{
//            	// No file selected
//        		GuiFileSelection.this.previewTitle = "";
//        		GuiFileSelection.this.previewAuthor = "";
//        		GuiFileSelection.this.previewPage = "";
//        		GuiFileSelection.this.tempClipboard.clearBook();
//        		GuiFileSelection.this.lastLoadedPath = null;
//        	}
//            GuiFileSelection.this.slotSelected = slotClicked;
//        }
//        
//
//        /**
//         * Returns true if the element passed in is currently selected
//         */
//        protected boolean isSelected(int pos){
//            return pos == GuiFileSelection.this.slotSelected;
//        }
//        
//
//        /**
//         * Return the height of the content being scrolled
//         * This is used to determine the scrollable area. It doesn't affect the actual slot height
//         */
//        protected int getContentHeight(){
//            return getPaddedSize() * SLOT_HEIGHT;
//        }
//
//        
//        protected void drawBackground(){
//            GuiFileSelection.this.drawDefaultBackground();
//        }
//
//        
//        protected void drawSlot(int slotNum, int slotX, int slotY, int four__UNKNOWN_USE__, int mouseX, int mouseY, float p_192637_7_){
//        	List<File> list= GuiFileSelection.this.listItems;
//        	//Empty padding slots at the bottom
//        	if (slotNum > list.size()){return;}
//        	
//        	String s = "";
//        	int color = 0xFFFFFF;
//        	
//        	if (slotNum == 0){
//        		s = "..";
//        		color = 0x00FF00;
//        	}
//        	else{
//        		s = BookUtilities.truncateStringPixels(list.get(slotNum-1).getName(), "...", 200, false);
//        		if (!list.get(slotNum-1).exists()){
//        			color = 0x333333;
//        			GuiFileSelection.this.directoryDirty = true;
//        		}
//        		else if (list.get(slotNum-1).isFile()){
//    				color = 0xFF0000;
//    			}
//    			else if (list.get(slotNum-1).isDirectory()){
//    				color = 0x00FF00;
//    			}
//        	}
//            GuiFileSelection.this.drawString(GuiFileSelection.this.fontRenderer, s, slotX + 2, slotY + 1, color);
//        }
//
//    }
//}
