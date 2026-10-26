package org.cyclops.evilcraft.proxy;

import org.cyclops.evilcraft.fabric.cyclopscore.network.packet.SendPlayerNbtPacket;
import org.cyclops.evilcraft.fabric.cyclopscore.network.packet.RequestPlayerNbtPacket;
import org.cyclops.evilcraft.fabric.cyclopscore.network.packet.SendPlayerAdvancementUnlockedPacket;
import org.cyclops.evilcraft.fabric.cyclopscore.network.packet.RequestPlayerAdvancementUnlockedPacket;
import org.cyclops.evilcraft.fabric.cyclopscore.network.packet.AdvancementRewardsObtainPacket;
import org.cyclops.evilcraft.fabric.cyclopscore.network.packet.ValueNotifyPacket;
import org.cyclops.evilcraft.fabric.cyclopscore.network.packet.ButtonClickPacket;
import org.cyclops.evilcraft.fabric.event.EntityTickEvent;
import org.cyclops.evilcraft.fabric.event.EntityStruckByLightningEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;
import org.cyclops.evilcraft.fabric.cyclopscore.network.PacketHandler;
import org.cyclops.evilcraft.fabric.cyclopscore.proxy.CommonProxyComponent;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.core.fluid.WorldSharedTankCache;
import org.cyclops.evilcraft.event.EntityStruckByLightningEventHook;
import org.cyclops.evilcraft.event.LivingUpdateEventHook;
import org.cyclops.evilcraft.network.packet.ExaltedCrafterOpenPacket;
import org.cyclops.evilcraft.network.packet.FartPacket;
import org.cyclops.evilcraft.network.packet.ResetChunkColorsPacket;
import org.cyclops.evilcraft.network.packet.SanguinaryPedestalBlockReplacePacket;
import org.cyclops.evilcraft.network.packet.UpdateWorldSharedTankClientCachePacket;

/**
 * Proxy for server and client side.
 * @author rubensworks
 *
 */
public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return EvilCraft._instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);

        // Register packets.
        packetHandler.register(FartPacket.ID, FartPacket.CODEC);
        packetHandler.register(SanguinaryPedestalBlockReplacePacket.ID, SanguinaryPedestalBlockReplacePacket.CODEC);
        packetHandler.register(ExaltedCrafterOpenPacket.ID, ExaltedCrafterOpenPacket.CODEC);
        packetHandler.register(UpdateWorldSharedTankClientCachePacket.ID, UpdateWorldSharedTankClientCachePacket.CODEC);
        packetHandler.register(ResetChunkColorsPacket.ID, ResetChunkColorsPacket.CODEC);

        // Cyclops Core's packets, which it only has on NeoForge
        packetHandler.register(ButtonClickPacket.TYPE, ButtonClickPacket.CODEC);
        packetHandler.register(ValueNotifyPacket.TYPE, ValueNotifyPacket.CODEC);
        packetHandler.register(AdvancementRewardsObtainPacket.TYPE, AdvancementRewardsObtainPacket.CODEC);
        packetHandler.register(RequestPlayerAdvancementUnlockedPacket.TYPE, RequestPlayerAdvancementUnlockedPacket.CODEC);
        packetHandler.register(SendPlayerAdvancementUnlockedPacket.TYPE, SendPlayerAdvancementUnlockedPacket.CODEC);
        packetHandler.register(RequestPlayerNbtPacket.TYPE, RequestPlayerNbtPacket.CODEC);
        packetHandler.register(SendPlayerNbtPacket.TYPE, SendPlayerNbtPacket.CODEC);

        EvilCraft.clog("Registered packet handler.");
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();
        EntityStruckByLightningEvent.LISTENERS.register(new EntityStruckByLightningEventHook()::onEntityStruckByLightning);
        EntityTickEvent.Post.addListener(new LivingUpdateEventHook()::tick);
        WorldSharedTankCache.getInstance().registerEvents();
    }

}
