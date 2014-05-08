package modpackTweaks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import modpackTweaks.command.CommandModpackTweaks;
import modpackTweaks.config.ConfigurationHandler;
import modpackTweaks.creativeTab.CreativeTabModpackTweaks;
import modpackTweaks.event.MTEventHandler;
import modpackTweaks.item.ModItems;
import modpackTweaks.lib.Reference;
import modpackTweaks.proxy.PacketHandler;
import modpackTweaks.util.FileLoader;
import modpackTweaks.util.MTPlayerTracker;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "modpacktweaks", name = "ModpackTweaks", version = ModpackTweaks.VERSION)
@NetworkMod(serverSideRequired = true, clientSideRequired = true, channels = { Reference.CHANNEL }, packetHandler = PacketHandler.class)
public class ModpackTweaks
{

	public static final String VERSION = "0.2.0";

	@Instance
	public static ModpackTweaks instance;

	public static MTEventHandler eventHandler;
	public static MTPlayerTracker playerTracker;

	public static CreativeTabModpackTweaks creativeTab = new CreativeTabModpackTweaks(CreativeTabs.getNextID());

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigurationHandler.init(new File(event.getModConfigurationDirectory().getAbsolutePath() + "/ModpackTweaks/ModpackTweaks.cfg"));

		try
		{
			FileLoader.init(ConfigurationHandler.cfg, 0);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		ConfigurationHandler.loadGuideText(FileLoader.getGuideText());
		try
		{
			ConfigurationHandler.loadChangelogText(FileLoader.getChangelogText());
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Changelog not found, please check the ModpackTweaks config folder.");
		}

		CommandModpackTweaks.initValidCommandArguments(FileLoader.getSupportedMods());

		ModItems.initItems();

		playerTracker = new MTPlayerTracker();
		GameRegistry.registerPlayerTracker(playerTracker);
		MinecraftForge.EVENT_BUS.register(playerTracker);

	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		eventHandler = new MTEventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		ModItems.registerRecipes();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		// RecipeTweaks.doRecipeTweaks();
	}

	@EventHandler
	public void onFMLServerStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandModpackTweaks());
	}
}