package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PermissionSubMenu extends Menu
{

    private InfoMenu infoMenu;
    private MainPlugin plugin;

    public PermissionSubMenu(MainPlugin plugin, InfoMenu infoMenu)
    {
        this.plugin = plugin;
        this.infoMenu = infoMenu;
    }

    @Override
    protected String getName()
    {
        return "Tilladelser";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        if (item.getType() == Material.STAINED_GLASS_PANE) return;
       if (slot == InventoryUtility.backSlot) whoClicked.openInventory(infoMenu.getInventory());
       else whoClicked.openInventory(new AccessSubMenu(plugin, this, whoClicked.getUniqueId(), slot).getInventory());

    }


    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookTwoPredicate, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);
        super.setItem(10, new ItemBuilder(Material.CHEST).setItemName("&b&lButik").setLore("&fKlik for at ændre hvem der skal", "&fkunne bruge bandeshoppen").buildItem());
        super.setItem(12, new ItemBuilder(Material.GOLD_INGOT).setItemName("&6&lAflevere penge").setLore("&fKlik for at ændre hvem der skal", "&fkunne aflevere penge til folk").buildItem());
        super.setItem(14, new ItemBuilder(Material.MAP).setItemName("&d&lAflevere items").setLore("&fKlik for at ændre hvem der skal", "&fkunne aflevere items til folk").buildItem());
        super.setItem(16, new ItemBuilder(Material.IRON_BOOTS).setItemName("&4&lSmide ud").setLore("&fKlik for at ændre hvem der skal", "&fkunne smide folk ud af banden").buildItem());
        super.setItem(20, new ItemBuilder(Material.PAPER).setItemName("&5&lBandechat").setLore("&fKlik for at ændre hvem der kan", "&fskrive i bandechatten").buildItem());
        super.setItem(22, new ItemBuilder(Material.FLOWER_POT_ITEM).setItemName("&2&lBande konto").setLore("&fKlik for at ændre hvem der skal", "&fkunne indsætte penge på bandekontoen").buildItem());
        super.setItem(24, new ItemBuilder(Material.PAPER).setItemName("&d&lAlliancechat").setLore("&fKlik for at ændre hvem der kan", "&fskrive i alliancechatten").buildItem());

        super.setItem(28, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&c&lRivaler").setLore("&fKlik for at ændre hvem der skal", "&fkunne tilføje og fjerne rivaler").buildItem());
        super.setItem(30, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&a&lAllierede").setLore("&fKlik for at ændre hvem der skal", "&fkunne tilføje og fjerne allierede").buildItem());
        super.setItem(32, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.YELLOW.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&e&lInvitationer").setLore("&fKlik for at ændre hvem der skal", "&fkunne invitere folk til banden").buildItem());
        super.setItem(34, new ItemBuilder(Material.OBSIDIAN).setItemName("&5&lLevel up").setLore("&fKlik for at ændre hvem der skal", "&fkunne level banden op").buildItem());

        return super.inventory;
    }
}
