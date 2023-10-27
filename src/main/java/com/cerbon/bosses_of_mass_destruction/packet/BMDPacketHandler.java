package com.cerbon.bosses_of_mass_destruction.packet;

import com.cerbon.bosses_of_mass_destruction.packet.custom.ChargedEnderPearlPacket;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class BMDPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id(){
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(BMDConstants.MOD_ID, "packets"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ChargedEnderPearlPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ChargedEnderPearlPacket::new)
                .encoder(ChargedEnderPearlPacket::write)
                .consumerMainThread(ChargedEnderPearlPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayersInChunk(MSG message, ServerLevel level, Vec3 pos){
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(BlockPos.containing(pos))), message);
    }
}
