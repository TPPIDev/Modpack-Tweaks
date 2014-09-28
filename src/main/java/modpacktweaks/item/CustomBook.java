package modpacktweaks.item;

import java.util.List;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.client.gui.GuiHelper;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.util.FileLoader;
import modpacktweaks.util.TxtParser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;

public class CustomBook extends ItemEditableBook
{
	public CustomBook()
	{
		super();
		setCreativeTab(ModpackTweaks.creativeTab);
	}

	private IIcon[] icons = new IIcon[3];
	private String[] unlocNames = {"item.welcomePacket", "item.changelog", "item.guide"};

	@Override
	public void registerIcons(IIconRegister par1IIconRegister)
	{
		icons[0] = par1IIconRegister.registerIcon("modpacktweaks:" + (Loader.isModLoaded("TPPITweaks") && !ConfigurationHandler.neverTPPITexture || ConfigurationHandler.forceTPPITexture ? "tppiBook" : "book" + ConfigurationHandler.bookTexture));
		icons[1] = Items.written_book.getIconFromDamage(0);
		icons[2] = par1IIconRegister.registerIcon(ConfigurationHandler.guideSkin == 0 ? "modpacktweaks:guide1" : "modpacktweaks:guide2");
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass)
	{
		return icons[stack.getItemDamage()];
	}
	
	@Override
	public IIcon getIconFromDamage(int par1)
	{
		return icons[par1];
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (stack.getItemDamage() == 2 && FMLCommonHandler.instance().getSide().isClient())
		{
			GuiHelper.doGuideGUI();
		}
		else if (stack.getItemDamage() != 2 && (ConfigurationHandler.autoUpdateBooks || stack.stackTagCompound == null || !stack.getTagCompound().getString("version").equals(ModpackTweaks.VERSION)))
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			{
				stack.setTagCompound(new NBTTagCompound());
				addTextToBook(stack);

				player.inventoryContainer.detectAndSendChanges();

				GuiHelper.doBookGUI(player, stack, false);
				return stack;
			}
		}
		else if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			GuiHelper.doBookGUI(player, stack, false);
		return stack;
	}

	public ItemStack addTextToBook(ItemStack book)
	{
	    // TODO 1.7 figure out how book data works
		NBTTagCompound nbttagcompound;
		NBTTagList bookPages;

		book.setTagInfo("author", new NBTTagString("author", ConfigurationHandler.bookAuthor));
		
		List<String> text;
		int damage = book.getItemDamage();
		
		switch (damage)
		{
		case 0:			
			book.setTagInfo("title", new NBTTagString("title", ConfigurationHandler.bookTitle));

			nbttagcompound = book.getTagCompound();
			bookPages = new NBTTagList("pages");

			text = ConfigurationHandler.autoUpdateBooks ? ConfigurationHandler.loadGuideText(FileLoader.getGuideText()) : ConfigurationHandler.bookText;
			
			for (int i = 0; i < text.size(); i++)
			{
				bookPages.appendTag(new NBTTagString("" + i, text.get(i)));
			}

			nbttagcompound.setTag("pages", bookPages);
			nbttagcompound.setString("version", ModpackTweaks.VERSION);

			break;
		case 1:
			book.setTagInfo("title", new NBTTagString("title", ConfigurationHandler.changelogTitle));
			
			nbttagcompound = book.getTagCompound();
			bookPages = new NBTTagList("pages");
			
			text = ConfigurationHandler.autoUpdateBooks ? ConfigurationHandler.loadChangelogText(FileLoader.getChangelogText()) : ConfigurationHandler.changelog;
			
			if (text == null)
			{
				text = ConfigurationHandler.loadChangelogText(FileLoader.getChangelogText());
			}
			
			for (int i = 0; i < text.size(); i++)
			{
				bookPages.appendTag(new NBTTagString("" + i, text.get(i)));
			}

			nbttagcompound.setTag("pages", bookPages);
			nbttagcompound.setString("version", ModpackTweaks.VERSION);

			break;
		}
		return book;
	}

	public ItemStack getGuide()
	{
		ConfigurationHandler.bookText = TxtParser.parseFileMain(FileLoader.getGuideText());
		return addTextToBook(new ItemStack(ModItems.book));
	}
	
	public ItemStack getChangelog()
	{
		ConfigurationHandler.changelog = TxtParser.parseFileMain(FileLoader.getChangelogText());
		return addTextToBook(new ItemStack(ModItems.book, 1, 1));
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack)
	{
		return false;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(addTextToBook(new ItemStack(this, 1, 0)));
		par3List.add(addTextToBook(new ItemStack(this, 1, 1)));
		par3List.add(new ItemStack(this, 1, 2));
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "MTbook";
	}
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack)
	{
		return unlocNames[par1ItemStack.getItemDamage()];
	}

}