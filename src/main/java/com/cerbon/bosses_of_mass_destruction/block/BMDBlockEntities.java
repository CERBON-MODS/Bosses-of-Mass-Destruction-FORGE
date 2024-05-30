package com.cerbon.bosses_of_mass_destruction.block;

import com.cerbon.bosses_of_mass_destruction.block.custom.*;
import com.cerbon.bosses_of_mass_destruction.client.render.BMDBlockEntityRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BMDBlockEntities {
    public static final DeferredRegister<TileEntityType<?>> BLOCKS_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BMDConstants.MOD_ID);

    public static final RegistryObject<TileEntityType<MobWardBlockEntity>> MOB_WARD = BLOCKS_ENTITIES.register("mob_ward",
            () -> TileEntityType.Builder.of(MobWardBlockEntity::new, BMDBlocks.MOB_WARD.get()).build(null));

    public static final RegistryObject<TileEntityType<MonolithBlockEntity>> MONOLITH_BLOCK_ENTITY = BLOCKS_ENTITIES.register("monolith_block",
            () -> TileEntityType.Builder.of(MonolithBlockEntity::new, BMDBlocks.MONOLITH_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<LevitationBlockEntity>> LEVITATION_BLOCK_ENTITY = BLOCKS_ENTITIES.register("levitation_block",
            () -> TileEntityType.Builder.of(LevitationBlockEntity::new, BMDBlocks.LEVITATION_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<VoidBlossomSummonBlockEntity>> VOID_BLOSSOM_SUMMON_BLOCK_ENTITY = BLOCKS_ENTITIES.register("void_blossom_block",
            () -> TileEntityType.Builder.of(VoidBlossomSummonBlockEntity::new, BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<VoidLilyBlockEntity>> VOID_LILY_BLOCK_ENTITY = BLOCKS_ENTITIES.register("void_lily",
            () -> TileEntityType.Builder.of(VoidLilyBlockEntity::new, BMDBlocks.VOID_LILY_BLOCK.get()).build(null));

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        ClientRegistry.bindTileEntityRenderer(LEVITATION_BLOCK_ENTITY.get(), context ->
                new BMDBlockEntityRenderer<LevitationBlockEntity>(
                        context,
                        new GeoModel<>(
                                entity -> new ResourceLocation(BMDConstants.MOD_ID, "geo/levitation_block.geo.json"),
                                entity -> new ResourceLocation(BMDConstants.MOD_ID, "textures/block/levitation_block.png"),
                                new ResourceLocation(BMDConstants.MOD_ID, "animations/levitation_block.animation.json"),
                                (animatable, data, geoModel) -> {}
                        ),
                        (bone, packedLight) -> IBoneLight.fullbright
                )
        );
    }

    public static void register(IEventBus eventBus){
        BLOCKS_ENTITIES.register(eventBus);
    }
}
