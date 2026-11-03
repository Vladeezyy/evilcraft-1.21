package org.cyclops.evilcraft.fabric.cyclopscore.network;

import org.cyclops.cyclopscore.network.IPacketHandler;
import org.cyclops.cyclopscore.network.PacketBase;


import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandler.Sharable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;

import javax.annotation.Nullable;

/**
 * The packet handler of the mod, with the methods of Cyclops Core's NeoForge packet handler.
 * @author rubensworks
 */
@Sharable
public final class PacketHandlerNeoForge implements IPacketHandler {

    private final ModBaseNeoForge mod;
    private final List<Pair<CustomPacketPayload.Type<?>, StreamCodec<? super RegistryFriendlyByteBuf, ? extends PacketBase>>> pendingPacketRegistrations;

    public PacketHandlerNeoForge(ModBaseNeoForge mod) {
        this.mod = mod;
        this.pendingPacketRegistrations = Lists.newArrayList();
        mod.getModEventBus().addListener(this::init);
        mod.getModEventBus().addListener(this::initClient);
    }

    protected void init(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(mod.getModId())
                .versioned("1.0.0")
                .executesOn(HandlerThread.NETWORK)
                .optional();

    public PacketHandler(IPacketHandler delegate) {
        this.delegate = delegate;
    }

    protected void initClient(RegisterClientPayloadHandlersEvent event) {
        for (Pair<CustomPacketPayload.Type, StreamCodec> pendingPacketRegistration : (List<Pair<CustomPacketPayload.Type, StreamCodec>>) (List) this.pendingPacketRegistrations) {
            this.registerActualClient(event, pendingPacketRegistration.getLeft(), pendingPacketRegistration.getRight());
        }
    }

    @Override
    public <P extends PacketBase> void register(Class<P> clazz, CustomPacketPayload.Type<P> type, StreamCodec<? super RegistryFriendlyByteBuf, P> codec) {
        this.pendingPacketRegistrations.add(Pair.of(type, codec));
    }

    protected <P extends PacketBase> void registerActual(PayloadRegistrar registrar, CustomPacketPayload.Type<P> type, StreamCodec<? super RegistryFriendlyByteBuf, P> codec) {
        registrar.playBidirectional(
                type,
                codec,
                (packet, ctx) -> {
                    if (packet.isAsync()) {
                        handlePacketServer(ctx, packet);
                    } else {
                        ctx.enqueueWork(() -> handlePacketServer(ctx, packet));
                    }
                });
    }

    protected <P extends PacketBase> void registerActualClient(RegisterClientPayloadHandlersEvent event, CustomPacketPayload.Type<P> type, StreamCodec<? super RegistryFriendlyByteBuf, P> codec) {
        event.register(type, (packet, ctx) -> {
            if (packet.isAsync()) {
                handlePacketClient(ctx, packet);
            } else {
                ctx.enqueueWork(() -> handlePacketClient(ctx, packet));
            }
        });
    }

    public void handlePacketClient(IPayloadContext context, PacketBase packet) {
        packet.actionClient(context.player().level(), context.player());
    }

    public void handlePacketServer(IPayloadContext context, PacketBase packet) {
        packet.actionServer(context.player().level(), (ServerPlayer) context.player());
    }

    @Override
    public void sendToServer(PacketBase packet) {
        ClientPacketDistributor.sendToServer(packet);
    }

    @Override
    public void sendToPlayer(PacketBase packet, ServerPlayer player) {
        delegate.sendToPlayer(packet, player);
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
        PacketDistributor.sendToAllPlayers(packet);
    }

}
