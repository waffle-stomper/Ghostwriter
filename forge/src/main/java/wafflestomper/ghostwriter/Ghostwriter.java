package wafflestomper.ghostwriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;


@Mod(modid = Ghostwriter.MODID, version = Ghostwriter.VERSION, name = Ghostwriter.NAME, canBeDeactivated = true)
public class Ghostwriter{
    public static final String MODID = "Ghostwriter";
    public static final String VERSION = "1.8.0-1.7.3";
    public static final String NAME = "Ghostwriter";
	
	private Minecraft mc = Minecraft.getMinecraft();
	private Printer printer = new Printer();
	public Clipboard clipboard = new Clipboard();
	private int connectWait = 10;
	private boolean connected = false;
	private int firstGuiOpenWait = 20;
	private boolean firstGuiOpen = false;
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
	
	@SubscribeEvent
	public void useItem(PlayerUseItemEvent event){
		System.out.println(event.toString());
	}
	
	
	/**
	 * This glorious bastard swaps the default book GUI for the Ghostwriter screen before it even loads
	 * I love the future
	 */
	@SubscribeEvent
	public void guiOpen(GuiOpenEvent event){
		if (event.gui == null){return;}
		if (event.gui instanceof GuiScreenBook){
			EntityPlayerSP p = this.mc.thePlayer;
        	ItemStack currStack = p.getHeldItem();
        	if (currStack != null){
        		Item currItem = currStack.getItem();
        		if (currItem != null){
                    event.gui = new GuiGhostwriterBook(p, p.getHeldItem(), currItem.equals(Items.writable_book), this.clipboard);
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
	
	
    @SubscribeEvent
    public void renderTick(RenderTickEvent event){
    	if (event.phase == Phase.START){
    		// Swap the default GuiScreenBook for Ghostwriter
          
            // THESE ARE USED IN TESTING ONLY!! DISABLE BEFORE RELEASE !!
    		if (!firstGuiOpen && this.devEnv){
    			if (firstGuiOpenWait-- <= 0){
    				firstGuiOpen = true;
    				//open the file selection gui
    				//this.mc.displayGuiScreen(new GuiFileSelection(new GuiGhostwriterBook(this.mc.thePlayer, this.mc.thePlayer.getHeldItem(), this.mc.thePlayer.getHeldItem().getItem().equals(Items.writable_book), this.clipboard)));
    				
    				//just open the ghostwriter gui
    				//this.mc.displayGuiScreen(new GuiGhostwriterBook(this.mc.thePlayer, this.mc.thePlayer.getHeldItem(), this.mc.thePlayer.getHeldItem().getItem().equals(Items.writable_book), this.clipboard));		
    			}
    		}
    	}
    	
    	if (event.phase == Phase.END && this.devEnv){
    		if (!this.connected){
    			if (connectWait-- <= 0){
    				//FMLClientHandler.instance().connectToServerAtStartup("localhost", 25565);
    	    		this.connected = true;
    			}
    		}
    	}
	}
	
	
}
