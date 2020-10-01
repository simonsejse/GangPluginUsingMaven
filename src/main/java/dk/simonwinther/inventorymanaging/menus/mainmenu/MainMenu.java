package dk.simonwinther.inventorymanaging.menus.mainmenu;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.submenus.ShopSubMenu;
import dk.simonwinther.inventorymanaging.menus.mainmenu.submenus.InviteSubMenu;
import dk.simonwinther.inventorymanaging.menus.mainmenu.submenus.OtherSubMenu;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MainMenu extends Menu
{
    private MainPlugin plugin;
    private UUID playerUuid;
    private boolean isInGang;

    public MainMenu(MainPlugin plugin, UUID playerUuid, boolean isInGang)
    {
        this.plugin = plugin;
        this.playerUuid = playerUuid;
        this.isInGang = isInGang;
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
        if (slot == InventoryUtility.memberAndOpenShopSlot){
            if (!isInGang) whoClicked.openInventory(new InviteSubMenu(plugin, this, playerUuid).getInventory());
            else
                whoClicked.openInventory(new ShopSubMenu(plugin, GangManaging.getGangByUuidFunction.apply(uniqueId)).getInventory());
        } else if (slot == InventoryUtility.othersSlot) whoClicked.openInventory(new OtherSubMenu(this).getInventory());
        else if (slot == InventoryUtility.backSlot || slot == 40) whoClicked.getOpenInventory().close();
        else if (slot == InventoryUtility.gangOrCreationSlot)
        {
            if (!isInGang)
            {
                whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CREATE_GANG_CHAT));
                plugin.getEventHandling().addCreateGangConsumer.accept(playerUuid);
                return;
            }

            whoClicked.openInventory(new InfoMenu(plugin, GangManaging.getGangByUuidFunction.apply(uniqueId), true).getInventory());
        }

    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookTwoPredicate, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), false);

        super.setItem(InventoryUtility.backSlot, new ItemBuilder(Material.BARRIER).setItemName("&c&lLuk Menuen").setLore("&fKlik her, for at", "&flukke menuen").buildItem());

        super.setItem(InventoryUtility.othersSlot, new ItemBuilder(Material.BOOK).setItemName("&b&lAndet").setLore("&fKlik for at se top 10", "&fog kommandoer").buildItem());
        if (!isInGang)
        {
            double balance = plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(playerUuid));
            super.setItem(InventoryUtility.memberAndOpenShopSlot, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.YELLOW.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&e&lBliv medlem").setLore("&fKlik her, for at få en", "&fliste over invitationer").buildItem());
            super.setItem(InventoryUtility.gangOrCreationSlot, new ItemBuilder(Material.NETHER_STAR).setItemName("&a&lOpret bande").setLore((balance >= GangManaging.getGangCost() ? "&fDu har penge nok" : "&fDu har &4ikke&f penge nok\n&fDu mangler &e" + ((int) (GangManaging.getGangCost() - balance)) + "$"), "&a&lPris: &f" + GangManaging.getGangCost()+"$").buildItem());
        } else
        {
            super.setItem(InventoryUtility.memberAndOpenShopSlot, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.YELLOW.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&e&lButik").setLore("&fKlik her, for at", "&fåbne bande butikken").buildItem());
            super.setItem(InventoryUtility.gangOrCreationSlot, new ItemBuilder(Material.NETHER_STAR).setItemName("&a&lDin bande").setLore("&fKlik her for at se info", "&fom din bande").buildItem());
        }

        return super.inventory;
    }
}
