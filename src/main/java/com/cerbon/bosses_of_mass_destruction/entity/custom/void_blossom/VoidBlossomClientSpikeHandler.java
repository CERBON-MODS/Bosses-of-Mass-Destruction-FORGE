package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoidBlossomClientSpikeHandler implements IEntityTick<World> {
    private final LinkedHashMap<BlockPos, VoidBlossomClientSpikeHandler.Spike> spikes = new LinkedHashMap<>();
    private final ClientParticleBuilder spikeParticleFactory = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
            .age(RandomUtils.range(10, 15))
            .color(BMDColors.VOID_PURPLE)
            .colorVariation(0.25)
            .brightness(BMDParticles.FULL_BRIGHT);
    private final int maxAge = 10;

    public Map<BlockPos, VoidBlossomClientSpikeHandler.Spike> getSpikes(){
        return spikes;
    }

    public void addSpike(BlockPos pos){
        Vector3d center = VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5));
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
    public void tick(World level) {
        List<BlockPos> toRemove = new ArrayList<>();

        for (Map.Entry<BlockPos, Spike> kv : spikes.entrySet()){
            Spike oldSpike = kv.getValue();
            int newAge = oldSpike.age + 1;
            Spike newSpike = new Spike(oldSpike.pos, oldSpike.offset, oldSpike.height, oldSpike.maxAge, newAge);
            spikes.put(kv.getKey(), newSpike);

            if (newAge == maxAge - 5) {
                spikeParticleFactory.build(
                        VecUtils.asVec3(kv.getKey()).add(RandomUtils.randVec().add(VecUtils.yAxis.scale(2.5 + RandomUtils.randomDouble(2.0)))),
                        Vector3d.ZERO
                );
            }

            if (newAge >= maxAge)
                toRemove.add(kv.getKey());
        }

        for (BlockPos removal : toRemove)
            spikes.remove(removal);
    }

    public static class Spike {
        public final Vector3d pos;
        public final Vector3d offset;
        public final float height;
        public final int maxAge;
        public final int age;

        public Spike(Vector3d pos, Vector3d offset, float height, int maxAge, int age) {
            this.pos = pos;
            this.offset = offset;
            this.height = height;
            this.maxAge = maxAge;
            this.age = age;
        }
    }
}
