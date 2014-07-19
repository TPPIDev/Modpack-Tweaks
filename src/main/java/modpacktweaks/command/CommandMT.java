package modpacktweaks.command;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.item.ModItems;
import modpacktweaks.lib.Reference;
import modpacktweaks.util.FileLoader;
import modpacktweaks.util.TxtParser;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class CommandMT extends CommandBase
{
	private static HashMap<String, String> modProperNames = new HashMap<String, String>();
	private static HashSet<String> validCommands = new HashSet<String>();

	/** First index is list, rest are mod names **/
	private static ArrayList<String> supportedModsAndList = new ArrayList<String>();

	public static void initValidCommandArguments(InputStream file)
	{
		validCommands.add("download");
		
		if (ConfigurationHandler.doModBooks)
			validCommands.add("mods");
		if (ConfigurationHandler.doChangelog)
			validCommands.add("changelog");
		if (ConfigurationHandler.doGuide)
			validCommands.add("guide");
		if (ConfigurationHandler.doModBooks)
			validCommands.add("removeBooks");

		supportedModsAndList.add("list");

		supportedModsAndList.addAll(TxtParser.getSupportedMods(file));
	}

	public static void addProperNameMapping(String argName, String properName)
	{
		modProperNames.put(argName, properName);
	}

	private boolean isValidArgument(String s)
	{
		return validCommands.contains(s);
	}

	@Override
	public String getCommandName()
	{
		return ConfigurationHandler.packAcronym.toLowerCase();
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/" + ConfigurationHandler.packAcronym.toLowerCase() + " <arg>";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1iCommandSender)
	{
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender command, String[] par2ArrayOfStr)
	{
		if (par2ArrayOfStr.length == 1)
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, validCommands.toArray(new String[validCommands.size()]));
		}
		else if (par2ArrayOfStr.length == 2)
		{
			if (par2ArrayOfStr[0].equals("mods"))
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, supportedModsAndList.toArray(new String[supportedModsAndList.size()]));
			else
				return null;
		}
		else
			return null;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring)
	{
		if (astring.length > 0 && isValidArgument(astring[0]))
		{
			if (astring[0].equalsIgnoreCase("download"))
			{
				if (!processCommandDownload(icommandsender, astring))
					ModpackTweaks.logger.log(Level.SEVERE, "Invalid Player");
			}
			else if (astring[0].equalsIgnoreCase("mods") && ConfigurationHandler.doModBooks)
			{
				processCommandMods(icommandsender, astring);
			}
			else if (astring[0].equalsIgnoreCase("changelog") && ConfigurationHandler.doChangelog)
			{
				processCommandChangelog(icommandsender);
			}
			else if (astring[0].equalsIgnoreCase("guide") && ConfigurationHandler.doGuide)
			{
				processCommandGuide(icommandsender);
			}
			else if (astring[0].equalsIgnoreCase("removeBooks") && ConfigurationHandler.doModBooks)
			{
				removeGuideBooks(icommandsender);
			}
		}
		else
		{
			String validCommandString = "";
			Iterator<String> it = validCommands.iterator();
			for (int i = 0; i < validCommands.size(); i++)
			{
				validCommandString += it.next();
				if (i < validCommands.size() - 1)
					validCommandString += ", ";
			}
			icommandsender.sendChatToPlayer(new ChatMessageComponent().addText(getCommandUsage(icommandsender)));
			icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("Valid args:"));
			icommandsender.sendChatToPlayer(new ChatMessageComponent().addText(validCommandString));
		}
	}

	private void removeGuideBooks(ICommandSender command)
	{
		EntityPlayer player = command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName());
		ItemStack[] inv = player.inventory.mainInventory;
		for (int i = 0; i < inv.length; i++)
		{
			if (inv[i] != null && // no null itemstack
					inv[i].stackTagCompound != null && // no null stack tag
					inv[i].stackTagCompound.toString().contains(ConfigurationHandler.bookAuthor) && // has
																									// the
																									// author
					inv[i].itemID == Item.writtenBook.itemID) // is a vanilla
																// book

				inv[i] = null;
		}
	}

	private void processCommandGuide(ICommandSender command)
	{
		ItemStack stack = new ItemStack(ModItems.book, 1, 2);
		stack.setTagInfo("title", new NBTTagString("title", (ConfigurationHandler.useAcronym ? ConfigurationHandler.packAcronym : ConfigurationHandler.packName) + " " + StatCollector.translateToLocal("item.guide.name")));

		if (!command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).inventory.addItemStackToInventory(stack))
			command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).entityDropItem(stack, 0);
	}

	private boolean processCommandMods(ICommandSender command, String[] args)
	{
		if (args.length == 2)
		{
			if (args[1].equals("list"))
			{
				listMods(command);
				return true;
			}
			else if (supportedModsAndList.contains(args[1]))
			{
				giveModBook(args[1], command);
			}
			else
			{
				command.sendChatToPlayer(new ChatMessageComponent().addText("Valid mod names:"));
				listMods(command);
			}

		}
		else
		{
			command.sendChatToPlayer(new ChatMessageComponent().addText("Proper Usage: /" + ConfigurationHandler.packAcronym.toLowerCase() + " mods <modname>"));
			command.sendChatToPlayer(new ChatMessageComponent().addText("or '/" + ConfigurationHandler.packAcronym.toLowerCase() + " mods list' to see valid names."));
		}

		return false;
	}

	private boolean processCommandDownload(ICommandSender command, String[] args)
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();

		packet.channel = Reference.CHANNEL;

		byte[] bytes = { (byte) 0 };
		boolean showGui = command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()) != null;

		if (showGui)
		{
			packet.length = 1;
			packet.data = bytes;
			PacketDispatcher.sendPacketToPlayer(packet, (Player) command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()));
			return true;
		}

		return false;
	}

	private boolean processCommandChangelog(ICommandSender command)
	{
		ItemStack changelog = ModItems.book.getChangelog();

		if (changelog == null)
			return false;

		if (!command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).inventory.addItemStackToInventory(changelog));
		command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).entityDropItem(changelog, 0);

		return true;
	}

	private void listMods(ICommandSender icommandsender)
	{
		String s = "";
		String total = "";
		icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("Listing mods:\n"));
		for (int i = 1; i < supportedModsAndList.size(); i++)
		{
			s += supportedModsAndList.get(i);
			if (i < supportedModsAndList.size() - 1)
				s += ", ";
			if (s.length() > 40)
			{
				total += s + "\n";
				s = "";
			}
		}

		icommandsender.sendChatToPlayer(new ChatMessageComponent().addText(total));
	}

	private void giveModBook(String modName, ICommandSender command)
	{
		String properName = modProperNames.get(modName);

		ItemStack stack = new ItemStack(Item.writtenBook);

		stack.setTagInfo("author", new NBTTagString("author", ConfigurationHandler.bookAuthor));
		stack.setTagInfo("title", new NBTTagString("title", "Guide To " + properName));

		NBTTagCompound nbttagcompound = stack.getTagCompound();
		NBTTagList bookPages = new NBTTagList("pages");

		ArrayList<String> pages;

		pages = TxtParser.parseFileMods(FileLoader.getSupportedModsFile(), modName + ", " + properName);

		if (pages.get(0).startsWith("<") && pages.get(0).endsWith("> "))
		{
			command.sendChatToPlayer(new ChatMessageComponent().addText(pages.get(0).substring(1, pages.get(0).length() - 2)));
			return;
		}

		for (int i = 0; i < pages.size(); i++)
		{
			bookPages.appendTag(new NBTTagString("" + i, pages.get(i)));
		}

		nbttagcompound.setTag("pages", bookPages);

		if (!command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).inventory.addItemStackToInventory(stack))
			command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).entityDropItem(stack, 0);
	}

	public static String getProperName(String modid)
	{
		return modProperNames.get(modid);
	}
	
	@Override
    public int compareTo(Object o) {
        if (o instanceof ICommand) {
            return this.compareTo((ICommand) o);
        } else {
            return 0;
        }
    }
}
