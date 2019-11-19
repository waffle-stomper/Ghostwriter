package wafflestomper.ghostwriter;

import java.io.File;
import java.util.List;

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
	private final GuiFileBrowser owner;
	
	public FileSelectionList(GuiFileBrowser ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.owner = ownerIn;
		this.parentDir = new FileSelectionList.ParentDirEntry(this.owner);
		// TODO Auto-generated constructor stub
	}

	
	public void updateFileList(List<File> displayFiles) {
		this.fileList.clear();
		for (File f : displayFiles) {
			this.fileList.add(new PathItemEntry(this.owner, f));
		}
		// This is required to have the slots render... I think?
		this.children().clear();
		this.children().add(this.parentDir);
		for (PathItemEntry n : this.fileList) {
			this.children().add(n);
		}
		
	}
	

	@OnlyIn(Dist.CLIENT)
	public static abstract class Entry extends ExtendedList.AbstractListEntry<FileSelectionList.Entry> {}
	
	
	public class ParentDirEntry extends FileSelectionList.Entry{
		protected long lastClickTime;
		protected final GuiFileBrowser owner;
		protected final Minecraft mc;
		
		
		public ParentDirEntry(GuiFileBrowser ownerIn) {
			this.lastClickTime = 0;
			this.owner = ownerIn;
			this.mc = Minecraft.getInstance();	
		}

		
		@Override
		public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_,
				int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
			this.mc.fontRenderer.drawString("..", (float)(p_render_3_ ), (float)(p_render_2_ + 1), 0x00FF00);
		}
		
		
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
	         double d0 = p_mouseClicked_1_ - (double)FileSelectionList.this.getRowLeft();
	         double d1 = p_mouseClicked_3_ - (double)FileSelectionList.this.getRowTop(FileSelectionList.this.children().indexOf(this));
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
		protected final GuiFileBrowser owner;
		protected final Minecraft mc;

		public PathItemEntry(GuiFileBrowser ownerIn, File pathIn) {
			this.path = pathIn;
			this.owner = ownerIn;
			this.mc = Minecraft.getInstance();
		}

		@Override
		public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_,
				int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
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
			this.mc.fontRenderer.drawString(this.path.getName(),(float)(p_render_3_ ), (float)(p_render_2_ + 1), color);
			
		}
		
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
	         double d0 = p_mouseClicked_1_ - (double)FileSelectionList.this.getRowLeft();
	         double d1 = p_mouseClicked_3_ - (double)FileSelectionList.this.getRowTop(FileSelectionList.this.children().indexOf(this));
	    
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
