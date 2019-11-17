package wafflestomper.ghostwriter;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ServerSelectionList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Based loosely on ServerSelection in 1.14
 *
 */
public class FileSelectionList extends ExtendedList<FileSelectionList.Entry> {
	private final List<FileSelectionList.NormalEntry> fileList = Lists.newArrayList();
	private final GuiFileBrowser owner;
	
	public FileSelectionList(GuiFileBrowser ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.owner = ownerIn;
		// TODO Auto-generated constructor stub
	}

	
	public void updateFileList(List<File> displayFiles) {
		this.fileList.clear();
		for (File f : displayFiles) {
			this.fileList.add(new NormalEntry(this.owner, f));
		}
		// This is required to have the slots render... I think?
		this.children().clear();
		for (NormalEntry n : this.fileList) {
			this.children().add(n);
		}
		
	}

	@OnlyIn(Dist.CLIENT)
	public abstract static class Entry extends ExtendedList.AbstractListEntry<FileSelectionList.Entry> {}

	
	@OnlyIn(Dist.CLIENT)
 	public class NormalEntry extends FileSelectionList.Entry {
		public File file;
		public long lastClickTime = 0;
		private final GuiFileBrowser owner;
		private final Minecraft mc;

		public NormalEntry(GuiFileBrowser ownerIn, File fileIn) {
			this.file = fileIn;
			this.owner = ownerIn;
			this.mc = Minecraft.getInstance();
		}

		@Override
		public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_,
				int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
			this.mc.fontRenderer.drawString(this.file.getName(),(float)(p_render_3_ ), (float)(p_render_2_ + 1), 16777215);
			
		}
		
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
	         double d0 = p_mouseClicked_1_ - (double)FileSelectionList.this.getRowLeft();
	         double d1 = p_mouseClicked_3_ - (double)FileSelectionList.this.getRowTop(FileSelectionList.this.children().indexOf(this));
	         if (d0 <= 32.0D) { // What is this? Some kind of left-right thing?


//	            int i = this.owner.serverListSelector.children().indexOf(this);
//	            if (d0 < 16.0D && d1 < 16.0D && i > 0) {
//	               int k = Screen.hasShiftDown() ? 0 : i - 1;
//	               this.owner.getServerList().swapServers(i, k);
//	               if (this.owner.serverListSelector.getSelected() == this) {
//	                  this.owner.func_214287_a(this);
//	               }
//
//	               this.owner.serverListSelector.updateOnlineServers(this.owner.getServerList());
//	               return true;
//	            }
//
//	            if (d0 < 16.0D && d1 > 16.0D && i < this.owner.getServerList().countServers() - 1) {
//	               ServerList serverlist = this.owner.getServerList();
//	               int j = Screen.hasShiftDown() ? serverlist.countServers() - 1 : i + 1;
//	               serverlist.swapServers(i, j);
//	               if (this.owner.serverListSelector.getSelected() == this) {
//	                  this.owner.func_214287_a(this);
//	               }
//
//	               this.owner.serverListSelector.updateOnlineServers(serverlist);
//	               return true;
//	            }
	         }
	         this.owner.setSelectedSlot(this);
	         if (Util.milliTime() - this.lastClickTime < 250L) {
	            // TODO: Double click handling
	         }

	         this.lastClickTime = Util.milliTime();
	         return false;
	      }
		
	}


	public void setSelectedSlot(NormalEntry normalEntry) {
		// TODO Auto-generated method stub
		
	}
}
