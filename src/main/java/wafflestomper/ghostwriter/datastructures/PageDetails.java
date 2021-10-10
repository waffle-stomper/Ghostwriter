package wafflestomper.ghostwriter.datastructures;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.chat.Component;

import java.util.List;

public class PageDetails {
	public final String fullPageText;
	public final int[] lineStartIndices;
	public final String[] lines;
	public final Component[] stylizedLines;
	
	public PageDetails(String fullPageText, IntList lineStartIndices, List<String> lines, List<Component> stylizedLines) {
		this.fullPageText = fullPageText;
		this.lineStartIndices = lineStartIndices.toIntArray();
		this.lines = lines.toArray(new String[0]);
		this.stylizedLines = stylizedLines.toArray(new Component[0]);
	}
}