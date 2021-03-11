package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JoinArgument implements CommandArguments
{
    private MainPlugin plugin;
    private MessageProvider mp;

    private final GangManaging gangManaging;

    public JoinArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
    }

    @Override
    public String getAlias()
    {
        return "j";
    }

    @Override
    public String getArgument()
    {
        return "join";
    }

    @Override
    public String usage()
    {
        return "/bande join <bandenavn>";
    }

    @Override
    public void perform(Player p, String... args)
    {
        //Player is not in gang
        //Gang exists.
        //Player is invited to current gang he's trying to join
        if (!(gangManaging.playerInGangPredicate.test(p.getUniqueId())))
        {
            if (gangManaging.gangExistsPredicate.test(args[1]))
            {
                if (gangManaging.alreadyInvitedByGangNamePredicate.test(args[1], p.getName()))
                {
                    Gang gang = gangManaging.getGangByNameFunction.apply(args[1]);
                    if (gang.getMembersSorted().size() < gang.getMaxMembers())
                    {
                        gangManaging.joinGang(p.getUniqueId(), p.getName(), args[1]);
                        p.sendMessage(this.mp.successfullyJoinedGang.replace("{name}", args[1]));
                        //TODO: Check if filter works.
                        Bukkit.getOnlinePlayers()
                                .stream()
                                .filter(globalPlayer -> !p.getName().equalsIgnoreCase(globalPlayer.getName()))
                                .forEach(globalPlayer ->
                                        globalPlayer.sendMessage(this.mp.successfullyJoinedGangGlobal.replace("{spiller}", p.getName()).replace("{name}", args[1])));

                    } else p.sendMessage(this.mp.gangNotSpaceEnough);
                } else p.sendMessage(this.mp.notInvitedToGang);
            } else p.sendMessage(this.mp.gangDoesNotExists.replace("{name}", args[1]));
        } else p.sendMessage(this.mp.alreadyInGang);
    }


}
