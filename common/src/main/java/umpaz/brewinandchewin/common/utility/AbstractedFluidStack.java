package umpaz.brewinandchewin.common.utility;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import umpaz.brewinandchewin.BrewinAndChewin;

import javax.annotation.Nullable;

public record AbstractedFluidStack(Fluid fluid, long amount, DataComponentMap components, @Nullable Object loaderSpecific) {
    public static final Codec<AbstractedFluidStack> CODEC = BrewinAndChewin.getHelper().getFluidStackWrapperCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidStack> STREAM_CODEC = BrewinAndChewin.getHelper().getFluidStackWrapperStreamCodec();
    public static final AbstractedFluidStack EMPTY = new AbstractedFluidStack(Fluids.EMPTY, 0, new PatchedDataComponentMap(DataComponentMap.EMPTY), null);

    public AbstractedFluidStack(Fluid fluid, long amount) {
        this(fluid, amount, new PatchedDataComponentMap(DataComponentMap.EMPTY), null);
    }

    public boolean isEmpty() {
        return this == EMPTY || fluid == Fluids.EMPTY || amount <= 0;
    }

    public boolean matches(AbstractedFluidStack other) {
        return fluid == other.fluid && components.equals(other.components);
    }

    @Override
    public Fluid fluid() {
        return isEmpty() ? Fluids.EMPTY : fluid;
    }
}
