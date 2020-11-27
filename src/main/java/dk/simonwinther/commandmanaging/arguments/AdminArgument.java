package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.inventorymanaging.menus.admingui.PaginatedAdminMenu;
import dk.simonwinther.utility.GangManaging;
import org.bukkit.entity.Player;

public class AdminArgument implements CommandArguments
{
    private MainPlugin plugin;
    private final GangManaging gangManaging;

    public AdminArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
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
        //TODO: Later version create ADMIN Gui
        if (p.isOp()){
            p.openInventory(new PaginatedAdminMenu(this.gangManaging, 0).getInventory());
        }else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
    }
}
