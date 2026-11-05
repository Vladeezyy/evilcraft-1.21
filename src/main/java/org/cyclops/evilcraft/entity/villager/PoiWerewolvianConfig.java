package org.cyclops.evilcraft.entity.villager;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import org.cyclops.cyclopscore.helper.IStructureHelpers;
import com.google.common.collect.Sets;
import net.minecraft.resources.Identifier;
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
        net.minecraft.core.Holder<PoiType> poi = net.minecraft.core.registries.BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(RegistryEntries.POI_WEREWOLVIAN.getKey());
        net.minecraft.world.entity.ai.village.poi.PoiTypes.registerBlockStates(poi, poi.value().matchingStates());
        org.cyclops.cyclopscore.helper.IStructureHelpers sh = getMod().getModHelpers().getStructureHelpers();
        // When the server data (and its structure template pools) is loaded
        net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (!client) {
                for (String biome : new String[]{"plains", "snowy", "savanna", "desert", "taiga"}) {
                    sh.addToStructureTemplatePool(
                            Identifier.withDefaultNamespace("village/" + biome + "/houses"),
                            Identifier.fromNamespaceAndPath(getMod().getModId(), "village/werewolvian_mansion"),
                            registries
                    );
                }
            }
        });
    }
}
