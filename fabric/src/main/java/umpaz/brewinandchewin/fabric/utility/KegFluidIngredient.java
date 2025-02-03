package umpaz.brewinandchewin.fabric.utility;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import java.util.List;

public class KegFluidIngredient {
    public static class Exact implements AbstractedFluidIngredient {
        public static final Codec<Exact> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(exact -> exact.displayStack.fluid()),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(exact -> exact.displayStack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY)
        ).apply(inst, Exact::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Exact> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.FLUID), exact -> exact.displayStack.fluid(),
                DataComponentPatch.STREAM_CODEC, exact -> exact.displayStack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY,
                Exact::new
        );

        private final AbstractedFluidStack displayStack;

        public Exact(Fluid fluid, PatchedDataComponentMap components) {
            displayStack = new AbstractedFluidStack(fluid, 1000, components, FluidVariant.of(fluid.builtInRegistryHolder().value(), components.asPatch()));
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
            return displayStack.matches(wrapper);
        }
    }
}