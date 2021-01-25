package wafflestomper.ghostwriter;

import net.minecraft.client.gui.screen.LecternScreen;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GhostwriterLecternScreen extends LecternScreen {

    private Button buttonSelectPageA;
    private Button buttonSelectPageB;

    //Used for copying multiple pages at a time
    private int selectedPageA = -1;
    private int selectedPageB = -1;

    private Clipboard clipboard;
    private static final Printer printer = new Printer();
    private static final Logger LOG = LogManager.getLogger();
    private final FileHandler fileHandler;
    private Button buttonCopySelectedPages;
    private String bookTitle = "";
    private String bookAuthor = "";

    private LecternContainer lecternContainer;


    public GhostwriterLecternScreen(ReadBookScreen.IBookInfo bookInfoIn, boolean pageTurnSoundsIn, ItemStack currStack,
                                    Clipboard globalClipboard, LecternContainer lecternContainer,
                                    PlayerInventory playerInventory) {
        // TODO: What's the text param for?
        super(lecternContainer, playerInventory, new StringTextComponent(""));
        this.lecternContainer = lecternContainer;
        this.clipboard = globalClipboard;
        this.fileHandler = new FileHandler(this.clipboard);
        if (currStack != null){
            Item currItem = currStack.getItem();
            if (currItem != null){
                CompoundNBT compoundnbt = currStack.getTag();
                if (compoundnbt != null) {
                    this.bookTitle = compoundnbt.getString("title");
                    this.bookAuthor = compoundnbt.getString("author");
                }
            }
        }
    }


    /**
     * Helper function that extracts the pages from the read book until I find a cleaner way to do this
     * @return
     */
    public List<String> bookPages(){
        if (this.bookInfo instanceof ReadBookScreen.WrittenBookInfo) {
            ReadBookScreen.WrittenBookInfo b = (ReadBookScreen.WrittenBookInfo)this.bookInfo;
            return b.pages;
        }
        else if (this.bookInfo instanceof ReadBookScreen.UnwrittenBookInfo) {
            ReadBookScreen.UnwrittenBookInfo b = (ReadBookScreen.UnwrittenBookInfo)this.bookInfo;
            return b.pages;
        }
        else {
            return new ArrayList<String>();
        }
    }


    private List<String> pagesAsList(){
        List<String> pages = new ArrayList<String>();
        for (int i=0; i<this.getPageCount(); i++){
            // Ugly hack to convert the new JSON "Yo dawg I heard you like strings, so I put a string in your string" strings
            //  back to the old-style literal strings that everyone knows and loves. I'll update this to do the opposite once
            //  we're finally allowed to send JSON strings to the server. It also converts to oldschool formatting codes
            String pageText = BookUtilities.deJSONify(this.bookPages().get(i)); // TODO: Should this use the getPage function from IBookInfo instead?
            pages.add(pageText);
        }
        return pages;
    }


    private void copyBook() {
        this.clipboard.author = this.bookAuthor;
        this.clipboard.title = this.bookTitle;
        this.clipboard.pages.clear();
        this.clipboard.pages.addAll(this.pagesAsList());
        this.clipboard.bookInClipboard = true;
        printer.gamePrint(Printer.GRAY + "Book copied");
        this.updateButtons();
    }


    private void copySelectedPagesToClipboard(){
        int firstPage = Math.min(this.selectedPageA, this.selectedPageB);
        int lastPage = Math.max(this.selectedPageA, this.selectedPageB);

        // Handle the case where A or B is -1 (i.e. no selection)
        if (firstPage == -1 || lastPage == -1) {
            firstPage = this.currPage;
            lastPage = this.currPage;
        }

        if (firstPage >= 0 && lastPage >= firstPage && lastPage < this.bookPages().size()){
            this.clipboard.miscPages.clear();
            List<String> pagesAsList = this.pagesAsList();
            for (int i=firstPage; i<=lastPage; i++){
                this.clipboard.miscPages.add(pagesAsList.get(i));
            }
            printer.gamePrint(Printer.GRAY + "Selection copied");
        }
        else{
            printer.gamePrint(Printer.RED + "Invalid selection! Copy aborted.");
        }
        this.updateButtons();
    }

    @Override
    public void init() {
        // TODO: Should this only happen once? (i.e. have an initialized field)

        // Note that you can use the parameter? in the lambda function like this:
        // pressed_button.x += 20; // neat!

        //KeyboardHelper.enableRepeatEvents(true);

        int buttonWidth = 120;
        int buttonHeight = 20;
        int buttonSideOffset = 5;

        // Is this no longer a thing?
        //ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        //int rightXPos = scaledResolution.getScaledWidth() - (buttonWidth + buttonSideOffset);
        // Temporary hack
        int rightXPos = this.width-(buttonWidth+buttonSideOffset);

        this.addButton(new Button(5, 5, buttonWidth, buttonHeight, new StringTextComponent("File Browser"), (pressed_button) -> {
            this.minecraft.displayGuiScreen(new GhostwriterFileBrowserScreen(this));
        }));

        this.addButton(new Button(rightXPos, 5, buttonWidth, buttonHeight, new StringTextComponent("Copy Book"), (pressed_button) -> {
            this.copyBook();
        }));

        this.buttonSelectPageA = 			this.addButton(new Button(rightXPos, 50, buttonWidth/2, buttonHeight, new StringTextComponent("A"), (pressed_button) -> {
            this.selectedPageA = this.currPage;
            this.updateButtons();
        }));

        this.buttonSelectPageB = 			this.addButton(new Button(rightXPos+buttonWidth/2, 50, buttonWidth/2, buttonHeight, new StringTextComponent("B"), (pressed_button) -> {
            this.selectedPageB = this.currPage;
            this.updateButtons();
        }));

        this.buttonCopySelectedPages = 		this.addButton(new Button(rightXPos, 70, buttonWidth, buttonHeight, new StringTextComponent("Copy This Page"), (pressed_button) -> {
            this.copySelectedPagesToClipboard();
        }));

        super.init();
        this.updateButtons();
        // This is a hack based on LecternScreen.func_214176_h()
        // Books can be left open to a specitic page on a lectern. This displays that page.
        // Otherwise we'd just be showing the first page every time
        this.showPage(this.lecternContainer.getPage());
    }


    public void saveBookToDisk(File filepath) {
        List<String> pages = new ArrayList();
        for (int i=0; i<this.getPageCount(); i++) {
            // func_230456_a_ is the old getPageText
            // TODO: Verify that getString is returning formatting codes (the old function was getFormattedText())
            String s = this.bookInfo.func_230456_a_(i).getString();
            pages.add(s);
        }
        this.fileHandler.saveBookToGHBFile(this.bookTitle, this.bookAuthor, pages, filepath);
    }


    @Override
    public void updateButtons() {
        super.updateButtons();

        // Reset invalid selection
        if (this.selectedPageA < -1 || this.selectedPageA >= this.getPageCount()) {
            this.selectedPageA = -1;
        }
        if (this.selectedPageB < -1 || this.selectedPageB >= this.getPageCount()) {
            this.selectedPageB = -1;
        }

        if (this.selectedPageA >= 0 && this.selectedPageB >= 0 && this.selectedPageA != this.selectedPageB){
            // Multi page selection
            this.buttonCopySelectedPages.active = true;
            String xPages = (Math.abs(this.selectedPageB-this.selectedPageA)+1) + " Pages";
            this.buttonCopySelectedPages.setMessage(new StringTextComponent("Copy " + xPages));
            this.buttonSelectPageA.setMessage(new StringTextComponent("A: " + (this.selectedPageA+1)));
            this.buttonSelectPageB.setMessage(new StringTextComponent("B: " + (this.selectedPageB+1)));
        }
        else{
            this.buttonCopySelectedPages.setMessage(new StringTextComponent("Copy This Page"));
            this.buttonSelectPageA.setMessage(new StringTextComponent("A"));
            this.buttonSelectPageB.setMessage(new StringTextComponent("B"));
            if (this.selectedPageA >= 0) {
                this.buttonSelectPageA.setMessage(new StringTextComponent("A: " + (this.selectedPageA+1)));
            }
            if (this.selectedPageB >= 0) {
                this.buttonSelectPageB.setMessage(new StringTextComponent("B: " + (this.selectedPageB+1)));
            }
        }
    }
}
