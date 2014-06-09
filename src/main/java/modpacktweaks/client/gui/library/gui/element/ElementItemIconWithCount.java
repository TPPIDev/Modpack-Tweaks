package modpacktweaks.client.gui.library.gui.element;

import modpacktweaks.client.gui.library.gui.IGuiBase;
import net.minecraft.item.ItemStack;

public class ElementItemIconWithCount extends ElementItemIcon
{
    public ElementItemIconWithCount(IGuiBase parent, int x, int y, ItemStack stack)
    {
        super(parent, x, y, stack);
    }

    @Override
    public void draw()
    {
        super.draw();
        gui.getItemRenderer().renderItemOverlayIntoGUI(gui.getFontRenderer(), gui.getTextureManager(), item, posX, posY, item.stackSize > 999 ? "+" : null);
    }
}
