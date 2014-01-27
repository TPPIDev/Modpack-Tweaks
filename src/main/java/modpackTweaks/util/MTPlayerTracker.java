package modpackTweaks.util;

import modpackTweaks.ModpackTweaks;
import modpackTweaks.event.MTEventHandler;
import modpackTweaks.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;

public class MTPlayerTracker implements IPlayerTracker
{

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		addBook(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		// Do Nothing
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
		// Do Nothing
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		System.out.println("adding NBT: " + MTEventHandler.NBTValOnDeath);
		player.getEntityData().setTag("TPPI", ModpackTweaks.eventHandler.getTag(player, true));
		
		addBook(player);
	}
	
	private boolean addBook(EntityPlayer player)
	{
		if (player != null && !player.getEntityData().getCompoundTag("TPPI").getBoolean("hasBook") && FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			System.out.println("adding book");

			player.getEntityData().setTag("TPPI", ModpackTweaks.eventHandler.getTag(player, false));
			
			ItemStack stack = ModItems.tppiBook.getGuide();
			player.inventory.addItemStackToInventory(stack);
			return true;
		}
		
		return false;
	}

}
