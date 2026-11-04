package org.cyclops.evilcraft.metadata;

import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.fabric.cyclopscore.metadata.IRegistryExportableRegistry;

public class RegistryExportables {

    public static IRegistryExportableRegistry REGISTRY = EvilCraft._instance.getRegistryManager()
            .getRegistry(IRegistryExportableRegistry.class);

    public static void load() {
        REGISTRY.register(new RegistryExportableBloodInfuserRecipe());
        REGISTRY.register(new RegistryExportableEnvirAccRecipe());
        REGISTRY.register(new RegistryExportableBroomModifier());
    }

}
