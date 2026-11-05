package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;
import org.cyclops.evilcraft.Reference;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

/**
 * Rendering of the in-game HUD (client only).
 */
@Environment(EnvType.CLIENT)
public class RenderGuiEvent {

    private final GuiGraphics guiGraphics;
    private final DeltaTracker partialTick;

    public RenderGuiEvent(GuiGraphics guiGraphics, DeltaTracker partialTick) {
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public DeltaTracker getPartialTick() {
        return partialTick;
    }

    /**
     * After the HUD was rendered.
     */
    public static class Post extends RenderGuiEvent {
        public Post(GuiGraphics guiGraphics, DeltaTracker partialTick) {
            super(guiGraphics, partialTick);
        }

        private static int nextId = 0;

        public static void addListener(Consumer<Post> listener) {
            HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "overlay" + (nextId++)),
                    (guiGraphics, deltaTracker) -> listener.accept(new Post(guiGraphics, deltaTracker)));
        }
    }
}
