package modpacktweaks.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.config.ConfigurationHandler;

public class FileLoader
{
	private static InputStream bookText, supportedMods, changelogText;

	public static void init(File file, int attempt) throws IOException
	{
		File dir = new File(file.getParent() + "");
		if (!dir.exists())
			dir.mkdir();
	}

	public static InputStream getGuideText() 
	{
		bookText = loadFile(new File(ConfigurationHandler.cfg.getParent() + "/BookText.txt"));
		return bookText;
	}

	public static InputStream getChangelogText()
	{
		changelogText = loadFile(new File(ConfigurationHandler.cfg.getParent() + "/changelog.txt"));
		return changelogText;
	}

	public static InputStream getSupportedModsFile()
	{
		supportedMods = loadFile(new File(ConfigurationHandler.cfg.getParent() + "/" + ConfigurationHandler.supportedModsName + ".txt"));
		return supportedMods;
	}
	
	private static InputStream loadFile(File file)
	{
		if (!file.exists())
		{
			FileWriter fw;
			try
			{
				file.createNewFile();
				fw = new FileWriter(file);
				fw.write("Default file, please make sure the correct file, " + file.getName() + ", exists in the modpackTweaks config directory before launching next time!");
				fw.flush();
				fw.close();
			} catch (IOException e1) {
				ModpackTweaks.logger.severe("Could not create default file" + file.getName() + "!");
				e1.printStackTrace();
			}
		}

		try
		{
			return new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			IOErr(file.getName(), e);
			return null;
		}
	}
	
	private static void IOErr(String filename, IOException e)
	{
		ModpackTweaks.logger.severe("IO error while loading ModpackTweaks, make sure nothing in the config folder is actively open and Minecraft has permission to read those files!");
		e.printStackTrace();
		throw new RuntimeException("IO Error in ModpackTweaks file loading, file: " + filename);
	}
}
