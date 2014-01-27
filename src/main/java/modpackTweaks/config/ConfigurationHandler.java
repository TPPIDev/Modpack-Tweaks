package modpackTweaks.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import modpackTweaks.util.TxtParser;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property.Type;

public class ConfigurationHandler
{

	public static HashMap<String, Boolean> am2SpawnControls = new HashMap<String, Boolean>();
	public static int bookID;
	public static int materialID;
	
	public static String bookTitle;
	public static String bookAuthor;
	public static String changelogTitle;

	public static boolean showDownloadGUI;
	
	public static File cfg;
	
	/** ArrayList of Strings, the strings are each one whole page **/
	public static List<String> bookText;
	public static List<String> changelog;
	public static String commandName;


	public static void init(File file)
	{
		cfg = file;
		
		Configuration config = new Configuration(file);
		config.load();
		
		bookID = config.getItem("bookID", 21000).getInt() - 256;
		
		bookTitle = config.get("Guide Info", "bookTitle", "Welcome Packet", "The title of the custom spawn book", Type.STRING).getString();
		bookAuthor = config.get("Guide Info", "bookAuthor", "The Modpack Team", "The author of the custom spawn book", Type.STRING).getString();
		changelogTitle = config.get("Guide Info", "changelogTitle", "Changelog", "The title of the changelog").getString();
		
		commandName = config.get("Command Info", "commandName", "modpackTweaks", "The first word used in the in-game command").getString();
		config.save();
	}

	/**
	 * Method that gathers the info for the book given to players on spawn
	 * 
	 * @param file
	 *            - The input stream to gather the text from
	 */
	public static void loadGuideText(InputStream file)
	{
		bookText = file == null ? new ArrayList<String>() : TxtParser.parseFileMain(file);
	}
	
	public static void loadChangelogText(InputStream file)
	{
		changelog = file == null ? new ArrayList<String>() : TxtParser.parseFileMain(file);
	}

	public static void stopShowingGUI()
	{
		try
		{
			FileReader fr1 = new FileReader(cfg);
			BufferedReader read = new BufferedReader(fr1);
			
			ArrayList<String> strings = new ArrayList<String>();
			
			while (read.ready())
			{
				strings.add(read.readLine());
			}
			
			fr1.close();
			read.close();
			
			FileWriter fw = new FileWriter(cfg);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (String s : strings)
			{
				if (s.equals("    B:showDownloadGUI=true"))
					s = "    B:showDownloadGUI=false";
				
				fw.write(s + "\n");
			}	
			
			bw.flush();
			bw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
