package modpacktweaks.event;

import java.util.logging.Level;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.IPlayerTracker;

// TODO 1.7 Where did this go!?!?
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
		// Do Nothing
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		// Do Nothing
	}
	
	private boolean addBook(EntityPlayer player)
	{
		if (player != null && ConfigurationHandler.doSpawnBook && !player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean("MT:hasBook") && !player.worldObj.isRemote)
		{
			ModpackTweaks.logger.log(Level.INFO, "Adding book");

			player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setBoolean("MT:hasBook", true);
			
			ItemStack stack = ModItems.book.getGuide();
			player.inventory.addItemStackToInventory(stack);
			return true;
		}
		
		return false;
	}

}
