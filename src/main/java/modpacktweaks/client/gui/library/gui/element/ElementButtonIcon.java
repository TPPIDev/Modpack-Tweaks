package modpacktweaks.client.gui.library.gui.element;

import modpacktweaks.client.gui.library.gui.IGuiBase;
import modpacktweaks.client.gui.library.gui.utils.GuiUtils;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

public class ElementButtonIcon extends ElementButton
{
    IIcon icon;

    public ElementButtonIcon(IGuiBase parent, int x, int y, String id, IIcon icon)
    {
        super(parent, x, y, 20, id, null);
        this.icon = icon;
    }

    public ElementButtonIcon(IGuiBase parent, int x, int y, String id, String hover, IIcon icon)
    {
        super(parent, x, y, 20, id, null, hover);
        this.icon = icon;
    }

    @Override
    public void draw()
    {
        super.draw();

        if (texture != null)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            gui.getTextureManager().bindTexture(texture);
            GuiUtils.drawIcon(gui, icon, posX + sizeX / 2 - 8, posY + sizeY / 2 - 8, 1);
        }
    }
}
