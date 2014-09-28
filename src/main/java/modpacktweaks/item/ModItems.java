package modpacktweaks.item;

import net.minecraft.init.Items;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems
{
    public static CustomBook book;

    public static void initItems()
    {
        book = new CustomBook();
        GameRegistry.registerItem(book, "MTbook");
    }

    public static void registerRecipes()
    {
        GameRegistry.addShapelessRecipe(book.getGuide(), Items.iron_ingot, Items.paper, Items.paper, Items.paper);
    }
}
