package org.cyclops.evilcraft.core.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.core.entity.item.EntityThrowable;

/**
 * A renderer for a throwable item.
 * @author rubensworks
 *
 */
@Environment(EnvType.CLIENT)
public class RenderThrowable extends ThrownItemRenderer<EntityThrowable> {

    public RenderThrowable(EntityRendererProvider.Context context) {
        super(context);
    }

}
