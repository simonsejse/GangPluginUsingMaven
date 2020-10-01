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

    public InfoArgument(MainPlugin plugin){
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
        if (!GangManaging.playerInGangPredicate.test(playerUuid))
        {
            p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
            return;
        }
        Gang gang = GangManaging.getGangByUuidFunction.apply(playerUuid);

        p.openInventory(new InfoMenu(plugin, gang, true).getInventory());
    }
}
