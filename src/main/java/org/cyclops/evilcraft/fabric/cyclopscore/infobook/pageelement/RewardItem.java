package org.cyclops.evilcraft.fabric.cyclopscore.infobook.pageelement;



import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.evilcraft.fabric.cyclopscore.infobook.AdvancedButton;
import org.cyclops.evilcraft.fabric.cyclopscore.infobook.IInfoBook;
import org.cyclops.evilcraft.fabric.cyclopscore.infobook.ScreenInfoBook;

/**
 * An item reward.
 * @author rubensworks
 */
public class RewardItem implements IReward {

    protected static final int SLOT_SIZE = 16;

    private final ItemStack itemStack;

    public RewardItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean canObtain(Player player) {
        return true;
    }

    @Override
    public void obtain(Player player) {
        if (!player.getInventory().add(itemStack.copy())) {
            ItemStackHelpers.spawnItemStack(player.getCommandSenderWorld(), player.blockPosition(), itemStack.copy());
        }
    }

    @Override
    public int getWidth() {
        return SLOT_SIZE + 4;
    }

    @Override
    public int getHeight() {
        return SLOT_SIZE + 4;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AdvancedButton createButton(IInfoBook infoBook) {
        return new RecipeAppendix.ItemButton(infoBook);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawElementInner(ScreenInfoBook gui, GuiGraphics guiGraphics, int x, int y, int width, int height, int page, int mx, int my, AdvancedButton button) {
        RecipeAppendix.renderItemForButton(gui, guiGraphics, x, y, itemStack, mx, my, true, (RecipeAppendix.ItemButton) button);
    }


}
