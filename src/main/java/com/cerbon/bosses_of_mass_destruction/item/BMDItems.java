package com.cerbon.bosses_of_mass_destruction.item;

import com.cerbon.bosses_of_mass_destruction.item.custom.CrystalFruitItem;
import com.cerbon.bosses_of_mass_destruction.item.custom.MaterialItem;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BMDItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BMDConstants.MOD_ID);

    public static final RegistryObject<Item> ANCIENT_ANIMA = ITEMS.register("ancient_anima",
            () -> new MaterialItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> BLAZING_EYE = ITEMS.register("blazing_eye",
            () -> new MaterialItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));

    public static final RegistryObject<Item> OBSIDIAN_HEART = ITEMS.register("obsidian_heart",
            () -> new MaterialItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));

    public static final RegistryObject<Item> VOID_THORN = ITEMS.register("void_thorn",
            () -> new MaterialItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));

    public static final RegistryObject<Item> CRYSTAL_FRUIT = ITEMS.register("crystal_fruit",
            () -> new CrystalFruitItem(new Item.Properties().rarity(Rarity.RARE).fireResistant().food(BMDFoods.CRYSTAL_FRUIT)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
