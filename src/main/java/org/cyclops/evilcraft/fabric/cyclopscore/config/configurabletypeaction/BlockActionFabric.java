package org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionRegistry;
import org.cyclops.cyclopscore.item.ItemInformationProviderCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

/**
 * Registers blocks, their items and their flammability, and adds them to the creative tab.
 * @author rubensworks
 */
public class BlockActionFabric extends ConfigurableTypeActionRegistry<BlockConfig, Block, ModBase<?>> {

    public static void register(@Nullable BiFunction<BlockConfig, Block, ? extends Item> itemBlockConstructor, BlockConfig config, @Nullable Callable<?> callback) {
        register(config, itemBlockConstructor == null ? callback : null); // Delay the callback until the item has been registered if there is one
        if (itemBlockConstructor != null) {
            ItemConfig itemConfig = new ItemConfig(config.getMod(), config.getNamedId(), (iConfig) -> {
                Item itemBlock = itemBlockConstructor.apply(config, config.getInstance());
                Objects.requireNonNull(itemBlock, "Received a null item for the item block constructor of " + config.getNamedId());
                return itemBlock;
            });
            config.getMod().getConfigHandler().registerToRegistry(BuiltInRegistries.ITEM, itemConfig, () -> {
                config.setItemInstance(itemConfig.getInstance());
                if (callback != null) {
                    callback.call();
                }
                return null;
            });
        }
    }

    @Override
    public void onRegisterForgeFilled(BlockConfig eConfig) {
        register(eConfig.getItemConstructor(), eConfig, () -> {
            eConfig.onForgeRegistered(); // Manually call after item has been registered
            this.polish(eConfig);
            return null;
        });
    }

    protected void polish(BlockConfig config) {
        for (ItemStack itemStack : config.getDefaultCreativeTabEntriesPublic()) {
            config.getMod().registerDefaultCreativeTabEntry(itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        Item item = config.getItemInstance();
        if (item != null) {
            ItemInformationProviderCommon.registerItem(item);
        }
        registerFlammability(config.getInstance());
    }

    /**
     * Blocks of this mod declare NeoForge's getFlammability and getFireSpreadSpeed (constant values), Fabric uses its
     * flammable block registry instead.
     */
    protected static void registerFlammability(Block block) {
        Method flammability = findMethod(block.getClass(), "getFlammability");
        Method spreadSpeed = findMethod(block.getClass(), "getFireSpreadSpeed");
        if (flammability != null && spreadSpeed != null) {
            try {
                int burn = (int) spreadSpeed.invoke(block, block.defaultBlockState(), null, null, null);
                int spread = (int) flammability.invoke(block, block.defaultBlockState(), null, null, null);
                FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Could not read the flammability of " + block, e);
            }
        }
    }

    @Nullable
    private static Method findMethod(Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null && c != Block.class; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                if (method.getName().equals(name) && method.getParameterCount() == 4 && method.getReturnType() == int.class) {
                    method.setAccessible(true);
                    return method;
                }
            }
        }
        return null;
    }
}
