package umpaz.brewinandchewin.neoforge.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import umpaz.brewinandchewin.client.BnCClientSetup;
import umpaz.brewinandchewin.client.BrewinAndChewinClient;
import umpaz.brewinandchewin.data.model.CoasterWrappedModel;
import umpaz.brewinandchewin.client.recipebook.BnCRecipeBook;
import umpaz.brewinandchewin.client.recipebook.FermentingBookCategory;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.renderer.CoasterBlockEntityRenderer;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.fluid.BnCFluidConstants;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.neoforge.BrewinAndChewinNeoForge;
import umpaz.brewinandchewin.neoforge.platform.client.BnCClientPlatfomHelperNeoForge;
import umpaz.brewinandchewin.neoforge.registry.BnCFluidTypes;

import java.util.ArrayList;
import java.util.List;

@Mod(value = BrewinAndChewin.MODID, dist = Dist.CLIENT)
public class BrewinAndChewinNeoForgeClient {
    public BrewinAndChewinNeoForgeClient(IEventBus eventBus) {
        BrewinAndChewinClient.setHelper(new BnCClientPlatfomHelperNeoForge());
    }

    @EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(createHoneyExtension(BnCFluidConstants.Colors.DEFAULT), BnCFluidTypes.HONEY);

            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.BEER), BnCFluidTypes.BEER);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.VODKA), BnCFluidTypes.VODKA);
            event.registerFluidType(createHoneyExtension(BnCFluidConstants.Colors.MEAD), BnCFluidTypes.MEAD);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.EGG_GROG), BnCFluidTypes.EGG_GROG);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.STRONGROOT_ALE), BnCFluidTypes.STRONGROOT_ALE);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.RICE_WINE), BnCFluidTypes.RICE_WINE);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.GLITTERING_GRENADINE), BnCFluidTypes.GLITTERING_GRENADINE);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.STEEL_TOE_STOUT), BnCFluidTypes.STEEL_TOE_STOUT);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.DREAD_NOG), BnCFluidTypes.DREAD_NOG);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.KOMBUCHA), BnCFluidTypes.KOMBUCHA);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.SACCHARINE_RUM), BnCFluidTypes.SACCHARINE_RUM);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.PALE_JANE), BnCFluidTypes.PALE_JANE);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.SALTY_FOLLY), BnCFluidTypes.SALTY_FOLLY);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.BLOODY_MARY), BnCFluidTypes.BLOODY_MARY);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.RED_RUM), BnCFluidTypes.RED_RUM);
            event.registerFluidType(createAlcoholExtension(BnCFluidConstants.Colors.WITHERING_DROSS), BnCFluidTypes.WITHERING_DROSS);

            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return BnCFluidConstants.Textures.FLAXEN_STILL_TEXTURE;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return BnCFluidConstants.Textures.FLAXEN_FLOWING_TEXTURE;
                }
            }, BnCFluidTypes.FLAXEN_CHEESE);
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return BnCFluidConstants.Textures.SCARLET_STILL_TEXTURE;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return BnCFluidConstants.Textures.SCARLET_FLOWING_TEXTURE;
                }
            }, BnCFluidTypes.SCARLET_CHEESE);
        }

        private static IClientFluidTypeExtensions createHoneyExtension(int color) {
            return new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return BnCFluidConstants.Textures.HONEY_FLUID_STILL_TEXTURE;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return BnCFluidConstants.Textures.HONEY_FLUID_FLOWING_TEXTURE;
                }

                @Override
                public int getTintColor() {
                    return color;
                }
            };
        }

        private static IClientFluidTypeExtensions createAlcoholExtension(int color) {
            return new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return BnCFluidConstants.Textures.FLUID_STILL_TEXTURE;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return BnCFluidConstants.Textures.FLUID_FLOWING_TEXTURE;
                }

                @Override
                public int getTintColor() {
                    return color;
                }
            };
        }

        @SubscribeEvent
        public static void registerRecipeBooks(RegisterRecipeBookCategoriesEvent event) {
            event.registerBookCategories(BnCRecipeBook.FERMENTING, ImmutableList.of(BnCRecipeBook.FERMENTING_SEARCH.get(), BnCRecipeBook.FERMENTING_DRINKS.get(), BnCRecipeBook.FERMENTING_MEALS.get()));
            event.registerAggregateCategory(BnCRecipeBook.FERMENTING_SEARCH.get(), ImmutableList.of(BnCRecipeBook.FERMENTING_DRINKS.get(), BnCRecipeBook.FERMENTING_MEALS.get()));
            event.registerRecipeCategoryFinder(BnCRecipeTypes.FERMENTING, recipe ->
            {
                if (recipe.value() instanceof KegFermentingRecipe fermentingRecipe) {
                    FermentingBookCategory tab = fermentingRecipe.getRecipeBookCategory();
                    if (tab != null) {
                        return switch (tab) {
                            case MEALS -> BnCRecipeBook.FERMENTING_MEALS.get();
                            case DRINKS -> BnCRecipeBook.FERMENTING_DRINKS.get();
                        };
                    }
                }
                return null;
            });
            event.registerRecipeCategoryFinder(BnCRecipeTypes.KEG_POURING, recipe -> RecipeBookCategories.UNKNOWN);
        }

        private static final List<ResourceLocation> MODELS = new ArrayList<>();

        @SubscribeEvent
        public static void registerModels(ModelEvent.RegisterAdditional event) {
            CoasterBlockEntityRenderer.resetCache();
            MODELS.addAll(BnCClientSetup.getModels(Minecraft.getInstance().getResourceManager(), Runnable::run));
            event.register(ModelResourceLocation.standalone(BrewinAndChewin.asResource("block/coaster")));
            event.register(ModelResourceLocation.standalone(BrewinAndChewin.asResource("block/coaster_tray")));
        }

        @SubscribeEvent
        public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
            for (ResourceLocation entry : MODELS) {
                event.getModels().put(ModelResourceLocation.standalone(entry.withPath(path -> "brewinandchewin/coaster/" + path)), new CoasterWrappedModel(event.getModels().get(entry)));
            }
            MODELS.clear();
        }
    }
}
