package wafflestomper.ghostwriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.text.TextFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

public class Printer{
	private static final Minecraft MC = Minecraft.getInstance();
	private static final Logger LOG = LogManager.getLogger();
	
	// I think we've already established that I'm a terrible person
	public static final TextFormatting GRAY = TextFormatting.GRAY;
	public static final TextFormatting DARK_GRAY = TextFormatting.DARK_GRAY;
	public static final TextFormatting GREEN = TextFormatting.GREEN;
	public static final TextFormatting AQUA = TextFormatting.AQUA;
	public static final TextFormatting RED = TextFormatting.RED;
	
	public Printer(){
	}
	
	public void gamePrint(String inStr){
		MC.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(inStr));
	}
	
	public void print(String toPrint){
		LOG.info(toPrint);
	}

	public void print(float toPrint) {
		print (String.format("%.2f", toPrint));
	}
}
