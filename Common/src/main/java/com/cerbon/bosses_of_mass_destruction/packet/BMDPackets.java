package com.cerbon.bosses_of_mass_destruction.packet;

import com.cerbon.bosses_of_mass_destruction.packet.custom.*;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.network.Network;
import net.minecraft.resources.ResourceLocation;

public class BMDPackets {

    public void registerPackets() {

        Network.registerPacket(channel("blindness"), BlindnessS2CPacket.class,
                BlindnessS2CPacket::new,
                BlindnessS2CPacket::write,
                BlindnessS2CPacket::handle
        );

        Network.registerPacket(channel("change_hitbox"), ChangeHitboxS2CPacket.class,
                ChangeHitboxS2CPacket::new,
                ChangeHitboxS2CPacket::write,
                ChangeHitboxS2CPacket::handle
        );

        Network.registerPacket(channel("charged_ender_pearl"), ChargedEnderPearlS2CPacket.class,
                ChargedEnderPearlS2CPacket::new,
                ChargedEnderPearlS2CPacket::write,
                ChargedEnderPearlS2CPacket::handle
        );

        Network.registerPacket(channel("heal"), HealS2CPacket.class,
                HealS2CPacket::new,
                HealS2CPacket::write,
                HealS2CPacket::handle
        );

        Network.registerPacket(channel("obsidilith_revive"), ObsidilithReviveS2CPacket.class,
                ObsidilithReviveS2CPacket::new,
                ObsidilithReviveS2CPacket::write,
                ObsidilithReviveS2CPacket::handle
        );

        Network.registerPacket(channel("place"), PlaceS2CPacket.class,
                PlaceS2CPacket::new,
                PlaceS2CPacket::write,
                PlaceS2CPacket::handle
        );

        Network.registerPacket(channel("send_delta_movement"), SendDeltaMovementS2CPacket.class,
                SendDeltaMovementS2CPacket::new,
                SendDeltaMovementS2CPacket::write,
                SendDeltaMovementS2CPacket::handle
        );

        Network.registerPacket(channel("send_particle"), SendParticleS2CPacket.class,
                SendParticleS2CPacket::new,
                SendParticleS2CPacket::write,
                SendParticleS2CPacket::handle
        );

        Network.registerPacket(channel("send_vec3"), SendVec3S2CPacket.class,
                SendVec3S2CPacket::new,
                SendVec3S2CPacket::write,
                SendVec3S2CPacket::handle
        );

        Network.registerPacket(channel("spike"), SpikeS2CPacket.class,
                SpikeS2CPacket::new,
                SpikeS2CPacket::write,
                SpikeS2CPacket::handle
        );

        Network.registerPacket(channel("void_blossom_revive"), VoidBlossomReviveS2CPacket.class,
                VoidBlossomReviveS2CPacket::new,
                VoidBlossomReviveS2CPacket::write,
                VoidBlossomReviveS2CPacket::handle
        );
    }

    private ResourceLocation channel(String name) {
        return new ResourceLocation(BMDConstants.MOD_ID, name);
    }

    public static void register() {
        new BMDPackets().registerPackets();
    }
}
