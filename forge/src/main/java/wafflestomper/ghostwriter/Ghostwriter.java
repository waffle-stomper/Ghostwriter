package wafflestomper.ghostwriter;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod(modid = Ghostwriter.MODID, version = Ghostwriter.VERSION, name = Ghostwriter.NAME, updateJSON = "https://raw.githubusercontent.com/waffle-stomper/Ghostwriter/master/update.json", canBeDeactivated = true)
public class Ghostwriter{
	
    public static final String MODID = "ghostwriter";
    public static final String VERSION = "1.8.8";
    public static final String NAME = "Ghostwriter";
	
	private Minecraft mc = Minecraft.getMinecraft();
	private Printer printer = new Printer();
	public Clipboard clipboard = new Clipboard();
	boolean devEnv = false;
	private long lastMessage = 0;
	
	
	public Ghostwriter(){
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		// Detect development environment
		this.devEnv = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}
	
	
	private void rateLimitedDebugMessage(String message){
		if (!this.devEnv){
			return;
		}
		if (System.currentTimeMillis() - this.lastMessage > 2000){
			this.printer.gamePrint(message);
			this.lastMessage = System.currentTimeMillis();
		}
	}
	
	
	/**
	 * This glorious bastard swaps the default book GUI for the Ghostwriter screen before it even loads
	 * I love the future
	 */
	@SubscribeEvent
	public void guiOpen(GuiOpenEvent event){
		GuiScreen eventGui = event.getGui();
		if (eventGui == null){return;}
		if (eventGui instanceof GuiScreenBook){
			EntityPlayerSP p = this.mc.thePlayer;
        	ItemStack currStack = p.getHeldItem(EnumHand.MAIN_HAND);
        	if (currStack != null){
        		Item currItem = currStack.getItem();
        		if (currItem != null){
        			// If left shift is held down, let the standard Minecraft book GUI open
        			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
        				return;
        			}
        			eventGui = new GuiGhostwriterBook(p, currStack, currItem.equals(Items.WRITABLE_BOOK), this.clipboard);
        			event.setGui(eventGui);
        		}
        		else{
            		rateLimitedDebugMessage("this.mc.thePlayer.getHeldItem().getItem() is null!");
            	}
        	}
        	else{
        		rateLimitedDebugMessage("this.mc.thePlayer.getHeldItem() is null!");
        	}
		}
	}
}
