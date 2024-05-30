package com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;

/**
 * Should be implemented by entities who want to know what part of them was attacked or interacted
 */
public interface MultipartAwareEntity extends MultipartEntity {
    /**
     * @return Entity bounds as shown in the example in {@link MultipartEntity}
     */
    EntityBounds getBounds();

    void onSetPos(final double x, final double y, final double z);

    /**
     * This is an ugly hack to not interfere with the vanilla damage logic.
     * This will be called right before {@link Entity#hurt(DamageSource, float)} is called, the part should be stored until then
     * and reset to null after {@link Entity#hurt(DamageSource, float)} is called.
     * A part of null indicates a damage not done by entity, for example, suffocation, void damage, etc.
     *
     * @param part The part hit by the attacker, may be null
     */
    void setNextDamagedPart(@Nullable String part);

    /**
     * @param entity The entity interacting with this
     * @param hand   The hand the entity is using
     * @param part   The part it is interacting with, should not be null
     * @return Same as vanilla interact
     */
    default ActionResultType interact(final Entity entity, final Hand hand, final String part) {
        return ActionResultType.PASS;
    }
}
