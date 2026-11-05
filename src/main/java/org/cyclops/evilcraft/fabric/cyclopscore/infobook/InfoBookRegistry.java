package org.cyclops.evilcraft.fabric.cyclopscore.infobook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.cyclops.cyclopscore.helper.IModHelpers;



import com.google.common.collect.Maps;
import org.apache.logging.log4j.Level;
import org.cyclops.evilcraft.fabric.cyclopscore.helper.RecipeHelpers;
import org.cyclops.evilcraft.fabric.cyclopscore.infobook.pageelement.AdvancementRewards;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Registry for info books for a mod.
 * @author rubensworks
 */
public class InfoBookRegistry implements IInfoBookRegistry {

    static {
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (client) {
                onClientTagsLoadedStatic();
            }
        });
        ServerLifecycleEvents.SERVER_STARTED.register(InfoBookRegistry::onServerStartedStatic);
    }

    private final Map<IInfoBook, String> bookPaths = Maps.newIdentityHashMap();
    private final Map<IInfoBook, InfoSection> bookRoots = Maps.newIdentityHashMap();
    private final Queue<SectionInjection> sectionInjections = new LinkedBlockingQueue<>(); // Thread-safe queue

    public InfoBookRegistry() {
        if (IModHelpers.get().getMinecraftHelpers().isClientSide()) {
            registerClient();
        }
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
    }

    @Override
    public void registerInfoBook(IInfoBook infoBook, String path) {
        bookPaths.put(infoBook, path);
    }

    @Override
    public void registerSection(ModBaseNeoForge<?> mod, IInfoBook infoBook, String parentSection, String sectionPath) {
        synchronized (sectionInjections) {
            sectionInjections.add(new SectionInjection(Objects.requireNonNull(mod), infoBook, parentSection, sectionPath));
        }
    }

    @Override
    public InfoSection getRoot(IInfoBook infoBook) {
        return bookRoots.get(infoBook);
    }

    // Reset achievement rewards to avoid remembering stuff across different servers and client worlds.
    // We have this in static methods to make sure that this is called only once per server start,
    // to avoid it being called for every infobook registry.
    public static void onClientTagsLoadedStatic() {
        AdvancementRewards.reset();
    }
    public static void onServerStartedStatic(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            // Only call this on dedicated servers, as the RecipesUpdatedEvent won't be emitted there
            AdvancementRewards.reset();
        }
    }

    @Environment(EnvType.CLIENT)
    private void registerClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> onPlayerLoggedIn());
    }

    public void onPlayerLoggedIn() {
        RecipeHelpers.reset();
        initializeAllBooks(false);
    }

    public void onServerStarted(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            initializeAllBooks(false);
        }
    }

    @Override
    public void initializeAllBooks(boolean reload) {
        // Load after recipes are loaded client-side
        for (Map.Entry<IInfoBook, String> entry : bookPaths.entrySet()) {
            if (!reload) {
                entry.getKey().getMod().log(Level.INFO, "Loading infobook " + entry.getValue());
            }
            bookRoots.put(entry.getKey(), InfoBookParser.initializeInfoBook(entry.getKey().getMod(), entry.getKey(), entry.getValue(), null));
            // Reset the infobook history
            entry.getKey().setCurrentSection(null);
        }

        // Load section injections
        for (SectionInjection sectionInjection : sectionInjections) {
            InfoSection section = sectionInjection.getInfoBook().getSection(sectionInjection.getParentSection());
            if (section == null) {
                throw new IllegalArgumentException(String.format("Could not find section '%s' in infobook '%s'.", sectionInjection.getParentSection(), sectionInjection.getInfoBook()));
            }
            section.registerSection(InfoBookParser.initializeInfoBook(sectionInjection.getMod(), sectionInjection.getInfoBook(), sectionInjection.getSectionPath(), section));
        }
    }

    private static final class SectionInjection {
        private final ModBaseNeoForge<?> mod;
        private final IInfoBook infoBook;
        private final String parentSection;
        private final String sectionPath;

        private SectionInjection(ModBaseNeoForge<?> mod, IInfoBook infoBook, String parentSection, String sectionPath) {
            this.mod = mod;
            this.infoBook = Objects.requireNonNull(infoBook);
            this.parentSection = parentSection;
            this.sectionPath = sectionPath;
        }

        public ModBaseNeoForge<?> getMod() {
            return mod;
        }

        public IInfoBook getInfoBook() {
            return infoBook;
        }

        public String getParentSection() {
            return parentSection;
        }

        public String getSectionPath() {
            return sectionPath;
        }
    }

}
