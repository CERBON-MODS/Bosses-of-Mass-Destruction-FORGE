package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Supplier;

public class ProjectileThrower {
    private final Supplier<ProjectileData> projectileProvider;

    public ProjectileThrower(Supplier<ProjectileData> projectileProvider) {
        this.projectileProvider = projectileProvider;
    }

    public void throwProjectile(Vector3d target) {
        ProjectileData projectileData = projectileProvider.get();
        Vector3d direction = target.subtract(projectileData.projectile.position());
        double h = Math.sqrt(direction.x * direction.x + direction.z * direction.z) * projectileData.gravityCompensation;
        projectileData.projectile.shoot(direction.x, direction.y + h, direction.z, projectileData.speed, projectileData.divergence);
        projectileData.projectile.level.addFreshEntity(projectileData.projectile);
    }

    public static class ProjectileData {

        private final ProjectileEntity projectile;
        private final float speed;
        private final float divergence;
        private final double gravityCompensation;

        public ProjectileData(ProjectileEntity projectile, float speed, float divergence, double gravityCompensation) {
            this.projectile = projectile;
            this.speed = speed;
            this.divergence = divergence;
            this.gravityCompensation = gravityCompensation;
        }
    }
}



