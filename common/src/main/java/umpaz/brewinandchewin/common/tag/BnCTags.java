package umpaz.brewinandchewin.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import umpaz.brewinandchewin.BrewinAndChewin;

public class BnCTags {

    public static final TagKey<Item> FERMENTED_DRINKS = modItemTag("fermented_drinks");
    public static final TagKey<Item> HORROR_MEATS = modItemTag("horror_meats");
    public static final TagKey<Item> RAW_MEATS = modItemTag("raw_meats");
    public static final TagKey<Item> PIZZA_TOPPINGS = modItemTag("pizza_toppings");
    public static final TagKey<Item> CHEESE_WEDGES = modItemTag("cheese_wedges");

    public static final TagKey<Block> FREEZE_SOURCES = modBlockTag("freeze_sources");

    public static final TagKey<MobEffect> MILK_BOTTLE_LOW_PRIORITY = modEffectTag("low_priority/milk_bottle");
    public static final TagKey<MobEffect> HOT_COCOA_LOW_PRIORITY = modEffectTag("low_priority/hot_cocoa");

    public static final TagKey<DamageType> TRIGGERS_RAGING = modDamageTypeTag("triggers_raging");


    private static TagKey<Item> modItemTag(String path) {
        return ItemTags.create(new ResourceLocation(BrewinAndChewin.MODID, path));
    }

    private static TagKey<Block> modBlockTag(String path) {
        return BlockTags.create(new ResourceLocation(BrewinAndChewin.MODID, path));
    }

    private static TagKey<MobEffect> modEffectTag(String path) {
        return TagKey.create(Registries.MOB_EFFECT, new ResourceLocation(BrewinAndChewin.MODID, path));
    }

    private static TagKey<DamageType> modDamageTypeTag(String path) {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(BrewinAndChewin.MODID, path));
    }
}
