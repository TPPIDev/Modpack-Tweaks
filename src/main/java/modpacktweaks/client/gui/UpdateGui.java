package modpacktweaks.client.gui;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.LayoutStyle.ComponentPlacement;

import modpacktweaks.config.ConfigurationHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

import java.awt.Color;

@SideOnly(Side.CLIENT)
public class UpdateGui extends JFrame implements Runnable
{
    private Box box;
    
    public UpdateGui()
    {
        getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
        
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        
        box = Box.createVerticalBox();
        box.setBorder(new BevelBorder(BevelBorder.RAISED, Color.GRAY, Color.LIGHT_GRAY, null, null));
        
        JButton btnNewButton = new JButton("New button");
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addComponent(box, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_panel.createSequentialGroup()
                            .addGap(20)
                            .addComponent(btnNewButton)))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(textArea, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addComponent(box, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                    .addComponent(btnNewButton)
                    .addGap(1))
                .addComponent(textArea, GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
        );
        panel.setLayout(gl_panel);
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private static final long serialVersionUID = 3419109419471048238L;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static List<ModDownload> mods = new ArrayList<ModDownload>();

    private Iterator<ModDownload> iterator;

    public static void addModDownload(ModDownload mod)
    {
        mods.add(mod);
    }

    //
    // @SuppressWarnings("unchecked")
    // @Override
    // public void initGui()
    // {
    // if (noShow)
    // {
    // ModpackTweaks.logger.info("not opening GUI");
    // this.mc.displayGuiScreen(this.parentScreen);
    // return;
    // }
    //
    // // Unsure exactly what this does but...it seems necessary
    // Keyboard.enableRepeatEvents(true);
    //
    // this.buttonList.clear();
    //
    // this.buttonList.add(new GuiButton(-1, this.width / 2 - 150, this.height / 2 + 30, 300, 20, "Continue"));
    // this.buttonList.add(new GuiButton(11, this.width / 2 - 150, this.height / 2 + 65, 300, 20, "Skip the downloads completely"));
    //
    // ConfigurationHandler.shouldLoadGUI = false;
    // }
    //
    // @Override
    // public void onGuiClosed()
    // {
    // Keyboard.enableRepeatEvents(false);
    // }
    //
    // @Override
    // protected void actionPerformed(GuiButton button)
    // {
    // if (button.enabled)
    // {
    // if (button.id == 11)
    // this.mc.displayGuiScreen(this.parentScreen);
    // else
    // {
    // try
    // {
    // if (iterator.hasNext())
    // {
    // this.mc.displayGuiScreen(iterator.next());
    // }
    // /*
    // else if (configGui != null)
    // {
    // File file=new File("config/modpackTweaks/config/hardconfig.zip");
    // if (file.exists()) this.mc.displayGuiScreen(configGui);
    // configGui = null;
    // }
    // */
    // else if (modScreens.size() > 0)
    // {
    // this.mc.displayGuiScreen(new RestartGui());
    // }
    // else
    // {
    // this.mc.displayGuiScreen(this.parentScreen);
    // }
    // }
    // catch (Exception e)
    // {
    // ModpackTweaks.logger.error("Error opening webpage, please contact your modpack author.");
    // e.printStackTrace();
    // }
    // }
    // }
    // }
    //
    // @Override
    // public void drawScreen(int par1, int par2, float par3)
    // {
    // drawScreen(par1, par2, par3, true);
    // }
    //
    // public void drawScreen(int par1, int par2, float par3, boolean draw)
    // {
    // if (draw)
    // {
    // this.drawDefaultBackground();
    //
    // if (firstTime)
    // {
    // this.drawCenteredString(this.fontRendererObj, "Hey there! This seems like the first time you are starting " + ConfigurationHandler.packName + ". Welcome!", this.width / 2, this.height / 2 -
    // 100, 0xFFFFFF);
    // this.drawCenteredString(this.fontRendererObj, "This menu will not show again unless enabled in the ModpackTweaks config.", this.width / 2, this.height / 2 - 10, 0xFFFFFF);
    // this.drawCenteredString(this.fontRendererObj, "Alternatively, you may use the command \"/" + ConfigurationHandler.packAcronym + " download\" to show it in-game.", this.width / 2, this.height /
    // 2, 0xFFFFFF);
    // }
    //
    // this.drawCenteredString(this.fontRendererObj, "As it turns out, there are some mods we really wanted to include,", this.width / 2, this.height / 2 - 80, 0xFFFFFF);
    // this.drawCenteredString(this.fontRendererObj, "but couldn't ship directly with the rest of the pack.", this.width / 2, this.height / 2 - 70, 0xFFFFFF);
    // this.drawCenteredString(this.fontRendererObj, "Though we had to leave them out, you may use this little utility to", this.width / 2, this.height / 2 - 50, 0xFFFFFF);
    // this.drawCenteredString(this.fontRendererObj, "help add them manually, to gain what we feel is the full experience.", this.width / 2, this.height / 2 - 40, 0xFFFFFF);
    // }
    //
    // super.drawScreen(par1, par2, par3);
    // }

    public static void launch()
    {
        UpdateGui gui = new UpdateGui();
        JsonArray arr = ConfigurationHandler.loadClientsideJson();

        for (int i = 0; i < arr.size(); i++)
        {
            addModDownload(gson.fromJson(arr.get(i), ModDownload.class));
        }

        gui.run();
    }

    @Override
    public void run()
    {
        iterator = mods.iterator();

        if (mods.isEmpty())
            return;
        
        for (ModDownload m : mods)
        {
            box.add(new JCheckBox(m.name));
        }
        
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.pack();
        this.setVisible(true);

        while (this.isDisplayable())
        {
            // lol
        }
    }
}
