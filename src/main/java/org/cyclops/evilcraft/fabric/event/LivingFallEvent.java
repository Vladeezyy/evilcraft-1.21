package org.cyclops.evilcraft.fabric.event;

import net.minecraft.world.entity.LivingEntity;

/**
 * Before fall damage is applied to a living entity; the fall distance can be changed.
 */
public class LivingFallEvent extends LivingEvent {

    public static final ListenerList<LivingFallEvent> LISTENERS = new ListenerList<>();

    private float distance;
    private final float damageMultiplier;

    public LivingFallEvent(LivingEntity entity, float distance, float damageMultiplier) {
        super(entity);
        this.distance = distance;
        this.damageMultiplier = damageMultiplier;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }
}
