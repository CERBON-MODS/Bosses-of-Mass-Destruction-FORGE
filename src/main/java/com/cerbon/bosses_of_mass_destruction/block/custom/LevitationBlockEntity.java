package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.AnimationUtils;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;
import java.util.stream.Collectors;

public class LevitationBlockEntity extends ChunkCacheBlockEntity implements IAnimatable, IAnimationTickable {
    private static final double tableOfElevationRadius = AutoConfig.getConfigHolder(BMDConfig.class).getConfig().generalConfig.tableOfElevationRadius;
    private AnimationFactory animationFactory;
    public int animationAge = 0;

    private static final HashSet<ServerPlayer> flight = new HashSet<>();

    public LevitationBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMDBlocks.LEVITATION_BLOCK.get(), BMDBlockEntities.LEVITATION_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(
                new AnimationController<>(
                        this,
                        "idle",
                        0,
                        AnimationUtils.createIdlePredicate("rotate")
                )
        );
    }

    @Override
    public AnimationFactory getFactory() {
        if (animationFactory == null)
            animationFactory = new AnimationFactory(this);

        return animationFactory;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LevitationBlockEntity entity){
        ChunkCacheBlockEntity.tick(level, pos, state, entity);
        if (level.isClientSide){
            entity.animationAge++;

            AABB box = getAffectingBox(level, VecUtils.asVec3(pos));
            List<Player> playersInBox = level.getEntitiesOfClass(Player.class, box);

            for (Player player : playersInBox) {

                for (double x : List.of(box.minX, box.maxX)) {
                    double zRand = box.getCenter().z + box.getZsize() * RandomUtils.randomDouble(0.5);
                    Particles.particlesFactory.build(randYPos(x, player, zRand), Vec3.ZERO);
                }

                for (double z : List.of(box.minZ, box.maxZ)) {
                    double xRand = box.getCenter().x + box.getXsize() * RandomUtils.randomDouble(0.5);
                    Particles.particlesFactory.build(randYPos(xRand, player, z), Vec3.ZERO);
                }
            }
        }
    }

    private static Vec3 randYPos(double x, Player player, double z){
        return new Vec3(x, player.getY() + RandomUtils.randomDouble(0.5) + 1, z);
    }

    public static void tickFlight(ServerPlayer player){
        List<BlockPos> blockToCheck = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++)
                blockToCheck.add(new BlockPos(x * (int) tableOfElevationRadius, 0, z * (int) tableOfElevationRadius));
        }
        Set<ChunkPos> chunksToCheck = blockToCheck.stream().map(pos -> new ChunkPos(pos.offset(player.blockPosition()))).collect(Collectors.toSet());
        boolean hasLevitationBlock = chunksToCheck.stream().anyMatch(chunkPos -> {
            Optional<ChunkBlockCache> blockCache = BMDCapabilities.getChunkBlockCache(player.level);

            if (blockCache.isPresent()){
                List<BlockPos> blocks = blockCache.get().getBlocksFromChunk(chunkPos, BMDBlocks.LEVITATION_BLOCK.get());
                return blocks.stream().anyMatch(pos -> getAffectingBox(player.level, VecUtils.asVec3(pos)).contains(player.position()));
            } else
                return false;
        });

        /*
          Known bugs:
          - does not have persistence (e.g. loading why flying will fall next time it is opened, though without fall damage)
          - does not handle changing gamemodes that change these abilities (e.g. changing to survival or losing the flying ability
          from taking another modded item off will not work as expected.)
          - However, it will be more-or-less able to work with other mods that mess with the flight ability provided that
          they do not set it every tick
         */
        if (!hasLevitationBlock){
            if (flight.contains(player)) {
                if (!player.isCreative() && !player.isSpectator()) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
                }
                flight.remove(player);
            }
        } else if (!flight.contains(player)){
            flight.add(player);
            if (!player.getAbilities().mayfly){
                player.getAbilities().mayfly = true;
                player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
            }
        }
    }

    private static AABB getAffectingBox(Level level, Vec3 pos){
        return new AABB(pos.x, level.getMinBuildHeight(), pos.z, (pos.x + 1), level.getHeight(), (pos.z + 1)).inflate(tableOfElevationRadius, 0.0, tableOfElevationRadius);
    }

    @Override
    public void tick() {}

    @Override
    public int tickTimer() {
        return animationAge;
    }

    private static class Particles {
        private static final ClientParticleBuilder particlesFactory = new ClientParticleBuilder(BMDParticles.LINE.get())
                .color(BMDColors.COMET_BLUE)
                .brightness(BMDParticles.FULL_BRIGHT)
                .colorVariation(0.5)
                .scale(0.075f);
    }
}
