package wafflestomper.ghostwriter;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import wafflestomper.ghostwriter.modified_mc_files.ReadBookScreenMod;


@Mod("ghostwriter")
public class Ghostwriter{
	
	private Minecraft mc = Minecraft.getInstance();
	private Printer printer = new Printer();
	public Clipboard globalClipboard = new Clipboard();
	boolean devEnv = false; // TODO: Find another way to detect this
	private static long lastMessage = 0;
	private static final Logger LOG = LogManager.getLogger();
	public static File currentPath; 
	
	
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
	
	
	/**
	 * This glorious bastard swaps the default book GUI for the Ghostwriter screen before it even loads
	 * I love the future
	 */
	@net.minecraftforge.eventbus.api.SubscribeEvent
	public void guiOpen(GuiOpenEvent event){
		Screen eventGui = event.getGui();
		if (eventGui == null){return;}
		LOG.debug("GUIOpenEvent: " + eventGui.toString());
		
		// TODO: Signed books are handled differently from unsigned books for some bizarre reason
		if (eventGui instanceof net.minecraft.client.gui.screen.EditBookScreen || eventGui instanceof ReadBookScreen){
			ClientPlayerEntity p = this.mc.player;
        	ItemStack currStack = p.getHeldItem(Hand.MAIN_HAND); // TODO: Does this need to take the off hand into account too?
        	
			// Abort if the player is crouching
			if (p.isSneaking()) {
				LOG.debug("Aborting GUI replacement becuase the player is crouching");
				return;
			}
        	
			// Abort if there's nothing in the player's hand (which should be impossible?)
        	if (currStack == null){
        		rateLimitedDebugMessage("Aborting GUI replacement - this.mc.thePlayer.getHeldItem() is null!");
        		return;
        	}
    		Item currItem = currStack.getItem();
    		if (currItem == null){
    			rateLimitedDebugMessage("this.mc.thePlayer.getHeldItem().getItem() is null!");
    			return;
    		}
    		
    		// Finally, do the GUI replacement
			if (eventGui instanceof net.minecraft.client.gui.screen.EditBookScreen) {
				eventGui = new GhostwriterEditBookScreen(p, currStack, Hand.MAIN_HAND, this.globalClipboard);
			}
			else if (eventGui instanceof ReadBookScreen) {
				ReadBookScreenMod.WrittenBookInfo bookInfo = new ReadBookScreenMod.WrittenBookInfo(currStack);
				eventGui = new GhostwriterReadBookScreen(bookInfo, true, currStack, this.globalClipboard); // TODO: Shouldn't this accept UnwrittenBookInfo too?
			}
			event.setGui(eventGui);
			LOG.debug("GUI swap done!");
		}
	}
}
