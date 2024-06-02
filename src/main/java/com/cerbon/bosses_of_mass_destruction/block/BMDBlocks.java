package com.cerbon.bosses_of_mass_destruction.block;

import com.cerbon.bosses_of_mass_destruction.block.custom.*;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BMDBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BMDConstants.MOD_ID);

    public static final RegistryObject<Block> OBSIDILITH_RUNE = BLOCKS.register("obsidilith_rune",
            () -> new ObsidilithRuneBlock(AbstractBlock.Properties.of(Material.STONE, DyeColor.BLACK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> VOID_BLOSSOM = BLOCKS.register("void_blossom",
            () -> new VoidBlossomBlock(AbstractBlock.Properties.of(Material.PLANT, DyeColor.BLACK).instabreak().noCollission().lightLevel(value -> 11).sound(SoundType.GRASS)));

    public static final RegistryObject<Block> VINE_WALL = BLOCKS.register("vine_wall",
            () -> new VineWallBlock(AbstractBlock.Properties.of(Material.PLANT, DyeColor.GREEN).sound(SoundType.WOOD).strength(2.0f, 6.0f)));

    public static final RegistryObject<Block> OBSIDILITH_SUMMON_BLOCK = BLOCKS.register("obsidilith_end_frame",
            () -> new ObsidilithSummonBlock(AbstractBlock.Properties.copy(Blocks.END_PORTAL_FRAME)));

    public static final RegistryObject<Block> GAUNTLET_BLACKSTONE = BLOCKS.register("gauntlet_blackstone",
            () -> new GauntletBlackstoneBlock(AbstractBlock.Properties.of(Material.STONE, DyeColor.BLACK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> SEALED_BLACKSTONE = BLOCKS.register("sealed_blackstone",
            () -> new Block(AbstractBlock.Properties.copy(Blocks.BEDROCK)));

    public static final RegistryObject<Block> CHISELED_STONE_ALTAR = BLOCKS.register("chiseled_stone_altar",
            () -> new ChiseledStoneAltarBlock(AbstractBlock.Properties.copy(Blocks.BEDROCK).lightLevel(blockState -> blockState.getValue(BlockStateProperties.LIT) ? 11 : 0)));

    public static final RegistryObject<Block> MOB_WARD = BLOCKS.register("mob_ward",
            () -> new MobWardBlock(AbstractBlock.Properties.of(Material.STONE, DyeColor.BLACK).requiresCorrectToolForDrops().noOcclusion().lightLevel(value -> 15).strength(10.0F, 1200.0F)));

    public static final RegistryObject<Block> MONOLITH_BLOCK = BLOCKS.register("monolith_block",
            () -> new MonolithBlock(AbstractBlock.Properties.of(Material.METAL, DyeColor.BLACK).requiresCorrectToolForDrops().noOcclusion().lightLevel(value -> 4).strength(10.0F, 1200.0F)));

    public static final RegistryObject<Block> LEVITATION_BLOCK = BLOCKS.register("levitation_block",
            () -> new LevitationBlock(AbstractBlock.Properties.of(Material.STONE, DyeColor.BLUE).requiresCorrectToolForDrops().noOcclusion().lightLevel(value -> 4).strength(10.0F, 1200.0F)));

    public static final RegistryObject<Block> VOID_BLOSSOM_SUMMON_BLOCK = BLOCKS.register("void_blossom_block",
            () -> new VoidBlossomSummonBlock(AbstractBlock.Properties.copy(Blocks.BEDROCK)));

    public static final RegistryObject<Block> VOID_LILY_BLOCK = BLOCKS.register("void_lily",
            () -> new VoidLilyBlock(AbstractBlock.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).lightLevel(value -> 8)));

    public static final RegistryObject<Block> LIGHT = BLOCKS.register("light", () ->
            new LightBlock(AbstractBlock.Properties.of(Material.AIR).strength(-1.0F, 3600000.8F).noCollission().noOcclusion().lightLevel(LightBlock.LIGHT_EMISSION)));

    @OnlyIn(Dist.CLIENT)
    public static void initClient(){
        RenderTypeLookup.setRenderLayer(MOB_WARD.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(VOID_BLOSSOM.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(VINE_WALL.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(VOID_LILY_BLOCK.get(), RenderType.cutout());
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
