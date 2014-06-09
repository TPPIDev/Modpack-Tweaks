package modpacktweaks.proxy;

import modpacktweaks.client.gui.ModDownload;
import modpacktweaks.client.gui.UpdateGui;

public class ClientProxy extends CommonProxy
{
	@Override
	public void addJsonToGUI(ModDownload fromJson)
	{
		UpdateGui.addModDownload(fromJson);
	}
}
