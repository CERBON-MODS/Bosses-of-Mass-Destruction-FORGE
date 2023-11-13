package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoidBlossomClientSpikeHandler implements IEntityTick<ClientLevel> {
    private final int maxAge = 10;
    private final LinkedHashMap<BlockPos, VoidBlossomClientSpikeHandler.Spike> spikes = new LinkedHashMap<>();
    private final ClientParticleBuilder spikeParticleFactory = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
            .age(RandomUtils.range(10, 15))
            .color(BMDColors.VOID_PURPLE)
            .colorVariation(0.25)
            .brightness(BMDParticles.FULL_BRIGHT);

    public Map<BlockPos, VoidBlossomClientSpikeHandler.Spike> getSpikes(){
        return spikes;
    }

    public void addSpike(BlockPos pos){
        Vec3 center = VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5));
        double spikeHeight = 4.0 + RandomUtils.randomDouble(0.5);
        spikes.put(pos, new Spike(
                center,
                RandomUtils.randVec().add(VecUtils.yAxis.scale(spikeHeight)).normalize(),
                (float) spikeHeight,
                maxAge,
                0
        ));
    }

    @Override
    public void tick(ClientLevel level) {
        List<BlockPos> toRemove = new ArrayList<>();

        for (var kv : spikes.entrySet()){
            int age = kv.getValue().age() + 1;

            if (age == maxAge - 5) {
                spikeParticleFactory.build(
                        VecUtils.asVec3(kv.getKey()).add(RandomUtils.randVec().add(VecUtils.yAxis.scale(2.5 + RandomUtils.randomDouble(2.0)))),
                        Vec3.ZERO
                );
            }

            if (age >= maxAge)
                toRemove.add(kv.getKey());
        }

        for (BlockPos removal : toRemove)
            spikes.remove(removal);
    }

    public record Spike(Vec3 pos, Vec3 offset, float height, int maxAge, int age){}
}