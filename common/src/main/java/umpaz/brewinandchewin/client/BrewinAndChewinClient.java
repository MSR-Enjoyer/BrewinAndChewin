package umpaz.brewinandchewin.client;

import umpaz.brewinandchewin.platform.client.BnCClientPlatformHelper;

public class BrewinAndChewinClient {
    private static BnCClientPlatformHelper helper;

    public static BnCClientPlatformHelper getHelper() {
        return helper;
    }

    public static void setHelper(BnCClientPlatformHelper helper) {
        if (BrewinAndChewinClient.helper != null)
            return;
        BrewinAndChewinClient.helper = helper;
    }
}
