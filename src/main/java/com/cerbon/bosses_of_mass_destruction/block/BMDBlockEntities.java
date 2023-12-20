package com.cerbon.bosses_of_mass_destruction.block;

import com.cerbon.bosses_of_mass_destruction.block.custom.*;
import com.cerbon.bosses_of_mass_destruction.client.render.BMDBlockEntityRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BMDBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BMDConstants.MOD_ID);

    public static final Supplier<BlockEntityType<MobWardBlockEntity>> MOB_WARD = BLOCKS_ENTITIES.register("mob_ward",
            () -> BlockEntityType.Builder.of(MobWardBlockEntity::new, BMDBlocks.MOB_WARD.get()).build(null));

    public static final Supplier<BlockEntityType<MonolithBlockEntity>> MONOLITH_BLOCK_ENTITY = BLOCKS_ENTITIES.register("monolith_block",
            () -> BlockEntityType.Builder.of(MonolithBlockEntity::new, BMDBlocks.MONOLITH_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<LevitationBlockEntity>> LEVITATION_BLOCK_ENTITY = BLOCKS_ENTITIES.register("levitation_block",
            () -> BlockEntityType.Builder.of(LevitationBlockEntity::new, BMDBlocks.LEVITATION_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<VoidBlossomSummonBlockEntity>> VOID_BLOSSOM_SUMMON_BLOCK_ENTITY = BLOCKS_ENTITIES.register("void_blossom_block",
            () -> BlockEntityType.Builder.of(VoidBlossomSummonBlockEntity::new, BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<VoidLilyBlockEntity>> VOID_LILY_BLOCK_ENTITY = BLOCKS_ENTITIES.register("void_lily",
            () -> BlockEntityType.Builder.of(VoidLilyBlockEntity::new, BMDBlocks.VOID_LILY_BLOCK.get()).build(null));

    @OnlyIn(Dist.CLIENT)
    public static void initClient(){
        BlockEntityRenderers.register(LEVITATION_BLOCK_ENTITY.get(), context ->
                new BMDBlockEntityRenderer<>(
                        new GeoModel<>(
                                entity -> new ResourceLocation(BMDConstants.MOD_ID, "geo/levitation_block.geo.json"),
                                entity -> new ResourceLocation(BMDConstants.MOD_ID, "textures/block/levitation_block.png"),
                                new ResourceLocation(BMDConstants.MOD_ID, "animations/levitation_block.animation.json"),
                                (animatable, data, geoModel) -> {},
                                RenderType::entityCutout
                        ),
                        (bone, packedLight) -> IBoneLight.fullbright
                )
        );
    }

    public static void register(IEventBus eventBus){
        BLOCKS_ENTITIES.register(eventBus);
    }
}
