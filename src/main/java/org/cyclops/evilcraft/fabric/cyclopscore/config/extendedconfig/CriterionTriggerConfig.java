package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;


import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

/**
 * Config for recipe conditions.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public abstract class CriterionTriggerConfig<T extends CriterionTriggerInstance> extends ExtendedConfigForge<CriterionTriggerConfig<T>, CriterionTrigger<T>> {
    public CriterionTriggerConfig(ModBase<?> mod, String namedId, CriterionTrigger<T> criterionTrigger) {
        super(mod, namedId, (eConfig) -> criterionTrigger);
    }

    @Override
    public String getTranslationKey() {
        return "recipecondition." + getMod().getModId() + "." + getNamedId();
    }

    // Needed for config gui
    @Override
    public String getFullTranslationKey() {
        return getTranslationKey();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.CRITERION_TRIGGER;
    }

    @Override
    public Registry<? super CriterionTrigger<T>> getRegistry() {
        return BuiltInRegistries.TRIGGER_TYPES;
    }
}
