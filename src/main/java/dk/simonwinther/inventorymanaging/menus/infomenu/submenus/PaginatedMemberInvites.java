package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;


import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.AbstractPaginatedMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.IntPredicate;

public class PaginatedMemberInvites extends AbstractPaginatedMenu {

    private final Gang gang;
    private final MessageProvider mp;
    private MainPlugin plugin;
    private final PaginatedMemberSubMenu paginatedMemberSubMenu;

    public PaginatedMemberInvites(MainPlugin plugin, Gang gang, PaginatedMemberSubMenu paginatedMemberSubMenu){
        super();
        this.plugin = plugin;
        this.mp = plugin.getMessageProvider();
        this.gang = gang;
        this.paginatedMemberSubMenu = paginatedMemberSubMenu;
        this.setItemStacksForInventory(gang.getMemberInvitations().stream().map(memberName -> playerInvitesItem.setItemName("&a&l" + memberName).setLore("&fKlik for at fjerne", "&fspillerens invitation").buildItem()).toArray(ItemStack[]::new));
    }

    @Override
    protected String getName() {
        return "Invitationer";
    }

    @Override
    protected int backPageSlot() {
        return 42;
    }

    @Override
    protected int frontPageSlot() {
        return 43;
    }

    @Override
    protected IntPredicate menuDecoration() {
        return InventoryUtility.MENU_PREDICATE_SIX;
    }

    @Override
    protected ItemStack itemDecoration() {
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.STAINED_GLASS.index]);
    }

    @Override
    protected int offsetStart() {
        return 10;
    }

    @Override
    protected int availableSlots() {
        return 26;
    }

    @Override
    protected int getSizeOfInventory() {
        return 9*6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType) {
        if (item.getType() == Material.SKULL_ITEM)
        {
            String invitedMemberName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            if (gang.getMemberInvitations().contains(invitedMemberName))
            {
                gang.removeMemberInvitation(invitedMemberName);
                whoClicked.sendMessage(this.mp.playerWasUninvited.replace("{args}", invitedMemberName));
                whoClicked.closeInventory();
            }else whoClicked.sendMessage(this.mp.notInSameGang);
        } else if (slot == InventoryUtility.BACK_SLOT) whoClicked.openInventory(paginatedMemberSubMenu.getInventory());
    }

    @Override
    protected void addSeparateItems() {
        inventories.stream().forEach(inventory -> inventory.setItem(InventoryUtility.BACK_SLOT, InventoryUtility.BACK_ITEM));
    }

    @Override
    public Inventory getInventory() {
        return super.inventories.get(0);
    }

    private final ItemBuilder playerInvitesItem = new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER);

}
