package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InfoArgument implements CommandArguments
{
    private MainPlugin plugin;
    private MessageProvider mp;
    private final GangManaging gangManaging;

    public InfoArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
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
        UUID playerUUID = p.getUniqueId();
        if (!gangManaging.playerInGangPredicate.test(playerUUID))
        {
            p.sendMessage(this.mp.notInGang);
            return;
        }
        Gang gang = gangManaging.getGangByUuidFunction.apply(playerUUID);

        p.openInventory(new InfoMenu(plugin, gang, true, null).getInventory());
    }
}
