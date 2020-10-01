package dk.simonwinther.commandmanaging;

import org.bukkit.entity.Player;

public interface CommandArguments
{

    String getAlias();

    String getArgument();

    String usage();

    void perform(Player p, String... args) throws Exception;

}
