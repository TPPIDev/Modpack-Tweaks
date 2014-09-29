package modpacktweaks.network;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.network.message.MessageShowDownloadGUI;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModpackTweaks.CHANNEL);
    private static int id = 0;
    
    public static void init()
    {
        INSTANCE.registerMessage(MessageShowDownloadGUI.class, MessageShowDownloadGUI.class, id++, Side.CLIENT);
    }
}
