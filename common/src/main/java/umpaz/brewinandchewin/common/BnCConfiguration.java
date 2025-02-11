package umpaz.brewinandchewin.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import house.greenhouse.greenhouseconfig.api.GreenhouseConfigHolder;
import house.greenhouse.greenhouseconfig.api.codec.GreenhouseConfigCodecs;
import house.greenhouse.greenhouseconfig.toml.TomlLang;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.utility.FluidUnit;
import umpaz.brewinandchewin.platform.BnCPlatform;

public class BnCConfiguration {

    public static final GreenhouseConfigHolder<Common> COMMON_CONFIG = GreenhouseConfigHolder.<BnCConfiguration.Common>builder("brewinandchewin-common", TomlLang.INSTANCE)
            .common(BnCConfiguration.Common.CODEC, BnCConfiguration.Common.DEFAULT)
            .networkSerializable(BnCConfiguration.Common.STREAM_CODEC.cast())
            .buildAndRegister();

    public static final GreenhouseConfigHolder<BnCConfiguration.Client> CLIENT_CONFIG = GreenhouseConfigHolder.<BnCConfiguration.Client>builder("brewinandchewin-client", TomlLang.INSTANCE)
            .client(BnCConfiguration.Client.CODEC, BnCConfiguration.Client.DEFAULT)
            .buildAndRegister();

    public static void init() {}

