package umpaz.brewinandchewin.common.utility;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.platform.BnCPlatform;

import java.util.function.Function;
import java.util.function.IntFunction;

public enum FluidUnit implements StringRepresentable {
    MILLIBUCKETS("millibuckets", l -> l + "mB", l -> l + " millibuckets",1),
    DROPLETS("droplets", l -> l + " droplets", l -> l + " droplets",81);

    private final String name;
    private final Function<Long, String> shortFormFormatFunc;
    private final Function<Long, String> longFormFormatFunc;
    private final long oneMb;

    public static final Codec<FluidUnit> CODEC = StringRepresentable.fromEnum(FluidUnit::values);
    public static final IntFunction<FluidUnit> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, FluidUnit> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    FluidUnit(String name, Function<Long, String> shortFormFormatFunc, Function<Long, String> longFormFormatFunc, long oneMb) {
        this.name = name;
        this.shortFormFormatFunc = shortFormFormatFunc;
        this.longFormFormatFunc = longFormFormatFunc;
        this.oneMb = oneMb;
    }

    public long convert(long value, FluidUnit unit) {
        return convert(value, this, unit);
    }

    public long convertToLoader(long value) {
        return converToLoader(value, this);
    }

    public static FluidUnit getLoaderUnit() {
        return BrewinAndChewin.getHelper().getPlatform() == BnCPlatform.NEOFORGE ? MILLIBUCKETS : DROPLETS;
    }

    public static long converToLoader(long value, FluidUnit unit) {
        return convert(value, unit, getLoaderUnit());
    }

    public static long convert(long value, FluidUnit originalUnit, FluidUnit newUnit) {
        if (originalUnit == newUnit)
            return value;
        return value / originalUnit.oneMb * newUnit.oneMb;
    }

    public String shortFormat(long value) {
        return shortFormFormatFunc.apply(value);
    }

    public String longFormat(long value) {
        return longFormFormatFunc.apply(value);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
