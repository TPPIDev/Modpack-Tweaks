package modpacktweaks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import modpacktweaks.client.gui.GuiHelper;
import modpacktweaks.command.CommandMT;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.event.ModEventHandler;
import modpacktweaks.event.PlayerTracker;
import modpacktweaks.item.ModItems;
import modpacktweaks.lib.Reference;
import modpacktweaks.util.FileLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cofh.network.PacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "modpackTweaks", name = "ModpackTweaks", version = ModpackTweaks.VERSION, dependencies = "required-after:NotEnoughItems;after:ThermalExpansion")
@NetworkMod(serverSideRequired = true, clientSideRequired = true, channels = { Reference.CHANNEL })
public class ModpackTweaks
{
	public static final String VERSION = "0.1.0";

	@Instance
	public static ModpackTweaks instance;

//	@SidedProxy(clientSide = "tppitweaks.proxy.ClientProxy", serverSide = "tppitweaks.proxy.CommonProxy")
//	public static CommonProxy proxy;

	public static ModEventHandler eventHandler;
	public static PlayerTracker playerTracker;

	public static final Logger logger = Logger.getLogger("ModpackTweaks");

	public static CreativeTabs creativeTab = new CreativeTabs("tabMT")
	{
		public net.minecraft.item.ItemStack getIconItemStack() 
		{
			return new ItemStack(ModItems.book, 1, 0);
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger.setParent(FMLCommonHandler.instance().getFMLLogger());
		
		ConfigurationHandler.init(new File(event.getModConfigurationDirectory().getAbsolutePath() + "/modpackTweaks/modpackTweaks.cfg"));

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
		GameRegistry.registerPlayerTracker(playerTracker);
		MinecraftForge.EVENT_BUS.register(playerTracker);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		eventHandler = new ModEventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		ModItems.registerRecipes();

		if (event.getSide().isClient()){
//			proxy.initTickHandler();
		}
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
