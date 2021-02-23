package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InviteArgument implements CommandArguments
{
    private final MainPlugin plugin;
    private final MessageProvider mp;
    private final GangManaging gangManaging;

    public InviteArgument(GangManaging gangManaging, MainPlugin plugin)
    {
        this.gangManaging = gangManaging;
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
    }

    @Override
    public String getAlias()
    {
        return "inv";
    }

    @Override
    public String getArgument()
    {
        return "invite";
    }

    @Override
    public String usage()
    {
        return "/bande invite <player>";
    }

    @Override
    public void perform(Player p, String... args)
    {
        if (this.gangManaging.playerInGangPredicate.test(p.getUniqueId())) {
            this.gangManaging.requestPlayerToJoinGang(
                    this.gangManaging.getGangByUuidFunction.apply(p.getUniqueId()),
                    p,
                    args[1]
            );
        } else p.sendMessage(this.mp.notInGang);
    }
}
