package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.entity.Player;

public class SpawnBotArgument implements CommandArguments
{

    @Override
    public String getAlias()
    {
        return "snpc";
    }

    @Override
    public String getArgument()
    {
        return "spawnnpc";
    }

    @Override
    public String usage()
    {
        return "/bande spawnnpc";
    }

    @Override
    public void perform(Player p, String... args)
    {
        //   GangManaging.getGangByUuidFunction.apply(p.getUniqueId()).getLevelSystem().setGangLevel(Integer.parseInt(args[1]));



    }
}
