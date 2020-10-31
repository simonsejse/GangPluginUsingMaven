package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InfoArgument implements CommandArguments
{
    private MainPlugin plugin;
    private GangManaging gangManaging;

    public InfoArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
    }

    @Override
    public String getAlias()
    {
        return "i";
    }

    @Override
    public String getArgument()
    {
        return "info";
    }

    @Override
    public String usage()
    {
        return "Forkert brug: /bande info";
    }

    @Override
    public void perform(Player p, String... args)
    {
        if (args.length > 1) return;
        UUID playerUuid = p.getUniqueId();
        if (!gangManaging.playerInGangPredicate.test(playerUuid))
        {
            p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
            return;
        }
        Gang gang = gangManaging.getGangByUuidFunction.apply(playerUuid);

        p.openInventory(new InfoMenu(gangManaging, plugin, gang, true).getInventory());
    }
}
