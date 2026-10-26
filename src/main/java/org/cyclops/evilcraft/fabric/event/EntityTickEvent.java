package org.cyclops.evilcraft.fabric.event;

import net.minecraft.world.entity.Entity;
import org.cyclops.cyclopscore.events.IEntityTickEvent;

import java.util.function.Consumer;

/**
 * After an entity has been ticked (both sides), from Cyclops Core's entity tick hook.
 */
public class EntityTickEvent {

    private final Entity entity;

    public EntityTickEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class Pre extends EntityTickEvent {
        public Pre(Entity entity) {
            super(entity);
        }

        /**
         * Fabric has no hook before an entity tick: this is called after it, like {@link Post}.
         */
        public static void addListener(Consumer<Pre> listener) {
            IEntityTickEvent.EVENT.register(entity -> listener.accept(new Pre(entity)));
        }
    }

    public static class Post extends EntityTickEvent {
        public Post(Entity entity) {
            super(entity);
        }

        public static void addListener(Consumer<Post> listener) {
            IEntityTickEvent.EVENT.register(entity -> listener.accept(new Post(entity)));
        }
    }
}
