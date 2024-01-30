package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ProjectileThrower {
    private final Supplier<ProjectileData> projectileProvider;

    public ProjectileThrower(Supplier<ProjectileData> projectileProvider) {
        this.projectileProvider = projectileProvider;
    }

    public void throwProjectile(Vec3 target) {
        ProjectileData projectileData = projectileProvider.get();
        Vec3 direction = target.subtract(projectileData.projectile().position());
        double h = Math.sqrt(direction.x * direction.x + direction.z * direction.z) * projectileData.gravityCompensation();
        projectileData.projectile().shoot(direction.x, direction.y + h, direction.z, projectileData.speed(), projectileData.divergence());
        projectileData.projectile().level().addFreshEntity(projectileData.projectile());
    }

    public record ProjectileData(Projectile projectile, float speed, float divergence, double gravityCompensation) {}
}