    public record Common(Root root, Keg keg, RecipeBook recipeBook) {
        public static final Common DEFAULT = new Common(Root.DEFAULT, Keg.DEFAULT, RecipeBook.DEFAULT);
        private static final Codec<Common> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Root.CODEC.forGetter(Common::root),
                Keg.CODEC.fieldOf("keg").forGetter(Common::keg),
                RecipeBook.CODEC.fieldOf("recipe_book").forGetter(Common::recipeBook)
        ).apply(inst, Common::new));
        public static final StreamCodec<ByteBuf, Common> STREAM_CODEC = StreamCodec.composite(
                Root.STREAM_CODEC, Common::root,
                Keg.STREAM_CODEC, Common::keg,
                RecipeBook.STREAM_CODEC, Common::recipeBook,
                Common::new
        );

        public record Root(int levelChatScramble, int levelSignScramble, int levelNameScramble) {
            public static final Root DEFAULT = new Root(3, 3, 3);

            public static final MapCodec<Root> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    Codec.intRange(1, 10),
                                    "At what amplifier of Tipsy should the chat scramble?",
                                    "Default: " + DEFAULT.levelChatScramble()
                            ),
                            "levelChatScramble",
                            DEFAULT.levelChatScramble()
                    ).forGetter(Root::levelChatScramble),
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    Codec.intRange(1, 10),
                                    "At what amplifier of Tipsy should signs scramble?",
                                    "Default: " + DEFAULT.levelSignScramble()
                            ),
                            "levelSignScramble",
                            DEFAULT.levelSignScramble()
                    ).forGetter(Root::levelSignScramble),
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    Codec.intRange(1, 10),
                                    "At what amplifier of Tipsy should nametags scramble?",
                                    "Default: " + DEFAULT.levelNameScramble()
                            ),
                            "levelNameScramble",
                            DEFAULT.levelNameScramble()
                    ).forGetter(Root::levelNameScramble)
            ).apply(inst, Root::new));

            public static final StreamCodec<ByteBuf, Root> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.INT, Root::levelChatScramble,
                    ByteBufCodecs.INT, Root::levelSignScramble,
                    ByteBufCodecs.INT, Root::levelNameScramble,
                    Root::new
            );
        }

        public record Keg(FluidUnit capacityUnit, long capacity,
                          int cold, int chilly, int warm, int hot,
                          boolean biomeTemp, boolean dimTemp) {
            public static final Keg DEFAULT = new Keg(
                    platformSpecificValue(FluidUnit.MILLIBUCKETS, FluidUnit.DROPLETS),
                    platformSpecificValue(1000L, 81000L),
                    2, 1, 1, 2,
                    true, true
            );

            /**
             * The fluid capacity appropriated for the mod loader.
             */
            public long appropriatedCapacity() {
                return FluidUnit.convert(capacity, capacityUnit, FluidUnit.getLoaderUnit());
            }

            public static final Codec<Keg> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    FluidUnit.CODEC,
                                    "Which unit the capacity field should use.",
                                    "Should be either 'millibuckets' or 'droplets'",
                                    "1mB = 81 droplets",
                                    "Default: " + DEFAULT.capacityUnit().getSerializedName()
                            ),
                            "kegCapacityUnit",
                            DEFAULT.capacityUnit()
                    ).forGetter(Keg::capacityUnit),
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    Codec.LONG.validate(l -> {
                                        if (l < 0)
                                            return DataResult.error(() -> "Keg capacity cannot be below 0.");
                                        return DataResult.success(l);
                                    }),
                                    "How much fluid (unit specified by capacityUnit) can the Keg hold?",
                                    "Range: 1 ~ "  + FluidUnit.convert(10000L, FluidUnit.MILLIBUCKETS, DEFAULT.capacityUnit()),
                                    "Default: " + DEFAULT.capacity() + "(" + DEFAULT.capacityUnit().getSerializedName() + ")"
                            ),
                            "kegCapacity",
                            DEFAULT.capacity()
                    ).forGetter(Keg::capacity),
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    ExtraCodecs.POSITIVE_INT,
                                    "How many cold blocks are required for a cold temperature in the Keg?",
                                    "Default: " + DEFAULT.cold()
                            ),
                            "kegCold",
                            DEFAULT.cold()
                    ).forGetter(Keg::cold),
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    ExtraCodecs.POSITIVE_INT,
                                    "How many cold blocks are required for a chilly temperature in the Keg?",
                                    "Default: " + DEFAULT.chilly()
                            ),
                            "kegChilly",
                            DEFAULT.chilly()
                    ).forGetter(Keg::chilly),
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    ExtraCodecs.POSITIVE_INT,
                                    "How many hot blocks are required for a warm temperature in the Keg?",
                                    "Default: " + DEFAULT.warm()
                            ),
                            "kegWarm",
                            DEFAULT.warm()
                    ).forGetter(Keg::warm),
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    ExtraCodecs.POSITIVE_INT,
                                    "How many hot blocks are required for a hot temperature in the Keg?",
                                    "Default: " + DEFAULT.hot()
                            ),
                            "kegHot",
                            DEFAULT.hot()
                    ).forGetter(Keg::hot),
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    Codec.BOOL,
                                    "Should the biome temperature influence the temperature in the Keg?",
                                    "Default: " + DEFAULT.biomeTemp()
                            ),
                            "kegBiomeTemp",
                            DEFAULT.biomeTemp()
                    ).forGetter(Keg::biomeTemp),
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    Codec.BOOL,
                                    "Should the dimension temperature influence the temperature in the Keg?",
                                    "Default: " + DEFAULT.dimTemp()
                            ),
                            "kegDimTemp",
                            DEFAULT.dimTemp()
                    ).forGetter(Keg::dimTemp)
            ).apply(inst, Keg::new));
            public static final StreamCodec<ByteBuf, Keg> STREAM_CODEC = StreamCodec.of(Keg::encode, Keg::new);

            public Keg(ByteBuf buf) {
                this(
                        FluidUnit.STREAM_CODEC.decode(buf), buf.readLong(),
                        buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),
                        buf.readBoolean(), buf.readBoolean()
                );
            }

            public static void encode(ByteBuf buf, Keg keg) {
                FluidUnit.STREAM_CODEC.encode(buf, keg.capacityUnit());
                buf.writeLong(keg.capacity);
                buf.writeInt(keg.cold);
                buf.writeInt(keg.chilly);
                buf.writeInt(keg.warm);
                buf.writeInt(keg.hot);
                buf.writeBoolean(keg.biomeTemp);
                buf.writeBoolean(keg.dimTemp);
            }
        }

        public record RecipeBook(boolean enabled) {
            public static final RecipeBook DEFAULT = new RecipeBook(true);
            public static final Codec<RecipeBook> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    GreenhouseConfigCodecs.defaultFieldCodec(
                            GreenhouseConfigCodecs.commentedCodec(
                                    Codec.BOOL,
                                    "Should the Keg have a Recipe Book available on its interface?",
                                    "Default: " + DEFAULT.enabled()
                            ),
                            "enableRecipeBookKeg",
                            DEFAULT.enabled()
                    ).forGetter(RecipeBook::enabled)
            ).apply(inst, RecipeBook::new));
            public static final StreamCodec<ByteBuf, RecipeBook> STREAM_CODEC = ByteBufCodecs.BOOL.map(RecipeBook::new, RecipeBook::enabled);
        }
    }


    public record Client(boolean numbedHeartFlickering, boolean intoxicationFoodOverlay,
                         boolean scrambleChat, boolean scrambleName, boolean scrambleSign,
                         boolean renderFluidInKeg) {
        public static final Client DEFAULT = new Client(
                true, true,
                true, true, true,
                true
        );
        private static final Codec<Client> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                GreenhouseConfigCodecs.defaultFieldCodec(
                        GreenhouseConfigCodecs.commentedCodec(
                                Codec.BOOL,
                                "Should the numbed hearts obtained from being damaged when Tipsy flicker when you are about to take damage?",
                                "Default: " + DEFAULT.numbedHeartFlickering()
                        ),
                        "numbedHeartFlickering",
                        DEFAULT.numbedHeartFlickering()
                ).forGetter(Client::numbedHeartFlickering),
                GreenhouseConfigCodecs.defaultFieldCodec(
                        GreenhouseConfigCodecs.commentedCodec(
                                Codec.BOOL,
                                "Should the food bar have a yellow overlay when the player has the Intoxication effect?",
                                "Default: " + DEFAULT.intoxicationFoodOverlay()
                        ),
                        "intoxicationFoodOverlay",
                        DEFAULT.intoxicationFoodOverlay()
                ).forGetter(Client::intoxicationFoodOverlay),
                GreenhouseConfigCodecs.defaultFieldCodec(
                        GreenhouseConfigCodecs.commentedCodec(
                                Codec.BOOL,
                                "Should the chat scramble when the player has the Tipsy effect?",
                                "Default: " + DEFAULT.scrambleChat()
                        ),
                        "scrambleChat",
                        DEFAULT.scrambleChat()
                ).forGetter(Client::scrambleChat),
                GreenhouseConfigCodecs.defaultFieldCodec(
                        GreenhouseConfigCodecs.commentedCodec(
                                Codec.BOOL,
                                "Should other player's nametags scramble when the player has the Tipsy effect?",
                                "Default: " + DEFAULT.scrambleName()
                        ),
                        "scrambleName",
                        DEFAULT.scrambleName()
                ).forGetter(Client::scrambleName),
                GreenhouseConfigCodecs.defaultFieldCodec(
                        GreenhouseConfigCodecs.commentedCodec(
                                Codec.BOOL,
                                "Should signs scramble when the player has the Tipsy effect?",
                                "Default: " + DEFAULT.scrambleSign()
                        ),
                        "scrambleSign",
                        DEFAULT.scrambleSign()
                ).forGetter(Client::scrambleSign),
                GreenhouseConfigCodecs.defaultFieldCodec(
                        GreenhouseConfigCodecs.commentedCodec(
                                Codec.BOOL,
                                "Should kegs render the fluid texture in the background of the fluid slot?",
                                "Default: " + DEFAULT.renderFluidInKeg()
                        ),
                        "renderFluidInKeg",
                        DEFAULT.renderFluidInKeg()
                ).forGetter(Client::scrambleSign)
        ).apply(inst, Client::new));
    }

    private static <T> T platformSpecificValue(T neoForge, T fabric) {
        return BrewinAndChewin.getHelper().getPlatform() == BnCPlatform.NEOFORGE ? neoForge : fabric;
    }
}
