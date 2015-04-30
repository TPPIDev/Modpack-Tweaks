package modpacktweaks.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.client.gui.ModDownload;
import modpacktweaks.util.TxtParser;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ConfigurationHandler
{
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static String bookTitle;
	public static String bookAuthor;
	public static int bookTexture;
	public static boolean forceTPPITexture;
	public static boolean neverTPPITexture;

	public static String changelogTitle;
	public static String supportedModsName;
	public static int guideSkin;
	public static boolean useAcronym;

	public static boolean showDownloadGUI;
    public static String downloadGuiText;

	public static boolean doSpawnBook;
	public static boolean doChangelog;
	public static boolean doGuide;
	public static boolean doModBooks;

	public static File cfg;

	/** ArrayList of Strings, the strings are each one whole page **/
	public static List<String> bookText;
	public static List<String> changelog;

	public static String packName;
	public static String packVersion;
	public static String packAcronym;

	public static boolean shouldLoadGUI = true;

	public static boolean autoUpdateBooks;
	public static boolean forcePageBreaks;

	public static String guideHomeText = "Welcome to the Documentation System, your source documentation for all mods in this pack. To start, click on a button signifying the letter the mod starts with, then click on the mod name to read the documentation related to it that we have available. To return to this menu, press the home button at any time.";

	public static void init(File file)
	{
		cfg = file;

		Configuration config = new Configuration(file);
		config.load();

		bookTitle = config.get("Book Settings", "bookTitle", "Welcome Packet", "The title of the custom spawn book").getString();
		bookAuthor = config.get("Book Settings", "bookAuthor", "Some Guys", "The author of the custom spawn book").getString();
		changelogTitle = config.get("Book Settings", "changelogTitle", "Changelog", "The title of the changelog").getString();
		bookTexture = config.getInt("bookTexture", "Book Settings", 1, 1, 4, "The texture of the spawn book (1-4)\n1 - Gray cover\n2 - Light blue cover\n3 - Green cover\n4 - Potato book");
		forceTPPITexture = config.get("Book Settings", "forceTPPITexture", false, "Forces the texture used in TPPI, if for some reason you want the snoo on your book...").getBoolean(false);
		neverTPPITexture = config.get("Book Settings", "neverTPPITexture", false, "Disables the TPPI book texture, even if TPPITweaks is detected").getBoolean(false);

		supportedModsName = config.get("Guide Settings", "supportedModsFilename", "SupportedMods",
				"The file name of the file to read the mod documentation from (used to support translation). Do not include the extension in the filename (it is .txt)").getString();
		guideSkin = config.get("Guide Settings", "GuideSkin", 0, "The skin of the guide GUI/item, 0=tech, 1=scroll").getInt();
		useAcronym = config.get("Guide Settings", "useAcronym", false, "Enable this if your pack name is too long to be on the guide item name, it will switch to using the acronym instead")
				.getBoolean(false);
		guideHomeText = config.get("Guide Settings", "guideHomeText", guideHomeText, "The text on the main screen of the guide.").getString();

		showDownloadGUI = config.get("GUI Settings", "showDownloadGUI", true, "Whether to show the download GUI at all").getBoolean(true);
        downloadGuiText = config.get("GUI Settings", "downloadGUIText",
                "Hey there! This seems like the first time you are starting %name%. Welcome!\\n" +
                        "This menu will not show again unless enabled in the ModpackTweaks config.\\n" +
                        "Alternatively, you may use the command \"/%acro% download\" to show it in-game.\\n\\n" +
                        "As it turns out, there are some mods we really wanted to include, but couldn't ship directly with the rest of the pack.\\n" +
                        "Though we had to leave them out, you may use this little utility to help add them manually, to gain what we feel is the full experience.",
                "Text to display when a mod is available to download\nUse \"\\n\" to create a linebreak. If the line gets too long, it will auto-break.\nUse \"%name%\" to insert the pack name.\nUse \"%acro%\" to insert the pack acronym").getString();

        packName = config.get("Pack Info", "packName", "Modpack #42", "The full name of the modpack").getString();
		packVersion = config.get("Pack Info", "packVersion", "0.0.0", "The version of the modpack").getString();
		packAcronym = config.get("Pack Info", "packAcronym", "reallyLongAcronymSoYouDontForget", "The acronym of the modpack").getString();

		autoUpdateBooks = config.get("Other options", "autoUpdateBooks", false, 
				"Whether (custom) books will update on right click. NOTE: This only works with CUSTOM books, not vanilla written books. Also, this is for debug purposes only, it can cause severe I/O lag in normal play.").getBoolean(false);
		forcePageBreaks = config.get("Other options", "forcePageBreaks", true,
				"Forces a page break after a certain amount of characters. Disable this if you are having issues with pages breaking too soon.").getBoolean(true);

		doSpawnBook = config.get("Global Settings", "doSpawnBook", true, "Whether or not to give the player a welcome book on first spawn").getBoolean(true);
		doGuide = config.get("Global Settings", "doGuide", true, "Whether or not to allow the guide command").getBoolean(true);
		doChangelog = config.get("Global Settings", "doChangelog", true, "Whether or not to allow the changelog command").getBoolean(true);
		doModBooks = config.get("Global Settings", "doModBooks", true, "Whether or not to allow the mod book commands").getBoolean(true);

		config.save();

		assert !(forceTPPITexture && neverTPPITexture) : "Do not force enable AND force disable the TPPI texture!";
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
		URL u = ModpackTweaks.class.getResource("/assets/modpacktweaks/misc/" + filename);
		FileUtils.copyURLToFile(u, to);
	}

	/**
	 * Method that gathers the info for the book given to players on spawn
	 * 
	 * @param file
	 *            - The input stream to gather the text from
	 */
	public static List<String> loadGuideText(InputStream file)
	{
		bookText = file == null ? new ArrayList<String>() : TxtParser.parseFileMain(file);
		return bookText;
	}

	public static List<String> loadChangelogText(InputStream file)
	{
		changelog = file == null ? new ArrayList<String>() : TxtParser.parseFileMain(file);
		return changelog;
	}
}
