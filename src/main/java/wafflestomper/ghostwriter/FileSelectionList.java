package wafflestomper.ghostwriter;

import java.io.File;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Based loosely on ServerSelectionList in 1.14
 *
 */
public class FileSelectionList extends ExtendedList<FileSelectionList.Entry> {

	private final FileSelectionList.ParentDirEntry parentDir;
	private final List<FileSelectionList.PathItemEntry> fileList = Lists.newArrayList();
	private final GhostwriterFileBrowserScreen owner;
	private static final Logger LOG = LogManager.getLogger();
	
	public FileSelectionList(GhostwriterFileBrowserScreen ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.owner = ownerIn;
		this.parentDir = new FileSelectionList.ParentDirEntry(this.owner);
		// TODO Auto-generated constructor stub
	}

	// TODO: Verify that this still works
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
	public static abstract class Entry extends ExtendedList.AbstractListEntry<FileSelectionList.Entry> {}
	
	
	public class ParentDirEntry extends FileSelectionList.Entry{
		protected long lastClickTime;
		protected final GhostwriterFileBrowserScreen owner;
		protected final Minecraft mc;
		
		
		public ParentDirEntry(GhostwriterFileBrowserScreen ownerIn) {
			this.lastClickTime = 0;
			this.owner = ownerIn;
			this.mc = Minecraft.getInstance();	
		}
		
		// TODO: Verify that this still works
		@Override
		public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_,
						   int mouseX, int mouseY, boolean mouseIsOver, float p_render_9_) {
			this.mc.fontRenderer.drawString(matrixStack, "..", (float)(p_render_3_ ), (float)(p_render_2_ + 1), 0x00FF00); // Params are string, x, y, color
		}
		
		
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			// TODO: Why were we calculating these? They're not used?
//	         double d0 = p_mouseClicked_1_ - (double)FileSelectionList.this.getRowLeft();
//	         double d1 = p_mouseClicked_3_ - (double)FileSelectionList.this.getRowTop(FileSelectionList.this.children().indexOf(this));
	         this.owner.setSelectedSlot(this);
	         if (Util.milliTime() - this.lastClickTime < 250L) {
	            // TODO: Double click handling
	        	 this.lastClickTime = 0; // Prevent triple-click
	        	 this.owner.navigateUp();
	        	 return true;
	         }

	         this.lastClickTime = Util.milliTime();
	         return false;
	     }

	}

	
	@OnlyIn(Dist.CLIENT)
 	public class PathItemEntry extends FileSelectionList.Entry {
		public File path;
		protected long lastClickTime;
		protected final GhostwriterFileBrowserScreen owner;
		protected final Minecraft mc;
		private long hoverStart = System.currentTimeMillis();

		public PathItemEntry(GhostwriterFileBrowserScreen ownerIn, File pathIn) {
			this.path = pathIn;
			this.owner = ownerIn;
			this.mc = Minecraft.getInstance();
		}
		
		/**
		 * @param p_render_1_ slot number?
		 * @param p_render_2_ some kind of y... maybe top?
		 * @param slotX slotX?
		 * @param slotWidth slotWidth?
		 * @param p_render_5_ 
		 * @param tickLengthMaybe tickLength i think
		 * 
		 */
		@Override
		public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, int slotX, int slotWidth, int p_render_5_,
				int mouseX, int mouseY, boolean mouseIsOver, float tickLengthMaybe) {

			
			int color = 0xFFFFFF;
			if (!path.exists()) { // TODO: do we really need to do do this every render tick?
				color = 0x333333;
    			this.owner.setDirectoryDirty();
			}
			else if (path.isFile()) {
				color = 0xFF0000;
			}
			else if (path.isDirectory()) {
				color = 0x00FF00;
			}
			
			// Draw the trimmed filename in the slot
			// TODO: I think func_238412_a_ is the new trimStringToWidth but I'm not sure
			String s = this.mc.fontRenderer.func_238412_a_(this.path.getName(), slotWidth);
			this.mc.fontRenderer.drawString(matrixStack, s,(float)(slotX ), (float)(p_render_2_ + 1), color);
			
			// Set up the hover text if the mouse is hovering over this slot
			if (mouseIsOver) {
				if (System.currentTimeMillis()-this.hoverStart > 2000) {
					String s2 = this.path.getName();
					this.owner.setHoveringText(s2);
				}
			}
			else {
				this.hoverStart = System.currentTimeMillis();
			}
			
		}
		
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			// TODO: Why were we calculating these? They aren't used?
//	         double d0 = p_mouseClicked_1_ - (double)FileSelectionList.this.getRowLeft();
//	         double d1 = p_mouseClicked_3_ - (double)FileSelectionList.this.getRowTop(FileSelectionList.this.children().indexOf(this));
	    
	         this.owner.setSelectedSlot(this);
	         if (Util.milliTime() - this.lastClickTime < 250L) {
	            // TODO: Double click handling
	        	 this.lastClickTime = 0; // Prevent triple-click
	        	 this.owner.navigateInto(path);
	        	 return true;
	         }

	         this.lastClickTime = Util.milliTime(); // TODO: Is this necessary now that double clicks no longer make sense for this screen?
	         return false; // TODO: Should this really return false?
	    }
	}


	public void setSelectedSlot(PathItemEntry normalEntry) {
		// TODO Auto-generated method stub
	}
}
