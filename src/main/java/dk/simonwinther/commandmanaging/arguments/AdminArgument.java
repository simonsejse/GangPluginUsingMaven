package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.inventorymanaging.menus.admingui.PaginatedAdminMenu;
import org.bukkit.entity.Player;

public class AdminArgument implements CommandArguments
{
    private MainPlugin plugin;

    public AdminArgument(MainPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getAlias()
    {
        return "adm";
    }

    @Override
    public String getArgument()
    {
        return "admin";
    }

    @Override
    public String usage()
    {
        return "/bande admin/adm";
    }

    @Override
    public void perform(Player p, String... args)
    {
        if (p.isOp()){
            p.openInventory(new PaginatedAdminMenu(0).getInventory());
        }else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
    }
}
