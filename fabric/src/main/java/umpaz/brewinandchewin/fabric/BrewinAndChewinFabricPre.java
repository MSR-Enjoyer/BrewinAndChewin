package umpaz.brewinandchewin.fabric;

import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.fabric.platform.BnCPlatformHelperFabric;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class BrewinAndChewinFabricPre implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        BrewinAndChewin.init(new BnCPlatformHelperFabric());
    }
}
