package umpaz.brewinandchewin.neoforge.utility;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.neoforged.neoforge.fluids.FluidStack;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.FluidUnit;

public class BnCNeoForgeCodecs {
    public static final Codec<AbstractedFluidStack> FLUID_STACK_WRAPPER = RecordCodecBuilder.create(inst -> inst.group(
                    FluidStack.FLUID_NON_EMPTY_CODEC.fieldOf("id").forGetter(stack -> stack.fluid().builtInRegistryHolder()),
                    Codec.LONG.validate(l -> {
                        if (l < 1)
                            return DataResult.error(() -> "Fluid amount must be positive");
                        return DataResult.success(l);
                    }).fieldOf("amount").forGetter(AbstractedFluidStack::amount),
                    FluidUnit.CODEC.optionalFieldOf("unit", FluidUnit.MILLIBUCKETS).forGetter(AbstractedFluidStack::unit),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(fluidStack -> fluidStack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY))
            .apply(inst, (t1, t2, t3, t4) ->
                    new AbstractedFluidStack(t1.value(), t2, PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, t4), t3, new FluidStack(t1, t2.intValue(), t4))));

    public static final Codec<AbstractedFluidIngredient> FLUID_INGREDIENT_WRAPPER = Codec.either(KegCompatibleFluidIngredients.Exact.CODEC, KegCompatibleFluidIngredients.NeoForgeIngredient.CODEC)
            .xmap(Either::unwrap, wrapper -> {
                if (wrapper instanceof KegCompatibleFluidIngredients.Exact exact)
                    return Either.left(exact);
                if (wrapper instanceof KegCompatibleFluidIngredients.NeoForgeIngredient neoForgeIngredient)
                    return Either.right(neoForgeIngredient);
                throw new UnsupportedOperationException("Unsupported wrapped fluid ingredient class.");
            });
}
