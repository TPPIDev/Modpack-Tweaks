package modpacktweaks.event;

import java.util.logging.Level;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker
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
		ModEventHandler.NBTValOnDeath = player.getEntityData().getCompoundTag("modpacktweaks").getBoolean("hasBook");
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		ModpackTweaks.logger.log(Level.INFO, "adding NBT: " + ModEventHandler.NBTValOnDeath);
		player.getEntityData().setTag("modpacktweaks", ModpackTweaks.eventHandler.getTag(player, true));
		
		addBook(player);
	}
	
	private boolean addBook(EntityPlayer player)
	{
		if (player != null && ConfigurationHandler.doSpawnBook && !player.getEntityData().getCompoundTag("modpacktweaks").getBoolean("hasBook") && !player.worldObj.isRemote)
		{
			ModpackTweaks.logger.log(Level.INFO, "Adding book");

			player.getEntityData().setTag("modpacktweaks", ModpackTweaks.eventHandler.getTag(player, false));
			
			ItemStack stack = ModItems.book.getGuide();
			player.inventory.addItemStackToInventory(stack);
			return true;
		}
		
		return false;
	}

}
