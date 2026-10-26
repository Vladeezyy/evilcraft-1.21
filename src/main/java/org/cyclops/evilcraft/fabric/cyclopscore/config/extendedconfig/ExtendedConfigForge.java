package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.Function;

/**
 * A config with a registry entry.
 * @author rubensworks
 */
public abstract class ExtendedConfigForge<C extends ExtendedConfigCommon<C, I, ModBase<?>>, I> extends ExtendedConfigRegistry<C, I, ModBase<?>> {
    public ExtendedConfigForge(ModBase<?> mod, String namedId, Function<C, ? extends I> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public ModBase<?> getMod() {
        return super.getMod();
    }
}
