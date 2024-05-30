package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.client.render.NodeBossBarRenderer;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class ObsidilithUtils {
    private static final int textureSize = 256;
    private static final ResourceLocation bossBarDividerTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/gui/obsidilith_boss_bar_dividers.png");
    public static final List<Float> hpPillarShieldMilestones = Lists.newArrayList(0.0f, 0.25f, 0.5f, 0.75f, 1.0f);
    public static final DataParameter<Boolean> isShielded = EntityDataManager.defineId(ObsidilithEntity.class, DataSerializers.BOOLEAN);
    public static final NodeBossBarRenderer obsidilithBossBarRenderer = new NodeBossBarRenderer(BMDEntities.OBSIDILITH.get().getDescriptionId(), hpPillarShieldMilestones, bossBarDividerTexture, textureSize);
    public static final List<Vector3d> circlePos = MathUtils.buildBlockCircle(Math.sqrt(Math.pow(2.0, 2) + Math.pow(1.0, 2)));

    public static final byte deathStatus = 3;
    public static final byte burstAttackStatus = 5;
    public static final byte waveAttackStatus = 6;
    public static final byte spikeAttackStatus = 7;
    public static final byte anvilAttackStatus = 8;
    public static final byte pillarDefenseStatus = 9;
    public static final int deathPillarHeight = 15;
    public static final int ticksBetweenPillarLayer = 5;

    public static Vector3d approximatePlayerNextPosition(List<Vector3d> previousPosition, Vector3d currentPos) {
        return previousPosition.stream()
                .map(vec3 -> VecUtils.planeProject(vec3.subtract(currentPos), VecUtils.yAxis))
                .reduce(Vector3d::add).orElse(Vector3d.ZERO)
                .multiply(-0.5, -0.5, -0.5).add(currentPos);
    }

    public static void onDeath(LivingEntity actor, int experienceDrop){
        World level = actor.level;

        if (!level.isClientSide()){
            BlockPos blockPos = actor.blockPosition();
            Vector3d vecPos = actor.position();
            EventScheduler eventScheduler = BMDCapabilities.getLevelEventScheduler(level);
            level.explode(actor, actor.getX(), actor.getY(), actor.getZ(), 2.0f, Explosion.Mode.NONE);

            for (int y = 0; y <= deathPillarHeight; y++){
                int y1 = y;
                eventScheduler.addEvent(
                        new TimedEvent(
                                () ->{
                                    actor.playSound(SoundEvents.STONE_PLACE, 1.0f, BMDUtils.randomPitch(actor.getRandom()));

                                    for (Vector3d pos : circlePos){
                                        level.setBlockAndUpdate(
                                                new BlockPos((int) pos.x, y1, (int) pos.z).offset(blockPos),
                                                Blocks.OBSIDIAN.defaultBlockState()
                                        );
                                    }
                                },
                                y * ticksBetweenPillarLayer
                        )
                );
            }

            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                BlockPos chestPos = blockPos.above(deathPillarHeight + 1);
                                level.setBlock(chestPos, Blocks.SHULKER_BOX.defaultBlockState(), 2);
                                LockableLootTileEntity.setLootTable(level, actor.getRandom(), chestPos, new ResourceLocation(BMDConstants.MOD_ID, "chests/obsidilith"));
                            },
                            deathPillarHeight * ticksBetweenPillarLayer
                    )
            );

            int expTicks = 20;
            int expPerTick = (int) (experienceDrop / (float) expTicks);
            Vector3d pillarTop = vecPos.add(VecUtils.yAxis.scale(deathPillarHeight));
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> VanillaCopiesServer.awardExperience(
                                    expPerTick,
                                    pillarTop.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).scale(2.0)),
                                    level
                            ),
                            deathPillarHeight * ticksBetweenPillarLayer,
                            expTicks,
                            () -> false
                    )
            );
        }
    }
}

