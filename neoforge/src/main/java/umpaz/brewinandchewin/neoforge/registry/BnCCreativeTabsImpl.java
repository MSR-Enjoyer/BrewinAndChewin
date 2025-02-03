package umpaz.brewinandchewin.neoforge.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.common.registry.BnCCreativeTabs;
import umpaz.brewinandchewin.common.registry.BnCItems;

public class BnCCreativeTabsImpl {
    public static void init() {
        BnCCreativeTabs.TAB_BREWIN_AND_CHEWIN = CreativeModeTab.builder().title(Component.translatable("itemGroup.brewinandchewin"))
                .icon(() -> new ItemStack(BnCItems.KEG))
                .displayItems((parameters, output) -> BnCItems.CREATIVE_TAB_ITEMS.forEach(output::accept))
                .build();
    }
}
