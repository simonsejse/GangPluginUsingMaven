package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.Gang;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.MessageProvider;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KickArgument implements CommandArguments
{
    private MessageProvider mp;
    private final GangManaging gangManaging;

    public KickArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.mp = plugin.getMessageProvider();
    }

    @Override
    public String getAlias()
    {
        return "k";
    }

    @Override
    public String getArgument()
    {
        return "kick";
    }


    @Override
    public String usage()
    {
        return "/bande kick <player>";
    }

    @Override
    public void perform(Player p, String... args) throws Exception
    {

        UUID playerUUID = p.getUniqueId();
        if (p.getName().toLowerCase().equals(args[1].trim()))
        {
            p.sendMessage(mp.playerKickHimself);
            return;
        }
        if (gangManaging.playerInGangPredicate.test(playerUUID))
        {
            if (gangManaging.isRankMinimumPredicate.test(playerUUID, gangManaging.getGangByUuidFunction.apply(playerUUID).gangPermissions.accessToKick))
            {
                try
                {
                    UUID playerKickedUuid = Bukkit.getOfflinePlayer(args[1].trim()).getUniqueId();
                    if (gangManaging.playerInGangPredicate.test(playerKickedUuid))
                    {
                        Gang gang = this.gangManaging.getGangByUuidFunction.apply(playerUUID);
                        Gang otherGang = this.gangManaging.getGangByUuidFunction.apply(playerKickedUuid);

                        if (gang.equals(otherGang))
                        {
                            if (gangManaging.rankFunction.apply(playerKickedUuid) < gangManaging.rankFunction.apply(playerUUID)){

                                gangManaging.kickConsumer.accept(playerKickedUuid);
                                //Check if kicked player is online, only send message if online.
                                p.sendMessage(this.mp.playerWasKicked.replace("{args}", args[1]));
                                if (Bukkit.getPlayer(playerKickedUuid) != null)
                                    Bukkit.getPlayer(playerKickedUuid).sendMessage(this.mp.memberKicked.replace("{name}", gang.getGangName()));
                            } else p.sendMessage(this.mp.kickHigherRank);
                        } else p.sendMessage(this.mp.notInSameGang);
                    } else p.sendMessage(this.mp.playerNotInGang);
                } catch (Exception e)
                {
                    //TODO: fjern det lort her
                    p.sendMessage("Spilleren findes ikke.");
                }
            } else p.sendMessage(this.mp.notHighRankEnough);

        } else p.sendMessage(this.mp.notInGang);

    }
}
