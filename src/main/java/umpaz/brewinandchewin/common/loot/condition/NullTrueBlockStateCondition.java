package umpaz.brewinandchewin.common.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import umpaz.brewinandchewin.common.registry.BnCLootConditions;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Used for checking block states with a value whilst also returning true for null values.
 */
public class NullTrueBlockStateCondition implements LootItemCondition {
    private final List<PropertyMatcher> matchers;

    protected NullTrueBlockStateCondition(List<PropertyMatcher> matchers) {
        this.matchers = matchers;
    }

    public LootItemConditionType getType() {
        return BnCLootConditions.NULL_TRUE_BLOCK_STATE.get();
    }

    public boolean test(LootContext context) {
        BlockState blockState = context.getParam(LootContextParams.BLOCK_STATE);
        return matchers.stream().allMatch(propertyMatcher -> propertyMatcher.match(blockState.getBlock().getStateDefinition(), blockState));
    }

    public static Builder checkState(PropertyMatcher... matchers) {
        return () -> new NullTrueBlockStateCondition(Arrays.stream(matchers).toList());
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<NullTrueBlockStateCondition> {
        public void serialize(JsonObject json, NullTrueBlockStateCondition condition, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            condition.matchers.forEach((p_67683_) -> {
                obj.add(p_67683_.getName(), p_67683_.toJson());
            });
            json.add("properties", obj);
        }

        /**
         * Deserialize a value by reading it from the JsonObject.
         */
        public NullTrueBlockStateCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            if (!json.has("properties"))
                throw new JsonParseException("No 'properties' field within brewinandchewin:null_true_block_state predicate.");

            JsonObject jsonobject = GsonHelper.getAsJsonObject(json, "properties");
            List<PropertyMatcher> list = Lists.newArrayList();

            for(Map.Entry<String, JsonElement> entry : jsonobject.entrySet())
                list.add(fromJson(entry.getKey(), entry.getValue()));

            return new NullTrueBlockStateCondition(list);
        }
    }

    private static PropertyMatcher fromJson(String name, JsonElement json) {
        if (json.isJsonPrimitive()) {
            String s2 = json.getAsString();
            return new PropertyMatcher(name, s2);
        } else {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "value");
            String min = GsonHelper.getAsString(jsonObject, "min", null);
            String max = GsonHelper.getAsString(jsonObject, "max", null);
            return min != null && min.equals(max) ? new PropertyMatcher(name, min) : new RangedPropertyMatcher(name, min, max);
        }
    }

    public static class PropertyMatcher {
        private final String name;
        private final String value;

        public PropertyMatcher(String pName, String value) {
            this.name = pName;
            this.value = value;
        }

        public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> pProperties, S pPropertyToMatch) {
            Property<?> property = pProperties.getProperty(this.name);
            return property == null || match(pPropertyToMatch, property);
        }

        protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> pProperties, Property<T> pProperty) {
            T t = pProperties.getValue(pProperty);
            Optional<T> optional = pProperty.getValue(this.value);
            return optional.isPresent() && t.compareTo(optional.get()) == 0;
        }

        public JsonElement toJson() {
            return new JsonPrimitive(value);
        }

        public String getName() {
            return this.name;
        }
    }

    public static class RangedPropertyMatcher extends PropertyMatcher {
        @Nullable
        private final String minValue;
        @Nullable
        private final String maxValue;

        public RangedPropertyMatcher(String pName, @Nullable String pMinValue, @Nullable String pMaxValue) {
            super(pName, "");
            this.minValue = pMinValue;
            this.maxValue = pMaxValue;
        }

        protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> pProperties, Property<T> pPropertyTarget) {
            T t = pProperties.getValue(pPropertyTarget);
            if (this.minValue != null) {
                Optional<T> optional = pPropertyTarget.getValue(this.minValue);
                if (optional.isEmpty() || t.compareTo(optional.get()) < 0) {
                    return false;
                }
            }

            if (this.maxValue != null) {
                Optional<T> optional1 = pPropertyTarget.getValue(this.maxValue);
                return optional1.isPresent() && t.compareTo(optional1.get()) <= 0;
            }

            return true;
        }

        public JsonElement toJson() {
            JsonObject jsonobject = new JsonObject();
            if (this.minValue != null) {
                jsonobject.addProperty("min", this.minValue);
            }

            if (this.maxValue != null) {
                jsonobject.addProperty("max", this.maxValue);
            }

            return jsonobject;
        }
    }
}