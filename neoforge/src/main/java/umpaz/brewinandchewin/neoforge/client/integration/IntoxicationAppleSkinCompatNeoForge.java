package umpaz.brewinandchewin.neoforge.client.integration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import squeek.appleskin.api.event.FoodValuesEvent;
import umpaz.brewinandchewin.common.registry.BnCEffects;

// TODO: Check if AppleSkin stuff still renders with Intoxicated.
// TODO: If so, be a bad modder and mixin to AppleSkin to abide by Intoxicated rendering.
public class IntoxicationAppleSkinCompatNeoForge {
    public static void init() {
        NeoForge.EVENT_BUS.register(IntoxicationAppleSkinCompatNeoForge.class);
    }

    @SubscribeEvent
    public static void preventSaturationInAppleSkin(FoodValuesEvent event) {
        Player entity = event.player;
        if (entity.hasEffect(BnCEffects.INTOXICATION)) {
            event.modifiedFoodProperties = new FoodProperties(
                    event.modifiedFoodProperties.nutrition(),
                    0.0F,
                    event.modifiedFoodProperties.canAlwaysEat(),
                    event.modifiedFoodProperties.eatSeconds(),
                    event.modifiedFoodProperties.usingConvertsTo(),
                    event.modifiedFoodProperties.effects()
            );
        }
    }
}
