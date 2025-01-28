package umpaz.brewinandchewin.common.utility;

import net.minecraftforge.fml.util.thread.SidedThreadGroup;

public class SideUtil {
    /**
     * Effectively {@link net.minecraftforge.fml.util.thread.EffectiveSide} except it does
     * not default to true sometimes on the server.
     * @return Whether the side is the client, returning false by default.
     */
    public static boolean isClient() {
        final ThreadGroup group = Thread.currentThread().getThreadGroup();
        return group instanceof SidedThreadGroup && ((SidedThreadGroup) group).getSide().isClient();
    }
}