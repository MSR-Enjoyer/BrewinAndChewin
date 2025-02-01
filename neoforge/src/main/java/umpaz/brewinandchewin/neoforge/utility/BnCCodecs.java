package umpaz.brewinandchewin.neoforge.utility;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.fluids.FluidStack;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

public class BnCCodecs {
    public static final Codec<AbstractedFluidStack> FLUID_STACK_WRAPPER = FluidStack.CODEC.xmap(
            fluidStack -> new AbstractedFluidStack(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getComponents(), fluidStack),
            wrapper -> {
                if (wrapper.loaderSpecific() instanceof FluidStack fluidStack)
                    return fluidStack;
                return wrapper.isEmpty() ? FluidStack.EMPTY : new FluidStack(wrapper.fluid().builtInRegistryHolder(), wrapper.amount(), wrapper.components().asPatch());
            });

    public static final Codec<AbstractedFluidIngredient> FLUID_INGREDIENT_WRAPPER = Codec.either(KegCompatibleFluidIngredients.Exact.CODEC, KegCompatibleFluidIngredients.NeoForgeIngredient.CODEC)
            .xmap(Either::unwrap, wrapper -> {
                if (wrapper instanceof KegCompatibleFluidIngredients.Exact exact)
                    return Either.left(exact);
                if (wrapper instanceof KegCompatibleFluidIngredients.NeoForgeIngredient neoForgeIngredient)
                    return Either.right(neoForgeIngredient);
                throw new UnsupportedOperationException("Unsupported wrapped fluid ingredient class.");
            });
}
