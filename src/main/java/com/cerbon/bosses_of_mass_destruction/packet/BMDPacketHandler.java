package com.cerbon.bosses_of_mass_destruction.packet;

import com.cerbon.bosses_of_mass_destruction.packet.custom.*;
import com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities.MultipartEntityInteractionC2SPacket;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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

        net.messageBuilder(ChargedEnderPearlS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ChargedEnderPearlS2CPacket::new)
                .encoder(ChargedEnderPearlS2CPacket::write)
                .consumer(ChargedEnderPearlS2CPacket::handle)
                .add();

        net.messageBuilder(SendDeltaMovementS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendDeltaMovementS2CPacket::new)
                .encoder(SendDeltaMovementS2CPacket::write)
                .consumer(SendDeltaMovementS2CPacket::handle)
                .add();

        net.messageBuilder(PlaceS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlaceS2CPacket::new)
                .encoder(PlaceS2CPacket::write)
                .consumer(PlaceS2CPacket::handle)
                .add();

        net.messageBuilder(SpikeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SpikeS2CPacket::new)
                .encoder(SpikeS2CPacket::write)
                .consumer(SpikeS2CPacket::handle)
                .add();

        net.messageBuilder(HealS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(HealS2CPacket::new)
                .encoder(HealS2CPacket::write)
                .consumer(HealS2CPacket::handle)
                .add();

        net.messageBuilder(BlindnessS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BlindnessS2CPacket::new)
                .encoder(BlindnessS2CPacket::write)
                .consumer(BlindnessS2CPacket::handle)
                .add();

        net.messageBuilder(ChangeHitboxS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ChangeHitboxS2CPacket::new)
                .encoder(ChangeHitboxS2CPacket::write)
                .consumer(ChangeHitboxS2CPacket::handle)
                .add();

        net.messageBuilder(SendParticleS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendParticleS2CPacket::new)
                .encoder(SendParticleS2CPacket::write)
                .consumer(SendParticleS2CPacket::handle)
                .add();

        net.messageBuilder(SendVec3S2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendVec3S2CPacket::new)
                .encoder(SendVec3S2CPacket::write)
                .consumer(SendVec3S2CPacket::handle)
                .add();

        net.messageBuilder(ObsidilithReviveS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ObsidilithReviveS2CPacket::new)
                .encoder(ObsidilithReviveS2CPacket::write)
                .consumer(ObsidilithReviveS2CPacket::handle)
                .add();

        net.messageBuilder(VoidBlossomReviveS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(VoidBlossomReviveS2CPacket::new)
                .encoder(VoidBlossomReviveS2CPacket::write)
                .consumer(VoidBlossomReviveS2CPacket::handle)
                .add();

        net.messageBuilder(MultipartEntityInteractionC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MultipartEntityInteractionC2SPacket::new)
                .encoder(MultipartEntityInteractionC2SPacket::write)
                .consumer(MultipartEntityInteractionC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayerEntity player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayersTrackingChunk(MSG message, ServerWorld level, Vector3d pos){
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(new BlockPos(pos))), message);
    }
}
