package com.cerbon.bosses_of_mass_destruction.entity;

import com.cerbon.bosses_of_mass_destruction.animation.PauseAnimationTimer;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.WeakHashPredicate;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.client.render.*;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichKillCounter;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.SporeBallOverlay;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.SporeBallSizeRenderer;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.SporeCodeAnimations;
import com.cerbon.bosses_of_mass_destruction.entity.util.SimpleLivingGeoRenderer;
import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.particle.ParticleFactories;
import com.cerbon.bosses_of_mass_destruction.projectile.MagicMissileProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.PetalBladeProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.comet.CometCodeAnimations;
import com.cerbon.bosses_of_mass_destruction.projectile.comet.CometProjectile;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.blaze3d.Blaze3D;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BMDEntities {
    private static final BMDConfig mobConfig = AutoConfig.getConfigHolder(BMDConfig.class).getConfig();

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BMDConstants.MOD_ID);

    public static final RegistryObject<EntityType<MagicMissileProjectile>> MAGIC_MISSILE = ENTITY_TYPES.register("blue_fireball",
            () -> EntityType.Builder.<MagicMissileProjectile>of(MagicMissileProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(BMDConstants.MOD_ID, "blue_fireball").toString()));

    public static final RegistryObject<EntityType<CometProjectile>> COMET = ENTITY_TYPES.register("comet",
            () -> EntityType.Builder.<CometProjectile>of(CometProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(BMDConstants.MOD_ID, "comet").toString()));

    public static final RegistryObject<EntityType<ChargedEnderPearlEntity>> CHARGED_ENDER_PEARL = ENTITY_TYPES.register("charged_ender_pearl",
            () -> EntityType.Builder.of(ChargedEnderPearlEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(BMDConstants.MOD_ID, "charged_ender_pearl").toString()));

    public static final RegistryObject<EntityType<SporeBallProjectile>> SPORE_BALL = ENTITY_TYPES.register("spore_ball",
            () -> EntityType.Builder.of(SporeBallProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(BMDConstants.MOD_ID, "spore_ball").toString()));

    public static final RegistryObject<EntityType<PetalBladeProjectile>> PETAL_BLADE = ENTITY_TYPES.register("petal_blade",
            () -> EntityType.Builder.of(PetalBladeProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(BMDConstants.MOD_ID, "petal_blade").toString()));

    public static LichKillCounter killCounter = new LichKillCounter(mobConfig.lichConfig.summonMechanic);

    public static void initClient(){
        PauseAnimationTimer pauseSecondTimer = new PauseAnimationTimer(Blaze3D::getTime, () -> Minecraft.getInstance().isPaused());

        EntityRenderers.register(COMET.get(), context ->
                new SimpleLivingGeoRenderer<>(
                        context,
                        new GeoModel<>(
                                geoAnimatable -> new ResourceLocation(BMDConstants.MOD_ID, "geo/comet.geo.json"),
                                entity -> new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/comet.png"),
                                new ResourceLocation(BMDConstants.MOD_ID, "animations/comet.animation.json"),
                                new CometCodeAnimations(),
                                RenderType::entityCutout
                        ),
                        new FullRenderLight<>(),
                        null,
                        new ConditionalRenderer<>(
                                new WeakHashPredicate<>(() -> new FrameLimiter(60f, pauseSecondTimer)::canDoFrame),
                                new LerpedPosRenderer<>(
                                        vec3 -> ParticleFactories.cometTrail().build(vec3.add(RandomUtils.randVec().multiply(0.5, 0.5, 0.5)), Vec3.ZERO)
                                )
                        ),
                        null,
                        null,
                        true
                ));

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

        EntityRenderers.register(SPORE_BALL.get(), context -> {
            SporeBallOverlay explosionFlasher = new SporeBallOverlay();
            return new SimpleLivingGeoRenderer<>(
                    context,
                    new GeoModel<>(
                            geoAnimatable -> new ResourceLocation(BMDConstants.MOD_ID, "geo/comet.geo.json"),
                            entity -> new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/spore.png"),
                            new ResourceLocation(BMDConstants.MOD_ID, "animations/comet.animation.json"),
                            new SporeCodeAnimations(),
                            RenderType::entityCutout),
                    new FullRenderLight<>(),
                    null,
                    new CompositeRenderer<>(
                            new ConditionalRenderer<>(
                                    new WeakHashPredicate<>(() -> new FrameLimiter(60f, pauseSecondTimer)::canDoFrame),
                                    new LerpedPosRenderer<>(vec3 -> {
                                        ClientParticleBuilder projectileParticles = new ClientParticleBuilder(BMDParticles.OBSIDILITH_BURST.get())
                                                .color(BMDColors.GREEN)
                                                .colorVariation(0.4)
                                                .scale(0.5f)
                                                .brightness(BMDParticles.FULL_BRIGHT);
                                        projectileParticles.build(vec3.add(RandomUtils.randVec().multiply(0.25, 0.25, 0.25)), VecUtils.yAxis.multiply(0.1, 0.1, 0.1));
                                    })),
                            explosionFlasher,
                            new SporeBallSizeRenderer()),
                    null,
                    explosionFlasher,
                    true);
        });

        ResourceLocation petalTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/petal_blade.png");
        RenderType petalBladeRenderType = RenderType.entityCutoutNoCull(petalTexture);
        EntityRenderers.register(PETAL_BLADE.get(), context ->
                new SimpleEntityRenderer<>(context,
                        new CompositeRenderer<>(
                                new PetalBladeRenderer(context.getEntityRenderDispatcher(), petalBladeRenderType),
                                new ConditionalRenderer<>(
                                        new WeakHashPredicate<>(() -> new FrameLimiter(30f, pauseSecondTimer)::canDoFrame),
                                        new PetalBladeParticleRenderer<>()
                                )),
                        entity -> petalTexture, new FullRenderLight<>()));
    }

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
