package modpackTweaks.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import modpackTweaks.ModpackTweaks;
import modpackTweaks.client.gui.ModDownload;
import modpackTweaks.config.ConfigurationHandler;
import modpackTweaks.lib.Reference;

public class FileLoader
{
	private static InputStream bookText, supportedMods, changelogText;
	
	private static String EXTENSION = ".removed";
		
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
	
	public static void disableTT()
	{
		for(File f : getTT()) {
			if(!f.getName().contains(EXTENSION)) {
				f.renameTo(new File(f.getAbsolutePath() + EXTENSION));
			}
		}
	}
	
	public static void enableTT()
	{
		for(File f : getTT()) {
			if(f.getName().contains(EXTENSION)) {
				f.renameTo(new File(f.getAbsolutePath().replace(EXTENSION, "")));
			}
		}
		
	}
	
	private static ArrayList<File> getTT()
	{
		ArrayList<File> thaumicTinkererFiles = new ArrayList<File>();
		File modJar;
		
		modJar = new File(Reference.modsFolder, Reference.TTFilename);
		if(modJar.exists()) {
			thaumicTinkererFiles.add(modJar);
		}
				
		modJar = new File(Reference.modsFolder, Reference.KAMIFilename);
		if(modJar.exists()) {
			thaumicTinkererFiles.add(modJar);
		}
				
		return thaumicTinkererFiles;
	}
	
	public static void getThaumicTinkererFilenameState() {
		
		File modJar;
		
		modJar = new File(Reference.modsFolder, Reference.TTFilename).exists() ? new File(Reference.modsFolder, Reference.TTFilename) : new File(Reference.modsFolder, Reference.TTFilename + EXTENSION);
		if(modJar.exists()) {
			Reference.TTFilename = modJar.getName();
		}
		
		modJar = new File(Reference.modsFolder, Reference.KAMIFilename).exists() ? new File(Reference.modsFolder, Reference.KAMIFilename) : new File(Reference.modsFolder, Reference.KAMIFilename + EXTENSION);
		if(modJar.exists()) {
			Reference.KAMIFilename = modJar.getName();
		}
		
	}
	
	public static Object manuallyGetConfigValue(Map<String, Object> m, String string, Object type) {
		if (type instanceof Boolean)
		{
			File config = new File(((File) m.get("mcLocation")).getAbsolutePath() + "/config/ModpackTweaks/ModpackTweaks.cfg");
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
			File config = new File(((File) m.get("mcLocation")).getAbsolutePath() + "/config/ModpackTweaks/ModpackTweaks.cfg");
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
		bookText = ModpackTweaks.class.getResourceAsStream("/assets/modpacktweaks/lang/BookText.txt");
		return bookText;
	}
	
	public static InputStream getChangelogText() throws FileNotFoundException
	{
		changelogText = new FileInputStream(new File(ConfigurationHandler.cfg.getParent() + "/changelog.txt"));
		return changelogText;
	}
	
	public static InputStream getSupportedMods()
	{
		supportedMods = ModpackTweaks.class.getResourceAsStream("/assets/modpacktweaks/lang/SupportedMods.txt");
		return supportedMods;
	}
	
	public static List<ModDownload> getDownloadMods() throws IOException
	{
		File file = new File(ConfigurationHandler.cfg.getParent() + "/downloadMods.txt");
		
		if (!file.exists())
		{
			file.createNewFile();
			populateFile(file);
		}
		
		FileInputStream modDownloadFile = new FileInputStream(new File(ConfigurationHandler.cfg.getParent() + "/downloadMods.txt"));
		
		Scanner scan = new Scanner(modDownloadFile);
		
		for (int i = 0; i < 5; i++)
		{
			if (scan.hasNextLine())
				scan.nextLine();
		}
		List<ModDownload> mods = new ArrayList<ModDownload>();
		while (scan.hasNextLine())
		{
			String name, url, modid;
			name = scan.nextLine();
			url = scan.nextLine();
			modid = scan.nextLine();
			
			mods.add(new ModDownload(name, url, modid));
			
			if (scan.hasNextLine())
				scan.nextLine();
		}
		scan.close();
		return mods;
	}
	
	private static void populateFile(File file) throws IOException
	{
		InputStream defaultFile = ModpackTweaks.class.getResourceAsStream("/assets/modpacktweaks/lang/downloadMods.txt");
		
		Scanner scan = new Scanner(defaultFile);
		String path = file.getCanonicalPath();
		FileWriter fw = new FileWriter(path);
		while (scan.hasNextLine())
		{
			fw.write(scan.nextLine() + "\n");
		}
		fw.flush();
		fw.close();
		scan.close();
	}
	
	public static void removeDuplicateMods()
	{
		ArrayList<File> jars = getTT();
		File removedTT = new File(Reference.modsFolder.getAbsolutePath() + "/" + Reference.TTFilename + EXTENSION);
		File removedKAMI = new File(Reference.modsFolder.getAbsolutePath() + "/" + Reference.KAMIFilename + EXTENSION);
		
		for (File f : jars)
		{
			if (f != null && removedTT.exists())
			{
				removedTT.delete();
			}
			if (f != null && removedKAMI.exists())
			{
				removedKAMI.delete();
			}
		}
	}
}
