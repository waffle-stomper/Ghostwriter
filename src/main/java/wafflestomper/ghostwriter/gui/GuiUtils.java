package wafflestomper.ghostwriter.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class GuiUtils {

    // Helper function so that we don't have to update the code in a million places every time they change buttons
    public static Button buttonFactory(int x, int y, int width, int height, String text, Button.OnPress onPress) {
        return Button.builder(
                Component.translatable(text),
                onPress
        ).bounds(
                x, y, width, height
        ).build();
    }
}