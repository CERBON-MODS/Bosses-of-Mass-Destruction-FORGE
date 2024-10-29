package com.cerbon.bosses_of_mass_destruction.packet;

import com.cerbon.bosses_of_mass_destruction.packet.custom.*;
import com.cerbon.cerbons_api.api.network.Network;

public class BMDPackets {

    public void registerPackets() {
        Network.registerPacket(BlindnessS2CPacket.IDENTIFIER, BlindnessS2CPacket.CODEC, BlindnessS2CPacket::handle);
        Network.registerPacket(ChangeHitboxS2CPacket.IDENTIFIER, ChangeHitboxS2CPacket.CODEC, ChangeHitboxS2CPacket::handle);
        Network.registerPacket(ChargedEnderPearlS2CPacket.IDENTIFIER, ChargedEnderPearlS2CPacket.CODEC, ChargedEnderPearlS2CPacket::handle);
        Network.registerPacket(HealS2CPacket.IDENTIFIER, HealS2CPacket.CODEC, HealS2CPacket::handle);
        Network.registerPacket(ObsidilithReviveS2CPacket.IDENTIFIER, ObsidilithReviveS2CPacket.CODEC, ObsidilithReviveS2CPacket::handle);
        Network.registerPacket(PlaceS2CPacket.IDENTIFIER, PlaceS2CPacket.CODEC, PlaceS2CPacket::handle);
        Network.registerPacket(SendDeltaMovementS2CPacket.IDENTIFIER, SendDeltaMovementS2CPacket.CODEC, SendDeltaMovementS2CPacket::handle);
        Network.registerPacket(SendParticleS2CPacket.IDENTIFIER, SendParticleS2CPacket.CODEC, SendParticleS2CPacket::handle);
        Network.registerPacket(SendVec3S2CPacket.IDENTIFIER, SendVec3S2CPacket.CODEC, SendVec3S2CPacket::handle);
        Network.registerPacket(SpikeS2CPacket.IDENTIFIER, SpikeS2CPacket.CODEC, SpikeS2CPacket::handle);
        Network.registerPacket(VoidBlossomReviveS2CPacket.IDENTIFIER, VoidBlossomReviveS2CPacket.CODEC, VoidBlossomReviveS2CPacket::handle);
    }

    public static void register() {
        new BMDPackets().registerPackets();
    }
}
