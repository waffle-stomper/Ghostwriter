package wafflestomper.ghostwriter.modified_mc_files;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReadBookScreenMod extends Screen {
	/**
	 * I think this builds an empty bookinfo?
	 */
	public static final ReadBookScreenMod.IBookInfo EMPTY_BOOK = new ReadBookScreenMod.IBookInfo() {
		public int getPageCount() {
			return 0;
		}

		public ITextComponent iGetPageText(int pageNum) {
			return new StringTextComponent("");
		}
	};
	
	public static final ResourceLocation BOOK_TEXTURES = new ResourceLocation("textures/gui/book.png");
	private ReadBookScreenMod.IBookInfo bookInfo;
	private int currPage;
	private List<ITextComponent> cachedPageLines = Collections.emptyList(); // cachedPageText? looks like it's split into lines
	private int cachedPage = -1;
	private ChangePageButton buttonNextPage;
	private ChangePageButton buttonPreviousPage;
	private final boolean pageTurnSounds;
	
	private long lastMessage = 0;

	// It seems like this gets called first in normal operation
	public ReadBookScreenMod(ReadBookScreenMod.IBookInfo bookInfoIn) {
		this(bookInfoIn, true);
	}

	/**
	 * I think this just creates a blank book
	 */
	public ReadBookScreenMod() {
		this(EMPTY_BOOK, false);
	}

	// Which calls this
	public ReadBookScreenMod(ReadBookScreenMod.IBookInfo bookInfoIn, boolean p_i51099_2_) {
		super(NarratorChatListener.EMPTY); // Empty string
		this.bookInfo = bookInfoIn;
		this.pageTurnSounds = p_i51099_2_;
	}
	
	
	private void rateLimitedDebugMessage(String message){
		if (System.currentTimeMillis() - this.lastMessage > 1000){
			System.out.println(message);
			this.lastMessage = System.currentTimeMillis();
		}
	}
	
	private void rateLimitedDebugMessage(int number){
		this.rateLimitedDebugMessage(String.valueOf(number));
	}


	public void func_214155_a(ReadBookScreenMod.IBookInfo p_214155_1_) {
		this.bookInfo = p_214155_1_;
		this.currPage = MathHelper.clamp(this.currPage, 0, p_214155_1_.getPageCount());
		this.updateButtons();
		this.cachedPage = -1;
	}

	/**
	 * Takes an integer
	 * 
	 * Param is almost certainly a page number
	 * 
	 * Seems like it might be called after changing pages to invalidate the cache and update buttons and page numbers?
	 * Then again it sets currPage
	 * 
	 * 
	 */
	public boolean showPage(int pageNum) {
		// Clamp the input to 0-max page index
		int i = MathHelper.clamp(pageNum, 0, this.bookInfo.getPageCount() - 1);
		
		if (i != this.currPage) {
			this.currPage = i;
			this.updateButtons();
			this.cachedPage = -1;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Takes an integer, probaly page number. Returns a boolean
	 * Maybe showPage moveToPage changeToPage something about going to page number?
	 */
	protected boolean showPage2(int pageNum) {
		return this.showPage(pageNum);
	}

	protected void init() {
		this.addDoneButton(); // Just adds the 'done' button
		this.addChangePageButtons(); // Adds the page buttons
		System.out.println("ReadBookScreenMod initialized!");
	}

	protected void addDoneButton() {
		this.addButton(new Button(this.width / 2 - 100, 196, 200, 20, I18n.format("gui.done"), (p_214161_1_) -> {
			this.minecraft.displayGuiScreen((Screen)null);
		}));
	}

	protected void addChangePageButtons() {
		int i = (this.width - 192) / 2;
		int j = 2;
		this.buttonNextPage = this.addButton(new ChangePageButton(i + 116, 159, true, (p_214159_1_) -> {
			this.nextPage();
		}, this.pageTurnSounds));
		this.buttonPreviousPage = this.addButton(new ChangePageButton(i + 43, 159, false, (p_214158_1_) -> {
			this.previousPage();
		}, this.pageTurnSounds));
		this.updateButtons();
	}

	/**
	 * getPageCount() ?
	 * @return
	 */
	private int getPageCount() {
		return this.bookInfo.getPageCount();
	}

	// Previous page
	protected void previousPage() {
		if (this.currPage > 0) {
			--this.currPage;
		}

		this.updateButtons();
	}

	// Next page
	protected void nextPage() {
		if (this.currPage < this.getPageCount() - 1) {
			++this.currPage;
		}

		this.updateButtons();
	}

	/**
	 * Update buttons?
	 */
	private void updateButtons() {
		this.buttonNextPage.visible = this.currPage < this.getPageCount() - 1;
		this.buttonPreviousPage.visible = this.currPage > 0;
	}

	/**
	 * @param p_keyPressed_1_ keyCode
	 * @param p_keyPressed_2_ scanCode
	 * @param p_keyPressed_3_ modifiers
	 */
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
			return true;
		} else {
			switch(p_keyPressed_1_) {
			case 266: // Page up
				this.buttonPreviousPage.onPress(); // probably previous page button
				return true;
			case 267: // Page down
				this.buttonNextPage.onPress();
				return true;
			default:
				return false;
			}
		}
	}

	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		//this.rateLimitedDebugMessage(cachedPageLines);
		this.renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BOOK_TEXTURES);
		int i = (this.width - 192) / 2;
		int j = 2;
		this.blit(i, 2, 0, 0, 192, 192);
		String s = I18n.format("book.pageIndicator", this.currPage + 1, Math.max(this.getPageCount(), 1));
		if (this.cachedPage != this.currPage) {
			ITextComponent itextcomponent = this.bookInfo.getPageText(this.currPage);
			this.cachedPageLines = RenderComponentsUtil.splitText(itextcomponent, 114, this.font, true, true);
		}

		this.cachedPage = this.currPage;
		int i1 = this.func_214156_a(s);
		this.font.drawString(s, (float)(i - i1 + 192 - 44), 18.0F, 0);
		int k = Math.min(128 / 9, this.cachedPageLines.size());

		for(int l = 0; l < k; ++l) {
			ITextComponent itextcomponent1 = this.cachedPageLines.get(l);
			this.font.drawString(itextcomponent1.getFormattedText(), (float)(i + 36), (float)(32 + l * 9), 0);
		}

		ITextComponent itextcomponent2 = this.func_214154_c((double)p_render_1_, (double)p_render_2_);
		if (itextcomponent2 != null) {
			this.renderComponentHoverEffect(itextcomponent2, p_render_1_, p_render_2_);
		}

		super.render(p_render_1_, p_render_2_, p_render_3_);
	}
	

	private int func_214156_a(String p_214156_1_) {
		return this.font.getStringWidth(this.font.getBidiFlag() ? this.font.bidiReorder(p_214156_1_) : p_214156_1_);
	}

	
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		if (p_mouseClicked_5_ == 0) {
			ITextComponent itextcomponent = this.func_214154_c(p_mouseClicked_1_, p_mouseClicked_3_);
			if (itextcomponent != null && this.handleComponentClicked(itextcomponent)) {
				return true;
			}
		}

		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}

	
	public boolean handleComponentClicked(ITextComponent p_handleComponentClicked_1_) {
		ClickEvent clickevent = p_handleComponentClicked_1_.getStyle().getClickEvent();
		if (clickevent == null) {
			return false;
		} else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
			String s = clickevent.getValue();

			try {
				int i = Integer.parseInt(s) - 1;
				return this.showPage2(i);
			} catch (Exception var5) {
				return false;
			}
		} else {
			boolean flag = super.handleComponentClicked(p_handleComponentClicked_1_);
			if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.minecraft.displayGuiScreen((Screen)null);
			}

			return flag;
		}
	}

	/**
	 * Called by mouseClicked() and render()
	 * Presumably it's finding the spot for the cursor within the text
	 * @param p_214154_1_ X?
	 * @param p_214154_3_ Y?
	 * @return
	 */
	@Nullable
	public ITextComponent func_214154_c(double p_214154_1_, double p_214154_3_) { // func_214154_c(double p_214154_1_, double p_214154_3_) {
		if (this.cachedPageLines == null) {
			return null;
		} else {
			int i = MathHelper.floor(p_214154_1_ - (double)((this.width - 192) / 2) - 36.0D);
			int j = MathHelper.floor(p_214154_3_ - 2.0D - 30.0D);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / 9, this.cachedPageLines.size());
				if (i <= 114 && j < 9 * k + k) {
					int l = j / 9;
					if (l >= 0 && l < this.cachedPageLines.size()) {
						ITextComponent itextcomponent = this.cachedPageLines.get(l);
						int i1 = 0;

						for(ITextComponent itextcomponent1 : itextcomponent) {
							if (itextcomponent1 instanceof StringTextComponent) {
								i1 += this.minecraft.fontRenderer.getStringWidth(itextcomponent1.getFormattedText());
								if (i1 > i) {
									return itextcomponent1;
								}
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	/**
	 * Converts pages as compound NBT to string list? Maybe converting old books to the new format?
	 * Only used in setting up bookinfos
	 * @param p_214157_0_
	 * @return
	 */
	public static List<String> nbtPagesToStrings(CompoundNBT p_214157_0_) {
		ListNBT listnbt = p_214157_0_.getList("pages", 8).copy();
		Builder<String> builder = ImmutableList.builder();

		for(int i = 0; i < listnbt.size(); ++i) {
			builder.add(listnbt.getString(i));
		}

		return builder.build();
	}

	@OnlyIn(Dist.CLIENT)
	public interface IBookInfo {
		
	  /**
		* getPageCount?
		* @return
		*/
		int getPageCount();

		/**
		 * 
		 * 
		 * @param pageNum pageNumber?
		 * @return
		 */
		ITextComponent iGetPageText(int pageNum);

		/**
		 * Another get page text? What
		 * 
		 * @param pageNum
		 * @return
		 */
		default ITextComponent getPageText(int pageNum) {
			return (ITextComponent)(pageNum >= 0 && pageNum < this.getPageCount() ? this.iGetPageText(pageNum) : new StringTextComponent(""));
		}

		static ReadBookScreenMod.IBookInfo func_216917_a(ItemStack p_216917_0_) {
			Item item = p_216917_0_.getItem();
			if (item == Items.WRITTEN_BOOK) {
				return new ReadBookScreenMod.WrittenBookInfo(p_216917_0_);
			} else {
				return (ReadBookScreenMod.IBookInfo)(item == Items.WRITABLE_BOOK ? new ReadBookScreenMod.UnwrittenBookInfo(p_216917_0_) : ReadBookScreenMod.EMPTY_BOOK);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class UnwrittenBookInfo implements ReadBookScreenMod.IBookInfo {
		private final List<String> pages;

		public UnwrittenBookInfo(ItemStack p_i50617_1_) {
			this.pages = func_216919_b(p_i50617_1_);
		}

		private static List<String> func_216919_b(ItemStack p_216919_0_) {
			CompoundNBT compoundnbt = p_216919_0_.getTag();
			return (List<String>)(compoundnbt != null ? ReadBookScreenMod.nbtPagesToStrings(compoundnbt) : ImmutableList.of());
		}

		/**
		 * getPageCount?
		 */
		public int getPageCount() {
			return this.pages.size();
		}

		public ITextComponent iGetPageText(int pageNum) {
			return new StringTextComponent(this.pages.get(pageNum));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class WrittenBookInfo implements ReadBookScreenMod.IBookInfo {
		private final List<String> pages; // pages?

		/**
		 * Converts ItemStack to WrittenBookInfo
		 * @param p_i50616_1_
		 */
		public WrittenBookInfo(ItemStack p_i50616_1_) {
			this.pages = func_216921_b(p_i50616_1_);
		}

		private static List<String> func_216921_b(ItemStack stack) {
			CompoundNBT compoundnbt = stack.getTag();
			return (List<String>)(compoundnbt != null && WrittenBookItem.validBookTagContents(compoundnbt) ? ReadBookScreenMod.nbtPagesToStrings(compoundnbt) : ImmutableList.of((new TranslationTextComponent("book.invalid.tag")).applyTextStyle(TextFormatting.DARK_RED).getFormattedText()));
		}

		/**
		 * getPageCount?
		 */
		public int getPageCount() {
			return this.pages.size();
		}

		/**
		 * getPageText?
		 */
		public ITextComponent iGetPageText(int pageNum) {
			String s = this.pages.get(pageNum);

			try {
				ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s);
				if (itextcomponent != null) {
					return itextcomponent;
				}
			} catch (Exception var4) {
				;
			}

			return new StringTextComponent(s);
		}
	}
}