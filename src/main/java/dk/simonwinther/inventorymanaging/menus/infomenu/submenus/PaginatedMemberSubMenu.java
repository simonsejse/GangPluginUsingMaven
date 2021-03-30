package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.inventorymanaging.AbstractPaginatedMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.function.IntPredicate;

public class PaginatedMemberSubMenu extends AbstractPaginatedMenu
{
    private Gang gang;
    private MainPlugin plugin;
    private final MessageProvider mp;
    private final GangManaging gangManaging;
    private final InfoMenu infoMenu;

    public PaginatedMemberSubMenu(MainPlugin plugin, Gang gang, InfoMenu infoMenu)
    {

        super();
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        this.gang = gang;
        this.infoMenu = infoMenu;
        this.gangManaging = plugin.getGangManaging();
        super.setItemStacksForInventory(gang.getMembersSorted()
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(Map.Entry::getValue)))
                .map(Map.Entry::getKey)
                .map(uuid ->
                {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    String rank = gang.getRank(uuid);
                    String block = plugin.getBlockAtPlayerLoc(uuid);
                    String lastOnline = getLastOnline(uuid);
                    return playerItem.setPlayerSkull(offlinePlayer.getName()).setItemName("&a" + offlinePlayer.getName()).setLore("&7Bande rank: " + rank, "&7Blok: &f" + block, "&7Sidst online:&f " + lastOnline, "", "&7Klik her for at administrere medlem").buildItem();
                })
                .toArray(ItemStack[]::new)
        );
        addSeparateItems();
    }

    @Override
    protected String getName() {
        return "Medlemmer";
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
        return InventoryUtility.MENU_PREDICATE_FIVE;
    }

    @Override
    protected ItemStack itemDecoration() {
        return this.decorationItem;
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
        switch (slot)
        {
            case 49:
                whoClicked.openInventory(infoMenu.getInventory());
                break;

            case 51:
                whoClicked.openInventory(new PaginatedMemberInvites(this.plugin, this.gangManaging.getGangByUuidFunction.apply(whoClicked.getUniqueId()), this).getInventory());
                break;
            case 47:
                whoClicked.getOpenInventory().close();
                plugin.getEventHandling().addPlayerToAwaitInvitation.accept(whoClicked.getUniqueId());
                whoClicked.sendMessage(this.mp.whoToInviteChat);
                break;
            default:
                if (item.getType() != Material.SKULL_ITEM) return;
                String chosenPlayer = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                whoClicked.openInventory(new EditMemberMenu(chosenPlayer, this.gangManaging, this.plugin, this.gang, this).getInventory());
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventories.get(0);
    }

    @Override
    public void addSeparateItems(){
        this.inventories.stream().forEach(inventory -> {
            inventory.setItem(47, this.inviteMemberItem);
            inventory.setItem(49, InventoryUtility.BACK_ITEM);
            inventory.setItem(51, this.openInvitationsItem);
        });
    }

    private String getLastOnline(UUID uuid)
    {
        return plugin.getEventHandling().getDate(uuid);
    }

    private final ItemBuilder playerItem = new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER);
    private final ItemStack decorationItem = new ItemStack(Material.STAINED_GLASS_PANE,1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]);
    private final ItemStack inviteMemberItem = new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER).setItemName("&a&lInviter").setLore("&fKlik her for at invitere", "&fet medlem til banden").buildItem();
    private final ItemStack openInvitationsItem = new ItemBuilder(Material.SKULL_ITEM,1, SkullType.PLAYER).setItemName("&a&lInvitationer").setLore("&fKlik for at se en liste over", "&fhvem der er inviteret til banden").buildItem();

}
