package modpacktweaks.network.message;

import modpacktweaks.client.gui.GuiHelper;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageShowDownloadGUI implements IMessage, IMessageHandler<MessageShowDownloadGUI, IMessage>
{
    public MessageShowDownloadGUI() {}
    
    @Override
    public void fromBytes(ByteBuf buf)
    {}

    @Override
    public void toBytes(ByteBuf buf)
    {}
    
    @Override
    public IMessage onMessage(MessageShowDownloadGUI message, MessageContext ctx)
    {
        GuiHelper.doDownloaderGUI();
        return null;
    }
}
