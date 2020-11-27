package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Level;

public class BankSubMenu extends Menu
{
    private MainPlugin plugin;
    private InfoMenu infoMenu;
    private Gang gang;
    private final GangManaging gangManaging;

    public BankSubMenu(GangManaging gangManaging, MainPlugin plugin, InfoMenu infoMenu, Gang gang, UUID playerUuid)
    {

        this.plugin = plugin;
        this.infoMenu = infoMenu;
        this.gang = gang;
        this.playerUuid = playerUuid;
        this.gangManaging = gangManaging;
    }

    private int deposit = 0;

    public int getDeposit()
    {
        return deposit;
    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_TWO, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIGHT_BLUE.value[ColorIndexEnum.STAINED_GLASS.index]), true);

        setItem(10, new ItemBuilder(Material.GOLD_NUGGET).setItemName("&c&l-1").buildItem());
        setItem(11, new ItemBuilder(Material.GOLD_NUGGET).setItemName("&c&l-25").buildItem());
        setItem(12, new ItemBuilder(Material.GOLD_NUGGET).setItemName("&c&l-50").buildItem());
        setItem(19, new ItemBuilder(Material.GOLD_INGOT).setItemName("&c&l-100").buildItem());
        setItem(20, new ItemBuilder(Material.GOLD_INGOT).setItemName("&c&l-250").buildItem());
        setItem(21, new ItemBuilder(Material.GOLD_INGOT).setItemName("&c&l-500").buildItem());
        setItem(28, new ItemBuilder(Material.GOLD_BLOCK).setItemName("&c&l-1000").buildItem());
        setItem(29, new ItemBuilder(Material.GOLD_BLOCK).setItemName("&c&l-2500").buildItem());
        setItem(30, new ItemBuilder(Material.GOLD_BLOCK).setItemName("&c&l-5000").buildItem());

        setItem(14, new ItemBuilder(Material.GOLD_NUGGET).setItemName("&a&l+1").setAmount(1).buildItem());
        setItem(15, new ItemBuilder(Material.GOLD_NUGGET).setItemName("&a&l+25").setAmount(25).buildItem());
        setItem(16, new ItemBuilder(Material.GOLD_NUGGET).setItemName("&a&l+50").setAmount(50).buildItem());
        setItem(23, new ItemBuilder(Material.GOLD_INGOT).setItemName("&a&l+100").buildItem());
        setItem(24, new ItemBuilder(Material.GOLD_INGOT).setItemName("&a&l+250").buildItem());
        setItem(25, new ItemBuilder(Material.GOLD_INGOT).setItemName("&a&l+500").buildItem());
        setItem(32, new ItemBuilder(Material.GOLD_BLOCK).setItemName("&a&l+1000").buildItem());
        setItem(33, new ItemBuilder(Material.GOLD_BLOCK).setItemName("&a&l+2500").buildItem());
        setItem(34, new ItemBuilder(Material.GOLD_BLOCK).setItemName("&a&l+5000").buildItem());

        setItem(22, new ItemBuilder(Material.FLOWER_POT).setItemName("&a&lIndsæt penge").setLore("&fIndsæt penge i banken", "&fDin konto: &a$" + MessageFormat.format("{0}", plugin.getEconomy().getBalance(Bukkit.getPlayer(playerUuid)))).buildItem());

        setItem(13, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_GLASS.index])).setItemName("&c&lAnnuller").setLore("&fKlik her for at", "&fannullere").buildItem());
        place();
        return super.inventory;
    }

    void place(){replace();}
    void replace(){
        setItem(31, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.GREEN.value[ColorIndexEnum.STAINED_GLASS.index])).setItemName("&a&lAccepter").setLore("&fKlik her for at", "&findsætte: &2&l" + MessageFormat.format("{0}"+"&a$", deposit)).buildItem());
    }

    @Override
    protected String getName()
    {
        return "Indsæt penge";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        if (slot == 13 || slot == InventoryUtility.BACK_SLOT)
        {
            whoClicked.openInventory(infoMenu.getInventory());
        } else if (slot == 31)
        {
            UUID playerUuid = whoClicked.getUniqueId();
            Player tempPlayer = Bukkit.getPlayer(whoClicked.getUniqueId());
            if (this.gangManaging.isRankMinimumPredicate.test(playerUuid, gang.gangPermissions.accessToDeposit))
            {
                if (plugin.getEconomy().getBalance(tempPlayer) >= deposit)
                {
                    whoClicked.openInventory(infoMenu.getInventory());
                    plugin.getEconomy().withdrawPlayer(tempPlayer, deposit);
                    whoClicked.sendMessage(plugin.getChatUtil().color("&c&l-" + MessageFormat.format("{0}", getDeposit())));
                    whoClicked.sendMessage(plugin.getChatUtil().color("&a&l+" + MessageFormat.format("{0}", getDeposit())));
                    gang.depositMoney(deposit);
                } else
                    whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_ENOUGH_MONEY));
            } else whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));

        } else if (item.getType().equals(Material.GOLD_NUGGET) || item.getType().equals(Material.GOLD_INGOT) || item.getType().equals(Material.GOLD_BLOCK))
        {
            String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            if (itemName.contains("+"))
            {
                try
                {
                    int price = Integer.parseInt(itemName.split("\\+")[1]);
                    deposit += price;
                } catch (NumberFormatException nfe)
                {
                    Bukkit.getLogger().log(Level.SEVERE, "En hilsen fra Simon, at der er en fejl i banken...");
                }
            } else if (itemName.contains("-"))
            {
                try
                {
                    int price = Integer.parseInt(itemName.split("\\-")[1]);
                    if (deposit - price < 0)
                    {
                        deposit = 0;
                        return;
                    }
                    deposit -= price;
                } catch (NumberFormatException nfe)
                {
                    Bukkit.getLogger().log(Level.SEVERE, "En hilsen fra Simon, at der er en fejl i banken...");
                }
            }
            replace();
        }
    }
}
