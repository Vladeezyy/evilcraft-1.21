package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.function.Consumer;

/**
 * Rendering of the in-game HUD (client only).
 */
@Environment(EnvType.CLIENT)
public class RenderGuiEvent {

    private final GuiGraphicsExtractor guiGraphics;
    private final DeltaTracker partialTick;

    public RenderGuiEvent(GuiGraphicsExtractor guiGraphics, DeltaTracker partialTick) {
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
    }

    public GuiGraphicsExtractor getGuiGraphics() {
        return guiGraphics;
    }

    public DeltaTracker getPartialTick() {
        return partialTick;
    }

    /**
     * After the HUD was rendered.
     */
    public static class Post extends RenderGuiEvent {
        public Post(GuiGraphicsExtractor guiGraphics, DeltaTracker partialTick) {
            super(guiGraphics, partialTick);
        }

        public static void addListener(Consumer<Post> listener) {
            HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> listener.accept(new Post(guiGraphics, deltaTracker)));
        }
    }
}
