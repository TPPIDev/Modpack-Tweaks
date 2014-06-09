package modpacktweaks.item;

import modpacktweaks.config.ConfigurationHandler;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems {
	
	public static CustomBook book;
	
	public static void initItems() {
		if (ConfigurationHandler.bookID != 0)
		{
			book = new CustomBook(ConfigurationHandler.bookID);
			GameRegistry.registerItem(book, "MTbook");
		}
	}
	
	public static void registerRecipes()
	{
		GameRegistry.addShapelessRecipe(book.getGuide(), Item.ingotIron, Item.paper, Item.paper, Item.paper);
	}
}
