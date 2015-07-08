package modpacktweaks.client.gui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import modpacktweaks.ModpackTweaks;
import modpacktweaks.lib.Reference;
import modpacktweaks.util.DesktopApi;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InstructionsGui extends GuiScreen
{

	ModDownload mod;
	int hasOpened = 0;
	
	private static File lastDir = FileUtils.getUserDirectory();

	public InstructionsGui(ModDownload modDownload)
	{
		mod = modDownload;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		this.buttonList.add(new GuiButton(11, this.width / 2 - 100, this.height / 2 - 60, 200, 20, "Download"));
		this.buttonList.add(new GuiButton(12, this.width / 2 - 100, this.height / 2, 200, 20, "Find Mod"));
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 70, 200, 20, "Continue"));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();

		this.drawCenteredString(this.fontRendererObj, mod.name, this.width / 2, this.height / 2 - 115, 0xFFFFFF);
		this.drawCenteredString(this.fontRendererObj, "1. Click the button below to download " + mod.name + ",", this.width / 2, this.height / 2 - 95, 0xFFFFFF);
		this.drawCenteredString(this.fontRendererObj, "or press continue at the bottom to skip installation.", this.width / 2, this.height / 2 - 85, 0xFFFFFF);
		this.drawCenteredString(this.fontRendererObj, "(for adfly, wait 5 seconds then click \"SKIP AD\" in the upper right)", this.width / 2, this.height / 2 - 75, 0xFFFFFF);
		this.drawCenteredString(this.fontRendererObj, "2. Select the file from your computer and it will be added to the mods directory.", this.width / 2, this.height / 2 - 15, 0xFFFFFF);
		this.drawCenteredString(this.fontRendererObj, "3. Press the button below to continue.", this.width / 2, this.height / 2 + 55, 0xFFFFFF);

		hasOpened = hasOpened <= 0 ? 0 : hasOpened - 1;
		
		super.drawScreen(par1, par2, par3);
	}

	@Override
	public void actionPerformed(GuiButton button)
	{
		switch (button.id)
		{
		case 11:
			try
			{
				DesktopApi.browse(new URI(mod.url));
			}
			catch (Exception e1)
			{
				ModpackTweaks.logger.error("Failed to reach " + mod.name + "'s download page!");
				e1.printStackTrace();
			}
			break;

		case 12:
			if (hasOpened == 0)
			{
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				@SuppressWarnings("serial")
				JFileChooser fc = new JFileChooser()
				{
					@Override
					protected JDialog createDialog(Component parent) throws HeadlessException
					{
						// intercept the dialog created by JFileChooser
						JDialog dialog = super.createDialog(parent);
						dialog.setModal(true);
						dialog.setAlwaysOnTop(true);
						return dialog;
					}
				};
				
				fc.setFileFilter(new FileFilter()
				{	
					@Override
					public String getDescription()
					{
						return "*.jar, *.zip";
					}
					
					@Override
					public boolean accept(File arg0)
					{
						return (arg0.getName().endsWith(".zip") || arg0.getName().endsWith(".jar") || arg0.isDirectory());
					}
				});
				
				fc.setCurrentDirectory(lastDir);
				fc.showOpenDialog(new JFrame());
				File mod = fc.getSelectedFile();
				lastDir = mod == null ? lastDir : mod.getParentFile();
				try
				{
					if (mod != null)
						FileUtils.copyFile(mod, new File(Reference.getModsFolder().getCanonicalPath() + "/" + mod.getName()));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				hasOpened = 50;
			}
			break;

		default:
			hasOpened = 0;
			GuiHelper.updateGui.actionPerformed(button);
		}

	}

}
