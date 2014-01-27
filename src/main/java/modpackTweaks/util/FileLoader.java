package modpackTweaks.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import modpackTweaks.ModpackTweaks;
import modpackTweaks.config.ConfigurationHandler;

public class FileLoader
{
	private static InputStream bookText, supportedMods, changelogText;
		
	public static void init(File file, int attempt) throws IOException
	{
		File dir = new File(file.getParent() + "");
		if (!dir.exists())
			dir.mkdir();
		
		/* 
		 * From here down commented out as it was 
		 * deemed useless and counter-productive. 
		 * If we add versioning for user-added content,
		 * this becomes useful again 
		 */
		
		/*
		try
		{
			bookText = new FileInputStream(new File(file.getParent() + "/BookText.txt"));
		}
		catch(FileNotFoundException e)
		{
			Scanner scan = new Scanner(TPPITweaks.class.getResourceAsStream("/assets/tppitweaks/lang/BookText.txt"));
			
			FileWriter newBookText = new FileWriter(new File(file.getParent() + "/BookText.txt"));
			
			while (scan.hasNext())
			{
				newBookText.write(scan.nextLine() + "\n");
			}
			
			scan.close();
			newBookText.flush(); 
			newBookText.close();
			
			if (attempt < 1)
				init(file, 1);
			else
			{
				System.err.println("TPPI Tweaks - IO Error, books will be partly non-functional. \n");
				e.printStackTrace();
			}
		}
		
		try
		{
			supportedMods = new FileInputStream(new File(file.getParent() + "/SupportedMods.txt"));
		}
		catch(FileNotFoundException e)
		{
			Scanner scan = new Scanner(TPPITweaks.class.getResourceAsStream("/assets/tppitweaks/lang/SupportedMods.txt"));
			
			FileWriter newBookText = new FileWriter(new File(file.getParent() + "/SupportedMods.txt"));
			
			while (scan.hasNext())
			{
				newBookText.write(scan.nextLine() + "\n");
			}
			
			scan.close();
			newBookText.flush(); 
			newBookText.close();
			
			if (attempt < 3)
				init(file, 2);
			else
			{
				System.err.println("TPPI Tweaks - IO Error, books will be partly non-functional. \n");
				e.printStackTrace();
			}
		}
		*/
	}
	
	public static Object manuallyGetConfigValue(Map<String, Object> m, String string, Object type) {
		if (type instanceof Boolean)
		{
			File config = new File(((File) m.get("mcLocation")).getAbsolutePath() + "/config/TPPI/TPPITweaks.cfg");
			boolean noConfig = false;
			Scanner scan = null;
			
			try {
				scan = new Scanner(config);
			} catch (FileNotFoundException e) {
				noConfig = true;
			}	
			
			if (noConfig)
				return true;
			
			while (scan.hasNext())
			{
				String s = scan.next();
				
				if (s.contains(string))
				{
					if (s.contains("true"))
						return true;
					else
						return false;
				}
			}
		}
		if (type instanceof String)
		{
			File config = new File(((File) m.get("mcLocation")).getAbsolutePath() + "/config/TPPI/TPPITweaks.cfg");
			boolean noConfig = false;
			Scanner scan = null;
			
			try {
				scan = new Scanner(config);
			} catch (FileNotFoundException e) {
				noConfig = true;
			}	
			
			if (noConfig)
				return "";
			
			while (scan.hasNext())
			{
				String s = scan.next();
				
				if (s.contains(string))
				{
					return s.substring(s.indexOf("=") + 1, s.length());
				}
			}
		}
		return false;
	}
	
	public static InputStream getGuideText()
	{
		bookText = ModpackTweaks.class.getResourceAsStream("/assets/tppitweaks/lang/BookText.txt");
		return bookText;
	}
	
	public static InputStream getChangelogText() throws FileNotFoundException
	{
		changelogText = new FileInputStream(new File(ConfigurationHandler.cfg.getParent() + "/changelog.txt"));
		return changelogText;
	}
	
	public static InputStream getSupportedMods()
	{
		supportedMods = ModpackTweaks.class.getResourceAsStream("/assets/tppitweaks/lang/SupportedMods.txt");
		return supportedMods;
	}
}
