package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.inventorymanaging.menus.rankmenu.RankMenu;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RankArgument implements CommandArguments
{
    private final MainPlugin plugin;
    private final MessageProvider mp;
    private final GangManaging gangManaging;

    public RankArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
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
        UUID playerUUID = p.getUniqueId();

        if (!args[1].equalsIgnoreCase(p.getName())){
            if (gangManaging.playerInGangPredicate.test(playerUUID))
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                if (offlinePlayer.hasPlayedBefore())
                {
                    UUID otherPlayerUUID = offlinePlayer.getUniqueId();
                    if(gangManaging.playerInGangPredicate.test(otherPlayerUUID)){
                        Gang gang = this.gangManaging.getGangByUuidFunction.apply(playerUUID);
                        Gang otherGang = this.gangManaging.getGangByUuidFunction.apply(otherPlayerUUID);
                        if (gang.equals(otherGang))
                        {
                            p.openInventory(new RankMenu(this.gangManaging, plugin, gang, p.getUniqueId(), p.getName(), otherPlayerUUID, args[1]).getInventory());
                        } else p.sendMessage(this.mp.notInSameGang);
                    }else p.sendMessage(this.mp.playerNotInGang);
                } else p.sendMessage(this.mp.hasNeverPlayed);
            } else p.sendMessage(this.mp.notInGang);
        }else p.sendMessage(this.mp.cantRankYourself);
    }
}
