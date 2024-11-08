package com.cerbon.bosses_of_mass_destruction.packet;

import com.cerbon.bosses_of_mass_destruction.packet.custom.*;
import com.cerbon.cerbons_api.api.network.Network;

public class BMDPackets {

    public void registerPackets() {
        Network.registerPacket(BlindnessS2CPacket.type(), BlindnessS2CPacket.class, BlindnessS2CPacket.STREAM_CODEC, BlindnessS2CPacket::handle);
        Network.registerPacket(ChangeHitboxS2CPacket.type(), ChangeHitboxS2CPacket.class, ChangeHitboxS2CPacket.STREAM_CODEC, ChangeHitboxS2CPacket::handle);
        Network.registerPacket(ChargedEnderPearlS2CPacket.type(), ChargedEnderPearlS2CPacket.class, ChargedEnderPearlS2CPacket.STREAM_CODEC, ChargedEnderPearlS2CPacket::handle);
        Network.registerPacket(HealS2CPacket.type(), HealS2CPacket.class, HealS2CPacket.STREAM_CODEC, HealS2CPacket::handle);
        Network.registerPacket(ObsidilithReviveS2CPacket.type(), ObsidilithReviveS2CPacket.class, ObsidilithReviveS2CPacket.STREAM_CODEC, ObsidilithReviveS2CPacket::handle);
        Network.registerPacket(PlaceS2CPacket.type(), PlaceS2CPacket.class, PlaceS2CPacket.STREAM_CODEC, PlaceS2CPacket::handle);
        Network.registerPacket(SendDeltaMovementS2CPacket.type(), SendDeltaMovementS2CPacket.class, SendDeltaMovementS2CPacket.STREAM_CODEC, SendDeltaMovementS2CPacket::handle);
        Network.registerPacket(SendParticleS2CPacket.type(), SendParticleS2CPacket.class, SendParticleS2CPacket.STREAM_CODEC, SendParticleS2CPacket::handle);
        Network.registerPacket(SendVec3S2CPacket.type(), SendVec3S2CPacket.class, SendVec3S2CPacket.STREAM_CODEC, SendVec3S2CPacket::handle);
        Network.registerPacket(SpikeS2CPacket.type(), SpikeS2CPacket.class, SpikeS2CPacket.STREAM_CODEC, SpikeS2CPacket::handle);
        Network.registerPacket(VoidBlossomReviveS2CPacket.type(), VoidBlossomReviveS2CPacket.class, VoidBlossomReviveS2CPacket.STREAM_CODEC, VoidBlossomReviveS2CPacket::handle);
    }

    public static void register() {
        new BMDPackets().registerPackets();
    }
}
