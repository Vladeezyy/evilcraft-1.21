package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.BlockClientConfig;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.IModelProviderConfig;


import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * Config for items.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public class ItemConfig extends ExtendedConfigForge<ItemConfig, Item> implements IModelProviderConfig {


    /**
     * Make a new instance.
     * @param mod     The mod instance.
     * @param namedId The unique name ID for the configurable.
     * @param elementConstructor The element constructor.
     */
    public ItemConfig(ModBase<?> mod, String namedId, Function<ItemConfig, ? extends Item> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getModelName(ItemStack itemStack) {
        return getNamedId();
    }

    @Override
    public String getTranslationKey() {
        return "item." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public String getFullTranslationKey() {
        return getTranslationKey();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.ITEM;
    }

    public Collection<ItemStack> getDefaultCreativeTabEntriesPublic() { // TODO: rm in next major, and make other method public
        return this.getDefaultCreativeTabEntries();
    }

    protected Collection<ItemStack> getDefaultCreativeTabEntries() {
        return Collections.singleton(new ItemStack(getInstance()));
    }

    @Override
    public Registry<? super Item> getRegistry() {
        return BuiltInRegistries.ITEM;
    }

}
