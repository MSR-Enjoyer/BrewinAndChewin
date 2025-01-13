package umpaz.brewinandchewin.common.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import umpaz.brewinandchewin.common.block.CheeseWheelBlock;
import umpaz.brewinandchewin.common.block.PizzaBlock;
import vectorwing.farmersdelight.common.tag.ForgeTags;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class BnCSlicingModifier extends LootModifier
{
    public static final Supplier<Codec<BnCSlicingModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst)
                    .and(ForgeRegistries.ITEMS.getCodec().fieldOf("slice").forGetter((m) -> m.slice))
                    .apply(inst, BnCSlicingModifier::new)));

    private final Item slice;

    protected BnCSlicingModifier(LootItemCondition[] conditionsIn, Item sliceIn) {
        super(conditionsIn);
        this.slice = sliceIn;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state != null) {
            Block targetBlock = state.getBlock();
            if (targetBlock instanceof PizzaBlock) {
                int servings = state.getValue(PizzaBlock.SERVINGS);
                generatedLoot.add(new ItemStack(slice, servings + 1));
            }
            else if (targetBlock instanceof CheeseWheelBlock) {
                    int servings = state.getValue(CheeseWheelBlock.SERVINGS);
                if (servings == 3 && !context.getParam(LootContextParams.TOOL).is(ForgeTags.TOOLS_KNIVES)) {
                    generatedLoot.add(new ItemStack(targetBlock.asItem()));
                }
                else {
                    generatedLoot.add(new ItemStack(slice, servings + 1));
                }
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
