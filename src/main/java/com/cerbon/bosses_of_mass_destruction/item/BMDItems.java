package com.cerbon.bosses_of_mass_destruction.item;

import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlItem;
import com.cerbon.bosses_of_mass_destruction.item.custom.CrystalFruitItem;
import com.cerbon.bosses_of_mass_destruction.item.custom.EarthdiveSpear;
import com.cerbon.bosses_of_mass_destruction.item.custom.MaterialItem;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
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

    public static final RegistryObject<Item> EARTHDIVE_SPEAR = ITEMS.register("earthdive_spear",
            () -> new EarthdiveSpear(new Item.Properties().fireResistant().durability(250)));

    public static final RegistryObject<Item> VOID_THORN = ITEMS.register("void_thorn",
            () -> new MaterialItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));

    public static final RegistryObject<Item> CRYSTAL_FRUIT = ITEMS.register("crystal_fruit",
            () -> new CrystalFruitItem(new Item.Properties().rarity(Rarity.RARE).fireResistant().food(BMDFoods.CRYSTAL_FRUIT)));

    public static final RegistryObject<Item> CHARGED_ENDER_PEARL = ITEMS.register("charged_ender_pearl",
            () -> new ChargedEnderPearlItem(new Item.Properties().fireResistant().stacksTo(1)));

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
