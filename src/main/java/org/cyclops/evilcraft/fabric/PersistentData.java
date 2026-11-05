package org.cyclops.evilcraft.fabric;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.cyclops.evilcraft.Reference;

/**
 * Custom data that is saved with an entity, as Fabric data attachments: like NeoForge's persistent data and the
 * part of it that players keep when they die.
 */
public final class PersistentData {

    private static final AttachmentType<CompoundTag> DATA = AttachmentRegistry.<CompoundTag>builder()
            .persistent(CompoundTag.CODEC)
            .initializer(CompoundTag::new)
            .buildAndRegister(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "persistent_data"));
    private static final AttachmentType<CompoundTag> PERSISTED = AttachmentRegistry.<CompoundTag>builder()
            .persistent(CompoundTag.CODEC)
            .initializer(CompoundTag::new)
            .copyOnDeath()
            .buildAndRegister(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "player_persisted_data"));

    private PersistentData() {
    }

    public static void load() {
        // Registers the attachment types
    }

    /**
     * @return The (mutable) custom data of an entity.
     */
    public static CompoundTag get(Entity entity) {
        return entity.getAttachedOrCreate(DATA);
    }

    /**
     * @return The (mutable) custom data of a player that is kept when it dies.
     */
    public static CompoundTag getPersisted(Entity entity) {
        return entity.getAttachedOrCreate(PERSISTED);
    }
}
