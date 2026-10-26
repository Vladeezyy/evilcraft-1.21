package org.cyclops.evilcraft.fabric.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Damage to a living entity's health, after armor, enchantments and absorption.
 * {@link Pre} can change the damage, {@link Post} is after it was applied.
 */
public abstract class LivingDamageEvent {

    private final LivingEntity entity;

    protected LivingDamageEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public static class Pre extends LivingDamageEvent {
        public static final ListenerList<Pre> LISTENERS = new ListenerList<>();

        private final DamageContainer container;

        public Pre(LivingEntity entity, DamageContainer container) {
            super(entity);
            this.container = container;
        }

        public DamageContainer getContainer() {
            return container;
        }
    }

    public static class Post extends LivingDamageEvent {
        public static final ListenerList<Post> LISTENERS = new ListenerList<>();

        private final DamageSource source;
        private final float originalDamage;
        private final float newDamage;

        public Post(LivingEntity entity, DamageSource source, float originalDamage, float newDamage) {
            super(entity);
            this.source = source;
            this.originalDamage = originalDamage;
            this.newDamage = newDamage;
        }

        public DamageSource getSource() {
            return source;
        }

        public float getOriginalDamage() {
            return originalDamage;
        }

        public float getNewDamage() {
            return newDamage;
        }
    }

    public static class DamageContainer {
        private final DamageSource source;
        private final float originalDamage;
        private float newDamage;

        public DamageContainer(DamageSource source, float damage) {
            this.source = source;
            this.originalDamage = damage;
            this.newDamage = damage;
        }

        public DamageSource getSource() {
            return source;
        }

        public float getOriginalDamage() {
            return originalDamage;
        }

        public float getNewDamage() {
            return newDamage;
        }

        public void setNewDamage(float newDamage) {
            this.newDamage = newDamage;
        }
    }

    /**
     * Called with the health damage of an entity; returns the (possibly changed) damage.
     */
    public static float firePre(LivingEntity entity, DamageSource source, float damage) {
        if (Pre.LISTENERS.isEmpty()) {
            return damage;
        }
        return Pre.LISTENERS.post(new Pre(entity, new DamageContainer(source, damage))).getContainer().getNewDamage();
    }

    public static void firePost(LivingEntity entity, DamageSource source, float originalDamage, float damage) {
        if (damage > 0 && !Post.LISTENERS.isEmpty()) {
            Post.LISTENERS.post(new Post(entity, source, originalDamage, damage));
        }
    }
}
