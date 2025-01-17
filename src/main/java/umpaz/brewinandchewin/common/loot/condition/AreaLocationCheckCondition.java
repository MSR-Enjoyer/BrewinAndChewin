package umpaz.brewinandchewin.common.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import umpaz.brewinandchewin.common.access.LootParamsParamSetAccess;
import umpaz.brewinandchewin.common.mixin.LootContextAccessor;
import umpaz.brewinandchewin.common.mixin.LootParamsAccessor;
import umpaz.brewinandchewin.common.registry.BnCLootConditions;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class AreaLocationCheckCondition implements LootItemCondition {
    private final LootItemCondition[] predicates;
    private final int range;

    protected AreaLocationCheckCondition(LootItemCondition[] predicates, int range) {
        this.predicates = predicates;
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
                    LootParams.Builder paramBuilder = new LootParams.Builder(context.getLevel());
                    LootParams originalParams = ((LootContextAccessor)context).brewinandchewin$getParams();
                    for (Map.Entry<LootContextParam<?>, Object> entry : ((LootParamsAccessor)originalParams).brewinandchewin$getParams().entrySet()) {
                        paramBuilder.withParameter((LootContextParam) entry.getKey(), entry.getValue());
                    }
                    paramBuilder.withParameter(LootContextParams.ORIGIN, offset);
                    if (context.hasParam(LootContextParams.BLOCK_STATE))
                        paramBuilder.withOptionalParameter(LootContextParams.BLOCK_STATE, context.getLevel().getBlockState(BlockPos.containing(offset)));
                    if (context.hasParam(LootContextParams.BLOCK_ENTITY))
                        paramBuilder.withOptionalParameter(LootContextParams.BLOCK_ENTITY, context.getLevel().getBlockEntity(BlockPos.containing(offset)));
                    LootContext newCtx = new LootContext.Builder(paramBuilder.create(((LootParamsParamSetAccess) originalParams).brewinandchewin$getParamSet())).create(null);
                    if (Arrays.stream(predicates).allMatch(condition -> condition.test(newCtx)))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN);
    }

    @Override
    public void validate(ValidationContext pContext) {
        LootItemCondition.super.validate(pContext);

        for(int i = 0; i < this.predicates.length; ++i)
            this.predicates[i].validate(pContext.forChild(".term[" + i + "]"));

    }

    public static LootItemCondition.Builder checkArea(int range, LootItemCondition.Builder... predicateBuilder) {
        return () -> new AreaLocationCheckCondition(Arrays.stream(predicateBuilder).map(Builder::build).toArray(LootItemCondition[]::new), range);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<AreaLocationCheckCondition> {
        public void serialize(JsonObject json, AreaLocationCheckCondition areaLocationCheck, JsonSerializationContext context) {
            json.add("predicate", context.serialize(areaLocationCheck.predicates));
            json.addProperty("range", areaLocationCheck.range);
        }

        /**
         * Deserialize a value by reading it from the JsonObject.
         */
        public AreaLocationCheckCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            LootItemCondition[] predicate = GsonHelper.getAsObject(json, "predicate", context, LootItemCondition[].class);
            int range = GsonHelper.getAsInt(json, "range", 0);
            return new AreaLocationCheckCondition(predicate, range);
        }
    }
}