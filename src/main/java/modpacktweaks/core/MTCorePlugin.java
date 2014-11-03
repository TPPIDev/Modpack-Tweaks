package modpacktweaks.core;

import java.io.File;
import java.util.Map;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.client.gui.UpdateGui;
import modpacktweaks.config.ConfigurationHandler;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class MTCorePlugin implements IFMLLoadingPlugin, IFMLCallHook
{
    private File mcDir;
    
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return getClass().getName();
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        mcDir = (File) data.get("mcLocation");
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }

    @Override
    public Void call() throws Exception
    {
        System.out.println(mcDir.getAbsolutePath());
        ConfigurationHandler.init(new File(mcDir.getAbsolutePath() + "/config/modpackTweaks/modpackTweaks.cfg"));
//        if (Launch.blackboard.get("Tweaks").equals("cpw.mods.fml.common.FMLTweaker")) // is client side. Don't ask.
        {
            UpdateGui.launch();
        }
        return null;
    }
}
