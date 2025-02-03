package umpaz.brewinandchewin.neoforge.utility;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

import java.util.Arrays;
import java.util.List;

public class KegCompatibleFluidIngredients {
    public static class Exact implements AbstractedFluidIngredient {
        public static final StreamCodec<RegistryFriendlyByteBuf, Exact> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.FLUID), exact -> exact.displayStack.fluid(),
                DataComponentPatch.STREAM_CODEC, exact -> exact.displayStack.components() instanceof PatchedDataComponentMap map ? map.asPatch() : DataComponentPatch.EMPTY,
                Exact::new
        );

        private final AbstractedFluidStack displayStack;

        public Exact(Fluid fluid, PatchedDataComponentMap components) {
            displayStack = new AbstractedFluidStack(fluid, 1000, components, new FluidStack(fluid.builtInRegistryHolder(), 1000, components.asPatch()));
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

    public static class NeoForgeIngredient implements AbstractedFluidIngredient {
        public static final StreamCodec<RegistryFriendlyByteBuf, NeoForgeIngredient> STREAM_CODEC = FluidIngredient.STREAM_CODEC.map(NeoForgeIngredient::new, neoForgeIngredient -> neoForgeIngredient.ingredient);

        private final FluidIngredient ingredient;
        private final List<AbstractedFluidStack> displayStacks;

        public NeoForgeIngredient(FluidIngredient ingredient) {
            this.ingredient = ingredient;
            displayStacks = Arrays.stream(ingredient.getStacks()).map(fluidStack -> new AbstractedFluidStack(fluidStack.getFluid(), 1000, fluidStack.getComponents(), fluidStack)).toList();
        }

        @Override
        public List<AbstractedFluidStack> displayStacks() {
            return displayStacks;
        }

        @Override
        public boolean matches(AbstractedFluidStack wrapper) {
            if (!wrapper.isEmpty() && wrapper.loaderSpecific() instanceof FluidStack fluidStack)
                return ingredient.test(fluidStack);
            return ingredient.test(FluidStack.EMPTY);
        }
    }
}