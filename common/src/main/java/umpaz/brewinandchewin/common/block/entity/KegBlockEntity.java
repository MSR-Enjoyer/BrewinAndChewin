package umpaz.brewinandchewin.common.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.block.KegBlock;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.registry.BnCBlockEntityTypes;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.tag.BnCTags;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;
import umpaz.brewinandchewin.common.utility.BnCTextUtils;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class KegBlockEntity extends SyncedBlockEntity implements MenuProvider, Nameable, RecipeCraftingHolder {

    public static final int CONTAINER_SLOT = 4;
    public static final int OUTPUT_SLOT = 5;
    public static final int INVENTORY_SIZE = OUTPUT_SLOT + 1;
    public static final int RANGE = 2;

    private final AbstractedItemHandler inventory;
    // TODO: Loader Transfer API.
    private final SidedKegWrapper inputHandler;
    private final SidedKegWrapper outputHandler;
    private final AbstractedFluidTank fluidTank;
    private final KegRecipeWrapper recipeWrapper;

    private int fermentTime;
    private int fermentTimeTotal;
    private Component customName;

    public int kegTemperature;

    protected final ContainerData kegData;
    private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;

    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;

    public KegBlockEntity(BlockPos pos, BlockState state) {
        super(BnCBlockEntityTypes.KEG, pos, state);
        this.inventory = createHandler();
        this.inputHandler = BrewinAndChewin.getHelper().createSidedKegWrapper(inventory, Direction.UP);
        this.outputHandler = BrewinAndChewin.getHelper().createSidedKegWrapper(inventory, Direction.DOWN);
        this.fluidTank = createFluidTank();
        this.kegData = createIntArray();
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.checkNewRecipe = true;
        this.recipeWrapper = BrewinAndChewin.getHelper().createRecipeWrapper(inventory, fluidTank);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        inventory.readFromNbt(compound.getCompound("Inventory"), provider);
        fluidTank.readFromNbt(compound.getCompound("FluidTank"), provider);
        fermentTime = compound.getInt("FermentTime");
        fermentTimeTotal = compound.getInt("FermentTimeTotal");
        if (compound.contains("CustomName", 8)) {
            customName = Component.Serializer.fromJson(compound.getString("CustomName"), provider);
        }
        CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");
        for (String key : compoundRecipes.getAllKeys()) {
            usedRecipeTracker.put(ResourceLocation.tryParse(key), compoundRecipes.getInt(key));
        }
        checkNewRecipe = true;
    }

    public static AbstractedFluidStack getMealFromItem(ItemStack kegStack, HolderLookup.Provider provider) {
        if (!kegStack.is(BnCItems.KEG)) {
            return AbstractedFluidStack.EMPTY;
        }

        CustomData data = kegStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        if (!tag.isEmpty()) {
            if (tag.contains("FluidTank")) {
                return AbstractedFluidStack.CODEC.decode(RegistryOps.create(NbtOps.INSTANCE, provider), tag.get("FluidTank")).mapOrElse(Pair::getFirst, pairError -> AbstractedFluidStack.EMPTY);
            }
        }

        return AbstractedFluidStack.EMPTY;
    }

    public AbstractedFluidStack getOutput() {
        return fluidTank.getAbstractedFluid();
    }

    public CustomData writeMeal(CompoundTag tag, HolderLookup.Provider provider) {
        AbstractedItemHandler drops = BrewinAndChewin.getHelper().createKegInventory(INVENTORY_SIZE, integer -> {});
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.setStackInSlot(i, i == CONTAINER_SLOT ? inventory.getStackInSlot(i) : ItemStack.EMPTY);
        }
        if (customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(customName, provider));
        }
        tag.put("Inventory", drops.writeToNbt(provider));
        if (!fluidTank.isEmpty()) {
            tag.put("FluidTank", this.fluidTank.writeToNbt(provider));
        }
        return CustomData.of(tag);
    }


    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.put("Inventory", inventory.writeToNbt(provider));
        compound.put("FluidTank", fluidTank.writeToNbt(provider));
        compound.putInt("FermentTime", fermentTime);
        compound.putInt("FermentTimeTotal", fermentTimeTotal);
        if (customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName, provider));
        }
        CompoundTag compoundRecipes = new CompoundTag();
        usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        compound.put("RecipesUsed", compoundRecipes);
    }

    private CompoundTag writeUpdateTag(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.put("Inventory", inventory.writeToNbt(provider));
        compound.put("FluidTank", fluidTank.writeToNbt(provider));
        compound.putInt("FermentTime", fermentTime);
        compound.putInt("FermentTimeTotal", fermentTimeTotal);
        return compound;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        components.set(DataComponents.CUSTOM_DATA, writeMeal(new CompoundTag(), level.registryAccess()));
    }

    public CompoundTag writeDrink(CompoundTag compound, HolderLookup.Provider provider) {
        if (customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName, provider));
        }
        if (!fluidTank.isEmpty()) {
            compound.put("FluidTank", this.fluidTank.writeToNbt(provider));
        }
        return compound;
    }


    public static boolean isValidTemp(int kegTemp, int want) {
        return switch (want) {
            case 1 -> kegTemp <= 1;
            case 2 -> kegTemp <= 2;
            case 3 -> kegTemp < 5 && kegTemp > 1;
            case 4 -> kegTemp >= 4;
            case 5 -> kegTemp >= 5;
            default -> false;
        };
    }

    protected boolean canFerment(KegFermentingRecipe recipe, KegBlockEntity keg) {
        if (!hasInput()) return false;
        if (level == null) return false;
        if (!isValidTemp(keg.getTemperature(), recipe.getTemperature()))
            return false; // make sure the temperature is valid


        if (recipe.getFluidIngredient().isEmpty()) { // if the recipe does not require a fluid
            return keg.fluidTank.isEmpty(); // make sure the fluid is empty
        } else {
            if (!recipe.getFluidIngredient().get().ingredient().matches(keg.fluidTank.getAbstractedFluid()))
                return false; // make sure the fluid is the same
            return keg.fluidTank.getAbstractedFluid().amount() % recipe.getFluidIngredient().get().amount() == 0; // make sure the fluid amount is a multiple of the recipe amount
        }
    }

    public static void fermentingTick(Level level, BlockPos pos, BlockState state, KegBlockEntity keg) {
        boolean didInventoryChange = false;

        keg.updateTemperature();

        if (keg.hasInput()) {
            Optional<RecipeHolder<KegFermentingRecipe>> recipe = keg.getMatchingRecipe(keg.recipeWrapper);
            if (recipe.isPresent()) {
                if (keg.canFerment(recipe.get().value(), keg)) {
                    didInventoryChange = keg.processFermenting(recipe.get().value(), keg);
                } else {
                    keg.fermentTime = Math.max(0, keg.fermentTime - 20);
                }
            } else {
                keg.fermentTime = Math.max(0, keg.fermentTime - 20);
            }
        } else if (keg.fermentTime > 0) {
            keg.fermentTime = Math.max(0, keg.fermentTime - 20);
        }

        List<ItemStack> out = keg.extractInGui(keg, keg.inventory.getStackInSlot(CONTAINER_SLOT), keg.inventory.getSlotLimit(OUTPUT_SLOT));
        if (!out.isEmpty()) {
            keg.inventory.insertItem(OUTPUT_SLOT, out.get(0), false);
            didInventoryChange = true;
        }

        if (didInventoryChange) {
            keg.inventoryChanged();
        }
    }

    public Optional<RecipeHolder<KegFermentingRecipe>> getRecipeWithoutTemperature() {
        if (!hasInput())
            return Optional.empty();
        Optional<RecipeHolder<KegFermentingRecipe>> recipe = getMatchingRecipe(recipeWrapper);
        if (recipe.isEmpty())
            return Optional.empty();
        if (recipe.get().value().getFluidIngredient().isEmpty()) { // if the recipe does not require a fluid
            if (!fluidTank.isEmpty()) // make sure the fluid is empty
                return Optional.empty();
        } else {
            if (!recipe.get().value().getFluidIngredient().get().ingredient().matches(fluidTank.getAbstractedFluid()))
                return Optional.empty(); // make sure the fluid is the same
            if (fluidTank.getAbstractedFluid().amount() % recipe.get().value().getFluidIngredient().get().amount() != 0) // make sure the fluid amount is a multiple of the recipe amount
                return Optional.empty();
        }
        return recipe;
    }

    private Optional<RecipeHolder<KegFermentingRecipe>> getMatchingRecipe(KegRecipeWrapper inventoryWrapper) {
        if (level == null) return Optional.empty();

        if (checkNewRecipe) {
            Optional<RecipeHolder<KegFermentingRecipe>> recipe = level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.FERMENTING).stream().filter(a -> a.value().matches(inventoryWrapper, level)).findFirst();
            if (recipe.isPresent()) {
                ResourceLocation newRecipeID = recipe.get().id();
                if (lastRecipeID != null && !lastRecipeID.equals(newRecipeID)) {
                    fermentTime = 0;
                }
                lastRecipeID = newRecipeID;
                return recipe;
            }
        }
        checkNewRecipe = false;

        if (lastRecipeID != null) {
            Optional<RecipeHolder<KegFermentingRecipe>> recipe = level.getRecipeManager()
                    .getRecipeFor(BnCRecipeTypes.FERMENTING, inventoryWrapper, level, lastRecipeID);
            if (recipe.isPresent() && recipe.get().value().matches(inventoryWrapper, level)) {
                return recipe;
            }
        }

        return Optional.empty();
    }

    private boolean hasInput() {
        for (int i = 0; i < OUTPUT_SLOT; ++i) {
            if (!inventory.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    private boolean processFermenting(KegFermentingRecipe recipe, KegBlockEntity keg) {
        if (level == null) return false;

        ++fermentTime;
        fermentTimeTotal = recipe.getFermentTime();
        if (fermentTime < fermentTimeTotal) {
            setChanged();
            return false;
        }


        fermentTime = 0;
        if (recipe.getResult().left().isPresent()) {
            keg.fluidTank.setAbstractedFluid(recipe.getResult().left().get());
            if (keg.level.isClientSide())
                keg.level.playLocalSound(keg.getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1, 0.8f, true);
        }

        if (recipe.getResult().right().isPresent()) {
            if (recipe.getFluidIngredient().isPresent())
                keg.fluidTank.drain(recipe.getFluidIngredient().get().amount(), recipe.getUnit(),false);
            keg.inventory.insertItem(OUTPUT_SLOT, recipe.getResult().right().get().copy(), false);
        }


        for (int i = 0; i < OUTPUT_SLOT; ++i) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (!BrewinAndChewin.getHelper().getCraftingRemainingItem(slotStack).isEmpty()) {
                ejectIngredientRemainder(BrewinAndChewin.getHelper().getCraftingRemainingItem(slotStack));
            }
            if (!slotStack.isEmpty())
                slotStack.shrink(1);
        }
        return true;
    }

    public List<ItemStack> extractInGui(KegBlockEntity keg, ItemStack slotIn, int maxTakeAmount) {
        return fluidExtract(keg, slotIn, maxTakeAmount, true, false);
    }

    public List<ItemStack> extractInWorld(KegBlockEntity keg, ItemStack slotIn, int maxTakeAmount,boolean isCreative) {
        return fluidExtract(keg, slotIn, maxTakeAmount, false, isCreative);
    }

    private List<ItemStack> fluidExtract(KegBlockEntity keg, ItemStack slotIn, int maxTakeAmount, boolean inGui, boolean isCreative) {
        if (slotIn.isEmpty())
            return List.of();

        Optional<KegPouringRecipe> recipe = keg.getPouringRecipe(slotIn);
        boolean changed = false;

        List<ItemStack> outputs = new ArrayList<>();

        if (recipe.isPresent() && (keg.fluidTank.isEmpty() || keg.fluidTank.getAbstractedFluid().fluid() == recipe.get().getRawFluid().fluid())) { // if the recipe is present and the fluid is empty or the same
            ItemStack resultItem = recipe.get().assemble(keg.recipeWrapper, keg.level.registryAccess());
            if (ItemStack.isSameItem(slotIn, recipe.get().getContainer()) && // if container is same
                    recipe.get().getRawFluid().amount() <= keg.fluidTank.getAbstractedFluid().amount() && // the amount is LTE the fluid amount
                    (!inGui || keg.inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() || ItemStack.isSameItemSameComponents(resultItem, keg.inventory.getStackInSlot(OUTPUT_SLOT)))) { // the output slot can accept this item
                int containerAmount = (int) Mth.clamp(Math.min(slotIn.getCount(), keg.fluidTank.getAbstractedFluid().unit().convertToLoader(keg.fluidTank.getAbstractedFluid().amount()) / recipe.get().getRawFluid().amount()), 1, maxTakeAmount);
                keg.fluidTank.drain(recipe.get().getRawFluid().amount() * containerAmount, recipe.get().getUnit(),false);

                if (!isCreative) {
                    int overflow = containerAmount;
                    while (overflow > 0 && !slotIn.isEmpty()) {
                        ItemStack newResult = resultItem.copyWithCount(Math.min(resultItem.getMaxStackSize(), overflow));
                        outputs.add(newResult);
                        overflow -= newResult.getCount();
                        slotIn.shrink(newResult.getCount());
                    }
                } else {
                    outputs.add(slotIn);
                }
                changed = true;
            } else if (recipe.filter(KegPouringRecipe::canFill).isPresent() && // if the recipe can fill
                    (recipe.get().isStrict() && ItemStack.isSameItemSameComponents(resultItem, slotIn) || !recipe.get().isStrict() && ItemStack.isSameItem(slotIn, resultItem)) && // if result is same
                    (keg.fluidTank.isEmpty() || keg.fluidTank.getAbstractedFluid().amount() < keg.fluidTank.getFluidCapacity()) && // if the result can fit in the container
                    (!inGui || keg.inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() || ItemStack.isSameItemSameComponents(resultItem, keg.inventory.getStackInSlot(OUTPUT_SLOT)))) { // the output slot can accept this item
                int containerAmount = (int) Mth.clamp(Math.min(slotIn.getCount(), (keg.fluidTank.getFluidCapacity() - keg.fluidTank.getAbstractedFluid().amount()) / recipe.get().getRawFluid().amount()), 1, maxTakeAmount);
                keg.fluidTank.fill(new AbstractedFluidStack(recipe.get().getFluid(slotIn).fluid(), recipe.get().getUnit().convertToLoader(recipe.get().getRawFluid().amount()) * containerAmount, recipe.get().getRawFluid().components(), recipe.get().getUnit(), null), false);

                if (!isCreative) {
                    ItemStack recipeItem = recipe.get().getContainer();
                    int overflow = containerAmount;
                    while (overflow > 0 && !slotIn.isEmpty()) {
                        ItemStack newResult = recipeItem.copyWithCount(Math.min(recipeItem.getMaxStackSize(), overflow));
                        outputs.add(newResult);
                        overflow -= newResult.getCount();
                        slotIn.shrink(newResult.getCount());
                    }
                } else {
                    outputs.add(slotIn);
                }
                changed = true;
            }

            if (changed) {
                inventoryChanged();
            }
        }

        if (!outputs.isEmpty() || recipe.isPresent())
            return outputs;

        // TODO: Platformify transfer API extractions.
//        LazyOptional<IFluidHandlerItem> fluidHandler = isCreative ? slotIn.copy().getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM) : slotIn.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
//        IFluidHandlerItem iFluidItemHandler = fluidHandler.orElse(null);
//
//        IFluidHandlerItem finalIFluidItemHandler = iFluidItemHandler;
//
//        if (fluidHandler.isPresent() && !slotIn.isEmpty()) {
//            if (keg.fluidTank.getFluid().isFluidEqual(iFluidItemHandler.getFluidInTank(0)) || keg.fluidTank.getFluid().isEmpty() &&
//                    (!inGui || keg.inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() || keg.inventory.getStackInSlot(OUTPUT_SLOT).is(iFluidItemHandler.getContainer().getItem())) &&
//                    keg.level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().anyMatch(pouringRecipe -> pouringRecipe.getFluid(slotIn).isFluidEqual(finalIFluidItemHandler.getFluidInTank(0)))) {
//                int amountToDrain = keg.fluidTank.getCapacity() - keg.fluidTank.getFluidAmount();
//                int amount = keg.fluidTank.fill(iFluidItemHandler.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE);
//                if (amount <= amountToDrain && amount > 0) {
//                    keg.fluidTank.fill(iFluidItemHandler.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
//                    if (!isCreative) {
//                        ItemStack recipeItem = iFluidItemHandler.getContainer();
//                        int overflow = amount / keg.fluidTank.getCapacity();
//                        while (overflow > 0 && !slotIn.isEmpty()) {
//                            ItemStack newResult = recipeItem.copyWithCount(Math.min(recipeItem.getMaxStackSize(), overflow));
//                            outputs.add(newResult);
//                            overflow -= newResult.getCount();
//                            slotIn.shrink(newResult.getCount());
//                        }
//                    } else {
//                        outputs.add(slotIn);
//                    }
//                    setChanged();
//                    inventoryChanged();
//                }
//            } else if (!keg.fluidTank.getFluid().isEmpty() && iFluidItemHandler.isFluidValid(0, keg.fluidTank.getFluid())
//            && (!inGui || keg.inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() || keg.inventory.getStackInSlot(OUTPUT_SLOT).is(iFluidItemHandler.getContainer().getItem()))) {
//                int amountToDrain = iFluidItemHandler.getTankCapacity(0);
//                iFluidItemHandler = slotIn.copyWithCount(amountToDrain / iFluidItemHandler.getTankCapacity(0)).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
//                int amount = iFluidItemHandler.fill(keg.fluidTank.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE);
//                if (amount > 0) {
//                    iFluidItemHandler.fill(keg.fluidTank.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
//                    if (amount <= amountToDrain) {
//                        if (!isCreative) {
//                            ItemStack recipeItem = iFluidItemHandler.getContainer();
//                            int overflow = amount / keg.fluidTank.getCapacity();
//                            while (overflow > 0 && !slotIn.isEmpty()) {
//                                ItemStack newResult = recipeItem.copyWithCount(Math.min(recipeItem.getMaxStackSize(), overflow));
//                                outputs.add(newResult);
//                                overflow -= newResult.getCount();
//                                slotIn.shrink(newResult.getCount());
//                            }
//                        } else {
//                            outputs.add(slotIn);
//                        }
//                        setChanged();
//                        inventoryChanged();
//
//                    }
//                }
//            }
//
//        }

        return outputs;
    }

    public Optional<KegPouringRecipe> getPouringRecipe(ItemStack slot) {
        if (level == null) return Optional.empty();
        return level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.KEG_POURING)
                .stream()
                .map(RecipeHolder::value)
                .sorted(Comparator.comparingInt(value -> value.isStrict() ? 0 : 1))
                .filter(r -> {
                    boolean containerCheck = false;
                    boolean resultCheck = false;
                    boolean fluidCheck = false;
                    if (r.isStrict() && ItemStack.isSameItemSameComponents(r.getContainer(), slot) || !r.isStrict() && (r.getContainer().getItem() == slot.getItem()))
                        containerCheck = true;
                    if (!containerCheck && r.canFill() && (r.isStrict() && ItemStack.isSameItemSameComponents(r.assemble(recipeWrapper, level.registryAccess()), slot) || !r.isStrict() && r.assemble(recipeWrapper, level.registryAccess()).getItem() == slot.getItem()))
                        resultCheck = true;
                    if (recipeWrapper.getFluid().isEmpty() || (containerCheck && r.getRawFluid().fluid() == recipeWrapper.getFluid().fluid() || r.getFluid(slot).matches(recipeWrapper.getFluid())))
                        fluidCheck = true;
                    return (containerCheck || resultCheck) && fluidCheck;
                })
                .findFirst();
    }

    public void updateTemperature() {
        ArrayList<BlockState> states = new ArrayList<>();
        for (int x = -RANGE; x <= RANGE; x++) {
            for (int y = -RANGE; y <= RANGE; y++) {
                for (int z = -RANGE; z <= RANGE; z++) {
                    states.add(level.getBlockState(worldPosition.offset(x, y, z)));
                }
            }
        }

        int heat = states.stream().filter(s -> s.is(ModTags.HEAT_SOURCES) && s.hasProperty(BlockStateProperties.LIT)).filter(s -> s.getValue(BlockStateProperties.LIT)).mapToInt(s -> 1).sum();
        heat += states.stream().filter(s -> s.is(ModTags.HEAT_SOURCES) && !s.hasProperty(BlockStateProperties.LIT)).mapToInt(s -> 1).sum();

        // Compat with mods that have lit states, such as a future Pug FD addon.
        int cold = states.stream().filter(s -> s.is(BnCTags.Blocks.FREEZE_SOURCES) && s.hasProperty(BlockStateProperties.LIT)).filter(s -> s.hasProperty(BlockStateProperties.LIT)).filter(s -> s.getValue(BlockStateProperties.LIT)).mapToInt(s -> 1).sum();
        cold += states.stream().filter(s -> s.is(BnCTags.Blocks.FREEZE_SOURCES) && !s.hasProperty(BlockStateProperties.LIT)).mapToInt(s -> 1).sum();

        if (BnCConfiguration.COMMON_CONFIG.get().keg().biomeTemp()) {
            Holder<Biome> biome = level.getBiome(worldPosition);
            if (biome.isBound()) {
                float biomeTemperature = biome.value().getBaseTemperature();
                if (biomeTemperature <= 0) {
                    cold += 1;
                } else if (biomeTemperature == 2) {
                    heat += 1;
                }
            }
        }

        kegTemperature = heat - cold;

        if (BnCConfiguration.COMMON_CONFIG.get().keg().dimTemp() && level.dimensionType().ultraWarm()) {
            kegTemperature += 2;
        }
    }

    public int getTemperature() {
        if (kegTemperature <= -BnCConfiguration.COMMON_CONFIG.get().keg().cold()) {
            return 1;
        } else if (kegTemperature <= -BnCConfiguration.COMMON_CONFIG.get().keg().chilly()) {
            return 2;
        } else if (kegTemperature < BnCConfiguration.COMMON_CONFIG.get().keg().warm()) {
            return 3;
        } else if (kegTemperature < BnCConfiguration.COMMON_CONFIG.get().keg().hot()) {
            return 4;
        } else {
            return 5;
        }
    }

    protected void ejectIngredientRemainder(ItemStack remainderStack) {
        Direction direction = getBlockState().getValue(KegBlock.FACING).getCounterClockWise();
        double x = worldPosition.getX() + 0.5 + (direction.getStepX() * 0.25);
        double y = worldPosition.getY() + 0.7;
        double z = worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.25);
        ItemUtils.spawnItemEntity(level, remainderStack, x, y, z,
                direction.getStepX() * 0.08F, 0.25F, direction.getStepZ() * 0.08F);
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeID = recipe.id();
            usedRecipeTracker.addTo(recipeID, 1);
        }
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player, List<ItemStack> items) {
        List<RecipeHolder<?>> usedRecipes = getUsedRecipesAndPopExperience(player.level(), player.position());
        player.awardRecipes(usedRecipes);
        usedRecipeTracker.clear();
    }

    public List<RecipeHolder<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
        List<RecipeHolder<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : usedRecipeTracker.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience((ServerLevel) level, pos, entry.getIntValue(), ((KegFermentingRecipe)recipe.value()).getExperience());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
        int expTotal = Mth.floor((float) craftedAmount * experience);
        float expFraction = Mth.frac((float) craftedAmount * experience);
        if (expFraction > 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }

        ExperienceOrb.award(level, pos, expTotal);
    }

    public AbstractedItemHandler getInventory() {
        return inventory;
    }

    public AbstractedFluidTank getFluidTank() {
        return fluidTank;
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.add(inventory.getStackInSlot(i));
        }
        return drops;
    }

    @Override
    public Component getName() {
        return customName != null ? customName : BnCTextUtils.getTranslation("container.keg");
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return customName;
    }

    public void setCustomName(Component name) {
        customName = name;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory player, Player entity) {
        return new KegMenu(id, player, this, kegData);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return writeUpdateTag(new CompoundTag(), provider);
    }

    private AbstractedItemHandler createHandler() {
        return BrewinAndChewin.getHelper().createKegInventory(INVENTORY_SIZE, (slot) -> {
            if (slot >= 0 && slot < OUTPUT_SLOT) {
                checkNewRecipe = true;
            }
            inventoryChanged();
        });
    }

    private AbstractedFluidTank createFluidTank() {
        return BrewinAndChewin.getHelper().createKegTank(BnCConfiguration.COMMON_CONFIG.get().keg().localizedCapacity(), () -> {
            setChanged();
            inventoryChanged();
            checkNewRecipe = true;
        });
    }

    private ContainerData createIntArray() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> KegBlockEntity.this.fermentTime;
                    case 1 -> KegBlockEntity.this.fermentTimeTotal;
                    case 2 -> KegBlockEntity.this.getTemperature();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> KegBlockEntity.this.fermentTime = value;
                    case 1 -> KegBlockEntity.this.fermentTimeTotal = value;
                    case 2 -> KegBlockEntity.this.getTemperature();

                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }
}