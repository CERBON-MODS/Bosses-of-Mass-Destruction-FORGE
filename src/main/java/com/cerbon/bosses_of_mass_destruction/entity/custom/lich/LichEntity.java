package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class LichEntity extends BaseEntity {
    private LichConfig mobConfig;

    public LichEntity(EntityType<? extends LichEntity> entityType, Level level, LichConfig mobConfig) {
        super(entityType, level);
        this.mobConfig = mobConfig;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }
}

