package dk.simonwinther.inventorymanaging.menus.mainmenu;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.submenus.ShopSubMenu;
import dk.simonwinther.inventorymanaging.menus.mainmenu.submenus.InviteSubMenu;
import dk.simonwinther.inventorymanaging.menus.mainmenu.submenus.OtherSubMenu;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MainMenu extends AbstractMenu
{
    private final MainPlugin plugin;
    private final UUID playerUUID;
    private final boolean isInGang;
    private final MessageProvider mp;
    private final GangManaging gangManaging;

    public MainMenu(MainPlugin plugin, UUID playerUUID, boolean isInGang)
    {
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        this.playerUUID = playerUUID;
        this.isInGang = isInGang;
        this.gangManaging = this.plugin.getGangManaging();
    }

    @Override
    protected String getName()
    {
        return "Bande";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        UUID uniqueId = whoClicked.getUniqueId();
        if (item.getType() == Material.STAINED_GLASS_PANE) return;
        if (slot == InventoryUtility.MEMBER_AND_OPEN_SHOP_SLOT){
            if (!isInGang) whoClicked.openInventory(new InviteSubMenu(this.gangManaging, plugin, this, playerUUID).getInventory());
            else whoClicked.openInventory(new ShopSubMenu(plugin, gangManaging.getGangByUuidFunction.apply(uniqueId), this).getInventory());
        } else if (slot == InventoryUtility.OTHERS_SLOT) whoClicked.openInventory(new OtherSubMenu(this.gangManaging, this).getInventory());
        else if (slot == InventoryUtility.BACK_SLOT || slot == 40) whoClicked.getOpenInventory().close();
        else if (slot == InventoryUtility.GANG_OR_CREATION_SLOT)
        {
            if (!isInGang)
            {
                whoClicked.sendMessage(this.mp.createGangChat);
                plugin.getEventHandling().addPlayerToAwaitGangCreation.accept(playerUUID);
                return;
            }
            whoClicked.openInventory(new InfoMenu(plugin, gangManaging.getGangByUuidFunction.apply(uniqueId), true, this).getInventory());
        }

    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_TWO, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), false);

        super.setItem(InventoryUtility.BACK_SLOT, new ItemBuilder(Material.BARRIER).setItemName("&c&lLuk Menuen").setLore("&fKlik her, for at", "&flukke menuen").buildItem());

        super.setItem(InventoryUtility.OTHERS_SLOT, new ItemBuilder(Material.BOOK).setItemName("&b&lAndet").setLore("&fKlik for at se top 10", "&fog kommandoer").buildItem());
        if (!isInGang)
        {
            double balance = plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(playerUUID));
            super.setItem(InventoryUtility.MEMBER_AND_OPEN_SHOP_SLOT, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.YELLOW.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&e&lBliv medlem").setLore("&fKlik her, for at få en", "&fliste over invitationer").buildItem());
            super.setItem(InventoryUtility.GANG_OR_CREATION_SLOT, new ItemBuilder(Material.NETHER_STAR).setItemName("&a&lOpret bande").setLore((balance >= gangManaging.GANG_COST ? "&fDu har penge nok" : "&fDu har &4ikke&f penge nok\n&fDu mangler &e" + (gangManaging.GANG_COST - balance) + "$"), "&a&lPris: &f" + gangManaging.GANG_COST+"$").buildItem());
        } else
        {
            super.setItem(InventoryUtility.MEMBER_AND_OPEN_SHOP_SLOT, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.YELLOW.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&e&lButik").setLore("&fKlik her, for at", "&fåbne bande butikken").buildItem());
            super.setItem(InventoryUtility.GANG_OR_CREATION_SLOT, new ItemBuilder(Material.NETHER_STAR).setItemName("&a&lDin bande").setLore("&fKlik her for at se info", "&fom din bande").buildItem());
        }

        return super.inventory;
    }
}
