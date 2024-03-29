package wafflestomper.ghostwriter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.client.event.ScreenEvent.Opening;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import wafflestomper.ghostwriter.gui.screen.GhostwriterEditBookScreen;
import wafflestomper.ghostwriter.gui.screen.GhostwriterLecternScreen;
import wafflestomper.ghostwriter.gui.screen.GhostwriterReadBookScreen;
import wafflestomper.ghostwriter.utilities.Clipboard;
import wafflestomper.ghostwriter.utilities.FileHandler;
import wafflestomper.ghostwriter.utilities.Printer;

import java.io.File;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;


@Mod("ghostwriter")
public class Ghostwriter {
	public static final Clipboard GLOBAL_CLIPBOARD = new Clipboard();
	public static final Printer PRINTER = new Printer();
	public static final Logger LOG = LogManager.getLogger();
	public static final FileHandler FILE_HANDLER = new FileHandler(GLOBAL_CLIPBOARD);
	private static final Minecraft MC = Minecraft.getInstance();
	public static File currentPath;
	private boolean lecternArmed = false;
	
	
	public Ghostwriter() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	
	private void setup(final FMLClientSetupEvent event) {
		LOG.info("Setting up...");
	}
	
	
	/**
	 * This swaps the book on a lectern for the Ghostwriter equivalent
	 */
	@SubscribeEvent
	public void tick(TickEvent event) {
		if (MC.screen == null) return;
		if (!lecternArmed || !MC.screen.getClass().equals(LecternScreen.class)) return;
		
		lecternArmed = false;
		LOG.debug("Lectern screen detected!");
		
		if (MC.player == null) {
			LOG.error("Aborting GUI replacement because the player is null");
			return;
		} else if (MC.player.isCrouching()) {
			LOG.debug("Aborting GUI replacement because the player is crouching");
			return;
		}
		
		LecternScreen ls = (LecternScreen) MC.screen;
		ItemStack bookStack = ls.getMenu().getBook();  // TODO check both of these for null
		LOG.info("Swapping LecternScreen for GhostwriterLecternScreen...");
		
		Item bookItem = bookStack.getItem();
		if (!(bookItem instanceof WritableBookItem) && !(bookItem instanceof WrittenBookItem)) {
			LOG.error("Unknown book type on lectern!");
			return;
		}
		
		LOG.debug("Replacing the current screen with a GhostwriterLecternScreen");
		GhostwriterLecternScreen g = new GhostwriterLecternScreen(
				bookStack,
				ls.getMenu(),
				MC.player.getInventory()
		);
		MC.setScreen(g);
		
		LOG.debug("Lectern GUI swap done!");
	}
	
	
	/**
	 * This swaps the default book GUI for the Ghostwriter screen before it loads
	 */
	@SubscribeEvent
	public void guiOpen(Opening event) {
		Screen eventGui = event.getScreen();
		if (eventGui == null) {
			return;
		}
		LOG.debug("GUIOpenEvent: " + eventGui.toString());
		
		if (!eventGui.getClass().equals(BookEditScreen.class) && !eventGui.getClass().equals(BookViewScreen.class) &&
				!eventGui.getClass().equals(LecternScreen.class)) {
			return;
		}
		
		if (MC.player == null) {
			LOG.error("Minecraft.player is null. Cannot continue with GUI swap");
			return;
		} else if (MC.player.isCrouching()) {
			LOG.debug("Aborting GUI replacement because the player is crouching");
			return;
		} else if (eventGui instanceof LecternScreen) {
			LOG.info("Aborting early GUI replacement (target is a lectern). Setting lectern swap flag");
			LOG.warn("Lectern support has temporarily been disabled until I have more time to debug it.");
			//lecternArmed = true;
			return;
		}
		
		ItemStack bookStack = MC.player.getItemInHand(InteractionHand.MAIN_HAND);
		
		// Finally, do the GUI replacement
		if (eventGui instanceof BookEditScreen) {
			LOG.debug("Replacing the current screen with a GhostwriterEditBookScreen");
			eventGui = new GhostwriterEditBookScreen(MC.player, bookStack, InteractionHand.MAIN_HAND);
		} else {
			LOG.debug("Replacing the current screen with a GhostwriterReadBookScreen");
			BookViewScreen.WrittenBookAccess bookInfo = new BookViewScreen.WrittenBookAccess(bookStack);
			eventGui = new GhostwriterReadBookScreen(bookInfo, bookStack);
		}
		event.setNewScreen(eventGui);
		LOG.debug("GUI swap done!");
	}
	
	
	// Fix by The Matt Crawford to reveal the hidden cursor when you hit a breakpoint
	// https://intellij-support.jetbrains.com/hc/en-us/community/posts/360007479999-If-application-window-is-in-focus-
	// when-breakpoint-is-hit-keyboard-and-mouse-focus-will-not-shift-to-any-other-window
	// To use it, you just need to call it from your breakpoint's condition field.
	// Note that you can import this class into the condition if your breakpoint isn't in the ghostwriter namespace
	public static boolean debuggerReleaseControl() {
		GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		return true;
	}
}
