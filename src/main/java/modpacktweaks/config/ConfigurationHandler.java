package modpacktweaks.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.client.gui.ModDownload;
import modpacktweaks.util.TxtParser;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property.Type;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ConfigurationHandler
{
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static HashMap<String, Boolean> am2SpawnControls = new HashMap<String, Boolean>();
	public static int bookID;

	public static String bookTitle;
	public static String bookAuthor;
	public static String changelogTitle;
	public static String supportedModsName;
	public static int guideSkin;

	public static boolean showDownloadGUI;

	public static boolean doSpawnBook;

	public static File cfg;

	/** ArrayList of Strings, the strings are each one whole page **/
	public static List<String> bookText;
	public static List<String> changelog;

	public static String packName;
	public static String packVersion;
	public static String packAcronym;

	public static boolean shouldLoadGUI = true;

	public static boolean autoUpdateBooks;

	public static void init(File file)
	{
		cfg = file;

		Configuration config = new Configuration(file);
		config.load();

		bookID = config.getItem("bookId", 21650).getInt() - 256;

		bookTitle = config.get("Book Settings", "bookTitle", "Welcome Packet", "The title of the custom spawn book", Type.STRING).getString();
		bookAuthor = config.get("Book Settings", "bookAuthor", "Some Guys", "The author of the custom spawn book", Type.STRING).getString();
		changelogTitle = config.get("Book Settings", "changelogTitle", "Changelog", "The title of the changelog").getString();
		supportedModsName = config.get("Guide Settings", "supportedModsFilename", "SupportedMods",
				"The file name of the file to read the mod documentation from (used to support translation). Do not include the extension in the filename (it is .txt)").getString();
		guideSkin = config.get("Guide Settings", "GuideSkin", 0, "The skin of the guide GUI/item, 0=tech, 1=scroll").getInt();
		doSpawnBook = config.get("Book Settings", "doSpawnBook", true, "Whether or not to give the player a welcome book on first spawn").getBoolean(true);

		showDownloadGUI = config.get("GUI Settings", "showDownloadGUI", true, "Whether to show the download GUI at all").getBoolean(true);

		packName = config.get("Pack Info", "packName", "Modpack #42", "The full name of the modpack").getString();
		packVersion = config.get("Pack Info", "packVersion", "0.0.0", "The version of the modpack").getString();
		packAcronym = config.get("Pack Info", "packAcronym", "reallyLongAcronymSoYouDontForget", "The acronym of the modpack").getString();

		autoUpdateBooks = config
				.get("Other options",
						"autoUpdateBooks",
						true,
						"Whether (custom) books will update on right click. NOTE: This only works with CUSTOM books, not vanilla written books. Also, this is for debug purposes only, it can cause severe I/O lag in normal play.")
				.getBoolean(true);

		config.save();
	}

	public static void loadClientsideJson()
	{
		File modDownloads = new File(cfg.getParentFile().getAbsolutePath() + "/modDownloads.json");

		JsonArray arr = initializeJson("modDownloads.json", modDownloads).getAsJsonArray();

		for (int i = 0; i < arr.size(); i++)
		{
			ModpackTweaks.proxy.addJsonToGUI(gson.fromJson(arr.get(i), ModDownload.class));
		}
	}

	private static JsonElement initializeJson(String filename, File f)
	{
		try
		{
			if (!f.exists())
				copyJsonFromJar(filename, f);

			String json = "";

			Scanner scan = new Scanner(f);
			while (scan.hasNextLine())
			{
				json += scan.nextLine() + "\n";
			}
			scan.close();

			return new JsonParser().parse(json);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static void copyJsonFromJar(String filename, File to) throws IOException
	{
		System.out.println("Copying file " + filename + " from jar");
		File jsonFile = new File(ModpackTweaks.class.getResource("/assets/modpacktweaks/misc/" + filename).getPath());
		FileUtils.copyFile(jsonFile, to);
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

	/**
	 * Sets a config value manually by editing the text file
	 * 
	 * @param prefix
	 *            - The prefix of the config option (anything before '='), must
	 *            match exactly.
	 * @param from
	 *            - The setting to change it from
	 * @param to
	 *            - The setting to change it to
	 * @return whether anything changed
	 */
	public static boolean manuallyChangeConfigValue(String prefix, String from, String to)
	{
		return manuallyChangeConfigValue(null, prefix, from, to);
	}

	/**
	 * Same as <code>manuallyChangeConfigValue(String, String, String)</code>,
	 * but with an additional parameter for <i>what</i> config file to edit
	 * 
	 * @param filePathFromConfigFolder
	 *            - the full path to the files, including extensions, from
	 *            inside config/
	 * @param prefix
	 *            - The prefix of the config option (anything before '='), must
	 *            match exactly.
	 * @param from
	 *            - The setting to change it from
	 * @param to
	 *            - The setting to change it to
	 * @return whether anything changed
	 */
	public static boolean manuallyChangeConfigValue(String filePathFromConfigFolder, String prefix, String from, String to)
	{
		File config = filePathFromConfigFolder == null ? cfg : new File(cfg.getParentFile().getParent() + "/" + filePathFromConfigFolder);
		boolean found = false;

		try
		{
			FileReader fr1 = new FileReader(config);
			BufferedReader read = new BufferedReader(fr1);

			ArrayList<String> strings = new ArrayList<String>();

			while (read.ready())
			{
				strings.add(read.readLine());
			}

			fr1.close();
			read.close();

			FileWriter fw = new FileWriter(config);
			BufferedWriter bw = new BufferedWriter(fw);

			for (String s : strings)
			{
				if (!found && s.contains(prefix + "=" + from) && !s.contains("=" + to))
				{
					s = s.replace(prefix + "=" + from, prefix + "=" + to);
					ModpackTweaks.logger.info("Successfully changed config value " + prefix + " from " + from + " to " + to);
					found = true;
				}

				fw.write(s + "\n");
			}

			bw.flush();
			bw.close();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		return found;
	}

	/**
	 * Finds the config value in the file specified (path starting after
	 * config/), and for the key specified
	 * 
	 * @param filePathFromConfigFolder
	 *            - The path to the file, everything up to config/ is calculated
	 *            for you
	 * @param key
	 *            - A key to find the value by, does not need to match exactly
	 * @return A parseable string that can be transformed into any of the types
	 *         of config values, for instance using
	 *         <code>Boolean.parseBoolean(String)</code>
	 */
	public static String manuallyGetConfigValue(String filePathFromConfigFolder, String key)
	{
		File config = new File(ConfigurationHandler.cfg.getParentFile().getParent() + "/" + filePathFromConfigFolder);
		boolean noConfig = false;
		Scanner scan = null;

		try
		{
			scan = new Scanner(config);
		}
		catch (FileNotFoundException e)
		{
			noConfig = true;
		}

		if (noConfig)
			return "";

		while (scan.hasNext())
		{
			String s = scan.next();

			if (s.contains(key))
			{
				scan.close();
				return s.substring(s.indexOf("=") + 1, s.length());
			}
		}
		return "";
	}
}
