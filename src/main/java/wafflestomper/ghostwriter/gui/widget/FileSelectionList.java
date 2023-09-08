package wafflestomper.ghostwriter.gui.widget;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import wafflestomper.ghostwriter.gui.screen.GhostwriterFileBrowserScreen;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;

/**
 * Based loosely on ServerSelectionList in 1.14
 */
public class FileSelectionList extends ObjectSelectionList<FileSelectionList.Entry> {
	
	private final FileSelectionList.ParentDirEntry parentDir;
	private final List<FileSelectionList.PathItemEntry> fileList = Lists.newArrayList();
	private final GhostwriterFileBrowserScreen owner;
	
	
	public FileSelectionList(GhostwriterFileBrowserScreen ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.owner = ownerIn;
		this.parentDir = new ParentDirEntry(this.owner);
	}
	
	
	public void updateFileList(List<File> displayFiles) {
		this.fileList.clear();
		for (File f : displayFiles) {
			this.fileList.add(new PathItemEntry(this.owner, f));
		}
		// This is required to have the slots render... I think?
		this.clearEntries();
		this.addEntry(this.parentDir);
		for (PathItemEntry n : this.fileList) {
			this.addEntry(n);
		}
	}
	
	
	@OnlyIn(Dist.CLIENT)
	public static abstract class Entry extends ObjectSelectionList.Entry<FileSelectionList.Entry> {
	}
	
	
	public static class ParentDirEntry extends FileSelectionList.Entry {
		protected final GhostwriterFileBrowserScreen owner;
		protected final Minecraft mc;
		protected long lastClickTime;
		
		
		public ParentDirEntry(GhostwriterFileBrowserScreen ownerIn) {
			this.lastClickTime = 0;
			this.owner = ownerIn;
			this.mc = Minecraft.getInstance();
		}
		
		@Override
		@ParametersAreNonnullByDefault
		public void render(GuiGraphics guiGraphics, int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_,
						   int mouseX, int mouseY, boolean mouseIsOver, float p_render_9_) {
			guiGraphics.drawString(this.mc.font, "..", p_render_3_, p_render_2_ + 1, 0x00FF00); // Params are string, x, y, color
		}
		
		
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			this.owner.setSelectedSlot(this);
			if (Util.getMillis() - this.lastClickTime < 250L) {
				this.lastClickTime = 0; // Prevent triple-click
				this.owner.navigateUp();
				return true;
			}
			
			this.lastClickTime = Util.getMillis();
			return false;
		}
		
		public Component getNarration(){
			return Component.translatable("NOT_SET - please file an issue in the Ghostwriter Github repo");
		}
	}
	
	
	@OnlyIn(Dist.CLIENT)
	public static class PathItemEntry extends FileSelectionList.Entry {
		public final File path;
		protected final GhostwriterFileBrowserScreen owner;
		protected final Minecraft mc;
		protected long lastClickTime;
		private long hoverStart = System.currentTimeMillis();
		
		public PathItemEntry(GhostwriterFileBrowserScreen ownerIn, File pathIn) {
			this.path = pathIn;
			this.owner = ownerIn;
			this.mc = Minecraft.getInstance();
		}
		
		/**
		 * @param p_render_1_  slot number?
		 * @param p_render_2_  some kind of y... maybe top?
		 * @param slotX        slotX?
		 * @param slotWidth    slotWidth?
		 * @param p_render_5_  No idea
		 * @param partialTicks Partial ticks
		 */
		@Override
		@ParametersAreNonnullByDefault
		public void render(GuiGraphics guiGraphics, int p_render_1_, int p_render_2_, int slotX, int slotWidth, int p_render_5_,
						   int mouseX, int mouseY, boolean mouseIsOver, float partialTicks) {
			
			int color = 0xFFFFFF;
			if (!path.exists()) {
				color = 0x333333;
				this.owner.setDirectoryDirty();
			} else if (path.isFile()) {
				color = 0xFF0000;
			} else if (path.isDirectory()) {
				color = 0x00FF00;
			}
			
			// Draw the trimmed filename in the slot
			// I think func_238412_a_ is the new trimStringToWidth, but I'm not sure
			String s = this.mc.font.plainSubstrByWidth(this.path.getName(), slotWidth);
			guiGraphics.drawString(this.mc.font, s, slotX, p_render_2_ + 1, color);
			
			// Set up the hover text if the mouse is hovering over this slot
			if (mouseIsOver) {
				if (System.currentTimeMillis() - this.hoverStart > 2000) {
					String s2 = this.path.getName();
					this.owner.setHoveringText(s2);
				}
			} else {
				this.hoverStart = System.currentTimeMillis();
			}
			
		}
		
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			this.owner.setSelectedSlot(this);
			if (Util.getMillis() - this.lastClickTime < 250L) {
				// Double click handling
				this.lastClickTime = 0; // Prevent triple-click
				this.owner.navigateInto(path);
				return true;
			}
			
			this.lastClickTime = Util.getMillis();
			return false;
		}
		
		public Component getNarration(){
			return Component.translatable("NOT_SET - please file an issue in the Ghostwriter Github repo");
		}
	}
}
