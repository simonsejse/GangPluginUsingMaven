package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.enums.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LeaveArgument implements CommandArguments
{
    private MainPlugin plugin;

    public LeaveArgument(MainPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getAlias()
    {
        return "l";
    }

    @Override
    public String getArgument()
    {
        return "leave";
    }

    @Override
    public String usage()
    {
        return "/bande leave";
    }

    @Override
    public void perform(Player p, String... args)
    {
        UUID playerUuid = p.getUniqueId();
        if (GangManaging.playerInGangPredicate.test(playerUuid)){
            if (GangManaging.rankFunction.apply(playerUuid) != Rank.LEADER.getValue()){
                Gang gang = GangManaging.getGangByUuidFunction.apply(playerUuid);
                gang.removeMember(playerUuid);
                p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().SUCCESSFULLY_LEFT_GANG.replace("{name}", gang.getGangName())));

                gang.getMembersSorted().keySet()
                        .stream()
                        .filter(member -> Bukkit.getPlayer(member) != null)
                        .map(Bukkit::getPlayer)
                        .forEach(member -> member.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().MEMBER_LEFT_GANG.replace("{oldmember}", p.getName()))));

            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PASS_OWNERSHIP));
        }else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
    }
}
