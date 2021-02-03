package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.enums.Rank;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LeaveArgument implements CommandArguments
{

    private final MessageProvider mp;
    private final GangManaging gangManaging;

    public LeaveArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.mp = plugin.getMessageProvider();
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
        if (gangManaging.playerInGangPredicate.test(playerUuid)){
            if (gangManaging.rankFunction.apply(playerUuid) != Rank.LEADER.getValue()){
                Gang gang = gangManaging.getGangByUuidFunction.apply(playerUuid);
                gangManaging.kick.accept(gang, playerUuid);

                p.sendMessage(this.mp.successfullyLeftGang.replace("{name}", gang.getGangName()));

                this.gangManaging.sendTeamMessage.accept(gang, this.mp.memberLeftGang.replace("{oldmember}", p.getName()));

            } else p.sendMessage(this.mp.passOwnership);
        }else p.sendMessage(this.mp.notInGang);
    }
}
