package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.constants.GangAccess;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PermissionSubMenu extends AbstractMenu
{

    private InfoMenu infoMenu;
    private MainPlugin plugin;
    private final GangManaging gangManaging;

    public PermissionSubMenu(MainPlugin plugin, InfoMenu infoMenu)
    {
        this.plugin = plugin;
        this.infoMenu = infoMenu;
        this.gangManaging = plugin.getGangManaging();
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
        super.setItem(GangAccess.GANG_SHOP_SLOT, GANG_SHOP_ITEM);
        super.setItem(GangAccess.TRANSFER_MONEY_SLOT, TRANSFER_MONEY_ITEM);
        super.setItem(GangAccess.TRANSFER_ITEM_SLOT, TRANSFER_MATERIAL_ITEM);
        super.setItem(GangAccess.KICK_SLOT, KICK_ITEM);
        super.setItem(GangAccess.GANG_CHAT_SLOT, GANG_CHAT_ITEM);
        super.setItem(GangAccess.DEPOSIT_SLOT, DEPOSIT_ITEM);
        super.setItem(GangAccess.ALLY_CHAT_SLOT, ALLY_CHAT_ITEM);

        super.setItem(GangAccess.ENEMY_SLOT, ENEMY_ITEM);
        super.setItem(GangAccess.ALLY_SLOT, ALLY_ITEM);
        super.setItem(GangAccess.INVITE_SLOT, INVITE_ITEM);
        super.setItem(GangAccess.LEVEL_SLOT, LEVEL_ITEM);
        return super.inventory;
    }

    private final static ItemStack GANG_SHOP_ITEM = new ItemBuilder(Material.CHEST).setItemName("&b&lButik").setLore("&fKlik for at ændre hvem der skal", "&fkunne bruge bandeshoppen").buildItem();
    private final static ItemStack TRANSFER_MONEY_ITEM = new ItemBuilder(Material.GOLD_INGOT).setItemName("&6&lAflevere penge").setLore("&fKlik for at ændre hvem der skal", "&fkunne aflevere penge til folk").buildItem();
    private final static ItemStack TRANSFER_MATERIAL_ITEM = new ItemBuilder(Material.MAP).setItemName("&d&lAflevere items").setLore("&fKlik for at ændre hvem der skal", "&fkunne aflevere items til folk").buildItem();
    private final static ItemStack KICK_ITEM = new ItemBuilder(Material.IRON_BOOTS).setItemName("&4&lSmide ud").setLore("&fKlik for at ændre hvem der skal", "&fkunne smide folk ud af banden").buildItem();
    private final static ItemStack GANG_CHAT_ITEM = new ItemBuilder(Material.PAPER).setItemName("&5&lBandechat").setLore("&fKlik for at ændre hvem der kan", "&fskrive i bandechatten").buildItem();
    private final static ItemStack DEPOSIT_ITEM = new ItemBuilder(Material.FLOWER_POT_ITEM).setItemName("&2&lBande konto").setLore("&fKlik for at ændre hvem der skal", "&fkunne indsætte penge på bandekontoen").buildItem();
    private final static ItemStack ALLY_CHAT_ITEM = new ItemBuilder(Material.PAPER).setItemName("&d&lAlliancechat").setLore("&fKlik for at ændre hvem der kan", "&fskrive i alliancechatten").buildItem();
    private final static ItemStack ENEMY_ITEM = new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&c&lRivaler").setLore("&fKlik for at ændre hvem der skal", "&fkunne tilføje og fjerne rivaler").buildItem();
    private final static ItemStack ALLY_ITEM =new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&a&lAllierede").setLore("&fKlik for at ændre hvem der skal", "&fkunne tilføje og fjerne allierede").buildItem();
    private final static ItemStack INVITE_ITEM = new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.YELLOW.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&e&lInvitationer").setLore("&fKlik for at ændre hvem der skal", "&fkunne invitere folk til banden").buildItem();
    private final static ItemStack LEVEL_ITEM =new ItemBuilder(Material.OBSIDIAN).setItemName("&5&lLevel up").setLore("&fKlik for at ændre hvem der skal", "&fkunne level banden op").buildItem();
}
