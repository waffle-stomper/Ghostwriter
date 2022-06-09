package wafflestomper.ghostwriter.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import wafflestomper.ghostwriter.Ghostwriter;

public class Printer {
	// I think we've already established that I'm a terrible person
	public static final ChatFormatting GRAY = ChatFormatting.GRAY;
	public static final ChatFormatting DARK_GRAY = ChatFormatting.DARK_GRAY;
	public static final ChatFormatting GREEN = ChatFormatting.GREEN;
	public static final ChatFormatting AQUA = ChatFormatting.AQUA;
	public static final ChatFormatting RED = ChatFormatting.RED;
	private static final Minecraft MC = Minecraft.getInstance();
	
	public void gamePrint(String inStr) {
		try {
			MC.gui.getChat().addMessage(Component.translatable(inStr));
		}
		catch (NullPointerException e){
			Ghostwriter.LOG.error("NPE while trying to print this message to game: " + inStr);
		}
	}
}
