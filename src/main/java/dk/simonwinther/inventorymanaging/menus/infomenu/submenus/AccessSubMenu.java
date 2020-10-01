package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.GangPermissions;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.enums.Rank;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;
import java.util.UUID;

public class AccessSubMenu extends Menu
{
    private MainPlugin plugin;
    private PermissionSubMenu permissionSubMenu;
    private Gang gang;
    private GangPermissions gangPermissions;
    private int slotType;

    public AccessSubMenu(MainPlugin plugin, PermissionSubMenu permissionSubMenu, UUID playerUuid, int slotType)
    {
        this.plugin = plugin;
        this.permissionSubMenu = permissionSubMenu;
        this.gang = GangManaging.getGangByUuidFunction.apply(playerUuid);
        this.gangPermissions = gang.getGangPermissions();
        this.slotType = slotType;
    }

    @Override
    protected String getName()
    {
        return "Adgang";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        Material mat = item.getType();
        if (mat.name().endsWith("SWORD"))
        {
            Rank rank = mat == Material.WOOD_SWORD ? Rank.MEMBER : mat == Material.STONE_SWORD ? Rank.OFFICER : mat == Material.IRON_SWORD ? Rank.CO_LEADER : Rank.LEADER;
            switch (slotType)
            {
                case 10:
                    gangPermissions.setAccessToGangShop(rank);
                    break;
                case 12:
                    gangPermissions.setAccessToTransferMoney(rank);
                    break;
                case 14:
                    gangPermissions.setAccessToTransferItems(rank);
                    break;
                case 16:
                    gangPermissions.setAccessToKick(rank);
                    break;
                case 20:
                    gangPermissions.setAccessToGangChat(rank);
                    break;
                case 22:
                    gangPermissions.setAccessToDeposit(rank);
                    break;
                case 24:
                    gangPermissions.setAccessToAllyChat(rank);
                    break;
                case 28:
                    gangPermissions.setAccessToEnemy(rank);
                    break;
                case 30:
                    gangPermissions.setAccessToAlly(rank);
                    break;
                case 32:
                    gangPermissions.setAccessToInvite(rank);
                    break;
                case 34:
                    gangPermissions.setAccessToLevelUp(rank);
                    break;
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> whoClicked.openInventory(new AccessSubMenu(this.plugin, this.permissionSubMenu, whoClicked.getUniqueId(), this.slotType).getInventory()), 10l);
        } else if (mat == Material.BED) whoClicked.openInventory(permissionSubMenu.getInventory());
    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookFourPredicate, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIGHT_BLUE.value[ColorIndexEnum.STAINED_GLASS.index]), true);


        int memberSlot = (getCurrentPermission() == Rank.MEMBER ? 31 : 2);
        int officerSlot = (getCurrentPermission() == Rank.OFFICER ? 31 : getCurrentPermission() == Rank.MEMBER ? 2 : 4);
        int coLeader = (getCurrentPermission() == Rank.CO_LEADER ? 31 : getCurrentPermission() == Rank.MEMBER || getCurrentPermission() == Rank.OFFICER ? 4 : 6);
        int leader = (getCurrentPermission() == Rank.LEADER ? 31 : 6);

        setItem(memberSlot, new ItemBuilder(Material.WOOD_SWORD).setItemName("&2&lMember").addFlags(ItemFlag.HIDE_ATTRIBUTES).setLore(getLore(Rank.MEMBER)).isItemChosen(isRankChosen(Rank.MEMBER)).buildItem());
        setItem(officerSlot, new ItemBuilder(Material.STONE_SWORD).setItemName("&e&lOfficer").addFlags(ItemFlag.HIDE_ATTRIBUTES).setLore(getLore(Rank.OFFICER)).isItemChosen(isRankChosen(Rank.OFFICER)).buildItem());
        setItem(coLeader, new ItemBuilder(Material.IRON_SWORD).setItemName("&c&lCo-Leder").addFlags(ItemFlag.HIDE_ATTRIBUTES).setLore(getLore(Rank.CO_LEADER)).isItemChosen(isRankChosen(Rank.CO_LEADER)).buildItem());
        setItem(leader, new ItemBuilder(Material.DIAMOND_SWORD).setItemName("&4&lLeder").addFlags(ItemFlag.HIDE_ATTRIBUTES).setLore(getLore(Rank.LEADER)).isItemChosen(isRankChosen(Rank.LEADER)).buildItem());


        return super.inventory;
    }

    private Rank getCurrentPermission()
    {
        switch (slotType)
        {
            case 10:
                return gangPermissions.accessToGangShop;
            case 12:
                return gangPermissions.accessToTransferMoney;
            case 14:
                return gangPermissions.accessToTransferItems;
            case 16:
                return gangPermissions.accessToKick;
            case 20:
                return gangPermissions.accessToGangChat;
            case 22:
                return gangPermissions.accessToDeposit;
            case 24:
                return gangPermissions.accessToAllyChat;
            case 28:
                return gangPermissions.accessToEnemy;
            case 30:
                return gangPermissions.accessToAlly;
            case 32:
                return gangPermissions.accessToInvite;
            case 34:
                return gangPermissions.accessToLevelUp;
        }
        return Rank.MEMBER;
    }

    public boolean isRankChosen(Rank rank)
    {
        return (getCurrentPermission() == rank);
    }

    public String getLore(Rank rank)
    {
        return (isRankChosen(rank) ? MessageFormat.format("&fFolk med {0}{1}&f og derover,\n&fhar adgang til kommandoen!", rank.getColor(), rank.getRankName()) : MessageFormat.format("&f&nKlik her&f for at give {0}{1}&f og derover\n &fadgang til at kommandoen!", rank.getColor(), rank.getRankName()));
    }


}
