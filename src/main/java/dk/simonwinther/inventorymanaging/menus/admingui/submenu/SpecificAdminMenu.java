package dk.simonwinther.inventorymanaging.menus.admingui.submenu;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.admingui.PaginatedAdminMenu;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class SpecificAdminMenu extends Menu
{

    private String gangName;
    private final PaginatedAdminMenu paginatedAdminMenu;
    private GangManaging gangManaging;

    public SpecificAdminMenu(GangManaging gangManaging, String gangName, final PaginatedAdminMenu paginatedAdminMenu){
        this.gangName = gangName;
        this.paginatedAdminMenu = paginatedAdminMenu;
        this.gangManaging = gangManaging;
    }


    @Override
    protected String getName()
    {
        return "";
    }

    @Override
    protected int getSize()
    {
        return 9*6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
      if (slot == InventoryUtility.BACK_SLOT) whoClicked.openInventory(paginatedAdminMenu.getInventory());

    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_SIX, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_GLASS.index]), true);
        super.setItem(10, new ItemBuilder(Material.ENDER_CHEST).setItemName("&c&lSlet bande").buildItem());
        return super.inventory;
    }

    private final Supplier<Gang> gangSupplier = () -> this.gangManaging.getGangByNameFunction.apply(gangName);
}
