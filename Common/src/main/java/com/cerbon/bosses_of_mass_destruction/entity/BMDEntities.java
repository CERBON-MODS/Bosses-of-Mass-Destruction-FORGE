package com.cerbon.bosses_of_mass_destruction.entity;

import com.cerbon.bosses_of_mass_destruction.animation.PauseAnimationTimer;
import com.cerbon.bosses_of_mass_destruction.client.render.*;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.*;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.*;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithArmorRenderer;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithBoneLight;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEntity;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.*;
import com.cerbon.bosses_of_mass_destruction.entity.util.SimpleLivingGeoRenderer;
import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import com.cerbon.bosses_of_mass_destruction.item.custom.SoulStarEntity;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ParticleFactories;
import com.cerbon.bosses_of_mass_destruction.projectile.MagicMissileProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.PetalBladeProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.comet.CometCodeAnimations;
import com.cerbon.bosses_of_mass_destruction.projectile.comet.CometProjectile;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.general.data.WeakHashPredicate;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.registry.RegistryEntry;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistries;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistry;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import com.mojang.blaze3d.Blaze3D;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;

public class BMDEntities {
    public static final BMDConfig mobConfig = AutoConfig.getConfigHolder(BMDConfig.class).getConfig();

    public static final ResourcefulRegistry<EntityType<?>> ENTITY_TYPES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, BMDConstants.MOD_ID);

    public static final RegistryEntry<EntityType<LichEntity>> LICH = ENTITY_TYPES.register("lich",
            () -> EntityType.Builder.<LichEntity>of((entityType, level) -> new LichEntity(entityType, level, mobConfig.lichConfig), MobCategory.MONSTER)
                    .sized(1.8f, 3.0f)
                    .updateInterval(1)
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "lich").toString()));

    public static final RegistryEntry<EntityType<MagicMissileProjectile>> MAGIC_MISSILE = ENTITY_TYPES.register("blue_fireball",
            () -> EntityType.Builder.<MagicMissileProjectile>of(MagicMissileProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "blue_fireball").toString()));

    public static final RegistryEntry<EntityType<CometProjectile>> COMET = ENTITY_TYPES.register("comet",
            () -> EntityType.Builder.<CometProjectile>of(CometProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "comet").toString()));

    public static final RegistryEntry<EntityType<SoulStarEntity>> SOUL_STAR = ENTITY_TYPES.register("soul_star",
            () -> EntityType.Builder.<SoulStarEntity>of(SoulStarEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "soul_star").toString()));

    public static final RegistryEntry<EntityType<ChargedEnderPearlEntity>> CHARGED_ENDER_PEARL = ENTITY_TYPES.register("charged_ender_pearl",
            () -> EntityType.Builder.of(ChargedEnderPearlEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "charged_ender_pearl").toString()));

    public static final RegistryEntry<EntityType<ObsidilithEntity>> OBSIDILITH = ENTITY_TYPES.register("obsidilith",
            () -> EntityType.Builder.<ObsidilithEntity>of((entityType, level) -> new ObsidilithEntity(entityType, level, mobConfig.obsidilithConfig), MobCategory.MONSTER)
                    .sized(2.0f, 4.4f)
                    .fireImmune()
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "obsidilith").toString()));

    public static final RegistryEntry<EntityType<GauntletEntity>> GAUNTLET = ENTITY_TYPES.register("gauntlet",
            () -> EntityType.Builder.<GauntletEntity>of((entityType, level) -> new GauntletEntity(entityType, level, mobConfig.gauntletConfig) , MobCategory.MONSTER)
                    .sized(5.0f, 4.0f)
                    .fireImmune()
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "gauntlet").toString()));

    public static final RegistryEntry<EntityType<VoidBlossomEntity>> VOID_BLOSSOM = ENTITY_TYPES.register("void_blossom",
            () -> EntityType.Builder.<VoidBlossomEntity>of((entityType, level) -> new VoidBlossomEntity(entityType, level, mobConfig.voidBlossomConfig), MobCategory.MONSTER)
                    .sized(8.0f, 10.0f)
                    .fireImmune()
                    .clientTrackingRange(3)
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "void_blossom").toString()));

    public static final RegistryEntry<EntityType<SporeBallProjectile>> SPORE_BALL = ENTITY_TYPES.register("spore_ball",
            () -> EntityType.Builder.<SporeBallProjectile>of(SporeBallProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "spore_ball").toString()));

    public static final RegistryEntry<EntityType<PetalBladeProjectile>> PETAL_BLADE = ENTITY_TYPES.register("petal_blade",
            () -> EntityType.Builder.<PetalBladeProjectile>of(PetalBladeProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "petal_blade").toString()));

    public static final LichKillCounter killCounter = new LichKillCounter(mobConfig.lichConfig.summonMechanic);

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        PauseAnimationTimer pauseSecondTimer = new PauseAnimationTimer(Blaze3D::getTime, () -> Minecraft.getInstance().isPaused());

        EntityRenderers.register(LICH.get(), context -> {
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/lich.png");
            return new SimpleLivingGeoRenderer<>(
                    context,
                    new GeoModel<>(
                            lichEntity -> ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "geo/lich.geo.json"),
                            entity -> texture,
                            ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "animations/lich.animation.json"),
                            new LichCodeAnimations(),
                            RenderType::entityCutoutNoCull
                    ),
                    new BoundedLighting<>(5),
                    new LichBoneLight(),
                    new EternalNightRenderer(),
                    null,
                    null,
                    true
            );
        });

        EntityRenderers.register(OBSIDILITH.get(), context -> {
            ObsidilithBoneLight runeColorHandler = new ObsidilithBoneLight();
            GeoModel<ObsidilithEntity> modelProvider = new GeoModel<>(
                    entity -> ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "geo/obsidilith.geo.json"),
                    entity -> ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/obsidilith.png"),
                    ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "animations/obsidilith.animation.json"),
                    (animatable, data, geoModel) -> {},
                    RenderType::entityCutout
            );
            ObsidilithArmorRenderer armorRenderer = new ObsidilithArmorRenderer(modelProvider, context);
            return new SimpleLivingGeoRenderer<>(
                    context,
                    modelProvider,
                    null,
                    runeColorHandler,
                    new CompositeRenderer<>(armorRenderer, runeColorHandler),
                    armorRenderer,
                    null,
                    false
            );
        });

        EntityRenderers.register(COMET.get(), context ->
                new SimpleLivingGeoRenderer<>(
                        context,
                        new GeoModel<>(
                                geoAnimatable -> ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "geo/comet.geo.json"),
                                entity -> ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/comet.png"),
                                ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "animations/comet.animation.json"),
                                new CometCodeAnimations(),
                                RenderType::entityCutout
                        ),
                        new FullRenderLight<>(),
                        null,
                        new ConditionalRenderer<>(
                                new WeakHashPredicate<>(() -> new FrameLimiter(60f, pauseSecondTimer)::canDoFrame),
                                new LerpedPosRenderer<>(vec3 -> ParticleFactories.cometTrail().build(vec3.add(RandomUtils.randVec().scale(0.5)), Vec3.ZERO))
                        ),
                        null,
                        null,
                        true
                ));

        EntityRenderers.register(SOUL_STAR.get(), context ->
                new ThrownItemRenderer<>(context, 1.0f, true));

        EntityRenderers.register(CHARGED_ENDER_PEARL.get(), ThrownItemRenderer::new);

        ResourceLocation missileTexture = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/blue_magic_missile.png");
        RenderType magicMissileRenderType = RenderType.entityCutoutNoCull(missileTexture);
        EntityRenderers.register(MAGIC_MISSILE.get(), context ->
                new SimpleEntityRenderer<>(context,
                        new CompositeRenderer<>(
                                new BillboardRenderer<>(context.getEntityRenderDispatcher(), magicMissileRenderType, f -> 0.5f),
                                new ConditionalRenderer<>(
                                        new WeakHashPredicate<>(() -> new FrameLimiter(20f, pauseSecondTimer)::canDoFrame),
                                        new LerpedPosRenderer<>(vec3 -> ParticleFactories.soulFlame().build(vec3.add(RandomUtils.randVec().scale(0.25)), Vec3.ZERO)))),
                        entity -> missileTexture,
                        new FullRenderLight<>()
                ));

        EntityRenderers.register(GAUNTLET.get(), context -> {
            GeoModel<GauntletEntity> modelProvider = new GeoModel<>(
                    entity -> ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "geo/gauntlet.geo.json"),
                    new GauntletTextureProvider(),
                    ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "animations/gauntlet.animation.json"),
                    new GauntletCodeAnimations(),
                    RenderType::entityCutout
            );
            GauntletEnergyRenderer energyRenderer = new GauntletEnergyRenderer(modelProvider, context);
            GauntletOverlay overlayOverride = new GauntletOverlay();
            return new SimpleLivingGeoRenderer<>(
                    context,
                    modelProvider,
                    null,
                    null,
                    new CompositeRenderer<>(
                            new GauntletLaserRenderer(),
                            new ConditionalRenderer<>(
                                    new WeakHashPredicate<>(() -> new FrameLimiter(20f, pauseSecondTimer)::canDoFrame),
                                    new LaserParticleRenderer()
                            ),
                            energyRenderer,
                            overlayOverride
                    ),
                    energyRenderer,
                    overlayOverride,
                    false
            );
        });

        EntityRenderers.register(VOID_BLOSSOM.get(), context -> {
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/void_blossom.png");
            GeoModel<VoidBlossomEntity> modelProvider = new GeoModel<>(
                    entity -> ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "geo/void_blossom.geo.json"),
                    entity -> texture,
                    ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "animations/void_blossom.animation.json"),
                    new VoidBlossomCodeAnimations(),
                    RenderType::entityCutout
            );
            VoidBlossomBoneLight boneLight = new VoidBlossomBoneLight();
            NoRedOnDeathOverlay overlay = new NoRedOnDeathOverlay();
            return new SimpleLivingGeoRenderer<>(
                    context,
                    modelProvider,
                    null,
                    boneLight,
                    new CompositeRenderer<>(new VoidBlossomSpikeRenderer(), boneLight, overlay),
                    null,
                    overlay,
                    false
            );
        });

        EntityRenderers.register(SPORE_BALL.get(), context -> {
            SporeBallOverlay explosionFlasher = new SporeBallOverlay();
            return new SimpleLivingGeoRenderer<>(
                    context,
                    new GeoModel<>(
                            geoAnimatable -> ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "geo/comet.geo.json"),
                            entity -> ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/spore.png"),
                            ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "animations/comet.animation.json"),
                            new SporeCodeAnimations(),
                            RenderType::entityCutout),
                    new FullRenderLight<>(),
                    null,
                    new CompositeRenderer<>(
                            new ConditionalRenderer<>(
                                    new WeakHashPredicate<>(() -> new FrameLimiter(60f, pauseSecondTimer)::canDoFrame),
                                    new LerpedPosRenderer<>(vec3 -> {
                                        ClientParticleBuilder projectileParticles = new ClientParticleBuilder(BMDParticles.OBSIDILITH_BURST.get())
                                                .color(Vec3Colors.GREEN)
                                                .colorVariation(0.4)
                                                .scale(0.5f)
                                                .brightness(BMDParticles.FULL_BRIGHT);
                                        projectileParticles.build(vec3.add(RandomUtils.randVec().scale(0.25)), VecUtils.yAxis.scale(0.1));
                                    })),
                            explosionFlasher,
                            new SporeBallSizeRenderer()),
                    null,
                    explosionFlasher,
                    true);
        });

        ResourceLocation petalTexture = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/petal_blade.png");
        RenderType petalBladeRenderType = RenderType.entityCutoutNoCull(petalTexture);
        EntityRenderers.register(PETAL_BLADE.get(), context ->
                new SimpleEntityRenderer<>(context,
                        new CompositeRenderer<>(
                                new PetalBladeRenderer(context.getEntityRenderDispatcher(), petalBladeRenderType),
                                new ConditionalRenderer<>(
                                        new WeakHashPredicate<>(() -> new FrameLimiter(30f, pauseSecondTimer)::canDoFrame),
                                        new PetalBladeParticleRenderer<>()
                                )),
                        entity -> petalTexture,
                        new FullRenderLight<>()));
    }

    public static void register() {
        ENTITY_TYPES.register();
    }
}
