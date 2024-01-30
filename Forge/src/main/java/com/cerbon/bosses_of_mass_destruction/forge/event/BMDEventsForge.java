package com.cerbon.bosses_of_mass_destruction.forge.event;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDCreativeModeTabs;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = BMDConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BMDEventsForge {

    @SubscribeEvent
    protected static void addCreativeTab(@NotNull BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == BMDCreativeModeTabs.BOSSES_OF_MASS_DESTRUCTION.get()) {
            event.accept(BMDItems.MOB_WARD.get());
            event.accept(BMDBlocks.MONOLITH_BLOCK.get());
            event.accept(BMDBlocks.LEVITATION_BLOCK.get());
            event.accept(BMDBlocks.VOID_LILY_BLOCK.get());
            event.accept(BMDItems.SOUL_STAR.get());
            event.accept(BMDItems.ANCIENT_ANIMA.get());
            event.accept(BMDItems.BLAZING_EYE.get());
            event.accept(BMDItems.OBSIDIAN_HEART.get());
            event.accept(BMDItems.EARTHDIVE_SPEAR.get());
            event.accept(BMDItems.VOID_THORN.get());
            event.accept(BMDItems.CRYSTAL_FRUIT.get());
            event.accept(BMDItems.CHARGED_ENDER_PEARL.get());
            event.accept(BMDItems.BRIMSTONE_NECTAR.get());
        }
    }

    @SubscribeEvent
    public static void createAttributes(EntityAttributeCreationEvent event){
        event.put(BMDEntities.LICH.get(), Mob.createMobAttributes()
                .add(Attributes.FLYING_SPEED, 5.0)
                .add(Attributes.MAX_HEALTH, BMDEntities.mobConfig.lichConfig.health)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_DAMAGE, BMDEntities.mobConfig.lichConfig.missile.damage)
                .build());

        event.put(BMDEntities.OBSIDILITH.get(), Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, BMDEntities.mobConfig.obsidilithConfig.health)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.ATTACK_DAMAGE, BMDEntities.mobConfig.obsidilithConfig.attack)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10)
                .add(Attributes.ARMOR, BMDEntities.mobConfig.obsidilithConfig.armor)
                .build());

        event.put(BMDEntities.GAUNTLET.get(), Mob.createMobAttributes()
                .add(Attributes.FLYING_SPEED, 4.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.MAX_HEALTH, BMDEntities.mobConfig.gauntletConfig.health)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10)
                .add(Attributes.ATTACK_DAMAGE, BMDEntities.mobConfig.gauntletConfig.attack)
                .add(Attributes.ARMOR, BMDEntities.mobConfig.gauntletConfig.armor)
                .build());

        event.put(BMDEntities.VOID_BLOSSOM.get(), Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, BMDEntities.mobConfig.voidBlossomConfig.health)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.ATTACK_DAMAGE, BMDEntities.mobConfig.voidBlossomConfig.attack)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10.0)
                .add(Attributes.ARMOR, BMDEntities.mobConfig.voidBlossomConfig.armor)
                .build());
    }
}
