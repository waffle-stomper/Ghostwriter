package wafflestomper.ghostwriter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.LecternScreen;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.screen.Screen;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;


@Mod("ghostwriter")
public class Ghostwriter{
	
	private final Minecraft mc = Minecraft.getInstance();
	public Clipboard globalClipboard = new Clipboard();
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
			LOG.info("Swapping LecternScreen for GhostwriterLecternScreen...");
			
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

			LOG.debug("Replacing the current screen with a GhostwriterLecternScreen");
			GhostwriterLecternScreen g = new GhostwriterLecternScreen(bookStack,
					this.globalClipboard, ls.getContainer(), this.mc.player.inventory);
			this.mc.displayGuiScreen(g);
			
			LOG.debug("Lectern GUI swap done!");
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
		
		// Signed books are handled differently from unsigned books for some bizarre reason
		if (eventGui instanceof EditBookScreen || eventGui instanceof ReadBookScreen){
			
			if (this.mc.player == null){
				LOG.error("Minecraft.player is null. Cannot continue with GUI swap");
				return;
			}
			
			// Abort if the player is crouching
			if (this.mc.player.isCrouching()) {
				LOG.debug("Aborting GUI replacement becuase the player is crouching");
				return;
			}

			if (eventGui instanceof LecternScreen){
				if (eventGui instanceof GhostwriterLecternScreen){
					LOG.info("Aborting GUI replacement - it's already a Ghostwriter Lectern Screen");
					return;
				}
				LOG.info("Aborting early GUI replacement (target is a lectern). Setting lectern swap flag");
				lecternArmed = true;
				return;
			}

			if (eventGui instanceof GhostwriterReadBookScreen || eventGui instanceof GhostwriterEditBookScreen){
				LOG.debug("Aborting GUI replacement - it's already a Ghostwriter gui");
				return;
			}
			
			// TODO: Does this need to take the off hand into account too?
			ItemStack bookStack = this.mc.player.getHeldItem(Hand.MAIN_HAND);
			
			// Finally, do the GUI replacement
			if (eventGui instanceof EditBookScreen){
				LOG.debug("Replacing the current screen with a GhostwriterEditBookScreen");
				eventGui = new GhostwriterEditBookScreen(this.mc.player, bookStack, Hand.MAIN_HAND, this.globalClipboard);
			}
			else if (eventGui instanceof ReadBookScreen) {
				LOG.debug("Replacing the current screen with a GhostwriterReadBookScreen");
				ReadBookScreen.WrittenBookInfo bookInfo = new ReadBookScreen.WrittenBookInfo(bookStack);
				eventGui = new GhostwriterReadBookScreen(bookInfo, true, bookStack, this.globalClipboard);
			}
			event.setGui(eventGui);
			LOG.debug("GUI swap done!");
		}
	}
}
