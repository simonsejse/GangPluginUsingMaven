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
                                    p.sendMessage(this.mp.playerWasUninvited.replace("{args}", args[1]));
                                    return;
                                }
                                if (Bukkit.getPlayer(args[1]) != null){
                                    Bukkit.getPlayer(args[1]).sendMessage(this.mp.invitedToGang.replace("{name}", gangManaging.gangNameFunction.apply(playerUuid)).replace("{player}", p.getName()));
                                }
                                //Invite player to gang
                                gangManaging.addInvitationConsumer.accept(playerUuid, args[1]);
                                p.sendMessage(this.mp.playerWasInvited.replace("{args}", args[1]));


                            } else p.sendMessage(this.mp.gangNotSpaceEnough);
                        } else p.sendMessage(this.mp.notHighRankEnough);
                    } else p.sendMessage(this.mp.sameGang);
                } else p.sendMessage(this.mp.hasNeverPlayed.replace("{args}", args[1]));
            } else p.sendMessage(this.mp.notInGang);
        } else p.sendMessage(this.mp.cantInviteYourself);
    }
}
