package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.AbstractPaginatedMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
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

public class PaginatedAllyInvitations extends AbstractPaginatedMenu {

    private final ItemBuilder skullBuilder = new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER);
    private final PaginatedListAllySubMenu paginatedListAllySubMenu;
    private final GangManaging gangManaging;
    private final MessageProvider mp;

    public PaginatedAllyInvitations(Gang gang, GangManaging gangManaging, PaginatedListAllySubMenu paginatedListAllySubMenu, MainPlugin plugin){
        super();
        this.paginatedListAllySubMenu = paginatedListAllySubMenu;
        this.gangManaging = gangManaging;
        this.mp = plugin.getMessageProvider();
        this.setItemStacksForInventory(
                gangManaging.allyInvitationGangListFunction.apply(gang.getGangName()).stream().map(otherGang -> skullBuilder.setItemName("&a&l" + otherGang.getGangName()).setLore("&7Bandens leder: &f" + otherGang.getOwner(), "&7Bandens co-leader: &f" + otherGang.getCoOwner(), "", "&7Venstreklik for at acceptere alliancen", "&7Højreklik for at afvise alliancen").buildItem()).toArray(ItemStack[]::new)
        );

    }

    @Override
    protected String getName() {
        return "Invitationer";
    }

    @Override
    protected int backPageSlot() {
        return 37;
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
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]);
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
        if (slot == InventoryUtility.BACK_SLOT) whoClicked.openInventory(this.paginatedListAllySubMenu.getInventory());

        if (item.getType() == Material.SKULL_ITEM)
        {
            String gangName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            Gang argGang = this.gangManaging.getGangByNameFunction.apply(gangName);
            Gang playerGang = gangManaging.getGangByUuidFunction.apply(whoClicked.getUniqueId());
            if (!argGang.hasRequestedAlly(playerGang.getGangName())) return;

            if (clickType == ClickType.RIGHT)
            {
                argGang.removeAllyRequest(playerGang.getGangName());

                gangManaging.sendTeamMessage.accept(argGang, mp.allyDenied.replace("{name}", playerGang.getGangName()));
                gangManaging.sendTeamMessage.accept(playerGang, mp.denyAlly.replace("{name}", gangName));
                whoClicked.openInventory(this.paginatedListAllySubMenu.getInventory());

            } else if (clickType == ClickType.LEFT)
            {
                argGang.removeAllyRequest(playerGang.getGangName());
                if (playerGang.hasRequestedAlly(gangName))
                    playerGang.removeAllyRequest(gangName);

                argGang.getAllies().put(playerGang.getGangId(), playerGang.getGangName().toLowerCase());
                playerGang.getAllies().put(argGang.getGangId(), gangName.toLowerCase());

                this.gangManaging.sendTeamMessage.accept(playerGang, mp.allySuccessful.replace("{name}", gangName));
                this.gangManaging.sendTeamMessage.accept(argGang, mp.allySuccessful.replace("{name}", playerGang.getGangName()));

                whoClicked.openInventory(this.paginatedListAllySubMenu.getInventory());
            }
        }
    }
    @Override
    protected void addSeparateItems() {
        inventories.stream().forEach(inventory -> inventory.setItem(InventoryUtility.BACK_SLOT, InventoryUtility.BACK_ITEM));
    }

    @Override
    public Inventory getInventory() {
        return inventories.get(0);
    }
}
