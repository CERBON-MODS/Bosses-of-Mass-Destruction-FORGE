package com.cerbon.bosses_of_mass_destruction.entity;

import com.cerbon.bosses_of_mass_destruction.animation.PauseAnimationTimer;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.WeakHashPredicate;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.client.render.*;
import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import com.cerbon.bosses_of_mass_destruction.particle.ParticleFactories;
import com.cerbon.bosses_of_mass_destruction.projectile.MagicMissileProjectile;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BMDEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BMDConstants.MOD_ID);

    public static final RegistryObject<EntityType<MagicMissileProjectile>> MAGIC_MISSILE = ENTITY_TYPES.register("blue_fireball",
            () -> EntityType.Builder.of(MagicMissileProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(BMDConstants.MOD_ID, "blue_fireball").toString()));

    public static final RegistryObject<EntityType<ChargedEnderPearlEntity>> CHARGED_ENDER_PEARL = ENTITY_TYPES.register("charged_ender_pearl",
            () -> EntityType.Builder.of(ChargedEnderPearlEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(BMDConstants.MOD_ID, "charged_ender_pearl").toString()));

    public static void initClient(){
        PauseAnimationTimer pauseSecondTimer = new PauseAnimationTimer(Blaze3D::getTime, () -> Minecraft.getInstance().isPaused());

        EntityRenderers.register(CHARGED_ENDER_PEARL.get(), ThrownItemRenderer::new);

        ResourceLocation missileTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/blue_magic_missile.png");
        RenderType magicMissileRenderType = RenderType.entityCutoutNoCull(missileTexture);
        EntityRenderers.register(MAGIC_MISSILE.get(), context ->
                new SimpleEntityRenderer<>(context,
                        new CompositeRenderer<>(
                                new BillboardRenderer<>(context.getEntityRenderDispatcher(), magicMissileRenderType, f -> 0.5f),
                                new ConditionalRenderer<>(
                                        new WeakHashPredicate<>(() -> new FrameLimiter(20f, pauseSecondTimer)::canDoFrame),
                                        new LerpedPosRenderer<>(vec3 -> ParticleFactories.soulFlame().build(vec3.add(RandomUtils.randVec().multiply(0.25, 0.25, 0.25)), Vec3.ZERO)))),
                        entity -> missileTexture, new FullRenderLight<>()
                ));
    }

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
