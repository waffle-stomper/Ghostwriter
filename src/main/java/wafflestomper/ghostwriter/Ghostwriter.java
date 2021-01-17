package wafflestomper.ghostwriter;

import java.io.File;

import net.minecraft.client.gui.screen.EditBookScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.LecternScreen;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.util.Hand;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod("ghostwriter")
public class Ghostwriter{
	
	private final Minecraft mc = Minecraft.getInstance();
	private final Printer printer = new Printer();
	public Clipboard globalClipboard = new Clipboard();
	boolean devEnv = false; // TODO: Find another way to detect this
	private static long lastMessage = 0;
	private static final Logger LOG = LogManager.getLogger();
	public static File currentPath; 
	private boolean lecternArmed = false;
	
	
	public Ghostwriter() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
	}
	
	
	private void setup(final FMLClientSetupEvent event){
        LOG.info("Setting up...");
    }
	
	
	public static void rateLimitedDebugMessage(String message){
		if (System.currentTimeMillis() - lastMessage > 1000){
			System.out.println(message);
			lastMessage = System.currentTimeMillis();
		}
	}


	// TODO: Is this even right? We're closing the container I think?
	// TODO: Add check for 'air' stack instead of book (I think there's a race condition where this might get called too early)
	// TODO: Refactor this to remove the duplicated code with guiOpen() below
	/**
	 * This swaps the book on a lectern for the Ghostwriter equivalent
	 */
	@SubscribeEvent
	public void tick(TickEvent event) {
		if (this.mc.currentScreen instanceof LecternScreen && lecternArmed) {
			lecternArmed = false;
			LOG.debug("Lectern screen detected!");

			if (this.mc.player == null){
				LOG.error("Aborting GUI replacement because the player is null");
				return;
			}

			// Abort if the player is crouching
			if (this.mc.player.isCrouching()) {
				LOG.debug("Aborting GUI replacement because the player is crouching");
				return;
			}
			
			LecternScreen ls = (LecternScreen)this.mc.currentScreen;
			ItemStack bookStack = ls.getContainer().getBook();
			LOG.info("Swapping LecternScreen for GhostwriterReadBookScreen...");
			
			ReadBookScreen.IBookInfo bookInfo;
			if (bookStack.getItem() instanceof WritableBookItem){
				bookInfo = new ReadBookScreen.UnwrittenBookInfo(bookStack);
			}
			else if (bookStack.getItem() instanceof WrittenBookItem) {
				bookInfo = new ReadBookScreen.WrittenBookInfo(bookStack);
			}
			else {
				LOG.error("Unknown book type on lectern!");
				return;
			}

			GhostwriterReadBookScreen g = new GhostwriterReadBookScreen(bookInfo, true, bookStack, this.globalClipboard, ls.getContainer());
			this.mc.displayGuiScreen(g);
			
			LOG.debug("GUI swap done!");
		}
	}
	
	
	/**
	 * This swaps the default book GUI for the Ghostwriter screen before it loads
	 */
	@SubscribeEvent
	public void guiOpen(GuiOpenEvent event){
		Screen eventGui = event.getGui();
		if (eventGui == null){return;}
		LOG.debug("GUIOpenEvent: " + eventGui.toString());
		
		// TODO: Signed books are handled differently from unsigned books for some bizarre reason
		if (eventGui instanceof net.minecraft.client.gui.screen.EditBookScreen || eventGui instanceof ReadBookScreen){
			
			// Abort if the player is crouching
			if (this.mc.player.isCrouching()) {
				LOG.debug("Aborting GUI replacement becuase the player is crouching");
				return;
			}

			if (eventGui instanceof LecternScreen) {
				LOG.info("Aborting early GUI replacement (target is a lectern)");
				lecternArmed = true;
				return;
			}
			
			ItemStack bookStack = this.mc.player.getHeldItem(Hand.MAIN_HAND); // TODO: Does this need to take the off hand into account too?
			
			// Abort if there's nothing in the player's hand (which should be impossible?)
        	if (bookStack == null){
        		LOG.error("Aborting GUI replacement - bookStack is null!");
        		return;
        	}
    		Item currItem = bookStack.getItem();
    		if (currItem == null){
    			LOG.error("bookStack.getItem() is null!");
    			return;
    		}
    		
    		// Finally, do the GUI replacement
			if (eventGui instanceof EditBookScreen) {
				eventGui = new GhostwriterEditBookScreen(this.mc.player, bookStack, Hand.MAIN_HAND, this.globalClipboard);
			}
			else if (eventGui instanceof ReadBookScreen) {
				ReadBookScreen.WrittenBookInfo bookInfo = new ReadBookScreen.WrittenBookInfo(bookStack);
				eventGui = new GhostwriterReadBookScreen(bookInfo, true, bookStack, this.globalClipboard, null); // TODO: Shouldn't this accept UnwrittenBookInfo too?
			}
			event.setGui(eventGui);
			LOG.debug("GUI swap done!");
		}
	}
}
