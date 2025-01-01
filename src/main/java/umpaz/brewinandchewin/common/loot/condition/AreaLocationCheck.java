package umpaz.brewinandchewin.common.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import umpaz.brewinandchewin.common.registry.BnCLootConditions;

public class AreaLocationCheck implements LootItemCondition {
    final LocationPredicate predicate;
    final int range;

    AreaLocationCheck(LocationPredicate pLocationPredicate, int range) {
        this.predicate = pLocationPredicate;
        this.range = range;
    }

    public LootItemConditionType getType() {
        return BnCLootConditions.AREA_LOCATION_CHECK.get();
    }

    public boolean test(LootContext context) {
        Vec3 vec3 = context.getParamOrNull(LootContextParams.ORIGIN);
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    Vec3 offset = vec3.add(x, y, z);
                    if (predicate.matches(context.getLevel(), offset.x, offset.y, offset.z))
                        return true;
                }
            }
        }
        return false;
    }

    public static LootItemCondition.Builder checkArea(LocationPredicate.Builder pLocationPredicateBuilder, int range) {
        return () -> {
            return new AreaLocationCheck(pLocationPredicateBuilder.build(), range);
        };
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<AreaLocationCheck> {
        public void serialize(JsonObject json, AreaLocationCheck areaLocationCheck, JsonSerializationContext context) {
            json.add("predicate", areaLocationCheck.predicate.serializeToJson());
            json.addProperty("range", areaLocationCheck.range);
        }

        /**
         * Deserialize a value by reading it from the JsonObject.
         */
        public AreaLocationCheck deserialize(JsonObject json, JsonDeserializationContext context) {
            LocationPredicate predicate = LocationPredicate.fromJson(json.get("predicate"));
            int range = GsonHelper.getAsInt(json, "range", 0);
            return new AreaLocationCheck(predicate, range);
        }
    }
}