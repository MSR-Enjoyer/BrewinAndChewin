package umpaz.brewinandchewin.fabric.registry;

import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;
import net.minecraft.core.Registry;
import umpaz.brewinandchewin.fabric.loot.modifier.BnCSlicingModifier;

public class BnCLootModifiers {
    public static void registerAll() {
        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BnCSlicingModifier.ID, BnCSlicingModifier.CODEC);
    }
}
