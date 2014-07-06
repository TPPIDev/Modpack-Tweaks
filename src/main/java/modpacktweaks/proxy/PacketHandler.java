package modpacktweaks.proxy;

import modpacktweaks.client.gui.UpdateGui;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if (packet.data[0] == 0)
		{
			Minecraft.getMinecraft().displayGuiScreen(new UpdateGui(null, false));
		}
	}
}
