package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.inventorymanaging.menus.mainmenu.MainMenu;
import dk.simonwinther.utility.GangManaging;
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

public class ShopSubMenu extends Menu
{
    private MainPlugin plugin;

    private InfoMenu infoMenu;
    private Gang gang;
    private final boolean openedFromMainMenu;
    private final int gangDamageCost = 10000;
    private final int allyDamageCost = 15000;
    private final int gangChangeNameCost = 25000;
    private final int maxMembersCost = 10000;
    private final int maxAlliesCost = 20000;
    private final int maxEnemiesCost = 15000;
    private final int accessToToiletCost = 10000;
    private final int accessToFarmCost = 100000;
    private final int accessToLabCost = 250000;


    public ShopSubMenu(MainPlugin plugin, Gang gang) //In case opening directly without opening the InfoMenu
    {
        this.plugin = plugin;
        this.infoMenu = new InfoMenu(plugin, gang, true);
        this.openedFromMainMenu = true;
        this.gang = gang;
    }
    
    public ShopSubMenu(MainPlugin plugin, InfoMenu infoMenu, Gang gang)
    {
        this.plugin = plugin;
        this.infoMenu = infoMenu;
        this.openedFromMainMenu = false;
        this.gang = gang;
    }


    @Override
    protected String getName()
    {
        return "Bandeshop";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        if (item.getType() != Material.BED && item.getType() != Material.INK_SACK) return;
        if (slot == InventoryUtility.backSlot) {
            Inventory inventory = null;
            if (openedFromMainMenu) inventory = new MainMenu(plugin, whoClicked.getUniqueId(), true).getInventory();
            else inventory = infoMenu.getInventory();
            whoClicked.openInventory(inventory);
            return;
        }
        if (GangManaging.isRankMinimumPredicate.test(whoClicked.getUniqueId(), gang.gangPermissions.accessToGangShop))
        {
            int cost = 0;
            int balance = gang.getGangBalance();
            switch (slot)
            {
                case 10:
                    if (balance >= gangDamageCost && gang.getGangDamage() > 0)
                    {
                        cost = gangDamageCost;
                        gang.setGangDamage(gang.getGangDamage() - 1);
                    }
                    break;
                case 11:
                    if (balance >= allyDamageCost && gang.getAllyDamage() > 0)
                    {
                        cost = allyDamageCost;
                        gang.setAllyDamage(gang.getAllyDamage() - 1);
                    }
                    break;
                case 15:
                    if (balance >= gangChangeNameCost && !gang.hasNameBeenChanged())
                    {
                        //Change name
                    }
                    break;
                case 16:
                    break;
                //sugarcane
                case 28:
                    if (balance >= maxMembersCost && gang.getMaxMembers() < 20)
                    {
                        cost = maxMembersCost;
                        gang.setMaxMembers(gang.getMaxMembers() + 1);
                    }
                    break;
                case 29:
                    if (balance >= maxAlliesCost && gang.getMaxAllies() < 10)
                    {
                        cost = maxAlliesCost;
                        gang.setMaxAllies(gang.getMaxAllies() + 1);
                    }
                    break;
                case 30:
                    if (balance >= maxEnemiesCost && gang.getMaxEnemies() < 15)
                    {
                        cost = maxEnemiesCost;
                        gang.setMaxEnemies(gang.getMaxEnemies() + 1);
                    }
                    break;
                case 32:
                    if (balance >= accessToToiletCost && !gang.gangPermissions.accessToToilets)
                    {
                        cost = accessToToiletCost;
                        gang.gangPermissions.setAccessToToilets(true);
                    }
                    break;
                case 33:
                    if (balance >= accessToFarmCost && !gang.gangPermissions.accessToFarm)
                    {
                        cost = accessToFarmCost;
                        gang.gangPermissions.setAccessToFarm(true);
                    }
                    break;
                case 34:
                    if (balance >= accessToLabCost && !gang.gangPermissions.accessToLab)
                    {
                        cost = accessToLabCost;
                        gang.gangPermissions.setAccessToLab(true);
                    }
                    break;
            }
            if (cost == 0)
            {
                whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_ENOUGH_MONEY));
                return;
            }
            gang.removeMoney(cost);
        } else plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH);
        whoClicked.openInventory(this.getInventory());
    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookTwoPredicate, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.STAINED_GLASS.index]), true);

        int gangLevel = gang.getGangLevel();
        setItem(10, gangLevel >= 3 ? (gang.getGangDamage() > 0 ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, Math.min(gang.getGangDamage(), 64), ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lBande skade")
                        .setLore("&7Køb 1% mindre bande skade", "&7Nuværende: &f"
                                + gang.getGangDamage() + "%", "&7Pris: &f" + gangDamageCost)
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index])).setItemName("&7&lMinimum nået!")
                .setLore("&fDin bande skade er", "&fallerede på 0%")
                .buildItem())
                : new ItemBuilder(Material.BARRIER).setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 3")
                .setAmount(3)
                .buildItem());

        setItem(11, gangLevel >= 4 ? (gang.getAllyDamage() > 0 ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, Math.min(gang.getAllyDamage(), 64), ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lAlliance skade")
                        .setLore("&7Køb 1% mindre alliance skade", "&7Nuværende: &f"
                                + gang.getAllyDamage() + "%", "&7Pris: &f" + allyDamageCost).buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lMinimum nået!")
                .setLore("&fDin alliance skade er", "&fallerede på 0%")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 4")
                .setAmount(4)
                .buildItem());

        setItem(15, gangLevel >= 8 ? (!gang.hasNameBeenChanged() ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index])).setItemName("&d&lSkift navn")
                        .setLore("&7Klik for at skifte din bandes navn", "&7Pris: &f$" + gangChangeNameCost)
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lAllerede gjort")
                .setLore("&fDin bande har allerede", "&fskiftet navn én gang før")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 8")
                .setAmount(8)
                .buildItem());

        setItem(16, gangLevel >= 2 ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lSugar Cane")
                        .setLore("&7Pris: &f$2.500", "&7&oKan kun købes hver time")
                        .buildItem()
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 2")
                .setAmount(2)
                .buildItem());

        setItem(28, (gang.getMaxMembers() < 20 ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxMembers(), ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lAntal medlemmer")
                        .setLore("&7Køb plads til 1 medlem mere", "&7Nuværende: &f"
                                + gang.getMaxMembers() + " medlemmer", "&7Pris: &f$" + maxMembersCost)
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxMembers(), ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lMaskimum nået!")
                .setLore("&fDu kan maksimum købe", "&fadgang til 20 medlemmer!")
                .buildItem()));
        setItem(29, gangLevel >= 2 ? (gang.getMaxAllies() < 10 ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxAllies(), ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lAntal allierede")
                        .setLore("&7Køb plads til 1 allierede mere", "&7Nuværende: &f"
                                + gang.getMaxAllies() + " medlemmer", "&7Pris: &f$" + maxAlliesCost)
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxAllies(), ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lMaskimum nået!")
                .setLore("&fDu kan maksimum købe", "&fadgang til 10 allierede!")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 2")
                .setAmount(2)
                .buildItem());
        setItem(30, gangLevel >= 6 ? (gang.getMaxEnemies() < 15 ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxEnemies(), ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lAntal rivaler")
                        .setLore("&7Køb plads til 1 rival mere", "&7Nuværende: &f"
                                + gang.getMaxEnemies() + " medlemmer", "&7Pris: &f$" + maxEnemiesCost)
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxEnemies(), ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lMaskimum nået!")
                .setLore("&fDu kan maksimum købe", "&fadgang til 15 rivaler!")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 6")
                .setAmount(6)
                .buildItem());

        setItem(32, gangLevel >= 3 ? (!gang.gangPermissions.accessToToilets ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lToiletterne")
                        .setLore("&7Køb adgang til toiletterne i C", "&7Pris: &f$" + accessToToiletCost)
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lAllerede købt")
                .setLore("&fDu har allerede adgang", "&ftil toiletterne i C!")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 3")
                .setAmount(3)
                .buildItem());
        setItem(33, (gangLevel >= 5) ? (!gang.gangPermissions.accessToFarm ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lGården")
                        .setLore("&7Køb adgang til gården i B", "&7Pris: &f$" + accessToFarmCost)
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lAllerede købt")
                .setLore("&fDu har allerede adgang", "&ftil gården i B!")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 5")
                .setAmount(5)
                .buildItem());

        setItem(34, gangLevel >= 7 ? (!gang.gangPermissions.accessToLab ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lLaboratoriet")
                        .setLore("&7Køb adgang til laboratoriet i A", "&7Pris: &f$" + accessToLabCost)
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lAllerede købt")
                .setLore("&fDu har allerede adgang", "&ftil laboratoriet i A!")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 7")
                .setAmount(7)
                .buildItem());


        return super.inventory;
    }

    public InfoMenu getInfoMenu()
    {
        return infoMenu;
    }

    public void setInfoMenu(InfoMenu infoMenu)
    {
        this.infoMenu = infoMenu;
    }

    public Gang getGang()
    {
        return gang;
    }

    public void setGang(Gang gang)
    {
        this.gang = gang;
    }
}
