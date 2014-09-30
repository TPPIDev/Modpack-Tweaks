package modpacktweaks.event;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class BookHandler
{
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event)
	{
		addBook(event.player);
	}
	
	private boolean addBook(EntityPlayer player)
	{
		if (player != null && ConfigurationHandler.doSpawnBook && !player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean("MT:hasBook") && !player.worldObj.isRemote)
		{
			ModpackTweaks.logger.info("Adding book");

			NBTTagCompound persist = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			persist.setBoolean("MT:hasBook", true);
			player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);
			
			ItemStack stack = ModItems.book.getGuide();
			player.inventory.addItemStackToInventory(stack);
			return true;
		}
		
		return false;
	}

}
