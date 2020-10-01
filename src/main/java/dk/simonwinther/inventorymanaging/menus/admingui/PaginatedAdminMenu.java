package dk.simonwinther.inventorymanaging.menus.admingui;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.admingui.submenu.SpecificAdminMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PaginatedAdminMenu extends Menu
{

    private int page;
    private final int PAGE_COUNT = 52;

    public PaginatedAdminMenu(int page)
    {
        super();
        this.page = page;
    }



    @Override
    protected String getName()
    {
        return "Vælg bande";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        if (item.getType() == Material.BARRIER) return;
        if (slot == 52 && page <= 0) return;
        if (slot == 53) whoClicked.openInventory(new PaginatedAdminMenu(++page).getInventory());
        else if (slot == 52) whoClicked.openInventory(new PaginatedAdminMenu(--page).getInventory());
        else{
            String gangName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            if (GangManaging.gangExistsPredicate.test(gangName))
            {
                whoClicked.openInventory(new SpecificAdminMenu(gangName, this).getInventory());
            }
        }
    }

    @Override
    public Inventory getInventory()
    {
        final List<Gang> gangList = new ArrayList<>(GangManaging.getGangMap().values());
        //Page 0 -> 0*52 -> 0
        //page 1 -> 1*52 -> 52
        for (int slot = 0, index = page * PAGE_COUNT; slot < PAGE_COUNT && index < gangList.size(); slot++)
        {
            Gang gang = gangList.get(index++);
            super.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER)
                    .setPlayerSkull(gang.getOwnerName()).setItemName("&a" + gang.getGangName()).buildItem());
        }


        super.setItem(52, page == 0 ? new ItemBuilder(Material.BARRIER).setItemName("&c&lTilbage!").setLore("&fDu kan ikke gå flere sider tilbage!").buildItem() : new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index])).setItemName("&cTilbage").setLore("&fKlik for, at gå tilbage til side &c"+page).buildItem());
        super.setItem(53, ((page == 0 ? 1 : Integer.sum(page, 1)) * PAGE_COUNT) >= gangList.size() ? new ItemBuilder(Material.BARRIER).setItemName("&a&lFrem").setLore("&fDu kan ikke gå flere sider frem!").buildItem() :  new ItemBuilder(Material.ARROW).setItemName("&aNæste side").setLore("&fKlik for, at gå til side &c" + Integer.sum(page, 1)).buildItem());
        return super.inventory;
    }
}
