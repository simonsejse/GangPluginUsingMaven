package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.constants.GangAccess;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.utility.GangManaging;
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
    private final GangManaging gangManaging;
    public PermissionSubMenu(GangManaging gangManaging, MainPlugin plugin, InfoMenu infoMenu)
    {
        this.plugin = plugin;
        this.infoMenu = infoMenu;
        this.gangManaging = gangManaging;
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
       if (slot == InventoryUtility.BACK_SLOT) whoClicked.openInventory(infoMenu.getInventory());
       else whoClicked.openInventory(new AccessSubMenu(this.gangManaging, plugin, this, whoClicked.getUniqueId(), slot).getInventory());

    }


    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_TWO, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);
        super.setItem(GangAccess.GANG_SHOP_SLOT, new ItemBuilder(Material.CHEST).setItemName("&b&lButik").setLore("&fKlik for at ændre hvem der skal", "&fkunne bruge bandeshoppen").buildItem());
        super.setItem(GangAccess.TRANSFER_MONEY_SLOT, new ItemBuilder(Material.GOLD_INGOT).setItemName("&6&lAflevere penge").setLore("&fKlik for at ændre hvem der skal", "&fkunne aflevere penge til folk").buildItem());
        super.setItem(GangAccess.TRANSFER_ITEM_SLOT, new ItemBuilder(Material.MAP).setItemName("&d&lAflevere items").setLore("&fKlik for at ændre hvem der skal", "&fkunne aflevere items til folk").buildItem());
        super.setItem(GangAccess.KICK_SLOT, new ItemBuilder(Material.IRON_BOOTS).setItemName("&4&lSmide ud").setLore("&fKlik for at ændre hvem der skal", "&fkunne smide folk ud af banden").buildItem());
        super.setItem(GangAccess.GANG_CHAT_SLOT, new ItemBuilder(Material.PAPER).setItemName("&5&lBandechat").setLore("&fKlik for at ændre hvem der kan", "&fskrive i bandechatten").buildItem());
        super.setItem(GangAccess.DEPOSIT_SLOT, new ItemBuilder(Material.FLOWER_POT_ITEM).setItemName("&2&lBande konto").setLore("&fKlik for at ændre hvem der skal", "&fkunne indsætte penge på bandekontoen").buildItem());
        super.setItem(GangAccess.ALLY_CHAT_SLOT, new ItemBuilder(Material.PAPER).setItemName("&d&lAlliancechat").setLore("&fKlik for at ændre hvem der kan", "&fskrive i alliancechatten").buildItem());

        super.setItem(GangAccess.ENEMY_SLOT, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&c&lRivaler").setLore("&fKlik for at ændre hvem der skal", "&fkunne tilføje og fjerne rivaler").buildItem());
        super.setItem(GangAccess.ALLY_SLOT, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&a&lAllierede").setLore("&fKlik for at ændre hvem der skal", "&fkunne tilføje og fjerne allierede").buildItem());
        super.setItem(GangAccess.INVITE_SLOT, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.YELLOW.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&e&lInvitationer").setLore("&fKlik for at ændre hvem der skal", "&fkunne invitere folk til banden").buildItem());
        super.setItem(GangAccess.LEVEL_SLOT, new ItemBuilder(Material.OBSIDIAN).setItemName("&5&lLevel up").setLore("&fKlik for at ændre hvem der skal", "&fkunne level banden op").buildItem());

        return super.inventory;
    }
}
