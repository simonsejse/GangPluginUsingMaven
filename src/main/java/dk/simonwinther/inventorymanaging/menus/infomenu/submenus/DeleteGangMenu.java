package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.constants.Rank;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DeleteGangMenu extends AbstractMenu {

    private final Gang gang;
    private final InfoMenu infoMenu;
    private final java.util.UUID UUID;

    public DeleteGangMenu(Gang gang, InfoMenu infoMenu, UUID UUID){
        this.gang = gang;
        this.infoMenu = infoMenu;
        this.UUID = UUID;
    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_TWO, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);
        super.setItem(20, gang.getMembersSorted().size() > 1 ? new ItemBuilder(Material.BARRIER).setItemName("&c&lDer er flere i bande").setLore("&fDu skal være den sidste", "&fi banden før du kan", "&fforlade den").buildItem() : (gang.getMembersSorted().get(UUID) == Rank.LEADER.getValue()) ? new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&a&lSlet bande").setLore("&fKlik her for at", "&fslette din bande").buildItem() : new ItemBuilder(Material.BARRIER).setItemName("&c&lDu ikke leder").setLore("&fKun lederen af banden", "&fkan slette den").buildItem());
        super.setItem(24, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&c&lAnnullere").setLore("&fKlik her for at", "&fannullere").buildItem());
        return super.inventory;
    }

    @Override
    protected String getName()
    {
        return "Er du sikker?";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        switch (slot)
        {
            case 49:
            case 24:
                whoClicked.openInventory(this.infoMenu.getInventory());
                break;
            case 20:
                whoClicked.performCommand("bande delete");
                whoClicked.getOpenInventory().close();
                break;
        }
    }

}
