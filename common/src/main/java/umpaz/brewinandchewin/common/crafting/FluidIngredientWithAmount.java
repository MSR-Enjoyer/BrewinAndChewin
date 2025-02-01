package umpaz.brewinandchewin.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;

public record FluidIngredientWithAmount(AbstractedFluidIngredient ingredient, int amount) {
    public static final Codec<FluidIngredientWithAmount> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            AbstractedFluidIngredient.CODEC.fieldOf("ingredient").forGetter(FluidIngredientWithAmount::ingredient),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("amount").forGetter(FluidIngredientWithAmount::amount)
    ).apply(inst, FluidIngredientWithAmount::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredientWithAmount> STREAM_CODEC = StreamCodec.composite(
            AbstractedFluidIngredient.STREAM_CODEC, FluidIngredientWithAmount::ingredient,
            ByteBufCodecs.INT, FluidIngredientWithAmount::amount,
            FluidIngredientWithAmount::new
    );
}
