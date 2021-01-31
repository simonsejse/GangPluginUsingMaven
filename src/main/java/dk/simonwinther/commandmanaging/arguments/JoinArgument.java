package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JoinArgument implements CommandArguments
{
    private MainPlugin plugin;
    private final GangManaging gangManaging;

    public JoinArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
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
                        p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().SUCCESSFULLY_JOINED_GANG.replace("{name}", args[1])));
                        //TODO: Check if filter works.
                        Bukkit.getOnlinePlayers()
                                .stream()
                                .filter(globalPlayer -> !p.getName().equalsIgnoreCase(globalPlayer.getName()))
                                .forEach(globalPlayer ->
                                        globalPlayer.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().SUCCESSFULLY_JOINED_GANG_GLOBAL.replace("{spiller}", p.getName()).replace("{name}", args[1]))));

                    } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().GANG_NOT_SPACE_ENOUGH));
                } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_INVITED_TO_GANG));
            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().GANG_DOES_NOT_EXISTS.replace("{name}", args[1])));
        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALREADY_IN_GANG));
    }


}
