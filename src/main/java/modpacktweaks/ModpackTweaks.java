package modpacktweaks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import modpacktweaks.client.gui.GuiHelper;
import modpacktweaks.command.CommandMT;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.event.PlayerTracker;
import modpacktweaks.item.ModItems;
import modpacktweaks.proxy.CommonProxy;
import modpacktweaks.util.FileLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "modpackTweaks", name = "ModpackTweaks", version = ModpackTweaks.VERSION, dependencies = "required-after:NotEnoughItems;after:ThermalExpansion")
public class ModpackTweaks
{
	public static final String VERSION = "0.1.0";

	@Instance
	public static ModpackTweaks instance;

	@SidedProxy(clientSide = "modpacktweaks.proxy.ClientProxy", serverSide = "modpacktweaks.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static PlayerTracker playerTracker;

	public static final Logger logger = LogManager.getLogger("ModpackTweaks");

	public static CreativeTabs creativeTab = new CreativeTabs("tabMT")
	{
		@Override
		public Item getTabIconItem()
		{
		    return ModItems.book;
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{		
		ConfigurationHandler.init(new File(event.getModConfigurationDirectory().getAbsolutePath() + "/modpackTweaks/modpackTweaks.cfg"));
		ConfigurationHandler.loadClientsideJson();
		
		try
		{
			FileLoader.init(ConfigurationHandler.cfg, 0);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		ConfigurationHandler.loadGuideText(FileLoader.getGuideText());
		ConfigurationHandler.loadChangelogText(FileLoader.getChangelogText());

		CommandMT.initValidCommandArguments(FileLoader.getSupportedModsFile());

		ModItems.initItems();

		playerTracker = new PlayerTracker();
		// TODO 1.7 IPlayerTracker GameRegistry.registerPlayerTracker(playerTracker);
		MinecraftForge.EVENT_BUS.register(playerTracker);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ModItems.registerRecipes();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if (FMLCommonHandler.instance().getSide().isClient())
		{
			try
			{
				GuiHelper.initMap();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onFMLServerStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandMT());
	}
}
