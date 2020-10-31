package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InviteArgument implements CommandArguments
{
    private MainPlugin plugin;
    private GangManaging gangManaging;

    public InviteArgument(GangManaging gangManaging, MainPlugin plugin)
    {
        this.gangManaging = gangManaging;
        this.plugin = plugin;
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
        UUID playerUuid = p.getUniqueId();
        if (!args[1].equalsIgnoreCase(p.getName()))
        {
            if (gangManaging.playerInGangPredicate.test(playerUuid))
            {
                if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore())
                {
                    //Check if player and invitedPlayer is in the game gang:
                    UUID invitedPlayerUuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
                    if (gangManaging.getGangByUuidFunction.apply(invitedPlayerUuid) == null || !gangManaging.playersInSameGangPredicate.test(playerUuid, invitedPlayerUuid))
                    {
                        if (gangManaging.isRankMinimumPredicate.test(playerUuid, gangManaging.getGangByUuidFunction.apply(playerUuid).gangPermissions.accessToInvite))
                        {
                            if (gangManaging.hasMemberSpacePredicate.test(playerUuid))
                            {
                                if (gangManaging.alreadyInvitedByUuidPredicate.test(playerUuid, args[1]))
                                {
                                    //Remove invitation
                                    gangManaging.removeInvitationConsumer.accept(playerUuid, args[1]);
                                    p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_WAS_UNINVITED.replace("{args}", args[1])));
                                    return;
                                }
                                if (Bukkit.getPlayer(args[1]) != null){
                                    Bukkit.getPlayer(args[1]).sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().INVITED_TO_GANG.replace("{name}", gangManaging.gangNameFunction.apply(playerUuid)).replace("{player}", p.getName())));
                                }
                                //Invite player to gang
                                gangManaging.addInvitationConsumer.accept(playerUuid, args[1]);
                                p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_WAS_INVITED.replace("{args}", args[1])));


                            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().GANG_NOT_SPACE_ENOUGH));
                        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
                    } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().SAME_GANG));
                } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().HAS_NEVER_PLAYED.replace("{args}", args[1])));
            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CANT_INVITE_YOURSELF));
    }
}
