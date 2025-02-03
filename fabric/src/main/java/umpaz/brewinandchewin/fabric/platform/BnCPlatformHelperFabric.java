package umpaz.brewinandchewin.fabric.platform;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.entity.EntityLookup;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.common.attachment.RagingAttachment;
import umpaz.brewinandchewin.common.attachment.TipsyHeartsAttachment;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.common.block.entity.container.KegStackedContents;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;
import umpaz.brewinandchewin.common.utility.BnCMenuConstructor;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;
import umpaz.brewinandchewin.fabric.BrewinAndChewinFabric;
import umpaz.brewinandchewin.fabric.container.KegFluidTankFabric;
import umpaz.brewinandchewin.fabric.container.KegItemHandlerFabric;
import umpaz.brewinandchewin.fabric.registry.BnCCreativeTabsImpl;
import umpaz.brewinandchewin.fabric.registry.BnCFluidsImpl;
import umpaz.brewinandchewin.fabric.utility.BnCFabricCodecs;
import umpaz.brewinandchewin.fabric.utility.BnCFabricStreamCodecs;
import umpaz.brewinandchewin.fabric.utility.KegFluidIngredient;
import umpaz.brewinandchewin.platform.BnCPlatform;
import umpaz.brewinandchewin.platform.BnCPlatformHelper;

import java.util.List;
import java.util.function.Consumer;

public class BnCPlatformHelperFabric implements BnCPlatformHelper {

    @Override
    public BnCPlatform getPlatform() {
        return BnCPlatform.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void sendClientboundTracking(Entity tracked, CustomPacketPayload payload) {
        for (ServerPlayer other : PlayerLookup.tracking(tracked))
            ServerPlayNetworking.send(other, payload);

        if (tracked instanceof ServerPlayer player)
            ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendServerbound(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public Component getFluidDisplayName(AbstractedFluidStack wrapper) {
        return FluidVariantAttributes.getName((FluidVariant)wrapper.loaderSpecific());
    }

    @Override
    public void openKegMenu(Player player, KegBlockEntity blockEntity, BlockPos pos) {
        player.openMenu(blockEntity);
    }

    @Override
    public MenuType<KegMenu> createMenuType(BnCMenuConstructor<KegMenu> constructor) {
        return new ExtendedScreenHandlerType<>(KegMenu::new, BlockPos.STREAM_CODEC);
    }

    @Override
    public AbstractedItemHandler createKegInventory(int size, Consumer<Integer> onContentsChanged) {
        return new KegItemHandlerFabric(size) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                onContentsChanged.accept(slot);
            }
        };
    }

    @Override
    public AbstractedFluidTank createKegTank(int capacity, Runnable onContentsChanged) {
        return new KegFluidTankFabric(capacity) {
            @Override
            protected void onFinalCommit() {
                super.onFinalCommit();
                onContentsChanged.run();
            }
        };
    }

    @Override
    public Slot createKegSlot(AbstractedItemHandler inventory, int slot, int x, int y, boolean canInsert, @Nullable Pair<ResourceLocation, ResourceLocation> noItemIcon) {
        // TODO
    }

    @Override
    public Ingredient createStrictFillPickerIngredient(List<KegStackedContents.PouringEntry> fluidOutputStacks) {
        // TODO
    }

    @Override
    public KegRecipeWrapper createRecipeWrapper(AbstractedItemHandler itemHandler, AbstractedFluidTank fluidTank) {
        // TODO
    }

    @Override
    public SidedKegWrapper createSidedKegWrapper(AbstractedItemHandler inventory, Direction direction) {
        // TODO
    }

    @Override
    public Codec<AbstractedFluidStack> getFluidStackWrapperCodec() {
        return BnCFabricCodecs.FLUID_VARIANT_WRAPPER;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidStack> getFluidStackWrapperStreamCodec() {
        return BnCFabricStreamCodecs.FLUID_STACK_WRAPPER;
    }

    @Override
    public Codec<AbstractedFluidIngredient> getFluidIngredientWrapperCodec() {
        return KegFluidIngredient.Exact.CODEC.xmap(exact -> exact, abstractedFluidIngredient -> (KegFluidIngredient.Exact) abstractedFluidIngredient);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidIngredient> getFluidIngredientWrapperStreamCodec() {
        return KegFluidIngredient.Exact.STREAM_CODEC.map(exact -> exact, abstractedFluidIngredient -> (KegFluidIngredient.Exact) abstractedFluidIngredient);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.getRecipeRemainder();
    }

    @Override
    public void initFluids() {
        BnCFluidsImpl.init();
    }

    @Override
    public void initCreativeTab() {
        BnCCreativeTabsImpl.init();
    }

    @Override
    public boolean isEdible(ItemStack stack, LivingEntity entity) {
        return stack.has(DataComponents.FOOD);
    }

    @Override
    public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
        return stack.get(DataComponents.FOOD);
    }

    @Override
    public MinecraftServer getServer() {
        return BrewinAndChewinFabric.getServer();
    }

    @Override
    public RagingAttachment getRagingAttachment(LivingEntity entity) {
        return null;
    }

    @Override
    public void setRagingAttachment(LivingEntity entity, @Nullable RagingAttachment value) {

    }

    @Override
    public TipsyHeartsAttachment getTipsyHeartsAttachment(LivingEntity entity) {
        return null;
    }

    @Override
    public void setTipsyHeartsAttachment(LivingEntity entity, @Nullable TipsyHeartsAttachment value) {

    }
}
