package com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class MobUtils {
    public static void setPos(Entity entity, Vec3 vec) {
        entity.absMoveTo(vec.x, vec.y, vec.z);
    }

    public static Vec3 eyePos(Entity entity) {
        return entity.getEyePosition(1.0f);
    }

    public static Vec3 lastRenderPos(Entity entity) {
        return new Vec3(entity.xOld, entity.yOld, entity.zOld);
    }
}
