package org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.EntityConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

/**
 * Registers entity types; their renderers are registered by the config.
 * @author rubensworks
 */
public class EntityActionFabric<T extends Entity> extends ConfigurableTypeActionRegistry<EntityConfig<T>, EntityType<T>, ModBase<?>> {

}
