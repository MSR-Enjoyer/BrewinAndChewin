package umpaz.brewinandchewin.common.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BnCCopyMealFunction extends LootItemConditionalFunction {
    public static final MapCodec<BnCCopyMealFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).apply(inst, BnCCopyMealFunction::new));

    public static final ResourceLocation ID = BrewinAndChewin.asResource("copy_meal");
    public static final LootItemFunctionType<BnCCopyMealFunction> TYPE = new LootItemFunctionType<>(CODEC);

    private BnCCopyMealFunction(List<LootItemCondition> conditions) {
        super(conditions);
    }

    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder(BnCCopyMealFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity tile = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (tile instanceof KegBlockEntity kegTile) {
            CustomData data = kegTile.writeMeal(stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag(), context.getLevel().registryAccess());
            stack.set(DataComponents.BLOCK_ENTITY_DATA, data);
        }
        return stack;
    }

    @Override
    public LootItemFunctionType<BnCCopyMealFunction> getType() {
        return TYPE;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        Builder() {}

        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new BnCCopyMealFunction(getConditions());
        }
    }
}