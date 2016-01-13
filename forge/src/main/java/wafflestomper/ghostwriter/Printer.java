package wafflestomper.ghostwriter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Printer{
	private Minecraft mc = Minecraft.getMinecraft();
	
	// I think we've already established that I'm a terrible person 
	public static final EnumChatFormatting BLACK = EnumChatFormatting.BLACK;
	public static final EnumChatFormatting DARK_BLUE = EnumChatFormatting.DARK_BLUE;
	public static final EnumChatFormatting DARK_GREEN = EnumChatFormatting.DARK_GREEN;
	public static final EnumChatFormatting DARK_AQUA = EnumChatFormatting.DARK_AQUA;
	public static final EnumChatFormatting DARK_RED = EnumChatFormatting.DARK_RED;
	public static final EnumChatFormatting DARK_PURPLE = EnumChatFormatting.DARK_PURPLE;
	public static final EnumChatFormatting GOLD = EnumChatFormatting.GOLD;
	public static final EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
	public static final EnumChatFormatting DARK_GRAY = EnumChatFormatting.DARK_GRAY;
	public static final EnumChatFormatting BLUE = EnumChatFormatting.BLUE;
	public static final EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
	public static final EnumChatFormatting AQUA = EnumChatFormatting.AQUA;
	public static final EnumChatFormatting RED = EnumChatFormatting.RED;
	public static final EnumChatFormatting LIGHT_PURPLE = EnumChatFormatting.LIGHT_PURPLE;
	public static final EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
	public static final EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
	public static final EnumChatFormatting OBFUSCATED = EnumChatFormatting.OBFUSCATED;
	public static final EnumChatFormatting BOLD = EnumChatFormatting.BOLD;
	public static final EnumChatFormatting STRIKETHROUGH = EnumChatFormatting.STRIKETHROUGH;
	public static final EnumChatFormatting UNDERLINE = EnumChatFormatting.UNDERLINE;
	public static final EnumChatFormatting ITALIC = EnumChatFormatting.ITALIC;
	public static final EnumChatFormatting RESET = EnumChatFormatting.RESET;
	
	public Printer(){
	}
	
	public void gamePrint(String inStr){
		this.mc.thePlayer.addChatMessage(new ChatComponentText(inStr));
	}
	
	public void print(String toPrint){
		System.out.println(toPrint);
	}

	public void print(float toPrint) {
		print (String.format("%.2f", toPrint));
	}
}
