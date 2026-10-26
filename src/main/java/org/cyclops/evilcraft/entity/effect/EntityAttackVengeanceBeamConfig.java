package org.cyclops.evilcraft.entity.effect;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.EntityConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.core.client.render.RenderNull;

/**
 * Config for the {@link EntityAttackVengeanceBeam}.
 * @author rubensworks
 *
 */
public class EntityAttackVengeanceBeamConfig extends EntityConfig<EntityAttackVengeanceBeam> {

    @ConfigurablePropertyCommon(category = "entity", comment = "If crossed beams should cause explosions.", isCommandable = true)
    public static boolean crossBeamsExplosions = true;

    public EntityAttackVengeanceBeamConfig() {
        super(
                EvilCraft._instance,
                "attack_vengeance_beam",
                eConfig -> EntityType.Builder.<EntityAttackVengeanceBeam>of(EntityAttackVengeanceBeam::new, MobCategory.MISC)
        );
    }

    @Environment(EnvType.CLIENT)
    @Override
    public EntityRenderer<Entity> getRender(EntityRendererProvider.Context renderContext, ItemRenderer renderItem) {
        return new RenderNull(renderContext);
    }

}
