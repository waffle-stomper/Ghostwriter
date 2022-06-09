package wafflestomper.ghostwriter.datastructures;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class Pages {
	private final List<PageDetails> pages = new ArrayList<>();
	
	/**
	 * @return The requested page, or a blank page if index is invalid
	 */
	public PageDetails get(int index) {
		if (index >= 0 && index < this.pages.size()) {
			return this.pages.get(index);
		}
		// Return an empty page
		IntList lineStartIndices = new IntArrayList();
		lineStartIndices.add(0);
		List<String> lines = new ArrayList<>();
		lines.add("");
		List<Component> stylizedLines = new ArrayList<>();
		stylizedLines.add(Component.translatable(""));
		return new PageDetails("", lineStartIndices, lines, stylizedLines);
	}
	
	
	/**
	 * Returns the pages as a list of strings
	 */
	public List<String> asStrings() {
		List<String> pageStrings = new ArrayList<>();
		for (PageDetails page : this.pages) {
			pageStrings.add(page.fullPageText);
		}
		return pageStrings;
	}
	
	
	public void add(PageDetails page) {
		this.pages.add(page);
	}
	
	
	public void addAll(Pages pagesToAdd) {
		for (int i = 0; i < pagesToAdd.pages.size(); i++) {
			this.add(pagesToAdd.get(i));
		}
	}
}