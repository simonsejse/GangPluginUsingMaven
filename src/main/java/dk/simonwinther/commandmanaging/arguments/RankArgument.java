package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.inventorymanaging.menus.rankmenu.RankMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RankArgument implements CommandArguments
{
    private MainPlugin plugin;
    private GangManaging gangManaging;

    public RankArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
    }

    @Override
    public String getAlias()
    {
        return "r";
    }

    @Override
    public String getArgument()
    {
        return "rank";
    }

    @Override
    public String usage()
    {
        return "/bande rank <player>";
    }

    @Deprecated
    @Override
    public void perform(Player p, String... args)
    {
        UUID playerUuid = p.getUniqueId();
        //Nested if-statements
        if (!args[1].equalsIgnoreCase(p.getName())){
            if (gangManaging.playerInGangPredicate.test(playerUuid))
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                if (offlinePlayer.hasPlayedBefore())
                {
                    if (gangManaging.playersInSameGangPredicate.test(p.getUniqueId(), offlinePlayer.getUniqueId()))
                    {
                        Gang tempGang = gangManaging.getGangByUuidFunction.apply(playerUuid);
                        UUID argsUuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
                        p.openInventory(new RankMenu(this.gangManaging, plugin, tempGang, p.getUniqueId(), p.getName(), argsUuid, args[1]).getInventory());
                    } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_SAME_GANG));
                } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().HAS_NEVER_PLAYED));
            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
        }else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CANT_RANK_YOURSELF));
    }
}
