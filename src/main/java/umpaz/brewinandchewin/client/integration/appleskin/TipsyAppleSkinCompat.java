package umpaz.brewinandchewin.client.integration.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.client.HUDOverlayHandler;
import squeek.appleskin.helpers.FoodHelper;
import squeek.appleskin.helpers.TextureHelper;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.mixin.client.integration.appleskin.HUDOverlayHandlerAccessor;
import umpaz.brewinandchewin.common.registry.BnCEffects;

public class TipsyAppleSkinCompat {
    public static final ResourceLocation MINECRAFT_ICONS = new ResourceLocation("textures/gui/icons.png");
    public static final ResourceLocation APPLESKIN_ICONS = new ResourceLocation("appleskin", "textures/icons.png");

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(TipsyAppleSkinCompat::preventSaturationInAppleSkin);
        MinecraftForge.EVENT_BUS.addListener(TipsyAppleSkinCompat::renderAppleSkinSaturation);
        MinecraftForge.EVENT_BUS.addListener(TipsyAppleSkinCompat::renderAppleSkinRestored);
    }

    public static void renderAppleSkinFoodWithIntoxication(RenderGuiOverlayEvent.Pre event, int foodIconsOffset) {
        if (!HUDOverlayHandlerAccessor.brewinandchewin$shouldRenderAnyOverlays())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;
        FoodData stats = player.getFoodData();

        int top = mc.getWindow().getGuiScaledHeight() - foodIconsOffset;
        int left = mc.getWindow().getGuiScaledWidth() / 2 - 91; // left of health bar
        int right = mc.getWindow().getGuiScaledWidth() / 2 + 91; // right of food bar

        HUDOverlayHandlerAccessor.brewinandchewin$generateHungerBarOffsets(top, left, right, mc.gui.getGuiTicks(), player);

        HUDOverlayEvent.Saturation saturationRenderEvent;

        saturationRenderEvent = new HUDOverlayEvent.Saturation(stats.getSaturationLevel(), right, top, event.getGuiGraphics());

        // cancel render overlay event when configuration disabled.
        if (!ModConfig.SHOW_SATURATION_OVERLAY.get())
            saturationRenderEvent.setCanceled(true);

        // notify everyone that we should render saturation hud overlay
        if (!saturationRenderEvent.isCanceled())
            MinecraftForge.EVENT_BUS.post(saturationRenderEvent);

        // the render saturation event maybe cancelled by other mods
        if (!saturationRenderEvent.isCanceled())
            HUDOverlayHandler.drawSaturationOverlay(0, mc.player.getFoodData().getSaturationLevel(), mc, event.getGuiGraphics(), right, top, 1f);

        // try to get the item stack in the player hand
        ItemStack heldItem = player.getMainHandItem();
        if (ModConfig.SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND.get() && !FoodHelper.canConsume(heldItem, player))
            heldItem = player.getOffhandItem();

        boolean shouldRenderHeldItemValues = !heldItem.isEmpty() && FoodHelper.canConsume(heldItem, player);
        if (!shouldRenderHeldItemValues)
        {
            HUDOverlayHandler.resetFlash();
            return;
        }

        FoodValues modifiedFoodValues = FoodHelper.getModifiedFoodValues(heldItem, player);
        FoodValuesEvent foodValuesEvent = new FoodValuesEvent(player, heldItem, FoodHelper.getDefaultFoodValues(heldItem, player), modifiedFoodValues);
        MinecraftForge.EVENT_BUS.post(foodValuesEvent);
        modifiedFoodValues = foodValuesEvent.modifiedFoodValues;

        if (!ModConfig.SHOW_FOOD_VALUES_OVERLAY.get())
            return;

        // notify everyone that we should render hunger hud overlay
        HUDOverlayEvent.HungerRestored renderRenderEvent = new HUDOverlayEvent.HungerRestored(stats.getFoodLevel(), heldItem, modifiedFoodValues, saturationRenderEvent.x, saturationRenderEvent.y, event.getGuiGraphics());
        MinecraftForge.EVENT_BUS.post(renderRenderEvent);
        if (renderRenderEvent.isCanceled())
            return;

        // calculate the final hunger and saturation
        int foodHunger = modifiedFoodValues.hunger;
        float foodSaturationIncrement = modifiedFoodValues.getSaturationIncrement();

        // restored hunger/saturation overlay while holding food
        HUDOverlayHandler.drawHungerOverlay(foodHunger, renderRenderEvent.currentFoodLevel, mc, event.getGuiGraphics(), renderRenderEvent.x, renderRenderEvent.y,  HUDOverlayHandlerAccessor.brewinandchewin$flashAlpha(), FoodHelper.isRotten(heldItem, player));

        // The render saturation overlay event maybe cancelled by other mods
        assert saturationRenderEvent != null;
        if (!saturationRenderEvent.isCanceled()) {
            int newFoodValue = stats.getFoodLevel() + foodHunger;
            float newSaturationValue = stats.getSaturationLevel() + foodSaturationIncrement;
            float saturationGained = newSaturationValue > newFoodValue ? newFoodValue - stats.getSaturationLevel() : foodSaturationIncrement;
            // Redraw saturation overlay for gained
            HUDOverlayHandler.drawSaturationOverlay(saturationGained, saturationRenderEvent.saturationLevel, mc, event.getGuiGraphics(), saturationRenderEvent.x, saturationRenderEvent.y, HUDOverlayHandlerAccessor.brewinandchewin$flashAlpha());
        }
    }

    public static void preventSaturationInAppleSkin(FoodValuesEvent event) {
        Player entity = event.player;
        if (entity.hasEffect(BnCEffects.INTOXICATION.get())) {
            event.modifiedFoodValues = new FoodValues(event.modifiedFoodValues.hunger, 0.0F);
        }
    }

    public static void renderAppleSkinSaturation(HUDOverlayEvent.Saturation event) {
        if (!BnCConfiguration.INTOXICATION_FOOD_OVERLAY.get() || !Minecraft.getInstance().player.hasEffect(BnCEffects.INTOXICATION.get()))
            return;

        if (event.saturationLevel < 0)
            return;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float modifiedSaturation = Math.max(0, Math.min(event.saturationLevel, 20));

        int startSaturationBar = 0;
        int endSaturationBar = (int) Math.ceil(modifiedSaturation / 2.0F);

        int iconSize = 9;

        int left = event.x;

        int ticks = Minecraft.getInstance().gui.getGuiTicks();
        for (int i = startSaturationBar; i < endSaturationBar; ++i) {
            int x = ( left - i * 8 - 9 ) + (int) ( Mth.cos(( ticks + i * 2 ) * 0.20F) * 2f );
            int y = ( event.y ) + (int) ( Mth.sin(( ticks + i * 2 ) * 0.25F) * 2f );

            int v = 0;
            int u = 0;

            float effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i;

            if (effectiveSaturationOfBar >= 1)
                u = 3 * iconSize;
            else if (effectiveSaturationOfBar > .5)
                u = 2 * iconSize;
            else if (effectiveSaturationOfBar > .25)
                u = iconSize;

            event.guiGraphics.blit(APPLESKIN_ICONS, x, y, u, v, iconSize, iconSize);
        }

        RenderSystem.setShaderTexture(0, MINECRAFT_ICONS);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        event.setCanceled(true);
    }

    public static void renderAppleSkinRestored(HUDOverlayEvent.HungerRestored event) {
        if (!BnCConfiguration.INTOXICATION_FOOD_OVERLAY.get() || !Minecraft.getInstance().player.hasEffect(BnCEffects.INTOXICATION.get()))
            return;

        if (event.foodValues.hunger < 0)
            return;


        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int modifiedFood = Math.max(0, Math.min(20, event.currentFoodLevel + event.foodValues.hunger));

        int startFoodBars = Math.max(0, event.currentFoodLevel / 2);
        int endFoodBars = (int) Math.ceil(modifiedFood / 2.0F);

        int iconStartOffset = 16;
        int iconSize = 9;

        int left = event.x;

        int ticks = Minecraft.getInstance().gui.getGuiTicks();
        for (int i = startFoodBars; i < endFoodBars; ++i) {
            int x = ( left - i * 8 - 9 ) + (int) ( Mth.cos(( ticks + i * 2 ) * 0.20F) * 2f );
            int y = ( event.y ) + (int) ( Mth.sin(( ticks + i * 2 ) * 0.25F) * 2f );

            int v = 3 * iconSize;
            int u = iconStartOffset + 4 * iconSize;
            int ub = iconStartOffset + iconSize;

            if (FoodHelper.isRotten(event.itemStack, Minecraft.getInstance().player)) {
                u += 4 * iconSize;
                ub += 12 * iconSize;
            }

            if (i * 2 + 1 == modifiedFood)
                u += iconSize;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, HUDOverlayHandlerAccessor.brewinandchewin$flashAlpha() * 0.25F);
            event.guiGraphics.blit(TextureHelper.MC_ICONS, x, y, ub, v, iconSize, iconSize);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, HUDOverlayHandlerAccessor.brewinandchewin$flashAlpha());
            event.guiGraphics.blit(TextureHelper.MC_ICONS, x, y, u, v, iconSize, iconSize);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        event.setCanceled(true);
    }
}
