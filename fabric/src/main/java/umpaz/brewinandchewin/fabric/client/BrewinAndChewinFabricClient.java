package umpaz.brewinandchewin.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.BrewinAndChewinClient;
import umpaz.brewinandchewin.fabric.client.platform.BnCClientPlatformHelperFabric;

public class BrewinAndChewinFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BrewinAndChewinClient.setHelper(new BnCClientPlatformHelperFabric());
        BrewinAndChewin.isClient = true;
    }
}
