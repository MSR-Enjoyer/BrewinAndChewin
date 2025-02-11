package umpaz.brewinandchewin.neoforge.utility;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.FluidUnit;

import java.util.Arrays;
import java.util.List;

public class KegCompatibleFluidIngredients {
    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidIngredient> FLUID_INGREDIENT_WRAPPER = ByteBufCodecs.either(KegCompatibleFluidIngredients.Exact.STREAM_CODEC, KegCompatibleFluidIngredients.NeoForgeIngredient.STREAM_CODEC)
            .map(Either::unwrap, wrapper -> {
                if (wrapper instanceof KegCompatibleFluidIngredients.Exact exact)
                    return Either.left(exact);
                if (wrapper instanceof KegCompatibleFluidIngredients.NeoForgeIngredient neoForgeIngredient)
                    return Either.right(neoForgeIngredient);
                throw new UnsupportedOperationException("Unsupported wrapped fluid ingredient class.");
            });

    public static class Exact implements AbstractedFluidIngredient {
        public static final Codec<Exact> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                        FluidStack.FLUID_NON_EMPTY_CODEC.fieldOf("id").forGetter(stack -> stack.displayStack.fluid().builtInRegistryHolder()),
                        DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(fluidStack -> fluidStack.displayStack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY))
                .apply(inst, (t1, t2) -> new Exact(t1.value(), t2)));
        public static final StreamCodec<RegistryFriendlyByteBuf, Exact> STREAM_CODEC = AbstractedFluidStack.STREAM_CODEC.map(Exact::new, exact -> exact.displayStack);
        private final AbstractedFluidStack displayStack;

        private Exact(AbstractedFluidStack displayStack) {
            this.displayStack = displayStack;
        }

        public Exact(Fluid fluid, PatchedDataComponentMap components) {
            displayStack = new AbstractedFluidStack(fluid, 1000, components, FluidUnit.MILLIBUCKETS, new FluidStack(fluid.builtInRegistryHolder(), 1000, components.asPatch()));
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

    public static class NeoForgeIngredient implements AbstractedFluidIngredient {
        public static final Codec<NeoForgeIngredient> CODEC = FluidIngredient.CODEC.xmap(NeoForgeIngredient::new, neoForgeIngredient -> neoForgeIngredient.ingredient);
        public static final StreamCodec<RegistryFriendlyByteBuf, NeoForgeIngredient> STREAM_CODEC = FluidIngredient.STREAM_CODEC.map(NeoForgeIngredient::new, neoForgeIngredient -> neoForgeIngredient.ingredient);

        private final FluidIngredient ingredient;
        private final List<AbstractedFluidStack> displayStacks;

        public NeoForgeIngredient(FluidIngredient ingredient) {
            this.ingredient = ingredient;
            displayStacks = Arrays.stream(ingredient.getStacks()).map(fluidStack -> new AbstractedFluidStack(fluidStack.getFluid(), 1000, fluidStack.getComponents(), FluidUnit.MILLIBUCKETS, fluidStack)).toList();
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