package org.cyclops.evilcraft.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.cyclopscore.helper.Helpers;

/**
 * Blood reactant.
 *
 * @author rubensworks
 *
 */
public class ItemPromiseAcceptor extends Item {

    private final Type type;

    public ItemPromiseAcceptor(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    public Type getType() {
        return this.type;
    }

    @Environment(EnvType.CLIENT)
    public static class ItemColor implements net.minecraft.client.color.item.ItemColor {
        @Override
        public int getColor(ItemStack itemStack, int renderPass) {
            return ((ItemPromiseAcceptor) itemStack.getItem()).getType().getColor();
        }
    }

    public static enum Type {
        IRON("iron", Helpers.RGBAToInt(255, 255, 255, 255)),
        GOLD("gold", Helpers.RGBAToInt(230, 230, 160, 255)),
        DIAMOND("diamond", Helpers.RGBAToInt(150, 250, 200, 255));

        private final String name;
        private final int color;

        Type(String name, int color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public int getColor() {
            return color;
        }
    }

}
