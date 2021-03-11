package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.constants.GangAccess;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.GangPermissions;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.constants.Rank;
import dk.simonwinther.inventorymanaging.AbstractMenu;
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

public class AccessSubMenu extends AbstractMenu
{
    private MainPlugin plugin;
    private PermissionSubMenu permissionSubMenu;
    private Gang gang;
    private GangPermissions gangPermissions;
    private int slotType;
    private final GangManaging gangManaging;

    public AccessSubMenu(GangManaging gangManaging, MainPlugin plugin, PermissionSubMenu permissionSubMenu, UUID playerUUID, int slotType)
    {
        this.plugin = plugin;
        this.permissionSubMenu = permissionSubMenu;
        this.gang = gangManaging.getGangByUuidFunction.apply(playerUUID);
        this.gangPermissions = gang.gangPermissions;
        this.slotType = slotType;
        this.gangManaging = gangManaging;
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
                case GangAccess.GANG_SHOP_SLOT:
                    gangPermissions.setAccessToGangShop(rank);
                    break;
                case GangAccess.TRANSFER_MONEY_SLOT:
                    gangPermissions.setAccessToTransferMoney(rank);
                    break;
                case GangAccess.TRANSFER_ITEM_SLOT:
                    gangPermissions.setAccessToTransferItems(rank);
                    break;
                case GangAccess.KICK_SLOT:
                    gangPermissions.setAccessToKick(rank);
                    break;
                case GangAccess.GANG_CHAT_SLOT:
                    gangPermissions.setAccessToGangChat(rank);
                    break;
                case GangAccess.DEPOSIT_SLOT:
                    gangPermissions.setAccessToDeposit(rank);
                    break;
                case GangAccess.ALLY_CHAT_SLOT:
                    gangPermissions.setAccessToAllyChat(rank);
                    break;
                case GangAccess.ENEMY_SLOT:
                    gangPermissions.setAccessToEnemy(rank);
                    break;
                case GangAccess.ALLY_SLOT:
                    gangPermissions.setAccessToAlly(rank);
                    break;
                case GangAccess.INVITE_SLOT:
                    gangPermissions.setAccessToInvite(rank);
                    break;
                case GangAccess.LEVEL_SLOT:
                    gangPermissions.setAccessToLevelUp(rank);
                    break;
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> whoClicked.openInventory(new AccessSubMenu(this.gangManaging, this.plugin, this.permissionSubMenu, whoClicked.getUniqueId(), this.slotType).getInventory()), 10l);
        } else if (mat == Material.BED) whoClicked.openInventory(permissionSubMenu.getInventory());
    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_FOUR, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIGHT_BLUE.value[ColorIndexEnum.STAINED_GLASS.index]), true);


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
        switch (this.slotType)
        {
            case GangAccess.GANG_SHOP_SLOT:
                return gangPermissions.accessToGangShop;
            case GangAccess.TRANSFER_MONEY_SLOT:
                return gangPermissions.accessToTransferMoney;
            case GangAccess.TRANSFER_ITEM_SLOT:
                return gangPermissions.accessToTransferItems;
            case GangAccess.KICK_SLOT:
                return gangPermissions.accessToKick;
            case GangAccess.GANG_CHAT_SLOT:
                return gangPermissions.accessToGangChat;
            case GangAccess.DEPOSIT_SLOT:
                return gangPermissions.accessToDeposit;
            case GangAccess.ALLY_CHAT_SLOT:
                return gangPermissions.accessToAllyChat;
            case GangAccess.ENEMY_SLOT:
                return gangPermissions.accessToEnemy;
            case GangAccess.ALLY_SLOT:
                return gangPermissions.accessToAlly;
            case GangAccess.INVITE_SLOT:
                return gangPermissions.accessToInvite;
            case GangAccess.LEVEL_SLOT:
                return gangPermissions.accessToLevelUp;
            default:
                return Rank.MEMBER;
        }
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
