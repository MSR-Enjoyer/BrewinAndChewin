package umpaz.brewinandchewin.common.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import umpaz.brewinandchewin.client.gui.BnCHUDOverlays;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.capability.TipsyNumbedHeartsCapability;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.util.Optional;
import java.util.Random;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final protected Minecraft minecraft;

    @Unique
    private int brewinandchewin$remainingHealth = 0;
    @Unique
    private float brewinandchewin$numbedAlpha = 1.0F;
    @Unique
    private boolean brewinandchewin$increaseNumbedAlpha = false;
    @Unique
    private boolean brewinandchewin$completedAbsorption = false;

    // TODO: Create an event for this overlay.
    // Implemented via mixin because Forge doesn't let us overlay health over specific hearts.
    @WrapOperation(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V", ordinal = 3))
    private void brewinandchewin$renderTipsyHearts(Gui instance,
                                                   GuiGraphics graphics,
                                                   Gui.HeartType heartType,
                                                   int heartX,
                                                   int heartY,
                                                   int yOffset,
                                                   boolean renderHighlight,
                                                   boolean halfHeart,
                                                   Operation<Void> operation,
                                                   @Local(argsOnly = true) Player player,
                                                   @Local(argsOnly = true) float maxHealth,
                                                   @Local(argsOnly = true, ordinal = 4) int currentHealth,
                                                   @Local(argsOnly = true, ordinal = 5) int displayHealth,
                                                   @Local(argsOnly = true, ordinal = 6) int absorptionAmount,
                                                   @Local(ordinal = 11) int heartIndex,
                                                   @Local(ordinal = 16) int fullHeart) {
        if (absorptionAmount <= 0)
            brewinandchewin$completedAbsorption = false;

        Optional<TipsyNumbedHeartsCapability> cap = player.getCapability(TipsyNumbedHeartsCapability.INSTANCE).resolve();
        if (!player.hasEffect(BnCEffects.TIPSY.get()) || cap.isEmpty() || cap.get().getNumbedHealth() <= 0 || absorptionAmount > 0 && brewinandchewin$completedAbsorption) {
            brewinandchewin$remainingHealth = 0;
            brewinandchewin$numbedAlpha = 1.0F;
            brewinandchewin$increaseNumbedAlpha = true;
            operation.call(instance, graphics, heartType, heartX, heartY, yOffset, renderHighlight, halfHeart);
            return;
        }

        int ticks = minecraft.gui.getGuiTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871L);

        RenderSystem.enableBlend();

        float renderHealth = player.getHealth();

        int healthStart = Mth.ceil(renderHealth / 2) - 1;
        int healthEnd = Math.max(Mth.floor((renderHealth - cap.get().getNumbedHealth()) / 2), -1);

        if (heartIndex > healthStart) {
            brewinandchewin$remainingHealth = 0;
            brewinandchewin$numbedAlpha = 1.0F;
            brewinandchewin$increaseNumbedAlpha = true;
            operation.call(instance, graphics, heartType, heartX, heartY, yOffset, renderHighlight, halfHeart);
            return;
        }

        if (heartIndex == healthStart && absorptionAmount <= 0)
            brewinandchewin$remainingHealth = Math.min(Mth.ceil(cap.get().getNumbedHealth()) - ((float) displayHealth % 1 < cap.get().getNumbedHealth() % 1 ? 1 : 0), Mth.ceil((float) displayHealth));

        operation.call(instance, graphics, heartType, heartX, heartY, yOffset, renderHighlight, halfHeart);

        if (brewinandchewin$remainingHealth <= 0)
            return;

        if (BnCConfiguration.NUMBED_HEART_FLICKERING.get() && cap.get().getNumbedHealth() > 1 && cap.get().getTicksUntilDamage() < 80 && heartIndex == healthStart) {
            if (!Minecraft.getInstance().isPaused()) {
                float increase = Mth.lerp((float) (80 - cap.get().getTicksUntilDamage()) / 80, 0.0F, 0.06F);
                brewinandchewin$numbedAlpha = Mth.clamp(brewinandchewin$numbedAlpha + (brewinandchewin$increaseNumbedAlpha ? increase : -increase), -0.01F, 1.01F);
                if (brewinandchewin$numbedAlpha < 0.0F)
                    brewinandchewin$increaseNumbedAlpha = true;
                if (brewinandchewin$numbedAlpha > 1.0F)
                    brewinandchewin$increaseNumbedAlpha = false;
            }
        } else if (heartIndex == healthStart) {
            brewinandchewin$numbedAlpha = 1.0F;
            brewinandchewin$increaseNumbedAlpha = true;
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, brewinandchewin$numbedAlpha);

        int textureYOffset = 0;
        if (player.level().getLevelData().isHardcore())
            textureYOffset += 18;

        boolean isHalfHeart = fullHeart + 1 == currentHealth;
        if (heartIndex == healthStart && isHalfHeart) {
            graphics.blit(BnCHUDOverlays.MOD_ICONS_TEXTURE, heartX, heartY, 0, 9 + textureYOffset, 9, 9); // Left Heart
            brewinandchewin$remainingHealth -= 1;
        } else if (heartIndex == healthStart && renderHealth % 2 < 1 && brewinandchewin$remainingHealth == 1 || heartIndex == healthEnd && brewinandchewin$remainingHealth == 1) {
            graphics.blit(BnCHUDOverlays.MOD_ICONS_TEXTURE, heartX, heartY, 18, 9 + textureYOffset, 9, 9); // Right Heart
            brewinandchewin$remainingHealth -= 1;
        } else {
            graphics.blit(BnCHUDOverlays.MOD_ICONS_TEXTURE, heartX, heartY, 9, 9 + textureYOffset, 9, 9); // Full Heart
            brewinandchewin$remainingHealth -= 2;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }


    @WrapOperation(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V", ordinal = 1))
    private void brewinandchewin$renderAbsorbingTipsyHearts(Gui instance,
                                                            GuiGraphics graphics,
                                                            Gui.HeartType heartType,
                                                            int heartX,
                                                            int heartY,
                                                            int yOffset,
                                                            boolean renderHighlight,
                                                            boolean halfHeart,
                                                            Operation<Void> operation,
                                                            @Local(argsOnly = true) Player player,
                                                            @Local(argsOnly = true) float maxHealth,
                                                            @Local(argsOnly = true, ordinal = 4) int currentHealth,
                                                            @Local(argsOnly = true, ordinal = 5) int displayHealth,
                                                            @Local(argsOnly = true, ordinal = 6) int absoprtionAmount,
                                                            @Local(ordinal = 11) int heartIndex,
                                                            @Local(ordinal = 16) int fullHeart) {
        Optional<TipsyNumbedHeartsCapability> cap = player.getCapability(TipsyNumbedHeartsCapability.INSTANCE).resolve();
        if (!player.hasEffect(BnCEffects.TIPSY.get()) || cap.isEmpty() || cap.get().getNumbedHealth() <= 0) {
            brewinandchewin$remainingHealth = 0;
            brewinandchewin$numbedAlpha = 1.0F;
            brewinandchewin$increaseNumbedAlpha = true;
            operation.call(instance, graphics, heartType, heartX, heartY, yOffset, renderHighlight, halfHeart);
            return;
        }

        int ticks = minecraft.gui.getGuiTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871L);

        RenderSystem.enableBlend();

        float renderHealth = player.getMaxHealth() + player.getAbsorptionAmount();

        int healthStart = Mth.ceil(renderHealth / 2) - 1;
        int healthEnd = Math.max(Mth.floor((renderHealth - cap.get().getNumbedHealth() - 1) / 2), -1);

        if (heartIndex > healthStart) {
            brewinandchewin$remainingHealth = 0;
            brewinandchewin$numbedAlpha = 1.0F;
            brewinandchewin$increaseNumbedAlpha = true;
            operation.call(instance, graphics, heartType, heartX, heartY, yOffset, renderHighlight, halfHeart);
            return;
        }

        if (heartIndex == healthStart) {
            brewinandchewin$completedAbsorption = false;
            brewinandchewin$remainingHealth = Math.min(Mth.ceil(cap.get().getNumbedHealth()) - ((float) displayHealth % 1 < cap.get().getNumbedHealth() % 1 ? 1 : 0), Mth.ceil((float) displayHealth));
        } else if (brewinandchewin$remainingHealth <= 0) {
            brewinandchewin$completedAbsorption = true;
            operation.call(instance, graphics, heartType, heartX, heartY, yOffset, renderHighlight, halfHeart);
            return;
        }

        operation.call(instance, graphics, heartType, heartX, heartY, yOffset, renderHighlight, halfHeart);

        if (BnCConfiguration.NUMBED_HEART_FLICKERING.get() && cap.get().getNumbedHealth() > 1 && cap.get().getTicksUntilDamage() < 80 && heartIndex == healthStart) {
            if (!Minecraft.getInstance().isPaused()) {
                float increase = Mth.lerp((float) (80 - cap.get().getTicksUntilDamage()) / 80, 0.0F, 0.08F);
                brewinandchewin$numbedAlpha = Mth.clamp(brewinandchewin$numbedAlpha + (brewinandchewin$increaseNumbedAlpha ? increase : -increase), -0.01F, 1.01F);
                if (brewinandchewin$numbedAlpha < 0.0F)
                    brewinandchewin$increaseNumbedAlpha = true;
                if (brewinandchewin$numbedAlpha > 1.0F)
                    brewinandchewin$increaseNumbedAlpha = false;
            }
        } else if (heartIndex == healthStart) {
            brewinandchewin$numbedAlpha = 1.0F;
            brewinandchewin$increaseNumbedAlpha = true;
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, brewinandchewin$numbedAlpha);

        int textureYOffset = 9;
        if (player.level().getLevelData().isHardcore())
            textureYOffset += 18;

        boolean isHalfHeart = absoprtionAmount % 2 == 1;
        if (heartIndex == healthStart && isHalfHeart) {
            graphics.blit(BnCHUDOverlays.MOD_ICONS_TEXTURE, heartX, heartY, 0, 9 + textureYOffset, 9, 9); // Left Heart
            brewinandchewin$remainingHealth -= 1;
        } else if (heartIndex == healthStart && renderHealth % 2 < 1 && brewinandchewin$remainingHealth == 1 || heartIndex == healthEnd && brewinandchewin$remainingHealth == 1) {
            graphics.blit(BnCHUDOverlays.MOD_ICONS_TEXTURE, heartX, heartY, 18, 9 + textureYOffset, 9, 9); // Right Heart
            brewinandchewin$remainingHealth -= 1;
        } else {
            graphics.blit(BnCHUDOverlays.MOD_ICONS_TEXTURE, heartX, heartY, 9, 9 + textureYOffset, 9, 9); // Full Heart
            brewinandchewin$remainingHealth -= 2;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
