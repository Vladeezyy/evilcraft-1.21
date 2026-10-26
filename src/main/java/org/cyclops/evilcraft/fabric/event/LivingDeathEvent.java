package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

/**
 * When a living entity dies (server side), from Fabric's after death event.
 */
public class LivingDeathEvent {

    private final LivingEntity entity;
    private final DamageSource source;

    public LivingDeathEvent(LivingEntity entity, DamageSource source) {
        this.entity = entity;
        this.source = source;
    }

    public static void addListener(Consumer<LivingDeathEvent> listener) {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> listener.accept(new LivingDeathEvent(entity, source)));
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public DamageSource getSource() {
        return source;
    }
}
