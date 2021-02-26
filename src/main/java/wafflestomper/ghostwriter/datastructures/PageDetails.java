package wafflestomper.ghostwriter.datastructures;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class PageDetails {
	public final String fullPageText;
	public final int[] lineStartIndices;
	public final String[] lines;
	public final ITextComponent[] stylizedLines;
	
	public PageDetails(String fullPageText, IntList lineStartIndices, List<String> lines, List<ITextComponent> stylizedLines) {
		this.fullPageText = fullPageText;
		this.lineStartIndices = lineStartIndices.toIntArray();
		this.lines = lines.toArray(new String[0]);
		this.stylizedLines = stylizedLines.toArray(new ITextComponent[0]);
	}
}