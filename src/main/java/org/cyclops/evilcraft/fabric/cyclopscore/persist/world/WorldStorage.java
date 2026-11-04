package org.cyclops.evilcraft.fabric.cyclopscore.persist.world;



import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.cyclops.evilcraft.fabric.ServerLifecycleHooks;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;

/**
 * Instances of this can store data inside the world NBT.
 *
 * @author rubensworks
 */
public abstract class WorldStorage<T extends WorldStorage> extends SavedData {

    /**
     * Called after the data is loaded from the world storage.
     */
    public void afterLoad() {

    }

    /**
     * Called before the data is saved to the world storage.
     */
    public void beforeSave() {

    }

    public static abstract class Access<T extends WorldStorage<T>> {

        private final SavedDataType<T> savedDataType;
        protected final ModBaseNeoForge<?> mod;

        public Access(SavedDataType<T> savedDataType, ModBaseNeoForge<?> mod) {
            this.savedDataType = savedDataType;
            this.mod = mod;
        }

        public T get(MinecraftServer server) {
            return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(savedDataType);
        }

        public T get() {
            return get(ServerLifecycleHooks.getCurrentServer());
        }

        /**
         * When a server is started.
         *
         * @param server The server.
         */
        public void onStartedEvent(MinecraftServer server) {
            T data = get(server);
            data.afterLoad();
        }

        /**
         * When a server is stopping.
         *
         * @param server The server.
         */
        public void onStoppingEvent(MinecraftServer server) {
            T data = get(server);
            data.beforeSave();
        }
    }

}
