
package umpaz.brewinandchewin.neoforge.utility;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

public class BnCStreamCodecs {
    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidStack> FLUID_STACK_WRAPPER = FluidStack.STREAM_CODEC.map(
            fluidStack -> new AbstractedFluidStack(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getComponents(), fluidStack),
            wrapper -> {
                if (wrapper.loaderSpecific() instanceof FluidStack fluidStack)
                    return fluidStack;
                return wrapper.isEmpty() ? FluidStack.EMPTY : new FluidStack(wrapper.fluid().builtInRegistryHolder(), wrapper.amount(), wrapper.components().asPatch());
            });

    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidIngredient> FLUID_INGREDIENT_WRAPPER = ByteBufCodecs.either(KegCompatibleFluidIngredients.Exact.STREAM_CODEC, KegCompatibleFluidIngredients.NeoForgeIngredient.STREAM_CODEC)
            .map(Either::unwrap, wrapper -> {
                if (wrapper instanceof KegCompatibleFluidIngredients.Exact exact)
                    return Either.left(exact);
                if (wrapper instanceof KegCompatibleFluidIngredients.NeoForgeIngredient neoForgeIngredient)
                    return Either.right(neoForgeIngredient);
                throw new UnsupportedOperationException("Unsupported wrapped fluid ingredient class.");
            });
}
