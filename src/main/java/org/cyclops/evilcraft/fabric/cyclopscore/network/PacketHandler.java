package org.cyclops.evilcraft.fabric.cyclopscore.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.cyclops.cyclopscore.network.IPacketHandler;
import org.cyclops.cyclopscore.network.PacketBase;

import javax.annotation.Nullable;

/**
 * The packet handler of the mod, with the methods of Cyclops Core's NeoForge packet handler.
 * @author rubensworks
 */
public final class PacketHandler implements IPacketHandler {

    private final IPacketHandler delegate;

    public PacketHandler(IPacketHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public <P extends PacketBase> void register(Class<P> clazz, CustomPacketPayload.Type<P> type, StreamCodec<? super RegistryFriendlyByteBuf, P> codec) {
        delegate.register(clazz, type, codec);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <P extends PacketBase> void register(CustomPacketPayload.Type<P> type, StreamCodec<? super RegistryFriendlyByteBuf, P> codec) {
        delegate.register((Class) PacketBase.class, type, codec);
    }

    @Override
    public void sendToServer(PacketBase packet) {
        delegate.sendToServer(packet);
    }

    @Override
    public void sendToPlayer(PacketBase packet, ServerPlayer player) {
        delegate.sendToPlayer(packet, player);
    }

    public void sendToAllAround(PacketBase packet, TargetPoint point) {
        delegate.sendToAllAroundPoint(packet, new IPacketHandler.TargetPoint(point.level(), point.x(), point.y(), point.z(), point.radius(), point.excluded()));
    }

    @Override
    public void sendToAllAroundPoint(PacketBase packet, IPacketHandler.TargetPoint point) {
        delegate.sendToAllAroundPoint(packet, point);
    }

    @Override
    public void sendToDimension(PacketBase packet, ServerLevel dimension) {
        delegate.sendToDimension(packet, dimension);
    }

    @Override
    public void sendToAll(PacketBase packet) {
        delegate.sendToAll(packet);
    }

    public static record TargetPoint(ServerLevel level, double x, double y, double z, double radius, @Nullable ServerPlayer excluded) {}
}
