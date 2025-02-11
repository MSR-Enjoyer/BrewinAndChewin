
package umpaz.brewinandchewin.neoforge.utility;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.FluidUnit;

public class BnCNeoForgeStreamCodecs {
    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidStack> FLUID_STACK_WRAPPER = StreamCodec.composite(
            FluidStack.STREAM_CODEC, fluidStack ->  {
                if (fluidStack.loaderSpecific() instanceof FluidStack neoForge)
                    return neoForge;
                return new FluidStack(fluidStack.fluid().builtInRegistryHolder(), (int) fluidStack.unit().convertToLoader(fluidStack.amount()), DataComponentPatch.EMPTY);
            },
            FluidUnit.STREAM_CODEC, AbstractedFluidStack::unit,
            (fluidStack, unit) -> new AbstractedFluidStack(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getComponents(), unit, fluidStack)
    );
}
