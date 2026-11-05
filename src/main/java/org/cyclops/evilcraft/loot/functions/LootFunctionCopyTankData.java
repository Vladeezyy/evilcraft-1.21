package org.cyclops.evilcraft.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.transfer.ItemAccess;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;
import org.cyclops.evilcraft.fabric.transfer.Transaction;
import org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid.IFluidHandlerCapacity;
import org.cyclops.evilcraft.fabric.cyclopscore.fluid.SingleUseTank;
import org.cyclops.evilcraft.core.blockentity.BlockEntityTankInventory;

import java.util.List;
import java.util.Optional;

/**
 * Copies fluid tank data to the item.
 * @author rubensworks
 */
public class LootFunctionCopyTankData extends LootItemConditionalFunction {
    public static final MapCodec<LootFunctionCopyTankData> CODEC = RecordCodecBuilder.mapCodec(
            builder -> commonFields(builder).apply(builder, LootFunctionCopyTankData::new)
    );
    public static final LootItemFunctionType TYPE = new LootItemFunctionType(LootFunctionCopyTankData.CODEC);

    protected LootFunctionCopyTankData(List<LootItemCondition> conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        if (lootContext.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BlockEntityTankInventory tile) {
            SingleUseTank fluidHandlerTile = tile.getTank();
            ItemAccess itemAccess = ItemAccess.forStack(itemStack);
            return Optional.ofNullable(itemAccess.getCapability(Capabilities.Fluid.ITEM))
                    .map(fluidHandlerItem -> {
                        try (var tx = Transaction.openRoot()) {
                            if (fluidHandlerItem instanceof IFluidHandlerCapacity) {
                                ((IFluidHandlerCapacity) fluidHandlerItem).setTankCapacity(0, fluidHandlerTile.getTankCapacity(0), tx);
                            }
                            FluidResource resource = fluidHandlerTile.getResource(0);
                            if (!resource.isEmpty()) {
                                fluidHandlerItem.insert(resource, fluidHandlerTile.getFluidAmount(), tx);
                            }
                            tx.commit();
                        }
                        return itemAccess.getResource().toStack(itemAccess.getAmount());
                    }).orElse(itemStack);
        }
        return itemStack;
    }

    @Override
    public LootItemFunctionType getType() {
        return TYPE;
    }
}
