package org.cyclops.evilcraft.fabric.event;

import net.minecraft.world.entity.LivingEntity;

/**
 * Living entity events.
 */
public class LivingEvent {

    private final LivingEntity entity;

    public LivingEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * After a living entity jumped.
     */
    public static class LivingJumpEvent extends LivingEvent {
        public static final ListenerList<LivingJumpEvent> LISTENERS = new ListenerList<>();

        public LivingJumpEvent(LivingEntity entity) {
            super(entity);
        }
    }
}
