package modpacktweaks.client.gui.modGuides;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import modpacktweaks.client.gui.library.gui.GuiBase;
import modpacktweaks.client.gui.library.gui.button.GuideButton;
import modpacktweaks.client.gui.library.gui.element.ElementBase;
import modpacktweaks.client.gui.library.gui.element.ElementScrollBar;
import modpacktweaks.client.gui.library.gui.element.ElementScrollPanel;
import modpacktweaks.client.gui.library.gui.element.ElementText;
import modpacktweaks.client.gui.library.gui.element.ElementTextClickable;
import modpacktweaks.command.CommandMT;
import modpacktweaks.config.ConfigurationHandler;
import modpacktweaks.util.FileLoader;
import modpacktweaks.util.TxtParser;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiGuideBase extends GuiBase
{
	protected static Map<String, GuiMod> mods = new TreeMap<String, GuiMod>();

	protected String title, body;

	protected static final int LENGTH = 180;

	protected GuideButton homeButton;
	
	public GuiGuideBase()
	{
		super(new ResourceLocation("modpacktweaks", ConfigurationHandler.guideSkin == 0 ? "textures/gui/guiGuide.png" : "textures/gui/guiGuide_alt.png"));
		this.xSize = 256;
		this.ySize = 178;

		setDefaultText(true);
	}

	public static void initMap()
	{
		for (String s : TxtParser.getSupportedMods(FileLoader.getSupportedModsFile()))
		{
			ArrayList<String> pages = TxtParser.parseFileMods(FileLoader.getSupportedModsFile(), s);
			String text = "";
			for (String page : pages)
			{
				page = page.replace("~", " ");
				page = page.replace("  ", " ");
				page = page.replace("  ", " ");
				text += page + " ";
			}
			mods.put(s, new GuiMod(CommandMT.getProperName(s), text, s));
		}
	}

	@Override
	public void initGui()
	{
		super.initGui();
		initButtons();
		initPanel();
	}

	// Initializes the panel by first clearing all panels/scrollbars from the
	// element list and then re-adding with updated text
	protected void initPanel()
	{
		ElementScrollPanel panel = new ElementScrollPanel(this, (this.xSize / 6) - 3, (this.ySize / 9) + 5, this.xSize - 75, (int) (this.ySize / 1.35) + 14);
		List<String> lines = getLines();

		int length = 0;
		for (int i = 0; i < lines.size(); i++)
		{
			panel.addElement(new ElementText(this, 0, 3 + (length * 10), lines.get(i), null, 0x282828, false));
			length++;

		}

		ArrayList<ElementBase> elementsNew = new ArrayList<ElementBase>();

		for (ElementBase e : elements)
		{
			if (!(e instanceof ElementScrollBar || e instanceof ElementScrollPanel))
				elementsNew.add(e);
		}

		this.elements = elementsNew;

		this.addElement(panel);
		this.addElement(new ElementScrollBar(this, 217, 15, 6, 139, panel));
	}

	@Override
	public void drawGuiBackgroundLayer(float f, int mouseX, int mouseY)
	{
	    super.drawGuiBackgroundLayer(f, mouseX, mouseY);
	    
	    if (texture != null) // Draw these parts of the texture again, so they overlap a portion of the scroll panel, allowing for smooth-looking scrolling
	    {
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            getMinecraft().renderEngine.bindTexture(texture);
            drawTexturedModalRect(guiLeft + 35, guiTop, 35, 0, this.xSize - 75, 14);
            drawTexturedModalRect(guiLeft + 35, guiTop + ySize - 24, 35, ySize - 24, this.xSize - 75, 14);
            mc.fontRenderer.drawString(ConfigurationHandler.packName, guiLeft + xSize / 2 - mc.fontRenderer.getStringWidth(ConfigurationHandler.packName) / 2, guiTop + 3, 0xFFFFFF);
	    }
	    
	    homeButton.draw(); // Make sure this button is on top of the overlay
	}
	
	private void initPanel(List<String> lines, List<String> modids)
	{
	    ElementScrollPanel panel = new ElementScrollPanel(this, (this.xSize / 6) - 3, (this.ySize / 9) + 5, this.xSize - 75, (int) (this.ySize / 1.35) + 10);

		for (int i = 0; i < lines.size(); i++)
		{
			panel.addElement(new ElementTextClickable(this, 0, 3 + i * 10, lines.get(i), null, 0x282828, modids.get(i)));
		}
		
		panel.addElement(new ElementText(this, 0, 3 + (lines.size() * 10), "", null)); // Adding an extra element because of the overlays & positions of the first element, stops the last element from being half visible when fully scrolled

		ArrayList<ElementBase> elementsNew = new ArrayList<ElementBase>();

		for (ElementBase e : elements)
		{
			if (!(e instanceof ElementScrollBar || e instanceof ElementScrollPanel))
				elementsNew.add(e);
		}

		this.elements = elementsNew;

		this.addElement(panel);
		this.addElement(new ElementScrollBar(this, 217, 15, 6, 139, panel));
	}

	// Splits the body text into correctly sized lines based on the LENGTH
	// constant
	private List<String> getLines()
	{
		List<String> lines = new ArrayList<String>();
		lines.add(title);
		lines.add("");
		if (body.startsWith("<"))
		{
			body = "No information for this mod yet! " + "Use /" + ConfigurationHandler.packAcronym + " mods [modname] to get a link to a helpful webpage for this mod, " + "or contribute some documentation on the github!";
		}
		FontRenderer render = this.mc.fontRenderer;
		String[] paragraphs = body.split("\n");
		List<String[]> words = new ArrayList<String[]>();
		for (String ss : paragraphs)
		{
			words.add(ss.split(" "));
		}
		String currentLine = "";
		for (String[] ss : words)
		{
			for (String s : ss)
			{
				if (s.endsWith(" "))
				{
					s = s.substring(0, s.length() - 1);
				}
				if (render.getStringWidth(currentLine + s + " ") < this.xSize - (this.xSize - LENGTH))
					currentLine += s + " ";
				else
				{
					lines.add(currentLine);
					currentLine = s + " ";
				}
			}
			lines.add(currentLine);
			currentLine = "";
			lines.add("");
		}
		return lines;
	}

	private void initButtons()
	{
		this.addElement(new GuideButton(this, 0, 2, 20));
		this.addElement(new GuideButton(this, 1, 2, 50));
		this.addElement(new GuideButton(this, 2, 2, 80));
		this.addElement(new GuideButton(this, 3, 2, 110));
		this.addElement(new GuideButton(this, 4, 228, 20));
		this.addElement(new GuideButton(this, 5, 228, 50));
		this.addElement(new GuideButton(this, 6, 228, 80));
		this.addElement(new GuideButton(this, 7, 228, 110));
		homeButton = new GuideButton(this, 8, 114, 157);
		this.addElement(homeButton);
	}

	// There should be @Override here but people shadowing OUT OF DATE NEI INTERFACES screws up the build. I blame greg.
	public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h)
	{
		return true;
	}

	// Sets the body/title and reinitializs the text
	public void modifyGui(int buttonID)
	{
		if (buttonID <= 7)

			this.body = "This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. This is a test. ";
		this.initPanel();
	}

	public void displayModInfo(String modid)
	{
		this.title = mods.get(modid).title;
		this.body = mods.get(modid).body;
		initPanel();
	}

	public void showModRange(char start, char end)
	{
		List<String> lines = new ArrayList<String>(), modids = new ArrayList<String>();

		for (GuiMod m : mods.values())
		{
			if (m.modid.toLowerCase().charAt(0) >= start && m.modid.toLowerCase().charAt(0) <= end)
			{
				lines.add(m.title);
				modids.add(m.modid);
			}
		}

		initPanel(lines, modids);

		mods.keySet();
	}

	public void setDefaultText(boolean startup)
	{
		title = "Main menu";
		body = ConfigurationHandler.guideHomeText;
		if (!startup)
			initPanel();
	}

    //	// No NEI, too crowded
//	@Override
//	public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility)
//	{
//		currentVisibility.showNEI = false;
//		return currentVisibility;
//	}

//	@Override
//	public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui)
//	{
//		return null;
//	}
//
//	@Override
//	public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button)
//	{
//		return false;
//	}

//    @Override
//    public Iterable<Integer> getItemSpawnSlots(GuiContainer arg0, ItemStack arg1)
//    {
//        return null;
//    }
}