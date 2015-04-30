package modpacktweaks.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.event.ModEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UpdateGui extends GuiScreen
{
	protected GuiScreen parentScreen;
	private boolean noShow = true, firstTime;

	private static List<InstructionsGui> modScreens = new ArrayList<InstructionsGui>();
	
	private Iterator<InstructionsGui> iterator;

	public static void addModDownload(ModDownload mod)
	{		
		modScreens.add(new InstructionsGui(mod));
	}

	public UpdateGui(GuiScreen parentScreen, boolean firstTime)
	{
		this.parentScreen = parentScreen;
		
		iterator = modScreens.iterator();
		
		if (modScreens.isEmpty() && !firstTime)
				Minecraft.getMinecraft().thePlayer.sendChatMessage("You have all optional mods installed!");
		
		for (InstructionsGui g : modScreens)
		{
			if (!Loader.isModLoaded(g.mod.modid))
				noShow = false;
		}

		this.firstTime = firstTime;
		
		GuiHelper.updateGui = this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		if (noShow)
		{
			ModpackTweaks.logger.info("not opening GUI");
			this.mc.displayGuiScreen(this.parentScreen);
			return;
		}

		// Unsure exactly what this does but...it seems necessary
		Keyboard.enableRepeatEvents(true);

		this.buttonList.clear();

		this.buttonList.add(new GuiButton(-1, this.width / 2 - 150, this.height / 2 + 75, 300, 20, "Continue"));
		this.buttonList.add(new GuiButton(11, this.width / 2 - 150, this.height / 2 + 96, 300, 20, "Skip the downloads completely"));
	}

	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			if (button.id == 11)
				this.mc.displayGuiScreen(this.parentScreen);
			else
			{
				try
				{
					if (iterator.hasNext())
					{
						this.mc.displayGuiScreen(iterator.next());
					}
					/*
					else if (configGui != null)
					{
						File file=new File("config/modpackTweaks/config/hardconfig.zip");
						if (file.exists())	this.mc.displayGuiScreen(configGui);
						configGui = null;
					}
					*/
					else if (modScreens.size() > 0)
					{
						this.mc.displayGuiScreen(new RestartGui());
					}
					else
					{
						this.mc.displayGuiScreen(this.parentScreen);
					}
				}
				catch (Exception e)
				{
					ModpackTweaks.logger.error("Error opening webpage, please contact your modpack author.");
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawScreen(par1, par2, par3, true);
	}

    @SuppressWarnings("unchecked")
	public void drawScreen(int par1, int par2, float par3, boolean draw)
	{
		if (draw)
		{
			this.drawDefaultBackground();

            int heightLoc = 100;

            String[] lines = replaceTextCodes(ConfigurationHandler.downloadGuiText).split("\n");
            for (String s : lines) {

                List<String> info = fontRendererObj.listFormattedStringToWidth(s, this.width - 40);
                for (String infoCut : info) {
                    drawCenteredString(this.fontRendererObj, infoCut, this.width / 2, this.height / 2 - heightLoc, 0xFFFFFF);
                    heightLoc = heightLoc - 12;
                }
            }
		}

		super.drawScreen(par1, par2, par3);
	}

    private static String replaceTextCodes(String toReplace) {
        return toReplace
                .replace("\\n", "\n")
                .replace("%name%", ConfigurationHandler.packName)
                .replace("%acro%", ConfigurationHandler.packAcronym)
                .replace("%version%", ConfigurationHandler.packVersion);
    }
}
