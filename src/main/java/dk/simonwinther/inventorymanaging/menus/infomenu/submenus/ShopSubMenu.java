package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.inventorymanaging.menus.mainmenu.MainMenu;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import dk.simonwinther.utility.ShopCostUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopSubMenu extends AbstractMenu
{
    private final MainPlugin plugin;
    private final MessageProvider mp;
    private final GangManaging gangManaging;
    private final Gang gang;
    private final MainMenu mainMenu;
    private final InfoMenu infoMenu;
    //This is the number we're dividing for making it more expensive per level, if you wish to make it more expensive per level, make this lower.
    private static final int DIVIDE_NUMBER = 10;
    //10000 * (1+(LEVEL/DIVIDE_NUMBER))

    public ShopSubMenu(MainPlugin plugin, Gang gang, InfoMenu infoMenu) //In case opening directly without opening the InfoMenu
    {
        this.plugin = plugin;
        this.gangManaging = this.plugin.getGangManaging();
        this.mp = this.plugin.getMessageProvider();
        this.infoMenu = infoMenu;
        this.mainMenu = null;
        this.gang = gang;
    }

    public ShopSubMenu(MainPlugin plugin, Gang gang, MainMenu mainMenu) //In case opening directly without opening the InfoMenu
    {
        this.plugin = plugin;
        this.gangManaging = this.plugin.getGangManaging();
        this.mp = this.plugin.getMessageProvider();
        this.mainMenu = mainMenu;
        this.infoMenu = null;
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
        if (slot == InventoryUtility.BACK_SLOT) {
            if (infoMenu == null && mainMenu != null) whoClicked.openInventory(mainMenu.getInventory());
            else whoClicked.openInventory(infoMenu.getInventory());
            return;
        }
        if (gangManaging.isRankMinimumPredicate.test(whoClicked.getUniqueId(), gang.gangPermissions.accessToGangShop))
        {
            int cost = 0;
            int balance = gang.getGangBalance();
            switch (slot)
            {
                case 10:
                    if (balance >= ShopCostUtil.GANG_DAMAGE && gang.getGangDamage() > 0)
                    {
                        cost = ShopCostUtil.GANG_DAMAGE;
                        gang.setGangDamage(gang.getGangDamage() - 1);
                    }
                    break;
                case 11:
                    if (balance >= ShopCostUtil.ALLY_DAMAGE && gang.getAllyDamage() > 0)
                    {
                        cost = ShopCostUtil.ALLY_DAMAGE;
                        gang.setAllyDamage(gang.getAllyDamage() - 1);
                    }
                    break;
                case 15:
                    if (balance >= ShopCostUtil.CHANGE_NAME && !gang.isNameBeenChanged())
                    {
                        cost = ShopCostUtil.CHANGE_NAME;
                        //TODO: Change name
                    }
                    break;
                case 16:
                    break;
                //sugarcane
                case 28:
                    if (balance >= ShopCostUtil.MAX_MEMBERS && gang.getMaxMembers() < this.plugin.getConfiguration().maxMembers)
                    {
                        cost = (ShopCostUtil.MAX_MEMBERS * (1 + (gang.getMembersSorted().size() / DIVIDE_NUMBER)));
                        gang.setMaxMembers(gang.getMaxMembers() + 1);
                    }
                    break;
                case 29:
                    if (balance >= ShopCostUtil.MAX_ALLIES && gang.getMaxAllies() < this.plugin.getConfiguration().maxAllies)
                    {
                        cost = ShopCostUtil.MAX_ALLIES * (1 + (gang.getAllies().size() / DIVIDE_NUMBER));
                        gang.setMaxAllies(gang.getMaxAllies() + 1);
                    }
                    break;
                case 30:
                    if (balance >= ShopCostUtil.MAX_ENEMIES && gang.getMaxEnemies() < this.plugin.getConfiguration().maxEnemies)
                    {
                        cost = ShopCostUtil.MAX_ENEMIES * (1 + (gang.getEnemies().size() / DIVIDE_NUMBER));
                        gang.setMaxEnemies(gang.getMaxEnemies() + 1);
                    }
                    break;
                case 32:
                    if (balance >= ShopCostUtil.ACCESS_TOILET && !gang.gangPermissions.accessToToilets)
                    {
                        cost = ShopCostUtil.ACCESS_TOILET;
                        gang.gangPermissions.setAccessToToilets(true);
                    }
                    break;
                case 33:
                    if (balance >= ShopCostUtil.ACCESS_TO_FARM && !gang.gangPermissions.accessToFarm)
                    {
                        cost = ShopCostUtil.ACCESS_TO_FARM;
                        gang.gangPermissions.setAccessToFarm(true);
                    }
                    break;
                case 34:
                    if (balance >= ShopCostUtil.ACCESS_TO_LAB && !gang.gangPermissions.accessToLab)
                    {
                        cost = ShopCostUtil.ACCESS_TO_LAB;
                        gang.gangPermissions.setAccessToLab(true);
                    }
                    break;
            }
            if (cost == 0)
            {
                whoClicked.sendMessage(this.mp.notEnoughMoney);
                return;
            }
            gang.removeMoney(cost);
        } else whoClicked.sendMessage(this.mp.notHighRankEnough);
        whoClicked.openInventory(this.getInventory());
    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_TWO, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.STAINED_GLASS.index]), true);

        int gangLevel = gang.getGangLevel();
        setItem(10, gangLevel >= 3 ? (gang.getGangDamage() > 0 ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, Math.min(gang.getGangDamage(), 64), ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lBande skade")
                        .setLore("&7Køb 1% mindre bande skade", "&7Nuværende: &f"
                                + gang.getGangDamage() + "%", "&7Pris: &f" + ShopCostUtil.GANG_DAMAGE)
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
                                + gang.getAllyDamage() + "%", "&7Pris: &f" + ShopCostUtil.ALLY_DAMAGE)
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lMinimum nået!")
                .setLore("&fDin alliance skade er", "&fallerede på 0%")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 4")
                .setAmount(4)
                .buildItem());

        setItem(15, gangLevel >= 8 ? (!gang.isNameBeenChanged() ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index])).setItemName("&d&lSkift navn")
                        .setLore("&7Klik for at skifte din bandes navn", "&7Pris: &f$" + ShopCostUtil.CHANGE_NAME)
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

        setItem(28, (gang.getMaxMembers() < this.plugin.getConfiguration().maxMembers ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxMembers(), ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lAntal medlemmer")
                        .setLore("&7Køb plads til 1 medlem mere", "&7Nuværende: &f"
                                + gang.getMaxMembers() + " medlemmer", "&7Pris: &f$" + ShopCostUtil.MAX_MEMBERS * (1 + (gang.getMembersSorted().size() / DIVIDE_NUMBER)))
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxMembers(), ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lMaskimum nået!")
                .setLore("&fDu kan maksimum købe", "&fadgang til"+this.plugin.getConfiguration().maxMembers+"medlemmer!")
                .buildItem()));
        setItem(29, gangLevel >= 2 ? (gang.getMaxAllies() < this.plugin.getConfiguration().maxAllies ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxAllies(), ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lAntal allierede")
                        .setLore("&7Køb plads til 1 allierede mere", "&7Nuværende: &f"
                                + gang.getMaxAllies() + " medlemmer", "&7Pris: &f$" + ShopCostUtil.MAX_ALLIES * (1 + (gang.getAllies().size() / DIVIDE_NUMBER)))
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxAllies(), ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lMaskimum nået!")
                .setLore("&fDu kan maksimum købe", "&fadgang til "+this.plugin.getConfiguration().maxAllies+" allierede!")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 2")
                .setAmount(2)
                .buildItem());
        setItem(30, gangLevel >= 6 ? (gang.getMaxEnemies() < this.plugin.getConfiguration().maxEnemies ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxEnemies(), ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lAntal rivaler")
                        .setLore("&7Køb plads til 1 rival mere", "&7Nuværende: &f"
                                + gang.getMaxEnemies() + " medlemmer", "&7Pris: &f$" + ShopCostUtil.MAX_ENEMIES * (1 + (gang.getEnemies().size() / DIVIDE_NUMBER)))
                        .buildItem()
                : new ItemBuilder(new ItemStack(Material.INK_SACK, gang.getMaxEnemies(), ColorDataEnum.GRAY.value[ColorIndexEnum.INK_SACH.index]))
                .setItemName("&7&lMaskimum nået!")
                .setLore("&fDu kan maksimum købe", "&fadgang til "+this.plugin.getConfiguration().maxEnemies+" rivaler!")
                .buildItem())
                : new ItemBuilder(Material.BARRIER)
                .setItemName("&4&lLåst")
                .setLore("&7&oLåses op i level 6")
                .setAmount(6)
                .buildItem());

        setItem(32, gangLevel >= 3 ? (!gang.gangPermissions.accessToToilets ?
                new ItemBuilder(new ItemStack(Material.INK_SACK, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.INK_SACH.index]))
                        .setItemName("&d&lToiletterne")
                        .setLore("&7Køb adgang til toiletterne i C", "&7Pris: &f$" + ShopCostUtil.ACCESS_TOILET)
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
                        .setLore("&7Køb adgang til gården i B", "&7Pris: &f$" + ShopCostUtil.ACCESS_TO_FARM)
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
                        .setLore("&7Køb adgang til laboratoriet i A", "&7Pris: &f$" + ShopCostUtil.ACCESS_TO_LAB)
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

    public Gang getGang()
    {
        return gang;
    }

}
