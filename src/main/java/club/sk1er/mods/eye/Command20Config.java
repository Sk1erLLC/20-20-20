package club.sk1er.mods.eye;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class Command20Config extends CommandBase {
    private TwentyTwentyTwentyMod twentyTwentyTwentyMod;

    public Command20Config(TwentyTwentyTwentyMod twentyTwentyTwentyMod) {
        this.twentyTwentyTwentyMod = twentyTwentyTwentyMod;
    }

    @Override
    public String getName() {
        return "20";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/20";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        new ConfigGui(twentyTwentyTwentyMod).show();
    }
}