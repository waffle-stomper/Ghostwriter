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
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.init.Items;
import net.minecraft.item.Item;






@Mod(modid = Ghostwriter.MODID, version = Ghostwriter.VERSION, name = Ghostwriter.NAME)
public class Ghostwriter{
    public static final String MODID = "Ghostwriter";
    public static final String VERSION = "1.7.10-1.5.1";
    public static final String NAME = "Ghostwriter";
	
	private Minecraft mc = Minecraft.getMinecraft();
	private Printer printer = new Printer();
	//This was a bad decision and I should feel bad about it.
	public Clipboard clipboard = new Clipboard();

	/* 
	 * AUTO GUI SWITCHER (AND SOME DEBUGGING STUFF). DO NOT DELETE IT ALL AT ONCE!
	 */
	public Ghostwriter(){
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private int connectWait = 10;
	private boolean connected = false;
	private int firstGuiOpenWait = 20;
	private boolean firstGuiOpen = false;
	
    @SubscribeEvent
    public void tick(PlayerTickEvent event) 
    {
    	if (event.phase == Phase.START){
    		
    		//######################################################
    		//######################################################
    		//######################################################
    		//Replace the default book GUI with the Ghostwriter GUI
    		//               DO NOT REMOVE THIS!!!!
    		//######################################################
    		//######################################################
    		//######################################################
            if (this.mc.currentScreen instanceof GuiScreenBook) {
            	EntityPlayerSP p = this.mc.thePlayer;
                mc.displayGuiScreen(new GuiGhostwriterBook(p, p.getHeldItem(), p.getHeldItem().getItem().equals(Items.writable_book), this.clipboard));
            }
            //######################################################
            //######################################################
            //######################################################
            //######################################################
            //######################################################
            
            
            
            // THESE ARE USED IN TESTING ONLY!! DISABLE BEFORE RELEASE !!
    		if (!firstGuiOpen){
    			if (firstGuiOpenWait-- <= 0){
    				firstGuiOpen = true;
    				//open the file selection gui
    				//this.mc.displayGuiScreen(new GuiFileSelection(new GuiGhostwriterBook(this.mc.thePlayer, this.mc.thePlayer.getHeldItem(), this.mc.thePlayer.getHeldItem().getItem().equals(Items.writable_book), this.clipboard)));
    				
    				//just open the ghostwriter gui
    				//this.mc.displayGuiScreen(new GuiGhostwriterBook(this.mc.thePlayer, this.mc.thePlayer.getHeldItem(), this.mc.thePlayer.getHeldItem().getItem().equals(Items.writable_book), this.clipboard));
    				   				
    			}
    		}
    		
    		
    		
    	}
    }
    
    //AUTO CONNECT TO LOCALHOST! USED FOR TESTING! DISABLE BEFORE RELEASE!    
    @SubscribeEvent
    public void renderTick(RenderTickEvent event){
    	if (event.phase == Phase.END){
    		if (!this.connected){
    			if (connectWait-- <= 0){
    				//FMLClientHandler.instance().connectToServerAtStartup("localhost", 25565);
    	    		this.connected = true;
    			}
    		}
    	}
    }
    
}
