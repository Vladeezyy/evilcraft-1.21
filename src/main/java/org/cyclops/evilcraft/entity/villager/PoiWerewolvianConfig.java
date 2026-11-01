package org.cyclops.evilcraft.entity.villager;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import org.cyclops.cyclopscore.helper.IStructureHelpers;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import org.cyclops.cyclopscore.config.extendedconfig.PoiConfigCommon;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;

import java.util.HashSet;

/**
 * Config for the werewolf villager profession.
 * @author rubensworks
 */
public class PoiWerewolvianConfig extends PoiConfigCommon<EvilCraft> {

    public PoiWerewolvianConfig() {
        super(
                EvilCraft._instance,
                "werewolvian",
                eConfig -> {
                    HashSet<BlockState> blockStates = Sets.newHashSet();
                    blockStates.addAll(RegistryEntries.BLOCK_BLOOD_INFUSER.value().getStateDefinition().getPossibleStates());
                    blockStates.addAll(RegistryEntries.BLOCK_BLOOD_CHEST.value().getStateDefinition().getPossibleStates());
                    blockStates.addAll(RegistryEntries.BLOCK_PURIFIER.value().getStateDefinition().getPossibleStates());
                    return new PoiType(blockStates, 1, 1);
                }
        );
    }

    @Override
    public void onRegistered() {
        super.onRegistered();
        // Fabric: the POI type in the registry alone is not found by its blocks, so villagers never took the
        // werewolf profession (NeoForge adds the block states of registered POI types itself)
        Holder<PoiType> poi = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolderOrThrow(RegistryEntries.POI_WEREWOLVIAN.getKey());
        PoiTypes.registerBlockStates(poi, poi.value().matchingStates());
        IStructureHelpers sh = getMod().getModHelpers().getStructureHelpers();
        // When the server data (and its structure template pools) is loaded
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (!client) {
                for (String biome : new String[]{"plains", "snowy", "savanna", "desert", "taiga"}) {
                    sh.addToStructureTemplatePool(
                            ResourceLocation.withDefaultNamespace("village/" + biome + "/houses"),
                            ResourceLocation.fromNamespaceAndPath(getMod().getModId(), "village/werewolvian_mansion"),
                            registries
                    );
                }
            }
        });
    }
}
