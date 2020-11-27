package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.exceptions.PlayerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KickArgument implements CommandArguments
{
    private MainPlugin plugin;
    private final GangManaging gangManaging;

    public KickArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
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

        UUID playerUuid = p.getUniqueId();
        if (p.getName().toLowerCase().equals(args[1].trim()))
        {
            p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_KICK_HIMSELF));
            return;
        }
        if (gangManaging.playerInGangPredicate.test(playerUuid))
        {
            if (gangManaging.isRankMinimumPredicate.test(playerUuid, gangManaging.getGangByUuidFunction.apply(playerUuid).getGangPermissions().accessToKick))
            {
                try
                {
                    UUID playerKickedUuid = Bukkit.getOfflinePlayer(args[1].trim()).getUniqueId();
                    if (gangManaging.playerInGangPredicate.test(playerKickedUuid))
                    {
                        if (gangManaging.playersInSameGangPredicate.test(playerUuid, playerKickedUuid))
                        {
                            if (gangManaging.rankFunction.apply(playerKickedUuid) < gangManaging.rankFunction.apply(playerUuid)){
                                String gangName = gangManaging.gangNameFunction.apply(playerKickedUuid);
                                gangManaging.kickConsumer.accept(playerKickedUuid);
                                //Check if kicked player is online, only send message if online.
                                p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_WAS_KICKED.replace("{args}", args[1])));
                                if (Bukkit.getPlayer(playerKickedUuid) != null)
                                    Bukkit.getPlayer(playerKickedUuid).sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().MEMBER_KICKED.replace("{name}", gangName)));
                            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().KICK_HIGHER_RANK));
                        } else
                            p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_SAME_GANG));
                    } else
                        p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_NOT_IN_GANG));
                } catch (Exception e)
                {
                    throw new PlayerNotFoundException("Spilleren findes ikke.");
                }
            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
    }
}
