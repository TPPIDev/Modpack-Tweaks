package modpackTweaks.item;

import modpackTweaks.config.ConfigurationHandler;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems
{
	public static ModpackTweaksBook mtBook;

	public static void initItems()
	{
		mtBook = new ModpackTweaksBook(ConfigurationHandler.bookID);
	}

	public static void registerRecipes()
	{
		GameRegistry.addShapelessRecipe(mtBook.getGuide(), Item.ingotIron, Item.paper, Item.paper, Item.paper);
	}
}
