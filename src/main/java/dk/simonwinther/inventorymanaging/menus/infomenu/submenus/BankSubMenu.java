package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
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

public class BankSubMenu extends AbstractMenu
{
    private final MainPlugin plugin;
    private final MessageProvider mp;
    private final InfoMenu infoMenu;
    private Gang gang;
    private final GangManaging gangManaging;;

    public BankSubMenu(MainPlugin plugin, InfoMenu infoMenu, Gang gang, UUID playerUUID)
    {
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        this.infoMenu = infoMenu;
        this.gang = gang;
        this.playerUUID = playerUUID;
        this.gangManaging = this.plugin.getGangManaging();
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

        setItem(10, goldNuggetItem.setItemName("&c&l-1").buildItem());
        setItem(11, goldNuggetItem.setItemName("&c&l-25").buildItem());
        setItem(12, goldNuggetItem.setItemName("&c&l-50").buildItem());
        setItem(19, goldIngotItem.setItemName("&c&l-100").buildItem());
        setItem(20, goldIngotItem.setItemName("&c&l-250").buildItem());
        setItem(21, goldIngotItem.setItemName("&c&l-500").buildItem());
        setItem(28, goldBlockItem.setItemName("&c&l-1000").buildItem());
        setItem(29, goldBlockItem.setItemName("&c&l-2500").buildItem());
        setItem(30, goldBlockItem.setItemName("&c&l-5000").buildItem());

        setItem(14, goldNuggetItem.setItemName("&a&l+1").setAmount(1).buildItem());
        setItem(15, goldNuggetItem.setItemName("&a&l+25").setAmount(25).buildItem());
        setItem(16, goldNuggetItem.setItemName("&a&l+50").setAmount(50).buildItem());
        setItem(23, goldIngotItem.setItemName("&a&l+100").buildItem());
        setItem(24, goldIngotItem.setItemName("&a&l+250").buildItem());
        setItem(25, goldIngotItem.setItemName("&a&l+500").buildItem());
        setItem(32, goldBlockItem.setItemName("&a&l+1000").buildItem());
        setItem(33, goldBlockItem.setItemName("&a&l+2500").buildItem());
        setItem(34, goldBlockItem.setItemName("&a&l+5000").buildItem());

        setItem(22, flowerPotItem.setItemName("&a&lIndsæt penge").setLore("&fIndsæt penge i banken", "&fDin konto: &a$" + MessageFormat.format("{0}", plugin.getEconomy().getBalance(Bukkit.getPlayer(playerUUID)))).buildItem());

        setItem(13, cancelItem.setItemName("&c&lAnnuller").setLore("&fKlik her for at", "&fannullere").buildItem());
        place();
        return super.inventory;
    }

    void place(){updateItem();}

    void updateItem(){
        setItem(31, acceptItem.setItemName("&a&lAccepter").setLore("&fKlik her for at", "&findsætte: &2&l" + MessageFormat.format("{0}"+"&a$", deposit)).buildItem());
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
            UUID playerUUID = whoClicked.getUniqueId();
            Player tempPlayer = Bukkit.getPlayer(whoClicked.getUniqueId());
            if (this.gangManaging.isRankMinimumPredicate.test(playerUUID, gang.gangPermissions.accessToDeposit))
            {
                if (plugin.getEconomy().getBalance(tempPlayer) >= deposit)
                {
                    whoClicked.openInventory(infoMenu.getInventory());
                    plugin.getEconomy().withdrawPlayer(tempPlayer, deposit);
                    whoClicked.sendMessage("§c§l-" + MessageFormat.format("{0}", getDeposit()));
                    whoClicked.sendMessage("§a§l+" + MessageFormat.format("{0}", getDeposit()));
                    gang.depositMoney(deposit);
                } else whoClicked.sendMessage(this.mp.notEnoughMoney);
            } else whoClicked.sendMessage(this.mp.notHighRankEnough);

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
            updateItem();
        }

    }

    private final ItemBuilder acceptItem = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.GREEN.value[ColorIndexEnum.STAINED_GLASS.index]));
    private final ItemBuilder cancelItem = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_GLASS.index]));
    private final ItemBuilder flowerPotItem = new ItemBuilder(Material.FLOWER_POT);
    private final ItemBuilder goldNuggetItem = new ItemBuilder(Material.GOLD_NUGGET);
    private final ItemBuilder goldIngotItem = new ItemBuilder(Material.GOLD_INGOT);
    private final ItemBuilder goldBlockItem = new ItemBuilder(Material.GOLD_BLOCK);
}
