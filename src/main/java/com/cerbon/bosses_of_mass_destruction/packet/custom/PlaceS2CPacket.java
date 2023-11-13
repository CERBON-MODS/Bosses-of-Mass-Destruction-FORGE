package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlaceS2CPacket {
    private final Vec3 pos;

    public PlaceS2CPacket(Vec3 pos) {
        this.pos = pos;
    }

    public PlaceS2CPacket(FriendlyByteBuf buf) {
        this.pos = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf){
        PacketUtils.writeVec3(buf, this.pos);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> handleVoidBlossomPlace(pos)));
        });
        ctx.setPacketHandled(true);
    }

    //TODO: Move Handle to the VoidBlossomBlock
    private void handleVoidBlossomPlace(Vec3 pos){
        for (int i = 0; i <= 12; i++){
            Vec3 spawnPos = pos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(0.5));
            Vec3 vel = VecUtils.yAxis.scale(RandomUtils.range(0.1, 0.3));
            int randomRot = RandomUtils.range(0, 360);
            float angularMomentum = RandomUtils.randSign() * 4f;

            ClientParticleBuilder petalParticleFactory = new ClientParticleBuilder(BMDParticles.PETAL.get())
                    .color(f -> MathUtils.lerpVec(f, BMDColors.PINK, BMDColors.ULTRA_DARK_PURPLE))
                    .brightness(BMDParticles.FULL_BRIGHT)
                    .colorVariation(0.15)
                    .scale(f -> 0.15f * (1 - f * 0.25f))
                    .age(30);

            petalParticleFactory
                    .continuousRotation(f -> randomRot + f.getAge() * angularMomentum)
                    .continuousVelocity(f -> vel.scale(1.0 - f.ageRatio))
                    .build(spawnPos, vel);
        }
    }
}
