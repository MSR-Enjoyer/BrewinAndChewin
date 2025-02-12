package umpaz.brewinandchewin.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import umpaz.brewinandchewin.BrewinAndChewin;

public class BnCTags {

    public static final TagKey<Item> FERMENTED_DRINKS = modItemTag("fermented_drinks");
    public static final TagKey<Item> FOOD_HORROR_MEAT = modItemTag("foods/horror_meat");
    public static final TagKey<Item> FOOD_RAW_MEAT = modItemTag("foods/raw_meat");
    public static final TagKey<Item> FOOD_PIZZA_TOPPING = modItemTag("foods/pizza_topping");
    public static final TagKey<Item> FOOD_CHEESE_WEDGE = modItemTag("foods/cheese_wedge");

    public static final TagKey<Block> FREEZE_SOURCES = modBlockTag("freeze_sources");

    public static final TagKey<EntityType<?>> IMMUNE_TO_INTOXICATION = modEntityTypeTag("immune_to_intoxication");

    public static final TagKey<MobEffect> MILK_BOTTLE_LOW_PRIORITY = modEffectTag("low_priority/milk_bottle");
    public static final TagKey<MobEffect> HOT_COCOA_LOW_PRIORITY = modEffectTag("low_priority/hot_cocoa");

    public static final TagKey<DamageType> TRIGGERS_RAGING = modDamageTypeTag("triggers_raging");


    private static TagKey<Item> modItemTag(String path) {
        return TagKey.create(Registries.ITEM, BrewinAndChewin.asResource(path));
    }

    private static TagKey<Block> modBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, BrewinAndChewin.asResource(path));
    }

    private static TagKey<EntityType<?>> modEntityTypeTag(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, BrewinAndChewin.asResource(path));
    }

    private static TagKey<MobEffect> modEffectTag(String path) {
        return TagKey.create(Registries.MOB_EFFECT, BrewinAndChewin.asResource(path));
    }

    private static TagKey<DamageType> modDamageTypeTag(String path) {
        return TagKey.create(Registries.DAMAGE_TYPE, BrewinAndChewin.asResource(path));
    }
}
