package umpaz.brewinandchewin.common.mixin;

import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.entity.player.StackedContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlaceRecipe.class)
public interface ServerPlaceRecipeAccessor {
    @Accessor("stackedContents") @Mutable @Final
    void brewinandchewin$setStackedContents(StackedContents value);

    @Invoker("testClearGrid")
    boolean brewinandchewin$invokeTestClearGrid();
}
