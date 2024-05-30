package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class VoidBlossomSummonBlockEntity extends TileEntity implements ITickableTileEntity {
    private int age = 0;

    public VoidBlossomSummonBlockEntity() {
        super(BMDBlockEntities.VOID_BLOSSOM_SUMMON_BLOCK_ENTITY.get());
    }

    @Override
    public void tick() {
        if (level == null) return;

        if (!level.isClientSide && this.age % 20 == 0){
            boolean playersInBox = !level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(getBlockPos()).inflate(40.0)).isEmpty();
            if (playersInBox){
                Vector3d spawnPos = VecUtils.asVec3(getBlockPos()).add(new Vector3d(0.5, 0.0, 0.5));
                VoidBlossomEntity boss = BMDEntities.VOID_BLOSSOM.get().create(level);
                if (boss == null) return;
                boss.absMoveTo(spawnPos.x, spawnPos.y, spawnPos.z);
                level.addFreshEntity(boss);
                level.removeBlock(getBlockPos(), false);
            }
        }
        this.age++;
    }
}
