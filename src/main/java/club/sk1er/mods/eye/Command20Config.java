package club.sk1er.mods.eye;

import club.sk1er.mods.core.ModCore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class Command20Config extends CommandBase {

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "20";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/20";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ModCore.getInstance().getGuiHandler().open(TwentyTwentyTwentyMod.instance.getConfig().gui());
    }
}
