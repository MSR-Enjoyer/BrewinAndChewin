package umpaz.brewinandchewin.neoforge.utility;

import com.simibubi.create.AllFluids;
import net.minecraft.world.level.material.Fluid;

public class BnCCreateDelegate {
    public static Fluid getPotionSource() {
        return AllFluids.POTION.getSource();
    }
}
