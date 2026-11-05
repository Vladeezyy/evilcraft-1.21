package org.cyclops.evilcraftcompat.network.packet;

import com.google.common.collect.Lists;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.cyclops.evilcraftcompat.Reference;
import org.cyclops.evilcraftcompat.modcompat.jei.spiritfurnace.SpiritFurnaceRecipeJEI;

import java.util.List;

/**
 * Packet for sending the spirit furnace recipes from server to client.
 * Fabric: all recipes in one packet, on a channel of its own (registered only by clients with JEI).
 * @author rubensworks
 *
 */
public record JeiSpiritFurnaceRecipePacket(List<SpiritFurnaceRecipeJEI> recipes) implements CustomPacketPayload {

    public static final Type<JeiSpiritFurnaceRecipePacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "jei_spirit_furnace_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, JeiSpiritFurnaceRecipePacket> CODEC = StreamCodec.of(
            (output, packet) -> {
                output.writeVarInt(packet.recipes().size());
                for (SpiritFurnaceRecipeJEI recipe : packet.recipes()) {
                    SpiritFurnaceRecipeJEI.encode(recipe, output);
                }
            },
            input -> {
                int count = input.readVarInt();
                List<SpiritFurnaceRecipeJEI> recipes = Lists.newArrayListWithCapacity(count);
                for (int i = 0; i < count; i++) {
                    recipes.add(SpiritFurnaceRecipeJEI.decode(input));
                }
                return new JeiSpiritFurnaceRecipePacket(recipes);
            });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
