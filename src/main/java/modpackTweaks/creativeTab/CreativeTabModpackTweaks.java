package modpackTweaks.creativeTab;

import modpackTweaks.item.ModItems;
import modpackTweaks.lib.Reference;
import net.minecraft.creativetab.CreativeTabs;

public class CreativeTabModpackTweaks extends CreativeTabs
{
	public CreativeTabModpackTweaks(int id)
	{
		super(id, Reference.TAB_NAME);
	}
	
	@Override
	public int getTabIconItemIndex() {
		return ModItems.tppiBook.itemID;
	}
	
	@Override
	public String getTabLabel() {
		return Reference.TAB_NAME;
	}
}
