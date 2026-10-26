package org.cyclops.evilcraft.entity.villager;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.npc.VillagerTrades;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import org.cyclops.evilcraft.fabric.event.ModLifecycle;
import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.VillagerConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.Reference;
import org.cyclops.evilcraft.RegistryEntries;

/**
 * Config for the werewolf villager profession.
 * @author rubensworks
 */
public class VillagerProfessionWerewolfConfig extends VillagerConfig {

    @ConfigurablePropertyCommon(category = "mob", comment = "If villagers struck by lightning should have a 50% chance of becoming a werewolf villager", isCommandable = true)
    public static boolean convertOnLightning = true;

    public VillagerProfessionWerewolfConfig() {
        super(
                EvilCraft._instance,
                "werewolf",
                eConfig -> new VillagerProfession(
                        ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, eConfig.getNamedId()).toString(),
                        (poiType) -> poiType.is(RegistryEntries.POI_WEREWOLVIAN),
                        (poiType) -> poiType.is(RegistryEntries.POI_WEREWOLVIAN),
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        SoundEvents.VILLAGER_WORK_BUTCHER
                )
        );
        ModLifecycle.afterRegistries(this::registerTrades);
    }

    public void registerTrades() {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = new Int2ObjectOpenHashMap<>();
        for (int level = 1; level <= 5; level++) {
            trades.put(level, Lists.newArrayList());
        }
        {
            // Villager accepts these for emeralds
            trades.get(2).add(new EmeraldForItemsTrade(RegistryEntries.ITEM_DARK_GEM.get(), 10, 50, 2));
            trades.get(2).add(new EmeraldForItemsTrade(RegistryEntries.ITEM_HARDENED_BLOOD_SHARD.get(), 20, 50, 2));
            trades.get(2).add(new EmeraldForItemsTrade(RegistryEntries.ITEM_POISON_SAC.get(), 3, 50, 2));
            trades.get(3).add(new EmeraldForItemsTrade(RegistryEntries.ITEM_BLOOK.get(), 2, 30, 5));
            trades.get(3).add(new EmeraldForItemsTrade(RegistryEntries.ITEM_INVERTED_POTENTIA.get(), 2, 30, 6));
            trades.get(4).add(new EmeraldForItemsTrade(RegistryEntries.ITEM_INVERTED_POTENTIA_EMPOWERED.get(), 1, 25, 10));
            trades.get(5).add(new EmeraldForItemsTrade(RegistryEntries.ITEM_BLOOD_INFUSION_CORE.get(), 3, 20, 10));
            trades.get(5).add(new EmeraldForItemsTrade(RegistryEntries.ITEM_WEREWOLF_BONE.get(), 2, 20, 20));
            trades.get(5).add(new EmeraldForItemsTrade(RegistryEntries.ITEM_WEREWOLF_FUR.get(), 1, 20, 20));

            // Villager offers these for emeralds
            // Args: output item, emeralds, items, xp
            trades.get(2).add(new ItemsForEmeraldsTrade(RegistryEntries.ITEM_DARK_GEM_CRUSHED.get(), 1, 5, 10));
            trades.get(3).add(new ItemsForEmeraldsTrade(RegistryEntries.ITEM_UNDEAD_SAPLING.get(), 1, 3, 10));
            trades.get(4).add(new ItemsForEmeraldsTrade(RegistryEntries.ITEM_VENGEANCE_FOCUS.get(), 3, 2, 10));
            trades.get(4).add(new ItemsForEmeraldsTrade(RegistryEntries.ITEM_BOX_OF_ETERNAL_CLOSURE.get(), 7, 1, 10));
            trades.get(5).add(new ItemsForEmeraldsTrade(RegistryEntries.ITEM_GARMONBOZIA.get(), 10, 1, 30));
        }
        trades.forEach((level, listings) -> TradeOfferHelper.registerVillagerOffers(getInstance(), level, factories -> factories.addAll(listings)));
    }

}
