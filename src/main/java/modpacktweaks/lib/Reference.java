package modpacktweaks.lib;

import java.io.File;

import modpacktweaks.config.ConfigurationHandler;


public class Reference
{
	public static final String CHANNEL = "modpacktweaks";
	public static final String TAB_NAME = "modpackTweaks";
	
	private static File modsFolder;
	
	public static File getModsFolder()
	{
		return modsFolder == null ? modsFolder = new File(ConfigurationHandler.cfg.getParentFile().getParentFile().getParentFile().getAbsolutePath() + "/mods") : modsFolder;
	}
}
