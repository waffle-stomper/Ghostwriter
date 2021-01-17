package wafflestomper.ghostwriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.text.TextFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

public class Printer{
	private static Minecraft mc = Minecraft.getInstance();
	private static final Logger LOG = LogManager.getLogger();
	
	// I think we've already established that I'm a terrible person 
	public static final TextFormatting BLACK = TextFormatting.BLACK;
	public static final TextFormatting DARK_BLUE = TextFormatting.DARK_BLUE;
	public static final TextFormatting DARK_GREEN = TextFormatting.DARK_GREEN;
	public static final TextFormatting DARK_AQUA = TextFormatting.DARK_AQUA;
	public static final TextFormatting DARK_RED = TextFormatting.DARK_RED;
	public static final TextFormatting DARK_PURPLE = TextFormatting.DARK_PURPLE;
	public static final TextFormatting GOLD = TextFormatting.GOLD;
	public static final TextFormatting GRAY = TextFormatting.GRAY;
	public static final TextFormatting DARK_GRAY = TextFormatting.DARK_GRAY;
	public static final TextFormatting BLUE = TextFormatting.BLUE;
	public static final TextFormatting GREEN = TextFormatting.GREEN;
	public static final TextFormatting AQUA = TextFormatting.AQUA;
	public static final TextFormatting RED = TextFormatting.RED;
	public static final TextFormatting LIGHT_PURPLE = TextFormatting.LIGHT_PURPLE;
	public static final TextFormatting YELLOW = TextFormatting.YELLOW;
	public static final TextFormatting WHITE = TextFormatting.WHITE;
	public static final TextFormatting OBFUSCATED = TextFormatting.OBFUSCATED;
	public static final TextFormatting BOLD = TextFormatting.BOLD;
	public static final TextFormatting STRIKETHROUGH = TextFormatting.STRIKETHROUGH;
	public static final TextFormatting UNDERLINE = TextFormatting.UNDERLINE;
	public static final TextFormatting ITALIC = TextFormatting.ITALIC;
	public static final TextFormatting RESET = TextFormatting.RESET;
	
	public Printer(){
	}
	
	public void gamePrint(String inStr){
		mc.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(inStr));
	}
	
	public void print(String toPrint){
		LOG.info(toPrint);
	}

	public void print(float toPrint) {
		print (String.format("%.2f", toPrint));
	}
}
