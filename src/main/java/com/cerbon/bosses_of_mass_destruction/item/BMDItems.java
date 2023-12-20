package com.cerbon.bosses_of_mass_destruction.item;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.item.custom.*;
import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.GauntletStructureRepair;
import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.LichStructureRepair;
import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.ObsidilithStructureRepair;
import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.VoidBlossomStructureRepair;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class BMDItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(BMDConstants.MOD_ID);

    public static final DeferredItem<SoulStarItem> SOUL_STAR = ITEMS.register("soul_star",
            () -> new SoulStarItem(new Item.Properties()));

    public static final DeferredItem<MaterialItem> ANCIENT_ANIMA = ITEMS.register("ancient_anima",
            () -> new MaterialItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final DeferredItem<MaterialItem> BLAZING_EYE = ITEMS.register("blazing_eye",
            () -> new MaterialItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));

    public static final DeferredItem<MaterialItem> OBSIDIAN_HEART = ITEMS.register("obsidian_heart",
            () -> new MaterialItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));

    public static final DeferredItem<EarthdiveSpear> EARTHDIVE_SPEAR = ITEMS.register("earthdive_spear",
            () -> new EarthdiveSpear(new Item.Properties().fireResistant().durability(250)));

    public static final DeferredItem<MaterialItem> VOID_THORN = ITEMS.register("void_thorn",
            () -> new MaterialItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));

    public static final DeferredItem<CrystalFruitItem> CRYSTAL_FRUIT = ITEMS.register("crystal_fruit",
            () -> new CrystalFruitItem(new Item.Properties().rarity(Rarity.RARE).fireResistant().food(BMDFoods.CRYSTAL_FRUIT)));

    public static final DeferredItem<ChargedEnderPearlItem> CHARGED_ENDER_PEARL = ITEMS.register("charged_ender_pearl",
            () -> new ChargedEnderPearlItem(new Item.Properties().fireResistant().stacksTo(1)));

    public static final DeferredItem<BrimstoneNectarItem> BRIMSTONE_NECTAR = ITEMS.register("brimstone_nectar",
            () -> new BrimstoneNectarItem(new Item.Properties().rarity(Rarity.RARE).fireResistant(), List.of(
                    new GauntletStructureRepair(), new LichStructureRepair(), new ObsidilithStructureRepair(), new VoidBlossomStructureRepair()
            )));

    public static final DeferredItem<BlockItem> OBSIDILITH_RUNE = ITEMS.register("obsidilith_rune",
            () -> new BlockItem(BMDBlocks.OBSIDILITH_RUNE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> CHISELED_STONE_ALTAR = ITEMS.register("chiseled_stone_altar",
            () -> new BlockItem(BMDBlocks.CHISELED_STONE_ALTAR.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> VOID_BLOSSOM = ITEMS.register("void_blossom",
            () -> new BlockItem(BMDBlocks.VOID_BLOSSOM.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> VINE_WALL = ITEMS.register("vine_wall",
            () -> new BlockItem(BMDBlocks.VINE_WALL.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> OBSIDILITH_SUMMON_BLOCK = ITEMS.register("obsidilith_end_frame",
            () -> new BlockItem(BMDBlocks.OBSIDILITH_SUMMON_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> GAUNTLET_BLACKSTONE = ITEMS.register("gauntlet_blackstone",
            () -> new BlockItem(BMDBlocks.GAUNTLET_BLACKSTONE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> SEALED_BLACKSTONE = ITEMS.register("sealed_blackstone",
            () -> new BlockItem(BMDBlocks.SEALED_BLACKSTONE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> MOB_WARD = ITEMS.register("mob_ward",
            () -> new BlockItem(BMDBlocks.MOB_WARD.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> MONOLITH_BLOCK = ITEMS.register("monolith_block",
            () -> new BlockItem(BMDBlocks.MONOLITH_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> LEVITATION_BLOCK = ITEMS.register("levitation_block",
            () -> new BlockItem(BMDBlocks.LEVITATION_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> VOID_BLOSSOM_SUMMON_BLOCK = ITEMS.register("void_blossom_block",
            () -> new BlockItem(BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> VOID_LILY = ITEMS.register("void_lily",
            () -> new BlockItem(BMDBlocks.VOID_LILY_BLOCK.get(), new Item.Properties()));

    @OnlyIn(Dist.CLIENT)
    public static void initClient(){
        ItemProperties.register(EARTHDIVE_SPEAR.get(),
                new ResourceLocation("throwing"),
                (stack, level, entity, seed) -> {
                    if (entity == null)
                        return 0.0f;

                    return (entity.isUsingItem() && entity.getUseItem() == stack) ? 1.0f : 0.0f;
                });
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
