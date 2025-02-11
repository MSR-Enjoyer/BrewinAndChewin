package umpaz.brewinandchewin.fabric.utility;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.BnCStreamCodecs;
import umpaz.brewinandchewin.common.utility.FluidUnit;

import java.util.List;

public class KegFluidIngredient {
    public static class Exact implements AbstractedFluidIngredient {
        public static final Codec<Exact> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(exact -> exact.displayStack.fluid()),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(exact -> exact.displayStack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY)
        ).apply(inst, Exact::new));
        public static final Codec<Exact> ALTERNATIVE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("id").forGetter(exact -> exact.displayStack.fluid()),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(exact -> exact.displayStack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY)
        ).apply(inst, Exact::new));
        public static final Codec<Exact> CODEC = Codec.withAlternative(DIRECT_CODEC, ALTERNATIVE_CODEC);
        public static final StreamCodec<RegistryFriendlyByteBuf, Exact> STREAM_CODEC = BnCFabricStreamCodecs.FLUID_STACK_WRAPPER.map(Exact::new, exact -> exact.displayStack);

        private final AbstractedFluidStack displayStack;

        private Exact(AbstractedFluidStack displayStack) {
            this.displayStack = displayStack;
        }

        public Exact(Fluid fluid, PatchedDataComponentMap components) {
            displayStack = new AbstractedFluidStack(fluid, 81000L, components, FluidUnit.DROPLETS, new AmountedFluidVariant(FluidVariant.of(fluid, components.asPatch()), 81000L, FluidUnit.DROPLETS));
        }

        public Exact(Fluid fluid, DataComponentPatch patch) {
            this(fluid, PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, patch));
        }

        public Exact(Fluid fluid) {
            this(fluid, new PatchedDataComponentMap(DataComponentMap.EMPTY));
        }

        @Override
        public List<AbstractedFluidStack> displayStacks() {
            return List.of(displayStack);
        }

        @Override
        public boolean matches(AbstractedFluidStack wrapper) {
            if (displayStack.components().isEmpty())
                return displayStack.fluid().isSame(wrapper.fluid());
            return displayStack.matches(wrapper);
        }
    }
}