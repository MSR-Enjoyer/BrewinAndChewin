package umpaz.brewinandchewin.client.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class BnCFluidItemDisplays {
    private static final Map<Fluid, FluidBasedItemStack> FLUID_TYPE_TO_ITEM_MAP = new HashMap<>();

    public static ItemStack getFluidItemDisplay(HolderLookup.Provider lookup, AbstractedFluidStack fluid) {
        if (FLUID_TYPE_TO_ITEM_MAP.containsKey(fluid.fluid()))
            return FLUID_TYPE_TO_ITEM_MAP.get(fluid.fluid()).getStack(lookup, fluid);
        if (fluid.fluid().getBucket() != Items.AIR)
            return fluid.fluid().getBucket().getDefaultInstance();
        return ItemStack.EMPTY;
    }

    public static class Loader extends SimplePreparableReloadListener<Map<Fluid, FluidBasedItemStack>> {
        public static final Loader INSTANCE = new Loader();
        private static final Gson GSON = new GsonBuilder().create();

        protected Loader() {}

        @Override
        protected Map<Fluid, FluidBasedItemStack> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            FileToIdConverter fileToIdConverter = FileToIdConverter.json("brewinandchewin/fluid_item_displays");
            FluidBasedItemStack.CACHE.clear();
            Map<Fluid, FluidBasedItemStack> map = new HashMap<>();
            for (Map.Entry<ResourceLocation, List<Resource>> entry : fileToIdConverter.listMatchingResourceStacks(resourceManager).entrySet()) {
                for (Resource resource : entry.getValue()) {
                    try (Reader reader = resource.openAsReader()) {
                        JsonObject jsonObject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                        for (var e : jsonObject.entrySet()) {
                            ResourceLocation fluidLocation = ResourceLocation.parse(e.getKey());
                            if (!BuiltInRegistries.FLUID.containsKey(fluidLocation)) {
                                if (e.getValue().isJsonObject() && e.getValue().getAsJsonObject().has("optional") && e.getValue().getAsJsonObject().get("optional").getAsBoolean())
                                    continue;
                                BrewinAndChewin.LOG.error("Could not find fluid '{}' from fluid item display JSON at location '{}' from pack '{}'.", e.getKey(), entry.getKey(), resource.sourcePackId());
                                continue;
                            }
                            Fluid fluid = BuiltInRegistries.FLUID.get(fluidLocation);
                            map.put(fluid, FluidBasedItemStack.createFromJson(e.getValue(), fluid));
                        }
                    } catch (IllegalArgumentException | IllegalStateException | IOException | JsonParseException | ResourceLocationException ex) {
                        BrewinAndChewin.LOG.error("Couldn't parse fluid item display JSON at location '{}' from pack '{}'. ", entry.getKey(), resource.sourcePackId(), ex);
                    }
                }
            }
            return map;
        }

        @Override
        protected void apply(Map<Fluid, FluidBasedItemStack> obj, ResourceManager resourceManager, ProfilerFiller profiler) {
            FLUID_TYPE_TO_ITEM_MAP.putAll(obj);
        }
    }

    public record FluidBasedItemStack(Fluid fluid, FluidItemComponentRemapper dataComponentRemapper) {
        private static final HashMap<Pair<Fluid, DataComponentMap>, ItemStack> CACHE = new HashMap<>(32);

        private static FluidBasedItemStack createFromJson(JsonElement json, Fluid fluid) {
            return new FluidBasedItemStack(fluid, FluidItemComponentRemapper.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst());
        }

        private ItemStack getStack(HolderLookup.Provider lookup, AbstractedFluidStack stack) {
            var pair = Pair.of(stack.fluid(), stack.components());
            if (CACHE.containsKey(pair))
                return CACHE.get(pair);

            ItemStack item = dataComponentRemapper.convert(lookup, stack);
            CACHE.put(pair, item);
            return item;
        }
    }
}
