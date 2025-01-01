package umpaz.brewinandchewin.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.loot.condition.AreaLocationCheck;

public class BnCLootConditions {

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, BrewinAndChewin.MODID);

    public static final RegistryObject<LootItemConditionType> AREA_LOCATION_CHECK = LOOT_CONDITIONS.register("area_location_check", () -> new LootItemConditionType(new AreaLocationCheck.Serializer()));
}
