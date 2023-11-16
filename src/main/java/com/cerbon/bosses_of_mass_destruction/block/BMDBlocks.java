package com.cerbon.bosses_of_mass_destruction.block;

import com.cerbon.bosses_of_mass_destruction.block.custom.*;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BMDBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BMDConstants.MOD_ID);

    public static final RegistryObject<Block> OBSIDILITH_RUNE = BLOCKS.register("obsidilith_rune",
            () -> new ObsidilithRuneBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .requiresCorrectToolForDrops()
                            .strength(50.0F, 1200.0F)
            ));

    public static final RegistryObject<Block> VOID_BLOSSOM = BLOCKS.register("void_blossom",
            () -> new VoidBlossomBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .instabreak()
                            .noCollission()
                            .lightLevel(value -> 11)
                            .sound(SoundType.SPORE_BLOSSOM)
            ));

    public static final RegistryObject<Block> VINE_WALL = BLOCKS.register("vine_wall",
            () -> new VineWallBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GREEN)
                            .sound(SoundType.WOOD)
                            .strength(2.0f, 6.0f)
            ));

    public static final RegistryObject<Block> OBSIDILITH_SUMMON_BLOCK = BLOCKS.register("obsidilith_end_frame",
            () -> new ObsidilithSummonBlock(BlockBehaviour.Properties.copy(Blocks.END_PORTAL_FRAME)));

    public static final RegistryObject<Block> GAUNTLET_BLACKSTONE = BLOCKS.register("gauntlet_blackstone",
            () -> new GauntletBlackstoneBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .requiresCorrectToolForDrops()
                            .strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> CHISELED_STONE_ALTAR = BLOCKS.register("chiseled_stone_altar",
            () -> new ChiseledStoneAltarBlock(
                    BlockBehaviour.Properties.copy(Blocks.BEDROCK)
                            .lightLevel(blockState -> blockState.getValue(BlockStateProperties.LIT) ? 11 : 0)
            ));

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
