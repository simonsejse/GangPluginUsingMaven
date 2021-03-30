package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.AbstractPaginatedMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.IntPredicate;

public class AllyRequestsPaginated extends AbstractPaginatedMenu {

    private final Gang gang;
    private final MainPlugin plugin;
    private final GangManaging gangManaging;
    private final PaginatedListAllySubMenu paginatedListAllySubMenu;
    private final ItemBuilder skullBuilder = new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER);


    public AllyRequestsPaginated(MainPlugin plugin, Gang gang, GangManaging gangManaging, PaginatedListAllySubMenu paginatedListAllySubMenu){
        super();
        this.gang = gang;
        this.plugin = plugin;
        this.paginatedListAllySubMenu = paginatedListAllySubMenu;
        this.gangManaging = gangManaging;
        this.setItemStacksForInventory(this.gang.getAllyInvitation()
                .stream()
                .map(gangManaging.getGangByNameFunction::apply)
                .map(allyGang -> this.skullBuilder
                        .setPlayerSkull(allyGang.getOwnerName())
                        .setItemName("&a&l" + allyGang.getGangName())
                        .setLore("&7Bandens leder: &f" + allyGang.getOwner(), "&7Bandens co-leader: &f" + allyGang.getCoOwner(), "", "&7Klik for at fjerne", "&7alliance invitationen")
                        .buildItem()
                )
                .toArray(ItemStack[]::new)
        );
    }

    public void addSeparateItems(){
        this.inventories.stream().forEach(inventory -> inventory.setItem(49, InventoryUtility.BACK_ITEM));
    }

    @Override
    protected String getName() {
        return "Ally Invitationer";
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
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.GREEN.value[ColorIndexEnum.STAINED_GLASS.index]);
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
        if (item.getType() == Material.STAINED_GLASS_PANE) return;
        else if (item.getType() == Material.AIR) return;
        else if (slot == InventoryUtility.BACK_SLOT) whoClicked.openInventory(paginatedListAllySubMenu.getInventory());
        else
        {
            String gangName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            gang.getAllyInvitation().remove(gangName);
            whoClicked.sendMessage(this.plugin.getMessageProvider().unAlly.replace("{name}", gangName));
            whoClicked.getOpenInventory().close();
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventories.get(0);
    }

}
