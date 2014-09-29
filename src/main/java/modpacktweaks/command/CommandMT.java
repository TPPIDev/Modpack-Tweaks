package modpacktweaks.command;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.item.ModItems;
import modpacktweaks.network.PacketHandler;
import modpacktweaks.network.message.MessageShowDownloadGUI;
import modpacktweaks.util.FileLoader;
import modpacktweaks.util.TxtParser;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

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
                    ModpackTweaks.logger.error("Invalid Player");
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
            icommandsender.addChatMessage(new ChatComponentText(getCommandUsage(icommandsender)));
            icommandsender.addChatMessage(new ChatComponentText("Valid args:"));
            icommandsender.addChatMessage(new ChatComponentText(validCommandString));
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
                    inv[i].stackTagCompound.toString().contains(ConfigurationHandler.bookAuthor) && // has the author
                    inv[i].getItem() == Items.written_book) // is a vanilla book
            {
                inv[i] = null;
            }
        }
    }

    private void processCommandGuide(ICommandSender command)
    {
        ItemStack stack = new ItemStack(ModItems.book, 1, 2);
        stack.setTagInfo("title", new NBTTagString((ConfigurationHandler.useAcronym ? ConfigurationHandler.packAcronym : ConfigurationHandler.packName) + " "
                + StatCollector.translateToLocal("item.guide.name")));

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
                command.addChatMessage(new ChatComponentText("Valid mod names:"));
                listMods(command);
            }

        }
        else
        {
            command.addChatMessage(new ChatComponentText("Proper Usage: /" + ConfigurationHandler.packAcronym.toLowerCase() + " mods <modname>"));
            command.addChatMessage(new ChatComponentText("or '/" + ConfigurationHandler.packAcronym.toLowerCase() + " mods list' to see valid names."));
        }

        return false;
    }

    private boolean processCommandDownload(ICommandSender command, String[] args)
    {
        EntityPlayer player = command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName());

        if (player != null)
        {
            PacketHandler.INSTANCE.sendTo(new MessageShowDownloadGUI(), (EntityPlayerMP) player);
            return true;
        }

        return false;
    }

    private boolean processCommandChangelog(ICommandSender command)
    {
        ItemStack changelog = ModItems.book.getChangelog();

        if (changelog == null)
            return false;

        if (!command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).inventory.addItemStackToInventory(changelog))
            ;
        command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).entityDropItem(changelog, 0);

        return true;
    }

    private void listMods(ICommandSender icommandsender)
    {
        String s = "";
        
        icommandsender.addChatMessage(new ChatComponentText("Listing mods:"));
        icommandsender.addChatMessage(new ChatComponentText(" "));
        
        for (int i = 1; i < supportedModsAndList.size(); i++)
        {
            s += supportedModsAndList.get(i);
            if (i < supportedModsAndList.size() - 1)
                s += ", ";
            if (s.length() > 40)
            {
                icommandsender.addChatMessage(new ChatComponentText(s));
                s = "";
            }
        }
    }

    private void giveModBook(String modName, ICommandSender command)
    {
        String properName = modProperNames.get(modName);

        ItemStack stack = new ItemStack(Items.written_book);

        stack.setTagInfo("author", new NBTTagString(ConfigurationHandler.bookAuthor));
        stack.setTagInfo("title", new NBTTagString("Guide To " + properName));

        NBTTagCompound nbttagcompound = stack.getTagCompound();
        NBTTagList bookPages = new NBTTagList();

        ArrayList<String> pages;

        pages = TxtParser.parseFileMods(FileLoader.getSupportedModsFile(), modName + ", " + properName);

        if (pages.get(0).startsWith("<") && pages.get(0).endsWith("> "))
        {
            command.addChatMessage(new ChatComponentText(pages.get(0).substring(1, pages.get(0).length() - 2)));
            return;
        }

        for (int i = 0; i < pages.size(); i++)
        {
            bookPages.appendTag(new NBTTagString(pages.get(i)));
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
    public int compareTo(Object o)
    {
        return this.getCommandName().compareTo(((ICommand) o).getCommandName());
    }
}
