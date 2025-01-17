package umpaz.brewinandchewin.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.loot.condition.AreaLocationCheckCondition;
import umpaz.brewinandchewin.common.loot.condition.NullTrueBlockStateCondition;

public class BnCLootConditions {

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, BrewinAndChewin.MODID);

    public static final RegistryObject<LootItemConditionType> AREA_LOCATION_CHECK = LOOT_CONDITIONS.register("area_location_check", () -> new LootItemConditionType(new AreaLocationCheckCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> NULL_TRUE_BLOCK_STATE = LOOT_CONDITIONS.register("null_true_block_state", () -> new LootItemConditionType(new NullTrueBlockStateCondition.Serializer()));
}
