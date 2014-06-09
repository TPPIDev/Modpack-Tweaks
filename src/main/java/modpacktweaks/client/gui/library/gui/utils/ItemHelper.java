package modpacktweaks.client.gui.library.gui.utils;

import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemHelper
{
    static String[] dyes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };

    public static int getDyeColour(ItemStack item)
    {
        String ore = OreDictionary.getOreName(OreDictionary.getOreID(item));

        for (int i = 0; i < dyes.length; i++)
        {
            if (dyes[i].equals(ore))
            {
                return ItemDye.dyeColors[i];
            }
        }

        return ItemDye.dyeColors[15];
    }

    public static boolean isDye(ItemStack item)
    {
        String ore = OreDictionary.getOreName(OreDictionary.getOreID(item));
        return ore != null && ore.startsWith("dye");
    }
}
