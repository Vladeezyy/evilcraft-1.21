package org.cyclops.evilcraft.fabric.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

/**
 * Before an entity is struck by lightning; canceling it prevents {@link Entity#thunderHit}.
 */
public class EntityStruckByLightningEvent extends CancellableEvent {

    public static final ListenerList<EntityStruckByLightningEvent> LISTENERS = new ListenerList<>();

    private final Entity entity;
    private final LightningBolt lightning;

    public EntityStruckByLightningEvent(Entity entity, LightningBolt lightning) {
        this.entity = entity;
        this.lightning = lightning;
    }

    public Entity getEntity() {
        return entity;
    }

    public LightningBolt getLightning() {
        return lightning;
    }
}
